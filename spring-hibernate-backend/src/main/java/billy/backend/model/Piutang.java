package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

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
  private Date lastUpdate;
  private String updatedBy;

  public Piutang() {}

  public Piutang(long id, Penjualan penjualan, String noKuitansi, int pembayaranKe,
      Date tglPembayaran, Status status, BigDecimal nilaiTagihan, BigDecimal pembayaran,
      Date tglJatuhTempo, Karyawan kolektor, String keterangan) {

    this.id = id;
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

  @Override
  public long getId() {
    return id;
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


  public Status getStatus() {
    return status;
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

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  @Override
  public void setId(long id) {
    this.id = id;
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

  public void setStatus(Status status) {
    this.status = status;
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
    return new ToStringBuilder(this).append("id", getId()).toString();
  }

}
