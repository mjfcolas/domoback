package com.manu.domoback.features;

public interface IChauffage extends IFeature {

    /**
     * Modifier le réglage de la temperature
     *
     * @param up: true si augmentation
     */
    void changeTemperature(boolean up);

    /**
     * Modifier le réglage de la température sur une plage d'heure
     *
     * @param up        : true si augmentation
     * @param startHour : début de la plage sur laquelle changer la température
     */
    void changeTemperatureHour(final boolean up, Integer startHour);

    /**
     * Switch entre mode horaire et mode constant
     */
    void changeMode();

}
