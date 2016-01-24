package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Piutang implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private Penjualan penjualan;
	private String noKuitansi;
	private int pembayaranKe;
	private Date tglPembayaran;
	private String status;
	private BigDecimal nilaiTagihan;
	private BigDecimal pembayaran;
	private BigDecimal piutang;
	private Date tglJatuhTempo;
	private Karyawan kolektor;
	private String keterangan;
	private Date lastUpdate;
	private String updatedBy;

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Piutang() {
	}

	public Piutang(long id, Penjualan penjualan, String noKuitansi,
			int pembayaranKe, Date tglPembayaran, String status,
			BigDecimal nilaiTagihan, BigDecimal pembayaran, BigDecimal piutang,
			Date tglJatuhTempo, Karyawan kolektor, String keterangan) {
		
		this.id = id;
		this.penjualan = penjualan;
		this.noKuitansi = noKuitansi;
		this.pembayaranKe = pembayaranKe;
		this.tglPembayaran = tglPembayaran;
		this.status = status;
		this.nilaiTagihan = nilaiTagihan;
		this.pembayaran = pembayaran;
		this.piutang = piutang;
		this.tglJatuhTempo = tglJatuhTempo;
		this.kolektor = kolektor;
		this.keterangan = keterangan;
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
	
	public Penjualan getPenjualan() {
		return penjualan;
	}

	public void setPenjualan(Penjualan penjualan) {
		this.penjualan = penjualan;
	}

	
	public String getNoKuitansi() {
		return noKuitansi;
	}

	public void setNoKuitansi(String noKuitansi) {
		this.noKuitansi = noKuitansi;
	}

	public int getPembayaranKe() {
		return pembayaranKe;
	}

	public void setPembayaranKe(int pembayaranKe) {
		this.pembayaranKe = pembayaranKe;
	}

	public Date getTglPembayaran() {
		return tglPembayaran;
	}

	public void setTglPembayaran(Date tglPembayaran) {
		this.tglPembayaran = tglPembayaran;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getNilaiTagihan() {
		return nilaiTagihan;
	}

	public void setNilaiTagihan(BigDecimal nilaiTagihan) {
		this.nilaiTagihan = nilaiTagihan;
	}

	public BigDecimal getPembayaran() {
		return pembayaran;
	}

	public void setPembayaran(BigDecimal pembayaran) {
		this.pembayaran = pembayaran;
	}

	public BigDecimal getPiutang() {
		return piutang;
	}

	public void setPiutang(BigDecimal piutang) {
		this.piutang = piutang;
	}

	public Date getTglJatuhTempo() {
		return tglJatuhTempo;
	}

	public void setTglJatuhTempo(Date tglJatuhTempo) {
		this.tglJatuhTempo = tglJatuhTempo;
	}

	public Karyawan getKolektor() {
		return kolektor;
	}

	public void setKolektor(Karyawan kolektor) {
		this.kolektor = kolektor;
	}

	public String getKeterangan() {
		return keterangan;
	}

	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
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

	public boolean equals(Piutang obj) {
		return getId() == obj.getId();
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

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
