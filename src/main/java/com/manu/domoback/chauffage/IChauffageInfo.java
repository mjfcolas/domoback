package com.manu.domoback.chauffage;

public interface IChauffageInfo {
    /**
     * Mode du chauffage
     *
     * @return
     */
    Boolean getChauffageMode();

    /**
     * Température demandée au chauffage
     *
     * @return
     */
    Integer getChauffageTemp();

    /**
     * true si l'état a changé depuis le dernier appel
     *
     * @return
     */
    Boolean hasChangedMode();

    /**
     * Enregistre le mode du chauffage
     *
     * @param chauffageMode
     */
    void setChauffageMode(Boolean chauffageMode);

    /**
     * Enregistre la température du chauffage
     *
     * @param chauffageTemp: température à régler
     */
    void setChauffageTemp(Integer chauffageTemp);

    /**
     * Récupération de l'état du chauffage
     *
     * @return
     */
    Boolean getChauffageState();

    /**
     * Sauvegarde de l'état du chauffage
     *
     * @param chauffageState
     */
    void setChauffageState(Boolean chauffageState);

    /**
     * True si on considère l'état du chauffage comme connu
     *
     * @return
     */
    Boolean getChauffageStateKnown();

    /**
     * set de l'état de connaissance de l'état du chauffage
     *
     * @param chauffageStateKnown
     */
    void setChauffageStateKnown(Boolean chauffageStateKnown);

}
