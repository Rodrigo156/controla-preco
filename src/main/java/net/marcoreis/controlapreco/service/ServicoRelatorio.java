package net.marcoreis.controlapreco.service;

import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.util.JPAUtil;
import net.marcoreis.controlapreco.vo.AcaoVO;

import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class ServicoRelatorio extends ServicoGenerico {

    private static final long serialVersionUID = 5338951746198221690L;
    private static Logger logger = Logger.getLogger(ServicoRelatorio.class);

    public List<String> findMesesDisponiveis() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        String sQuery = "select distinct date_format(data, '%m/%Y') from Compra order by data desc";
        Query query = em.createNativeQuery(sQuery);
        List resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public List<Object[]> consultarGastosPorMes() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select date_format(data, '%m/%Y') as mesReferencia, sum(valorTotal) as total ");
        sql.append("from Compra group by date_format(data, '%m/%Y') order by date_format(data, '%m/%Y')");
        Query query = em.createQuery(sql.toString());
        List<Object[]> resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public List<Object[]> consultarGastosPorCategoria(String mesAnoReferencia) {
        return consultarGastosPorCategoria(mesAnoReferencia, "data desc");
    }

    public List<Object[]> consultarGastosPorCategoria(String mesAnoReferencia,
            String campoOrdenacao) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select ct.nome as nomeCategoria, sum(ic.valorTotal) as valor from Compra c ");
        sql.append("inner join ItemCompra ic on ic.compra_id = c.id ");
        sql.append("inner join Produto p on p.id = ic.produto_id ");
        sql.append("inner join Categoria ct on p.categoria_id = ct.id ");
        sql.append("where date_format(data, '%m/%Y') = :mesAnoReferencia ");
        sql.append("group by ct.nome ");
        sql.append("order by " + campoOrdenacao);
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("mesAnoReferencia", mesAnoReferencia);
        List<Object[]> resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public List<Object[]> consultarHistoricoPrecoProduto(Produto produto) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select p.nome, avg(ic.valorUnitario) as precoMedio, date_format(data, '%m/%Y') as mesReferencia ");
        sql.append("from ItemCompra ic  ");
        sql.append("inner join Compra c on ic.compra_id = c.id ");
        sql.append("inner join Produto p on p.id = ic.produto_id ");
        sql.append("where produto_id = :produto ");
        sql.append("group by date_format(data, '%m/%Y'), p.nome ");
        sql.append("order by date_format(data, '%m/%Y') ");
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("produto", produto);
        List<Object[]> resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public List<Object[]> consultarHistoricoReceitas() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select date_format(data, '%m/%Y'), sum(valor) ");
        sql.append("from Movimentacao where tipo = 'RECEITA' ");
        sql.append("group by date_format(data, '%m/%Y') order by data");
        Query query = em.createQuery(sql.toString());
        List<Object[]> resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public Double consultarDespesasMes(String mesAnoReferencia) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select ifnull(sum(c.valorTotal), 0) ");
        // sql.append("from ItemCompra ic  ");
        // sql.append("inner join Compra c on ic.compra_id = c.id ");
        sql.append("from Compra c ");
        sql.append("where date_format(data, '%m/%Y') = :mesAnoReferencia ");
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("mesAnoReferencia", mesAnoReferencia);
        Double despesas = (Double) query.getSingleResult();
        em.close();
        return despesas;
    }

    public Double consultarReceitasMes(String mesAnoReferencia) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select ifnull(sum(valor), 0) ");
        sql.append("from Movimentacao m ");
        sql.append("where date_format(data, '%m/%Y') = :mesAnoReferencia ");
        sql.append(" AND tipo = 'RECEITA' ");
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("mesAnoReferencia", mesAnoReferencia);
        Double receitas = (Double) query.getSingleResult();
        em.close();
        return receitas;
    }

    public List<AcaoVO> consultarHistoricoPrecoAcoes(String simbolo,
            String dateInicio, String dataFim) {
        try {
            StringBuilder urlServico = new StringBuilder();
            urlServico.append("http://query.yahooapis.com/v1/public/yql?q=");
            urlServico.append("select * from yahoo.finance.historicaldata ");
            urlServico.append("where symbol=\"");
            urlServico.append(simbolo);
            urlServico.append("\" ");
            urlServico.append("and startDate=\"");
            urlServico.append(dateInicio);
            urlServico.append("\" and endDate=\"");
            urlServico.append(dataFim);
            urlServico
                    .append("\" &format=json&env=store://datatables.org/alltableswithkeys");
            URL url = new URL(encodeUrl(urlServico.toString()));
            //
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(url);
            List<AcaoVO> lista = mapper.readValue(root.findValue("quote"),
                    new TypeReference<List<AcaoVO>>() {
                    });
            return lista;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public static String encodeUrl(String url) {
        try {
            return new URLCodec().encode(url, "UTF-8").replace("%3A", ":")
                    .replace("%2F", "/").replace("%3F", "?")
                    .replace("%3D", "=").replace("%26", "&");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
