/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.servlets.replication

import com.citytechinc.aem.bedrock.services.replication.SelectiveReplicationService
import com.citytechinc.aem.bedrock.testing.specs.AbstractBedrockSpec
import com.day.cq.replication.Agent
import com.day.cq.replication.AgentManager
import com.day.cq.replication.ReplicationActionType
import groovy.json.JsonBuilder
import spock.lang.Unroll

class SelectiveReplicationServletSpec extends AbstractBedrockSpec {

    @Unroll
    def "invalid parameters throw exception"() {
        def request = requestBuilder.build {
            parameters paths: paths, agentIds: agentIds, action: action
        }

        def response = responseBuilder.build()

        def servlet = new SelectiveReplicationServlet()

        servlet.agentManager = Mock(AgentManager)
        servlet.selectiveReplicationService = Mock(SelectiveReplicationService)

        when:
        servlet.doPost(request, response)

        then:
        thrown(IllegalArgumentException)

        where:
        paths        | agentIds    | action
        null         | null        | null
        []           | []          | ""
        ["/content"] | ["publish"] | ""
        []           | ["publish"] | ReplicationActionType.ACTIVATE.name()
        ["/content"] | []          | ReplicationActionType.ACTIVATE.name()
    }

    def "valid parameters"() {
        def request = requestBuilder.build {
            parameters paths: ["/content", "/etc"], agentIds: ["publish"], action: ReplicationActionType.ACTIVATE.name()
        }

        def response = responseBuilder.build()

        def servlet = new SelectiveReplicationServlet()

        def agent = Mock(Agent)
        def agentManager = Mock(AgentManager)
        def selectiveReplicationService = Mock(SelectiveReplicationService)

        servlet.agentManager = agentManager
        servlet.selectiveReplicationService = selectiveReplicationService

        def json = new JsonBuilder([["/content": true], ["/etc": true]]).toString()

        when:
        servlet.doPost(request, response)

        then:
        1 * agentManager.agents >> ["publish": agent]
        2 * selectiveReplicationService.replicate(*_)

        then:
        response.output == json
    }
}