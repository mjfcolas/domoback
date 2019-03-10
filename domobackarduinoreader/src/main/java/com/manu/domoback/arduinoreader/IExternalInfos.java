package com.manu.domoback.arduinoreader;

public interface IExternalInfos {

    /**
     * Retourne la température
     *
     * @return température
     */
    Float getTemperature();

    /**
     * Retourne la température de la chambre
     *
     * @return température
     */
    Float getTemperature2();

    /**
     * Retourne la température extérieure
     *
     * @return température
     */
    Float getTemperature3();

    /**
     * Retourne la pression absolue
     *
     * @return pression absolue
     */
    Float getPressionAbsolue();

    /**
     * Retourne la pression relative
     *
     * @return pression relative
     */
    Float getPressionRelative();

    /**
     * Retourne l'hygrométrie
     *
     * @return hygrométrie
     */
    Float getHygrometrie();

    /**
     * Retourne la clé des informations envoyée par l'arduino
     *
     * @return clé
     */
    String getKey();

    /**
     * Retourne l'état actuel du chauffage
     */
    Boolean getChauffageState();
}
