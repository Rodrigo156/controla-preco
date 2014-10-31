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
    private BarChartModel graficoDeBarrasHorizontaisGastosPorCategoria;
    private LineChartModel graficoDeLinhasConsumoEletricidade;
    private CartesianChartModel graficoDeLinhasProdutosSelecionados;
    private CartesianChartModel graficoDeBarrasReceitasDepesas;
    private CartesianChartModel graficoDeLinhasReceitasDepesas;
    private MeterGaugeChartModel graficoGaugeReceitasDepesas;
    private SimpleDateFormat sdfMesAno = new SimpleDateFormat("MM/yyyy");
    private SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
    private SimpleDateFormat sdfAnoMesDia = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
    public void init() {
        mesAnoReferencia = sdfMesAno
                .format(new Date(System.currentTimeMillis()));
    }

    public void initGraficoHistoricoPrecoProduto() {
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
                // yyyy-MM-dd, por isso adicionamos
                // o "-01" no final
                String data = dados[2] + "-01";
                Date dataDate;
                try {
                    dataDate = sdfAnoMesDia.parse(data);
                    if (dataDate.before(dataInicio)) {
                        dataInicio = dataDate;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                .setExtender("fnGraficoHistoricoPrecos");
    }

    public void initGraficoReceitasDespesasBarras() throws ParseException {
        SimpleDateFormat sdfAnoMes = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMMM", new Locale(
                "pt", "BR"));
        graficoDeBarrasReceitasDepesas = new BarChartModel();
        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new ChartSeries();
        Date dataInicio = new Date(System.currentTimeMillis());
        Date dataFim = sdfAnoMes.parse("2014-01");
        int i = 0;
        for (Object[] dados : receitas) {
            Date dataDate = sdfAnoMes.parse(dados[0].toString());
            String data = sdfMes.format(dataDate);
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
            Date dataDate = sdfAnoMes.parse(dados[0].toString());
            String data = sdfMes.format(dataDate);
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
                .setExtender("fnGraficoReceitasDespesasBarras");
    }

    public void initGraficoReceitasDespesasLinhas() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        graficoDeLinhasReceitasDepesas = new LineChartModel();
        List<Object[]> receitas = getServico().consultarHistoricoReceitas();
        ChartSeries serieReceitas = new LineChartSeries();
        Date dataInicio = new Date(System.currentTimeMillis());
        Date dataFim = sdf.parse("2014-01-01");
        int i = 0;
        for (Object[] dados : receitas) {
            String data = dados[0] + "-01";
            Double valor = (Double) dados[1];
            Date dataDate = sdf.parse(data);
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
        ChartSeries serieDespesas = new LineChartSeries();
        i = 0;
        for (Object[] dados : gastos) {
            String data = dados[0] + "-01";
            Double valor = (Double) dados[1];
            Date dataDate = sdf.parse(data);
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
        graficoDeLinhasReceitasDepesas
                .setExtender("fnGraficoReceitasDespesasLinhas");
    }

    public void initProdutos() {
        produtos = getServico().find("from Produto order by nome");
    }

    public void initGraficoReceitasDespesasGauge() {
        //
        List<Number> intervalos = new ArrayList<Number>() {
            {
                // add(-10);
                // add(-30);
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
        Double percentualEconomizado = 0d;

        if (receitas > 0 && despesas > 0 && receitas > despesas) {
            percentualEconomizado = receitas / despesas;
        } else if (receitas > 0 && despesas > 0 && receitas < despesas) {
            percentualEconomizado = receitas / despesas * -1;
        }

        if (percentualEconomizado > 50) {
            intervalos.add(percentualEconomizado);
        }
        graficoGaugeReceitasDepesas = new MeterGaugeChartModel(
                percentualEconomizado, intervalos);
        graficoGaugeReceitasDepesas.setSeriesColors("FF0000, FFFF00, 00CC00");
        String titulo = "Percentual economizado das receitas";
        graficoGaugeReceitasDepesas.setTitle(titulo);
        graficoGaugeReceitasDepesas.setGaugeLabel("%");

    }

    public void atualizarGraficosDeGastosPorCategoria() {
        try {
            initGraficoPizzaGastosPorCategoria();
            initGraficoBarrasVerticaisGastosPorCategoria();
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    public void initGraficoPizzaGastosPorCategoria() {
        graficoDePizzaGastosPorCategoria = new PieChartModel();
        List<Object[]> gastos = getServico().consultarGastosPorCategoria(
                getMesAnoReferencia(), "valor");
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

    public void initGraficoBarrasVerticaisGastosPorCategoria()
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

    public void initGraficoBarrasHorizontaisGastosPorCategoria()
            throws ParseException {
        graficoDeBarrasHorizontaisGastosPorCategoria = new BarChartModel();
        graficoDeBarrasHorizontaisGastosPorCategoria
                .setTitle("Gastos por categoria em " + getMesAnoReferencia());
        Axis yaxis = graficoDeBarrasHorizontaisGastosPorCategoria
                .getAxis(AxisType.Y);
        yaxis.setLabel("Total (R$)");
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

    public void initGraficoGastosMensaisLinhas() {
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
        graficoDeLinhasGastosMensais.getAxes().put(AxisType.X, eixoData);
        // 6
        graficoDeLinhasGastosMensais.addSeries(serie);
        graficoDeLinhasGastosMensais.setShowDatatip(false);
        graficoDeLinhasGastosMensais
                .setExtender("fnGraficoGastosMensaisLinhas");
    }

    public void initGraficoGastosMensaisBarras() throws ParseException {
        // 1
        graficoDeBarrasGastosMensais = new BarChartModel();
        SimpleDateFormat sdfAnoMes = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMMM", new Locale(
                "pt", "BR"));
        // 2
        Axis xaxis = graficoDeBarrasGastosMensais.getAxis(AxisType.X);
        xaxis.setLabel("Mês");
        Axis yaxis = graficoDeBarrasGastosMensais.getAxis(AxisType.Y);
        yaxis.setLabel("Total (R$)");
        // 3
        List<Object[]> gastos = getServico().consultarGastosPorMes();
        // 4
        ChartSeries serie = new ChartSeries();
        String dataInicio = null;
        for (Object[] dados : gastos) {
            Date dataDate = sdfAnoMes.parse(dados[0].toString());
            String data = sdfMes.format(dataDate);
            Number valor = (Number) dados[1];
            serie.set(data, valor);
            if (dataInicio == null) {
                dataInicio = data;
            }
        }
        // 6
        graficoDeBarrasGastosMensais.addSeries(serie);
        graficoDeBarrasGastosMensais.setTitle("Gastos por mês");
        graficoDeBarrasGastosMensais.setShowPointLabels(true);
        graficoDeBarrasGastosMensais
                .setExtender("fnGraficoGastosMensaisBarras");
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

    public void initGraficoConsumoEletricidade() {
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
        graficoDeLinhasConsumoEletricidade = new LineChartModel();
        String titulo = "Consumo de eletricidade (kWh)";
        graficoDeLinhasConsumoEletricidade.setTitle(titulo);
        graficoDeLinhasConsumoEletricidade.addSeries(serie);
        //
        graficoDeLinhasConsumoEletricidade.getAxis(AxisType.Y).setMin(0);
        //
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickInterval("1 month");
        eixoData.setMin("2013-06-01");
        eixoData.setTickAngle(-60);
        //
        graficoDeLinhasConsumoEletricidade.getAxes().put(AxisType.X, eixoData);
        graficoDeLinhasConsumoEletricidade
                .setExtender("fnGraficoConsumoEletricidade");
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
        return graficoDeLinhasProdutosSelecionados;
    }

    public void carregarHistoricoPrecoProduto() {
        try {
            initGraficoHistoricoPrecoProduto();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BarChartModel getGraficoDeGastosMensaisBarras() {
        return graficoDeBarrasGastosMensais;
    }

    public LineChartModel getGraficoDeGastosMensaisLinhas() {
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

    public LineChartModel getGraficoConsumoEletricidade() {
        return graficoDeLinhasConsumoEletricidade;
    }

    public CartesianChartModel getGraficoReceitasDepesasBarras() {
        return graficoDeBarrasReceitasDepesas;
    }

    public CartesianChartModel getGraficoReceitasDepesasLinhas() {
        return graficoDeLinhasReceitasDepesas;
    }

    public MeterGaugeChartModel getGraficoReceitasDepesasGauge() {
        return graficoGaugeReceitasDepesas;
    }

    public BarChartModel getGraficoDeBarrasVerticaisGastosPorCategoria() {
        return graficoDeBarrasVerticaisGastosPorCategoria;
    }

    public BarChartModel getGraficoDeBarrasHorizontaisGastosPorCategoria() {
        return graficoDeBarrasHorizontaisGastosPorCategoria;
    }
}
