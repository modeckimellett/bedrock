/**
 * Copyright 2014, CITYTECH, Inc.
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
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import javax.jcr.Session;
import java.util.Set;

/**
 * Provides replication to a selected set of agents.
 */
@Component
@Service
public final class DefaultSelectiveReplicationService implements SelectiveReplicationService {

    @Reference
    private Replicator replicator;

    @Override
    public void replicate(final Session session, final String path, final ReplicationActionType actionType,
        final Set<String> agentIds) throws ReplicationException {
        final AgentFilter filter = new AgentIdFilter(agentIds.toArray(new String[agentIds.size()]));

        final ReplicationOptions replicationOptions = new ReplicationOptions();

        replicationOptions.setFilter(filter);

        replicator.replicate(session, actionType, path, replicationOptions);
    }
}
