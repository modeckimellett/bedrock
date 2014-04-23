/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.servlets;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;

/**
 * Base servlet for writing a JSON response.
 */
public abstract class AbstractJsonResponseServlet extends SlingAllMethodsServlet {

    private static final JsonFactory FACTORY = new JsonFactory().disable(Feature.AUTO_CLOSE_TARGET);

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonResponseServlet.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm aaa z";

    private static final MediaType MEDIA_TYPE = MediaType.JSON_UTF_8;

    private static final String ENCODING = MEDIA_TYPE.charset().get().name();

    private static final String CONTENT_TYPE = MEDIA_TYPE.withoutParameters().toString();

    private static final long serialVersionUID = 1L;

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     * @throws IOException if error occurs writing JSON response
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object)
        throws IOException {
        writeJsonResponseInternal(response, object, false, DATE_FORMAT, Locale.US);
    }

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects using US locale
     * @throws IOException if error occurs writing JSON response
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object,
        final String dateFormat) throws IOException {
        writeJsonResponseInternal(response, object, false, dateFormat, Locale.US);
    }

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects
     * @param locale locale for date format
     * @throws IOException if error occurs writing JSON response
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object,
        final String dateFormat, final Locale locale) throws IOException {
        writeJsonResponseInternal(response, object, false, dateFormat, locale);
    }

    /**
     * Write an enum object to the response as JSON.
     *
     * @param response sling response
     * @param object enum object to be written as JSON
     * @throws IOException if error occurs writing JSON response
     */
    protected final void writeJsonResponseEnumStrings(final SlingHttpServletResponse response, final Object object)
        throws IOException {
        writeJsonResponseInternal(response, object, true, DATE_FORMAT, Locale.US);
    }

    /**
     * Write an enum object to the response as JSON.
     *
     * @param response sling response
     * @param object enum object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects using US locale
     * @throws IOException if error occurs writing JSON response
     */
    protected final void writeJsonResponseEnumStrings(final SlingHttpServletResponse response, final Object object,
        final String dateFormat) throws IOException {
        writeJsonResponseInternal(response, object, true, dateFormat, Locale.US);
    }

    /**
     * Write an enum object to the response as JSON.
     *
     * @param response sling response
     * @param object enum object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects
     * @param locale locale for date format
     * @throws IOException if error occurs writing JSON response
     */
    protected final void writeJsonResponseEnumStrings(final SlingHttpServletResponse response, final Object object,
        final String dateFormat, final Locale locale) throws IOException {
        writeJsonResponseInternal(response, object, true, dateFormat, locale);
    }

    private void writeJsonResponseInternal(final SlingHttpServletResponse response, final Object object,
        final boolean useStrings, final String dateFormat, final Locale locale) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(ENCODING);

        try {
            final JsonGenerator generator = FACTORY.createGenerator(response.getWriter());

            final SimpleDateFormat format = new SimpleDateFormat(dateFormat, locale);

            if (useStrings) {
                MAPPER.getSerializationConfig().with(format).with(WRITE_ENUMS_USING_TO_STRING);
            } else {
                MAPPER.getSerializationConfig().with(format).without(WRITE_ENUMS_USING_TO_STRING);
            }

            MAPPER.writeValue(generator, object);
        } catch (IOException e) {
            LOG.error("error writing JSON response", e);

            throw e;
        }
    }
}
