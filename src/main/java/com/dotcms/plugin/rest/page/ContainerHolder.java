package com.dotcms.plugin.rest.page;

import java.io.Serializable;
import java.util.List;

import com.dotmarketing.beans.ContainerStructure;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;

class ContainerHolder implements Serializable {

  private static final long serialVersionUID = 1L;
  final Container container;
  final List<ContainerStructure> containerStructures;
  final String rendered;
  final List<Contentlet> containerContentlets;
  public ContainerHolder(Container container, List<ContainerStructure> containerStructures, String rendered, final List<Contentlet> containerContentlets) {
    super();
    this.container = container;
    this.containerStructures = containerStructures;
    this.rendered = rendered;
    this.containerContentlets=containerContentlets;
  }

  public Container getContainer() {
    return container;
  }

  public String getRendered() {
    return rendered;
  }

  public List<ContainerStructure> getContainerStructures() {
    return containerStructures;
  }
  public List<Contentlet> getContainerContentlet() {
      return containerContentlets;
    }
}
