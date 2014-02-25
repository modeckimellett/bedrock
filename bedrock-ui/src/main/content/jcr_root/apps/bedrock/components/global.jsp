<%@include file="/libs/foundation/global.jsp"%>
<%@taglib prefix="bedrock" uri="http://www.citytechinc.com/taglibs/bedrock"%>

<bedrock:defineObjects />

<c:if test="${isDebug}">
    <!-- resource path: ${resource.path} -->
    <!-- resource type: ${resource.resourceType} -->
    <!-- script: ${sling.script.scriptResource.path} -->
</c:if>