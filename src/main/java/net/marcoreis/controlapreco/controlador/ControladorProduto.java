package net.marcoreis.controlapreco.controlador;

import java.util.List;

import javax.annotation.PostConstruct;

import net.marcoreis.controlapreco.entidades.Produto;

import org.primefaces.model.UploadedFile;

public class ControladorProduto extends BaseBean {
    private static final long serialVersionUID = -5962643759675198689L;
    private Produto produto;
    private List<Produto> produtos;
    private UploadedFile imagem;
    private ServicoProduto servico = new ServicoProduto();

    @PostConstruct
    public void init() {
        produto = new Produto();
        produtos = getServico().findAll(Produto.class);
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public ServicoProduto getServico() {
        return servico;
    }

    public void setImagem(UploadedFile imagem) {
        this.imagem = imagem;
    }

    public UploadedFile getImagem() {
        return imagem;
    }

    public String editar(Produto produto) {
        this.produto = produto;
        return "produto";
    }

}
