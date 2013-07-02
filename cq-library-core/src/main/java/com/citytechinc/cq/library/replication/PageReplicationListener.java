/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.replication;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(immediate = true)
@Service
@Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)
public final class PageReplicationListener extends AbstractReplicationListener {

    private static final Logger LOG = LoggerFactory.getLogger(PageReplicationListener.class);

    @Reference
    private Replicator replicator;

    private PageManager pageManager;

    private Session session;

    @Override
    protected void handleActivate(final String path) {
        try {
            final Node node = session.getNode(path);

            if (NameConstants.NT_PAGE.equals(node.getPrimaryNodeType().getName())) {
                final Page page = pageManager.getPage(path);

                Page parent = page.getParent();

                while (parent != null && parent.getDepth() > 2) {
                    final ReplicationStatus status = parent.getContentResource().adaptTo(ReplicationStatus.class);

                    if (!status.isActivated()) {
                        activatePage(parent);
                    }

                    parent = parent.getParent();
                }
            } else {
                LOG.debug("handleActivate() path is not a page = {}", path);
            }
        } catch (RepositoryException e) {
            LOG.error("error getting node for path = " + path, e);
        }
    }

    @Override
    protected void handleDeactivate(final String path) {
        // nothing to do
    }

    private void activatePage(final Page page) {
        final String path = page.getPath();

        LOG.info("activatePage() activating page = {}", path);

        try {
            replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
        } catch (ReplicationException e) {
            LOG.error("error replicating page = " + path, e);
        }
    }

    @Activate
    protected void activate() throws LoginException, RepositoryException {
        session = getAdministrativeSession();
        pageManager = getAdministrativeResourceResolver().adaptTo(PageManager.class);
    }

    @Deactivate
    protected void deactivate() {
        closeSession();
        closeResourceResolver();
    }
}
