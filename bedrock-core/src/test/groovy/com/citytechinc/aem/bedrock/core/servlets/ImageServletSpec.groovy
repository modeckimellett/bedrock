package com.citytechinc.aem.bedrock.core.servlets

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.day.cq.wcm.commons.AbstractImageServlet
import com.day.image.Layer
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

@Unroll
class ImageServletSpec extends BedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            home {
                "jcr:content"() {
                    component()
                }
            }
        }
    }

    def "create layer returns null"() {
        setup:
        def servlet = new ImageServlet()

        expect:
        !servlet.createLayer(null)
    }

    def "image wrapper"() {
        setup:
        def request = requestBuilder.build {
            path = requestPath
            selectors = requestSelectors
        }

        def wrapper = new ImageServlet.ImageWrapper(request)

        expect:
        wrapper.width == width
        wrapper.name == name

        where:
        requestPath                           | requestSelectors       | width | name
        "/content/home"                       | ["img"]                | -1    | "image"
        "/content/home/jcr:content"           | ["img"]                | -1    | "image"
        "/content/home/jcr:content"           | ["img", "200"]         | 200   | "image"
        "/content/home/jcr:content"           | ["img", "meme", "200"] | 200   | "meme"
        "/content/home/jcr:content/component" | ["img"]                | -1    | null
        "/content/home/jcr:content/component" | ["img", "200"]         | 200   | null
        "/content/home/jcr:content/component" | ["img", "meme", "200"] | 200   | "meme"
    }

    def "write layer for page, no selectors"() {
        setup:
        def servlet = new ImageServlet()

        def request = requestBuilder.build {
            path = requestPath
        }

        def response = responseBuilder.build()

        def imageContext = Mock(AbstractImageServlet.ImageContext)
        def layer = Mock(Layer)

        when:
        servlet.writeLayer(request, response, imageContext, layer)

        then:
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        requestPath << ["/content/home", "/content/home/jcr:content", "/content/home/jcr:content/component"]
    }
}
