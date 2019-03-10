package com.manu.domoback.database.factory;

import com.manu.domoback.database.impl.Jdbc;
import com.manu.domoback.persistence.api.PersistenceApi;

public class JdbcFactory {
    private static PersistenceApi jdbc;

    private JdbcFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static PersistenceApi getInstance() {
        if (jdbc == null) {
            jdbc = new Jdbc();
        }
        return jdbc;
    }
}
