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

    private final ProcessSignal signalProcessor = new ProcessSignal(Bundles.prop().getProperty("teleinfo.filetouse"),
            "1".equals(Bundles.prop().getProperty("teleinfo.processrecord")));
    private Map<String, String> trameInfos = new HashMap<>();

    public Teleinfo(final IJdbc jdbc) {
        super(jdbc);
    }

    @Override
    public void run() {
        try {
            final List<Trame> trames;
            trames = this.signalProcessor.getTrames(Integer.parseInt(Bundles.prop().getProperty("teleinfo.trametime")));

            if (!trames.isEmpty()) {
                int i = 0;
                boolean inError = true;
                while (inError && i < trames.size()) {
                    final Trame curTrame = trames.get(i);
                    if (!curTrame.isInError()) {
                        curTrame.parseInfos();
                        curTrame.formatInfos();
                        this.trameInfos = curTrame.getFormatedInfos();
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
        return this.trameInfos;
    }

    @Override
    public void save() {
        try {
            final String iInstStr = this.trameInfos.get("IINST");
            final String hcAmountStr = this.trameInfos.get("HCHC");
            final String hpAmountStr = this.trameInfos.get("HCHP");
            if (iInstStr != null && hcAmountStr != null && hpAmountStr != null) {
                final Integer iInst = Integer.parseInt(iInstStr);
                final Integer hcAmount = Integer.parseInt(hcAmountStr);
                final Integer hpAmount = Integer.parseInt(hpAmountStr);
                this.jdbc.saveTeleinfos(iInst, hcAmount, hpAmount);
            }
        } catch (final Exception e) {
            LOGGER.error("Erreur de sauvegarde téléinfo", e);
        }

    }

}
