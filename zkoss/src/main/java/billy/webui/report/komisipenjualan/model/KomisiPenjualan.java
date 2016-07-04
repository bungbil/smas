package billy.webui.report.komisipenjualan.model;

import java.math.BigDecimal;
 
public class KomisiPenjualan {
 
	private String nomorFaktur;
    private String namaPelanggan;
    private String kodePartner;
    private String intervalKredit;
    private String namaBarang;
    private Double qtyKirim;
    private BigDecimal penjualanBarang;
    private BigDecimal penerimaanPenjualan;
    private BigDecimal komisiPenjualan;
    
    
    
    
    public KomisiPenjualan() {
		
	}




	public KomisiPenjualan(String nomorFaktur, String namaPelanggan,
			String kodePartner, String intervalKredit, String namaBarang,
			Double qtyKirim, BigDecimal penjualanBarang,
			BigDecimal penerimaanPenjualan, BigDecimal komisiPenjualan) {
		super();
		this.nomorFaktur = nomorFaktur;
		this.namaPelanggan = namaPelanggan;
		this.kodePartner = kodePartner;
		this.intervalKredit = intervalKredit;
		this.namaBarang = namaBarang;
		this.qtyKirim = qtyKirim;
		this.penjualanBarang = penjualanBarang;
		this.penerimaanPenjualan = penerimaanPenjualan;
		this.komisiPenjualan = komisiPenjualan;
	}




	public String getNomorFaktur() {
		return nomorFaktur;
	}




	public void setNomorFaktur(String nomorFaktur) {
		this.nomorFaktur = nomorFaktur;
	}




	public String getNamaPelanggan() {
		return namaPelanggan;
	}




	public void setNamaPelanggan(String namaPelanggan) {
		this.namaPelanggan = namaPelanggan;
	}




	public String getKodePartner() {
		return kodePartner;
	}




	public void setKodePartner(String kodePartner) {
		this.kodePartner = kodePartner;
	}




	public String getIntervalKredit() {
		return intervalKredit;
	}




	public void setIntervalKredit(String intervalKredit) {
		this.intervalKredit = intervalKredit;
	}




	public String getNamaBarang() {
		return namaBarang;
	}




	public void setNamaBarang(String namaBarang) {
		this.namaBarang = namaBarang;
	}




	public Double getQtyKirim() {
		return qtyKirim;
	}




	public void setQtyKirim(Double qtyKirim) {
		this.qtyKirim = qtyKirim;
	}




	public BigDecimal getPenjualanBarang() {
		return penjualanBarang;
	}




	public void setPenjualanBarang(BigDecimal penjualanBarang) {
		this.penjualanBarang = penjualanBarang;
	}




	public BigDecimal getPenerimaanPenjualan() {
		return penerimaanPenjualan;
	}




	public void setPenerimaanPenjualan(BigDecimal penerimaanPenjualan) {
		this.penerimaanPenjualan = penerimaanPenjualan;
	}




	public BigDecimal getKomisiPenjualan() {
		return komisiPenjualan;
	}




	public void setKomisiPenjualan(BigDecimal komisiPenjualan) {
		this.komisiPenjualan = komisiPenjualan;
	}


}
