/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.page.impl;

import com.citytechinc.aem.bedrock.content.link.ImageLink;
import com.citytechinc.aem.bedrock.content.link.Link;
import com.citytechinc.aem.bedrock.content.link.NavigationLink;
import com.citytechinc.aem.bedrock.content.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.content.node.BasicNode;
import com.citytechinc.aem.bedrock.content.node.ComponentNode;
import com.citytechinc.aem.bedrock.content.node.impl.DefaultComponentNode;
import com.citytechinc.aem.bedrock.content.node.predicates.ComponentNodePropertyExistsPredicate;
import com.citytechinc.aem.bedrock.content.node.predicates.ComponentNodePropertyValuePredicate;
import com.citytechinc.aem.bedrock.content.page.PageDecorator;
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator;
import com.citytechinc.aem.bedrock.content.page.enums.TitleType;
import com.day.cq.commons.Filter;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.citytechinc.aem.bedrock.content.node.impl.NodeFunctions.RESOURCE_TO_COMPONENT_NODE;
import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultPageDecorator implements PageDecorator {

    private static final Predicate<PageDecorator> ALL = Predicates.alwaysTrue();

    private static final Filter<Page> ALL_PAGES = new Filter<Page>() {
        @Override
        public boolean includes(final Page page) {
            return true;
        }
    };

    private static final Predicate<PageDecorator> DISPLAYABLE_ONLY = new Predicate<PageDecorator>() {
        @Override
        public boolean apply(final PageDecorator page) {
            return page.getContentResource() != null && !page.isHideInNav();
        }
    };

    private final Page delegate;

    private final Optional<ComponentNode> componentNodeOptional;

    public DefaultPageDecorator(final Page page) {
        this.delegate = page;

        componentNodeOptional = Optional.fromNullable(page.getContentResource()).transform(RESOURCE_TO_COMPONENT_NODE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        final AdapterType result;

        if (type == BasicNode.class || type == ComponentNode.class) {
            final Resource resource = delegate.getContentResource();

            result = resource == null ? null : (AdapterType) new DefaultComponentNode(resource);
        } else {
            result = delegate.adaptTo(type);
        }

        return result;
    }

    @Override
    public ValueMap asMap() {
        return componentNodeOptional.transform(new Function<ComponentNode, ValueMap>() {
            @Override
            public ValueMap apply(final ComponentNode componentNode) {
                return componentNode.asMap();
            }
        }).or(ValueMap.EMPTY);
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return componentNodeOptional.transform(new Function<ComponentNode, T>() {
            @Override
            public T apply(final ComponentNode componentNode) {
                return componentNode.get(propertyName, defaultValue);
            }
        }).or(defaultValue);
    }

    @Override
    public <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<T>>() {
            @Override
            public Optional<T> apply(final ComponentNode componentNode) {
                return componentNode.get(propertyName, type);
            }
        }).or(Optional.<T>absent());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHref(propertyName);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHref(propertyName, strict);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHref(propertyName, strict, mapped);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLink(propertyName);
            }
        }).or(Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLink(propertyName, strict);
            }
        }).or(Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLink(propertyName, strict, mapped);
            }
        }).or(Optional.<Link>absent());
    }

    @Override
    public <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return componentNodeOptional.transform(new Function<ComponentNode, List<T>>() {
            @Override
            public List<T> apply(final ComponentNode componentNode) {
                return componentNode.getAsList(propertyName, type);
            }
        }).or(Collections.<T>emptyList());
    }

    @Override
    public Optional<PageDecorator> getAsPage(final String propertyName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<PageDecorator>>() {
            @Override
            public Optional<PageDecorator> apply(final ComponentNode componentNode) {
                return componentNode.getAsPage(propertyName);
            }
        }).or(Optional.<PageDecorator>absent());
    }

    @Override
    public Optional<String> getImageReference() {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReference();
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReference(name);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageRendition(renditionName);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageRendition(name, renditionName);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource() {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource();
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource(final int width) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(width);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource(final String name) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(name);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource(final String name, final int width) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(name, width);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public <T> T getInherited(final String propertyName, final T defaultValue) {
        return componentNodeOptional.transform(new Function<ComponentNode, T>() {
            @Override
            public T apply(final ComponentNode componentNode) {
                return componentNode.getInherited(propertyName, defaultValue);
            }
        }).or(defaultValue);
    }

    @Override
    public <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<T>>() {
            @Override
            public Optional<T> apply(final ComponentNode componentNode) {
                return componentNode.getInherited(propertyName, type);
            }
        }).or(Optional.<T>absent());
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHrefInherited(propertyName);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHrefInherited(propertyName, strict);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHrefInherited(propertyName, strict, mapped);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLinkInherited(propertyName);
            }
        }).or(Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLinkInherited(propertyName, strict);
            }
        }).or(Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLinkInherited(propertyName, strict, mapped);
            }
        }).or(Optional.<Link>absent());
    }

    @Override
    public <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return componentNodeOptional.transform(new Function<ComponentNode, List<T>>() {
            @Override
            public List<T> apply(final ComponentNode componentNode) {
                return componentNode.getAsListInherited(propertyName, type);
            }
        }).or(Collections.<T>emptyList());
    }

    @Override
    public Optional<PageDecorator> getAsPageInherited(final String propertyName) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<PageDecorator>>() {
            @Override
            public Optional<PageDecorator> apply(final ComponentNode componentNode) {
                return componentNode.getAsPageInherited(propertyName);
            }
        }).or(Optional.<PageDecorator>absent());
    }

    @Override
    public Optional<String> getImageReferenceInherited() {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReferenceInherited();
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageReferenceInherited(final String name) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReferenceInherited(name);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited() {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited();
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited(final int width) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited(width);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited(final String name) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited(name);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited(final String name, final int width) {
        return componentNodeOptional.transform(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited(name, width);
            }
        }).or(Optional.<String>absent());
    }

    @Override
    public Optional<PageDecorator> findAncestor(final Predicate<PageDecorator> predicate) {
        PageDecorator page = this;
        PageDecorator ancestorPage = null;

        while (page != null) {
            if (predicate.apply(page)) {
                ancestorPage = page;
                break;
            } else {
                page = page.getParent();
            }
        }

        return Optional.fromNullable(ancestorPage);
    }

    @Override
    public Optional<PageDecorator> findAncestorWithProperty(final String propertyName) {
        return findAncestorForPredicate(new ComponentNodePropertyExistsPredicate(propertyName));
    }

    @Override
    public <V> Optional<PageDecorator> findAncestorWithPropertyValue(final String propertyName, final V propertyValue) {
        return findAncestorForPredicate(new ComponentNodePropertyValuePredicate<V>(propertyName, propertyValue));
    }

    @Override
    public List<PageDecorator> findDescendants(final Predicate<PageDecorator> predicate) {
        final List<PageDecorator> pages = Lists.newArrayList();

        final Iterator<Page> iterator = delegate.listChildren(ALL_PAGES, true);

        final PageManagerDecorator pageManager = getPageManagerDecorator();

        while (iterator.hasNext()) {
            final PageDecorator child = pageManager.getPage(iterator.next());

            if (predicate.apply(child)) {
                pages.add(child);
            }
        }

        return pages;
    }

    @Override
    public List<PageDecorator> getChildren() {
        return filterChildren(ALL, false);
    }

    @Override
    public List<PageDecorator> getChildren(final boolean displayableOnly) {
        return displayableOnly ? filterChildren(DISPLAYABLE_ONLY, false) : filterChildren(ALL, false);
    }

    @Override
    public List<PageDecorator> getChildren(final Predicate<PageDecorator> predicate) {
        return filterChildren(checkNotNull(predicate), false);
    }

    @Override
    public Optional<ComponentNode> getComponentNode() {
        return componentNodeOptional;
    }

    @Override
    public Optional<ComponentNode> getComponentNode(final String relativePath) {
        final Optional<ComponentNode> optional;

        if (componentNodeOptional.isPresent()) {
            optional = componentNodeOptional.get().getComponentNode(relativePath);
        } else {
            optional = Optional.absent();
        }

        return optional;
    }

    @Override
    public String getHref() {
        return getHref(false);
    }

    @Override
    public String getHref(final boolean mapped) {
        return getLink(mapped).getHref();
    }

    @Override
    public ImageLink getImageLink(final String imageSource) {
        return LinkBuilder.forPage(this).setImageSource(checkNotNull(imageSource)).buildImageLink();
    }

    @Override
    public boolean isHasImage() {
        return componentNodeOptional.transform(new Function<ComponentNode, Boolean>() {
            @Override
            public Boolean apply(final ComponentNode componentNode) {
                return componentNode.isHasImage();
            }
        }).or(false);
    }

    @Override
    public boolean isHasImage(final String name) {
        return componentNodeOptional.transform(new Function<ComponentNode, Boolean>() {
            @Override
            public Boolean apply(final ComponentNode componentNode) {
                return componentNode.isHasImage(name);
            }
        }).or(false);
    }

    @Override
    public Link getLink() {
        return getLink(false);
    }

    @Override
    public Link getLink(final boolean mapped) {
        return getLinkBuilder(mapped).build();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return getLinkBuilder(false);
    }

    @Override
    public LinkBuilder getLinkBuilder(final boolean mapped) {
        return LinkBuilder.forPage(this, mapped);
    }

    @Override
    public NavigationLink getNavigationLink() {
        return LinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).buildNavigationLink();
    }

    @Override
    public NavigationLink getNavigationLink(final boolean isActive) {
        return LinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).setActive(isActive).buildNavigationLink();
    }

    @Override
    public Optional<String> getNavigationTitleOptional() {
        return Optional.fromNullable(delegate.getNavigationTitle());
    }

    @Override
    public Optional<String> getPageTitleOptional() {
        return Optional.fromNullable(delegate.getPageTitle());
    }

    // overrides

    @Override
    public PageDecorator getAbsoluteParent(final int level) {
        final Page absoluteParent = delegate.getAbsoluteParent(level);

        return absoluteParent == null ? null : new DefaultPageDecorator(absoluteParent);
    }

    @Override
    public PageManagerDecorator getPageManager() {
        return getPageManagerDecorator();
    }

    @Override
    public PageDecorator getParent() {
        final Page parent = delegate.getParent();

        return parent == null ? null : new DefaultPageDecorator(parent);
    }

    @Override
    public PageDecorator getParent(final int level) {
        final Page parent = delegate.getParent(level);

        return parent == null ? null : new DefaultPageDecorator(parent);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("path", getPath()).add("title", getTitle()).toString();
    }

    // delegate methods

    @Override
    public boolean canUnlock() {
        return delegate.canUnlock();
    }

    @Override
    public Resource getContentResource() {
        return delegate.getContentResource();
    }

    @Override
    public Resource getContentResource(final String relPath) {
        return delegate.getContentResource(relPath);
    }

    @Override
    public int getDepth() {
        return delegate.getDepth();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public Locale getLanguage(final boolean ignoreContent) {
        return delegate.getLanguage(ignoreContent);
    }

    @Override
    public Calendar getLastModified() {
        return delegate.getLastModified();
    }

    @Override
    public String getLastModifiedBy() {
        return delegate.getLastModifiedBy();
    }

    @Override
    public String getLockOwner() {
        return delegate.getLockOwner();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getNavigationTitle() {
        return delegate.getNavigationTitle();
    }

    @Override
    public Calendar getOffTime() {
        return delegate.getOffTime();
    }

    @Override
    public Calendar getOnTime() {
        return delegate.getOnTime();
    }

    @Override
    public String getPageTitle() {
        return delegate.getPageTitle();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public ValueMap getProperties() {
        return delegate.getProperties();
    }

    @Override
    public ValueMap getProperties(final String relativePath) {
        return delegate.getProperties(relativePath);
    }

    @Override
    public Tag[] getTags() {
        return delegate.getTags();
    }

    @Override
    public Template getTemplate() {
        return delegate.getTemplate();
    }

    @Override
    public String getTemplatePath() {
        return getProperties().get(NameConstants.NN_TEMPLATE, "");
    }

    @Override
    public String getTitle() {
        return getProperties().get(NameConstants.PN_TITLE, "");
    }

    @Override
    public String getVanityUrl() {
        return delegate.getVanityUrl();
    }

    @Override
    public boolean hasChild(final String name) {
        return delegate.hasChild(name);
    }

    @Override
    public boolean hasContent() {
        return delegate.hasContent();
    }

    @Override
    public boolean isHideInNav() {
        return delegate.isHideInNav();
    }

    @Override
    public boolean isLocked() {
        return delegate.isLocked();
    }

    @Override
    public boolean isValid() {
        return delegate.isValid();
    }

    @Override
    public Iterator<Page> listChildren() {
        return delegate.listChildren();
    }

    @Override
    public Iterator<Page> listChildren(final Filter<Page> filter) {
        return delegate.listChildren(filter);
    }

    @Override
    public Iterator<Page> listChildren(final Filter<Page> filter, final boolean deep) {
        return delegate.listChildren(filter, deep);
    }

    @Override
    public void lock() throws WCMException {
        delegate.lock();
    }

    @Override
    public long timeUntilValid() {
        return delegate.timeUntilValid();
    }

    @Override
    public void unlock() throws WCMException {
        delegate.unlock();
    }

    // internal methods

    private Optional<PageDecorator> findAncestorForPredicate(final Predicate<ComponentNode> predicate) {
        PageDecorator page = this;
        PageDecorator ancestorPage = null;

        while (page != null) {
            final Optional<ComponentNode> optionalComponentNode = page.getComponentNode();

            if (optionalComponentNode.isPresent() && predicate.apply(optionalComponentNode.get())) {
                ancestorPage = page;
                break;
            } else {
                page = page.getParent();
            }
        }

        return Optional.fromNullable(ancestorPage);
    }

    private List<PageDecorator> filterChildren(final Predicate<PageDecorator> predicate, final boolean deep) {
        final List<PageDecorator> pages = Lists.newArrayList();

        final Iterator<Page> children = delegate.listChildren(ALL_PAGES, deep);

        final PageManagerDecorator pageManager = getPageManagerDecorator();

        while (children.hasNext()) {
            final PageDecorator child = pageManager.getPage(children.next());

            if (child != null && predicate.apply(child)) {
                pages.add(child);
            }
        }

        return pages;
    }

    private PageManagerDecorator getPageManagerDecorator() {
        return delegate.adaptTo(Resource.class).getResourceResolver().adaptTo(PageManagerDecorator.class);
    }
}
