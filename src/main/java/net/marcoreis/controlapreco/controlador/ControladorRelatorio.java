package net.marcoreis.controlapreco.controlador;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.marcoreis.controlapreco.service.ServicoRelatorio;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@RequestScoped
public class ControladorRelatorio extends ControladorGenerico {

    private static final long serialVersionUID = -8989131094545262992L;
    private ServicoRelatorio servico = new ServicoRelatorio();
    private BarChartModel graficoDeGastosPorMes;
    private PieChartModel graficoDeGastosPorCategoria;
    private Date mesReferencia;
    private List<?> mesesDisponiveis;

    // @PostConstruct
    public void init() {
        initGraficoBarras();
        initGraficoCategorias();
    }

    private void initGraficoCategorias() {
        graficoDeGastosPorCategoria = new PieChartModel();
        List<Object> gastos = getServico().consultarGastosPorCategoria(
                getMesReferencia());
        for (Object linha : gastos) {
            Object[] dados = (Object[]) linha;
            String chave = dados[0].toString();
            Number valor = (Number) dados[1];
            graficoDeGastosPorCategoria.set(chave, valor);
        }
        graficoDeGastosPorCategoria.setTitle("Gastos por categoria");
        graficoDeGastosPorCategoria.setShowDataLabels(true);
        graficoDeGastosPorCategoria.setLegendPosition("e");
    }

    private void initGraficoBarras() {
        // 1
        graficoDeGastosPorMes = new BarChartModel();
        ChartSeries serie = new ChartSeries();
        // 2
        Axis xaxis = graficoDeGastosPorMes.getAxis(AxisType.X);
        xaxis.setLabel("Mês");
        Axis yaxis = graficoDeGastosPorMes.getAxis(AxisType.Y);
        yaxis.setLabel("Gastos");
        // 3
        List<Object> gastos = getServico().consultarGastosPorMes();
        // 4
        for (Object linha : gastos) {
            Object[] dados = (Object[]) linha;
            Object chave = dados[0];
            Number valor = (Number) dados[1];
            serie.set(chave, valor);
        }
        // 5
        graficoDeGastosPorMes.addSeries(serie);
        graficoDeGastosPorMes.setTitle("Gastos por mês");
        graficoDeGastosPorMes.setShowDatatip(false);
    }

    public BarChartModel getGraficoDeGastosPorMes() {
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
        LineChartModel lcModel = new LineChartModel();
        lcModel.setTitle("Consumo de eletricidade (kWh)");
        lcModel.addSeries(serie);
        //
        lcModel.getAxis(AxisType.Y).setMin(0);
        //
        DateAxis eixoData = new DateAxis();
        eixoData.setTickFormat("%m/%Y");
        eixoData.setTickCount(15);
        eixoData.setMin("2013-06-01");
        eixoData.setMax("2014-08-01");
        eixoData.setTickAngle(-60);
        lcModel.getAxes().put(AxisType.X, eixoData);
        //
        return lcModel;
    }
}
