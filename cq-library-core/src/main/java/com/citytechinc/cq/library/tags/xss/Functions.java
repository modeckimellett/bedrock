/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;

public final class Functions {

    public static String encodeForHTMLAttr(final XSSAPI xssApi, final String source) {
        return xssApi.encodeForHTMLAttr(source);
    }

    public static String encodeForHTML(final XSSAPI xssApi, final String source) {
        return xssApi.encodeForHTML(source);
    }

    public static String encodeForJSString(final XSSAPI xssApi, final String source) {
        return xssApi.encodeForJSString(source);
    }

    public static String encodeForXMLAttr(final XSSAPI xssApi, final String source) {
        return xssApi.encodeForXMLAttr(source);
    }

    public static String encodeForXML(final XSSAPI xssApi, final String source) {
        return xssApi.encodeForXML(source);
    }

    public static String filterHTML(final XSSAPI xssApi, final String source) {
        return xssApi.filterHTML(source);
    }

    public static String validDimension(final XSSAPI xssApi, final String dimension, final String defaultValue) {
        return xssApi.getValidDimension(dimension, defaultValue);
    }

    public static String validHref(final XSSAPI xssApi, final String url) {
        return xssApi.getValidHref(url);
    }

    public static int validInteger(final XSSAPI xssApi, final String integer, final int defaultValue) {
        return xssApi.getValidInteger(integer, defaultValue);
    }

    public static String validJSToken(final XSSAPI xssApi, final String token, final String defaultValue) {
        return xssApi.getValidJSToken(token, defaultValue);
    }

    private Functions() {

    }
}
