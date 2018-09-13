package com.manu.domoback.features;

import com.manu.domoback.database.IJdbc;
import com.manu.domoback.listeners.DataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;

public abstract class AbstractFeature implements IFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFeature.class.getName());

    protected IJdbc jdbc;
    protected String name;

    private final EventListenerList listeners = new EventListenerList();

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
        LOGGER.trace("AbstractFeature.fireDataChanged");
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
