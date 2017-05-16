
# README
----
This plugin offers two example REST end points

## 1. PageResource

This resoruce will give you all the objects on an associated page (template, theme, containers) and optionally render them.

To call this method, send:

`http://localhost:8080/api/page/json/{path-to-your-page}`

e.g.

`http://localhost:8080/api/page/json/about-us/locations/index`


To render the page contents, send "render" instead of "json"  

`http://localhost:8080/api/page/render/about-us/locations/index`


## 2. ContentRelationshipResource

This resource will allow you to pull content using similar syntaxt as the normal content resource, but will also include the related contents in the payload.  To use:

Pass a query 

`http://localhost:8080/api/contentRelationships/query/+contentType:News%20+(conhost:48190c8c-42c4-46af-8d1a-0cd5db894797%20conhost:SYSTEM_HOST)%20+deleted:false%20+working:true/limit/3/orderby/modDate%20desc`

or an id

`http://localhost:8080/api/contentRelationships/id/2943b5eb-9105-4dcf-a1c7-87a9d4dc92a6`

or an inode

`http://localhost:8080/api/contentRelationships/inode/aaee9776-8fb7-4501-8048-844912a20405`






## How to build this example
----

To install all you need to do is build the JAR. To do this run from this directory:

`./gradlew jar`

or for windows

`.\gradlew.bat jar`

This will build a jar in the build/libs directory



## Authentication
----
This API supports the same REST auth infrastructure as other 
rest apis in dotcms. There are 4 ways to authenticate.

* user/xxx/password/yyy in the URI
* basic http/https authentication (base64 encoded)
* DOTAUTH header similar to basic auth and base64 encoded, e.g. setHeader("DOTAUTH", base64.encode("admin@dotcms.com:admin"))
* Session based (form based login) for frontend or backend logged in user

Curl Example:
`curl -u admin@dotcms.com:admin --head http://localhost:8080/api/page/json/index`



## Exports I needed to get this to work
```
javax.management,
javax.servlet.resources,
com.dotcms.mock.response,
com.dotcms.repackage.javax.ws.rs,
com.dotcms.repackage.javax.ws.rs.core,
com.dotcms.repackage.javax.ws.rs.core.Response,
com.dotcms.rest,
com.dotcms.rest.annotation,
com.dotcms.rest.config,
com.dotmarketing.beans,
com.dotmarketing.business,
com.dotmarketing.business.web,
com.dotmarketing.exception,
com.dotmarketing.osgi,
com.dotmarketing.portlets.containers.business,
com.dotmarketing.portlets.containers.model,
com.dotmarketing.portlets.contentlet.business,
com.dotmarketing.portlets.contentlet.model,
com.dotmarketing.portlets.contentlet.util,
com.dotmarketing.portlets.htmlpageasset.business,
com.dotmarketing.portlets.htmlpageasset.model,
com.dotmarketing.portlets.languagesmanager.business,
com.dotmarketing.portlets.languagesmanager.model,
com.dotmarketing.portlets.structure.model,
com.dotmarketing.portlets.structure.model.ContentletRelationships,
com.dotmarketing.portlets.templates.business,
com.dotmarketing.portlets.templates.design.bean,
com.dotmarketing.portlets.templates.model,
com.dotmarketing.util,
com.dotmarketing.util.json,
com.dotmarketing.viewtools,
com.dotmarketing.viewtools.content.util,
com.fasterxml.jackson.annotation,
com.fasterxml.jackson.core,
com.fasterxml.jackson.databind,
com.liferay.portal.model,
javax.servlet;javax.servlet.http;version=3.1.0,
org.apache.velocity.context,
org.apache.velocity.exception,
org.apache.velocity.tools.view.context,
org.osgi.framework
```





