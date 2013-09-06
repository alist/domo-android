package com.buggycoder.domo.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shirish on 23/7/13.
 */
public class DaoManager {

    private static final int MAX_CLASSES = 10;
    Map<String, Dao> daoCache = null;
    LinkedHashMap<String, Class> classCache = null;
    OrmLiteSqliteOpenHelper ormHelper = null;

    public DaoManager(OrmLiteSqliteOpenHelper h) {
        this.ormHelper = h;
        this.daoCache = new HashMap<String, Dao>(MAX_CLASSES);
        this.classCache = new LinkedHashMap<String, Class>(MAX_CLASSES);
    }

    public DaoManager registerDaoClass(Class c) {
        classCache.put(c.getSimpleName(), c);
        return this;
    }

    public DaoManager unregisterDaoClass(Class c) {
        classCache.remove(c.getSimpleName());
        return this;
    }

    public void createTables(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            for (Class c : classCache.values()) {
                TableUtils.createTable(connectionSource, c);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public void updateTables(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            // TODO: Somehow store list of old tables (to drop them)
            for (Class c : classCache.values()) {
                TableUtils.dropTable(connectionSource, c, true);
            }
            // after we drop the old databases, we create the new ones
            createTables(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public <T, E> Dao<T, E> getDao(Class c) throws SQLException {
        Dao cachedDao = daoCache.get(c.getSimpleName());
        if (cachedDao == null) {
            cachedDao = ormHelper.getDao(c);
            daoCache.put(c.getSimpleName(), cachedDao);
        }
        return cachedDao;
    }

    public void clearAll() {
        daoCache.clear();
        classCache.clear();
    }


}