package com.dotcms.plugin.rest.page;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.dotcms.mock.response.BaseResponse;
import com.dotcms.mock.response.MockHttpResponse;
import com.dotcms.repackage.javax.ws.rs.GET;
import com.dotcms.repackage.javax.ws.rs.Path;
import com.dotcms.repackage.javax.ws.rs.PathParam;
import com.dotcms.repackage.javax.ws.rs.core.Context;
import com.dotcms.repackage.javax.ws.rs.core.MediaType;
import com.dotcms.repackage.javax.ws.rs.core.Response;
import com.dotcms.repackage.javax.ws.rs.core.Response.ResponseBuilder;
import com.dotcms.rest.InitDataObject;
import com.dotcms.rest.WebResource;
import com.dotcms.rest.annotation.NoCache;
import com.dotmarketing.beans.ContainerStructure;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.business.Permissionable;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.htmlpageasset.model.HTMLPageAsset;
import com.dotmarketing.portlets.templates.design.bean.TemplateLayout;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.VelocityUtil;
import com.dotmarketing.viewtools.DotTemplateTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.liferay.portal.model.User;




/**
 * 
 * 
 * Call 
 *
 */
@Path("/page")
public class PageResource  {

    private final WebResource webResource = new WebResource();

    /**

     * 
     * @param request
     * @param params
     * @return
     * @throws DotStateException
     * @throws DotDataException
     * @throws DotSecurityException
     * @throws JsonProcessingException 
     */
    @NoCache
    @GET
    @Path("/json/{uri: .*}")
    public Response loadJson(@Context HttpServletRequest request, @PathParam("uri") String uri) throws DotStateException,
            DotDataException, DotSecurityException, JsonProcessingException {
        // force authentication
        InitDataObject auth = webResource.init(false, request, false);
        User user = auth.getUser();
        uri = (uri.startsWith("/"))?uri : "/"+uri;
        
        String hostName = request.getParameter("Host") ==null ? request.getServerName() : request.getParameter("Host");
        Host host = WebAPILocator.getHostWebAPI().resolveHostName(hostName, user, true);
        
        // get page id
        Identifier id = APILocator.getIdentifierAPI().find(host, uri);
        
        HTMLPageAsset page = (HTMLPageAsset) APILocator.getHTMLPageAssetAPI().getPageByPath(uri, host, APILocator.getLanguageAPI().getDefaultLanguage().getId(), true);
        Template template = (Template) APILocator.getVersionableAPI().findLiveVersion(page.getTemplateId(), user, true);
        
        List<Container> containers = APILocator.getTemplateAPI().getContainersInTemplate(template, user, true);
        Map<String, ContainerHolder> newContainers = new LinkedHashMap<>();
        for(Container container : containers){
          List<ContainerStructure> containerStructures = APILocator.getContainerAPI().getContainerStructures(container);
          newContainers.put(container.getIdentifier(),new ContainerHolder(container, containerStructures, null));
          
        }
        TemplateLayout layout=(null!=template.getTheme()) ? new DotTemplateTool().themeLayout(template.getInode()) : null;
        
        PageResourceHolder prh = new PageResourceHolder(host, template, newContainers, page, layout);

        ObjectWriter ow = JsonMapper.mapper.writer().withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(prh);
        System.out.println("page param:" + uri);
        
        ResponseBuilder builder = Response.ok(json, MediaType.APPLICATION_JSON);

        builder.header("Access-Control-Expose-Headers", "Authorization");
        builder.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        
        return builder.build();

    }
    /**
     * This is an authenticated rest service.
     * 
     * @param request
     * @param params
     * @return
     * @throws Exception 
     * @throws ParseErrorException 
     * @throws ResourceNotFoundException 
     */
    @NoCache
    @GET
    @Path("/render/{uri: .*}")
    public Response renderPage(@Context HttpServletRequest request,@Context HttpServletResponse response, @PathParam("uri") String uri) throws ResourceNotFoundException, ParseErrorException, Exception {
        // force authentication
        InitDataObject auth = webResource.init(false, request, false);
        
        // get a context
        org.apache.velocity.context.Context context = VelocityUtil.getWebContext(request, new MockHttpResponse(new BaseResponse().response()).response());
        
        User user = auth.getUser();
        uri = (uri.startsWith("/"))?uri : "/"+uri;
        
        String hostName = request.getParameter("Host") ==null ? request.getServerName() : request.getParameter("Host");
        Host host = WebAPILocator.getHostWebAPI().resolveHostName(hostName, user, true);
        
        //get page info
        HTMLPageAsset page = (HTMLPageAsset) APILocator.getHTMLPageAssetAPI().getPageByPath(uri, host, APILocator.getLanguageAPI().getDefaultLanguage().getId(), true);
        page.setProperty("rendered", VelocityUtil.mergeTemplate("/live/" + page.getIdentifier() + "_" + page.getLanguageId() + ".dotpage", context));
        
        Template template = (Template) APILocator.getVersionableAPI().findLiveVersion(page.getTemplateId(), user, true);
        List<Container> containers = APILocator.getTemplateAPI().getContainersInTemplate(template, user, true);
        Map<String, ContainerHolder> newContainers = new LinkedHashMap<>();
        TemplateLayout layout=(null!=template.getTheme()) ? new DotTemplateTool().themeLayout(template.getInode()) : null;
        
        
        
        for(Container container : containers){
          List<ContainerStructure> containerStructures = APILocator.getContainerAPI().getContainerStructures(container);
          newContainers.put(container.getIdentifier(), new ContainerHolder(container, containerStructures, VelocityUtil.mergeTemplate("/live/" + container.getIdentifier() + ".container", context)));  
        }
        
        PageResourceHolder prh = new PageResourceHolder(host, template, newContainers, page, layout);

        ObjectWriter ow = JsonMapper.mapper.writer().withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(prh);

        
        ResponseBuilder builder = Response.ok(json, MediaType.APPLICATION_JSON);
        
        builder.header("Access-Control-Expose-Headers", "Authorization");
        builder.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        return builder.build();

    }


}