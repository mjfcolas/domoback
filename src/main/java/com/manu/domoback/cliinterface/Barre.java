package com.manu.domoback.cliinterface;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.manu.domoback.common.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Barre {

    protected Map<String, Label> labels = new HashMap<>();
    protected int totalLength;

    public Barre(int columnSize, int posX, int posY, int length) {
        Double dividedLength = (double) length * columnSize / 3;
        Label label = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label.setBackgroundColor(TextColor.ANSI.BLUE);
        label.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        int computedPos = posX * columnSize;
        label.setPosition(new TerminalPosition(computedPos, posY));
        label.setSize(new TerminalSize(dividedLength.intValue(), 1));
        labels.put("1", label);

        Label label2 = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label2.setBackgroundColor(TextColor.ANSI.YELLOW);
        label2.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        computedPos = posX * columnSize + dividedLength.intValue();
        label2.setPosition(new TerminalPosition(computedPos, posY));
        label2.setSize(new TerminalSize(dividedLength.intValue(), 1));
        labels.put("2", label2);

        computedPos = posX * columnSize + 2 * dividedLength.intValue();
        dividedLength = length * columnSize - 2 * dividedLength;
        Label label3 = new Label(StringUtils.repeat(' ', dividedLength.intValue()));
        label3.setBackgroundColor(TextColor.ANSI.RED);
        label3.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        label3.setPosition(new TerminalPosition(computedPos, posY));
        label3.setSize(new TerminalSize(dividedLength.intValue(), 1));
        labels.put("3", label3);
    }
}
