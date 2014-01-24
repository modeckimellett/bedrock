/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.enums

import spock.lang.Specification
import spock.lang.Unroll

class LinkTargetSpec extends Specification {

    def "for invalid target"() {
        when:
        LinkTarget.forTarget("")

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "for target"() {
        expect:
        LinkTarget.forTarget(target) == linkTarget

        where:
        target    | linkTarget
        "_none"   | LinkTarget.SELF
        "_self"   | LinkTarget.SELF
        "_blank"  | LinkTarget.BLANK
        "_parent" | LinkTarget.PARENT
        "_top"    | LinkTarget.TOP
    }
}
