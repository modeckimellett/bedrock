package com.citytechinc.aem.bedrock.core.replication;

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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Replication listener that ensures ancestor pages are activated when any page receives an activation request.
 */
@Component(immediate = true)
@Service
@Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)
public final class PageReplicationListener extends AbstractReplicationListener {

    private static final Logger LOG = LoggerFactory.getLogger(PageReplicationListener.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingRepository repository;

    @Reference
    protected Replicator replicator;

    protected PageManager pageManager;

    protected Session session;

    protected ResourceResolver resourceResolver;

    @Override
    protected void handleActivate(final String path) {
        try {
            final Node node = session.getNode(path);

            if (NameConstants.NT_PAGE.equals(node.getPrimaryNodeType().getName())) {
                final Page page = pageManager.getPage(path);

                Page parent = page.getParent();

                while (parent != null && parent.getDepth() > 1) {
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

    @SuppressWarnings("deprecation")
    @Activate
    protected void activate() throws LoginException, RepositoryException {
        session = repository.loginAdministrative(null);
        resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
        pageManager = resourceResolver.adaptTo(PageManager.class);
    }

    @Deactivate
    protected void deactivate() {
        if (session != null) {
            session.logout();
        }

        if (resourceResolver != null) {
            resourceResolver.close();
        }
    }
}
