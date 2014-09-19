package net.marcoreis.controlapreco.teste;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;

import net.marcoreis.controlapreco.entidades.Movimentacao;
import net.marcoreis.controlapreco.entidades.TipoMovimentacao;
import net.marcoreis.controlapreco.util.JPAUtil;

public class TesteMovimentacao {
    
    @Test
    public void inserir() {
        Movimentacao m = new Movimentacao();
        m.setData(new Date());
        m.setTipo(TipoMovimentacao.DESPESA);
        m.setValor(1334.0);
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        em.getTransaction().begin();
        em.persist(m);
        em.getTransaction().commit();

    }
}
