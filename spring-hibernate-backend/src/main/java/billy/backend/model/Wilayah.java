package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Wilayah implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeWilayah;
	private String namaWilayah;
	private String status;
	private Date lastUpdate;
	private String updatedBy;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Wilayah() {
	}

	public Wilayah(long id, String kodeWilayah) {
		this.setId(id);
		this.kodeWilayah = kodeWilayah;
	}

	public Wilayah(long id, String kodeWilayah, String namaWilayah, String status) {
		this.setId(id);
		this.kodeWilayah = kodeWilayah;
		this.namaWilayah = namaWilayah;
		this.status = status;	
		
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

	public String getKodeWilayah() {
		return kodeWilayah;
	}

	public void setKodeWilayah(String kodeWilayah) {
		this.kodeWilayah = kodeWilayah;
	}

	public String getNamaWilayah() {
		return namaWilayah;
	}

	public void setNamaWilayah(String namaWilayah) {
		this.namaWilayah = namaWilayah;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public boolean equals(Wilayah Parameter) {
		return getId() == Parameter.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Wilayah) {
			Wilayah Parameter = (Wilayah) obj;
			return equals(Parameter);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
