package billy.webui.report.summarypenjualan.model;

import java.math.BigDecimal;
 
public class SummaryPenjualan {
 
	private String namaDivisi;
    private String namaBarang;
    private int unitSetTerjual;
    private BigDecimal penjualanBarang;
    private BigDecimal penerimaanPenjualan;
    private BigDecimal sisaPiutang;
    
    
    
    
    public SummaryPenjualan() {
		
	}




	public String getNamaDivisi() {
		return namaDivisi;
	}




	public void setNamaDivisi(String namaDivisi) {
		this.namaDivisi = namaDivisi;
	}




	public String getNamaBarang() {
		return namaBarang;
	}




	public void setNamaBarang(String namaBarang) {
		this.namaBarang = namaBarang;
	}




	public int getUnitSetTerjual() {
		return unitSetTerjual;
	}




	public void setUnitSetTerjual(int unitSetTerjual) {
		this.unitSetTerjual = unitSetTerjual;
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




	public BigDecimal getSisaPiutang() {
		return sisaPiutang;
	}




	public void setSisaPiutang(BigDecimal sisaPiutang) {
		this.sisaPiutang = sisaPiutang;
	}




	public SummaryPenjualan(String namaDivisi, String namaBarang,
			int unitSetTerjual, BigDecimal penjualanBarang,
			BigDecimal penerimaanPenjualan, BigDecimal sisaPiutang) {
		super();
		this.namaDivisi = namaDivisi;
		this.namaBarang = namaBarang;
		this.unitSetTerjual = unitSetTerjual;
		this.penjualanBarang = penjualanBarang;
		this.penerimaanPenjualan = penerimaanPenjualan;
		this.sisaPiutang = sisaPiutang;
	}



}
