/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.link.builders

import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.content.page.enums.TitleType
import com.citytechinc.aem.bedrock.testing.specs.BedrockSpec
import com.day.cq.wcm.api.NameConstants
import spock.lang.Unroll

@Unroll
class LinkBuilderSpec extends BedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            global("Global") {
                "jcr:content"(navTitle: "Global Navigation")
            }
            us("US") {
                "jcr:content"()
            }
            de("DE") {
                "jcr:content"(redirectTarget: "/content/global")
            }
        }

        session.getNode("/content").addNode("se", NameConstants.NT_PAGE)
        session.save()
    }

    def "build link for existing link"() {
        setup:
        def link = LinkBuilder.forPath("/content/global").build()

        expect:
        LinkBuilder.forLink(link).build() == link
    }

    def "build link for page"() {
        setup:
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage("/content/global")
        def link = LinkBuilder.forPage(page).build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global.html"
        link.extension == "html"
        link.title == "Global"
    }

    def "build link for page with no jcr:content node"() {
        setup:
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage("/content/se")
        def link = LinkBuilder.forPage(page).build()

        expect:
        link.path == "/content/se"
        link.href == "/content/se.html"
        link.extension == "html"
        link.title == ""
    }

    def "build link for page with redirect"() {
        setup:
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage("/content/de")
        def link = LinkBuilder.forPage(page).build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global.html"
        link.extension == "html"
        link.title == "DE"
    }

    def "build link for page with navigation title"() {
        setup:
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage("/content/global")
        def link = LinkBuilder.forPage(page, TitleType.NAVIGATION_TITLE).build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global.html"
        link.extension == "html"
        link.title == "Global Navigation"
    }

    def "build link for page without navigation title"() {
        setup:
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage("/content/us")
        def link = LinkBuilder.forPage(page, TitleType.NAVIGATION_TITLE).build()

        expect:
        link.path == "/content/us"
        link.href == "/content/us.html"
        link.extension == "html"
        link.title == "US"
    }

    def "build link for path"() {
        setup:
        def builder = LinkBuilder.forPath("/content")

        builder.extension = extension
        builder.suffix = suffix
        builder.host = host
        builder.port = port
        builder.secure = secure

        def link = builder.build()

        expect:
        link.href == href

        where:
        extension | suffix    | host        | port | secure | href
        null      | ""        | "localhost" | 0    | false  | "http://localhost/content.html"
        null      | "/suffix" | "localhost" | 0    | false  | "http://localhost/content.html/suffix"
        ""        | ""        | "localhost" | 0    | false  | "http://localhost/content"
        ""        | "/suffix" | "localhost" | 0    | false  | "http://localhost/content/suffix"
        "html"    | ""        | "localhost" | 0    | false  | "http://localhost/content.html"
        "html"    | "/suffix" | "localhost" | 0    | false  | "http://localhost/content.html/suffix"
        "json"    | ""        | "localhost" | 0    | false  | "http://localhost/content.json"
        null      | ""        | "localhost" | 4502 | false  | "http://localhost:4502/content.html"
        null      | ""        | "localhost" | 0    | true   | "https://localhost/content.html"
    }

    def "build link for strict path"() {
        setup:
        def builder = LinkBuilder.forPath(resourceResolver, path)

        expect:
        builder.href == href

        where:
        path                          | href
        "/content/global"             | "/content/global.html"
        "/content/global/jcr:content" | "/content/global/jcr:content.html"
        "/content/page"               | "/content/page"
        "etc"                         | "etc"
        "/webapp"                     | "/webapp"
    }

    def "build link for path with selectors"() {
        setup:
        def link = LinkBuilder.forPath(path).addSelectors(selectors).build()

        expect:
        link.href == href

        where:
        path                    | selectors  | href
        "/content"              | []         | "/content.html"
        "/content"              | ["a"]      | "/content.a.html"
        "/content"              | ["a", "b"] | "/content.a.b.html"
        "http://www.reddit.com" | ["a", "b"] | "http://www.reddit.com"
    }

    def "build link for path with parameters"() {
        setup:
        def link = LinkBuilder.forPath("/content").addParameters(parameters).build()

        expect:
        link.href == href
        link.queryString == queryString

        where:
        parameters           | href                    | queryString
        [:]                  | "/content.html"         | ""
        ["a": "1"]           | "/content.html?a=1"     | "?a=1"
        ["a": "1", "b": "2"] | "/content.html?a=1&b=2" | "?a=1&b=2"
    }

    def "build link for path with same-name parameters"() {
        setup:
        def builder = LinkBuilder.forPath("/content")

        when:
        builder.addParameter("a", "1")
        builder.addParameter("a", "2")
        builder.addParameter("a", "3")

        then:
        def link = builder.build()

        expect:
        link.queryString == "?a=1&a=2&a=3"
    }

    def "build image link"() {
        setup:
        def imageLink = LinkBuilder.forPath("/content/global").setImageSource(imageSource).buildImageLink()

        expect:
        imageLink.imageSource == imageSource

        where:
        imageSource << ["abc.png", ""]
    }

    def "build navigation link without children"() {
        setup:
        def navigationLink = LinkBuilder.forPath("/content/global").buildNavigationLink()

        expect:
        !navigationLink.children
    }

    def "build navigation link without children with active state"() {
        setup:
        def navigationLink = LinkBuilder.forPath("/content/global").setActive(active).buildNavigationLink()

        expect:
        navigationLink.active == active

        where:
        active << [true, false]
    }

    def "build navigation link with children"() {
        setup:
        def builder = LinkBuilder.forPath("/content/global")

        builder.addChild(LinkBuilder.forPath("/content/1").buildNavigationLink())
        builder.addChild(LinkBuilder.forPath("/content/2").buildNavigationLink())
        builder.addChild(LinkBuilder.forPath("/content/3").buildNavigationLink())

        def navigationLink = builder.buildNavigationLink()

        expect:
        navigationLink.children.size() == 3
        navigationLink.children*.path == ["/content/1", "/content/2", "/content/3"]
    }
}