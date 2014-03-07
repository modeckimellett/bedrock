/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.replication

import com.citytechinc.aem.bedrock.testing.specs.BedrockSpec
import com.citytechinc.aem.bedrock.utils.PathUtils
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationStatus
import com.day.cq.replication.Replicator
import org.apache.sling.api.resource.Resource
import spock.lang.Shared

class PageReplicationListenerSpec extends BedrockSpec {

    static final def STATUS_ACTIVE = [isActivated: { true }] as ReplicationStatus

    static final def STATUS_INACTIVE = [isActivated: { false }] as ReplicationStatus

    @Shared listener

    @Override
    Map<Class, Closure> addResourceAdapters() {
        [(ReplicationStatus): { Resource resource ->
            def pagePath = PathUtils.getPagePath(resource.path)
            def status

            if (pagePath.startsWith("/content/home/active1")) {
                status = STATUS_ACTIVE
            } else {
                status = STATUS_INACTIVE
            }

            status
        }]
    }

    def setupSpec() {
        pageBuilder.content {
            home {
                active1 {
                    active2()
                }
                inactive1 {
                    inactive2()
                }
            }
        }
    }

    def setup() {
        listener = new PageReplicationListener()

        listener.session = session
        listener.pageManager = pageManager
    }

    def "handle activate for invalid path fails gracefully"() {
        when:
        listener.handleActivate("/content/invalid")

        then:
        notThrown(Exception)
    }

    def "handle activate for non-page path does nothing"() {
        when:
        listener.handleActivate("/")

        then:
        notThrown(Exception)
    }

    def "handle activate for page path activates ancestor pages"() {
        setup:
        def replicator = Mock(Replicator)

        listener.replicator = replicator

        when:
        listener.handleActivate("/content/home/inactive1/inactive2")

        then:
        1 * replicator.replicate(session, ReplicationActionType.ACTIVATE, "/content/home/inactive1")
        1 * replicator.replicate(session, ReplicationActionType.ACTIVATE, "/content/home")
    }

    def "handle activate for page path ignores already activated ancestor pages"() {
        setup:
        def replicator = Mock(Replicator)

        listener.replicator = replicator

        when:
        listener.handleActivate("/content/home/active1/active2")

        then:
        1 * replicator.replicate(session, ReplicationActionType.ACTIVATE, "/content/home")
    }
}
