package net.marcoreis.controlapreco.apresentacao.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.marcoreis.controlapreco.entidades.Produto;
import net.marcoreis.controlapreco.service.ServicoProduto;

@FacesConverter(forClass = Produto.class, value = "conversorProduto")
public class ConversorProduto implements Converter {
    private ServicoProduto servico = new ServicoProduto();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        try {
            Long id = Long.parseLong(value);
            Object obj = servico.findById(Produto.class, id);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        try {
            Produto p = (Produto) value;
            return String.valueOf(p.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
