package com.manu.domoback.features;

import com.manu.domoback.common.CustLogger;
import com.manu.domoback.database.IJdbc;
import com.manu.domoback.listeners.DataListener;

import javax.swing.event.EventListenerList;

public abstract class AbstractFeature implements IFeature {

    protected IJdbc jdbc;
    protected String name;

    protected final EventListenerList listeners = new EventListenerList();

    public void subscribe(DataListener listener) {
        listeners.add(DataListener.class, listener);
    }

    public void unsubscribe(DataListener listener) {
        listeners.remove(DataListener.class, listener);
    }

    public DataListener[] getListeners() {
        return listeners.getListeners(DataListener.class);
    }

    protected void fireDataChanged() {
        CustLogger.traprintln("AbstractFeature.fireDataChanged");
        for (DataListener listener : getListeners()) {
            listener.changedOccured();
        }
    }

    public String getName() {
        return name;
    }

    public AbstractFeature(IJdbc jdbc) {
        this.jdbc = jdbc;
    }

}
