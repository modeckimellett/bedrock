package com.citytechinc.aem.bedrock.core.page.impl

import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.page.predicates.TemplatePredicate
import com.citytechinc.aem.bedrock.core.utils.PathUtils
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMException
import com.google.common.base.Predicate
import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

import javax.jcr.RepositoryException
import javax.jcr.query.Query
import javax.jcr.query.Row

import static com.google.common.base.Preconditions.checkNotNull
import static java.util.concurrent.TimeUnit.MILLISECONDS

@Slf4j("LOG")
class DefaultPageManagerDecorator implements PageManagerDecorator {

    private final ResourceResolver resourceResolver

    @Delegate
    private final PageManager pageManager

    DefaultPageManagerDecorator(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver

        pageManager = resourceResolver.adaptTo(PageManager)
    }

    @Override
    List<PageDecorator> findPages(String rootPath, Collection<String> tagIds, boolean matchOne) {
        checkNotNull(rootPath)
        checkNotNull(tagIds)

        LOG.debug("path = {}, tag IDs = {}", rootPath, tagIds)

        def stopwatch = Stopwatch.createStarted()

        def iterator = resourceResolver.adaptTo(TagManager).find(rootPath, tagIds as String[], matchOne)

        def pages = []

        iterator*.each { resource ->
            if (JcrConstants.JCR_CONTENT.equals(resource.name)) {
                def page = getPage(resource.parent.path)

                if (page) {
                    pages.add(page)
                }
            }
        }

        stopwatch.stop()

        LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS))

        pages
    }

    @Override
    List<PageDecorator> search(Query query) {
        search(query, -1)
    }

    @Override
    List<PageDecorator> search(Query query, int limit) {
        checkNotNull(query)

        LOG.debug("query statement = {}", query.statement)

        def stopwatch = Stopwatch.createStarted()

        def pages = []

        int count = 0

        try {
            def rows = query.execute().rows
            def paths = [] as Set

            rows.each { Row row ->
                if (limit == -1 || count < limit) {
                    def path = row.path

                    LOG.debug("result path = {}", path)

                    def pagePath = PathUtils.getPagePath(path)

                    // ensure no duplicate pages are added
                    if (!paths.contains(pagePath)) {
                        paths.add(pagePath)

                        def page = getPageDecorator(path)

                        if (page) {
                            pages.add(page)
                            count++
                        } else {
                            LOG.error("result is null for path = {}", path)
                        }
                    }
                }
            }

            stopwatch.stop()

            LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS))
        } catch (RepositoryException re) {
            LOG.error("error finding pages for query = ${query.statement}", re)
        }

        pages
    }

    @Override
    List<PageDecorator> findPages(String rootPath, String template) {
        findPages(rootPath, new TemplatePredicate(template))
    }

    @Override
    List<PageDecorator> findPages(String rootPath, Predicate<PageDecorator> predicate) {
        def page = getPage(checkNotNull(rootPath))

        def stopwatch = Stopwatch.createStarted()

        def result = page ? page.findDescendants(predicate) : Collections.emptyList()

        stopwatch.stop()

        LOG.debug("found {} result(s) in {}ms", result.size(), stopwatch.elapsed(MILLISECONDS))

        result
    }

    @Override
    PageDecorator copy(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict) throws WCMException {
        decorate(pageManager.copy(page, destination, beforeName, shallow, resolveConflict))
    }

    @Override
    PageDecorator copy(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict, boolean autoSave) throws WCMException {
        decorate(pageManager.copy(page, destination, beforeName, shallow, resolveConflict, autoSave))
    }

    @Override
    PageDecorator create(String parentPath, String pageName, String template,
        String title) throws WCMException {
        decorate(pageManager.create(parentPath, pageName, template, title))
    }

    @Override
    PageDecorator create(String parentPath, String pageName, String template,
        String title, boolean autoSave) throws WCMException {
        decorate(pageManager.create(parentPath, pageName, template, title, autoSave))
    }

    @Override
    PageDecorator getContainingPage(Resource resource) {
        decorate(pageManager.getContainingPage(resource))
    }

    @Override
    PageDecorator getContainingPage(String path) {
        decorate(pageManager.getContainingPage(path))
    }

    @Override
    PageDecorator getPage(Page page) {
        decorate(page)
    }

    @Override
    PageDecorator getPage(String path) {
        getPageDecorator(checkNotNull(path))
    }

    @Override
    PageDecorator move(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict, String[] adjustRefs) throws WCMException {
        decorate(pageManager.move(page, destination, beforeName, shallow, resolveConflict, adjustRefs))
    }

    @Override
    PageDecorator restore(String path, String revisionId) throws WCMException {
        decorate(pageManager.restore(path, revisionId))
    }

    @Override
    PageDecorator restoreTree(String path, Calendar date) throws WCMException {
        decorate(pageManager.restoreTree(path, date))
    }

    // internals

    private PageDecorator getPageDecorator(String path) {
        decorate(pageManager.getPage(path))
    }

    private static PageDecorator decorate(Page page) {
        page ? new DefaultPageDecorator(page) : null
    }
}
