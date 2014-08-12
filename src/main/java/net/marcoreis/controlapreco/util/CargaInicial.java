package net.marcoreis.controlapreco.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.marcoreis.controlapreco.entidades.Categoria;
import net.marcoreis.controlapreco.entidades.Usuario;

public class CargaInicial {
    private EntityManager em;

    public static void main(String[] args) {
        new CargaInicial().carregar();
    }

    public CargaInicial() {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("controla-preco-pu");
        em = emf.createEntityManager();
    }

    private void carregar() {
        String hql = "from Usuario where email = 'ma@marcoreis.net'";
        Query query = em.createQuery(hql);
        Usuario usuario = (Usuario) query.getSingleResult();
        em.getTransaction().begin();
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setEmail("ma@marcoreis.net");
            usuario = em.merge(usuario);
        }
        Categoria c = new Categoria();
        c.setNome("Transporte");
        c.setUsuario(usuario);
        em.persist(c);
        c.setNome("Alimentação");
        em.persist(c);
        c.setNome("Lazer");
        em.persist(c);
        c.setNome("Moradia");
        em.persist(c);
        c.setNome("Educação");
        em.persist(c);
        //
        em.getTransaction().commit();
    }
}
