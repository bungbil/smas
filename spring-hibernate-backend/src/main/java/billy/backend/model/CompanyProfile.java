package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CompanyProfile implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String companyName;
  private String address;
  private String phone;
  private String email;
  private Date lastUpdate;
  private String updatedBy;


  public CompanyProfile() {}

  public CompanyProfile(long id, String companyName) {
    this.setId(id);
    this.companyName = companyName;
  }

  public CompanyProfile(long id, String companyName, String address, String phone, String email) {
    this.setId(id);
    this.companyName = companyName;
    this.address = address;
    this.phone = phone;
    this.email = email;
  }

  public boolean equals(CompanyProfile companyProfile) {
    return getId() == companyProfile.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof CompanyProfile) {
      CompanyProfile companyProfile = (CompanyProfile) obj;
      return equals(companyProfile);
    }

    return false;
  }

  public String getAddress() {
    return address;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getEmail() {
    return email;
  }


  @Override
  public long getId() {
    return id;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public String getPhone() {
    return phone;
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

  public void setAddress(String address) {
    this.address = address;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setPhone(String phone) {
    this.phone = phone;
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
