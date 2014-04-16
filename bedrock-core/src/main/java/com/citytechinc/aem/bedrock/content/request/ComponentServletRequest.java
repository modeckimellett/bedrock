/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.request;

import com.day.cq.wcm.api.WCMMode;
import com.google.common.base.Optional;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import java.util.List;

/**
 * Request facade for use in CQ servlets and component beans.
 */
public interface ComponentServletRequest extends ComponentResourceRequest {

    /**
     * Retrieve a parameter from the request or return an absent <code>Optional</code> if it does not exist.
     *
     * @param parameterName request parameter name
     * @return Optional parameter value
     */
    Optional<String> getRequestParameter(String parameterName);

    /**
     * Retrieve parameters from the request or return an absent <code>Optional</code> if they do not exist.
     *
     * @param parameterName request parameter name
     * @return Optional parameter values
     */
    Optional<List<String>> getRequestParameters(String parameterName);

    /**
     * Retrieve a parameter value from the request or return a default value if the parameter does not exist. If the
     * parameter does not exist and the default value is null, the empty string is returned.
     *
     * @param parameterName request parameter name
     * @param defaultValue String of the default value to return if the parameter does not exist.
     * @return String of the parameter or the default value if it doesn't exist. If the default value is null, return
     *         the empty string.
     */
    String getRequestParameter(String parameterName, String defaultValue);

    /**
     * @return Sling request selectors or empty array if the request has no selectors
     */
    List<String> getSelectors();

    /**
     * @return Sling servlet request
     */
    SlingHttpServletRequest getSlingRequest();

    /**
     * @return Sling servlet response
     */
    SlingHttpServletResponse getSlingResponse();

    /**
     * @return current WCM mode for this request
     */
    WCMMode getWCMMode();
}
