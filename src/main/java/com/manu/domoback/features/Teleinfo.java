package com.manu.domoback.features;

import com.manu.domoback.common.Bundles;
import com.manu.domoback.database.IJdbc;
import com.manu.domoback.teleinfo.ProcessSignal;
import com.manu.domoback.teleinfo.Trame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teleinfo extends AbstractFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(Teleinfo.class.getName());

    ProcessSignal signalProcessor = new ProcessSignal(Bundles.prop().getProperty("teleinfo.filetouse"),
            "1".equals(Bundles.prop().getProperty("teleinfo.processrecord")));
    Map<String, String> trameInfos = new HashMap<>();

    public Teleinfo(IJdbc jdbc) {
        super(jdbc);
    }

    @Override
    public void run() {
        try {
            List<Trame> trames;
            trames = signalProcessor.getTrames(Integer.parseInt(Bundles.prop().getProperty("teleinfo.trametime")));

            if (!trames.isEmpty()) {
                int i = 0;
                boolean inError = true;
                while (inError && i < trames.size()) {
                    Trame curTrame = trames.get(i);
                    if (!curTrame.isInError()) {
                        curTrame.parseInfos();
                        curTrame.formatInfos();
                        trameInfos = curTrame.getFormatedInfos();
                        inError = false;
                    }
                    i++;
                }
            }
            this.fireDataChanged();
        } catch (NumberFormatException | UnsupportedAudioFileException | IOException e) {
            LOGGER.error("Erreur de lecture des trames teleinfo", e);
        }

    }

    @Override
    public Map<String, String> getInfos() {
        return trameInfos;
    }

    @Override
    public void save() {
        try {
            String iInstStr = trameInfos.get("IINST");
            String hcAmountStr = trameInfos.get("HCHC");
            String hpAmountStr = trameInfos.get("HCHP");
            if (iInstStr != null && hcAmountStr != null && hpAmountStr != null) {
                Integer iInst = Integer.parseInt(iInstStr);
                Integer hcAmount = Integer.parseInt(hcAmountStr);
                Integer hpAmount = Integer.parseInt(hpAmountStr);
                jdbc.saveTeleinfos(iInst, hcAmount, hpAmount);
            }
        } catch (Exception e) {
            LOGGER.error("Erreur de sauvegarde téléinfo", e);
        }

    }

}
