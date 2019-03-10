package com.manu.domoback.features;

import com.manu.domoback.features.api.IFeature;
import com.manu.domoback.features.api.listeners.DataListener;
import com.manu.domoback.persistence.api.PersistenceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;

public abstract class AbstractFeature implements IFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFeature.class.getName());

    protected PersistenceApi jdbc;
    protected String name;

    private final EventListenerList listeners = new EventListenerList();

    @Override
    public void subscribe(final DataListener listener) {
        this.listeners.add(DataListener.class, listener);
    }

    @Override
    public void unsubscribe(final DataListener listener) {
        this.listeners.remove(DataListener.class, listener);
    }

    private DataListener[] getListeners() {
        return this.listeners.getListeners(DataListener.class);
    }

    void fireDataChanged() {
        LOGGER.trace("AbstractFeature.fireDataChanged");
        for (final DataListener listener : this.getListeners()) {
            listener.changedOccured();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public AbstractFeature(final PersistenceApi jdbc) {
        this.jdbc = jdbc;
    }

}
