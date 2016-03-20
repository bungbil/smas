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
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(PenjualanDetail obj) {
		return getId() == obj.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof PenjualanDetail) {
			PenjualanDetail objs = (PenjualanDetail) obj;
			return equals(objs);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
