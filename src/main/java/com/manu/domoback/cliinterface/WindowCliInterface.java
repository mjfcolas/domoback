package com.manu.domoback.cliinterface;

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
import com.manu.domoback.common.Bundles;
import com.manu.domoback.common.NumberUtils;
import com.manu.domoback.common.StringUtils;
import com.manu.domoback.features.IChauffage;
import com.manu.domoback.features.IFeature;
import com.manu.domoback.features.IFeatureWrapper;
import com.manu.domoback.features.ReturnKeys;
import com.manu.domoback.listeners.DataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WindowCliInterface implements DataListener, WindowListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowCliInterface.class.getName());

    protected static SimpleTheme theme = null;

    protected DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    protected Screen screen = null;
    protected MODE mode = MODE.TELEINFO;
    protected IFeatureWrapper featureWrapper = null;
    protected WindowBasedTextGUI textGUI = null;
    protected Window window = null;
    protected Panel contentPanel = null;
    protected IChauffage chauffage = null;


    protected static final int COLUMN_SIZE = 10;
    protected static final double TMAX = 40;

    protected EnumMap<MODE, Label> mainTitleLabels = new EnumMap<>(MODE.class);
    protected EnumMap<ReturnKeys, Label> titleLabels = new EnumMap<>(ReturnKeys.class);
    protected EnumMap<ReturnKeys, Label> valueLabels = new EnumMap<>(ReturnKeys.class);
    protected Map<String, Label> tempIndicLabels = new HashMap<>();
    protected Map<String, Label> intIndicLabels = new HashMap<>();

    protected Map<String, String> infos = new HashMap<>();

    static {
        theme = new SimpleTheme(TextColor.ANSI.BLACK, TextColor.ANSI.BLACK);
        theme.setWindowDecorationRenderer(new EmptyWindowDecorationRenderer());
    }

    public WindowCliInterface(IFeatureWrapper featureWrapper, IChauffage chauffage) {
        try {
            this.featureWrapper = featureWrapper;
            this.chauffage = chauffage;

            for (IFeature feature : this.featureWrapper.getFeatures()) {
                feature.subscribe(this);
            }

            this.initialiseLabels();

            this.screen = defaultTerminalFactory.createScreen();
            this.screen.startScreen();
            this.screen.clear();
            this.textGUI = new MultiWindowTextGUI(this.screen, TextColor.ANSI.BLACK);
            this.window = new BasicWindow();
            this.window.setTheme(theme);
            this.window.addWindowListener(this);

        } catch (IOException e) {
            LOGGER.error("An error occured", e);
            System.exit(-1);
        }
    }

    protected void initialiseLabels() {

        TextColor pureWhite = new RGB(255, 255, 255);
        Label titleTeleinfo = this.createLabel(Bundles.messages().getProperty("gui.teleinfo.panel.title"), FLOATMODE.RIGHT, 2, 1);
        titleTeleinfo.setForegroundColor(pureWhite);
        Label titleMeteo = this.createLabel(Bundles.messages().getProperty("gui.meteo.panel.title"), FLOATMODE.RIGHT, 2, 1);
        titleMeteo.setForegroundColor(pureWhite);
        Label titleChauff = this.createLabel(Bundles.messages().getProperty("gui.chauff.panel.title"), FLOATMODE.RIGHT, 2, 1);
        titleChauff.setForegroundColor(pureWhite);

        mainTitleLabels.put(MODE.TELEINFO, titleTeleinfo);
        mainTitleLabels.put(MODE.METEO, titleMeteo);
        mainTitleLabels.put(MODE.CHAUFFAGE, titleChauff);

        titleLabels.put(ReturnKeys.ADCO, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.address"), FLOATMODE.RIGHT, 2, 2));
        titleLabels.put(ReturnKeys.OPTARIF, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.optarif"), FLOATMODE.RIGHT, 2, 3));
        titleLabels.put(ReturnKeys.ISOUSC, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.isousc"), FLOATMODE.RIGHT, 2, 4));
        titleLabels.put(ReturnKeys.HHPHC, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.trhor"), FLOATMODE.RIGHT, 6, 2));
        titleLabels.put(ReturnKeys.HCHC, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.hc"), FLOATMODE.RIGHT, 6, 3));
        titleLabels.put(ReturnKeys.HCHP, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.hp"), FLOATMODE.RIGHT, 6, 4));
        titleLabels.put(ReturnKeys.IINST, this.createLabel(Bundles.messages().getProperty("gui.teleinfo.label.iinst"), FLOATMODE.RIGHT, 2, 6));

        valueLabels.put(ReturnKeys.ADCO, this.createLabel("", FLOATMODE.LEFT, 3, 2));
        valueLabels.put(ReturnKeys.OPTARIF, this.createLabel("", FLOATMODE.LEFT, 3, 3));
        valueLabels.put(ReturnKeys.ISOUSC, this.createLabel("", FLOATMODE.LEFT, 3, 4));
        valueLabels.put(ReturnKeys.HHPHC, this.createLabel("", FLOATMODE.LEFT, 7, 2));
        valueLabels.put(ReturnKeys.HCHC, this.createLabel("", FLOATMODE.LEFT, 7, 3));
        valueLabels.put(ReturnKeys.HCHP, this.createLabel("", FLOATMODE.LEFT, 7, 4));
        valueLabels.put(ReturnKeys.IINST, this.createLabel("", FLOATMODE.LEFT, 3, 6));

        titleLabels.put(ReturnKeys.TEMP, this.createLabel(Bundles.messages().getProperty("gui.meteo.label.tint"), FLOATMODE.RIGHT, 2, 2));
        titleLabels.put(ReturnKeys.TEMP2, this.createLabel(Bundles.messages().getProperty("gui.meteo.label.tcha"), FLOATMODE.RIGHT, 2, 3));
        titleLabels.put(ReturnKeys.ABSPRE, this.createLabel(Bundles.messages().getProperty("gui.meteo.label.pabs"), FLOATMODE.RIGHT, 2, 4));
        titleLabels.put(ReturnKeys.RELPRE, this.createLabel(Bundles.messages().getProperty("gui.meteo.label.prel"), FLOATMODE.RIGHT, 2, 5));
        titleLabels.put(ReturnKeys.HYGROHUM, this.createLabel(Bundles.messages().getProperty("gui.meteo.label.hum"), FLOATMODE.RIGHT, 2, 6));

        valueLabels.put(ReturnKeys.TEMP, this.createLabel("", FLOATMODE.LEFT, 3, 2));
        valueLabels.put(ReturnKeys.TEMP2, this.createLabel("", FLOATMODE.LEFT, 3, 3));
        valueLabels.put(ReturnKeys.ABSPRE, this.createLabel("", FLOATMODE.LEFT, 3, 4));
        valueLabels.put(ReturnKeys.RELPRE, this.createLabel("", FLOATMODE.LEFT, 3, 5));
        valueLabels.put(ReturnKeys.HYGROHUM, this.createLabel("", FLOATMODE.LEFT, 3, 6));

        titleLabels.put(ReturnKeys.MODECHAUFF, this.createLabel(Bundles.messages().getProperty("gui.chauff.label.state"), FLOATMODE.RIGHT, 2, 2));
        titleLabels.put(ReturnKeys.TEMPCHAUFF, this.createLabel(Bundles.messages().getProperty("gui.chauff.label.temp"), FLOATMODE.RIGHT, 2, 3));

        valueLabels.put(ReturnKeys.MODECHAUFF, this.createLabel("", FLOATMODE.LEFT, 3, 2));
        valueLabels.put(ReturnKeys.TEMPCHAUFF, this.createLabel("", FLOATMODE.LEFT, 3, 3));

        this.createTempIndicator();
        this.createIntensityIndicator();

    }

    protected void createTempIndicator() {

        tempIndicLabels.put("0", this.createLabel("0 °C", FLOATMODE.LEFT, 1, 9));
        tempIndicLabels.put("10", this.createLabel("10 °C", FLOATMODE.LEFT, 2, 9));
        tempIndicLabels.put("20", this.createLabel("20 °C", FLOATMODE.LEFT, 3, 9));
        tempIndicLabels.put("30", this.createLabel("30 °C", FLOATMODE.LEFT, 4, 9));
        tempIndicLabels.put("40", this.createLabel("40 °C", FLOATMODE.LEFT, 5, 9));
        tempIndicLabels.putAll(this.createBarre(1, 7, 4));
    }

    protected void createIntensityIndicator() {

        intIndicLabels.put("0", this.createLabel("| 0 %", FLOATMODE.LEFT, 1, 9));
        intIndicLabels.put("20", this.createLabel("| 20 %", FLOATMODE.LEFT, 2, 9));
        intIndicLabels.put("40", this.createLabel("| 40 %", FLOATMODE.LEFT, 3, 9));
        intIndicLabels.put("60", this.createLabel("| 60 %", FLOATMODE.LEFT, 4, 9));
        intIndicLabels.put("80", this.createLabel("| 80 %", FLOATMODE.LEFT, 5, 9));
        intIndicLabels.put("100", this.createLabel("| 100 %", FLOATMODE.LEFT, 6, 9));
        intIndicLabels.putAll(this.createBarre(1, 7, 5));
    }

    protected Map<String, Label> createBarre(int posX, int posY, int length) {

        Map<String, Label> result = new HashMap<>();

        Double dividedLength = (double) length * COLUMN_SIZE / 3;
        Label label = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label.setPreferredSize(new TerminalSize(dividedLength.intValue(), 1));
        label.setBackgroundColor(TextColor.ANSI.BLUE);
        label.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        int computedPos = posX * COLUMN_SIZE;
        label.setPosition(new TerminalPosition(computedPos, posY));
        label.setSize(new TerminalSize(dividedLength.intValue(), 1));
        result.put("1", label);

        Label label2 = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label2.setPreferredSize(new TerminalSize(dividedLength.intValue(), 1));
        label2.setBackgroundColor(TextColor.ANSI.YELLOW);
        label2.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        computedPos = posX * COLUMN_SIZE + dividedLength.intValue();
        label2.setPosition(new TerminalPosition(computedPos, posY));
        label2.setSize(new TerminalSize(dividedLength.intValue(), 1));
        result.put("2", label2);

        computedPos = posX * COLUMN_SIZE + 2 * dividedLength.intValue();
        dividedLength = length * COLUMN_SIZE - 2 * dividedLength;
        Label label3 = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label3.setPreferredSize(new TerminalSize(dividedLength.intValue(), 1));
        label3.setBackgroundColor(TextColor.ANSI.RED);
        label3.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        label3.setPosition(new TerminalPosition(computedPos, posY));
        label3.setSize(new TerminalSize(dividedLength.intValue(), 1));
        result.put("3", label3);

        return result;
    }

    protected void fillBarre(Map<String, Label> barre, double value, double max) {
        double ratio1 = Math.min((value / max) * 3, 1);
        double ratio2 = Math.max(0, Math.min((value / max) * 3 - 1, 1));
        double ratio3 = Math.max(0, Math.min((value / max) * 3 - 2, 1));

        this.fillOneBarreLabel(barre.get("1"), ratio1);
        this.fillOneBarreLabel(barre.get("2"), ratio2);
        this.fillOneBarreLabel(barre.get("3"), ratio3);
    }

    protected void fillOneBarreLabel(Label label, double ratio) {
        int spaceNumber = (int) (label.getPreferredSize().getColumns() * ratio);
        label.setText(StringUtils.repeat(' ', spaceNumber));
    }

    protected Label createLabel(String labelTxt, FLOATMODE horiz, int posX, int posY) {
        Label label = new Label(labelTxt);
        label.setForegroundColor(TextColor.ANSI.WHITE);
        label.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        int computedPos = posX * COLUMN_SIZE;
        if (horiz == FLOATMODE.RIGHT) {
            int offset = COLUMN_SIZE - labelTxt.length();
            computedPos += offset;
        }
        computedPos = Math.max(0, computedPos);
        label.setPosition(new TerminalPosition(computedPos, posY));
        label.setSize(new TerminalSize(Math.max(COLUMN_SIZE, labelTxt.length()), 1));
        return label;
    }

    protected void createTeleinfoPanel() {

        this.contentPanel = new Panel(new AbsoluteLayout());
        this.contentPanel.setTheme(theme);

        this.contentPanel.addComponent(this.mainTitleLabels.get(MODE.TELEINFO));

        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.ADCO));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.OPTARIF));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.ISOUSC));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.HHPHC));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.HCHC));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.HCHP));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.IINST));

        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.ADCO));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.OPTARIF));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.ISOUSC));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.HHPHC));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.HCHC));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.HCHP));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.IINST));

        for (Label current : this.intIndicLabels.values()) {
            this.contentPanel.addComponent(current);
        }

        this.window.setComponent(contentPanel);

    }

    protected void createMeteoPanel() {

        this.contentPanel = new Panel(new AbsoluteLayout());
        this.contentPanel.setTheme(theme);

        this.contentPanel.addComponent(this.mainTitleLabels.get(MODE.METEO));

        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.TEMP));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.TEMP2));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.ABSPRE));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.RELPRE));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.HYGROHUM));

        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.TEMP));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.TEMP2));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.ABSPRE));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.RELPRE));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.HYGROHUM));

        for (Label current : this.tempIndicLabels.values()) {
            this.contentPanel.addComponent(current);
        }

        this.window.setComponent(contentPanel);
    }

    protected void createChauffagePanel() {

        this.contentPanel = new Panel(new AbsoluteLayout());
        this.contentPanel.setTheme(theme);

        this.contentPanel.addComponent(this.mainTitleLabels.get(MODE.CHAUFFAGE));

        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.MODECHAUFF));
        this.contentPanel.addComponent(this.titleLabels.get(ReturnKeys.TEMPCHAUFF));

        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.MODECHAUFF));
        this.contentPanel.addComponent(this.valueLabels.get(ReturnKeys.TEMPCHAUFF));

        this.window.setComponent(contentPanel);
    }

    public void changedOccured() {
        try {
            this.infos = featureWrapper.getFeaturesInfos();
            this.changedTeleinfo();
            this.changedMeteo();
            this.changedChauffage();
            this.resizeValueLabels();
        } catch (Exception e) {
            LOGGER.error("An error occured", e);
        }
    }

    private void resizeValueLabels() {
        for (Label label : valueLabels.values()) {
            label.setSize(new TerminalSize(Math.max(COLUMN_SIZE, label.getText().length()), 1));
        }
    }

    private void changedTeleinfo() {
        this.valueLabels.get(ReturnKeys.ADCO).setText(this.infos.getOrDefault(ReturnKeys.ADCO.name(), ""));
        this.valueLabels.get(ReturnKeys.OPTARIF).setText(this.infos.getOrDefault(ReturnKeys.OPTARIF.name(), ""));
        this.valueLabels.get(ReturnKeys.ISOUSC).setText(this.addUnit(this.infos.getOrDefault(ReturnKeys.ISOUSC.name(), ""), UNITS.A));
        this.valueLabels.get(ReturnKeys.HHPHC).setText(this.infos.getOrDefault(ReturnKeys.HHPHC.name(), ""));
        this.valueLabels.get(ReturnKeys.HCHC).setText(this.addUnit(this.infos.getOrDefault(ReturnKeys.HCHC.name(), ""), UNITS.WH));
        this.valueLabels.get(ReturnKeys.HCHP).setText(this.addUnit(this.infos.getOrDefault(ReturnKeys.HCHP.name(), ""), UNITS.WH));
        this.valueLabels.get(ReturnKeys.IINST).setText(this.addUnit(this.infos.getOrDefault(ReturnKeys.IINST.name(), ""), UNITS.A));
        if (this.infos.containsKey((ReturnKeys.IINST.name()))
                && NumberUtils.tryParseDouble(this.infos.get(ReturnKeys.IINST.name()))
                && this.infos.containsKey((ReturnKeys.ISOUSC.name()))
                && NumberUtils.tryParseDouble(this.infos.get(ReturnKeys.ISOUSC.name()))) {
            this.fillBarre(this.intIndicLabels, Double.parseDouble(this.infos.get(ReturnKeys.IINST.name())), Double.parseDouble(this.infos.get(ReturnKeys.ISOUSC.name())));
        }
    }

    private void changedMeteo() {
        this.valueLabels.get(ReturnKeys.TEMP).setText(this.addUnit(this.infos.get(ReturnKeys.TEMP.name()), UNITS.C));
        this.valueLabels.get(ReturnKeys.TEMP2).setText(this.addUnit(this.infos.get(ReturnKeys.TEMP2.name()), UNITS.C));
        this.valueLabels.get(ReturnKeys.ABSPRE).setText(this.addUnit(this.infos.get(ReturnKeys.ABSPRE.name()), UNITS.HPA));
        this.valueLabels.get(ReturnKeys.RELPRE).setText(this.addUnit(this.infos.get(ReturnKeys.RELPRE.name()), UNITS.HPA));
        this.valueLabels.get(ReturnKeys.HYGROHUM).setText(this.addUnit(this.infos.get(ReturnKeys.HYGROHUM.name()), UNITS.PERCENT));
        if (this.infos.containsKey((ReturnKeys.TEMP.name()))
                && NumberUtils.tryParseDouble(this.infos.get(ReturnKeys.TEMP.name()))) {
            this.fillBarre(this.tempIndicLabels, Double.parseDouble(this.infos.get(ReturnKeys.TEMP.name())), TMAX);
        }
    }

    private void changedChauffage() {
        this.valueLabels.get(ReturnKeys.MODECHAUFF).setText(this.infos.get(ReturnKeys.MODECHAUFF.name()));
        this.valueLabels.get(ReturnKeys.TEMPCHAUFF).setText(this.addUnit(this.infos.get(ReturnKeys.TEMPCHAUFF.name()), UNITS.C));
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

    public void displayInterface() {
        this.createTeleinfoPanel();
        textGUI.addWindowAndWait(window);
    }

    private void changeMode(MODE mode) {
        if (this.mode != mode) {
            this.mode = mode;
            this.updateDisplayTrame();
        }
    }

    private String addUnit(String base, UNITS unit) {
        String result = base;
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
        }
        return result;
    }

    @Override
    public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            window.close();
        } else if (keyStroke.getKeyType() == KeyType.F1) {
            this.changeMode(MODE.TELEINFO);
        } else if (keyStroke.getKeyType() == KeyType.F2) {
            this.changeMode(MODE.METEO);
        } else if (keyStroke.getKeyType() == KeyType.F3) {
            this.changeMode(MODE.CHAUFFAGE);
        }

        if (this.mode == MODE.CHAUFFAGE) {
            if (keyStroke.getKeyType() == KeyType.F9) {
                chauffage.save();
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                chauffage.changeTemperature(true);
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                chauffage.changeTemperature(false);
            }
        }
    }

    @Override
    public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
        //Pas d'actions sur cet évènement
    }

    @Override
    public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
        //Pas d'actions sur cet évènement
    }

    @Override
    public void onMoved(Window window, TerminalPosition oldPosition, TerminalPosition newPosition) {
        //Pas d'actions sur cet évènement
    }
}
