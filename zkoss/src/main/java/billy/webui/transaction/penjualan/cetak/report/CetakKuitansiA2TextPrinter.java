package billy.webui.transaction.penjualan.cetak.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class CetakKuitansiA2TextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(CetakKuitansiA2TextPrinter.class);
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


  public CetakKuitansiA2TextPrinter(Component parent, List<Penjualan> listPenjualan,
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
    Locale id = new Locale("in", "ID");
    formatDate = new SimpleDateFormat("dd MMMM yyyy", id);
    PenjualanService as = (PenjualanService) SpringUtil.getBean("penjualanService");
    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    List<Kuitansi> listKuitansi = new ArrayList<Kuitansi>();
    for (Penjualan penjualan : listPenjualan) {
      if (penjualan.getIntervalKredit() > 1) {
        Kuitansi kuitansi = new Kuitansi();

        Calendar cal = Calendar.getInstance();
        cal.setTime(penjualan.getTglAngsuran2());
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        String monthString = String.valueOf(month);
        String dateString = String.valueOf(date);
        if (dateString.length() == 1) {
          dateString = "0" + dateString;
        }
        if (monthString.length() == 1) {
          monthString = "0" + monthString;
        }
        kuitansi.setNomorKuitansi(dateString + "." + monthString + "." + penjualan.getNoFaktur());
        kuitansi.setMandiri(penjualan.getMandiriId().getKodeMandiri());
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


        kuitansi.setAlamat4(penjualan.getAlamat2());
        kuitansi.setAlamat5(penjualan.getAlamat3());
        kuitansi.setTelepon(penjualan.getTelepon());

        if (kuitansi.getAlamat2().isEmpty()) {
          kuitansi.setAlamat2(penjualan.getAlamat2());
          kuitansi.setAlamat3(penjualan.getAlamat3());
          kuitansi.setAlamat4(penjualan.getTelepon());
          kuitansi.setAlamat5("");
          kuitansi.setTelepon("");
        } else if (kuitansi.getAlamat3().isEmpty()) {
          kuitansi.setAlamat3(penjualan.getAlamat2());
          kuitansi.setAlamat4(penjualan.getAlamat3());
          kuitansi.setAlamat5(penjualan.getTelepon());
          kuitansi.setTelepon("");
        }

        String jumlahInWord =
            "# " + angkaToTerbilang(penjualan.getKreditPerBulan().longValue()) + " #";
        kuitansi.setJumlahInWord("");
        kuitansi.setJumlahInWord2("");

        sb = new StringBuilder(jumlahInWord);
        i = 0;
        while (i + 60 < sb.length() && (i = sb.lastIndexOf(" ", i + 60)) != -1) {
          sb.replace(i, i + 1, "\n");
        }
        String[] jumlahInWordStr = sb.toString().split("\n");
        length = jumlahInWordStr.length;
        for (int k = 0; k < length; k++) {
          if (k == 0) {
            kuitansi.setJumlahInWord(jumlahInWordStr[0]);
          } else if (k == 1) {
            kuitansi.setJumlahInWord2(jumlahInWordStr[1]);
          }
        }

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
          kuitansi.tambahItemFaktur(new ItemFaktur(detail.getBarang().getKodeBarang(), namaBarang,
              String.valueOf(detail.getQty()), df.format(detail.getHarga()), df.format(detail
                  .getTotal())));
        }

        listKuitansi.add(kuitansi);
      }
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
    // InputStream ff = new ByteArrayInputStream("\f".getBytes());
    // Doc docff = new SimpleDoc(ff, flavor, null);
    // DocPrintJob jobff = selectedPrinter.createPrintJob();
    // pjw = new PrintJobWatcher(jobff);
    // jobff.print(docff, null);
    // pjw.waitForDone();

  }

  private String generateData(List<Kuitansi> listKuitansi) {
    StringBuffer sb = new StringBuffer();

    for (Kuitansi kuitansi : listKuitansi) {
      addNewLine(sb, 2);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamatKantor());
      int maxLengthAlamatKantor = 60;
      addWhiteSpace(sb, maxLengthAlamatKantor - kuitansi.getAlamatKantor().length());
      sb.append(kuitansi.getMandiri());

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamatKantor2());

      addNewLine(sb, 1);
      int maxLengthSales = 75;
      setAlignRight(sb, maxLengthSales, kuitansi.getNamaSales1() + " / " + kuitansi.getNamaSales2());

      addNewLine(sb, 2);
      addWhiteSpace(sb, 47);
      sb.append(kuitansi.getNomorKuitansi());
      addNewLine(sb, 2);

      int sizeItem = kuitansi.getListItemFaktur().size();
      int maxLengthAlamat = 35;
      int maxLengthBarang = 22;
      int maxLengthKodeBarang = 7;

      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getNamaPelanggan());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getNamaPelanggan().length());

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamat());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getAlamat().length());

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamat2());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getAlamat2().length());
      if (sizeItem >= 1) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(0);
        setAlignRight(sb, maxLengthKodeBarang, item.getKodeBarang());
        addWhiteSpace(sb, 1);
        String namaBarang = item.getNamaBarang();
        if (namaBarang.length() > maxLengthBarang) {
          namaBarang.subSequence(0, maxLengthBarang).toString();
        }
        setAlignLeft(sb, maxLengthBarang, namaBarang);
        addWhiteSpace(sb, 1);
        sb.append(item.getQty());
      }

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamat3());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getAlamat3().length());
      if (sizeItem >= 2) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(1);
        setAlignRight(sb, maxLengthKodeBarang, item.getKodeBarang());
        addWhiteSpace(sb, 1);
        String namaBarang = item.getNamaBarang();
        if (namaBarang.length() > maxLengthBarang) {
          namaBarang.subSequence(0, maxLengthBarang).toString();
        }
        setAlignLeft(sb, maxLengthBarang, namaBarang);
        addWhiteSpace(sb, 1);
        sb.append(item.getQty());
      }

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamat4());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getAlamat4().length());
      if (sizeItem >= 3) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(2);
        setAlignRight(sb, maxLengthKodeBarang, item.getKodeBarang());
        addWhiteSpace(sb, 1);
        String namaBarang = item.getNamaBarang();
        if (namaBarang.length() > maxLengthBarang) {
          namaBarang.subSequence(0, maxLengthBarang).toString();
        }
        setAlignLeft(sb, maxLengthBarang, namaBarang);
        addWhiteSpace(sb, 1);
        sb.append(item.getQty());
      }

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getAlamat5());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getAlamat5().length());
      if (sizeItem >= 4) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(3);
        setAlignRight(sb, maxLengthKodeBarang, item.getKodeBarang());
        addWhiteSpace(sb, 1);
        String namaBarang = item.getNamaBarang();
        if (namaBarang.length() > maxLengthBarang) {
          namaBarang.subSequence(0, maxLengthBarang).toString();
        }
        setAlignLeft(sb, maxLengthBarang, namaBarang);
        addWhiteSpace(sb, 1);
        sb.append(item.getQty());
      }

      addNewLine(sb, 1);
      addWhiteSpace(sb, 3);
      sb.append(kuitansi.getTelepon());
      addWhiteSpace(sb, maxLengthAlamat - kuitansi.getTelepon().length());
      if (sizeItem >= 5) {
        ItemFaktur item = kuitansi.getListItemFaktur().get(4);
        setAlignRight(sb, maxLengthKodeBarang, item.getKodeBarang());
        addWhiteSpace(sb, 1);
        String namaBarang = item.getNamaBarang();
        if (namaBarang.length() > maxLengthBarang) {
          namaBarang.subSequence(0, maxLengthBarang).toString();
        }
        setAlignLeft(sb, maxLengthBarang, namaBarang);
        addWhiteSpace(sb, 1);
        sb.append(item.getQty());
      }

      addNewLine(sb, 3);
      addWhiteSpace(sb, 17);
      sb.append(kuitansi.getJumlahInWord());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 17);
      sb.append(kuitansi.getJumlahInWord2());
      addNewLine(sb, 2);
      addWhiteSpace(sb, 13);
      sb.append(kuitansi.getJumlah());
      addWhiteSpace(sb, 16);
      sb.append(kuitansi.getAngsuranKe());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 20);
      sb.append(kuitansi.getSisaPembayaran());
      addNewLine(sb, 2);
      addWhiteSpace(sb, 58);
      sb.append(kuitansi.getTglAngsuran());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 6);
      sb.append(kuitansi.getNamaKolektor());
      int maxLengthKolektor = 40;
      addWhiteSpace(sb, maxLengthKolektor - kuitansi.getNamaKolektor().length());
      sb.append(kuitansi.getNamaSupervisor());
      addNewLine(sb, 5);

    }

    return sb.toString();
  }

  private void setAlignLeft(StringBuffer sb, int width, String value) {
    sb.append(value);
    addWhiteSpace(sb, width - value.length());
  }

  private void setAlignRight(StringBuffer sb, int width, String value) {
    addWhiteSpace(sb, width - value.length());
    sb.append(value);
  }
}
