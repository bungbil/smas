package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

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
  private String alamat2;
  private String alamat3;
  private String remark;

  private String metodePembayaran;
  private BigDecimal diskon;
  private BigDecimal downPayment;
  private int intervalKredit;

  private BigDecimal kreditPerBulan;
  private Date tglAngsuran2;
  private Karyawan kolektor;

  private Status status;
  private Mandiri mandiriId;
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


  public Penjualan(long id, int version, String noFaktur, String mandiri, String noOrderSheet,
      Date tglPenjualan, Date rencanaKirim, Wilayah wilayah, Karyawan sales1, Karyawan sales2,
      Karyawan pengirim, String namaPelanggan, String telepon, String alamat, String alamat2,
      String alamat3, String remark, String metodePembayaran, BigDecimal diskon,
      BigDecimal downPayment, int intervalKredit, BigDecimal kreditPerBulan, Date tglAngsuran2,
      Karyawan kolektor, Status status, Mandiri mandiriId, BigDecimal total, BigDecimal grandTotal,
      Karyawan divisi, BigDecimal piutang, boolean needApproval, String reasonApproval,
      String approvedRemark, String approvedBy, Date lastUpdate, String updatedBy) {
    super();
    this.id = id;
    this.version = version;
    this.noFaktur = noFaktur;
    this.mandiri = mandiri;
    this.noOrderSheet = noOrderSheet;
    this.tglPenjualan = tglPenjualan;
    this.rencanaKirim = rencanaKirim;
    this.wilayah = wilayah;
    this.sales1 = sales1;
    this.sales2 = sales2;
    this.pengirim = pengirim;
    this.namaPelanggan = namaPelanggan;
    this.telepon = telepon;
    this.alamat = alamat;
    this.alamat2 = alamat2;
    this.alamat3 = alamat3;
    this.remark = remark;
    this.metodePembayaran = metodePembayaran;
    this.diskon = diskon;
    this.downPayment = downPayment;
    this.intervalKredit = intervalKredit;
    this.kreditPerBulan = kreditPerBulan;
    this.tglAngsuran2 = tglAngsuran2;
    this.kolektor = kolektor;
    this.status = status;
    this.mandiriId = mandiriId;
    this.total = total;
    this.grandTotal = grandTotal;
    this.divisi = divisi;
    this.piutang = piutang;
    this.needApproval = needApproval;
    this.reasonApproval = reasonApproval;
    this.approvedRemark = approvedRemark;
    this.approvedBy = approvedBy;
    this.lastUpdate = lastUpdate;
    this.updatedBy = updatedBy;
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


  public String getAlamat2() {
    return alamat2;
  }


  public String getAlamat3() {
    return alamat3;
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

  public Karyawan getKolektor() {
    return kolektor;
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

  public Mandiri getMandiriId() {
    return mandiriId;
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

  public void setAlamat2(String alamat2) {
    this.alamat2 = alamat2;
  }

  public void setAlamat3(String alamat3) {
    this.alamat3 = alamat3;
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

  public void setKolektor(Karyawan kolektor) {
    this.kolektor = kolektor;
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


  public void setMandiriId(Mandiri mandiriId) {
    this.mandiriId = mandiriId;
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
    return String
        .format(
            "Penjualan [id=%s, version=%s, noFaktur=%s, mandiri=%s, noOrderSheet=%s, tglPenjualan=%s, rencanaKirim=%s, wilayah=%s, sales1=%s, sales2=%s, pengirim=%s, namaPelanggan=%s, telepon=%s, alamat=%s, alamat2=%s, alamat3=%s, remark=%s, metodePembayaran=%s, diskon=%s, downPayment=%s, intervalKredit=%s, kreditPerBulan=%s, tglAngsuran2=%s, kolektor=%s, status=%s, mandiriId=%s, total=%s, grandTotal=%s, divisi=%s, piutang=%s, needApproval=%s, reasonApproval=%s, approvedRemark=%s, approvedBy=%s, lastUpdate=%s, updatedBy=%s]",
            id, version, noFaktur, mandiri, noOrderSheet, tglPenjualan, rencanaKirim, wilayah,
            sales1, sales2, pengirim, namaPelanggan, telepon, alamat, alamat2, alamat3, remark,
            metodePembayaran, diskon, downPayment, intervalKredit, kreditPerBulan, tglAngsuran2,
            kolektor, status, mandiriId, total, grandTotal, divisi, piutang, needApproval,
            reasonApproval, approvedRemark, approvedBy, lastUpdate, updatedBy);
  }


}
