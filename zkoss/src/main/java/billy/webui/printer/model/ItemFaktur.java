package billy.webui.printer.model;

public class ItemFaktur {
 
    private String namaBarang;
    private String qty;
    private String harga;
    private String jumlah;
 
    
 
    public ItemFaktur(String namaBarang, String qty, String harga, String jumlah) {
		
		this.namaBarang = namaBarang;
		this.qty = qty;
		this.harga = harga;
		this.jumlah = jumlah;
	}

	public String getNamaBarang() {
        return namaBarang;
    }

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getHarga() {
		return harga;
	}

	public void setHarga(String harga) {
		this.harga = harga;
	}

	public String getJumlah() {
		return jumlah;
	}

	public void setJumlah(String jumlah) {
		this.jumlah = jumlah;
	}

	public void setNamaBarang(String namaBarang) {
		this.namaBarang = namaBarang;
	}
 
}