package com.manu.domoback.features.api;

import java.util.Map;
import java.util.Set;

public interface IFeatureWrapper {

    /**
     * Ajout d'une fonctionnalité
     *
     * @param feature fonctionnalité à ajouter
     */
    void addFeature(IFeature feature);

    /**
     * Récupération des infos de toutes les fonctionnalités
     *
     * @return Map contenant toutes les infos des différentes fonctionnalités
     */
    Map<String, String> getFeaturesInfos();

    /**
     * Revnoie la collection de features
     *
     * @return
     */
    Set<IFeature> getFeatures();
}
