package net.marcoreis.controlapreco.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.Categoria;
import net.marcoreis.controlapreco.util.JPAUtil;

public class ServicoCategoria extends ServicoGenerico {

    private static final long serialVersionUID = 4614499984422263392L;

    public List<Categoria> findCategoriasPorNome(String consulta) {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        try {
            String hql = "from Categoria where upper(nome) like :nome";
            Query query = em.createQuery(hql);
            query.setParameter("nome", "%" + consulta.toUpperCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
