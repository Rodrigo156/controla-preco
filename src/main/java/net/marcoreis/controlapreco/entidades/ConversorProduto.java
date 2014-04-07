package net.marcoreis.controlapreco.entidades;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.marcoreis.controlapreco.controlador.ServicoProduto;

@FacesConverter(forClass = Produto.class)
public class ConversorProduto implements Converter {
    private ServicoProduto servico = new ServicoProduto();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        try {
            Long id = Long.parseLong(value);
            Object obj = servico.findById(Produto.class, id);
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        try {
            Produto p = (Produto) value;
            return String.valueOf(p.getId());
        } catch (Exception e) {
            return "";
        }
    }

}
