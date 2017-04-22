package billy.webui.printer.model;

public class ItemFaktur {

  private String kodeBarang;
  private String namaBarang;
  private String qty;
  private String harga;
  private String jumlah;


  public ItemFaktur(String kodeBarang, String namaBarang, String qty, String harga, String jumlah) {

    this.kodeBarang = kodeBarang;
    this.namaBarang = namaBarang;
    this.qty = qty;
    this.harga = harga;
    this.jumlah = jumlah;
  }

  public String getHarga() {
    return harga;
  }

  public String getJumlah() {
    return jumlah;
  }

  public String getKodeBarang() {
    return kodeBarang;
  }

  public String getNamaBarang() {
    return namaBarang;
  }

  public String getQty() {
    return qty;
  }

  public void setHarga(String harga) {
    this.harga = harga;
  }

  public void setJumlah(String jumlah) {
    this.jumlah = jumlah;
  }

  public void setKodeBarang(String kodeBarang) {
    this.kodeBarang = kodeBarang;
  }

  public void setNamaBarang(String namaBarang) {
    this.namaBarang = namaBarang;
  }

  public void setQty(String qty) {
    this.qty = qty;
  }

}
