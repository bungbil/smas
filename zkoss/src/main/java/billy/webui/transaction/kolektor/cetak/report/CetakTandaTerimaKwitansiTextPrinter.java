package billy.webui.transaction.kolektor.cetak.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import billy.backend.model.Piutang;
import billy.backend.service.CompanyProfileService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.transaction.kolektor.cetak.model.ReportKwitansi;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CetakTandaTerimaKwitansiTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(CetakTandaTerimaKwitansiTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private final int pageLength = 50;
  private final int pageWidth = 80;
  private BigDecimal totalTagih = BigDecimal.ZERO;
  private BigDecimal totalPembayaran = BigDecimal.ZERO;
  private BigDecimal totalAkhirTagih = BigDecimal.ZERO;
  private BigDecimal totalAkhirPembayaran = BigDecimal.ZERO;
  private final int WIDTH_COLUMN_SEPERATE = 1;
  private final int WIDTH_COLUMN_A = 5;
  private final int WIDTH_COLUMN_B = 12;
  private final int WIDTH_COLUMN_C = 19;
  private final int WIDTH_COLUMN_D = 11;
  private final int WIDTH_COLUMN_E = 16;
  private final int WIDTH_COLUMN_F = 9;

  private final int WIDTH_FOOTER_COLUMN_A = 16;
  private final int WIDTH_FOOTER_COLUMN_B = 17;
  private final int WIDTH_FOOTER_COLUMN_C = 18;
  private final int WIDTH_FOOTER_COLUMN_D = 17;

  DecimalFormat df = new DecimalFormat("#,###");

  public CetakTandaTerimaKwitansiTextPrinter(Component parent, Karyawan karyawan, Date startDate,
      Date endDate, List<Piutang> listPiutang, PrintService selectedPrinter)
      throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(karyawan, startDate, endDate, listPiutang, selectedPrinter);
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

  public void doPrint(Karyawan karyawan, Date startDate, Date endDate, List<Piutang> listPiutang,
      PrintService selectedPrinter) throws PrintException, IOException, Exception {

    List<ReportKwitansi> listData = new ArrayList<ReportKwitansi>();
    int i = 1;
    for (Piutang piutang : listPiutang) {
      ReportKwitansi data = new ReportKwitansi();
      data.setNo(i + ".");
      data.setNoFaktur(piutang.getPenjualan().getNoFaktur());
      data.setTglBawa(piutang.getTglBawaKolektor());
      data.setTglBayar(piutang.getTglPembayaran());
      data.setTglKuitansi(piutang.getTglJatuhTempo());
      data.setNamaCustomer(piutang.getPenjualan().getNamaPelanggan());
      data.setNilaiPembayaran(piutang.getPembayaran());
      data.setNilaiTagih(piutang.getNilaiTagihan());
      data.setKeterangan(piutang.getKeterangan());

      listData.add(data);
      i++;
    }

    InputStream is =
        new ByteArrayInputStream(generateData(karyawan, startDate, endDate, listData).getBytes(
            "UTF8"));
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
      List<ReportKwitansi> listItem) throws Exception {
    StringBuffer sb = new StringBuffer();

    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    String companyName = companyService.getAllCompanyProfiles().get(0).getCompanyName();
    String companyAddress = companyService.getAllCompanyProfiles().get(0).getAddress();
    int itemPerPage = 40;
    int totalPage = roundUp(listItem.size(), itemPerPage);
    totalTagih = BigDecimal.ZERO;
    totalPembayaran = BigDecimal.ZERO;
    totalAkhirTagih = BigDecimal.ZERO;
    totalAkhirPembayaran = BigDecimal.ZERO;

    for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
      totalTagih = BigDecimal.ZERO;
      totalPembayaran = BigDecimal.ZERO;

      generateHeaderReport(sb, karyawan, startDate, endDate, pageNo, companyName, companyAddress);
      generateDataReport(sb, listItem, itemPerPage, pageNo);
      generateFooterReport(sb);

    }
    generateLastFooterReport(sb);

    return sb.toString();
  }

  private void generateDataReport(StringBuffer sb, List<ReportKwitansi> listItem, int itemPerPage,
      int pageNo) throws Exception {

    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      ReportKwitansi item = listItem.get(i);

      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
      setAlignRight(sb, WIDTH_COLUMN_A, item.getNo());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);


      setAlignLeft(sb, WIDTH_COLUMN_B, item.getNoFaktur());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_C, item.getNamaCustomer());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiTagihStr = df.format(item.getNilaiTagih());
      setAlignRight(sb, WIDTH_COLUMN_D, nilaiTagihStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPembayaranStr = "";
      if (item.getNilaiPembayaran() != null) {
        nilaiPembayaranStr = df.format(item.getNilaiPembayaran());
      }
      setAlignRight(sb, WIDTH_COLUMN_E, nilaiPembayaranStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      // String tglBayarStr = formatDate.format(item.getTglBayar());
      // setAlignLeft(sb, WIDTH_COLUMN_F, tglBayarStr);
      // addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
      // addNewLine(sb, 1);

      String keteranganStr = "";
      if (item.getKeterangan() != null) {
        keteranganStr = item.getKeterangan();
      }
      setAlignLeft(sb, WIDTH_COLUMN_F, keteranganStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
      addNewLine(sb, 1);

      totalTagih = totalTagih.add(item.getNilaiTagih());
      totalAkhirTagih = totalAkhirTagih.add(item.getNilaiTagih());
      // totalPembayaran = totalPembayaran.add(item.getNilaiPembayaran());
      // totalAkhirPembayaran = totalAkhirPembayaran.add(item.getNilaiPembayaran());
    }

  }

  private void generateFooterReport(StringBuffer sb) throws Exception {

    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, WIDTH_FOOTER_COLUMN_A);
    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_B, "Total per halaman");

    String totalTagihStr = df.format(totalTagih);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, totalTagihStr);

    // String totalBayarStr = df.format(totalPembayaran);
    // setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, totalBayarStr);

    addNewLine(sb, 1);
    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, WIDTH_FOOTER_COLUMN_A);
    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_B, "Total Akhir");

    String totalAkhirTagihStr = df.format(totalAkhirTagih);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, totalAkhirTagihStr);

    // String totalAkhirBayarStr = df.format(totalAkhirPembayaran);
    // setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, totalAkhirBayarStr);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);

  }

  private void generateHeaderReport(StringBuffer sb, Karyawan karyawan, Date startDate,
      Date endDate, int pageNo, String companyName, String companyAddress) throws Exception {

    Date printDate = new Date();
    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    SimpleDateFormat formatHour = new SimpleDateFormat();
    formatHour = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
    String printDateStr = "TGL : " + formatDate.format(printDate);
    String printHourStr = "JAM : " + formatHour.format(printDate);
    String halStr = "HAL : " + pageNo;
    String titleReport = "LAPORAN TANDA TERIMA KWITANSI";
    String startDateStr = formatDate.format(startDate);
    String endDateStr = formatDate.format(endDate);


    int maxLengthTglPrint = 65;
    setAlignLeft(sb, maxLengthTglPrint, companyName);
    sb.append(printDateStr);
    addNewLine(sb, 1);
    setAlignLeft(sb, maxLengthTglPrint, companyAddress);
    sb.append(printHourStr);
    addNewLine(sb, 1);
    addWhiteSpace(sb, 20);
    sb.append(titleReport);
    addWhiteSpace(sb, maxLengthTglPrint - titleReport.length() - 20);
    sb.append(halStr);
    addNewLine(sb, 2);

    sb.append("Tanggal Bawa : " + startDateStr + " s/d " + endDateStr);
    addNewLine(sb, 1);
    sb.append("Kolektor : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan());
    addWhiteSpace(sb, 10);
    sb.append("Status : Belum dilunasi !");

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignLeft(sb, WIDTH_COLUMN_A, "Nomor");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_B, "Nomor Faktur");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_C, "Nama Customer");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_D, "Nilai Tagih");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_E, "Nilai Pembayaran");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_F, "Keterangan");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);
  }

  private void generateLastFooterReport(StringBuffer sb) {

    addNewLine(sb, 1);
    addWhiteSpace(sb, 6);
    sb.append("Yang Menerima");
    addWhiteSpace(sb, 35);
    sb.append("Yang Memberi");
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
