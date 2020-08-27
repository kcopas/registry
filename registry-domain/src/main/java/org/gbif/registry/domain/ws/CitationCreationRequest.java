package org.gbif.registry.domain.ws;

import org.gbif.api.model.common.DOI;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class CitationCreationRequest {

  private DOI originalDownloadDOI;
  private String title;
  private String creator;
  private URI target;
  // List of DOIs or UUIDs
  private List<String> relatedDatasets;

  public DOI getOriginalDownloadDOI() {
    return originalDownloadDOI;
  }

  public void setOriginalDownloadDOI(DOI originalDownloadDOI) {
    this.originalDownloadDOI = originalDownloadDOI;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public URI getTarget() {
    return target;
  }

  public void setTarget(URI target) {
    this.target = target;
  }

  public List<String> getRelatedDatasets() {
    return Collections.unmodifiableList(relatedDatasets);
  }

  public void setRelatedDatasets(List<String> relatedDatasets) {
    this.relatedDatasets = relatedDatasets;
  }
}
