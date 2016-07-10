package billy.webui.transaction.penjualan.cetak.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.printer.model.ItemFaktur;
import billy.webui.printer.model.Kuitansi;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CetakKuitansiTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(CetakKuitansiTextPrinter.class);
  static String[] angkaTerbilang = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh",
      "Delapan", "Sembilan", "Sepuluh", "Sebelas"};

  public static String angkaToTerbilang(Long angka) {
    if (angka < 12)
      return angkaTerbilang[angka.intValue()];
    if (angka >= 12 && angka <= 19)
      return angkaTerbilang[angka.intValue() % 10] + " Belas";
    if (angka >= 20 && angka <= 99)
      return angkaToTerbilang(angka / 10) + " Puluh " + angkaTerbilang[angka.intValue() % 10];
    if (angka >= 100 && angka <= 199)
      return "Seratus " + angkaToTerbilang(angka % 100);
    if (angka >= 200 && angka <= 999)
      return angkaToTerbilang(angka / 100) + " Ratus " + angkaToTerbilang(angka % 100);
    if (angka >= 1000 && angka <= 1999)
      return "Seribu " + angkaToTerbilang(angka % 1000);
    if (angka >= 2000 && angka <= 999999)
      return angkaToTerbilang(angka / 1000) + " Ribu " + angkaToTerbilang(angka % 1000);
    if (angka >= 1000000 && angka <= 999999999)
      return angkaToTerbilang(angka / 1000000) + " Juta " + angkaToTerbilang(angka % 1000000);
    if (angka >= 1000000000 && angka <= 999999999999L)
      return angkaToTerbilang(angka / 1000000000) + " Milyar "
          + angkaToTerbilang(angka % 1000000000);
    if (angka >= 1000000000000L && angka <= 999999999999999L)
      return angkaToTerbilang(angka / 1000000000000L) + " Triliun "
          + angkaToTerbilang(angka % 1000000000000L);
    if (angka >= 1000000000000000L && angka <= 999999999999999999L)
      return angkaToTerbilang(angka / 1000000000000000L) + " Quadrilyun "
          + angkaToTerbilang(angka % 1000000000000000L);
    return "";
  }


  public CetakKuitansiTextPrinter(Component parent, List<Penjualan> listPenjualan,
      PrintService selectedPrinter) throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(listPenjualan, selectedPrinter);
    } catch (final Exception e) {
      ZksampleMessageUtils.showErrorMessage(e.toString());
    }
  }

  private void addNewLine(StringBuffer sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append("\n");
    }
  }

  private void addWhiteSpace(StringBuffer sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append(" ");
    }
  }

  public void doPrint(List<Penjualan> listPenjualan, PrintService selectedPrinter)
      throws PrintException, IOException {
    DecimalFormat df = new DecimalFormat("#,###");
    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    PenjualanService as = (PenjualanService) SpringUtil.getBean("penjualanService");
    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    List<Kuitansi> listKuitansi = new ArrayList<Kuitansi>();
    for (Penjualan penjualan : listPenjualan) {
      Kuitansi kuitansi = new Kuitansi();
      kuitansi.setNomorFaktur(penjualan.getNoFaktur());
      kuitansi.setMandiri(penjualan.getMandiri());
      kuitansi.setNamaSales1(penjualan.getSales1().getNamaPanggilan() + "("
          + penjualan.getSales1().getSupervisorDivisi().getInisialDivisi() + ")");
      if (penjualan.getSales2() != null) {
        kuitansi.setNamaSales2(penjualan.getSales2().getNamaPanggilan() + "("
            + penjualan.getSales2().getSupervisorDivisi().getInisialDivisi() + ")");
      } else {
        kuitansi.setNamaSales2("");
      }
      kuitansi.setAlamatKantor(companyService.getAllCompanyProfiles().get(0).getAddress());
      kuitansi.setAlamatKantor2(companyService.getAllCompanyProfiles().get(0).getPhone());
      kuitansi.setNamaPelanggan(penjualan.getNamaPelanggan());
      kuitansi.setAlamat("");
      kuitansi.setAlamat2("");
      kuitansi.setAlamat3("");

      StringBuilder sb = new StringBuilder(penjualan.getAlamat());
      int i = 0;
      while (i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 35)) != -1) {
        sb.replace(i, i + 1, "\n");
      }
      String[] alamat = sb.toString().split("\n");
      int length = alamat.length;
      for (int k = 0; k < length; k++) {
        if (k == 0) {
          kuitansi.setAlamat(alamat[0]);
        } else if (k == 1) {
          kuitansi.setAlamat2(alamat[1]);
        } else if (k == 2) {
          kuitansi.setAlamat3(alamat[2]);
        }
      }
      kuitansi.setTelepon(penjualan.getTelepon());
      kuitansi.setJumlahInWord("# " + angkaToTerbilang(penjualan.getKreditPerBulan().longValue())
          + " #");
      kuitansi.setJumlah(df.format(penjualan.getKreditPerBulan()));
      int sisaBulan = penjualan.getIntervalKredit() - 2;
      BigDecimal sisaPiutang = penjualan.getPiutang().subtract(penjualan.getKreditPerBulan());
      kuitansi.setSisaPembayaran(df.format(penjualan.getKreditPerBulan()) + " : SELAMA "
          + sisaBulan + " BULAN , Total Rp. " + df.format(sisaPiutang));
      kuitansi.setAngsuranKe("2");
      kuitansi.setTglAngsuran(formatDate.format(penjualan.getTglAngsuran2()));
      kuitansi.setNamaSupervisor(penjualan.getDivisi().getSupervisorDivisi().getNamaPanggilan());
      kuitansi.setNamaKolektor("");
      if (penjualan.getKolektor() != null) {
        kuitansi.setNamaKolektor(penjualan.getKolektor().getNamaPanggilan());
      }
      List<PenjualanDetail> listPenjualanDetail = as.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail detail : listPenjualanDetail) {
        String namaBarang = detail.getBarang().getNamaBarang();
        if (namaBarang.length() > 18) {
          namaBarang = namaBarang.substring(0, 17);
        }
        kuitansi.tambahItemFaktur(new ItemFaktur(namaBarang, String.valueOf(detail.getQty()), df
            .format(detail.getHarga()), df.format(detail.getTotal())));
      }

      listKuitansi.add(kuitansi);
    }

    // prints the famous hello world! plus a form feed
    InputStream is = new ByteArrayInputStream(generateData(listKuitansi).getBytes("UTF8"));
    // InputStream is = new ByteArrayInputStream("hello world!\f".getBytes("UTF8"));
    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    pras.add(new Copies(1));

    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    Doc doc = new SimpleDoc(is, flavor, null);
    DocPrintJob job = selectedPrinter.createPrintJob();

    PrintJobWatcher pjw = new PrintJobWatcher(job);
    job.print(doc, pras);
    pjw.waitForDone();
    is.close();

    // send FF to eject the page
    InputStream ff = new ByteArrayInputStream("\f".getBytes());
    Doc docff = new SimpleDoc(ff, flavor, null);
    DocPrintJob jobff = selectedPrinter.createPrintJob();
    pjw = new PrintJobWatcher(jobff);
    jobff.print(docff, null);
    pjw.waitForDone();

  }

  private String generateData(List<Kuitansi> listKuitansi) {
    StringBuffer sb = new StringBuffer();

    for (Kuitansi kuitansi : listKuitansi) {
      addNewLine(sb, 3);
      addWhiteSpace(sb, 5);
      sb.append(kuitansi.getAlamatKantor());
      int maxLengthAlamatKantor = 60;
      addWhiteSpace(sb, maxLengthAlamatKantor - kuitansi.getAlamatKantor().length());
      sb.append(kuitansi.getMandiri());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append(kuitansi.getAlamatKantor2());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 52);
      sb.append(kuitansi.getNamaSales1() + " / " + kuitansi.getNamaSales2());
      addNewLine(sb, 2);
      addWhiteSpace(sb, 55);
      sb.append(kuitansi.getNomorFaktur());
      addNewLine(sb, 4);

      int sizeItem = kuitansi.getListItemFaktur().size();
      addWhiteSpace(sb, 5);
      sb.append(kuitansi.getNamaPelanggan());
      int maxLengthNama = 45;
      addWhiteSpace(sb, maxLengthNama - kuitansi.getNamaPelanggan().length());
      if (sizeItem >= 1) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(0);
        sb.append("1.");
        addWhiteSpace(sb, 1);
        sb.append(item.getNamaBarang());
        int maxLengthBarang = 18;
        addWhiteSpace(sb, maxLengthBarang - item.getNamaBarang().length());
        sb.append(item.getQty());
      }
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append(kuitansi.getAlamat());
      int maxLengthAlamat = 45;
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getAlamat().length());
      if (sizeItem >= 2) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(1);
        sb.append("2.");
        addWhiteSpace(sb, 1);
        sb.append(item.getNamaBarang());
        int maxLengthBarang = 18;
        addWhiteSpace(sb, maxLengthBarang - item.getNamaBarang().length());
        sb.append(item.getQty());
      }
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append(kuitansi.getAlamat2());
      int maxLengthAlamat2 = 45;
      addWhiteSpace(sb, maxLengthAlamat2 - kuitansi.getAlamat2().length());
      if (sizeItem >= 3) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(2);
        sb.append("3.");
        addWhiteSpace(sb, 1);
        sb.append(item.getNamaBarang());
        int maxLengthBarang = 18;
        addWhiteSpace(sb, maxLengthBarang - item.getNamaBarang().length());
        sb.append(item.getQty());
      }
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append(kuitansi.getAlamat3());
      int maxLengthAlamat3 = 45;
      addWhiteSpace(sb, maxLengthAlamat3 - kuitansi.getAlamat3().length());
      if (sizeItem >= 4) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(3);
        sb.append("4.");
        addWhiteSpace(sb, 1);
        sb.append(item.getNamaBarang());
        int maxLengthBarang = 18;
        addWhiteSpace(sb, maxLengthBarang - item.getNamaBarang().length());
        sb.append(item.getQty());
      }

      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append("(" + kuitansi.getNamaKolektor() + ")");
      addNewLine(sb, 3);
      addWhiteSpace(sb, 20);
      sb.append(kuitansi.getJumlahInWord());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 15);
      sb.append(kuitansi.getJumlah());
      addWhiteSpace(sb, 18);
      sb.append(kuitansi.getAngsuranKe());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 20);
      sb.append(kuitansi.getSisaPembayaran());
      addNewLine(sb, 2);
      addWhiteSpace(sb, 60);
      sb.append(kuitansi.getTglAngsuran());
      addNewLine(sb, 4);
      addWhiteSpace(sb, 6);
      sb.append(kuitansi.getNamaKolektor());
      int maxLengthKolektor = 17;
      addWhiteSpace(sb, maxLengthKolektor - kuitansi.getNamaKolektor().length());
      sb.append(kuitansi.getNamaSupervisor());
      addNewLine(sb, 3);

    }

    return sb.toString();
  }
}
