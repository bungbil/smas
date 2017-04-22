package billy.webui.report.summarypenjualan.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.report.summarypenjualan.model.SummaryPenjualan;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class SummaryPenjualanTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(SummaryPenjualanTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private final int pageLength = 50;
  private final int pageWidth = 80;
  private int totalUnit = 0;
  private BigDecimal totalPenjualan = BigDecimal.ZERO;
  private BigDecimal totalPenerimaan = BigDecimal.ZERO;
  private BigDecimal totalSisaPiutang = BigDecimal.ZERO;

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

  public SummaryPenjualanTextPrinter(Component parent, Karyawan karyawan, Date startDate,
      Date endDate, List<Penjualan> listPenjualan, PrintService selectedPrinter)
      throws InterruptedException {
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
    List<SummaryPenjualan> listData = generateDataSummary(karyawan, listPenjualan);

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
      List<SummaryPenjualan> listItem) {
    StringBuffer sb = new StringBuffer();

    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    String companyName = companyService.getAllCompanyProfiles().get(0).getCompanyName();
    String companyAddress = companyService.getAllCompanyProfiles().get(0).getAddress();
    int itemPerPage = 80;
    int totalPage = roundUp(listItem.size(), itemPerPage);
    totalUnit = 0;
    totalPenjualan = BigDecimal.ZERO;
    totalPenerimaan = BigDecimal.ZERO;
    totalSisaPiutang = BigDecimal.ZERO;

    for (int pageNo = 1; pageNo <= totalPage; pageNo++) {

      generateHeaderReport(sb, karyawan, startDate, endDate, pageNo, companyName, companyAddress);
      generateDataReport(sb, listItem, itemPerPage, pageNo);
      generateFooterReport(sb);

    }
    // generateLastFooterReport(sb);

    return sb.toString();
  }

  private void generateDataReport(StringBuffer sb, List<SummaryPenjualan> listItem,
      int itemPerPage, int pageNo) {

    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      SummaryPenjualan item = listItem.get(i);

      String namaBarang = item.getNamaBarang();
      if (namaBarang.length() > WIDTH_COLUMN_A) {
        namaBarang = namaBarang.subSequence(0, WIDTH_COLUMN_A).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_A, namaBarang);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignRight(sb, WIDTH_COLUMN_B, item.getUnitSetTerjual() + "");
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPenjualanStr = df.format(item.getPenjualanBarang());
      setAlignRight(sb, WIDTH_COLUMN_C, nilaiPenjualanStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPenerimaanStr = df.format(item.getPenerimaanPenjualan());
      setAlignRight(sb, WIDTH_COLUMN_D, nilaiPenerimaanStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiSisaPiutangStr = df.format(item.getSisaPiutang());
      setAlignRight(sb, WIDTH_COLUMN_E, nilaiSisaPiutangStr);


      totalUnit += item.getUnitSetTerjual();
      totalPenjualan = totalPenjualan.add(item.getPenjualanBarang());
      totalPenerimaan = totalPenerimaan.add(item.getPenerimaanPenjualan());
      totalSisaPiutang = totalSisaPiutang.add(item.getSisaPiutang());

      addNewLine(sb, 1);

    }

  }

  private List<SummaryPenjualan> generateDataSummary(Karyawan karyawan,
      List<Penjualan> listPenjualan) {
    Map<String, SummaryPenjualan> mapBarang = new HashMap<String, SummaryPenjualan>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    for (Penjualan penjualan : listPenjualan) {
      List<PenjualanDetail> penjualanDetails =
          penjualanService.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail penjualanDetail : penjualanDetails) {
        String kodeBarang = penjualanDetail.getBarang().getKodeBarang();
        SummaryPenjualan sp = mapBarang.get(kodeBarang);
        if (sp == null) {
          sp = new SummaryPenjualan();
          sp.setNamaDivisi(karyawan.getNamaPanggilan());
          sp.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
          sp.setBonus(penjualanDetail.isBonus());
          sp.setUnitSetTerjual(penjualanDetail.getQty());
          sp.setPenjualanBarang(penjualanDetail.getTotal());
          sp.setPenerimaanPenjualan(penjualanDetail.getDownPayment());
          sp.setSisaPiutang(penjualanDetail.getTotal().subtract(penjualanDetail.getDownPayment()));
          mapBarang.put(kodeBarang, sp);
        } else {
          sp.setUnitSetTerjual(sp.getUnitSetTerjual() + penjualanDetail.getQty());
          sp.setPenjualanBarang(sp.getPenjualanBarang().add(penjualanDetail.getTotal()));
          sp.setPenerimaanPenjualan(sp.getPenerimaanPenjualan().add(
              penjualanDetail.getDownPayment()));
          sp.setSisaPiutang(sp.getSisaPiutang().add(
              penjualanDetail.getTotal().subtract(penjualanDetail.getDownPayment())));
        }
      }
    }

    List<SummaryPenjualan> summaryPenjualanList = new ArrayList<SummaryPenjualan>();
    List<SummaryPenjualan> summaryPenjualanBonusList = new ArrayList<SummaryPenjualan>();
    for (Map.Entry<String, SummaryPenjualan> kodeBarangMap : mapBarang.entrySet()) {
      SummaryPenjualan sp = kodeBarangMap.getValue();
      if (sp.isBonus()) {
        summaryPenjualanBonusList.add(sp);
      } else {
        summaryPenjualanList.add(sp);
      }
    }

    Collections.sort(summaryPenjualanList, new Comparator<SummaryPenjualan>() {
      @Override
      public int compare(SummaryPenjualan obj1, SummaryPenjualan obj2) {
        return obj1.getNamaBarang().compareTo(obj2.getNamaBarang());
      }
    });

    Collections.sort(summaryPenjualanBonusList, new Comparator<SummaryPenjualan>() {
      @Override
      public int compare(SummaryPenjualan obj1, SummaryPenjualan obj2) {
        return obj1.getNamaBarang().compareTo(obj2.getNamaBarang());
      }
    });

    summaryPenjualanList.addAll(summaryPenjualanBonusList);

    logger.info("list summaryPenjualanList size : " + summaryPenjualanList.size());
    return summaryPenjualanList;
  }

  private void generateFooterReport(StringBuffer sb) {

    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_A, "Total");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_B, totalUnit + "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalPenjualanStr = df.format(totalPenjualan);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, totalPenjualanStr);
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalPenerimaanStr = df.format(totalPenerimaan);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, totalPenerimaanStr);
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalSisaPiutangStr = df.format(totalSisaPiutang);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_E, totalSisaPiutangStr);

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
    String titleReport = "LAPORAN SUMMARY PENJUALAN";
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

    setAlignRight(sb, WIDTH_COLUMN_B, "Unit");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_C, "Penjualan");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_D, "Penerimaan");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_COLUMN_E, "Sisa Piutang");

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
