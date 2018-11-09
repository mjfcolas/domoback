package com.manu.domoback.cliinterface;

import com.googlecode.lanterna.gui2.Label;

class TabableLabel extends Label {

    private final String id;

    TabableLabel(final String labelText, final String id) {
        super(labelText);
        this.id = id;
    }

    String getId() {
        return this.id;
    }

}
