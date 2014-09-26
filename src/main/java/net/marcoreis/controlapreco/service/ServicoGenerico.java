package net.marcoreis.controlapreco.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.IPersistente;
import net.marcoreis.controlapreco.util.JPAUtil;

import org.apache.log4j.Logger;

public class ServicoGenerico implements Serializable {
    private static final long serialVersionUID = 1410786127780867298L;
    private static Logger logger = Logger.getLogger(ServicoGenerico.class);

    public IPersistente salvar(IPersistente persistente) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            // if (persistente.getId() != null && persistente.getId() > 0) {
            IPersistente salvo = em.merge(persistente);
            // } else {
            // em.persist(persistente);
            // }
            em.getTransaction().commit();
            return salvo;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public List findAll(Class clazz) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            String jpaql = "from " + clazz.getName();
            List list = em.createQuery(jpaql).getResultList();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public List find(String jpaql) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            List list = em.createQuery(jpaql).getResultList();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public Object findById(Class clazz, Long id) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        Object p = em.find(clazz, id);
        em.close();
        return p;
    }

    public void excluir(Class clazz, Long id) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            Object persistente = em.find(clazz, id);
            em.getTransaction().begin();
            em.remove(persistente);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.error(e);
        } finally {
            em.close();
        }
    }

}
