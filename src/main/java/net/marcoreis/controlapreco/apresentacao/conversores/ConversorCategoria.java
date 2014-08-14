package net.marcoreis.controlapreco.apresentacao.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.marcoreis.controlapreco.entidades.Categoria;
import net.marcoreis.controlapreco.service.ServicoCategoria;

@FacesConverter(forClass = Categoria.class)
public class ConversorCategoria implements Converter {
    private ServicoCategoria servico = new ServicoCategoria();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        try {
            Long id = Long.parseLong(value);
            Object obj = servico.findById(Categoria.class, id);
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        try {
            Categoria c = (Categoria) value;
            return String.valueOf(c.getId());
        } catch (Exception e) {
            return "";
        }
    }

}
