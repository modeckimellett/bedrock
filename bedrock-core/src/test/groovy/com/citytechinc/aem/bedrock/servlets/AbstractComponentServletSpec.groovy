/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.servlets

import com.citytechinc.aem.bedrock.content.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.specs.BedrockSpec
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
