package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PenjualanDetail implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private Penjualan penjualan;
	private Barang barang;
	private int qty;
	private BigDecimal harga;
	private BigDecimal total;
	
	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public PenjualanDetail() {
	}

	
	public PenjualanDetail(long id, Penjualan penjualan, Barang barang,
			int qty, BigDecimal harga) {
		
		this.id = id;
		this.penjualan = penjualan;
		this.barang = barang;
		this.qty = qty;
		this.harga = harga;
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

	public Barang getBarang() {
		return barang;
	}

	public void setBarang(Barang barang) {
		this.barang = barang;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public BigDecimal getHarga() {
		return harga;
	}

	public void setHarga(BigDecimal harga) {
		this.harga = harga;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barang == null) ? 0 : barang.hashCode());
		result = prime * result + ((harga == null) ? 0 : harga.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((penjualan == null) ? 0 : penjualan.hashCode());
		result = prime * result + qty;
		result = prime * result + ((total == null) ? 0 : total.hashCode());
		result = prime * result + version;
		return result;
	}

	public boolean equals(PenjualanDetail obj) {
		return getId() == obj.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PenjualanDetail other = (PenjualanDetail) obj;
		if (barang == null) {
			if (other.barang != null)
				return false;
		} else if (!barang.equals(other.barang))
			return false;
		if (harga == null) {
			if (other.harga != null)
				return false;
		} else if (!harga.equals(other.harga))
			return false;
		if (id != other.id)
			return false;
		if (penjualan == null) {
			if (other.penjualan != null)
				return false;
		} else if (!penjualan.equals(other.penjualan))
			return false;
		if (qty != other.qty)
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PenjualanDetail [id=" + id + ", version=" + version
				+ ", penjualan=" + penjualan + ", barang=" + barang + ", qty="
				+ qty + ", harga=" + harga + ", total=" + total + "]";
	}

}
