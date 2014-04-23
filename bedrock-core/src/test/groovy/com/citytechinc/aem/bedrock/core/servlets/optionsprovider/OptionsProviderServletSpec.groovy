package com.citytechinc.aem.bedrock.core.servlets.optionsprovider

import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.google.common.base.Optional
import groovy.json.JsonBuilder

class OptionsProviderServletSpec extends BedrockSpec {

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
        response.contentAsString == new JsonBuilder([]).toString()
    }

    def "options with root"() {
        def servlet = new RootOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder(["root": LIST]).toString()
    }

    def "options with no root"() {
        def servlet = new NoRootOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder(LIST).toString()
    }
}