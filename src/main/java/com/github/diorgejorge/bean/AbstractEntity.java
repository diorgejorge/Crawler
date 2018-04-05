package com.github.diorgejorge.bean;

/**
 * Created by Diorge Jorge on 21/03/2018.
 */

import org.apache.log4j.Logger;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Base class for ORM (Object-Relational Mapping). All classes that have {@code @Entity} should {@code extends}
 * this class.
 * @author Ronaldo Lanhellas
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final transient Logger logger = Logger.getLogger(AbstractEntity.class);


    @Transient
    private boolean novo;

    @Transient
    private boolean selected;

    public abstract Object getId();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AbstractEntity) && (this.getId() != null) ? getId().equals(
                ((AbstractEntity) obj).getId()) : (obj == this);
    }

    public AbstractEntity clone() {
        try {
            return (AbstractEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "AbstractEntity [getId()=" + getId() + "]";
    }

    public boolean isNovo() {
        return novo;
    }

    public void setNovo(boolean novo) {
        this.novo = novo;
    }

}
