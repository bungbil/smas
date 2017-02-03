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
import billy.webui.report.komisi.model.KomisiSales;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class KomisiSalesTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(KomisiSalesTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private final int pageLength = 50;
  private final int pageWidth = 80;
  private double totalUnit = 0;
  private BigDecimal totalTabungan = BigDecimal.ZERO;
  private BigDecimal totalKomisi = BigDecimal.ZERO;
  private BigDecimal totalJumlah = BigDecimal.ZERO;

  private final int WIDTH_COLUMN_SEPERATE = 1;
  private final int WIDTH_COLUMN_A = 27;
  private final int WIDTH_COLUMN_B = 6;
  private final int WIDTH_COLUMN_C = 6;
  private final int WIDTH_COLUMN_D = 12;
  private final int WIDTH_COLUMN_E = 12;
  private final int WIDTH_COLUMN_F = 12;

  private final int WIDTH_FOOTER_COLUMN_A = 20;
  private final int WIDTH_FOOTER_COLUMN_B = 6;
  private final int WIDTH_FOOTER_COLUMN_C = 6;
  private final int WIDTH_FOOTER_COLUMN_D = 14;
  private final int WIDTH_FOOTER_COLUMN_E = 14;
  private final int WIDTH_FOOTER_COLUMN_F = 15;

  DecimalFormat df = new DecimalFormat("#,###");

  public KomisiSalesTextPrinter(Component parent, Karyawan karyawan, Date startDate, Date endDate,
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

    List<KomisiSales> listData = generateDataSummary(karyawan, listPenjualan);
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
      List<KomisiSales> listItem) {
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

  private void generateDataReport(StringBuffer sb, List<KomisiSales> listItem, int itemPerPage,
      int pageNo) {

    SimpleDateFormat formatDate = new SimpleDateFormat();
    formatDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      KomisiSales item = listItem.get(i);

      String namaBarang = item.getNamaBarang();
      if (namaBarang.length() > WIDTH_COLUMN_A) {
        namaBarang = namaBarang.subSequence(0, WIDTH_COLUMN_A).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_A, namaBarang);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignRight(sb, WIDTH_COLUMN_B, item.getIntervalKredit() + "");
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignRight(sb, WIDTH_COLUMN_C, item.getQty() + "");
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPenjualanStr = df.format(item.getKomisiSales());
      setAlignRight(sb, WIDTH_COLUMN_D, nilaiPenjualanStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiPenerimaanStr = df.format(item.getTabunganSales());
      setAlignRight(sb, WIDTH_COLUMN_E, nilaiPenerimaanStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiSisaPiutangStr = df.format(item.getJumlah());
      setAlignRight(sb, WIDTH_COLUMN_F, nilaiSisaPiutangStr);

      totalUnit += item.getQty();

      addNewLine(sb, 1);

    }

  }

  private List<KomisiSales> generateDataSummary(Karyawan karyawan, List<Penjualan> listPenjualan) {
    Map<String, KomisiSales> mapBarang = new HashMap<String, KomisiSales>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    for (Penjualan penjualan : listPenjualan) {
      List<PenjualanDetail> penjualanDetails =
          penjualanService.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail penjualanDetail : penjualanDetails) {
        if (!penjualanDetail.getBarang().isBonus()) {
          String kodeBarang =
              penjualanDetail.getBarang().getKodeBarang() + "-" + penjualan.getIntervalKredit();
          KomisiSales data = mapBarang.get(kodeBarang);
          if (data == null) {
            data = new KomisiSales();
            data.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
            data.setIntervalKredit(penjualan.getIntervalKredit());
            data.setKomisiSales(penjualanDetail.getKomisiSales());
            data.setTabunganSales(penjualanDetail.getTabunganSales());

            Double qty = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));
            BigDecimal komisi = BigDecimal.ZERO;
            BigDecimal tabungan = BigDecimal.ZERO;
            tabungan =
                penjualanDetail.getTabunganSales().multiply(
                    new BigDecimal(penjualanDetail.getQty()));
            komisi =
                penjualanDetail.getKomisiSales().multiply(new BigDecimal(penjualanDetail.getQty()));

            if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales2() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            } else if (penjualan.getSales2() != null
                && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales1() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            }
            totalKomisi = totalKomisi.add(komisi);
            totalTabungan = totalTabungan.add(tabungan);
            totalUnit = totalUnit + qty;

            data.setQty(qty);
            data.setJumlah(komisi.add(tabungan));
            mapBarang.put(kodeBarang, data);
          } else {

            Double qty = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));
            BigDecimal komisi = BigDecimal.ZERO;
            BigDecimal tabungan = BigDecimal.ZERO;
            tabungan =
                penjualanDetail.getTabunganSales().multiply(
                    new BigDecimal(penjualanDetail.getQty()));
            komisi =
                penjualanDetail.getKomisiSales().multiply(new BigDecimal(penjualanDetail.getQty()));

            if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales2() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            } else if (penjualan.getSales2() != null
                && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales1() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            }
            totalKomisi = totalKomisi.add(komisi);
            totalTabungan = totalTabungan.add(tabungan);
            totalUnit = totalUnit + qty;

            data.setQty(data.getQty() + qty);
            data.setJumlah(data.getJumlah().add(komisi).add(tabungan));
          }
        }
      }
    }

    List<KomisiSales> komisiSalesList = new ArrayList<KomisiSales>();
    for (Map.Entry<String, KomisiSales> kodeBarangMap : mapBarang.entrySet()) {
      KomisiSales data = kodeBarangMap.getValue();
      komisiSalesList.add(data);
      totalJumlah = totalJumlah.add(data.getJumlah());
    }

    return komisiSalesList;
  }

  private void generateFooterReport(StringBuffer sb) {

    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_A, "Total");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_FOOTER_COLUMN_B, "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, totalUnit + "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_FOOTER_COLUMN_E, "");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    String totalKomisiStr = df.format(totalJumlah);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_F, totalKomisiStr);

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
    String titleReport = "LAPORAN KOMISI SALES";
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
    String divisi = "Sales : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan();
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

    setAlignRight(sb, WIDTH_COLUMN_B, "Kredit");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_C, "Qty");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_D, "Komisi");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_E, "Tabungan");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_COLUMN_F, "Jumlah");

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);
  }

  private void generateLastFooterReport(StringBuffer sb, Karyawan karyawan) {
    BonusTransportService bonusService =
        (BonusTransportService) SpringUtil.getBean("bonusTransportService");
    BigDecimal bonusSales = bonusService.getBonusSales(karyawan, totalUnit);
    BigDecimal transportSales = bonusService.getTransportSales(karyawan, totalUnit);
    BigDecimal totalPendapatan = totalJumlah.add(bonusSales).add(transportSales);
    BigDecimal total = totalPendapatan.subtract(totalTabungan);

    int column0 = 45;
    int column1 = 20;
    int column2 = 14;
    String separate = ":";

    addNewLine(sb, 2);
    addWhiteSpace(sb, column0);
    setAlignLeft(sb, column1, "Bonus");
    sb.append(separate);
    String bonusSalesStr = df.format(bonusSales);
    setAlignRight(sb, column2, bonusSalesStr);

    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    setAlignLeft(sb, column1, "Transport");
    sb.append(separate);
    String transportSalesStr = df.format(transportSales);
    setAlignRight(sb, column2, transportSalesStr);

    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    addSingleBorder(sb, 35);

    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    setAlignLeft(sb, column1, "Total Pendapatan");
    sb.append(separate);
    String totalPendapatanStr = df.format(totalPendapatan);
    setAlignRight(sb, column2, totalPendapatanStr);

    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    setAlignLeft(sb, column1, "Potongan Tabungan");
    sb.append(separate);
    String totalTabunganStr = df.format(totalTabungan);
    setAlignRight(sb, column2, totalTabunganStr);

    addNewLine(sb, 1);
    addWhiteSpace(sb, column0);
    addSingleBorder(sb, 35);

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
