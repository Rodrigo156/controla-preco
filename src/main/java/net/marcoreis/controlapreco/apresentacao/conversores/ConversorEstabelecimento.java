package net.marcoreis.controlapreco.apresentacao.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.marcoreis.controlapreco.entidades.Estabelecimento;
import net.marcoreis.controlapreco.service.ServicoEstabelecimento;

@FacesConverter(forClass = Estabelecimento.class)
public class ConversorEstabelecimento implements Converter {
    private ServicoEstabelecimento servico = new ServicoEstabelecimento();

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        Long id = Long.parseLong(value);
        return servico.findById(Estabelecimento.class, id);
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        try {
            Estabelecimento obj = (Estabelecimento) value;
            return String.valueOf(obj.getId());
        } catch (Exception e) {
            return "";
        }
    }

}
