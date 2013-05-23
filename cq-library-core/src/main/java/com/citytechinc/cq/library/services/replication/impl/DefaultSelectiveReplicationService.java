/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.services.replication.impl;

import com.citytechinc.cq.library.services.replication.SelectiveReplicationService;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.AgentIdFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Set;

@Component(label = "Selective Replication Service", description = "Provides replication to a selected set of agents.")
@Service
@Property(name = Constants.SERVICE_VENDOR, value = "CITYTECH, Inc.")
public final class DefaultSelectiveReplicationService implements SelectiveReplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSelectiveReplicationService.class);

    @Reference
    private Replicator replicator;

    @Override
    public void replicate(final Session session, final String path, final ReplicationActionType actionType,
        final Set<String> agentIds) throws ReplicationException {
        LOG.info("replicate() executing replication action = {} for path = {} to agent IDs = {}",
            new Object[]{ actionType, path, agentIds });

        final ReplicationOptions replicationOptions = new ReplicationOptions();

        final AgentFilter filter = new AgentIdFilter(agentIds.toArray(new String[agentIds.size()]));

        replicationOptions.setFilter(filter);

        try {
            replicator.replicate(session, actionType, path, replicationOptions);
        } catch (ReplicationException e) {
            LOG.error("error executing replication action = " + actionType + " for path = " + path, e);

            throw e;
        }
    }
}
