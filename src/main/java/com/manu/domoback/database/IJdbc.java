package com.manu.domoback.database;

import java.sql.SQLException;

public interface IJdbc {

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
     * Récupère l'état du chauffage
     *
     * @return
     * @throws SQLException
     */
    Boolean getCommandeChauffage() throws SQLException;

    /**
     * Sauvegarde la température demandée
     *
     * @param temp
     * @throws SQLException
     */
    void setCurrentTemp(int temp) throws SQLException;

    /**
     * Récupération de la dernière température enregistrée
     *
     * @return La température enregistrée
     * @throws SQLException
     */
    Integer getCurrentTemp() throws SQLException;
}
