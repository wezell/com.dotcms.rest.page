package com.dotcms.plugin.rest.page;

import com.dotmarketing.business.Permissionable;
import com.dotmarketing.portlets.structure.model.Structure;
import com.fasterxml.jackson.annotation.JsonIgnore;

abstract class ContentletMixIn {
  @JsonIgnore abstract Permissionable getParentPermissionable(); 
  @JsonIgnore abstract public Structure getStructure() ;
 
}
