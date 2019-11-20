package org.gbif.registry.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.gbif.registry.ws.util.LegacyResourceConstants;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Class used to generate response for legacy (GBRDS/IPT) API.
 * </br>
 * JAXB annotations allow the class to be converted into an XML document or JSON response. @XmlElement is used to
 * specify element names that consumers of legacy services expect to find.
 */
@XmlRootElement(name = "organisation")
public class LegacyOrganizationBriefResponse {

  private String key;
  private String name;

  /**
   * No argument, default constructor needed by JAXB.
   */
  public LegacyOrganizationBriefResponse() {
    // default constructor
  }

  @JsonProperty(LegacyResourceConstants.KEY_PARAM)
  @XmlElement(name = LegacyResourceConstants.KEY_PARAM)
  @NotNull
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @JsonProperty(LegacyResourceConstants.NAME_PARAM)
  @XmlElement(name = LegacyResourceConstants.NAME_PARAM)
  @NotNull
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LegacyOrganizationBriefResponse that = (LegacyOrganizationBriefResponse) o;
    return Objects.equal(key, that.key) &&
      Objects.equal(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, name);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("key", key)
      .add("name", name)
      .toString();
  }

  public static class ItemSerializer extends JsonSerializer<LegacyOrganizationBriefResponse[]> {

    @Override
    public void serialize(
      LegacyOrganizationBriefResponse[] value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {

      // replace default array's 'item' with 'organisation'
      if (jgen instanceof ToXmlGenerator) {
        ToXmlGenerator xmlGen = (ToXmlGenerator) jgen;
        xmlGen.setNextName(new QName("organisation"));
      }

      jgen.writeStartArray();
      for (LegacyOrganizationBriefResponse item : value) {
        jgen.writeStartObject();
        jgen.writeStringField(LegacyResourceConstants.KEY_PARAM, item.getKey());
        jgen.writeStringField(LegacyResourceConstants.NAME_PARAM, item.getName());
        jgen.writeEndObject();
      }
      jgen.writeEndArray();
    }
  }
}
