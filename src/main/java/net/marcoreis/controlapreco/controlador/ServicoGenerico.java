package net.marcoreis.controlapreco.controlador;

import java.util.List;

import javax.persistence.EntityManager;

import net.marcoreis.controlapreco.entidades.IPersistente;
import net.marcoreis.controlapreco.util.JPAUtil;

public class ServicoGenerico {
    public void salvar(IPersistente persistente) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            // if (persistente.getId() != null && persistente.getId() > 0) {
            em.merge(persistente);
            // } else {
            // em.persist(persistente);
            // }
            em.getTransaction().commit();
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

}
