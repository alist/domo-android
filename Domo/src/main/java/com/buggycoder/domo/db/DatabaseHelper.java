package com.buggycoder.domo.db;

/**
 * Created by shirish on 17/6/13.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "domo.db";
    private static final int DATABASE_VERSION = 1;

    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    // we do this so there is only one helper
    private static DatabaseHelper helper = null;

    private static DaoManager daoManager = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        daoManager = new DaoManager(this);
    }

    /**
     * Get the helper, possibly constructing it if necessary. For each call to this method, there should be 1 and only 1
     * call to {@link #close()}.
     */
    public static synchronized DatabaseHelper init(Context context) {
        if (helper == null) {
            helper = new DatabaseHelper(context);
        }
        usageCounter.incrementAndGet();
        return helper;
    }

    public static DaoManager getDaoManager() {
        return daoManager;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        // TODO handle migration
        daoManager.createTables(db, connectionSource);
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // TODO handle migration
        daoManager.updateTables(db, connectionSource, oldVersion, newVersion);
    }

    /**
     * Close the database connections and clear any cached DAOs. For each call to {@link #init(android.content.Context)}, there
     * should be 1 and only 1 call to this method. If there were 3 calls to {@link #init(android.content.Context)} then on the 3rd
     * call to this method, the helper and the underlying database connections will be closed.
     */
    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            daoManager.clearAll();
        }
    }
}
