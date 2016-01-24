package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Penjualan implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String noFaktur;
	private Date tglPenjualan;
	private String metodePembayaran;
	private Status status;
	private Karyawan sales1;
	private Karyawan sales2;
	private Karyawan divisi;
	private Wilayah wilayah;
	private String namaPelanggan;
	private String telepon;
	private String alamat;
	private Date rencanaKirim;
	private Karyawan pengirim;
	private BigDecimal downPayment;
	private int intervalKredit;	
	private BigDecimal diskon;
	private Date tglAngsuran1;
	private BigDecimal total;
	private BigDecimal grandTotal;
	private BigDecimal kreditPerBulan;
	private BigDecimal piutang;
	private String remark;
	private String bulanPenjualan;
	private String tahunPenjualan;
	
	private Date lastUpdate;
	private String updatedBy;

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Penjualan() {
	}

	public Penjualan(long id, String noFaktur, Date tglPenjualan,
			String metodePembayaran, Status status, Karyawan sales1,
			Karyawan sales2, Karyawan divisi, Wilayah wilayah,
			String namaPelanggan, String telepon, String alamat,
			Date rencanaKirim, Karyawan pengirim, BigDecimal downPayment,
			int intervalKredit, BigDecimal diskon, Date tglAngsuran1,
			BigDecimal total, BigDecimal grandTotal, BigDecimal kreditPerBulan,
			String remark, String bulanPenjualan, String tahunPenjualan) {
		
		this.id = id;
		this.noFaktur = noFaktur;
		this.tglPenjualan = tglPenjualan;
		this.metodePembayaran = metodePembayaran;
		this.status = status;
		this.sales1 = sales1;
		this.sales2 = sales2;
		this.divisi = divisi;
		this.wilayah = wilayah;
		this.namaPelanggan = namaPelanggan;
		this.telepon = telepon;
		this.alamat = alamat;
		this.rencanaKirim = rencanaKirim;
		this.pengirim = pengirim;
		this.downPayment = downPayment;
		this.intervalKredit = intervalKredit;
		this.diskon = diskon;
		this.tglAngsuran1 = tglAngsuran1;
		this.total = total;
		this.grandTotal = grandTotal;
		this.kreditPerBulan = kreditPerBulan;
		this.remark = remark;
		this.bulanPenjualan = bulanPenjualan;
		this.tahunPenjualan = tahunPenjualan;
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
	
	public String getNoFaktur() {
		return noFaktur;
	}

	public void setNoFaktur(String noFaktur) {
		this.noFaktur = noFaktur;
	}

	public Date getTglPenjualan() {
		return tglPenjualan;
	}

	public void setTglPenjualan(Date tglPenjualan) {
		this.tglPenjualan = tglPenjualan;
	}

	public String getMetodePembayaran() {
		return metodePembayaran;
	}

	public void setMetodePembayaran(String metodePembayaran) {
		this.metodePembayaran = metodePembayaran;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Karyawan getSales1() {
		return sales1;
	}

	public void setSales1(Karyawan sales1) {
		this.sales1 = sales1;
	}

	public Karyawan getSales2() {
		return sales2;
	}

	public void setSales2(Karyawan sales2) {
		this.sales2 = sales2;
	}

	public Karyawan getDivisi() {
		return divisi;
	}

	public void setDivisi(Karyawan divisi) {
		this.divisi = divisi;
	}

	public Wilayah getWilayah() {
		return wilayah;
	}

	public void setWilayah(Wilayah wilayah) {
		this.wilayah = wilayah;
	}

	public String getNamaPelanggan() {
		return namaPelanggan;
	}

	public void setNamaPelanggan(String namaPelanggan) {
		this.namaPelanggan = namaPelanggan;
	}

	public String getTelepon() {
		return telepon;
	}

	public void setTelepon(String telepon) {
		this.telepon = telepon;
	}

	public String getAlamat() {
		return alamat;
	}

	public void setAlamat(String alamat) {
		this.alamat = alamat;
	}

	public Date getRencanaKirim() {
		return rencanaKirim;
	}

	public void setRencanaKirim(Date rencanaKirim) {
		this.rencanaKirim = rencanaKirim;
	}

	public Karyawan getPengirim() {
		return pengirim;
	}

	public void setPengirim(Karyawan pengirim) {
		this.pengirim = pengirim;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public int getIntervalKredit() {
		return intervalKredit;
	}

	public void setIntervalKredit(int intervalKredit) {
		this.intervalKredit = intervalKredit;
	}

	public BigDecimal getDiskon() {
		return diskon;
	}

	public void setDiskon(BigDecimal diskon) {
		this.diskon = diskon;
	}

	public Date getTglAngsuran1() {
		return tglAngsuran1;
	}

	public void setTglAngsuran1(Date tglAngsuran1) {
		this.tglAngsuran1 = tglAngsuran1;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public BigDecimal getKreditPerBulan() {
		return kreditPerBulan;
	}

	public void setKreditPerBulan(BigDecimal kreditPerBulan) {
		this.kreditPerBulan = kreditPerBulan;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBulanPenjualan() {
		return bulanPenjualan;
	}

	public void setBulanPenjualan(String bulanPenjualan) {
		this.bulanPenjualan = bulanPenjualan;
	}

	public String getTahunPenjualan() {
		return tahunPenjualan;
	}

	public void setTahunPenjualan(String tahunPenjualan) {
		this.tahunPenjualan = tahunPenjualan;
	}

	public BigDecimal getPiutang() {
		return piutang;
	}

	public void setPiutang(BigDecimal piutang) {
		this.piutang = piutang;
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

	public boolean equals(Penjualan obj) {
		return getId() == obj.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Penjualan) {
			Penjualan objs = (Penjualan) obj;
			return equals(objs);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
