package net.marcoreis.controlapreco.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import net.marcoreis.controlapreco.entidades.Produto;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class IndexadorECommerce {
    private IndexWriter writer;
    private Directory diretorio;
    private Tika tika;

    public IndexadorECommerce() {
        try {
            diretorio = FSDirectory.open(new File(Constantes.DIRETORIO_INDICE));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48,
                    analyzer);
            writer = new IndexWriter(diretorio, config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public IndexWriter getWriter() {
        return writer;
    }

    public Tika getTika() {
        if (tika == null) {
            tika = new Tika();
        }
        return tika;
    }

    public void indexarProdutos() throws IOException, TikaException {
        EntityManager em = JPAUtil.getInstance().getEntityManager();
        List<Produto> produtos = em.createQuery("from Produto").getResultList();
        for (Produto prod : produtos) {
            indexarProduto(prod);
        }
    }

    private void indexarProduto(Produto prod) throws IOException, TikaException {
        Document doc = new Document();
        doc.add(new LongField("id.produto", prod.getId(), Store.YES));
        doc.add(new TextField("categoria", prod.getCategoria().getNome(),
                Store.YES));
        doc.add(new TextField("nome", prod.getNome(), Store.YES));
        // doc.add(new TextField("descricao", prod.getDescricao(), Store.YES));
        // //
        // ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
        // prod.getEspecificacaoFabricante());
        // String especificacaoFabricante = getTika().parseToString(
        // byteArrayInputStream);
        // doc.add(new TextField("especificacaoFabricante",
        // especificacaoFabricante, Store.YES));
        //
        StringBuilder textoCompleto = new StringBuilder();
        textoCompleto.append(prod.getCategoria().getNome());
        textoCompleto.append(" ");
        textoCompleto.append(prod.getNome());
        textoCompleto.append(" ");
        // textoCompleto.append(especificacaoFabricante);
        textoCompleto.append(" ");
        // textoCompleto.append(prod.getDescricao());
        doc.add(new TextField("textoCompleto", textoCompleto.toString(),
                Store.YES));
        doc.add(new TextField("tabela", "produto", Store.YES));
        Term termoIdentificacao = new Term("id.produto", prod.getId()
                .toString());
        writer.updateDocument(termoIdentificacao, doc);
    }

    public void fechar() throws IOException {
        writer.close();
    }
}
