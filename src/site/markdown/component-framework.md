## Component Framework

### Overview

Component JSPs should contain only the HTML markup and JSTL tags necessary to render the component and it's view permutations, rather than Java "scriptlet" blocks containing business logic.  To facilitate the separation of controller logic from presentation, the CQ Library provides a custom JSP tag to associate a Java class (or "backing bean") to a component JSP.  The library also provides an abstract template class containing accessors and convenience methods for objects that are typically available in the JSP page context (e.g. current page, current node, Sling resource resolver, etc.).  Decorator instances for the current page and component node implement common use cases to reduce boilerplate code and encourage the use of established conventions.  This allows the developer to focus on project-specific concerns rather than reimplementing functionality that is frequently required for a typical CQ implementation but may not be provided by the CQ APIs.

### Usage

The component JSP needs to include the CQ Library `global.jsp` to define the tag namespace and ensure that required variables are set in the page context.

Component Java classes can be instantiated in one of two ways:

* Include the `<ct:component/>` tag in the JSP as shown below.
* Define a `className` attribute in the `.content.xml` descriptor file for the component and annotate the Java class with the `com.citytechinc.cq.library.components.annotations.AutoInstantiate` annotation.

In the latter case, the `global.jsp` will instantiate the component class via the `<ct:defineObjects/>` tag included therein.

    <%@include file="/apps/cq-library/components/global.jsp"%>

    <ct:component className="com.projectname.cq.components.content.Navigation" name="navigation"/>

    <h1>${navigation.title}</h1>

    <ul>
        <c:forEach items="${navigation.pages}" var="page">
            <li><a href="${page.href}">${page.title}</a></li>
        </c:forEach>
    </ul>

The backing Java class for the component should expose getters for the values that required to render the component's view.

    package com.projectname.cq.library.components.content;

    import com.citytechinc.cq.library.components.AbstractComponent;
    import com.citytechinc.cq.library.content.page.PageDecorator;
    import com.citytechinc.cq.library.content.request.ComponentRequest;

    import java.util.List;

    public final class Navigation extends AbstractComponent {

        public Navigation(final ComponentRequest request) {
            super(request);
        }

        public String getTitle() {
            return get("title", "");
        }

        public List<PageDecorator> getPages() {
            return currentPage.getChildren(true);
        }
    }

### Abstract Component Java Class

The `AbstractComponent` class should be extended by all component backing classes.  This base class enforces the creation of a single argument constructor that takes a `ComponentRequest` argument, which is required by the `<ct:component/>` JSP tag to instantiate the component class and provide the required page context attributes.  The additional `ComponentNode` constructor allows for component classes to instantiate other component classes directly.

    final PageDecorator homepage = request.getPageManager().getPage("/content/home");

    // get the component node for the Homepage Latest News component
    final ComponentNode latestNewsComponentNode = homepage.getComponentNode("latestnews");

    // get an instance of the Latest News component for the given component node
    final LatestNews latestNews = new LatestNews(latestNewsComponentNode);

See the [Javadoc](http://code.citytechinc.com/cq-library/apidocs/com/citytechinc/cq/library/components/AbstractComponent.html) for details of the available methods.

### Development Guidelines

* Component beans should be **read-only**; since components are generally accessed by an anonymous user in publish mode.  Repository write operations should be performed only in author mode (and replicated only when a page is activated by a content author).  Since component classes are executed in both author and publish modes, ideally one should consider alternative approaches to performing write operations in a component bean:
    * Delegate write operations to an OSGi service that is bound to an administrative session.
    * Refactor the component to perform dialog-based content modifications by attaching a listener to the appropriate [dialog event](http://dev.day.com/content/docs/en/cq/current/widgets-api/index.html?class=CQ.Dialog), e.g. 'beforesubmit'.
    * Register a [JCR event listener](http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/observation/ObservationManager.html) to trigger event-based repository updates.
* Classes should be designed for [immutability](http://www.javapractices.com/topic/TopicAction.do?Id=29) and remain stateless.  Since the lifecycle of a component is bound to a request, state should be maintained client-side using cookies, HTML5 web storage, or DOM data attributes.
    * In brief, the class itself and fields should be marked `final` and should contain no setters.