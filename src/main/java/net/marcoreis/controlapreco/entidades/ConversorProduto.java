package net.marcoreis.controlapreco.entidades;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang.StringUtils;

import net.marcoreis.controlapreco.controlador.ServicoProduto;

@FacesConverter(forClass = Produto.class, value = "conversorProduto")
public class ConversorProduto implements Converter {
    private ServicoProduto servico = new ServicoProduto();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (StringUtils.isEmpty(value))
            return null;
        Long id = Long.parseLong(value);
        Object obj = servico.findById(Produto.class, id);
        return obj;
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
