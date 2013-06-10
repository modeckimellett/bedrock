/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.replication;

import com.citytechinc.cq.library.services.AbstractSlingService;
import com.day.cq.replication.ReplicationAction;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listeners extending this class need to add the following SCR annotations to
 * register the listener.
 * <p/>
 * <pre>
 * {@code
 * @Component(immediate = true, metatype = true, inherit = true)
 * @Service
 * @Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)
 * }
 * </pre>
 */
public abstract class AbstractReplicationListener extends AbstractSlingService implements EventHandler {

    private static final String TYPE_ACTIVATE = "ACTIVATE";

    private static final String TYPE_DEACTIVATE = "DEACTIVATE";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReplicationListener.class);

    @Override
    public final void handleEvent(final Event event) {
        final String type = (String) event.getProperty(ReplicationAction.PROPERTY_TYPE);
        final String path = (String) event.getProperty(ReplicationAction.PROPERTY_PATH);

        if (TYPE_ACTIVATE.equals(type)) {
            LOG.info("handleEvent() handling activate event for path = {}", path);

            handleActivate(path);
        } else if (TYPE_DEACTIVATE.equals(type)) {
            LOG.info("handleEvent() handling deactivate event for path = {}", path);

            handleDeactivate(path);
        } else {
            LOG.info("handleEvent() type = {} not handled for path = {}", type, path);
        }
    }

    /**
     * Handle activation event.
     *
     * @param path payload path
     */
    protected abstract void handleActivate(final String path);

    /**
     * Handle deactivation event.
     *
     * @param path payload path
     */
    protected abstract void handleDeactivate(final String path);
}
