package com.manu.domoback.arduinoreader;

public interface ExternalDataController {

    /**
     * Initialisation du reader
     */
    void initialize();

    /**
     * Vérification de l'état du reader
     *
     * @return true si le reader est pret
     */
    boolean isReady();

    /**
     * Mettre en file d'attente une chaine de caractère à écrire sur la liaison série
     *
     * @param toSend chaine à écrire sur la liaison série
     */
    void writeData(String toSend);

    /**
     * Récupération des informations enregistrées par l'arduino
     *
     * @return informations enregistrées par l'arduino
     */
    ArduinoInfos getInfos();

}
