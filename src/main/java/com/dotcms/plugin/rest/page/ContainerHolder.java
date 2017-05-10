package com.dotcms.plugin.rest.page;

import java.io.Serializable;
import java.util.List;

import com.dotmarketing.beans.ContainerStructure;
import com.dotmarketing.portlets.containers.model.Container;

class ContainerHolder implements Serializable {

  private static final long serialVersionUID = 1L;
  final Container container;
  final List<ContainerStructure> containerStructures;
  final String rendered;

  public ContainerHolder(Container container, List<ContainerStructure> containerStructures, String rendered) {
    super();
    this.container = container;
    this.containerStructures = containerStructures;
    this.rendered = rendered;
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
}
