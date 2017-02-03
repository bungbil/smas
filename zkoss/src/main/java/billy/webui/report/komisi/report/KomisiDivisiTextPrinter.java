package billy.webui.report.komisi.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.BonusTransportService;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.report.komisi.model.KomisiDivisi;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class KomisiDivisiTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(KomisiDivisiTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private final int pageLength = 50;
  private final int pageWidth = 80;
  private double totalUnit = 0;
  private BigDecimal totalKomisi = BigDecimal.ZERO;

  private final int WIDTH_COLUMN_SEPERATE = 1;
  private final int WIDTH_COLUMN_A = 32;
  private final int WIDTH_COLUMN_B = 8;
  private final int WIDTH_COLUMN_C = 12;
  private final int WIDTH_COLUMN_D = 12;
  private final int WIDTH_COLUMN_E = 12;

  private final int WIDTH_FOOTER_COLUMN_A = 32;
  private final int WIDTH_FOOTER_COLUMN_B = 8;
  private final int WIDTH_FOOTER_COLUMN_C = 12;
  private final int WIDTH_FOOTER_COLUMN_D = 12;
  private final int WIDTH_FOOTER_COLUMN_E = 12;

  DecimalFormat df = new DecimalFormat("#,###");

  public KomisiDivisiTextPrinter(Component parent, Karyawan karyawan, Date startDate, Date endDate,
      List<Penjualan> listPenjualan, PrintService selectedPrinter) throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(karyawan, startDate, endDate, listPenjualan, selectedPrinter);
    } catch (final Exception e) {
      e.printStackTrace();
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
      List<Penjualan> listPenjualan, PrintService selectedPrinter) throws PrintException,
      IOException {

    List<KomisiDivisi> listData = generateDataSummary(karyawan, listPenjualan);
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
      List<KomisiDivisi> listItem) {
    StringBuffer sb = new StringBuffer();

    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    String companyName = companyService.getAllCompanyProfiles().get(0).getCompanyName();
    String companyAddress = companyService.getAllCompanyProfiles().get(0).getAddress();
    int itemPerPage = 80;
    int totalPage = roundUp(listItem.size(), itemPerPage);
    totalUnit = 0;
    totalKomisi = BigDecimal.ZERO;

    for (int pageNo = 1; pageNo <= totalPage; pageNo++) {

      generateHeaderReport(sb, karyawan, startDate, endDate, pageNo, companyName, companyAddress);
      generateDataReport(sb, listItem, itemPerPage, pageNo);
      generateFooterReport(sb);

    }
    generateLastFooterReport(sb, karyawan);

    return sb.toString();
  }

  private void generateDataReport(StringBuffer sb, List<KomisiDivisi> listItem, int itemPerPage,
      int pageNo) {

    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      KomisiDivisi item = listItem.get(i);

      String namaBarang = item.getNamaBarang();
      if (namaBarang.length() > WIDTH_COLUMN_A) {
        namaBarang = namaBarang.subSequence(0, WIDTH_COLUMN_A).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_A, namaBarang);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignRight(sb, WIDTH_COLUMN_B, item.getQty() + "");
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPenjualanStr = df.format(item.getOprDivisi());
      setAlignRight(sb, WIDTH_COLUMN_C, nilaiPenjualanStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPenerimaanStr = df.format(item.getOrDivisi());
      setAlignRight(sb, WIDTH_COLUMN_D, nilaiPenerimaanStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiSisaPiutangStr = df.format(item.getJumlah());
      setAlignRight(sb, WIDTH_COLUMN_E, nilaiSisaPiutangStr);


      totalUnit += item.getQty();
      totalKomisi = totalKomisi.add(item.getJumlah());


      addNewLine(sb, 1);

    }

  }

  private List<KomisiDivisi> generateDataSummary(Karyawan karyawan, List<Penjualan> listPenjualan) {
    Map<String, KomisiDivisi> mapBarang = new HashMap<String, KomisiDivisi>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    for (Penjualan penjualan : listPenjualan) {
      List<PenjualanDetail> penjualanDetails =
          penjualanService.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail penjualanDetail : penjualanDetails) {
        if (!penjualanDetail.getBarang().isBonus()) {
          String kodeBarang = penjualanDetail.getBarang().getKodeBarang();
          KomisiDivisi data = mapBarang.get(kodeBarang);
          if (penjualanDetail.getOprDivisi() == null) {
            penjualanDetail.setOprDivisi(BigDecimal.ZERO);
          }
          if (penjualanDetail.getOrDivisi() == null) {
            penjualanDetail.setOrDivisi(BigDecimal.ZERO);
          }
          if (data == null) {
            data = new KomisiDivisi();
            data.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
            data.setQty(penjualanDetail.getQty());
            data.setOprDivisi(penjualanDetail.getOprDivisi());
            data.setOrDivisi(penjualanDetail.getOrDivisi());
            BigDecimal jumlah =
                (penjualanDetail.getOprDivisi().add(penjualanDetail.getOrDivisi()))
                    .multiply(new BigDecimal(penjualanDetail.getQty()));
            data.setJumlah(jumlah);
            mapBarang.put(kodeBarang, data);
          } else {
            data.setQty(data.getQty() + penjualanDetail.getQty());
            BigDecimal jumlah =
                (penjualanDetail.getOprDivisi().add(penjualanDetail.getOrDivisi()))
                    .multiply(new BigDecimal(penjualanDetail.getQty()));
            data.setJumlah(data.getJumlah().add(jumlah));
            data.setOprDivisi(data.getOprDivisi().add(penjualanDetail.getOprDivisi()));
            data.setOrDivisi(data.getOrDivisi().add(penjualanDetail.getOrDivisi()));
          }
        }
      }
    }

    List<KomisiDivisi> komisiDivisiList = new ArrayList<KomisiDivisi>();
    for (Map.Entry<String, KomisiDivisi> kodeBarangMap : mapBarang.entrySet()) {
      KomisiDivisi data = kodeBarangMap.getValue();
      totalKomisi = totalKomisi.add(data.getJumlah());
      totalUnit = totalUnit + data.getQty();
      komisiDivisiList.add(data);
    }

    return komisiDivisiList;
  }

  private void generateFooterReport(StringBuffer sb) {

    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_A, "Total");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_B, totalUnit + "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalKomisiStr = df.format(totalKomisi);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_E, totalKomisiStr);

    addNewLine(sb, 1);
    addSingleBorder(sb, pageWidth);

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
    String titleReport = "LAPORAN KOMISI DIVISI";
    String startDateStr = formatDate.format(startDate);
    String endDateStr = formatDate.format(endDate);


    int maxLengthTglPrint = 65;
    setAlignLeft(sb, maxLengthTglPrint, companyName);
    sb.append(printDateStr);
    addNewLine(sb, 1);
    // setAlignLeft(sb, maxLengthTglPrint, "");
    addWhiteSpace(sb, 20);
    sb.append(titleReport);
    addWhiteSpace(sb, maxLengthTglPrint - titleReport.length() - 20);
    sb.append(printHourStr);
    addNewLine(sb, 1);
    String divisi = "Divisi : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan();
    sb.append(divisi);
    addWhiteSpace(sb, maxLengthTglPrint - divisi.length());
    sb.append(halStr);
    addNewLine(sb, 1);
    sb.append("Tanggal Penjualan : " + startDateStr + " s/d " + endDateStr);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);


    setAlignLeft(sb, WIDTH_COLUMN_A, "Nama Barang");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_B, "Qty");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_C, "OPR");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_D, "OR");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_COLUMN_E, "Jumlah");

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);
  }

  private void generateLastFooterReport(StringBuffer sb, Karyawan karyawan) {


    BonusTransportService bonusService =
        (BonusTransportService) SpringUtil.getBean("bonusTransportService");
    BigDecimal honorDivisi = bonusService.getHonorDivisi(karyawan, totalUnit);
    BigDecimal total = totalKomisi.add(honorDivisi);
    int column0 = 50;
    int column1 = 15;
    int column2 = 14;
    String separate = ":";

    addNewLine(sb, 2);
    addWhiteSpace(sb, column0);
    setAlignLeft(sb, column1, "Honor");
    sb.append(separate);
    String honorDivisiStr = df.format(honorDivisi);
    setAlignRight(sb, column2, honorDivisiStr);

    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    addSingleBorder(sb, 30);
    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    setAlignLeft(sb, column1, "Total Komisi");
    sb.append(separate);
    String totalStr = df.format(total);
    setAlignRight(sb, column2, totalStr);


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
