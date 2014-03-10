/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.servlets.replication

import com.citytechinc.aem.bedrock.services.replication.SelectiveReplicationService
import com.citytechinc.aem.bedrock.servlets.AbstractJsonResponseServlet
import com.day.cq.replication.AgentManager
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationException
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.jcr.Session
import javax.servlet.ServletException

import static com.google.common.base.Preconditions.checkArgument

@Slf4j("LOG")
class SelectiveReplicationServlet extends AbstractJsonResponseServlet {

    private static final String PARAMETER_ACTION = "action"

    private static final String PARAMETER_AGENT_IDS = "agentIds"

    private static final String PARAMETER_PATHS = "paths"

    @Reference
    protected AgentManager agentManager

    @Reference
    protected SelectiveReplicationService selectiveReplicationService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        def paths = request.getParameterValues(PARAMETER_PATHS)
        def agentIds = request.getParameterValues(PARAMETER_AGENT_IDS)

        checkArgument(paths as Boolean, "paths parameter must be non-null and non-empty")
        checkArgument(agentIds as Boolean, "agentIds parameter must be non-null and non-empty")

        def action = request.getParameter(PARAMETER_ACTION)
        def actionType = ReplicationActionType.fromName(action)

        checkArgument(actionType as Boolean , "invalid action parameter = $action")

        def uniqueAgentIds = agentIds as LinkedHashSet

        checkArgument(agentsExist(uniqueAgentIds), "invalid agent IDs, one or more of the provided agent IDs does not exist")

        def session = request.resourceResolver.adaptTo(Session)

        def result = [:]

        (paths as LinkedHashSet).each { path ->
            boolean success = false

            LOG.info "doPost() executing replication action = $actionType for path = $path to agent IDs = $agentIds"

            try {
                selectiveReplicationService.replicate(session, path, actionType, uniqueAgentIds)

                success = true
            } catch (ReplicationException e) {
                LOG.error "error executing replication action = $actionType for path = $path", e
            }

            result[path] = success
        }

        writeJsonResponse(response, result)
    }

    private boolean agentsExist(agentIds) {
        agentManager.agents.keySet().containsAll(agentIds)
    }
}
