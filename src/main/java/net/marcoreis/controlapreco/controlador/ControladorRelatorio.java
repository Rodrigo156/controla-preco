package net.marcoreis.controlapreco.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.service.ServicoRelatorio;

import org.joda.time.DateTime;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class ControladorRelatorio extends ControladorGenerico {

    private static final long serialVersionUID = -8989131094545262992L;
    private ServicoRelatorio servico = new ServicoRelatorio();
    //
    private String mesAnoReferencia;
    private List<?> mesesDisponiveis;
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
    private CartesianChartModel graficoDeLinhasReceitasDepesas;
    private MeterGaugeChartModel graficoGaugeReceitasDepesas;
    //
    private SimpleDateFormat sdfMesPorExtenso = new SimpleDateFormat("MMMMM",
            new Locale("pt", "BR"));
    private SimpleDateFormat sdfMesAno = new SimpleDateFormat("MM/yyyy");
    private SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
    private SimpleDateFormat sdfAnoMesDia = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfDiaMesAno = new SimpleDateFormat("dd/MM/yyyy");

    @PostConstruct
    public void init() {
        mesAnoReferencia = sdfMesAno
                .format(new Date(System.currentTimeMillis()));
    }

    public void initGraficoDeLinhasHistoricoPrecoProduto() {
        graficoDeLinhasProdutosSelecionados = new LineChartModel();
        Date dataInicio = new Date(System.currentTimeMillis());
        for (Produto p : getProdutosSelecionados()) {
            List<Object[]> historico = getServico()
                    .consultarHistoricoPrecoProduto(p);
            ChartSeries serie = new LineChartSeries();
            for (Object[] dados : historico) {
                String nomeProduto = dados[0].toString();
                Double valor = (Double) dados[1];
                // Para usar um eixo com data, os valores devem estar em formato
                // yyyy-MM-dd
                String dataString = "01/" + dados[2];
                String dataInvertida;
                try {
                    dataInvertida = sdfAnoMesDia.format(sdfDiaMesAno
                            .parse(dataString));
                    Date dataDate;
                    dataDate = sdfAnoMesDia.parse(dataInvertida);
                    if (dataDate.before(dataInicio)) {
                        dataInicio = dataDate;
                    }
                    serie.setLabel(nomeProduto);
                    serie.set(dataInvertida, valor);
                } catch (ParseException e) {
                    logger.error(e);
                }
            }
            if (historico.size() > 0) {
                graficoDeLinhasProdutosSelecionados.addSeries(serie);
            }
        }
        //
        if (graficoDeLinhasProdutosSelecionados.getSeries().size() == 0) {
            String titulo = "Selecione produtos com preços cadastrados";
            ChartSeries serie = new LineChartSeries(titulo);
            serie.set("2014-01-01", 0);
            graficoDeLinhasProdutosSelecionados.addSeries(serie);
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
        graficoDeLinhasProdutosSelecionados.getAxes().put(AxisType.X, eixoData);
        graficoDeLinhasProdutosSelecionados.setTitle("Histórico de preços");
        graficoDeLinhasProdutosSelecionados.setLegendPosition("ne");
        graficoDeLinhasProdutosSelecionados
                .setExtender("fnGraficoDeLinhasHistoricoPrecos");
    }

    public void initGraficoDeBarrasReceitasDespesas() throws ParseException {
        graficoDeBarrasReceitasDepesas = new BarChartModel();
        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new ChartSeries();
        Date dataInicio = new Date(System.currentTimeMillis());
        Date dataFim = sdfMesAno.parse("01/2014");
        int i = 0;
        for (Object[] dados : receitas) {
            Date dataDate = sdfMesAno.parse(dados[0].toString());
            String data = sdfMesPorExtenso.format(dataDate);
            Double valor = (Double) dados[1];
            if (dataDate.before(dataInicio)) {
                dataInicio = dataDate;
            }
            if (dataDate.after(dataFim)) {
                dataFim = dataDate;
            }
            serieReceitas.set(data, valor);
        }
        serieReceitas.setLabel("Receitas");
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        ChartSeries serieDespesas = new ChartSeries();
        i = 0;
        for (Object[] dados : gastos) {
            Date dataDate = sdfMesAno.parse(dados[0].toString());
            String data = sdfMesPorExtenso.format(dataDate);
            Double valor = (Double) dados[1];
            if (dataDate.before(dataInicio)) {
                dataInicio = dataDate;
            }
            if (dataDate.after(dataFim)) {
                dataFim = dataDate;
            }

            serieDespesas.set(data, valor);
        }
        serieDespesas.setLabel("Despesas");
        //
        graficoDeBarrasReceitasDepesas.addSeries(serieDespesas);
        graficoDeBarrasReceitasDepesas.addSeries(serieReceitas);
        graficoDeBarrasReceitasDepesas.setLegendPosition("ne");
        graficoDeBarrasReceitasDepesas
                .setExtender("fnGraficoDeBarrasReceitasDespesas");
    }

    public void initGraficoDeLinhasReceitasDespesas() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        graficoDeLinhasReceitasDepesas = new LineChartModel();
        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new LineChartSeries();
        Date dataInicio = new Date(System.currentTimeMillis());
        Date dataFim = sdf.parse("2014-01-01");
        int i = 0;
        for (Object[] dados : receitas) {
            String dataString = "01/" + dados[0];
            String dataInvertida = sdfAnoMesDia.format(sdfDiaMesAno
                    .parse(dataString));
            Double valor = (Double) dados[1];
            Date dataDate = sdf.parse(dataInvertida);
            if (dataDate.before(dataInicio)) {
                dataInicio = dataDate;
            }
            if (dataDate.after(dataFim)) {
                dataFim = dataDate;
            }
            serieReceitas.set(dataInvertida, valor);
        }
        serieReceitas.setLabel("Receitas");
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        ChartSeries serieDespesas = new LineChartSeries();
        i = 0;
        for (Object[] dados : gastos) {
            String dataString = "01/" + dados[0];
            String dataInvertida = sdfAnoMesDia.format(sdfDiaMesAno
                    .parse(dataString));
            Double valor = (Double) dados[1];
            Date dataDate = sdf.parse(dataInvertida);
            if (dataDate.before(dataInicio)) {
                dataInicio = dataDate;
            }
            if (dataDate.after(dataFim)) {
                dataFim = dataDate;
            }

            serieDespesas.set(dataInvertida, valor);
        }
        serieDespesas.setLabel("Despesas");
        //
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickInterval("1 month");
        eixoData.setTickAngle(-60);
        eixoData.setMin(sdf.format(dataInicio));
        eixoData.setMax(sdf.format(dataFim));
        graficoDeLinhasReceitasDepesas.getAxes().put(AxisType.X, eixoData);
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
        Double despesas = getServico().consultarDespesasMes(
                getMesAnoReferencia());
        Double receitas = getServico().consultarReceitasMes(
                getMesAnoReferencia());

        // if (receitas > 0 && despesas > 0 && receitas > despesas) {
        // percentualEconomizado = receitas / despesas;
        // } else if (receitas > 0 && despesas > 0 && receitas < despesas) {
        // percentualEconomizado = receitas / despesas * -1;
        // }

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
        graficoGaugeReceitasDepesas.setTitle(titulo);
        graficoGaugeReceitasDepesas.setGaugeLabel("%");
        graficoGaugeReceitasDepesas.setExtender("fnGraficoGaugeReceitasDespesas");

    }

    public void atualizarGraficosDeGastosPorCategoria() {
        try {
            initGraficoDePizzaGastosPorCategoria();
            initGraficoDeBarrasVerticaisGastosPorCategoria();
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    public void initGraficoDePizzaGastosPorCategoria() {
        graficoDePizzaGastosPorCategoria = new PieChartModel();
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(
                getMesAnoReferencia(), "nomeCategoria");
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

    public void initGraficoDeBarrasVerticaisGastosPorCategoria()
            throws ParseException {
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

    public void initGraficoDeBarrasHorizontaisGastosPorCategoria()
            throws ParseException {
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
        // 1
        graficoDeLinhasGastosMensais = new LineChartModel();
        // 2
        Axis xaxis = graficoDeLinhasGastosMensais.getAxis(AxisType.X);
        xaxis.setLabel("Mês");
        Axis yaxis = graficoDeLinhasGastosMensais.getAxis(AxisType.Y);
        yaxis.setLabel("Total (R$)");
        // 3
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        // 4
        ChartSeries serie = new ChartSeries();
        // A data de início deve ser definida, caso contrário o jqPlot não
        // consegue
        // montar o gráfico
        // As datas devem estar no formato yyyy-MM-dd.
        String dataInicio = null;
        for (Object[] dados : gastos) {
            String dataString = "01/" + dados[0];
            String dataInvertida = sdfAnoMesDia.format(sdfDiaMesAno
                    .parse(dataString));
            Number valor = (Number) dados[1];
            serie.set(dataInvertida, valor);
            if (dataInicio == null) {
                dataInicio = dataInvertida;
            }
        }
        // 5
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickAngle(-60);
        eixoData.setTickInterval("1 month");
        eixoData.setMin(recuperarMesAnterior(dataInicio));
        graficoDeLinhasGastosMensais.getAxes().put(AxisType.X, eixoData);
        // 6
        graficoDeLinhasGastosMensais.addSeries(serie);
        graficoDeLinhasGastosMensais.setShowDatatip(false);
        graficoDeLinhasGastosMensais
                .setExtender("fnGraficoDeLinhasGastosMensais");
    }

    public void initGraficoDeBarrasGastosMensais() {
        try {
            graficoDeBarrasGastosMensais = new BarChartModel();
            Axis xaxis = graficoDeBarrasGastosMensais.getAxis(AxisType.X);
            xaxis.setLabel("Mês");
            Axis yaxis = graficoDeBarrasGastosMensais.getAxis(AxisType.Y);
            yaxis.setLabel("Total (R$)");
            List<Object[]> gastos = getServico().consultarGastosPorMes();
            ChartSeries serie = new ChartSeries();
            for (Object[] dados : gastos) {
                Date dataDate = sdfMesAno.parse(dados[0].toString());
                String data = sdfMesPorExtenso.format(dataDate);
                Number valor = (Number) dados[1];
                serie.set(data, valor);
            }
            graficoDeBarrasGastosMensais.addSeries(serie);
            graficoDeBarrasGastosMensais.setTitle("Gastos por mês");
            graficoDeBarrasGastosMensais.setShowPointLabels(true);
            graficoDeBarrasGastosMensais
                    .setExtender("fnGraficoDeBarrasGastosMensais");
        } catch (ParseException e) {
            logger.error(e);
        }
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

    public void initGraficoDeLinhasConsumoEletricidade() {
        // 1
        graficoDeLinhasConsumoEletricidade = new LineChartModel();
        // 2
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
        // 3
        String titulo = "Consumo de eletricidade (kWh)";
        graficoDeLinhasConsumoEletricidade.setTitle(titulo);
        graficoDeLinhasConsumoEletricidade.getAxis(AxisType.Y).setMin(0);
        // 4
        graficoDeLinhasConsumoEletricidade.addSeries(serie);
        // 5
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickInterval("1 month");
//        eixoData.setMin("2013-06-01");
        eixoData.setMax("2014-07-01");
        eixoData.setTickAngle(-60);
        graficoDeLinhasConsumoEletricidade.getAxes().put(AxisType.X, eixoData);
        // 6
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
        try {
            initGraficoDeLinhasHistoricoPrecoProduto();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public List getMesesDisponiveis() {
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
}
