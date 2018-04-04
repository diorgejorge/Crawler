package br.com.badarane.bean;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.net.URI;

/**
 * Created by Diorge Jorge on 29/03/2018.
 */
@Data
@ToString
@EqualsAndHashCode
@Builder
public class LinksFound {
    public URI urlOrigem;
    public URI urlEncontrada;
    public String htmlText;
    public String date;
}
