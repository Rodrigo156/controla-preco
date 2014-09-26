package net.marcoreis.controlapreco.controlador;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.marcoreis.controlapreco.entidades.Compra;
import net.marcoreis.controlapreco.entidades.Estabelecimento;
import net.marcoreis.controlapreco.entidades.ItemCompra;
import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.service.ServicoCompra;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;

@ManagedBean
@ViewScoped
public class ControladorCompra extends ControladorGenerico {
    private static final long serialVersionUID = -3786512477792609776L;
    private Compra compra;
    private Produto produtoSelecionado;
    private List<Compra> compras;
    private List<ItemCompra> itens = new ArrayList<ItemCompra>();
    private ServicoCompra servico = new ServicoCompra();
    private Double valorUnitario;
    private Double valorTotal = 0d;
    private Double quantidade = 1d;
    private List<Estabelecimento> estabelecimentos;

    @PostConstruct
    public void init() {
        if (!StringUtils.isEmpty(getParametro("id"))) {
            Long id = Long.parseLong(getParametro("id"));
            compra = (Compra) getServico().findById(Compra.class, id);
            itens = getServico().findItensCompra(compra.getId());
            atualizarValorTotal();
        } else {
            compra = new Compra();
            compra.setData(new Date(System.currentTimeMillis()));
        }
        estabelecimentos = getServico().findAll(Estabelecimento.class);
        carregarCompras();
    }

    private void carregarCompras() {
        String jpaql = "from Compra order by data desc";
        compras = getServico().find(jpaql);
    }

    private void atualizarValorTotal() {
        valorTotal = 0d;
        for (ItemCompra i : getItens()) {
            valorTotal += i.getValorTotal();
        }
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public ServicoCompra getServico() {
        return servico;
    }

    public void salvar() {
        try {
            getCompra().setUsuario(getUsuario());
            getServico().salvar(getCompra());
            infoMsg(MENSAGEM_SUCESSO);
        } catch (Exception e) {
            errorMsg(e);
        }
    }

    public String editar(Compra compra) {
        this.compra = compra;
        return "compra?faces-redirect=true&id=" + compra.getId();
    }

    public void setProdutoSelecionado(Produto produtoSelecionado) {
        this.produtoSelecionado = produtoSelecionado;
    }

    public Produto getProdutoSelecionado() {
        if (produtoSelecionado == null) {
            produtoSelecionado = new Produto();
        }
        return produtoSelecionado;
    }

    public List<Produto> completarProduto(String query) {
        return getServico().findProdutosPorNome(query);
    }

    public List<Produto> completarEstabelecimento(String query) {
        return getServico().findEstabelecimentosPorNome(query);
    }

    public void handleSelect(SelectEvent event) {
        try {
            Estabelecimento estabelecimento = (Estabelecimento) event
                    .getObject();
            getCompra().setEstabelecimento(estabelecimento);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setItens(List<ItemCompra> itens) {
        this.itens = itens;
    }

    public List<ItemCompra> getItens() {
        return itens;
    }

    public void removerItem(ItemCompra item) {
        getServico().excluir(ItemCompra.class, item.getId());
        getItens().remove(item);
        atualizarValorTotal();
        getCompra().setValorTotal(getValorTotal());
        getCompra().setUsuario(getUsuario());
        compra = (Compra) getServico().salvar(getCompra());
    }

    public void adicionarItem() {
        try {
            getCompra().setUsuario(getUsuario());
            if (getCompra().getId() == 0) {
                compra = (Compra) getServico().salvar(getCompra());
            }
            // if (getProdutoSelecionado().getId() == null) {
            // produtoSelecionado = (Produto) getServico().salvar(
            // getProdutoSelecionado());
            // }
            Double fracao = getQuantidade() % 1;
            if (fracao > 0) {
                preencherDadosFracao();
            } else {
                preencherDadosInteiro();
            }
            //
            getCompra().setValorTotal(getValorTotal());
            compra = (Compra) getServico().salvar(getCompra());
            //
            setProdutoSelecionado(null);
            setQuantidade(1d);
            setValorUnitario(null);
        } catch (Exception e) {
            errorMsg(e);
        }
    }

    private void preencherDadosInteiro() {
        for (int i = 0; i < getQuantidade(); i++) {
            ItemCompra item = new ItemCompra();
            item.setCompra(getCompra());
            item.setProduto(getProdutoSelecionado());
            item.setQuantidade(1d);
            item.setValorUnitario(getValorUnitario());
            item.setValorTotal(getValorUnitario());
            item.setUsuario(getUsuario());
            item = (ItemCompra) getServico().salvar(item);
            valorTotal += getValorUnitario();
            getItens().add(item);
        }
    }

    private void preencherDadosFracao() {
        ItemCompra item = new ItemCompra();
        item.setCompra(getCompra());
        item.setProduto(getProdutoSelecionado());
        item.setValorUnitario(getValorUnitario());
        item.setValorTotal(getValorUnitario() * getQuantidade());
        item.setQuantidade(getQuantidade());
        item.setUsuario(getUsuario());
        item = (ItemCompra) getServico().salvar(item);
        valorTotal += item.getValorTotal();
        getItens().add(item);
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void excluir(Compra compra) {
        getServico().excluir(compra);
        getCompras().remove(compra);
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorTotalFormatado() {
        DecimalFormat df = new DecimalFormat("R$ ###0.00",
                new DecimalFormatSymbols(new Locale("pt", "BR")));
        return df.format(getValorTotal());
    }

    public List<Estabelecimento> getEstabelecimentos() {
        return estabelecimentos;
    }
}
