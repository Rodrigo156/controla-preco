package net.marcoreis.controlapreco.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.Compra;
import net.marcoreis.controlapreco.entidades.ItemCompra;
import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.util.JPAUtil;

public class ServicoCompra extends ServicoGenerico {

    public List<Produto> findProdutosPorNome(String nome) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            String hql = "from Produto where upper(nome) like :nome";
            Query query = em.createQuery(hql);
            query.setParameter("nome", "%" + nome.toUpperCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }

    }

    public List<Produto> findEstabelecimentosPorNome(String nome) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            String hql = "from Estabelecimento where upper(nome) like :nome";
            Query query = em.createQuery(hql);
            query.setParameter("nome", "%" + nome.toUpperCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ItemCompra> findItensCompra(Long id) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            Query query = em.createQuery("from ItemCompra where compra.id = :id");
            query.setParameter("id", id);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void excluir(Compra compra) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            String jpql = "delete from ItemCompra where compra.id = :id";
            em.getTransaction().begin();
            Query query = em.createQuery(jpql);
            query.setParameter("id", compra.getId());
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        excluir(Compra.class, compra.getId());
    }

}
