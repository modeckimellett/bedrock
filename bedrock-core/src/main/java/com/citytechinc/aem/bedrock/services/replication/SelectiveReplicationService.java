/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.services.replication;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;

import javax.jcr.Session;
import java.util.Set;

/**
 * Service providing "selective" replication to activate/deactivate content to a subset of replication agents (rather
 * than all agents, which is the CQ5 default behavior).
 */
public interface SelectiveReplicationService {

    /**
     * Selectively replicate content to the specified agents.
     *
     * @param session JCR session backing the current request
     * @param path page or node path to be replicated
     * @param actionType replication action
     * @param agentIds IDs of replication agents to use for this replication request
     * @throws ReplicationException if error occurs replicating content to the given agents
     */
    void replicate(Session session, String path, ReplicationActionType actionType, Set<String> agentIds)
        throws ReplicationException;
}
