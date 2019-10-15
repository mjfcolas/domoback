package com.manu.domoback.cliinterface.display;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.RGB;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.manu.domoback.common.NumberUtils;
import com.manu.domoback.common.StringUtils;
import com.manu.domoback.cliinterface.enums.FLOATMODE;
import com.manu.domoback.features.api.features.IChauffage;
import com.manu.domoback.features.api.features.IFeature;
import com.manu.domoback.features.api.IFeatureWrapper;
import com.manu.domoback.features.api.enums.INFOS;
import com.manu.domoback.cliinterface.enums.MODE;
import com.manu.domoback.cliinterface.enums.UNITS;
import com.manu.domoback.features.api.listeners.DataListener;
import com.manu.domoback.ui.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WindowCliInterface implements DataListener, WindowListener, UserInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowCliInterface.class.getName());
    private static final Properties MESSAGE_BUNDLE = new Properties();

    private static final SimpleTheme theme;

    private final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    private Screen screen = null;
    private MODE mode = MODE.TELEINFO;
    private IFeatureWrapper featureWrapper = null;
    private WindowBasedTextGUI textGUI = null;
    private Window window = null;
    private Panel contentPanel = null;
    protected IChauffage chauffage = null;
    private boolean displayed = false;

    private static final int COLUMN_SIZE = 10;
    private static final double TMAX = 40;

    private final EnumMap<MODE, Label> mainTitleLabels = new EnumMap<>(MODE.class);
    private final EnumMap<INFOS, Label> titleLabels = new EnumMap<>(INFOS.class);
    private final EnumMap<INFOS, Label> valueLabels = new EnumMap<>(INFOS.class);
    private final Map<String, Label> tempIndicLabels = new HashMap<>();
    private final Map<String, Label> intIndicLabels = new HashMap<>();
    private final Map<String, Label> hoursIndicLabel = new HashMap<>();
    private final Map<String, Label> hoursValueLabel = new HashMap<>();
    private final List<TabableLabel> tabableLabels = new ArrayList<>();
    private Integer selectedTabableLabelIndex = 0;

    protected Map<String, String> infos = new HashMap<>();

    static {
        theme = new SimpleTheme(TextColor.ANSI.BLACK, TextColor.ANSI.BLACK);
        theme.setWindowDecorationRenderer(new EmptyWindowDecorationRenderer());
        try {
            InputStream stream = WindowCliInterface.class.getResourceAsStream("/messages.properties");
            MESSAGE_BUNDLE.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("Fichier de config non trouvé");
            System.exit(-1);
        }
    }

    public WindowCliInterface(final IFeatureWrapper featureWrapper, final IChauffage chauffage) {
        try {
            this.featureWrapper = featureWrapper;
            this.chauffage = chauffage;

            for (final IFeature feature : this.featureWrapper.getFeatures()) {
                feature.subscribe(this);
            }

            this.initialiseLabels();

            this.screen = this.defaultTerminalFactory.createScreen();
            this.screen.startScreen();
            this.screen.clear();
            this.textGUI = new MultiWindowTextGUI(this.screen, TextColor.ANSI.BLACK);
            this.window = new BasicWindow();
            this.window.setTheme(theme);
            this.window.addWindowListener(this);

        } catch (final IOException e) {
            LOGGER.error("An error occured", e);
            System.exit(-1);
            System.exit(-1);
        }
    }

    private void initialiseLabels() {

        final TextColor pureWhite = new RGB(255, 255, 255);
        final Label titleTeleinfo = this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.panel.title"), FLOATMODE.RIGHT, 2, 1);
        titleTeleinfo.setForegroundColor(pureWhite);
        final Label titleMeteo = this.createLabel(MESSAGE_BUNDLE.getProperty("gui.meteo.panel.title"), FLOATMODE.RIGHT, 2, 1);
        titleMeteo.setForegroundColor(pureWhite);
        final Label titleChauff = this.createLabel(MESSAGE_BUNDLE.getProperty("gui.chauff.panel.title"), FLOATMODE.RIGHT, 2, 1);
        titleChauff.setForegroundColor(pureWhite);

        this.mainTitleLabels.put(MODE.TELEINFO, titleTeleinfo);
        this.mainTitleLabels.put(MODE.METEO, titleMeteo);
        this.mainTitleLabels.put(MODE.CHAUFFAGE, titleChauff);

        this.titleLabels.put(INFOS.ADCO, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.address"), FLOATMODE.RIGHT, 2, 2));
        this.titleLabels.put(INFOS.OPTARIF, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.optarif"), FLOATMODE.RIGHT, 2, 3));
        this.titleLabels.put(INFOS.ISOUSC, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.isousc"), FLOATMODE.RIGHT, 2, 4));
        this.titleLabels.put(INFOS.HHPHC, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.trhor"), FLOATMODE.RIGHT, 6, 2));
        this.titleLabels.put(INFOS.HCHC, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.hc"), FLOATMODE.RIGHT, 6, 3));
        this.titleLabels.put(INFOS.HCHP, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.hp"), FLOATMODE.RIGHT, 6, 4));
        this.titleLabels.put(INFOS.IINST, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.teleinfo.label.iinst"), FLOATMODE.RIGHT, 2, 6));

        this.valueLabels.put(INFOS.ADCO, this.createLabel("", FLOATMODE.LEFT, 3, 2));
        this.valueLabels.put(INFOS.OPTARIF, this.createLabel("", FLOATMODE.LEFT, 3, 3));
        this.valueLabels.put(INFOS.ISOUSC, this.createLabel("", FLOATMODE.LEFT, 3, 4));
        this.valueLabels.put(INFOS.HHPHC, this.createLabel("", FLOATMODE.LEFT, 7, 2));
        this.valueLabels.put(INFOS.HCHC, this.createLabel("", FLOATMODE.LEFT, 7, 3));
        this.valueLabels.put(INFOS.HCHP, this.createLabel("", FLOATMODE.LEFT, 7, 4));
        this.valueLabels.put(INFOS.IINST, this.createLabel("", FLOATMODE.LEFT, 3, 6));

        this.titleLabels.put(INFOS.TEMP, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.meteo.label.tint"), FLOATMODE.RIGHT, 2, 2));
        this.titleLabels.put(INFOS.TEMP2, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.meteo.label.tcha"), FLOATMODE.RIGHT, 2, 3));
        this.titleLabels.put(INFOS.ABSPRE, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.meteo.label.pabs"), FLOATMODE.RIGHT, 2, 4));
        this.titleLabels.put(INFOS.RELPRE, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.meteo.label.prel"), FLOATMODE.RIGHT, 2, 5));
        this.titleLabels.put(INFOS.HYGROHUM, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.meteo.label.hum"), FLOATMODE.RIGHT, 2, 6));

        this.valueLabels.put(INFOS.TEMP, this.createLabel("", FLOATMODE.LEFT, 3, 2));
        this.valueLabels.put(INFOS.TEMP2, this.createLabel("", FLOATMODE.LEFT, 3, 3));
        this.valueLabels.put(INFOS.ABSPRE, this.createLabel("", FLOATMODE.LEFT, 3, 4));
        this.valueLabels.put(INFOS.RELPRE, this.createLabel("", FLOATMODE.LEFT, 3, 5));
        this.valueLabels.put(INFOS.HYGROHUM, this.createLabel("", FLOATMODE.LEFT, 3, 6));

        this.titleLabels.put(INFOS.TEMPHOURMODE, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.chauff.label.hourmode"), FLOATMODE.RIGHT, 2, 2));
        this.titleLabels.put(INFOS.MODECHAUFF, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.chauff.label.state"), FLOATMODE.RIGHT, 2, 3));
        this.titleLabels.put(INFOS.TEMPCHAUFF, this.createLabel(MESSAGE_BUNDLE.getProperty("gui.chauff.label.temp"), FLOATMODE.RIGHT, 2, 4));

        this.valueLabels.put(INFOS.TEMPHOURMODE, this.createLabel("", FLOATMODE.LEFT, 3, 2));
        this.valueLabels.put(INFOS.MODECHAUFF, this.createLabel("", FLOATMODE.LEFT, 3, 3));
        this.valueLabels.put(INFOS.TEMPCHAUFF, this.createTabableLabel("GENERAL", "", FLOATMODE.LEFT, 3, 4));
        this.tabableLabels.add((TabableLabel) this.valueLabels.get(INFOS.TEMPCHAUFF));

        this.createTempIndicator();
        this.createIntensityIndicator();
        this.createHourIndicator();

    }

    private void createTempIndicator() {

        this.tempIndicLabels.put("0", this.createLabel("0 °C", FLOATMODE.LEFT, 1, 9));
        this.tempIndicLabels.put("10", this.createLabel("10 °C", FLOATMODE.LEFT, 2, 9));
        this.tempIndicLabels.put("20", this.createLabel("20 °C", FLOATMODE.LEFT, 3, 9));
        this.tempIndicLabels.put("30", this.createLabel("30 °C", FLOATMODE.LEFT, 4, 9));
        this.tempIndicLabels.put("40", this.createLabel("40 °C", FLOATMODE.LEFT, 5, 9));
        this.tempIndicLabels.putAll(this.createBarre(1, 7, 4));
    }

    private void createIntensityIndicator() {

        this.intIndicLabels.put("0", this.createLabel("| 0 %", FLOATMODE.LEFT, 1, 9));
        this.intIndicLabels.put("20", this.createLabel("| 20 %", FLOATMODE.LEFT, 2, 9));
        this.intIndicLabels.put("40", this.createLabel("| 40 %", FLOATMODE.LEFT, 3, 9));
        this.intIndicLabels.put("60", this.createLabel("| 60 %", FLOATMODE.LEFT, 4, 9));
        this.intIndicLabels.put("80", this.createLabel("| 80 %", FLOATMODE.LEFT, 5, 9));
        this.intIndicLabels.put("100", this.createLabel("| 100 %", FLOATMODE.LEFT, 6, 9));
        this.intIndicLabels.putAll(this.createBarre(1, 7, 5));
    }

    private void createHourIndicator() {

        for (Integer i = 0; i < 12; i++) {
            final String key = INFOS.TEMPCHAUFFTIME.name() + String.format("%02d", i);
            this.hoursIndicLabel.put(key, this.createLabel(this.formatAndAddUnit(i.toString(), UNITS.HOURS), FLOATMODE.LEFT, i, 9, 6));
            this.hoursValueLabel.put(key, this.createTabableLabel(i.toString(), "", FLOATMODE.LEFT, i, 7, 6));
            this.tabableLabels.add((TabableLabel) this.hoursValueLabel.get(key));
        }
        for (Integer i = 12; i < 24; i++) {
            final String key = INFOS.TEMPCHAUFFTIME.name() + String.format("%02d", i);
            this.hoursIndicLabel.put(INFOS.TEMPCHAUFFTIME.name() + String.format("%02d", i), this.createLabel(this.formatAndAddUnit(i.toString(), UNITS.HOURS), FLOATMODE.LEFT, i - 12, 13, 6));
            this.hoursValueLabel.put(INFOS.TEMPCHAUFFTIME.name() + String.format("%02d", i), this.createTabableLabel(i.toString(), "", FLOATMODE.LEFT, i - 12, 11, 6));
            this.tabableLabels.add((TabableLabel) this.hoursValueLabel.get(key));
        }

    }

    private Map<String, Label> createBarre(final int posX, final int posY, final int length) {

        final Map<String, Label> result = new HashMap<>();

        Double dividedLength = (double) length * COLUMN_SIZE / 3;
        final Label label = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label.setPreferredSize(new TerminalSize(dividedLength.intValue(), 1));
        label.setBackgroundColor(TextColor.ANSI.BLUE);
        label.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        int computedPos = posX * COLUMN_SIZE;
        label.setPosition(new TerminalPosition(computedPos, posY));
        label.setSize(new TerminalSize(dividedLength.intValue(), 1));
        result.put("1", label);

        final Label label2 = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label2.setPreferredSize(new TerminalSize(dividedLength.intValue(), 1));
        label2.setBackgroundColor(TextColor.ANSI.YELLOW);
        label2.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        computedPos = posX * COLUMN_SIZE + dividedLength.intValue();
        label2.setPosition(new TerminalPosition(computedPos, posY));
        label2.setSize(new TerminalSize(dividedLength.intValue(), 1));
        result.put("2", label2);

        computedPos = posX * COLUMN_SIZE + 2 * dividedLength.intValue();
        dividedLength = length * COLUMN_SIZE - 2 * dividedLength;
        final Label label3 = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label3.setPreferredSize(new TerminalSize(dividedLength.intValue(), 1));
        label3.setBackgroundColor(TextColor.ANSI.RED);
        label3.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        label3.setPosition(new TerminalPosition(computedPos, posY));
        label3.setSize(new TerminalSize(dividedLength.intValue(), 1));
        result.put("3", label3);

        return result;
    }

    private void fillBarre(final Map<String, Label> barre, final double value, final double max) {
        final double ratio1 = Math.min((value / max) * 3, 1);
        final double ratio2 = Math.max(0, Math.min((value / max) * 3 - 1, 1));
        final double ratio3 = Math.max(0, Math.min((value / max) * 3 - 2, 1));

        this.fillOneBarreLabel(barre.get("1"), ratio1);
        this.fillOneBarreLabel(barre.get("2"), ratio2);
        this.fillOneBarreLabel(barre.get("3"), ratio3);
    }

    private void fillOneBarreLabel(final Label label, final double ratio) {
        final int spaceNumber = (int) (label.getPreferredSize().getColumns() * ratio);
        label.setText(StringUtils.repeat(' ', spaceNumber));
    }

    private Label createLabel(final String labelTxt, final FLOATMODE horiz, final int posX, final int posY, final int columnSize) {
        final Label label = new Label(labelTxt);
        this.prepareLabel(label, labelTxt, horiz, posX, posY, columnSize);
        return label;
    }

    private TabableLabel createTabableLabel(final String id, final String labelTxt, final FLOATMODE horiz, final int posX, final int posY, final int columnSize) {
        final TabableLabel label = new TabableLabel(labelTxt, id);
        this.prepareLabel(label, labelTxt, horiz, posX, posY, columnSize);
        return label;
    }

    private TabableLabel createTabableLabel(final String id, final String labelTxt, final FLOATMODE horiz, final int posX, final int posY) {
        return this.createTabableLabel(id, labelTxt, horiz, posX, posY, COLUMN_SIZE);
    }

    private Label createLabel(final String labelTxt, final FLOATMODE horiz, final int posX, final int posY) {
        return this.createLabel(labelTxt, horiz, posX, posY, COLUMN_SIZE);
    }

    private void prepareLabel(final Label label, final String labelTxt, final FLOATMODE horiz, final int posX, final int posY, final int columnSize) {
        label.setForegroundColor(TextColor.ANSI.WHITE);
        label.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        int computedPos = posX * columnSize;
        if (horiz == FLOATMODE.RIGHT) {
            final int offset = columnSize - labelTxt.length();
            computedPos += offset;
        }
        computedPos = Math.max(0, computedPos);
        label.setPosition(new TerminalPosition(computedPos, posY));
        label.setSize(new TerminalSize(Math.max(columnSize, labelTxt.length()), 1));
    }

    private void createTeleinfoPanel() {

        this.contentPanel = new Panel(new AbsoluteLayout());
        this.contentPanel.setTheme(theme);

        this.contentPanel.addComponent(this.mainTitleLabels.get(MODE.TELEINFO));

        this.contentPanel.addComponent(this.titleLabels.get(INFOS.ADCO));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.OPTARIF));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.ISOUSC));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.HHPHC));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.HCHC));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.HCHP));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.IINST));

        this.contentPanel.addComponent(this.valueLabels.get(INFOS.ADCO));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.OPTARIF));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.ISOUSC));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.HHPHC));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.HCHC));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.HCHP));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.IINST));

        for (final Label current : this.intIndicLabels.values()) {
            this.contentPanel.addComponent(current);
        }

        this.window.setComponent(this.contentPanel);

    }

    private void createMeteoPanel() {

        this.contentPanel = new Panel(new AbsoluteLayout());
        this.contentPanel.setTheme(theme);

        this.contentPanel.addComponent(this.mainTitleLabels.get(MODE.METEO));

        this.contentPanel.addComponent(this.titleLabels.get(INFOS.TEMP));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.TEMP2));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.ABSPRE));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.RELPRE));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.HYGROHUM));

        this.contentPanel.addComponent(this.valueLabels.get(INFOS.TEMP));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.TEMP2));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.ABSPRE));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.RELPRE));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.HYGROHUM));

        for (final Label current : this.tempIndicLabels.values()) {
            this.contentPanel.addComponent(current);
        }

        this.window.setComponent(this.contentPanel);
    }

    private void createChauffagePanel() {

        this.contentPanel = new Panel(new AbsoluteLayout());
        this.contentPanel.setTheme(theme);

        this.contentPanel.addComponent(this.mainTitleLabels.get(MODE.CHAUFFAGE));

        this.contentPanel.addComponent(this.titleLabels.get(INFOS.TEMPHOURMODE));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.MODECHAUFF));
        this.contentPanel.addComponent(this.titleLabels.get(INFOS.TEMPCHAUFF));

        this.contentPanel.addComponent(this.valueLabels.get(INFOS.TEMPHOURMODE));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.MODECHAUFF));
        this.contentPanel.addComponent(this.valueLabels.get(INFOS.TEMPCHAUFF));

        for (final Label current : this.hoursIndicLabel.values()) {
            this.contentPanel.addComponent(current);
        }
        for (final Label current : this.hoursValueLabel.values()) {
            this.contentPanel.addComponent(current);
        }

        this.window.setComponent(this.contentPanel);
    }

    @Override
    public void changedOccured() {
        try {
            this.infos = this.featureWrapper.getFeaturesInfos();
            this.changedTeleinfo();
            this.changedMeteo();
            this.changedChauffage();
            this.resizeValueLabels();
        } catch (final Exception e) {
            LOGGER.error("An error occured", e);
        }
    }

    private void resizeValueLabels() {
        for (final Label label : this.valueLabels.values()) {
            label.setSize(new TerminalSize(Math.max(COLUMN_SIZE, label.getText().length()), 1));
        }
    }

    private void changedTeleinfo() {
        this.valueLabels.get(INFOS.ADCO).setText(this.infos.getOrDefault(INFOS.ADCO.name(), ""));
        this.valueLabels.get(INFOS.OPTARIF).setText(this.infos.getOrDefault(INFOS.OPTARIF.name(), ""));
        this.valueLabels.get(INFOS.ISOUSC).setText(this.formatAndAddUnit(this.infos.getOrDefault(INFOS.ISOUSC.name(), ""), UNITS.A));
        this.valueLabels.get(INFOS.HHPHC).setText(this.infos.getOrDefault(INFOS.HHPHC.name(), ""));
        this.valueLabels.get(INFOS.HCHC).setText(this.formatAndAddUnit(this.infos.getOrDefault(INFOS.HCHC.name(), ""), UNITS.WH));
        this.valueLabels.get(INFOS.HCHP).setText(this.formatAndAddUnit(this.infos.getOrDefault(INFOS.HCHP.name(), ""), UNITS.WH));
        this.valueLabels.get(INFOS.IINST).setText(this.formatAndAddUnit(this.infos.getOrDefault(INFOS.IINST.name(), ""), UNITS.A));
        if (this.infos.containsKey((INFOS.IINST.name()))
                && NumberUtils.tryParseDouble(this.infos.get(INFOS.IINST.name()))
                && this.infos.containsKey((INFOS.ISOUSC.name()))
                && NumberUtils.tryParseDouble(this.infos.get(INFOS.ISOUSC.name()))) {
            this.fillBarre(this.intIndicLabels, Double.parseDouble(this.infos.get(INFOS.IINST.name())), Double.parseDouble(this.infos.get(INFOS.ISOUSC.name())));
        }
    }

    private void changedMeteo() {
        this.valueLabels.get(INFOS.TEMP).setText(this.formatAndAddUnit(this.infos.get(INFOS.TEMP.name()), UNITS.C));
        this.valueLabels.get(INFOS.TEMP2).setText(this.formatAndAddUnit(this.infos.get(INFOS.TEMP2.name()), UNITS.C));
        this.valueLabels.get(INFOS.ABSPRE).setText(this.formatAndAddUnit(this.infos.get(INFOS.ABSPRE.name()), UNITS.HPA));
        this.valueLabels.get(INFOS.RELPRE).setText(this.formatAndAddUnit(this.infos.get(INFOS.RELPRE.name()), UNITS.HPA));
        this.valueLabels.get(INFOS.HYGROHUM).setText(this.formatAndAddUnit(this.infos.get(INFOS.HYGROHUM.name()), UNITS.PERCENT));
        if (this.infos.containsKey((INFOS.TEMP.name()))
                && NumberUtils.tryParseDouble(this.infos.get(INFOS.TEMP.name()))) {
            this.fillBarre(this.tempIndicLabels, Double.parseDouble(this.infos.get(INFOS.TEMP.name())), TMAX);
        }
    }

    private void changedChauffage() {
        this.valueLabels.get(INFOS.TEMPHOURMODE).setText(this.infos.get(INFOS.TEMPHOURMODE.name()));
        this.valueLabels.get(INFOS.MODECHAUFF).setText(this.infos.get(INFOS.MODECHAUFF.name()));
        this.valueLabels.get(INFOS.TEMPCHAUFF).setText(this.formatAndAddUnit(this.infos.get(INFOS.TEMPCHAUFF.name()), UNITS.C));
        for (final Map.Entry<String, Label> entry : this.hoursValueLabel.entrySet()) {
            entry.getValue().setText(this.formatAndAddUnit(this.infos.get(entry.getKey()), UNITS.C));
        }
        if (this.selectedTabableLabelIndex == 0) {
            this.highLightLabel(this.valueLabels.get(INFOS.TEMPCHAUFF));
        } else {
            final Integer tempLabelIndex = this.selectedTabableLabelIndex - 1;
            this.highLightLabel(this.hoursValueLabel.get(INFOS.TEMPCHAUFFTIME.name() + String.format("%02d", tempLabelIndex)));
        }
    }

    private void highLightLabel(final Label label) {
        for (final Label labelToReset : this.tabableLabels) {
            labelToReset.setBackgroundColor(TextColor.ANSI.BLACK);
            labelToReset.setForegroundColor(TextColor.ANSI.WHITE);
        }
        label.setBackgroundColor(TextColor.ANSI.WHITE);
        label.setForegroundColor(TextColor.ANSI.BLACK);
    }

    private void updateDisplayTrame() {
        if (this.mode == MODE.TELEINFO) {
            this.createTeleinfoPanel();
        } else if (this.mode == MODE.METEO) {
            this.createMeteoPanel();
        } else if (this.mode == MODE.CHAUFFAGE) {
            this.createChauffagePanel();
        }
    }

    @Override
    public void displayInterface() {
        this.createTeleinfoPanel();
        this.displayed = true;
        this.textGUI.addWindowAndWait(this.window);
    }

    private void changeMode(final MODE mode) {
        if (this.mode != mode) {
            this.mode = mode;
            this.updateDisplayTrame();
        }
    }

    private String formatAndAddUnit(final String base, final UNITS unit) {
        String result = base == null ? "N/A" : base;
        result += " ";
        if (UNITS.C.equals(unit)) {
            result += "°C";
        } else if (UNITS.WH.equals(unit)) {
            result += "Wh";
        } else if (UNITS.A.equals(unit)) {
            result += "A";
        } else if (UNITS.PERCENT.equals(unit)) {
            result += "%";
        } else if (UNITS.HPA.equals(unit)) {
            result += "hPa";
        } else if (UNITS.HOURS.equals(unit)) {
            result += "h";
        }
        return result;
    }

    @Override
    public void onInput(final Window basePane, final KeyStroke keyStroke, final AtomicBoolean deliverEvent) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            this.window.close();
        } else if (keyStroke.getKeyType() == KeyType.F1) {
            this.changeMode(MODE.TELEINFO);
        } else if (keyStroke.getKeyType() == KeyType.F2) {
            this.changeMode(MODE.METEO);
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            this.changeMode(MODE.CHAUFFAGE);
        }

        if (this.mode == MODE.CHAUFFAGE) {
            if (keyStroke.getKeyType() == KeyType.F9) {
                this.chauffage.save();
            } else if (keyStroke.getKeyType() == KeyType.F8) {
                this.chauffage.changeMode();
            } else if (keyStroke.getKeyType() == KeyType.Tab) {
                this.selectedTabableLabelIndex++;
                if (this.selectedTabableLabelIndex >= this.tabableLabels.size()) {
                    this.selectedTabableLabelIndex = 0;
                }
                this.changedOccured();
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                this.manageTemperatureChange(true);
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                this.manageTemperatureChange(false);
            }
        }
    }

    private void manageTemperatureChange(final boolean isUp) {
        final String idSelectedLabel = this.tabableLabels.get(this.selectedTabableLabelIndex).getId();
        if ("GENERAL".equals(idSelectedLabel)) {
            this.chauffage.changeTemperature(isUp);
        } else {
            final Integer startHour = Integer.parseInt(idSelectedLabel);
            this.chauffage.changeTemperatureHour(isUp, startHour);
        }

    }

    @Override
    public void onUnhandledInput(final Window basePane, final KeyStroke keyStroke, final AtomicBoolean hasBeenHandled) {
        //Pas d'actions sur cet évènement
    }

    @Override
    public void onResized(final Window window, final TerminalSize oldSize, final TerminalSize newSize) {
        //Pas d'actions sur cet évènement
    }

    @Override
    public void onMoved(final Window window, final TerminalPosition oldPosition, final TerminalPosition newPosition) {
        //Pas d'actions sur cet évènement
    }

    public boolean isDisplayed() {
        return this.displayed;
    }
}
