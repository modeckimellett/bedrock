/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
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

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonResponseServlet.class);

    private static final JsonFactory FACTORY = new JsonFactory().disable(Feature.AUTO_CLOSE_TARGET);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object) {
        writeJsonResponseWithEnums(response, object, false, Locale.US);
    }

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects using US locale
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object,
        final String dateFormat) {
        writeJsonResponseWithEnums(response, object, false, dateFormat, Locale.US);
    }

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects
     * @param locale locale for date format
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object,
        final String dateFormat, final Locale locale) {
        writeJsonResponseWithEnums(response, object, false, dateFormat, locale);
    }

    /**
     * Write an enum object to the response as JSON.
     *
     * @param response sling response
     * @param object enum object to be written as JSON
     */
    protected final void writeJsonResponseEnumStrings(final SlingHttpServletResponse response, final Object object) {
        writeJsonResponseWithEnums(response, object, true, Locale.US);
    }

    /**
     * Write an enum object to the response as JSON.
     *
     * @param response sling response
     * @param object enum object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects using US locale
     */
    protected final void writeJsonResponseEnumStrings(final SlingHttpServletResponse response, final Object object,
        final String dateFormat) {
        writeJsonResponseWithEnums(response, object, true, dateFormat, Locale.US);
    }

    /**
     * Write an enum object to the response as JSON.
     *
     * @param response sling response
     * @param object enum object to be written as JSON
     * @param dateFormat SimpleDateFormat pattern for formatting Date objects
     * @param locale locale for date format
     */
    protected final void writeJsonResponseEnumStrings(final SlingHttpServletResponse response, final Object object,
        final String dateFormat, final Locale locale) {
        writeJsonResponseWithEnums(response, object, true, dateFormat, locale);
    }

    private void writeJsonResponseWithEnums(final SlingHttpServletResponse response, final Object object,
        final boolean useStrings, final Locale locale) {
        writeJsonResponseWithEnums(response, object, useStrings, "MM/dd/yyyy hh:mm aaa z", locale);
    }

    private void writeJsonResponseWithEnums(final SlingHttpServletResponse response, final Object object,
        final boolean useStrings, final String dateFormat, final Locale locale) {
        final MediaType mediaType = MediaType.JSON_UTF_8;

        response.setContentType(mediaType.withoutParameters().toString());
        response.setCharacterEncoding(mediaType.charset().get().name());

        try {
            final JsonGenerator generator = FACTORY.createGenerator(response.getWriter());

            final SimpleDateFormat format = new SimpleDateFormat(dateFormat, locale);

            if (useStrings) {
                MAPPER.getSerializationConfig().with(format).with(WRITE_ENUMS_USING_TO_STRING);
            } else {
                MAPPER.getSerializationConfig().with(format).without(WRITE_ENUMS_USING_TO_STRING);
            }

            MAPPER.writeValue(generator, object);
        } catch (JsonGenerationException jge) {
            LOG.error("error generating JSON response", jge);
        } catch (JsonMappingException jme) {
            LOG.error("error mapping JSON response", jme);
        } catch (IOException ioe) {
            LOG.error("error writing JSON response", ioe);
        }
    }
}
