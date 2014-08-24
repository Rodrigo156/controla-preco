package net.marcoreis.controlapreco.controlador;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.marcoreis.controlapreco.service.ServicoCompra;
import net.marcoreis.controlapreco.service.ServicoGenerico;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean
@RequestScoped
public class ControladorRelatorio extends ControladorGenerico {

    private static final long serialVersionUID = -8989131094545262992L;
    private ServicoCompra servico = new ServicoCompra();
    private BarChartModel graficoDeGastosPorMes;
    private List mesesDisponiveis;

    @PostConstruct
    public void init() {
        initGraficoBarras();
    }

    private void initGraficoBarras() {
        // Recuperar os dados
        mesesDisponiveis = getServico().findMesesDisponiveis();
        Map<Object, Number> mapaDeGastos = getServico().recuperarMapaDeGastosPorMes();
        //
        graficoDeGastosPorMes = new BarChartModel();
        ChartSeries serie = new ChartSeries();
        serie.setLabel("Mês");
        for (Object mes : mesesDisponiveis) {
            serie.set(mes, 1);
        }
        graficoDeGastosPorMes.addSeries(serie);
    }

    public BarChartModel getGraficoDeGastosPorMes() {
        return graficoDeGastosPorMes;
    }

    public ServicoGenerico getServico() {
        return servico;
    }

    public List getMesesDisponiveis() {
        return mesesDisponiveis;
    }
}
