## Filters

### Login Page Filter

`com.citytechinc.aem.bedrock.filters.LoginPageFilter`

This filter is used customize the default Sling authentication failure and session timeout messages.  These messages are hard-coded into the Sling framework, so it is necessary to apply request filtering to replace the message content with client-appropriate values.

The OSGi configuration for the filter (accessible through the Apache Felix Console) contains a "Paths" entry in addition to the message values.  This entry should be updated with the page paths of any login pages for the site, e.g. "/login".  Requests for all other pages will pass through the filter unmodified.

This filter can be configured to filter requests to a configurable set of pages to customize default Sling authentication messages.