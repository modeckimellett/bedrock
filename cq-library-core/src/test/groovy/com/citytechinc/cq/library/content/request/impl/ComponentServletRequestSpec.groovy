/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.request.impl

import com.citytechinc.cq.library.testing.specs.AbstractCqSpec
import org.apache.sling.api.SlingHttpServletResponse
import spock.lang.Unroll

class ComponentServletRequestSpec extends AbstractCqSpec {

    @Unroll
    def "get request parameter optional"() {
        setup:
        def slingRequest = getRequestBuilder().build {
            parameters "a": ["1", "2"], "b": [""]
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.getRequestParameter(parameterName).present == isPresent

        where:
        parameterName | isPresent
        "a"           | true
        "b"           | true
        "c"           | false
    }

    @Unroll
    def "get request parameter"() {
        setup:
        def slingRequest = getRequestBuilder().build {
            parameters "a": ["1", "2"], "b": [""]
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.getRequestParameter(parameterName).get() == parameterValue

        where:
        parameterName | parameterValue
        "a"           | "1"
        "b"           | ""
    }

    @Unroll
    def "get request parameter with default value"() {
        setup:
        def slingRequest = getRequestBuilder().build {
            parameters "a": ["1", "2"], "b": [""]
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.getRequestParameter(parameterName, "default") == parameterValue

        where:
        parameterName | parameterValue
        "a"           | "1"
        "b"           | ""
        "c"           | "default"
    }

    @Unroll
    def "get request parameters optional"() {
        setup:
        def slingRequest = getRequestBuilder().build {
            parameters "a": ["1", "2"], "b": ["1"]
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.getRequestParameters(parameterName).present == isPresent

        where:
        parameterName | isPresent
        "a"           | true
        "b"           | true
        "c"           | false
    }

    @Unroll
    def "get request parameters"() {
        setup:
        def slingRequest = getRequestBuilder().build {
            parameters "a": ["1", "2"], "b": ["1"]
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.getRequestParameters(parameterName).get() as List == parameterValues

        where:
        parameterName | parameterValues
        "a"           | ["1", "2"]
        "b"           | ["1"]
    }
}
