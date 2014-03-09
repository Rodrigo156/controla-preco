package net.marcoreis.controlapreco.controlador;

import java.util.List;

import javax.persistence.EntityManager;

import net.marcoreis.controlapreco.entidades.IPersistente;
import net.marcoreis.controlapreco.util.JPAUtil;

import org.apache.log4j.Logger;

public class ServicoGenerico {
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
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public List findAll(Class clazz) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            List list = em.createQuery("from " + clazz.getName())
                    .getResultList();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public IPersistente findById(Class clazz, Long id) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        IPersistente p = em.find(clazz, id);
        em.close();
        return p;
    }

    public void excluir(Class clazz, Long id) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            IPersistente persistente = em.find(clazz, id);
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
