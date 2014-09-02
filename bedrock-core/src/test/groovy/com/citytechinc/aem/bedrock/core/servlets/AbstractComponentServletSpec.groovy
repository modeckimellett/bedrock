package com.citytechinc.aem.bedrock.core.servlets

import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.core.components.TestComponent
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.day.cq.wcm.api.designer.Designer
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

    class TestComponentServlet extends AbstractComponentServlet {

        @Override
        protected void processGet(ComponentServletRequest request) throws ServletException, IOException {
            def component = getComponent(request, TestComponent)

            new JsonBuilder([title: component.title]).writeTo(request.slingResponse.writer)
        }
    }

    @Override
    Map<Class, Closure> addResourceResolverAdapters() {
        [(Designer): {
            [getDesign: { null }] as Designer
        }]
    }

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
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

    def "get component"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/citytechinc/jcr:content/component"
        }

        def response = responseBuilder.build()

        when:
        new TestComponentServlet().doGet(request, response)

        then:
        response.contentAsString == new JsonBuilder([title: "Testing Component"]).toString()
    }
}
