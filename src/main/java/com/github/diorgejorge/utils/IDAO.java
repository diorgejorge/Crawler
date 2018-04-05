package com.github.diorgejorge.utils;

import com.github.diorgejorge.bean.AbstractEntity;
import com.github.diorgejorge.exception.DAOException;
import com.github.diorgejorge.pojo.NamedParams;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Diorge Jorge on 05/04/2018.
 */
public interface IDAO {
    void save(HashMap<String, Object> map)throws DAOException;
    void delete(AbstractEntity abstractEntity) throws DAOException;
    AbstractEntity find(AbstractEntity entidade) throws DAOException;
    <T extends AbstractEntity> List<T> findListByModel(AbstractEntity example, Integer maxResults, NamedParams namedParamsRange, String columnOrderBy) throws DAOException ;
}
