## Servlets

### JSON Response Servlet

`com.citytechinc.cq.library.servlets.AbstractJsonResponseServlet`

Servlets should extend this class when writing a JSON response.  Objects passed to any of the `writeJsonResponse()` methods will be serialized to the response writer using the [Jackson](https://github.com/FasterXML/jackson-databind) data binding library.

### Image Servlet

`com.citytechinc.cq.library.servlets.ImageServlet`

The image servlet overrides CQ's default image rendering servlets to provide image resizing and the ability to associate additional named images to a page or component.

For additional details, see the [Image Rendering](https://github.com/Citytechinc/cq-library/wiki/Image-Rendering) page.

### Options Provider Servlet

`com.citytechinc.cq.library.servlets.optionsprovider.AbstractOptionsProviderServlet`



### Abstract Validation Servlet

`com.citytechinc.cq.library.servlets.AbstractValidationServlet`

### Selective Replication Servlet

`com.citytechinc.cq.library.servlets.replication.SelectiveReplicationServlet`

### Paragraph JSON Servlet

`com.citytechinc.cq.library.servlets.paragraphs.ParagraphJsonServlet`