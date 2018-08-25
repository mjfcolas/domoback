package com.manu.domoback.features;

public interface IChauffage extends IFeature {

    /**
     * Modifier le réglage de la temperature
     *
     * @param up: true si augmentation
     */
    void changeTemperature(boolean up);

}
