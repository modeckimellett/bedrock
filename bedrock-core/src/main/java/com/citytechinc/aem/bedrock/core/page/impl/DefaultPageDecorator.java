/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.page.impl;

import com.citytechinc.aem.bedrock.api.link.ImageLink;
import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.NavigationLink;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder;
import com.citytechinc.aem.bedrock.api.node.BasicNode;
import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.core.node.impl.DefaultComponentNode;
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodePropertyExistsPredicate;
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodePropertyValuePredicate;
import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator;
import com.citytechinc.aem.bedrock.api.page.enums.TitleType;
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

import static com.citytechinc.aem.bedrock.core.node.impl.NodeFunctions.RESOURCE_TO_COMPONENT_NODE;
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
        return get(new Function<ComponentNode, T>() {
            @Override
            public T apply(final ComponentNode componentNode) {
                return componentNode.get(propertyName, defaultValue);
            }
        }, defaultValue);
    }

    @Override
    public <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return get(new Function<ComponentNode, Optional<T>>() {
            @Override
            public Optional<T> apply(final ComponentNode componentNode) {
                return componentNode.get(propertyName, type);
            }
        }, Optional.<T>absent());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHref(propertyName);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHref(propertyName, strict);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHref(propertyName, strict, mapped);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return get(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLink(propertyName);
            }
        }, Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return get(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLink(propertyName, strict);
            }
        }, Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return get(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLink(propertyName, strict, mapped);
            }
        }, Optional.<Link>absent());
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
        return get(new Function<ComponentNode, Optional<PageDecorator>>() {
            @Override
            public Optional<PageDecorator> apply(final ComponentNode componentNode) {
                return componentNode.getAsPage(propertyName);
            }
        }, Optional.<PageDecorator>absent());
    }

    @Override
    public Optional<String> getImageReference() {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReference();
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReference(name);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageRendition(renditionName);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageRendition(name, renditionName);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource() {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource();
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource(final int width) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(width);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource(final String name) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(name);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSource(final String name, final int width) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(name, width);
            }
        }, Optional.<String>absent());
    }

    @Override
    public <T> T getInherited(final String propertyName, final T defaultValue) {
        return get(new Function<ComponentNode, T>() {
            @Override
            public T apply(final ComponentNode componentNode) {
                return componentNode.getInherited(propertyName, defaultValue);
            }
        }, defaultValue);
    }

    @Override
    public <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return get(new Function<ComponentNode, Optional<T>>() {
            @Override
            public Optional<T> apply(final ComponentNode componentNode) {
                return componentNode.getInherited(propertyName, type);
            }
        }, Optional.<T>absent());
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHrefInherited(propertyName);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHrefInherited(propertyName, strict);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getAsHrefInherited(propertyName, strict, mapped);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName) {
        return get(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLinkInherited(propertyName);
            }
        }, Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return get(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLinkInherited(propertyName, strict);
            }
        }, Optional.<Link>absent());
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return get(new Function<ComponentNode, Optional<Link>>() {
            @Override
            public Optional<Link> apply(final ComponentNode componentNode) {
                return componentNode.getAsLinkInherited(propertyName, strict, mapped);
            }
        }, Optional.<Link>absent());
    }

    @Override
    public <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return get(new Function<ComponentNode, List<T>>() {
            @Override
            public List<T> apply(final ComponentNode componentNode) {
                return componentNode.getAsListInherited(propertyName, type);
            }
        }, Collections.<T>emptyList());
    }

    @Override
    public Optional<PageDecorator> getAsPageInherited(final String propertyName) {
        return get(new Function<ComponentNode, Optional<PageDecorator>>() {
            @Override
            public Optional<PageDecorator> apply(final ComponentNode componentNode) {
                return componentNode.getAsPageInherited(propertyName);
            }
        }, Optional.<PageDecorator>absent());
    }

    @Override
    public Optional<String> getImageReferenceInherited() {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReferenceInherited();
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageReferenceInherited(final String name) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageReferenceInherited(name);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited() {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited();
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited(final int width) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited(width);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited(final String name) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited(name);
            }
        }, Optional.<String>absent());
    }

    @Override
    public Optional<String> getImageSourceInherited(final String name, final int width) {
        return get(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSourceInherited(name, width);
            }
        }, Optional.<String>absent());
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
        return DefaultLinkBuilder.forPage(this).setImageSource(checkNotNull(imageSource)).buildImageLink();
    }

    @Override
    public boolean isHasImage() {
        return get(new Function<ComponentNode, Boolean>() {
            @Override
            public Boolean apply(final ComponentNode componentNode) {
                return componentNode.isHasImage();
            }
        }, false);
    }

    @Override
    public boolean isHasImage(final String name) {
        return get(new Function<ComponentNode, Boolean>() {
            @Override
            public Boolean apply(final ComponentNode componentNode) {
                return componentNode.isHasImage(name);
            }
        }, false);
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
        return DefaultLinkBuilder.forPage(this, mapped);
    }

    @Override
    public NavigationLink getNavigationLink() {
        return DefaultLinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).buildNavigationLink();
    }

    @Override
    public NavigationLink getNavigationLink(final boolean isActive) {
        return DefaultLinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).setActive(isActive).buildNavigationLink();
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

    private <T> T get(final Function<ComponentNode, T> transformer, final T defaultValue) {
        return componentNodeOptional.transform(transformer).or(defaultValue);
    }

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
