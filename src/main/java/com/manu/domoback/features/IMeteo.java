package com.manu.domoback.features;

import com.manu.domoback.arduinoreader.IExternalInfos;

public interface IMeteo extends IFeature {

    /**
     * Renvoie les informations de météo non formatées
     *
     * @return
     */
    IExternalInfos getRawInfos();

}
