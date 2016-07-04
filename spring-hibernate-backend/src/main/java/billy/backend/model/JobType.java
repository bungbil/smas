package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class JobType implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String namaJobType;
  private Date lastUpdate;
  private String updatedBy;


  public JobType() {}

  public JobType(long id, String namaJobType) {
    this.setId(id);
    this.namaJobType = namaJobType;
  }

  public boolean equals(JobType jobType) {
    return getId() == jobType.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof JobType) {
      JobType jobType = (JobType) obj;
      return equals(jobType);
    }

    return false;
  }

  @Override
  public long getId() {
    return id;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public String getNamaJobType() {
    return namaJobType;
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

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setNamaJobType(String namaJobType) {
    this.namaJobType = namaJobType;
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
