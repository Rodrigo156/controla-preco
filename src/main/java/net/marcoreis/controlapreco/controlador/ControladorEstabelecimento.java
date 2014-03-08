package net.marcoreis.controlapreco.controlador;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

import net.marcoreis.controlapreco.entidades.Estabelecimento;

@ManagedBean
public class ControladorEstabelecimento extends BaseBean {
    private static final long serialVersionUID = -8276067130004922771L;
    private List<Estabelecimento> estabelecimentos;
    private ServicoEstabelecimento servico = new ServicoEstabelecimento();
    private Estabelecimento estabelecimento;

    @PostConstruct
    public void init() {
        estabelecimento = new Estabelecimento();
        estabelecimentos = getServico().findAll(Estabelecimento.class);
    }

    public List<Estabelecimento> getEstabelecimentos() {
        return estabelecimentos;
    }

    public void setEstabelecimentos(List<Estabelecimento> estabelecimentos) {
        this.estabelecimentos = estabelecimentos;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public ServicoEstabelecimento getServico() {
        return servico;
    }

    public void salvar() {
        getServico().salvar(getEstabelecimento());
        infoMsg(MENSAGEM_SUCESSO);
    }

}
