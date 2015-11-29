package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Status implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeStatus;
	private String deskripsiStatus;
	private String statusType;
	private Date lastUpdate;
	private String updatedBy;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Status() {
	}

	public Status(long id, String kodeStatus) {
		this.setId(id);
		this.kodeStatus = kodeStatus;
	}

	public Status(long id, String kodeStatus, String deskripsiStatus, String statusType) {
		this.setId(id);
		this.kodeStatus = kodeStatus;
		this.deskripsiStatus = deskripsiStatus;
		this.statusType = statusType;	
		
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getKodeStatus() {
		return kodeStatus;
	}

	public void setKodeStatus(String kodeStatus) {
		this.kodeStatus = kodeStatus;
	}

	public String getDeskripsiStatus() {
		return deskripsiStatus;
	}

	public void setDeskripsiStatus(String deskripsiStatus) {
		this.deskripsiStatus = deskripsiStatus;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(Status Parameter) {
		return getId() == Parameter.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Status) {
			Status Parameter = (Status) obj;
			return equals(Parameter);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
