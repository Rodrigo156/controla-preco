package net.marcoreis.controlapreco.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.service.ServicoRelatorio;

import org.joda.time.DateTime;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class ControladorRelatorio extends ControladorGenerico {

    private static final long serialVersionUID = -8989131094545262992L;
    private ServicoRelatorio servico = new ServicoRelatorio();
    private LineChartModel graficoDeGastosPorMes;
    private PieChartModel graficoDeGastosPorCategoria;
    private LineChartModel dadosGraficoConsumoEletricidade;
    private Date mesReferencia;
    private List<?> mesesDisponiveis;
    private List<Produto> produtos;
    private List<Produto> produtosSelecionados;
    private CartesianChartModel dadosProdutosSelecionados;

    @PostConstruct
    public void init() {
        try {
            initProdutos();
            initGraficoGastosPorMes();
            initGraficoGastosPorCategoria();
            initGraficoConsumoEletricidade();
            initGraficoHistoricoPrecoProduto();
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    private void initGraficoHistoricoPrecoProduto() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dadosProdutosSelecionados = new LineChartModel();
        Date dataInicio = new Date(System.currentTimeMillis());
        for (Produto p : getProdutosSelecionados()) {
            List<Object[]> historico = getServico()
                    .consultarHistoricoPrecoProduto(p);
            ChartSeries serie = new LineChartSeries();
            for (Object[] dados : historico) {
                String nomeProduto = dados[0].toString();
                Double valor = (Double) dados[1];
                // Para usar um eixo com data, os valores devem estar em formato
                // yyyy-MM-dd, por isso adicionamos
                // o "-01" no final
                String data = dados[2] + "-01";
                Date dataDate = sdf.parse(data);
                if (dataDate.before(dataInicio)) {
                    dataInicio = dataDate;
                }
                serie.setLabel(nomeProduto);
                serie.set(data, valor);
            }
            if (historico.size() > 0) {
                dadosProdutosSelecionados.addSeries(serie);
            }
        }
        //
        if (dadosProdutosSelecionados.getSeries().size() == 0) {
            String titulo = "Selecione produtos com preços cadastrados";
            ChartSeries serie = new LineChartSeries(titulo);
            serie.set("2014-01-01", 0);
            dadosProdutosSelecionados.addSeries(serie);
        }
        //
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickInterval("1 month");
        if (dataInicio != null) {
            eixoData.setMin(recuperarMesAnterior(dataInicio));
        }
        eixoData.setTickAngle(-60);
        //
        dadosProdutosSelecionados.getAxes().put(AxisType.X, eixoData);
        dadosProdutosSelecionados.setTitle("Histórico de preços");
        dadosProdutosSelecionados.setLegendPosition("ne");
        dadosProdutosSelecionados.setExtender("fnGraficoHistoricoPrecos");
    }

    private void initProdutos() {
        produtos = getServico().find("from Produto order by nome");
    }

    private void initGraficoGastosPorCategoria() {
        graficoDeGastosPorCategoria = new PieChartModel();
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(
                getMesReferencia());
        for (Object[] dados : gastos) {
            String chave = dados[0].toString();
            Number valor = (Number) dados[1];
            graficoDeGastosPorCategoria.set(chave, valor);
        }
        graficoDeGastosPorCategoria.setTitle("Gastos por categoria");
        graficoDeGastosPorCategoria.setShowDataLabels(true);
        graficoDeGastosPorCategoria.setLegendPosition("e");
    }

    private void initGraficoGastosPorMes() {
        // 1
        graficoDeGastosPorMes = new LineChartModel();
        // 2
        Axis xaxis = graficoDeGastosPorMes.getAxis(AxisType.X);
        xaxis.setLabel("Mês");
        Axis yaxis = graficoDeGastosPorMes.getAxis(AxisType.Y);
        yaxis.setLabel("Total (R$)");
        // 3
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        // 4
        ChartSeries serie = new ChartSeries();
        String dataInicio = null;
        for (Object[] dados : gastos) {
            String data = dados[0] + "-01";
            Number valor = (Number) dados[1];
            serie.set(data, valor);
            if (dataInicio == null) {
                dataInicio = data;
            }
        }
        // 5
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickAngle(-60);
        eixoData.setTickInterval("1 month");
        eixoData.setMin(recuperarMesAnterior(dataInicio));
        graficoDeGastosPorMes.getAxes().put(AxisType.X, eixoData);
        // 6
        graficoDeGastosPorMes.addSeries(serie);
        graficoDeGastosPorMes.setTitle("Gastos por mês");
        graficoDeGastosPorMes.setShowDatatip(false);
        graficoDeGastosPorMes.setShowPointLabels(true);
        graficoDeGastosPorMes.setExtender("fnGraficoGastosPorMes");
    }

    private String recuperarMesAnterior(String dataInicio) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date data = sdf.parse(dataInicio);
            Date mesAnterior = new DateTime(data.getTime()).minusMonths(1)
                    .toDate();
            return sdf.format(mesAnterior);
        } catch (ParseException e) {
            return dataInicio;
        }
    }

    private String recuperarMesAnterior(Date dataInicio) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mesAnterior = new DateTime(dataInicio.getTime())
                    .minusMonths(1).toDate();
            return sdf.format(mesAnterior);
        } catch (Exception e) {
            return sdf.format(dataInicio);
        }
    }

    private void initGraficoConsumoEletricidade() {
        //
        LineChartSeries serie = new LineChartSeries();
        serie.set("2013-07-01", 133);
        serie.set("2013-08-01", 123);
        serie.set("2013-09-01", 154);
        serie.set("2013-10-01", 142);
        serie.set("2013-11-01", 151);
        serie.set("2013-12-01", 112);
        serie.set("2014-01-01", 139);
        serie.set("2014-02-01", 117);
        serie.set("2014-03-01", 124);
        serie.set("2014-04-01", 107);
        serie.set("2014-05-01", 157);
        serie.set("2014-06-01", 98);
        serie.set("2014-07-01", 145);
        //
        dadosGraficoConsumoEletricidade = new LineChartModel();
        String titulo = "Consumo de eletricidade (kWh)";
        dadosGraficoConsumoEletricidade.setTitle(titulo);
        dadosGraficoConsumoEletricidade.addSeries(serie);
        //
        dadosGraficoConsumoEletricidade.getAxis(AxisType.Y).setMin(0);
        //
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickInterval("1 month");
        eixoData.setMin("2013-06-01");
        eixoData.setTickAngle(-60);
        //
        dadosGraficoConsumoEletricidade.getAxes().put(AxisType.X, eixoData);
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Produto> getProdutos() {
        if (produtos == null) {
            produtos = new ArrayList<Produto>();
        }
        return produtos;
    }

    public void setProdutosSelecionados(List<Produto> produtosSelecionados) {
        this.produtosSelecionados = produtosSelecionados;
    }

    public List<Produto> getProdutosSelecionados() {
        if (produtosSelecionados == null) {
            produtosSelecionados = new ArrayList<Produto>();
        }
        return produtosSelecionados;
    }

    public CartesianChartModel getDadosProdutosSelecionados() {
        return dadosProdutosSelecionados;
    }

    public void carregarHistoricoPrecoProduto() {
        try {
            initGraficoHistoricoPrecoProduto();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LineChartModel getGraficoDeGastosPorMes() {
        return graficoDeGastosPorMes;
    }

    public ServicoRelatorio getServico() {
        return servico;
    }

    public List getMesesDisponiveis() {
        return mesesDisponiveis;
    }

    public PieChartModel getGraficoDeGastosPorCategoria() {
        return graficoDeGastosPorCategoria;
    }

    public void setMesReferencia(Date mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public Date getMesReferencia() {
        return mesReferencia;
    }

    public LineChartModel getDadosGraficoConsumoEletricidade() {
        return dadosGraficoConsumoEletricidade;
    }

}
