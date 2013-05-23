/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page.impl

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.page.PageManagerDecorator
import com.day.cq.tagging.TagConstants
import spock.lang.Shared

import javax.jcr.query.Query

class DefaultPageManagerDecoratorSpec extends AbstractCqLibrarySpec {

    @Shared pageManager

    def setupSpec() {
        nodeBuilder.etc {
            tags {
                tag1("cq:Tag")
                tag2("cq:Tag")
                tag3("cq:Tag")
            }
        }

        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb") {
                    component {
                        one("sling:resourceType": "one", "cq:tags": "/etc/tags/tag3")
                        two("sling:resourceType": "two")
                    }
                }
                child {
                    "jcr:content"(hideInNav: true, "cq:template": "template", "cq:tags": "/etc/tags/tag1")
                }
            }
            other {
                "jcr:content"("cq:template": "template", "cq:tags": ["/etc/tags/tag1", "/etc/tags/tag2"].toArray(new String[0]))
            }
        }

        def taggableNodePaths = ["/content/citytechinc/child/jcr:content", "/content/other/jcr:content", "/content/citytechinc/jcr:content/component/one"]

        taggableNodePaths.each { path ->
            session.getNode(path).addMixin(TagConstants.NT_TAGGABLE)
        }

        session.save()

        pageManager = resourceResolver.adaptTo(PageManagerDecorator)
    }

    def "find pages for tag IDs"() {
        expect:
        pageManager.findPages("/content", ["/etc/tags/tag1"], true).size() == 2
    }

    def "find pages for tag IDs matching all"() {
        expect:
        pageManager.findPages("/content", ["/etc/tags/tag1", "/etc/tags/tag2"], false).size() == 1
    }

    def "tagged non-page node is excluded from search results"() {
        expect:
        !pageManager.findPages("/content", ["/etc/tags/tag3"], true)
    }

    def "search"() {
        setup:
        def statement = "/jcr:root/content//element(*, cq:Page) order by @jcr:score descending"
        def query = session.workspace.queryManager.createQuery(statement, Query.XPATH)

        expect:
        pageManager.search(query).size() == 3
    }

    def "search with limit"() {
        setup:
        def statement = "/jcr:root/content//element(*, cq:Page) order by @jcr:score descending"
        def query = session.workspace.queryManager.createQuery(statement, Query.XPATH)

        expect:
        pageManager.search(query, 1).size() == 1
    }

    def "find pages for template"() {
        expect:
        pageManager.findPages("/content", "template").size() == 2
    }

    def "find pages for non-existing template"() {
        expect:
        !pageManager.findPages("/content", "ghost")
    }

    def "find pages for template with invalid starting path"() {
        expect:
        !pageManager.findPages("/etc", "template")
    }
}
