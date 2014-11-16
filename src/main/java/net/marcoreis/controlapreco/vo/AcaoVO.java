package net.marcoreis.controlapreco.vo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AcaoVO {
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private String symbol;
    private String date;

    @JsonProperty("Open")
    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    @JsonProperty("High")
    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    @JsonProperty("Low")
    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    @JsonProperty("Close")
    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("Date")
    public String getDate() {
        return date;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("Symbol")
    public String getSymbol() {
        return symbol;
    }
}
