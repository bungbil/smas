package billy.webui.report.piutangm3.model;

import java.math.BigDecimal;
import java.util.Date;

public class ReportPiutangM3 {
  private String no;
  private String noFaktur;
  private String namaCustomer;
  private Date tglBayarTerakhir;
  private BigDecimal sisaPiutang;
  private String namaKolektorBayarTerakhir;


  public ReportPiutangM3() {

  }


  public ReportPiutangM3(String no, String noFaktur, String namaCustomer, Date tglBayarTerakhir,
      BigDecimal sisaPiutang, String namaKolektorBayarTerakhir) {
    super();
    this.no = no;
    this.noFaktur = noFaktur;
    this.namaCustomer = namaCustomer;
    this.tglBayarTerakhir = tglBayarTerakhir;
    this.sisaPiutang = sisaPiutang;
    this.namaKolektorBayarTerakhir = namaKolektorBayarTerakhir;
  }


  public String getNamaCustomer() {
    return namaCustomer;
  }


  public String getNamaKolektorBayarTerakhir() {
    return namaKolektorBayarTerakhir;
  }


  public String getNo() {
    return no;
  }


  public String getNoFaktur() {
    return noFaktur;
  }


  public BigDecimal getSisaPiutang() {
    return sisaPiutang;
  }


  public Date getTglBayarTerakhir() {
    return tglBayarTerakhir;
  }


  public void setNamaCustomer(String namaCustomer) {
    this.namaCustomer = namaCustomer;
  }


  public void setNamaKolektorBayarTerakhir(String namaKolektorBayarTerakhir) {
    this.namaKolektorBayarTerakhir = namaKolektorBayarTerakhir;
  }


  public void setNo(String no) {
    this.no = no;
  }


  public void setNoFaktur(String noFaktur) {
    this.noFaktur = noFaktur;
  }


  public void setSisaPiutang(BigDecimal sisaPiutang) {
    this.sisaPiutang = sisaPiutang;
  }


  public void setTglBayarTerakhir(Date tglBayarTerakhir) {
    this.tglBayarTerakhir = tglBayarTerakhir;
  }


  @Override
  public String toString() {
    return String
        .format(
            "ReportPiutangM3 [no=%s, noFaktur=%s, namaCustomer=%s, tglBayarTerakhir=%s, sisaPiutang=%s, namaKolektorBayarTerakhir=%s]",
            no, noFaktur, namaCustomer, tglBayarTerakhir, sisaPiutang, namaKolektorBayarTerakhir);
  }


}
