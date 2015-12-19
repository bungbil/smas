package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class HargaBarang implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -1714867949815379740L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private Barang barang;
	private int intervalKredit;	
	private BigDecimal hargaBarang;
	private BigDecimal cicilanPerBulan;
	private BigDecimal komisiSales;
	private BigDecimal tabunganSales;
	private Date lastUpdate;
	private String updatedBy;
	
	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public HargaBarang() {
	}

	public HargaBarang(long id, Barang barang) {
		this.setId(id);
		this.barang = barang;
	}

	public HargaBarang(long id, int version, Barang barang, int intervalKredit,
			BigDecimal hargaBarang, BigDecimal cicilanPerBulan,
			BigDecimal komisiSales, BigDecimal tabunganSales, Date lastUpdate,
			String updatedBy) {
		
		this.setId(id);
		this.version = version;
		this.barang = barang;
		this.intervalKredit = intervalKredit;
		this.hargaBarang = hargaBarang;
		this.cicilanPerBulan = cicilanPerBulan;
		this.komisiSales = komisiSales;
		this.tabunganSales = tabunganSales;
		this.lastUpdate = lastUpdate;
		this.updatedBy = updatedBy;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	/**
	 * EN: Hibernate version field. Do not touch this!.<br>
	 * DE: Hibernate Versions Info. Bitte nicht benutzen!<br>
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * EN: Hibernate version field. Do not touch this!.<br>
	 * DE: Hibernate Versions Info. Bitte nicht benutzen!<br>
	 */
	public void setVersion(int version) {
		this.version = version;
	}


	public Barang getBarang() {
		return barang;
	}

	public void setBarang(Barang barang) {
		this.barang = barang;
	}

	public int getIntervalKredit() {
		return intervalKredit;
	}

	public void setIntervalKredit(int intervalKredit) {
		this.intervalKredit = intervalKredit;
	}

	public BigDecimal getHargaBarang() {
		return hargaBarang;
	}

	public void setHargaBarang(BigDecimal hargaBarang) {
		this.hargaBarang = hargaBarang;
	}

	public BigDecimal getCicilanPerBulan() {
		return cicilanPerBulan;
	}

	public void setCicilanPerBulan(BigDecimal cicilanPerBulan) {
		this.cicilanPerBulan = cicilanPerBulan;
	}

	public BigDecimal getKomisiSales() {
		return komisiSales;
	}

	public void setKomisiSales(BigDecimal komisiSales) {
		this.komisiSales = komisiSales;
	}

	public BigDecimal getTabunganSales() {
		return tabunganSales;
	}

	public void setTabunganSales(BigDecimal tabunganSales) {
		this.tabunganSales = tabunganSales;
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

	public boolean equals(HargaBarang hargaBarang) {
		return getId() == hargaBarang.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof HargaBarang) {
			HargaBarang hargaBarang = (HargaBarang) obj;
			return equals(hargaBarang);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
