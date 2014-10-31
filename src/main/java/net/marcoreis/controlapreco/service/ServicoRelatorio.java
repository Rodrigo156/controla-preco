package net.marcoreis.controlapreco.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.util.JPAUtil;

import org.apache.log4j.Logger;

public class ServicoRelatorio extends ServicoGenerico {

    private static final long serialVersionUID = 5338951746198221690L;
    private static Logger logger = Logger.getLogger(ServicoRelatorio.class);

    public List<String> findMesesDisponiveis() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        String sQuery = "select distinct date_format(data, '%m/%Y') from Compra";
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

    public List<Object[]> consultarHistorioPrecoProduto() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        String sql = "select date_format(data, '%m/%Y') as mesReferencia, sum(valorTotal) as total from Compra group by date_format(data, '%m/%Y') order by date_format(data, '%m/%Y')";
        Query query = em.createQuery(sql);
        List<Object[]> resultado = query.getResultList();
        em.close();
        return resultado;
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

    public List<Object[]> consultarHistoricoPrecoProduto(List<Produto> produtos) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select p.nome, avg(ic.valorUnitario) as precoMedio, date_format(data, '%m/%Y') as mesReferencia ");
        sql.append("from ItemCompra ic  ");
        sql.append("inner join Compra c on ic.compra_id = c.id ");
        sql.append("inner join Produto p on p.id = ic.produto_id ");
        if (produtos.size() > 0) {
            sql.append("where produto_id in :produtos ");
        }
        sql.append("group by date_format(data, '%m/%Y'), p.nome ");
        sql.append("order by date_format(data, '%m/%Y') ");
        Query query = em.createNativeQuery(sql.toString());
        if (produtos.size() > 0) {
            query.setParameter("produtos", produtos);
        }
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
        sql.append("select ifnull(sum(ic.valorUnitario), 0) ");
        sql.append("from ItemCompra ic  ");
        sql.append("inner join Compra c on ic.compra_id = c.id ");
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
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("mesAnoReferencia", mesAnoReferencia);
        Double receitas = (Double) query.getSingleResult();
        em.close();
        return receitas;
    }

}
