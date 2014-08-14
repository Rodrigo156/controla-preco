package net.marcoreis.controlapreco.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.Usuario;
import net.marcoreis.controlapreco.util.JPAUtil;

public class ServicoUsuario extends ServicoGenerico {
    public Usuario findByEmail(String email) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        Query query = em.createQuery("from Usuario where email = :email");
        query.setParameter("email", email);
        return (Usuario) query.getSingleResult();
    }
}
