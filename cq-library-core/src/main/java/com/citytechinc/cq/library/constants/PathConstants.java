/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.constants;

import com.day.cq.commons.jcr.JcrConstants;

import java.util.regex.Pattern;

public final class PathConstants {

    public static final String EXTENSION_HTML = "html";

    public static final String EXTENSION_JSON = "json";

    public static final String EXTENSION_PNG = "png";

    public static final String PATH_CONTENT = "/content";

    public static final String PATH_CONTENT_DAM_START = "/content/dam/";

    public static final String PATH_CONTENT_START = "/content/";

    public static final String PATH_ETC = "/etc";

    public static final String PATH_JCR_CONTENT = "/" + JcrConstants.JCR_CONTENT;

    public static final String PATH_SEPARATOR = "/";

    public static final String REGEX_CONTENT = "/content/([^/]+)/?(.+)?";

    public static final Pattern PATTERN_CONTENT = Pattern.compile(REGEX_CONTENT);

    public static final String SELECTOR = ".";

    private PathConstants() {

    }
}
