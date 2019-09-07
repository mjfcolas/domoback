package com.manu.domoback.persistence.api;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public interface PersistenceApi {

    /**
     * Return the identifier of the implementation
     * @return
     */
    String identify();
    /**
     * Sauvegarde en BDD des informations météorologiques
     *
     * @param temperature température
     * @param pressionRel Pression relative
     * @param pressionAbs Pression absolue
     * @param hygro       Hygrométrie
     * @param type        capteur concerné
     * @throws SQLException
     */
    void saveMeteoInfos(Float temperature, Float pressionRel, Float pressionAbs, Float hygro, Integer type) throws SQLException;

    /**
     * Sauvegarde en BDD des informations EDF
     *
     * @param iInst    intensité
     * @param hcAmount index heures creuses
     * @param hpAmount index heures pleines
     * @throws SQLException
     */
    void saveTeleinfos(Integer iInst, Integer hcAmount, Integer hpAmount) throws SQLException;

    /**
     * Inverse l'état de la commande chauffage
     *
     * @return la nouvelle commande, ou null si erreur
     * @throws SQLException
     */
    Boolean switchCommandeChauffage() throws SQLException;

    /**
     * Changement du mode hour/constant du chauffage
     *
     * @return la nouvelle commande, ou null si erreur
     * @throws SQLException
     */
    Boolean switchHourModeChauffage() throws SQLException;

    /**
     * Récupère l'état du chauffage
     *
     * @return
     * @throws SQLException
     */
    Boolean getCommandeChauffage() throws SQLException;

    /**
     * Récupère le mode hour/constant du chauffage
     *
     * @return
     * @throws SQLException
     */
    Boolean getHourModeChauffage() throws SQLException;

    /**
     * Sauvegarde la température demandée
     *
     * @param temp
     * @throws SQLException
     */
    void setCurrentTemp(int temp) throws SQLException;

    /**
     * Set la température programmée pour une heure donnée
     *
     * @param temp
     * @param startTime
     */
    void setTemp(final int temp, final Date startTime);

    /**
     * Récupération de la température générale
     *
     * @param hourMode : get the temperature for the current hour if true, global temp else
     * @return La température enregistrée
     * @throws SQLException
     */
    Integer getCurrentTemp(final boolean hourMode) throws SQLException;

    /**
     * Récupération de la température d'un créneau défini par son heure de départ
     *
     * @param startHour
     * @return
     * @throws SQLException
     */
    Integer getTempForStartHour(final Date startHour) throws SQLException;

    /**
     * Récupération de la température pour une heure donnée
     *
     * @param date
     * @return
     * @throws SQLException
     */
    Integer getTemp(Date date) throws SQLException;

    /**
     * Récupération de toutes les températures programmées sur des horaires
     *
     * @return
     * @throws SQLException
     */
    Map<Date, Integer> getTempMap() throws SQLException;

    /**
     * Insert a date into the error table
     *
     * @param date
     * @throws SQLException
     */
    void saveSerialEvent(final LocalDateTime date, String errorType, boolean isError) throws SQLException;
}
