package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class KategoriBarang implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeKategoriBarang;
	private String deskripsiKategoriBarang;
	private Date lastUpdate;
	private String updatedBy;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public KategoriBarang() {
	}

	public KategoriBarang(long id, String kodeKategoriBarang) {
		this.setId(id);
		this.kodeKategoriBarang = kodeKategoriBarang;
	}

	public KategoriBarang(long id, String kodeKategoriBarang, String deskripsiKategoriBarang) {
		this.setId(id);
		this.kodeKategoriBarang = kodeKategoriBarang;
		this.deskripsiKategoriBarang = deskripsiKategoriBarang;		
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

	public String getKodeKategoriBarang() {
		return kodeKategoriBarang;
	}

	public void setKodeKategoriBarang(String kodeKategoriBarang) {
		this.kodeKategoriBarang = kodeKategoriBarang;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getDeskripsiKategoriBarang() {
		return deskripsiKategoriBarang;
	}

	public void setDeskripsiKategoriBarang(String deskripsiKategoriBarang) {
		this.deskripsiKategoriBarang = deskripsiKategoriBarang;
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

	public boolean equals(KategoriBarang Parameter) {
		return getId() == Parameter.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof KategoriBarang) {
			KategoriBarang Parameter = (KategoriBarang) obj;
			return equals(Parameter);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
