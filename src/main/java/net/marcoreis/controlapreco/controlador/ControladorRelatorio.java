package net.marcoreis.controlapreco.controlador;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.marcoreis.controlapreco.service.ServicoCompra;
import net.marcoreis.controlapreco.service.ServicoGenerico;

import org.primefaces.component.chart.bar.BarChart;

@ManagedBean
@RequestScoped
public class ControladorRelatorio extends ControladorGenerico {

    private static final long serialVersionUID = -8989131094545262992L;
    private ServicoCompra servico = new ServicoCompra();
    private BarChart graficoDeGastosEmBarras;
    private List mesesDisponiveis;

    @PostConstruct
    public void init() {
        mesesDisponiveis = getServico().findMesesDisponiveis();
    }

    public void criarGraficoDeGastosEmBarras() {
    }

    public BarChart getGraficoDeGastosEmBarras() {
        return graficoDeGastosEmBarras;
    }

    public ServicoGenerico getServico() {
        return servico;
    }

    public List getMesesDisponiveis() {
        return mesesDisponiveis;
    }
}
