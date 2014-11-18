package net.marcoreis.controlapreco.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.service.ServicoRelatorio;
import net.marcoreis.controlapreco.vo.AcaoVO;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.BubbleChartModel;
import org.primefaces.model.chart.BubbleChartSeries;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class ControladorRelatorio extends ControladorGenerico {

    private static final long serialVersionUID = -8989131094545262992L;
    private ServicoRelatorio servico = new ServicoRelatorio();
    //
    private String mesAnoReferencia;
    private String simbolo;
    private Date dataInicio;
    private Date dataFim;
    private List<String> mesesDisponiveis;
    private List<Produto> produtos;
    private List<Produto> produtosSelecionados;
    //
    private LineChartModel graficoDeLinhasGastosMensais;
    private BarChartModel graficoDeBarrasGastosMensais;
    private PieChartModel graficoDePizzaGastosPorCategoria;
    private BarChartModel graficoDeBarrasVerticaisGastosPorCategoria;
    private HorizontalBarChartModel graficoDeBarrasHorizontaisGastosPorCategoria;
    private LineChartModel graficoDeLinhasConsumoEletricidade;
    private CartesianChartModel graficoDeLinhasProdutosSelecionados;
    private CartesianChartModel graficoDeBarrasReceitasDepesas;
    private BarChartModel graficoDeBarrasReceitasDepesasCombinado;
    private CartesianChartModel graficoDeLinhasReceitasDepesas;
    private MeterGaugeChartModel graficoGaugeReceitasDepesas;
    private BubbleChartModel graficoDeBolhasGastosMensaisPorCategoria;
    private OhlcChartModel graficoOhlcHistoricoPrecoAcoes;
    //
    private SimpleDateFormat sdfAnoMesDia = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
    public void init() {
        mesesDisponiveis = getServico().findMesesDisponiveis();
        initDatas();
    }

    private void initDatas() {
        SimpleDateFormat sdfMesAno = new SimpleDateFormat("MM/yyyy");
        try {
            if (getDataInicio() == null) {
                setDataInicio(sdfMesAno.parse("09/2014"));
            }
            if (getDataFim() == null) {
                setDataFim(sdfMesAno.parse("12/2014"));
            }
            if (getSimbolo() == null) {
                setSimbolo("ORCL");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void initGraficoDeLinhasHistoricoPrecoProduto() {
        graficoDeLinhasProdutosSelecionados = new LineChartModel();
        graficoDeLinhasProdutosSelecionados.getAxes().put(AxisType.X,
                new CategoryAxis());

        Axis xaxis = graficoDeLinhasProdutosSelecionados.getAxis(AxisType.X);
        xaxis.setLabel("Mês");
        Axis yaxis = graficoDeLinhasProdutosSelecionados.getAxis(AxisType.Y);
        yaxis.setLabel("Valor (R$)");
        for (Produto p : getProdutosSelecionados()) {
            List<Object[]> historico = getServico()
                    .consultarHistoricoPrecoProduto(p);
            LineChartSeries serie = new LineChartSeries();
            for (Object[] dados : historico) {
                String nomeProduto = dados[0].toString();
                Double valor = (Double) dados[1];
                String data = dados[2].toString();
                serie.setLabel(nomeProduto);
                serie.set(data, valor);
            }
            if (historico.size() > 0) {
                graficoDeLinhasProdutosSelecionados.addSeries(serie);
            }
        }
        //
        if (graficoDeLinhasProdutosSelecionados.getSeries().size() == 0) {
            String titulo = "Selecione produtos com preços cadastrados";
            ChartSeries serie = new LineChartSeries(titulo);
            serie.set("", 0);
            graficoDeLinhasProdutosSelecionados.addSeries(serie);
        }
        //
        graficoDeLinhasProdutosSelecionados.setLegendPosition("ne");
        graficoDeLinhasProdutosSelecionados
                .setExtender("fnGraficoDeLinhasHistoricoPrecos");
    }

    public void initGraficoDeBarrasReceitasDespesas() {
        graficoDeBarrasReceitasDepesas = new BarChartModel();
        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new ChartSeries();
        int i = 0;
        for (Object[] dados : receitas) {
            Double valor = (Double) dados[1];
            String dataMesAno = dados[0].toString();
            serieReceitas.set(dataMesAno, valor);
        }
        serieReceitas.setLabel("Receitas");
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        ChartSeries serieDespesas = new ChartSeries();
        i = 0;
        for (Object[] dados : gastos) {
            Double valor = (Double) dados[1];
            String dataMesAno = dados[0].toString();
            serieDespesas.set(dataMesAno, valor);
        }
        serieDespesas.setLabel("Despesas");
        //
        graficoDeBarrasReceitasDepesas.addSeries(serieDespesas);
        graficoDeBarrasReceitasDepesas.addSeries(serieReceitas);
        graficoDeBarrasReceitasDepesas.setLegendPosition("ne");
        graficoDeBarrasReceitasDepesas
                .setExtender("fnGraficoDeBarrasReceitasDespesas");
    }

    public void initGraficoDeBarrasReceitasDespesasCombinado() {
        graficoDeBarrasReceitasDepesasCombinado = new BarChartModel();
        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new ChartSeries();
        for (Object[] dados : receitas) {
            Double valor = (Double) dados[1];
            String dataMesAno = dados[0].toString();
            serieReceitas.set(dataMesAno, valor);
        }
        serieReceitas.setLabel("Receitas");
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        ChartSeries serieDespesas = new LineChartSeries();
        for (Object[] dados : gastos) {
            Double valor = (Double) dados[1];
            String dataMesAno = dados[0].toString();
            serieDespesas.set(dataMesAno, valor);
        }
        serieDespesas.setLabel("Despesas");
        //
        graficoDeBarrasReceitasDepesasCombinado.addSeries(serieReceitas);
        graficoDeBarrasReceitasDepesasCombinado.addSeries(serieDespesas);
        graficoDeBarrasReceitasDepesasCombinado.setLegendPosition("ne");
        graficoDeBarrasReceitasDepesasCombinado.setMouseoverHighlight(false);
        graficoDeBarrasReceitasDepesasCombinado
                .setExtender("fnGraficoDeBarrasReceitasDespesasCombinado");
    }

    public void initGraficoDeLinhasReceitasDespesas() {
        graficoDeLinhasReceitasDepesas = new LineChartModel();
        graficoDeLinhasReceitasDepesas.getAxes().put(AxisType.X,
                new CategoryAxis());

        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new LineChartSeries();
        for (Object[] dados : receitas) {
            String dataMesAno = dados[0].toString();
            Double valor = (Double) dados[1];
            serieReceitas.set(dataMesAno, valor);
        }
        serieReceitas.setLabel("Receitas");
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        ChartSeries serieDespesas = new LineChartSeries();
        for (Object[] dados : gastos) {
            String dataMesAno = dados[0].toString();
            Double valor = (Double) dados[1];
            serieDespesas.set(dataMesAno, valor);
        }
        serieDespesas.setLabel("Despesas");
        //
        graficoDeLinhasReceitasDepesas.addSeries(serieDespesas);
        graficoDeLinhasReceitasDepesas.addSeries(serieReceitas);
        graficoDeLinhasReceitasDepesas.setLegendPosition("ne");
        graficoDeLinhasReceitasDepesas.setShadow(true);
        graficoDeLinhasReceitasDepesas
                .setExtender("fnGraficoDeLinhasReceitasDespesas");
    }

    public void initProdutos() {
        produtos = getServico().find("from Produto order by nome");
    }

    public void initGraficoGaugeReceitasDespesas() {
        //
        List<Number> intervalos = new ArrayList<Number>() {
            {
                add(10);
                add(30);
                add(50);
            }
        };
        //
        String mes = getMesAnoReferencia();
        if (mes == null && getMesesDisponiveis() != null) {
            mes = getMesesDisponiveis().get(0);
        }
        Double despesas = getServico().consultarDespesasMes(mes);
        Double receitas = getServico().consultarReceitasMes(mes);

        int percentualEconomizado = (int) ((despesas * -100) / receitas) + 100;

        if (percentualEconomizado > 50) {
            intervalos.add(percentualEconomizado);
        }
        if (percentualEconomizado < 0) {
            graficoGaugeReceitasDepesas.setMin(-30);
        }
        graficoGaugeReceitasDepesas = new MeterGaugeChartModel(
                percentualEconomizado, intervalos);
        graficoGaugeReceitasDepesas.setSeriesColors("FF0000, FFFF00, 00CC00");
        String titulo = "Economia (" + percentualEconomizado + "%)";
        titulo += "<br />Receitas (" + receitas + "), despesas (" + despesas
                + ")";
        graficoGaugeReceitasDepesas.setTitle(titulo);
        graficoGaugeReceitasDepesas.setGaugeLabel("%");
        graficoGaugeReceitasDepesas
                .setExtender("fnGraficoGaugeReceitasDespesas");

    }

    public void initGraficoDeBolhasGastosMensaisPorCategoria() {
        graficoDeBolhasGastosMensaisPorCategoria = new BubbleChartModel();
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(
                getMesAnoReferencia(), "ct.nome");
        int i = 1;
        for (Object[] dados : gastos) {
            String categoria = dados[0].toString();
            int y = (int) Float.parseFloat(dados[1].toString());
            int radius = (int) (y / 3.14);
            int x = i++;
            graficoDeBolhasGastosMensaisPorCategoria.add(new BubbleChartSeries(
                    categoria, x, y, radius));
        }
        if (gastos.size() == 0) {
            graficoDeBolhasGastosMensaisPorCategoria.add(new BubbleChartSeries(
                    "Nenhuma categoria cadastrada", 1, 1, 5));
        }
        graficoDeBolhasGastosMensaisPorCategoria
                .setTitle("Gastos por categoria");
        // graficoDeBolhasGastosMensaisPorCategoria.setShadow(false);
        graficoDeBolhasGastosMensaisPorCategoria
                .setExtender("fnGraficoDeBolhasGastosMensaisPorCategoria");
    }

    public void atualizarGraficosDeGastosPorCategoria() {
        initGraficoDePizzaGastosPorCategoria();
        initGraficoDeBarrasVerticaisGastosPorCategoria();
        initGraficoDeBarrasHorizontaisGastosPorCategoria();
    }

    public void initGraficoDePizzaGastosPorCategoria() {
        graficoDePizzaGastosPorCategoria = new PieChartModel();
        String mes = getMesAnoReferencia();
        if (mes == null && getMesesDisponiveis() != null) {
            mes = getMesesDisponiveis().get(0);
        }
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(mes,
                "nomeCategoria");
        if (gastos.size() == 0) {
            graficoDePizzaGastosPorCategoria.set("", 0);
        }
        for (Object[] dados : gastos) {
            String chave = dados[0].toString();
            Number valor = (Number) dados[1];
            graficoDePizzaGastosPorCategoria.set(chave, valor);
        }
        graficoDePizzaGastosPorCategoria.setTitle("Gastos por categoria");
        graficoDePizzaGastosPorCategoria.setShowDataLabels(true);
        graficoDePizzaGastosPorCategoria.setLegendPosition("e");
    }

    public void initGraficoDeBarrasVerticaisGastosPorCategoria() {
        graficoDeBarrasVerticaisGastosPorCategoria = new BarChartModel();
        graficoDeBarrasVerticaisGastosPorCategoria
                .setTitle("Gastos por categoria em " + getMesAnoReferencia());
        Axis yaxis = graficoDeBarrasVerticaisGastosPorCategoria
                .getAxis(AxisType.Y);
        yaxis.setLabel("Total (R$)");
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(
                getMesAnoReferencia(), "valor desc");
        ChartSeries serie = new ChartSeries();
        for (Object[] dados : gastos) {
            String nomeCategoria = dados[0].toString();
            Number valor = (Number) dados[1];
            serie.set(nomeCategoria, valor);
        }
        graficoDeBarrasVerticaisGastosPorCategoria.addSeries(serie);
        graficoDeBarrasVerticaisGastosPorCategoria.setShowPointLabels(true);
        graficoDeBarrasVerticaisGastosPorCategoria
                .setExtender("fnGraficoDeBarrasVerticaisGastosPorCategoria");
    }

    public void initGraficoDeBarrasHorizontaisGastosPorCategoria() {
        graficoDeBarrasHorizontaisGastosPorCategoria = new HorizontalBarChartModel();
        graficoDeBarrasHorizontaisGastosPorCategoria
                .setTitle("Gastos por categoria em " + getMesAnoReferencia());
        Axis xaxis = graficoDeBarrasHorizontaisGastosPorCategoria
                .getAxis(AxisType.X);
        xaxis.setLabel("Total (R$)");
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(
                getMesAnoReferencia(), "valor");
        ChartSeries serie = new ChartSeries();
        for (Object[] dados : gastos) {
            String nomeCategoria = dados[0].toString();
            Number valor = (Number) dados[1];
            serie.set(nomeCategoria, valor);
        }
        graficoDeBarrasHorizontaisGastosPorCategoria.addSeries(serie);
        graficoDeBarrasHorizontaisGastosPorCategoria.setShowPointLabels(true);
        graficoDeBarrasHorizontaisGastosPorCategoria
                .setExtender("fnGraficoDeBarrasHorizontaisGastosPorCategoria");
    }

    public void initGraficoDeLinhasGastosMensais() throws ParseException {
        SimpleDateFormat sdfMesPorExtenso = new SimpleDateFormat("MMMMM",
                new Locale("pt", "BR"));
        SimpleDateFormat sdfMesAno = new SimpleDateFormat("MM/yyyy");

        // 1
        graficoDeLinhasGastosMensais = new LineChartModel();
        graficoDeLinhasGastosMensais.getAxes().put(AxisType.X,
                new CategoryAxis());
        // 2
        Axis xaxis = graficoDeLinhasGastosMensais.getAxis(AxisType.X);
        xaxis.setLabel("Mês");
        Axis yaxis = graficoDeLinhasGastosMensais.getAxis(AxisType.Y);
        yaxis.setLabel("Total (R$)");
        // 3
        LineChartSeries serie = new LineChartSeries();
        // 4
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        for (Object[] dados : gastos) {
            Number valor = (Number) dados[1];
            Date dataDate = sdfMesAno.parse(dados[0].toString());
            String dataMesPorExtenso = sdfMesPorExtenso.format(dataDate);
            serie.set(dataMesPorExtenso, valor);
        }
        //
        graficoDeLinhasGastosMensais.addSeries(serie);
        graficoDeLinhasGastosMensais
                .setExtender("fnGraficoDeLinhasGastosMensais");
    }

    public void initGraficoDeBarrasGastosMensais() {
        try {
            SimpleDateFormat sdfMesPorExtenso = new SimpleDateFormat("MMMMM",
                    new Locale("pt", "BR"));
            SimpleDateFormat sdfMesAno = new SimpleDateFormat("MM/yyyy");
            // 1
            graficoDeBarrasGastosMensais = new BarChartModel();
            // 2
            Axis xaxis = graficoDeBarrasGastosMensais.getAxis(AxisType.X);
            xaxis.setLabel("Mês");
            Axis yaxis = graficoDeBarrasGastosMensais.getAxis(AxisType.Y);
            yaxis.setLabel("Total (R$)");
            // 3
            List<Object[]> gastos = getServico().consultarGastosPorMes();
            // 4
            BarChartSeries serie = new BarChartSeries();
            for (Object[] dados : gastos) {
                Date dataDate = sdfMesAno.parse(dados[0].toString());
                String dataMesPorExtenso = sdfMesPorExtenso.format(dataDate);
                Number valor = (Number) dados[1];
                serie.set(dataMesPorExtenso, valor);
            }
            graficoDeBarrasGastosMensais.addSeries(serie);
            graficoDeBarrasGastosMensais.setTitle("Gastos por mês");
            graficoDeBarrasGastosMensais
                    .setExtender("fnGraficoDeBarrasGastosMensais");
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    public void initGraficoOhlcHistoricoPrecoAcoes() {
        graficoOhlcHistoricoPrecoAcoes = new OhlcChartModel();
        List<AcaoVO> lista = getServico().consultarHistoricoPrecoAcoes(
                getSimbolo(), sdfAnoMesDia.format(getDataInicio()),
                sdfAnoMesDia.format(getDataFim()));
        if (lista == null) {
            return;
        }
        Collections.reverse(lista);
        for (AcaoVO acao : lista) {
            String data = "'" + acao.getDate() + "'";
            graficoOhlcHistoricoPrecoAcoes
                    .add(new OhlcChartSeries(data, acao.getOpen(), acao
                            .getHigh(), acao.getLow(), acao.getClose()));
        }
        graficoOhlcHistoricoPrecoAcoes
                .setExtender("fnGraficoOhlcHistoricoPrecoAcoes");
    }

    public void initGraficoDeLinhasConsumoEletricidade() {
        // 1
        graficoDeLinhasConsumoEletricidade = new LineChartModel();
        graficoDeLinhasConsumoEletricidade.getAxes().put(AxisType.X,
                new CategoryAxis());
        // 2
        LineChartSeries serie = new LineChartSeries();
        serie.set("07/2013", 133);
        serie.set("08/2013", 123);
        serie.set("09/2013", 154);
        serie.set("10/2013", 142);
        serie.set("11/2013", 151);
        serie.set("12/2013", 112);
        serie.set("01/2014", 139);
        serie.set("02/2014", 117);
        serie.set("03/2014", 124);
        serie.set("04/2014", 107);
        serie.set("05/2014", 157);
        serie.set("06/2014", 98);
        serie.set("07/2014", 145);
        // 3
        String titulo = "Consumo de eletricidade (kWh)";
        graficoDeLinhasConsumoEletricidade.setTitle(titulo);
        graficoDeLinhasConsumoEletricidade.getAxis(AxisType.Y).setMin(0);
        // 4
        graficoDeLinhasConsumoEletricidade.addSeries(serie);
        // 5
        graficoDeLinhasConsumoEletricidade
                .setExtender("fnGraficoDeLinhasConsumoEletricidade");
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

    public CartesianChartModel getGraficoDeLinhasProdutosSelecionados() {
        return graficoDeLinhasProdutosSelecionados;
    }

    public void carregarHistoricoPrecoProduto() {
        initGraficoDeLinhasHistoricoPrecoProduto();
    }

    public BarChartModel getGraficoDeBarrasGastosMensais() {
        return graficoDeBarrasGastosMensais;
    }

    public LineChartModel getGraficoDeLinhasGastosMensais() {
        return graficoDeLinhasGastosMensais;
    }

    public ServicoRelatorio getServico() {
        return servico;
    }

    public List<String> getMesesDisponiveis() {
        return mesesDisponiveis;
    }

    public PieChartModel getGraficoDePizzaGastosPorCategoria() {
        return graficoDePizzaGastosPorCategoria;
    }

    public void setMesAnoReferencia(String mesAnoReferencia) {
        this.mesAnoReferencia = mesAnoReferencia;
    }

    public String getMesAnoReferencia() {
        return mesAnoReferencia;
    }

    public LineChartModel getGraficoDeLinhasConsumoEletricidade() {
        return graficoDeLinhasConsumoEletricidade;
    }

    public CartesianChartModel getGraficoDeBarrasReceitasDepesas() {
        return graficoDeBarrasReceitasDepesas;
    }

    public CartesianChartModel getGraficoDeLinhasReceitasDepesas() {
        return graficoDeLinhasReceitasDepesas;
    }

    public MeterGaugeChartModel getGraficoGaugeReceitasDepesas() {
        return graficoGaugeReceitasDepesas;
    }

    public BarChartModel getGraficoDeBarrasVerticaisGastosPorCategoria() {
        return graficoDeBarrasVerticaisGastosPorCategoria;
    }

    public HorizontalBarChartModel getGraficoDeBarrasHorizontaisGastosPorCategoria() {
        return graficoDeBarrasHorizontaisGastosPorCategoria;
    }

    public BubbleChartModel getGraficoDeBolhasGastosMensaisPorCategoria() {
        return graficoDeBolhasGastosMensaisPorCategoria;
    }

    public OhlcChartModel getGraficoOhlcHistoricoPrecoAcoes() {
        return graficoOhlcHistoricoPrecoAcoes;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public BarChartModel getGraficoDeBarrasReceitasDepesasCombinado() {
        return graficoDeBarrasReceitasDepesasCombinado;
    }
}
