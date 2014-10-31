package net.marcoreis.controlapreco.teste;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;

import net.marcoreis.controlapreco.entidades.Categoria;
import net.marcoreis.controlapreco.entidades.Movimentacao;
import net.marcoreis.controlapreco.entidades.TipoMovimentacao;
import net.marcoreis.controlapreco.util.JPAUtil;

import org.junit.Test;

public class TesteMovimentacao {

    @Test
    public void inserir() throws ParseException {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        Movimentacao m = new Movimentacao();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        m.setData(sdf.parse("2014-04-23"));
        m.setTipo(TipoMovimentacao.RECEITA);
        m.setValor(7101.0);
        Categoria categoria = em.find(Categoria.class, 6l);
        m.setCategoria(categoria);
        em.getTransaction().begin();
        em.persist(m);
        em.getTransaction().commit();

    }
}
