package com.github.diorgejorge.utils;

import com.github.diorgejorge.bean.AbstractEntity;
import com.github.diorgejorge.exception.DAOException;
import com.github.diorgejorge.pojo.NamedParams;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.service.Firebase;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Diorge Jorge on 05/04/2018.
 */
public class DAOFirebase implements IDAO {

    private Firebase firebase;

    public DAOFirebase(String dataBaseUrl){
        try {
            firebase = new Firebase(dataBaseUrl);
        } catch (FirebaseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void save(HashMap<String, Object> map) {
        try {
            firebase.put(map);
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(AbstractEntity abstractEntity) {
    }

    @Override
    public AbstractEntity find(AbstractEntity entidade) throws DAOException {
        return null;
    }

    @Override
    public <T extends AbstractEntity> List<T> findListByModel(AbstractEntity example, Integer maxResults, NamedParams namedParamsRange, String columnOrderBy) throws DAOException {
        return null;
    }
}
