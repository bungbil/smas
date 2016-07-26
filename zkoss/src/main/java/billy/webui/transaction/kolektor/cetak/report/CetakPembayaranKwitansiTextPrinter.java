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

public class CetakPembayaranKwitansiTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(CetakPembayaranKwitansiTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private final int pageLength = 50;
  private final int pageWidth = 80;
  private BigDecimal totalTagih = BigDecimal.ZERO;
  private BigDecimal totalPembayaran = BigDecimal.ZERO;
  private BigDecimal totalAkhirTagih = BigDecimal.ZERO;
  private BigDecimal totalAkhirPembayaran = BigDecimal.ZERO;


  public CetakPembayaranKwitansiTextPrinter(Component parent, Karyawan karyawan, Date startDate,
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
      PrintService selectedPrinter) throws PrintException, IOException {

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
      List<ReportKwitansi> listItem) {
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
      int pageNo) {
    DecimalFormat df = new DecimalFormat("#,###");
    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      ReportKwitansi item = listItem.get(i);

      int maxLengthNomor = 5;
      addWhiteSpace(sb, maxLengthNomor - item.getNo().length());
      sb.append(item.getNo());
      addWhiteSpace(sb, 1);

      int maxLengthNomorFaktur = 13;
      sb.append(item.getNoFaktur());
      addWhiteSpace(sb, maxLengthNomorFaktur - item.getNoFaktur().length());

      int maxLengthNamaCustomer = 21;
      sb.append(item.getNamaCustomer());
      addWhiteSpace(sb, maxLengthNamaCustomer - item.getNamaCustomer().length());

      String nilaiTagihStr = df.format(item.getNilaiTagih());
      int maxLengthNilaiTagih = 11;
      addWhiteSpace(sb, maxLengthNilaiTagih - nilaiTagihStr.length());
      sb.append(nilaiTagihStr);

      // addWhiteSpace(sb, 16);

      String nilaiPembayaranStr = df.format(item.getNilaiPembayaran());
      int maxLengthNilaiPembayaran = 18;
      addWhiteSpace(sb, maxLengthNilaiPembayaran - nilaiPembayaranStr.length());
      sb.append(nilaiPembayaranStr);


      // addWhiteSpace(sb, 3);
      // sb.append(item.getKeterangan());

      addWhiteSpace(sb, 3);
      String tglBayarStr = formatDate.format(item.getTglBayar());
      sb.append(tglBayarStr);
      addNewLine(sb, 1);

      totalTagih = totalTagih.add(item.getNilaiTagih());
      totalAkhirTagih = totalAkhirTagih.add(item.getNilaiTagih());
      totalPembayaran = totalPembayaran.add(item.getNilaiPembayaran());
      totalAkhirPembayaran = totalAkhirPembayaran.add(item.getNilaiPembayaran());
    }

  }

  private void generateFooterReport(StringBuffer sb) {
    DecimalFormat df = new DecimalFormat("#,###");

    addNewLine(sb, 1);
    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, 16);
    sb.append("Total per halaman");
    int maxLengthTotalPerHal = 18;
    String totalTagihStr = df.format(totalTagih);
    addWhiteSpace(sb, maxLengthTotalPerHal - totalTagihStr.length());
    sb.append(totalTagihStr);


    int maxLengthTotalBayarPerHal = 18;
    String totalBayarStr = df.format(totalPembayaran);
    addWhiteSpace(sb, maxLengthTotalBayarPerHal - totalBayarStr.length());
    sb.append(totalBayarStr);


    addNewLine(sb, 1);
    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, 16);
    sb.append("Total Akhir");
    int maxLengthTotalAkhir = 24;
    String totalAkhirTagihStr = df.format(totalAkhirTagih);
    addWhiteSpace(sb, maxLengthTotalAkhir - totalAkhirTagihStr.length());
    sb.append(totalAkhirTagihStr);

    int maxLengthTotalAkhirBayar = 18;
    String totalAkhirBayarStr = df.format(totalAkhirPembayaran);
    addWhiteSpace(sb, maxLengthTotalAkhirBayar - totalAkhirBayarStr.length());
    sb.append(totalAkhirBayarStr);


    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);

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
    String titleReport = "LAPORAN PEMBAYARAN KWITANSI";
    String startDateStr = formatDate.format(startDate);
    String endDateStr = formatDate.format(endDate);

    sb.append(companyName);
    int maxLengthTglPrint = 65;
    addWhiteSpace(sb, maxLengthTglPrint - companyName.length());
    sb.append(printDateStr);
    addNewLine(sb, 1);
    sb.append(companyAddress);
    addWhiteSpace(sb, maxLengthTglPrint - companyAddress.length());
    sb.append(printHourStr);
    addNewLine(sb, 1);
    addWhiteSpace(sb, 20);
    sb.append(titleReport);
    addWhiteSpace(sb, maxLengthTglPrint - titleReport.length() - 20);
    sb.append(halStr);
    addNewLine(sb, 2);

    sb.append("Tanggal Bayar : " + startDateStr + " s/d " + endDateStr);
    addNewLine(sb, 1);
    sb.append("Kolektor : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan());
    addWhiteSpace(sb, 10);
    sb.append("Status : Sudah dilunasi !");

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    sb.append("Nomor");
    addWhiteSpace(sb, 1);
    sb.append("Nomor Faktur");
    addWhiteSpace(sb, 1);
    sb.append("Nama Customer");
    addWhiteSpace(sb, 8);
    sb.append("Nilai Tagih");
    addWhiteSpace(sb, 2);
    sb.append("Nilai Pembayaran");
    addWhiteSpace(sb, 2);
    // sb.append("Keterangan");
    sb.append("Tgl Bayar");

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
}
