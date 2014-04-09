/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.servlets

import com.citytechinc.aem.bedrock.specs.BedrockSpec
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest

class AbstractValidatorServletSpec extends BedrockSpec {

    class ValidatorServlet extends AbstractValidatorServlet {

        @Override
        protected boolean isValid(SlingHttpServletRequest request, String path, String value) {
            false
        }
    }

    def setupSpec() {
        pageBuilder.content {
            home()
        }
    }

    def "exception thrown when value parameter is null"() {
        setup:
        def servlet = new ValidatorServlet()

        def request = requestBuilder.build {
            path = "/content/home"
        }

        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        thrown(NullPointerException)
    }

    def "validate method called with correct arguments"() {
        setup:
        def servlet = Spy(ValidatorServlet) {
            1 * isValid(_, "/content/home", "foo")
        }

        def request = requestBuilder.build {
            path = "/content/home"
            parameters = ["value": "foo"]
        }

        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder([valid: false]).toString()
    }
}
