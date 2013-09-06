package com.buggycoder.domo.db.model;

import com.buggycoder.domo.db.DaoManager;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shirish on 23/7/13.
 */
public abstract class Model {

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static <T> Dao.CreateOrUpdateStatus createOrUpdate(DaoManager daoManager, T o) throws SQLException {
        Dao<T, String> questionDao = daoManager.getDao(o.getClass());
        return questionDao.createOrUpdate(o);
    }

    public static <T> T createIfNotExists(DaoManager daoManager, T o) throws SQLException {
        Dao<T, String> questionDao = daoManager.getDao(o.getClass());
        return questionDao.createIfNotExists(o);
    }

    public static <T, E> T findById(DaoManager daoManager, Class<T> c, E id) throws SQLException {
        Dao<T, E> dao = daoManager.<T, E>getDao(c);
        return dao.queryForId(id);
    }

    public static <T, E> List<T> findAll(DaoManager daoManager, Class<T> c) throws SQLException {
        Dao<T, E> dao = daoManager.<T, E>getDao(c);
        return dao.queryForAll();
    }

    public static <T, E> int update(DaoManager daoManager, T o) throws SQLException {
        Dao<T, E> dao = daoManager.<T, E>getDao(o.getClass());
        return dao.update(o);
    }

    public static <T, E> boolean idExists(DaoManager daoManager, Class<T> c, E o) throws SQLException {
        Dao<T, E> dao = daoManager.<T, E>getDao(c);
        return dao.idExists(o);
    }
}
