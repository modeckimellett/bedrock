package com.citytechinc.aem.bedrock.core.servlets

import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import groovy.json.JsonBuilder

import javax.servlet.ServletException

class AbstractComponentServletSpec extends BedrockSpec {

    static final def MAP = [one: 1, two: 2]

    class ComponentServlet extends AbstractComponentServlet {

        @Override
        protected void processGet(ComponentServletRequest request) throws ServletException, IOException {
            new JsonBuilder(MAP).writeTo(request.slingResponse.writer)
        }
    }

    def "process get"() {
        setup:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        new ComponentServlet().doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder(MAP).toString()
    }
}
