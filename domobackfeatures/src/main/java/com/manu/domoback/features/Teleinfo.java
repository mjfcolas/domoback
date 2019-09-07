package com.manu.domoback.features;

import com.manu.domoback.conf.CONFKEYS;
import com.manu.domoback.conf.DomobackConf;
import com.manu.domoback.features.api.enums.INFOS;
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

    private final ProcessSignal signalProcessor = new ProcessSignal(DomobackConf.get(CONFKEYS.TELEINFO_FILETOUSE),
            "1".equals(DomobackConf.get(CONFKEYS.TELEINFO_PROCESSRECORD)));
    private Map<String, String> trameInfos = new HashMap<>();

    public Teleinfo() {
        super();
    }

    @Override
    public void run() {
        try {
            final List<Trame> trames;
            trames = this.signalProcessor.getTrames(Integer.parseInt(DomobackConf.get(CONFKEYS.TELEINFO_TRAMETIME)));

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
    public boolean save() {
        try {
            final String iInstStr = this.trameInfos.get(INFOS.IINST.name());
            final String hcAmountStr = this.trameInfos.get(INFOS.HCHC.name());
            final String hpAmountStr = this.trameInfos.get(INFOS.HCHP.name());
            if (iInstStr != null && hcAmountStr != null && hpAmountStr != null) {
                final Integer iInst = Integer.parseInt(iInstStr);
                final Integer hcAmount = Integer.parseInt(hcAmountStr);
                final Integer hpAmount = Integer.parseInt(hpAmountStr);
                this.jdbc.saveTeleinfos(iInst, hcAmount, hpAmount);
                return true;
            }
        } catch (final Exception e) {
            LOGGER.error("Erreur de sauvegarde téléinfo", e);
        }
        return false;
    }

}
