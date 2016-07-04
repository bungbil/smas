package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Parameter implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String paramName;
  private String paramValue;
  private String description;
  private Date lastUpdate;
  private String updatedBy;


  public Parameter() {}

  public Parameter(long id, String paramName) {
    this.setId(id);
    this.paramName = paramName;
  }

  public Parameter(long id, String paramName, String paramValue, String description) {
    this.setId(id);
    this.paramName = paramName;
    this.paramValue = paramValue;
    this.description = description;

  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Parameter) {
      Parameter parameter = (Parameter) obj;
      return equals(parameter);
    }

    return false;
  }

  public boolean equals(Parameter parameter) {
    return getId() == parameter.getId();
  }

  public String getDescription() {
    return description;
  }

  @Override
  public long getId() {
    return id;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }


  public String getParamName() {
    return paramName;
  }

  public String getParamValue() {
    return paramValue;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public int getVersion() {
    return this.version;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(getId()).hashCode();
  }

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setParamName(String paramName) {
    this.paramName = paramName;
  }

  public void setParamValue(String paramValue) {
    this.paramValue = paramValue;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", getId()).toString();
  }

}
