/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets.paragraphs;

import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.request.ComponentServletRequest;
import com.citytechinc.cq.library.servlets.AbstractComponentServlet;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Retrieves HTML for all contained components on a Page.  This differs from the OOB Paragraph servlet,
 * com.day.cq.wcm.foundation.ParagraphServlet, in that the OOB servlet only pulls content from top level containers
 * while this servlet will pull content from any container on the Page regardless of depth of nesting.  Concretely, if
 * your page has nested paragraph systems, the OOB paragraph servlet will not recognize and collect components within
 * the nested paragraph systems while this servlet will.
 * <p/>
 * This Servlet is suitable for overriding the OOB behavior of xtypes such as paragraphreference, causing it to pull all
 * components on a page as opposed to the top level components.
 */
@SlingServlet(resourceTypes = { NameConstants.NT_PAGE }, selectors = { ParagraphJsonServlet.SELECTOR },
    extensions = { ParagraphJsonServlet.EXTENSION }, methods = { "GET" })
public final class ParagraphJsonServlet extends AbstractComponentServlet {

    public static final String SELECTOR = "ctparagraphs";

    public static final String EXTENSION = "json";

    private static final long serialVersionUID = 1L;

    private static final String CONTAINER_COMPONENTS_FROM_LIBS_XPATH_QUERY = "/jcr:root/libs//element(*,cq:Component)[@cq:isContainer='true']";

    private static final String CONTAINER_COMPONENTS_FROM_APPS_XPATH_QUERY = "/jcr:root/apps//element(*,cq:Component)[@cq:isContainer='true']";

    private static final String LIBS_PATH_STRING = "/libs/";

    private static final String APPS_PATH_STRING = "/apps/";

    private static final Logger LOG = LoggerFactory.getLogger(ParagraphJsonServlet.class);

    @Override
    public void processGet(final ComponentServletRequest request) throws ServletException, IOException {
        final SlingHttpServletRequest slingRequest = request.getSlingRequest();
        final SlingHttpServletResponse slingResponse = request.getSlingResponse();

        WCMMode.DISABLED.toRequest(slingRequest);

        try {
            final List<Paragraph> paragraphs = getParagraphs(request);

            if (paragraphs.isEmpty()) {
                LOG.debug("{} Paragraphs found on page", paragraphs.size());

                writeJsonResponse(slingResponse, ImmutableMap.of("paragraphs", paragraphs));
            } else {
                LOG.info("Null returned indicating a lack of page or a lack of content");
                slingResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (RepositoryException e) {
            LOG.error("RepositoryException hit when requesting paragraph HTML for contained components", e);
            slingResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void processPost(final ComponentServletRequest request) throws ServletException, IOException {
        // nothing to do
    }

    /**
     * @param request
     * @return A List of Paragraphs representing all components which exist within containers under a given Page
     * @throws RepositoryException
     * @throws ServletException
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    private List<Paragraph> getParagraphs(final ComponentServletRequest request)
        throws RepositoryException, ServletException, IOException {
        /*
         * Request the current page
         */
        final PageDecorator currentPage = request.getCurrentPage();

        if (currentPage == null) {
            LOG.info("The request is not within the context of a Page");
            return null;
        }

        /*
         * Request the page's content node
         */
        final Resource pageContentResource = currentPage.getContentResource();

        if (pageContentResource == null) {
            LOG.info("The requested resource does not have a child content resource");
            return null;
        }

        /*
         * Get a handle to the query manager
         */
        final Session session = request.getResourceResolver().adaptTo(Session.class);

        final QueryManager queryManager = session.getWorkspace().getQueryManager();

        /*
         * Find all container components which are children of the requested resources content node
         */
        final Set<String> containerComponentResourceTypes = getContainerComponents(queryManager);

        /*
         * Construct a query which will request all resources under the current page's content
         * node which are of a type indicated to be a container via the cq:isContainer property.
         */
        final List<String> resourceTypeAttributeQueryStrings = new ArrayList<String>();

        for (final String curContainerResourceType : containerComponentResourceTypes) {
            resourceTypeAttributeQueryStrings.add("@sling:resourceType='" + curContainerResourceType + "'");
        }

        final String resourceTypeAttributeQueryString = StringUtils.join(resourceTypeAttributeQueryStrings, " or ");

        final String resourceQueryString = "/jcr:root" + pageContentResource
            .getPath() + "//element(*,nt:unstructured)[" + resourceTypeAttributeQueryString + "]";

        LOG.debug("Resource Query String: {}", resourceQueryString);

        /*
         * Execute the query
         */
        final Query resourceQuery = queryManager.createQuery(resourceQueryString, Query.XPATH);

        final QueryResult resourceQueryResult = resourceQuery.execute();
        final NodeIterator resourceQueryResultIterator = resourceQueryResult.getNodes();

        final List<Paragraph> retList = new ArrayList<Paragraph>();

        /*
         * Go through the direct children of each container resource, adding them to the
         * final list of Paragraphs
         */
        while (resourceQueryResultIterator.hasNext()) {
            retList.addAll(getChildParagraphs(request, resourceQueryResultIterator.nextNode().getPath(),
                containerComponentResourceTypes));
        }

        /*
         * Return the list of paragraphs
         */
        return retList;
    }

    /**
     * @param request
     * @param parentPath
     * @param containerResourceTypes
     * @return A list of Paragraphs representing the non-container resources which are direct children of the resource
     *         indicated by the parentPath
     * @throws ServletException
     * @throws IOException
     */
    private List<Paragraph> getChildParagraphs(final ComponentServletRequest request, final String parentPath,
        final Set<String> containerResourceTypes) throws ServletException, IOException {

        final List<Paragraph> retList = new ArrayList<Paragraph>();

        final Resource parentResource = request.getResourceResolver().getResource(parentPath);

        if (parentResource != null) {
            final Iterator<Resource> childResources = parentResource.listChildren();

            while (childResources.hasNext()) {
                final Resource curResource = childResources.next();

                if (!containerResourceTypes.contains(curResource.getResourceType())) {
                    retList.add(new Paragraph(curResource.getPath(), renderResourceHtml(curResource,
                        request.getSlingRequest(), request.getSlingResponse())));
                }
            }
        }

        return retList;
    }

    @SuppressWarnings("deprecation")
    private Set<String> getContainerComponents(final QueryManager queryManager) throws RepositoryException {
        final Set<String> containerComponentSet = new HashSet<String>();

        final Query containerComponentsFromLibsQuery = queryManager.createQuery(
            CONTAINER_COMPONENTS_FROM_LIBS_XPATH_QUERY, Query.XPATH);
        final Query containerComponentsFromAppsQuery = queryManager.createQuery(
            CONTAINER_COMPONENTS_FROM_APPS_XPATH_QUERY, Query.XPATH);

        final QueryResult containerComponentsFromLibsQueryResult = containerComponentsFromLibsQuery.execute();
        final QueryResult containerComponentsFromAppsQueryResult = containerComponentsFromAppsQuery.execute();

        final NodeIterator containerComponentsFromLibsQueryResultIterator = containerComponentsFromLibsQueryResult
            .getNodes();
        final NodeIterator containerComponentsFromAppsQueryResultIterator = containerComponentsFromAppsQueryResult
            .getNodes();

        LOG.debug("Query execution complete");

        while (containerComponentsFromLibsQueryResultIterator.hasNext()) {
            final Node curContainerComponentNode = containerComponentsFromLibsQueryResultIterator.nextNode();

            final String curNodePath = curContainerComponentNode.getPath();

            LOG.debug("Adding {} from libs as a container resource type", curNodePath);

            if (curNodePath.startsWith(LIBS_PATH_STRING)) {
                containerComponentSet.add(curNodePath.substring(LIBS_PATH_STRING.length()));
            } else {
                containerComponentSet.add(curContainerComponentNode.getPath());
            }
        }

        while (containerComponentsFromAppsQueryResultIterator.hasNext()) {
            final Node curContainerComponentNode = containerComponentsFromAppsQueryResultIterator.nextNode();

            final String curNodePath = curContainerComponentNode.getPath();

            LOG.debug("Adding {} from apps as a container resource type", curNodePath);

            if (curNodePath.startsWith(APPS_PATH_STRING)) {
                containerComponentSet.add(curNodePath.substring(APPS_PATH_STRING.length()));
            } else {
                containerComponentSet.add(curContainerComponentNode.getPath());
            }
        }

        return containerComponentSet;
    }

    private String renderResourceHtml(final Resource resource, final SlingHttpServletRequest request,
        final SlingHttpServletResponse response) throws ServletException, IOException {
        final Writer outputBuffer = new StringWriter();

        final ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public void write(final int b) throws IOException {
                outputBuffer.append((char) b);
            }
        };

        final SlingHttpServletResponseWrapper responseWrapper = new SlingHttpServletResponseWrapper(response) {
            @Override
            public ServletOutputStream getOutputStream() {
                return outputStream;
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return new PrintWriter(outputBuffer);
            }
        };

        final RequestDispatcher requestDispatcher = request.getRequestDispatcher(resource.getPath() + ".html");

        requestDispatcher.include(request, responseWrapper);

        return outputBuffer.toString();
    }
}
