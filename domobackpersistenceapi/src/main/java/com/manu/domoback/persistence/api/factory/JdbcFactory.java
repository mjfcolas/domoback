package com.manu.domoback.persistence.api.factory;

import com.manu.domoback.persistence.api.PersistenceApi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class JdbcFactory {
    private static PersistenceApi jdbc;

    private JdbcFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static PersistenceApi getInstance() {
        if (jdbc == null) {
            ServiceLoader<PersistenceApi> persistenceApiLoader = ServiceLoader.load(PersistenceApi.class);
            Iterator<PersistenceApi> persistenceApiIterator = persistenceApiLoader.iterator();
            while (persistenceApiIterator.hasNext()) {
                jdbc = persistenceApiIterator.next();
                break;
            }
        }
        return jdbc;
    }
}
