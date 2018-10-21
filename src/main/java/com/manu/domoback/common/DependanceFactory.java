package com.manu.domoback.common;

import com.manu.domoback.database.IJdbc;
import com.manu.domoback.database.Jdbc;

public class DependanceFactory {

    private static IJdbc jdbc;

    private DependanceFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static IJdbc getJdbc() {
        if (jdbc == null) {
            jdbc = new Jdbc();
        }
        return jdbc;
    }

    //public static

}
