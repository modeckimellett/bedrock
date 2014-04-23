package com.citytechinc.aem.bedrock.core.request.impl

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import org.apache.sling.api.SlingHttpServletResponse
import spock.lang.Unroll

@Unroll
class DefaultComponentServletRequestSpec extends BedrockSpec {

    def "get selectors"() {
        setup:
        def slingRequest = requestBuilder.build {
            selectors = selectorsList
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.selectors == selectorsList

        where:
        selectorsList << [["a", "b"], ["a"], []]
    }

    def "get request parameter optional"() {
        setup:
        def slingRequest = requestBuilder.build {
            parameters = ["a": ["1", "2"], "b": [""]]
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

    def "get request parameter"() {
        setup:
        def slingRequest = requestBuilder.build {
            parameters = ["a": ["1", "2"], "b": [""]]
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

    def "get request parameter with default value"() {
        setup:
        def slingRequest = requestBuilder.build {
            parameters = ["a": ["1", "2"], "b": [""]]
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

    def "get request parameters optional"() {
        setup:
        def slingRequest = requestBuilder.build {
            parameters = ["a": ["1", "2"], "b": ["1"]]
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

    def "get request parameters"() {
        setup:
        def slingRequest = requestBuilder.build {
            parameters = ["a": ["1", "2"], "b": ["1"]]
        }

        def slingResponse = Mock(SlingHttpServletResponse)

        def request = new DefaultComponentServletRequest(slingRequest, slingResponse)

        expect:
        request.getRequestParameters(parameterName).get() == parameterValues

        where:
        parameterName | parameterValues
        "a"           | ["1", "2"]
        "b"           | ["1"]
    }
}
