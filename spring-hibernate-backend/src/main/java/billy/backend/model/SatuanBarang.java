 package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SatuanBarang implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeSatuanBarang;
	private String deskripsiSatuanBarang;
	private boolean satuanStandarBarang;
	private long nilaiStandarSatuan;
	private long nilaiKonversi;	
	private Date lastUpdate;
	private String updatedBy;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public SatuanBarang() {
	}

	public SatuanBarang(long id, String kodeSatuanBarang) {
		this.setId(id);
		this.kodeSatuanBarang = kodeSatuanBarang;
	}

	public SatuanBarang(long id, String kodeSatuanBarang, String deskripsiSatuanBarang, boolean satuanStandarBarang) {
		this.setId(id);
		this.kodeSatuanBarang = kodeSatuanBarang;
		this.deskripsiSatuanBarang = deskripsiSatuanBarang;
		this.satuanStandarBarang = satuanStandarBarang;	
		
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

	public String getKodeSatuanBarang() {
		return kodeSatuanBarang;
	}

	public void setKodeSatuanBarang(String kodeSatuanBarang) {
		this.kodeSatuanBarang = kodeSatuanBarang;
	}


	public String getDeskripsiSatuanBarang() {
		return deskripsiSatuanBarang;
	}

	public void setDeskripsiSatuanBarang(String deskripsiSatuanBarang) {
		this.deskripsiSatuanBarang = deskripsiSatuanBarang;
	}

	public boolean isSatuanStandarBarang() {
		return satuanStandarBarang;
	}

	public void setSatuanStandarBarang(boolean satuanStandarBarang) {
		this.satuanStandarBarang = satuanStandarBarang;
	}

	public long getNilaiStandarSatuan() {
		return nilaiStandarSatuan;
	}

	public void setNilaiStandarSatuan(long nilaiStandarSatuan) {
		this.nilaiStandarSatuan = nilaiStandarSatuan;
	}

	public long getNilaiKonversi() {
		return nilaiKonversi;
	}

	public void setNilaiKonversi(long nilaiKonversi) {
		this.nilaiKonversi = nilaiKonversi;
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

	public boolean equals(SatuanBarang satuanBarang) {
		return getId() == satuanBarang.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof SatuanBarang) {
			SatuanBarang satuanBarang = (SatuanBarang) obj;
			return equals(satuanBarang);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
