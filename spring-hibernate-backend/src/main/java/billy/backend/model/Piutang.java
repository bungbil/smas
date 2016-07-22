package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

public class Piutang implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private Penjualan penjualan;
  private String noKuitansi;
  private int pembayaranKe;
  private Date tglPembayaran;
  private Status status;
  private BigDecimal nilaiTagihan;
  private BigDecimal pembayaran;

  private Date tglJatuhTempo;
  private Karyawan kolektor;
  private String keterangan;

  private boolean needApproval = false;
  private String reasonApproval;
  private String approvedRemark;
  private String approvedBy;

  private BigDecimal diskon;
  private Date tglBawaKolektor;
  private boolean fullPayment = false;
  private Status statusFinal;
  private BigDecimal kekuranganBayar;
  private boolean aktif = false;
  private Date lastUpdate;
  private String updatedBy;

  public Piutang() {}


  public Piutang(long id, int version, Penjualan penjualan, String noKuitansi, int pembayaranKe,
      Date tglPembayaran, Status status, BigDecimal nilaiTagihan, BigDecimal pembayaran,
      Date tglJatuhTempo, Karyawan kolektor, String keterangan, boolean needApproval,
      String reasonApproval, String approvedRemark, String approvedBy, BigDecimal diskon,
      Date tglBawaKolektor, boolean fullPayment, Status statusFinal, BigDecimal kekuranganBayar,
      boolean aktif, Date lastUpdate, String updatedBy) {
    super();
    this.id = id;
    this.version = version;
    this.penjualan = penjualan;
    this.noKuitansi = noKuitansi;
    this.pembayaranKe = pembayaranKe;
    this.tglPembayaran = tglPembayaran;
    this.status = status;
    this.nilaiTagihan = nilaiTagihan;
    this.pembayaran = pembayaran;
    this.tglJatuhTempo = tglJatuhTempo;
    this.kolektor = kolektor;
    this.keterangan = keterangan;
    this.needApproval = needApproval;
    this.reasonApproval = reasonApproval;
    this.approvedRemark = approvedRemark;
    this.approvedBy = approvedBy;
    this.diskon = diskon;
    this.tglBawaKolektor = tglBawaKolektor;
    this.fullPayment = fullPayment;
    this.statusFinal = statusFinal;
    this.kekuranganBayar = kekuranganBayar;
    this.aktif = aktif;
    this.lastUpdate = lastUpdate;
    this.updatedBy = updatedBy;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Piutang) {
      Piutang objs = (Piutang) obj;
      return equals(objs);
    }

    return false;
  }


  public boolean equals(Piutang obj) {
    return getId() == obj.getId();
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


  @Override
  public long getId() {
    return id;
  }


  public BigDecimal getKekuranganBayar() {
    return kekuranganBayar;
  }


  public String getKeterangan() {
    return keterangan;
  }


  public Karyawan getKolektor() {
    return kolektor;
  }


  public Date getLastUpdate() {
    return lastUpdate;
  }

  public BigDecimal getNilaiTagihan() {
    return nilaiTagihan;
  }

  public String getNoKuitansi() {
    return noKuitansi;
  }

  public BigDecimal getPembayaran() {
    return pembayaran;
  }

  public int getPembayaranKe() {
    return pembayaranKe;
  }

  public Penjualan getPenjualan() {
    return penjualan;
  }

  public String getReasonApproval() {
    return reasonApproval;
  }

  public Status getStatus() {
    return status;
  }

  public Status getStatusFinal() {
    return statusFinal;
  }

  public Date getTglBawaKolektor() {
    return tglBawaKolektor;
  }

  public Date getTglJatuhTempo() {
    return tglJatuhTempo;
  }

  public Date getTglPembayaran() {
    return tglPembayaran;
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

  public boolean isAktif() {
    return aktif;
  }

  public boolean isFullPayment() {
    return fullPayment;
  }

  public boolean isNeedApproval() {
    return needApproval;
  }


  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public void setAktif(boolean aktif) {
    this.aktif = aktif;
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

  public void setFullPayment(boolean fullPayment) {
    this.fullPayment = fullPayment;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setKekuranganBayar(BigDecimal kekuranganBayar) {
    this.kekuranganBayar = kekuranganBayar;
  }

  public void setKeterangan(String keterangan) {
    this.keterangan = keterangan;
  }

  public void setKolektor(Karyawan kolektor) {
    this.kolektor = kolektor;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setNeedApproval(boolean needApproval) {
    this.needApproval = needApproval;
  }

  public void setNilaiTagihan(BigDecimal nilaiTagihan) {
    this.nilaiTagihan = nilaiTagihan;
  }

  public void setNoKuitansi(String noKuitansi) {
    this.noKuitansi = noKuitansi;
  }

  public void setPembayaran(BigDecimal pembayaran) {
    this.pembayaran = pembayaran;
  }

  public void setPembayaranKe(int pembayaranKe) {
    this.pembayaranKe = pembayaranKe;
  }

  public void setPenjualan(Penjualan penjualan) {
    this.penjualan = penjualan;
  }

  public void setReasonApproval(String reasonApproval) {
    this.reasonApproval = reasonApproval;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setStatusFinal(Status statusFinal) {
    this.statusFinal = statusFinal;
  }

  public void setTglBawaKolektor(Date tglBawaKolektor) {
    this.tglBawaKolektor = tglBawaKolektor;
  }

  public void setTglJatuhTempo(Date tglJatuhTempo) {
    this.tglJatuhTempo = tglJatuhTempo;
  }


  public void setTglPembayaran(Date tglPembayaran) {
    this.tglPembayaran = tglPembayaran;
  }


  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }


  public void setVersion(int version) {
    this.version = version;
  }


  @Override
  public String toString() {
    return String
        .format(
            "Piutang [id=%s, version=%s, penjualan=%s, noKuitansi=%s, pembayaranKe=%s, tglPembayaran=%s, status=%s, nilaiTagihan=%s, pembayaran=%s, tglJatuhTempo=%s, kolektor=%s, keterangan=%s, needApproval=%s, reasonApproval=%s, approvedRemark=%s, approvedBy=%s, diskon=%s, tglBawaKolektor=%s, fullPayment=%s, statusFinal=%s, kekuranganBayar=%s, aktif=%s, lastUpdate=%s, updatedBy=%s]",
            id, version, penjualan, noKuitansi, pembayaranKe, tglPembayaran, status, nilaiTagihan,
            pembayaran, tglJatuhTempo, kolektor, keterangan, needApproval, reasonApproval,
            approvedRemark, approvedBy, diskon, tglBawaKolektor, fullPayment, statusFinal,
            kekuranganBayar, aktif, lastUpdate, updatedBy);
  }

}
