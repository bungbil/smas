package billy.webui.report.piutangm3.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import billy.backend.model.Karyawan;
import billy.backend.service.CompanyProfileService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.report.piutangm3.model.ReportPiutangM3;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PiutangM3TextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PiutangM3TextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private final BigDecimal totalAkhirTabungan = BigDecimal.ZERO;

  private final Double totalQty = 0.0;
  private final Double totalAkhirQty = 0.0;

  private final BigDecimal totalKomisi = BigDecimal.ZERO;
  private final BigDecimal totalAkhirKomisi = BigDecimal.ZERO;

  private final BigDecimal totalNilaiJual = BigDecimal.ZERO;
  private final BigDecimal totalAkhirNilaiJual = BigDecimal.ZERO;

  private BigDecimal totalAngsuran = BigDecimal.ZERO;
  private final BigDecimal totalAkhirAngsuran = BigDecimal.ZERO;

  private final int pageWidth = 80;

  private final int WIDTH_COLUMN_SEPERATE = 1;
  private final int WIDTH_COLUMN_A = 4;
  private final int WIDTH_COLUMN_B = 12;
  private final int WIDTH_COLUMN_C = 20;
  private final int WIDTH_COLUMN_D = 15;
  private final int WIDTH_COLUMN_E = 10;
  private final int WIDTH_COLUMN_F = 13;

  private final int WIDTH_FOOTER_COLUMN_A = 39;
  private final int WIDTH_FOOTER_COLUMN_B = 40;

  DecimalFormat df = new DecimalFormat("#,###");

  public PiutangM3TextPrinter(Component parent, Karyawan karyawan, Date startDate, Date endDate,
      List<ReportPiutangM3> listPenjualan, PrintService selectedPrinter)
      throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(karyawan, startDate, endDate, listPenjualan, selectedPrinter);
    } catch (final Exception e) {
      ZksampleMessageUtils.showErrorMessage(e.toString());
    }
  }

  private void addDoubleBorder(StringBuffer sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append("=");
    }
  }

  private void addNewLine(StringBuffer sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append("\n");
    }
  }

  private void addSingleBorder(StringBuffer sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append("-");
    }
  }

  private void addWhiteSpace(StringBuffer sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append(" ");
    }
  }

  public void doPrint(Karyawan karyawan, Date startDate, Date endDate,
      List<ReportPiutangM3> listPenjualan, PrintService selectedPrinter) throws PrintException,
      IOException {

    InputStream is =
        new ByteArrayInputStream(generateData(karyawan, startDate, endDate, listPenjualan)
            .getBytes("UTF8"));
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

  private String generateData(Karyawan karyawan, Date startDate, Date endDate,
      List<ReportPiutangM3> listItem) {
    StringBuffer sb = new StringBuffer();

    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    String companyName = companyService.getAllCompanyProfiles().get(0).getCompanyName();
    String companyAddress = companyService.getAllCompanyProfiles().get(0).getAddress();
    int itemPerPage = 54;
    int totalPage = roundUp(listItem.size(), itemPerPage);

    totalAngsuran = BigDecimal.ZERO;

    for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
      totalAngsuran = BigDecimal.ZERO;
      generateHeaderReport(sb, karyawan, startDate, endDate, pageNo, companyName, companyAddress);
      generateDataReport(sb, listItem, itemPerPage, pageNo);
      generateFooterReport(sb);

    }
    generateLastFooterReport(sb, karyawan);

    return sb.toString();
  }

  private void generateDataReport(StringBuffer sb, List<ReportPiutangM3> listItem, int itemPerPage,
      int pageNo) {

    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }

    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());


    for (int i = startIndex; i < maxIndex; i++) {
      ReportPiutangM3 item = listItem.get(i);

      setAlignLeft(sb, WIDTH_COLUMN_A, item.getNo());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_B, item.getNoFaktur());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String namaPelanggan = item.getNamaCustomer();
      if (namaPelanggan.length() > WIDTH_COLUMN_C) {
        namaPelanggan = namaPelanggan.subSequence(0, WIDTH_COLUMN_C).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_C, namaPelanggan);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiAngsuranStr = df.format(item.getSisaPiutang());
      setAlignRight(sb, WIDTH_COLUMN_D, nilaiAngsuranStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String startDateStr = formatDate.format(item.getTglBayarTerakhir());
      setAlignRight(sb, WIDTH_COLUMN_E, startDateStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_F, item.getNamaKolektorBayarTerakhir());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      totalAngsuran = totalAngsuran.add(item.getSisaPiutang());

      addNewLine(sb, 1);
    }
  }

  private void generateFooterReport(StringBuffer sb) {

    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_A, "Total Tagihan M3 per halaman");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalAngsuranStr = df.format(totalAngsuran);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_B, totalAngsuranStr);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 3);
  }

  private void generateHeaderReport(StringBuffer sb, Karyawan karyawan, Date startDate,
      Date endDate, int pageNo, String companyName, String companyAddress) {
    Date printDate = new Date();
    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    SimpleDateFormat formatHour = new SimpleDateFormat();
    formatHour = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
    String printDateStr = "TGL : " + formatDate.format(printDate);
    String printHourStr = "JAM : " + formatHour.format(printDate);
    String halStr = "HAL : " + pageNo;
    String titleReport = "LAPORAN PIUTANG M3";
    String startDateStr = formatDate.format(startDate);
    String endDateStr = formatDate.format(endDate);


    int maxLengthTglPrint = 65;
    setAlignLeft(sb, maxLengthTglPrint, companyName);
    sb.append(printDateStr);
    addNewLine(sb, 1); //
    addWhiteSpace(sb, 20);
    sb.append(titleReport);
    addWhiteSpace(sb, maxLengthTglPrint - titleReport.length() - 20);
    sb.append(printHourStr);
    addNewLine(sb, 1);
    String tglPenjualan = "Tanggal Penjualan : " + startDateStr + " s/d " + endDateStr;
    sb.append(tglPenjualan);
    addWhiteSpace(sb, maxLengthTglPrint - tglPenjualan.length());
    sb.append(halStr);
    addNewLine(sb, 1);
    sb.append("Divisi : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan());


    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);


    setAlignLeft(sb, WIDTH_COLUMN_A, "No");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_B, "No Faktur");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_C, "Pelanggan");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_D, "Jumlah Tagihan");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_E, "Tgl Bayar Terakhir");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_F, "Kolektor Terakhir");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);
  }

  private void generateLastFooterReport(StringBuffer sb, Karyawan karyawan) {

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_A, "Total Semua Tagihan M3");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalAkhirAngsuranStr = df.format(totalAkhirAngsuran);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_B, totalAkhirAngsuranStr);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);

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
