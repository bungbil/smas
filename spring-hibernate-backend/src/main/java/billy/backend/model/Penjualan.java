package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Penjualan implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String noFaktur;
  private String mandiri;
  private String noOrderSheet;
  private Date tglPenjualan;
  private Date rencanaKirim;
  private Wilayah wilayah;
  private Karyawan sales1;
  private Karyawan sales2;
  private Karyawan pengirim;

  private String namaPelanggan;
  private String telepon;
  private String alamat;
  private String remark;

  private String metodePembayaran;
  private BigDecimal diskon;
  private BigDecimal downPayment;
  private int intervalKredit;

  private BigDecimal kreditPerBulan;
  private Date tglAngsuran2;


  private Status status;
  private BigDecimal total;
  private BigDecimal grandTotal;
  private Karyawan divisi;
  private BigDecimal piutang;

  private boolean needApproval = false;
  private String reasonApproval;
  private String approvedRemark;
  private String approvedBy;

  private Date lastUpdate;
  private String updatedBy;

  public Penjualan() {}

  public Penjualan(long id, String noFaktur, Date tglPenjualan, String metodePembayaran,
      Status status, Karyawan sales1, Karyawan sales2, Karyawan divisi, Wilayah wilayah,
      String namaPelanggan, String telepon, String alamat, Date rencanaKirim, Karyawan pengirim,
      BigDecimal downPayment, int intervalKredit, BigDecimal diskon, Date tglAngsuran2,
      BigDecimal total, BigDecimal grandTotal, BigDecimal kreditPerBulan, String remark,
      String mandiri, String noOrderSheet) {

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
    this.tglAngsuran2 = tglAngsuran2;
    this.total = total;
    this.grandTotal = grandTotal;
    this.kreditPerBulan = kreditPerBulan;
    this.remark = remark;
    this.mandiri = mandiri;
    this.noOrderSheet = noOrderSheet;
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

  public boolean equals(Penjualan obj) {
    return getId() == obj.getId();
  }

  public String getAlamat() {
    return alamat;
  }

  public String getApprovedBy() {
    return approvedBy;
  }

  public String getApprovedRemark() {
    return approvedRemark;
  }

  public BigDecimal getDiskon() {
    return diskon;
  }

  public Karyawan getDivisi() {
    return divisi;
  }

  public BigDecimal getDownPayment() {
    return downPayment;
  }

  public BigDecimal getGrandTotal() {
    return grandTotal;
  }

  @Override
  public long getId() {
    return id;
  }

  public int getIntervalKredit() {
    return intervalKredit;
  }

  public BigDecimal getKreditPerBulan() {
    return kreditPerBulan;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public String getMandiri() {
    return mandiri;
  }

  public String getMetodePembayaran() {
    return metodePembayaran;
  }

  public String getNamaPelanggan() {
    return namaPelanggan;
  }

  public String getNoFaktur() {
    return noFaktur;
  }

  public String getNoOrderSheet() {
    return noOrderSheet;
  }

  public Karyawan getPengirim() {
    return pengirim;
  }

  public BigDecimal getPiutang() {
    return piutang;
  }

  public String getReasonApproval() {
    return reasonApproval;
  }

  public String getRemark() {
    return remark;
  }

  public Date getRencanaKirim() {
    return rencanaKirim;
  }

  public Karyawan getSales1() {
    return sales1;
  }

  public Karyawan getSales2() {
    return sales2;
  }

  public Status getStatus() {
    return status;
  }

  public String getTelepon() {
    return telepon;
  }

  public Date getTglAngsuran2() {
    return tglAngsuran2;
  }

  public Date getTglPenjualan() {
    return tglPenjualan;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public int getVersion() {
    return this.version;
  }

  public Wilayah getWilayah() {
    return wilayah;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(getId()).hashCode();
  }

  public boolean isNeedApproval() {
    return needApproval;
  }

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public void setAlamat(String alamat) {
    this.alamat = alamat;
  }

  public void setApprovedBy(String approvedBy) {
    this.approvedBy = approvedBy;
  }

  public void setApprovedRemark(String approvedRemark) {
    this.approvedRemark = approvedRemark;
  }

  public void setDiskon(BigDecimal diskon) {
    this.diskon = diskon;
  }

  public void setDivisi(Karyawan divisi) {
    this.divisi = divisi;
  }

  public void setDownPayment(BigDecimal downPayment) {
    this.downPayment = downPayment;
  }

  public void setGrandTotal(BigDecimal grandTotal) {
    this.grandTotal = grandTotal;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setIntervalKredit(int intervalKredit) {
    this.intervalKredit = intervalKredit;
  }

  public void setKreditPerBulan(BigDecimal kreditPerBulan) {
    this.kreditPerBulan = kreditPerBulan;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }


  public void setMandiri(String mandiri) {
    this.mandiri = mandiri;
  }

  public void setMetodePembayaran(String metodePembayaran) {
    this.metodePembayaran = metodePembayaran;
  }

  public void setNamaPelanggan(String namaPelanggan) {
    this.namaPelanggan = namaPelanggan;
  }

  public void setNeedApproval(boolean needApproval) {
    this.needApproval = needApproval;
  }

  public void setNoFaktur(String noFaktur) {
    this.noFaktur = noFaktur;
  }

  public void setNoOrderSheet(String noOrderSheet) {
    this.noOrderSheet = noOrderSheet;
  }

  public void setPengirim(Karyawan pengirim) {
    this.pengirim = pengirim;
  }

  public void setPiutang(BigDecimal piutang) {
    this.piutang = piutang;
  }

  public void setReasonApproval(String reasonApproval) {
    this.reasonApproval = reasonApproval;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public void setRencanaKirim(Date rencanaKirim) {
    this.rencanaKirim = rencanaKirim;
  }

  public void setSales1(Karyawan sales1) {
    this.sales1 = sales1;
  }

  public void setSales2(Karyawan sales2) {
    this.sales2 = sales2;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setTelepon(String telepon) {
    this.telepon = telepon;
  }

  public void setTglAngsuran2(Date tglAngsuran2) {
    this.tglAngsuran2 = tglAngsuran2;
  }

  public void setTglPenjualan(Date tglPenjualan) {
    this.tglPenjualan = tglPenjualan;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void setWilayah(Wilayah wilayah) {
    this.wilayah = wilayah;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", getId()).toString();
  }

}
