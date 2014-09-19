package net.marcoreis.controlapreco.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.util.JPAUtil;

import org.apache.log4j.Logger;

public class ServicoRelatorio extends ServicoGenerico {

    private static final long serialVersionUID = 5338951746198221690L;
    private static Logger logger = Logger.getLogger(ServicoRelatorio.class);

    public List<String> findMesesDisponiveis() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        String sQuery = "select distinct date_format(data, '%Y-%m') from Compra";
        Query query = em.createNativeQuery(sQuery);
        List resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public List<Object> consultarGastosPorMes() {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        String sql = "select date_format(data, '%Y-%m') as mesReferencia, sum(valorTotal) as total from Compra group by date_format(data, '%Y-%m') order by date_format(data, '%Y-%m')";
        Query query = em.createQuery(sql);
        List<Object> resultado = query.getResultList();
        em.close();
        return resultado;
    }

    public List<Object> consultarGastosPorCategoria(Date mesReferencia) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        StringBuilder sql = new StringBuilder();
        sql.append("select ct.nome as nomeCategoria, sum(ic.valorTotal) as valor from Compra c ");
        sql.append("inner join ItemCompra ic on ic.compra_id = c.id ");
        sql.append("inner join Produto p on p.id = ic.produto_id ");
        sql.append("inner join Categoria ct on p.categoria_id = ct.id ");
        sql.append("group by ct.nome ");
        Query query = em.createNativeQuery(sql.toString());
        List<Object> resultado = query.getResultList();
        em.close();
        return resultado;
    }

    // public Double findGastosNoMes(String mes) {
    // EntityManager em = JPAUtil.getInstance().getEntityManager();
    // String sQuery =
    // "select sum( distinct date_format(data, '%Y-%m') from Compra";
    // Query query = em.createNativeQuery(sQuery);
    // List resultado = query.getResultList();
    // em.close();
    // return resultado;
    // }
}
