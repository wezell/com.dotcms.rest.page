package com.dotcms.plugin.rest.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotcms.repackage.javax.ws.rs.GET;
import com.dotcms.repackage.javax.ws.rs.Path;
import com.dotcms.repackage.javax.ws.rs.PathParam;
import com.dotcms.repackage.javax.ws.rs.core.Context;
import com.dotcms.repackage.javax.ws.rs.core.MediaType;
import com.dotcms.repackage.javax.ws.rs.core.Response;
import com.dotcms.repackage.javax.ws.rs.core.Response.ResponseBuilder;
import com.dotcms.rest.InitDataObject;
import com.dotcms.rest.RESTParams;
import com.dotcms.rest.ResourceResponse;
import com.dotcms.rest.WebResource;
import com.dotcms.rest.annotation.NoCache;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.ContentletRelationships;
import com.dotmarketing.portlets.structure.model.ContentletRelationships.ContentletRelationshipRecords;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.json.JSONArray;
import com.dotmarketing.util.json.JSONException;
import com.dotmarketing.util.json.JSONObject;
import com.dotmarketing.viewtools.content.util.ContentUtils;
import com.liferay.portal.model.User;




/**
 * Pass a query 
 * http://localhost:8080/api/contentRelationships/query/+contentType:News%20+(conhost:48190c8c-42c4-46af-8d1a-0cd5db894797%20conhost:SYSTEM_HOST)%20+deleted:false%20+working:true/limit/3/orderby/modDate%20desc
 *
 * or an id
 * http://localhost:8080/api/contentRelationships/id/2943b5eb-9105-4dcf-a1c7-87a9d4dc92a6
 * 
 * or an inode
 * http://localhost:8080/api/contentRelationships/inode/aaee9776-8fb7-4501-8048-844912a20405
 */




@Path("/contentRelationships")
public class ContentWithRelationsResource {

  private final WebResource webResource = new WebResource();

  @NoCache
  @GET
  @Path("/{params: .*}")
  public Response getContent(@Context HttpServletRequest request, @Context HttpServletResponse response,
      @PathParam("params") String params) throws DotDataException, DotSecurityException, JSONException {

    InitDataObject initData = webResource.init(params, true, request, false, null);

    Map<String, String> paramsMap = initData.getParamsMap();
    User user = initData.getUser();


    String query = paramsMap.get(RESTParams.QUERY.getValue());
    String id = paramsMap.get(RESTParams.ID.getValue());
    String orderBy = paramsMap.get(RESTParams.ORDERBY.getValue());
    String limitStr = paramsMap.get(RESTParams.LIMIT.getValue());
    String offsetStr = paramsMap.get(RESTParams.OFFSET.getValue());
    String inode = paramsMap.get(RESTParams.INODE.getValue());
    orderBy = UtilMethods.isSet(orderBy) ? orderBy : "modDate desc";
    long language = APILocator.getLanguageAPI().getDefaultLanguage().getId();

    if (paramsMap.get(RESTParams.LANGUAGE.getValue()) != null) {
      try {
        language = Long.parseLong(paramsMap.get(RESTParams.LANGUAGE.getValue()));
      } catch (Exception e) {
        Logger.warn(this.getClass(), "Invald language passed in, defaulting to, well, the default");
      }
    }

    /* Limit and Offset Parameters Handling, if not passed, using default */

    int limit = 10;
    int offset = 0;

    try {
      if (UtilMethods.isSet(limitStr)) {
        limit = Integer.parseInt(limitStr);
      }
    } catch (NumberFormatException e) {
    }

    try {
      if (UtilMethods.isSet(offsetStr)) {
        offset = Integer.parseInt(offsetStr);
      }
    } catch (NumberFormatException e) {
    }

    boolean live = (paramsMap.get(RESTParams.LIVE.getValue()) == null
        || !"false".equals(paramsMap.get(RESTParams.LIVE.getValue())));

    /* Fetching the content using a query if passed or an id */

    List<Contentlet> cons = new ArrayList<Contentlet>();
    Boolean idPassed = false;
    Boolean inodePassed = false;
    Boolean queryPassed = false;

    try {
      if (idPassed = UtilMethods.isSet(id)) {
        cons.add(APILocator.getContentletAPI().findContentletByIdentifier(id, live, language, user, true));
      } else if (inodePassed = UtilMethods.isSet(inode)) {
        cons.add(APILocator.getContentletAPI().find(inode, user, true));
      } else if (queryPassed = UtilMethods.isSet(query)) {
        String tmDate = (String) request.getSession().getAttribute("tm_date");
        cons = ContentUtils.pull(query, offset, limit, orderBy, user, tmDate);
      }
    } catch (Exception e) {
      if (idPassed) {
        Logger.warn(this, "Can't find Content with Identifier: " + id);
      } else if (queryPassed) {
        Logger.warn(this, "Can't find Content with Inode: " + inode);
      } else if (inodePassed) {
        Logger.warn(this, "Error searching Content : " + e.getMessage());
      }
    }

    JSONArray array = new JSONArray();
    for (Contentlet con : cons) {
      JSONObject jo = toJson(con, user);
      ContentletRelationships rels = APILocator.getContentletAPI().getAllRelationships(con);
      for (ContentletRelationshipRecords rel : rels.getRelationshipsRecords()) {
        JSONArray arr = (JSONArray) jo.optJSONArray(rel.getRelationship().getTitle());
        if (arr == null) {
          arr = new JSONArray();
        }
        for(Contentlet c : rel.getRecords()){
          Logger.info(this, "No longer adding this Content : " + c.getIdentifier() + ", lang: " + c.getLanguageId());
          try {
            List<Contentlet> list =  APILocator.getContentletAPI().getAllLanguages(c,true, user, false);
            for(Contentlet item : list){
//              if (item.getLanguageId() == language) {
                arr.add(toJson(item, user));
                Logger.info(this, "Adding Content : " + item.getIdentifier() + ", " + item.getLanguageId());
//              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        jo.put(rel.getRelationship().getRelationTypeValue(), arr);
      }
      array.add(jo);
    }
    ResponseBuilder builder = Response.ok(array.toString(), MediaType.APPLICATION_JSON);
    builder.header("Access-Control-Expose-Headers", "Authorization");
    builder.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    
    return builder.build();

  }


  JSONObject toJson(Contentlet con, User user) throws DotDataException, JSONException {
    JSONObject jo = new JSONObject();
    Structure s = con.getStructure();

    for (String key : con.getMap().keySet()) {
      jo.put(key, con.getMap().get(key));
    }

    return jo;
  }
  
  
  
  
  



}
