package com.citytechinc.aem.bedrock.core.utils;

import com.citytechinc.aem.bedrock.core.constants.PathConstants;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.regex.Pattern;

import static com.citytechinc.aem.bedrock.core.constants.PathConstants.PATH_JCR_CONTENT;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * JCR path utilities.
 */
public final class PathUtils {

    /**
     * Deny outside instantiation.
     */
    private PathUtils() {

    }

    /**
     * Determine if the given path is a valid content path.
     *
     * @param path JCR path
     * @return true if path is a valid content path
     */
    public static boolean isContent(final String path) {
        return Pattern.matches(PathConstants.REGEX_CONTENT, checkNotNull(path));
    }

    /**
     * Determine if the given path is external (i.e. not a JCR path).
     *
     * @param path JCR path or external URL
     * @return true if path starts with "/", false otherwise
     */
    public static boolean isExternal(final String path) {
        return !checkNotNull(path).startsWith("/");
    }

    /**
     * Get the page path from a Sling request.
     *
     * @param request sling request
     * @return current page path
     */
    public static String getPagePath(final SlingHttpServletRequest request) {
        return getPagePath(checkNotNull(request).getResource().getPath());
    }

    /**
     * Get the page path from a JCR content path.
     *
     * @param path JCR content path
     * @return page path
     */
    public static String getPagePath(final String path) {
        return checkNotNull(path).contains(PATH_JCR_CONTENT) ? path.substring(0, path.indexOf(PATH_JCR_CONTENT)) : path;
    }
}
