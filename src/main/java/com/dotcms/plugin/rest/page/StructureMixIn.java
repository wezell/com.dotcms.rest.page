package com.dotcms.plugin.rest.page;

import java.util.List;

import com.dotcms.sync.Exportable;
import com.dotmarketing.business.Permissionable;
import com.fasterxml.jackson.annotation.JsonIgnore;

abstract class StructureMixIn {
  @JsonIgnore abstract List<Exportable> getDependencies(); 
  @JsonIgnore abstract Permissionable getParentPermissionable(); // we don't need it!  
}
