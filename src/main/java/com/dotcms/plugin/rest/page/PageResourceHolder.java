package com.dotcms.plugin.rest.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.portlets.htmlpageasset.model.HTMLPageAsset;
import com.dotmarketing.portlets.templates.design.bean.TemplateLayout;
import com.dotmarketing.portlets.templates.model.Template;

public class PageResourceHolder implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  final Host host;
  final Template template;
  final Map<String,ContainerHolder> containers;
  final HTMLPageAsset page;
  final TemplateLayout layout;
  public PageResourceHolder(Host host, Template template, Map<String,ContainerHolder> containers, HTMLPageAsset page,TemplateLayout layout) {
    super();
    this.host = host;
    this.template = template;
    this.containers = containers;
    this.page = page;
    this.layout = layout;
  }

  public TemplateLayout getLayout() {
    return layout;
  }

  public Host getHost() {
    return host;
  }

  public Template getTemplate() {
    return template;
  }

  public Map<String,ContainerHolder> getContainers() {
    return containers;
  }

  public HTMLPageAsset getPage() {
    return page;
  }
  
  
  
  
  
  
  
}
