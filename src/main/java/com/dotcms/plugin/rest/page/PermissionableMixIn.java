package com.dotcms.plugin.rest.page;

import com.dotmarketing.business.Permissionable;
import com.fasterxml.jackson.annotation.JsonIgnore;

abstract class PermissionableMixIn {
  @JsonIgnore abstract Permissionable getParentPermissionable(); // we don't need it!  
}
