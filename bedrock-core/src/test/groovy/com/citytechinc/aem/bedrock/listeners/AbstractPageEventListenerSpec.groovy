/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.listeners

import com.citytechinc.aem.bedrock.testing.specs.BedrockSpec
import com.day.cq.wcm.api.NameConstants
import spock.lang.Shared

import javax.jcr.Session
import javax.jcr.observation.Event
import javax.jcr.observation.EventIterator

class AbstractPageEventListenerSpec extends BedrockSpec {

    static final def EVENT_PATHS = ["/content/home",
        "/content/home/jcr:content",
        "/content/about/jcr:content"]

    class TestPageEventListener extends AbstractPageEventListener {

        def paths = []

        TestPageEventListener(Session session) {
            super(session)
        }

        @Override
        void processPage(String path) {
            paths.add(path)
        }
    }

    class TestEventIterator implements EventIterator {

        @Delegate Iterator<Event> iterator

        @Override
        long getPosition() {
            0
        }

        @Override
        long getSize() {
            0
        }

        @Override
        void skip(long skipNum) {

        }

        @Override
        Event nextEvent() {
            if (!iterator.hasNext()) {
                throw new NoSuchElementException()
            }

            iterator.next()
        }
    }

    @Shared listener

    def setupSpec() {
        pageBuilder.content {
            home((NameConstants.NN_TEMPLATE): "template")
            about()
        }

        listener = new TestPageEventListener(session)
    }

    def "process page"() {
        setup:
        def iterator = EVENT_PATHS.collect { path ->
            Mock(Event) {
                1 * getPath() >> path
            }
        }.iterator()

        def events = new TestEventIterator(iterator: iterator)

        when:
        listener.onEvent(events)

        then:
        listener.paths == ["/content/home"]
    }
}