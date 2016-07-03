package billy.webui.printer.model;

import java.util.ArrayList;
import java.util.List;
 
public class Faktur {
 
    private String nomorFaktur;
    private String kodeSales1;
    private String namaSales1;
    private String kodeSales2;
    private String namaSales2;
    private String intervalKredit;
    private String namaPelanggan;
    private String alamat;
    private String alamat2;
    private String alamat3;
    private String telepon;
    private String total;
    private String dp;
    private String tglPenjualan;
    private String namaSupervisor;
    private String namaPengirim;
    
    
    private List<ItemFaktur> listItemFaktur = new ArrayList<ItemFaktur>();
 
    public Faktur() {
		
	}

	public Faktur(String nomorFaktur, String kodeSales1, String namaSales1,
			String kodeSales2, String namaSales2, String intervalKredit,
			String namaPelanggan, String alamat, String alamat2,
			String alamat3, String telepon, String total, String dp,
			String tglPenjualan, String namaSupervisor, String namaPengirim,
			List<ItemFaktur> listItemFaktur) {
		
		this.nomorFaktur = nomorFaktur;
		this.kodeSales1 = kodeSales1;
		this.namaSales1 = namaSales1;
		this.kodeSales2 = kodeSales2;
		this.namaSales2 = namaSales2;
		this.intervalKredit = intervalKredit;
		this.namaPelanggan = namaPelanggan;
		this.alamat = alamat;
		this.alamat2 = alamat2;
		this.alamat3 = alamat3;
		this.telepon = telepon;
		this.total = total;
		this.dp = dp;
		this.tglPenjualan = tglPenjualan;
		this.namaSupervisor = namaSupervisor;
		this.namaPengirim = namaPengirim;
		this.listItemFaktur = listItemFaktur;
	}

	
 
    public String getNomorFaktur() {
        return nomorFaktur;
    }
 
    public String getKodeSales1() {
		return kodeSales1;
	}

	public void setKodeSales1(String kodeSales1) {
		this.kodeSales1 = kodeSales1;
	}

	public String getNamaSales1() {
		return namaSales1;
	}

	public void setNamaSales1(String namaSales1) {
		this.namaSales1 = namaSales1;
	}

	public String getKodeSales2() {
		return kodeSales2;
	}

	public void setKodeSales2(String kodeSales2) {
		this.kodeSales2 = kodeSales2;
	}

	public String getNamaSales2() {
		return namaSales2;
	}

	public void setNamaSales2(String namaSales2) {
		this.namaSales2 = namaSales2;
	}

	public String getIntervalKredit() {
		return intervalKredit;
	}

	public void setIntervalKredit(String intervalKredit) {
		this.intervalKredit = intervalKredit;
	}

	public String getNamaPelanggan() {
		return namaPelanggan;
	}

	public void setNamaPelanggan(String namaPelanggan) {
		this.namaPelanggan = namaPelanggan;
	}

	public String getAlamat() {
		return alamat;
	}

	public void setAlamat(String alamat) {
		this.alamat = alamat;
	}

	public String getTelepon() {
		return telepon;
	}

	public void setTelepon(String telepon) {
		this.telepon = telepon;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getDp() {
		return dp;
	}

	public void setDp(String dp) {
		this.dp = dp;
	}

	public String getTglPenjualan() {
		return tglPenjualan;
	}

	public void setTglPenjualan(String tglPenjualan) {
		this.tglPenjualan = tglPenjualan;
	}

	public String getNamaSupervisor() {
		return namaSupervisor;
	}

	public void setNamaSupervisor(String namaSupervisor) {
		this.namaSupervisor = namaSupervisor;
	}

	public String getNamaPengirim() {
		return namaPengirim;
	}

	public void setNamaPengirim(String namaPengirim) {
		this.namaPengirim = namaPengirim;
	}

	public void setNomorFaktur(String nomorFaktur) {
		this.nomorFaktur = nomorFaktur;
	}

	public void setListItemFaktur(List<ItemFaktur> listItemFaktur) {
		this.listItemFaktur = listItemFaktur;
	}

	public List<ItemFaktur> getListItemFaktur() {
        return listItemFaktur;
    }
 
    public void tambahItemFaktur(ItemFaktur itemFaktur) {
        this.listItemFaktur.add(itemFaktur);
    }

	public String getAlamat2() {
		return alamat2;
	}

	public void setAlamat2(String alamat2) {
		this.alamat2 = alamat2;
	}

	public String getAlamat3() {
		return alamat3;
	}

	public void setAlamat3(String alamat3) {
		this.alamat3 = alamat3;
	}
}
