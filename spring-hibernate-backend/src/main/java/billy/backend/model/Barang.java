package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Barang implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeBarang;
	private String namaBarang;
	private Wilayah wilayah;
	private BigDecimal divisiOr;	
	private BigDecimal divisiOpr;
	private Set<HargaBarang> hargaBarang = new HashSet<HargaBarang>(0);
	private Date lastUpdate;
	private String updatedBy;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Barang() {
	}

	public Barang(long id, String kodeBarang) {
		this.setId(id);
		this.kodeBarang = kodeBarang;
	}

	public Barang(long id, int version, String kodeBarang, String namaBarang,
			Wilayah wilayah, BigDecimal divisiOr, BigDecimal divisiOpr,
			Set<HargaBarang> hargaBarang, Date lastUpdate, String updatedBy) {
		this.setId(id);
		this.version = version;
		this.kodeBarang = kodeBarang;
		this.namaBarang = namaBarang;
		this.wilayah = wilayah;
		this.divisiOr = divisiOr;
		this.divisiOpr = divisiOpr;
		this.hargaBarang = hargaBarang;
		this.lastUpdate = lastUpdate;
		this.updatedBy = updatedBy;
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

	public String getKodeBarang() {
		return kodeBarang;
	}

	public void setKodeBarang(String kodeBarang) {
		this.kodeBarang = kodeBarang;
	}

	public String getNamaBarang() {
		return namaBarang;
	}

	public void setNamaBarang(String namaBarang) {
		this.namaBarang = namaBarang;
	}


	public Wilayah getWilayah() {
		return wilayah;
	}

	public void setWilayah(Wilayah wilayah) {
		this.wilayah = wilayah;
	}

	public BigDecimal getDivisiOr() {
		return divisiOr;
	}

	public void setDivisiOr(BigDecimal divisiOr) {
		this.divisiOr = divisiOr;
	}

	public BigDecimal getDivisiOpr() {
		return divisiOpr;
	}

	public void setDivisiOpr(BigDecimal divisiOpr) {
		this.divisiOpr = divisiOpr;
	}

	public Set<HargaBarang> getHargaBarang() {
		return hargaBarang;
	}

	public void setHargaBarang(Set<HargaBarang> hargaBarang) {
		this.hargaBarang = hargaBarang;
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

	public boolean equals(Barang barang) {
		return getId() == barang.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Barang) {
			Barang barang = (Barang) obj;
			return equals(barang);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
