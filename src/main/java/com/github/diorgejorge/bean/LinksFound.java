package com.github.diorgejorge.bean;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.net.URI;

/**
 * Created by Diorge Jorge on 29/03/2018.
 */
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "linkns_found")
public class LinksFound extends AbstractEntity{
    @Id
    public Object id;
    @Column
    public URI urlOrigem;
    @Column
    public URI urlEncontrada;
    @Column
    public String htmlText;
    @Column
    public String date;

    @Override
    public Object getId() {
        return id;
    }
}
