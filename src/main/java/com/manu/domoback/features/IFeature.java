package com.manu.domoback.features;

import com.manu.domoback.listeners.DataListener;

import java.util.Map;

public interface IFeature {
    /**
     * Déroulement de la fonctionnalité
     */
    void run();

    /**
     * Récupération des informations gérées par la fonctionnalité
     *
     * @return
     */
    Map<String, String> getInfos();

    /**
     * Sauvegarde des informations gérées par la fonctionnalité
     *
     * @return true if save succeeded
     */
    boolean save();

    /**
     * Renvoie le nom de la feature
     *
     * @return
     */
    String getName();

    /**
     * Subscribe to data changes
     *
     * @param listener
     */
    void subscribe(DataListener listener);

    /**
     * Unsubscribe to data changes
     *
     * @param listener
     */
    void unsubscribe(DataListener listener);
}
