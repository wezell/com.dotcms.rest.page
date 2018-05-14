package com.dotcms.plugin.rest.page;

import org.osgi.framework.BundleContext;

import com.dotcms.rest.config.RestServiceUtil;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Logger;

public class Activator extends GenericBundleActivator {

  Class[] clazzes = {PageResource.class};

  public void start(BundleContext context) throws Exception {
    for (Class clazz : clazzes) {
      Logger.info(this.getClass(), "Adding new Restful Service:" + clazz.getSimpleName());
      RestServiceUtil.addResource(clazz);
    }

  }

  public void stop(BundleContext context) throws Exception {

    for (Class clazz : clazzes) {
      Logger.info(this.getClass(), "Adding new Restful Service:" + clazz.getSimpleName());
      RestServiceUtil.removeResource(clazz);
    }
  }

}
