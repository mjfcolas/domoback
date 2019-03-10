package com.manu.domoback.features.chauffage;

import java.util.Date;
import java.util.Map;

public interface IChauffageInfo {
    /**
     * Mode du chauffage
     *
     * @return
     */
    Boolean getChauffageMode();

    /**
     * Mode hour/constant du chauffage
     *
     * @return
     */
    Boolean getChauffageHourMode();

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
     * Enregistre le mode hour/constant du chauffage
     *
     * @param chauffageHourMode
     */
    void setChauffageHourMode(Boolean chauffageHourMode);

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

    /**
     * Map contenant la programmation de température selon les heures de départ
     *
     * @return
     */
    Map<Date, Integer> getTempByHoursMap();

    /**
     * Set de la pam contenant la programmation des températures selon les heures de départ
     *
     * @param tempByHoursMap
     */
    void setTempByHoursMap(final Map<Date, Integer> tempByHoursMap);

}
