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
            Query query = em
                    .createQuery("from Produto where upper(nome) like ?");
            query.setParameter(1, "%" + nome.toUpperCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }

    }

    public List<Produto> findEstabelecimentosPorNome(String nome) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            Query query = em
                    .createQuery("from Estabelecimento where upper(nome) like ?");
            query.setParameter(1, "%" + nome.toUpperCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ItemCompra> findItensCompra(Long id) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            Query query = em.createQuery("from ItemCompra where compra.id = ?");
            query.setParameter(1, id);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void excluir(Compra compra) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            String jpql = "delete from ItemCompra where compra.id = ?";
            em.getTransaction().begin();
            Query query = em.createQuery(jpql);
            query.setParameter(1, compra.getId());
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
