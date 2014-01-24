/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets.replication

import com.citytechinc.cq.library.content.request.ComponentServletRequest
import com.citytechinc.cq.library.servlets.optionsprovider.AbstractOptionsProviderServlet
import com.citytechinc.cq.library.servlets.optionsprovider.Option
import com.citytechinc.cq.library.testing.specs.AbstractCqSpec
import com.google.common.base.Optional
import groovy.json.JsonBuilder

class OptionsProviderServletSpec extends AbstractCqSpec {

    static final def MAP = ["one": "One", "two": "Two"]

    static final def LIST = MAP.collect { key, value -> [text: value, value: key] }

    static final def OPTIONS = Option.fromMap(MAP)

    class NoOptionsProviderServlet extends AbstractOptionsProviderServlet {

        @Override
        List<Option> getOptions(ComponentServletRequest request) {
            []
        }

        @Override
        Optional<String> getOptionsRoot(ComponentServletRequest request) {
            Optional.absent()
        }
    }

    class RootOptionsProviderServlet extends AbstractOptionsProviderServlet {

        @Override
        List<Option> getOptions(ComponentServletRequest request) {
            OPTIONS
        }

        @Override
        Optional<String> getOptionsRoot(ComponentServletRequest request) {
            Optional.of("root")
        }
    }

    class NoRootOptionsProviderServlet extends AbstractOptionsProviderServlet {

        @Override
        List<Option> getOptions(ComponentServletRequest request) {
            OPTIONS
        }

        @Override
        Optional<String> getOptionsRoot(ComponentServletRequest request) {
            Optional.absent()
        }
    }

    def "no options"() {
        def servlet = new NoOptionsProviderServlet()

        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.output == new JsonBuilder([]).toString()
    }

    def "options with root"() {
        def servlet = new RootOptionsProviderServlet()

        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.output == new JsonBuilder(["root": LIST]).toString()
    }

    def "options with no root"() {
        def servlet = new NoRootOptionsProviderServlet()

        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.output == new JsonBuilder(LIST).toString()
    }
}