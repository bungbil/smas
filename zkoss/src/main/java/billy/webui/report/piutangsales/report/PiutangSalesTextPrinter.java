package billy.webui.report.piutangsales.report;

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
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.backend.service.PiutangService;
import billy.backend.service.StatusService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.report.piutangsales.model.PiutangSales;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PiutangSalesTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PiutangSalesTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }


  private Double totalQty = 0.0;
  private Double totalAkhirQty = 0.0;

  private BigDecimal totalSisaPiutang = BigDecimal.ZERO;
  private BigDecimal totalAkhirSisaPiutang = BigDecimal.ZERO;

  private BigDecimal totalNilaiJual = BigDecimal.ZERO;
  private BigDecimal totalAkhirNilaiJual = BigDecimal.ZERO;

  private BigDecimal totalAngsuran = BigDecimal.ZERO;
  private BigDecimal totalAkhirAngsuran = BigDecimal.ZERO;

  private final int pageWidth = 80;

  private final int WIDTH_COLUMN_SEPERATE = 1;
  private final int WIDTH_COLUMN_A = 11;
  private final int WIDTH_COLUMN_B = 10;
  private final int WIDTH_COLUMN_C = 4;
  private final int WIDTH_COLUMN_D = 2;
  private final int WIDTH_COLUMN_E = 12;
  private final int WIDTH_COLUMN_F = 3;
  private final int WIDTH_COLUMN_G = 10;
  private final int WIDTH_COLUMN_H = 10;
  private final int WIDTH_COLUMN_I = 10;

  private final int WIDTH_FOOTER_COLUMN_A = 13;
  private final int WIDTH_FOOTER_COLUMN_B = 27;
  private final int WIDTH_FOOTER_COLUMN_C = 3;
  private final int WIDTH_FOOTER_COLUMN_D = 11;
  private final int WIDTH_FOOTER_COLUMN_E = 10;
  private final int WIDTH_FOOTER_COLUMN_F = 10;

  DecimalFormat df = new DecimalFormat("#,###");

  public PiutangSalesTextPrinter(Component parent, Karyawan karyawan, Date startDate, Date endDate,
      List<Penjualan> listPenjualan, PrintService selectedPrinter) throws InterruptedException {
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

  private boolean checkAngsuran2Lunas(Status statusLunas, PiutangService piutangService,
      Penjualan penjualan) {
    // TODO Auto-generated method stub
    try {
      if (penjualan.getMetodePembayaran().equals("Cash")) {
        return true;
      } else {
        List<Piutang> piutanglist = piutangService.getPiutangsByPenjualan(penjualan);
        BigDecimal totalAngsuran = BigDecimal.ZERO;
        for (Piutang data : piutanglist) {
          if (data.getPembayaran() != null) {
            totalAngsuran = totalAngsuran.add(data.getPembayaran());
          }
        }
        if (totalAngsuran.compareTo(penjualan.getDownPayment()) == 1) {
          return true;
        } else if (totalAngsuran.compareTo(penjualan.getDownPayment()) == 0) {
          return true;
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.info("catch checkAngsurang2Lunas");
    }
    return false;
  }

  private boolean checkPiutangFinalStatusTarikBarang(Status statusTarikBarang,
      PiutangService piutangService, Penjualan penjualan) {
    // TODO Auto-generated method stub
    try {
      List<Piutang> piutanglist = piutangService.getPiutangsByPenjualan(penjualan);
      for (Piutang data : piutanglist) {
        if (data.getStatusFinal() != null && statusTarikBarang.equals(data.getStatusFinal())) {
          return true;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.info("catch checkPiutangFinalStatusTarikBarang");
    }
    return false;
  }

  public void doPrint(Karyawan karyawan, Date startDate, Date endDate,
      List<Penjualan> listPenjualan, PrintService selectedPrinter) throws PrintException,
      IOException {

    List<PiutangSales> resultList = generatePiutangSales(karyawan, listPenjualan);


    InputStream is =
        new ByteArrayInputStream(generateData(karyawan, startDate, endDate, resultList).getBytes(
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
      List<PiutangSales> listItem) {
    StringBuffer sb = new StringBuffer();

    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    String companyName = companyService.getAllCompanyProfiles().get(0).getCompanyName();
    String companyAddress = companyService.getAllCompanyProfiles().get(0).getAddress();
    int itemPerPage = 54;
    int totalPage = roundUp(listItem.size(), itemPerPage);

    totalSisaPiutang = BigDecimal.ZERO;
    totalQty = 0.0;
    totalNilaiJual = BigDecimal.ZERO;
    totalAngsuran = BigDecimal.ZERO;

    for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
      totalNilaiJual = BigDecimal.ZERO;
      totalSisaPiutang = BigDecimal.ZERO;
      totalAngsuran = BigDecimal.ZERO;
      totalQty = 0.0;
      generateHeaderReport(sb, karyawan, startDate, endDate, pageNo, companyName, companyAddress);
      generateDataReport(sb, listItem, itemPerPage, pageNo);
      generateFooterReport(sb);

    }
    generateLastFooterReport(sb, karyawan);

    return sb.toString();
  }

  private void generateDataReport(StringBuffer sb, List<PiutangSales> listItem, int itemPerPage,
      int pageNo) {

    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      PiutangSales item = listItem.get(i);

      setAlignLeft(sb, WIDTH_COLUMN_A, item.getNomorFaktur());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String namaPelanggan = item.getNamaPelanggan();
      if (namaPelanggan.length() > WIDTH_COLUMN_B) {
        namaPelanggan = namaPelanggan.subSequence(0, WIDTH_COLUMN_B).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_B, namaPelanggan);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_C, item.getKodePartner());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_D, item.getIntervalKredit());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String namaBarang = item.getNamaBarang();
      if (namaBarang.length() > WIDTH_COLUMN_E) {
        namaBarang = namaBarang.subSequence(0, WIDTH_COLUMN_E).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_E, namaBarang);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignRight(sb, WIDTH_COLUMN_F, item.getQtyKirim().toString());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiJualStr = df.format(item.getPenjualanBarang());
      setAlignRight(sb, WIDTH_COLUMN_G, nilaiJualStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiAngsuranStr = df.format(item.getPenerimaanPenjualan());
      setAlignRight(sb, WIDTH_COLUMN_H, nilaiAngsuranStr);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String nilaiSisaPiutangStr = df.format(item.getSisaPiutang());
      setAlignRight(sb, WIDTH_COLUMN_I, nilaiSisaPiutangStr);

      totalQty = totalQty + item.getQtyKirim();

      totalNilaiJual = totalNilaiJual.add(item.getPenjualanBarang());

      totalAngsuran = totalAngsuran.add(item.getPenerimaanPenjualan());

      totalSisaPiutang = totalSisaPiutang.add(item.getSisaPiutang());

      addNewLine(sb, 1);

    }

  }

  private void generateFooterReport(StringBuffer sb) {

    addSingleBorder(sb, pageWidth);
    addNewLine(sb, 1);

    addWhiteSpace(sb, WIDTH_FOOTER_COLUMN_A);
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_B, "Total per halaman");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, totalQty.toString());

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalNilaiJualStr = df.format(totalNilaiJual);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, totalNilaiJualStr);

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalAngsuranStr = df.format(totalAngsuran);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_E, totalAngsuranStr);

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalSisaPiutangStr = df.format(totalSisaPiutang);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_F, totalSisaPiutangStr);

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
    String titleReport = "LAPORAN PIUTANG SALES";
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
    String tglPenjualan = "Tanggal Penjualan : " + startDateStr + " s/d " + endDateStr;
    sb.append(tglPenjualan);
    addWhiteSpace(sb, maxLengthTglPrint - tglPenjualan.length());
    sb.append(halStr);
    addNewLine(sb, 1);
    sb.append("Sales : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan());


    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);


    setAlignLeft(sb, WIDTH_COLUMN_A, "No Faktur");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_B, "Pelanggan");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_C, "Part");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_D, "In");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_E, "Nama Barang");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_F, "Qty");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_G, "Nilai Jual");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_H, "Pembayaran");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_I, "Piutang");


    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);
  }

  private void generateLastFooterReport(StringBuffer sb, Karyawan karyawan) {


    addWhiteSpace(sb, WIDTH_FOOTER_COLUMN_A);
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignLeft(sb, WIDTH_FOOTER_COLUMN_B, "Total Semua");

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_C, totalAkhirQty.toString());

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalAkhirNilaiJualStr = df.format(totalAkhirNilaiJual);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_D, totalAkhirNilaiJualStr);

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalAkhirAngsuranStr = df.format(totalAkhirAngsuran);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_E, totalAkhirAngsuranStr);

    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);
    String totalAkhirSisaPiutangStr = df.format(totalAkhirSisaPiutang);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_F, totalAkhirSisaPiutangStr);

    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);

    addNewLine(sb, 5);

  }

  private List<PiutangSales> generatePiutangSales(Karyawan karyawan, List<Penjualan> listPenjualan) {
    List<PiutangSales> komisiPenjualanList = new ArrayList<PiutangSales>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    StatusService statusService = (StatusService) SpringUtil.getBean("statusService");
    PiutangService piutangService = (PiutangService) SpringUtil.getBean("piutangService");

    Status statusLunas = statusService.getStatusByID(new Long(2)); // LUNAS
    Status statusTarikBarang = statusService.getStatusByID(new Long(6)); // Tarik Barang

    for (Penjualan penjualan : listPenjualan) {
      try {
        if (checkAngsuran2Lunas(statusLunas, piutangService, penjualan)) {
          List<PenjualanDetail> penjualanDetails =
              penjualanService.getPenjualanDetailsByPenjualan(penjualan);
          for (PenjualanDetail penjualanDetail : penjualanDetails) {
            if (!penjualanDetail.getBarang().isBonus()) {
              PiutangSales data = new PiutangSales();
              data.setNomorFaktur(penjualan.getNoFaktur());
              data.setNamaPelanggan(penjualan.getNamaPelanggan());
              data.setIntervalKredit(penjualan.getIntervalKredit() + "");
              String kodePartner = "0000";
              Double qtyKirim = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));

              if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                  && penjualan.getSales2() != null) {
                kodePartner = penjualan.getSales2().getKodeKaryawan();
                qtyKirim = qtyKirim / 2;
              } else if (penjualan.getSales2() != null
                  && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                  && penjualan.getSales1() != null) {
                kodePartner = penjualan.getSales1().getKodeKaryawan();
                qtyKirim = qtyKirim / 2;
              }
              if (checkPiutangFinalStatusTarikBarang(statusTarikBarang, piutangService, penjualan)) {
                qtyKirim = 0.0;
              }
              data.setKodePartner(kodePartner);
              data.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
              data.setQtyKirim(qtyKirim);
              data.setPenjualanBarang(penjualanDetail.getTotal());
              data.setPenerimaanPenjualan(penjualan.getTotal().subtract(penjualan.getPiutang()));
              data.setSisaPiutang(penjualan.getPiutang());
              totalAkhirSisaPiutang = totalAkhirSisaPiutang.add(penjualan.getPiutang());
              totalAkhirNilaiJual = totalAkhirNilaiJual.add(penjualanDetail.getTotal());
              totalAkhirAngsuran =
                  totalAkhirAngsuran.add(penjualan.getTotal().subtract(penjualan.getPiutang()));
              totalAkhirQty = totalAkhirQty + qtyKirim;
              komisiPenjualanList.add(data);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        logger.info("error piutangSales penjualan : " + penjualan.getNoFaktur());
      }
    }

    Collections.sort(komisiPenjualanList, new Comparator<PiutangSales>() {
      @Override
      public int compare(PiutangSales obj1, PiutangSales obj2) {
        return obj1.getNomorFaktur().compareTo(obj2.getNomorFaktur());
      }
    });

    return komisiPenjualanList;
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
