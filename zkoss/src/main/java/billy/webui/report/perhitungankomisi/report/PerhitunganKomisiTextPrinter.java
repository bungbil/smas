package billy.webui.report.perhitungankomisi.report;

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
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.BonusTransportService;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.report.perhitungankomisi.model.PerhitunganKomisi;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PerhitunganKomisiTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PerhitunganKomisiTextPrinter.class);

  public static int roundUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private BigDecimal totalAkhirTabungan = BigDecimal.ZERO;

  private Double totalQty = 0.0;
  private Double totalAkhirQty = 0.0;

  private BigDecimal totalKomisi = BigDecimal.ZERO;
  private BigDecimal totalAkhirKomisi = BigDecimal.ZERO;

  private BigDecimal totalNilaiJual = BigDecimal.ZERO;
  private BigDecimal totalAkhirNilaiJual = BigDecimal.ZERO;

  private BigDecimal totalAngsuran = BigDecimal.ZERO;
  private BigDecimal totalAkhirAngsuran = BigDecimal.ZERO;

  private final int pageWidth = 80;

  private final int WIDTH_COLUMN_SEPERATE = 1;
  private final int WIDTH_COLUMN_A = 10;
  private final int WIDTH_COLUMN_B = 10;
  private final int WIDTH_COLUMN_C = 5;
  private final int WIDTH_COLUMN_D = 3;
  private final int WIDTH_COLUMN_E = 12;
  private final int WIDTH_COLUMN_F = 3;
  private final int WIDTH_COLUMN_G = 10;
  private final int WIDTH_COLUMN_H = 10;
  private final int WIDTH_COLUMN_I = 9;

  private final int WIDTH_FOOTER_COLUMN_A = 14;
  private final int WIDTH_FOOTER_COLUMN_B = 21;
  private final int WIDTH_FOOTER_COLUMN_C = 5;
  private final int WIDTH_FOOTER_COLUMN_D = 13;
  private final int WIDTH_FOOTER_COLUMN_E = 11;
  private final int WIDTH_FOOTER_COLUMN_F = 11;

  DecimalFormat df = new DecimalFormat("#,###");

  public PerhitunganKomisiTextPrinter(Component parent, Karyawan karyawan, Date startDate,
      Date endDate, List<Penjualan> listPenjualan, PrintService selectedPrinter)
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
      List<Penjualan> listPenjualan, PrintService selectedPrinter) throws PrintException,
      IOException {

    List<PerhitunganKomisi> resultList = generatePerhitunganKomisi(karyawan, listPenjualan);


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
      List<PerhitunganKomisi> listItem) {
    StringBuffer sb = new StringBuffer();

    CompanyProfileService companyService =
        (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    String companyName = companyService.getAllCompanyProfiles().get(0).getCompanyName();
    String companyAddress = companyService.getAllCompanyProfiles().get(0).getAddress();
    int itemPerPage = 80;
    int totalPage = roundUp(listItem.size(), itemPerPage);
    totalAkhirTabungan = BigDecimal.ZERO;
    totalKomisi = BigDecimal.ZERO;
    totalAkhirKomisi = BigDecimal.ZERO;
    totalQty = 0.0;
    totalAkhirQty = 0.0;
    totalNilaiJual = BigDecimal.ZERO;
    totalAkhirNilaiJual = BigDecimal.ZERO;
    totalAngsuran = BigDecimal.ZERO;
    totalAkhirAngsuran = BigDecimal.ZERO;

    for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
      totalNilaiJual = BigDecimal.ZERO;
      totalKomisi = BigDecimal.ZERO;
      totalNilaiJual = BigDecimal.ZERO;
      totalQty = 0.0;
      generateHeaderReport(sb, karyawan, startDate, endDate, pageNo, companyName, companyAddress);
      generateDataReport(sb, listItem, itemPerPage, pageNo);
      generateFooterReport(sb);

    }
    generateLastFooterReport(sb, karyawan);

    return sb.toString();
  }

  private void generateDataReport(StringBuffer sb, List<PerhitunganKomisi> listItem,
      int itemPerPage, int pageNo) {

    int startIndex = itemPerPage * (pageNo - 1);
    int maxIndex = itemPerPage * pageNo;

    if (maxIndex >= listItem.size()) {
      maxIndex = listItem.size();
    }
    for (int i = startIndex; i < maxIndex; i++) {
      PerhitunganKomisi item = listItem.get(i);

      setAlignLeft(sb, WIDTH_COLUMN_A, item.getNomorFaktur());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String namaPelanggan = item.getNamaPelanggan();
      if (namaPelanggan.length() > 10) {
        namaPelanggan = namaPelanggan.subSequence(0, 10).toString();
      }
      setAlignLeft(sb, WIDTH_COLUMN_B, namaPelanggan);
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_C, item.getKodePartner());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      setAlignLeft(sb, WIDTH_COLUMN_D, item.getIntervalKredit());
      addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

      String namaBarang = item.getNamaBarang();
      if (namaBarang.length() > 12) {
        namaBarang = namaBarang.subSequence(0, 12).toString();
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

      String nilaiKomisiStr = df.format(item.getKomisiPenjualan());
      setAlignRight(sb, WIDTH_COLUMN_I, nilaiKomisiStr);

      totalQty = totalQty + item.getQtyKirim();
      totalAkhirQty = totalAkhirQty + item.getQtyKirim();

      totalNilaiJual = totalNilaiJual.add(item.getPenjualanBarang());
      totalAkhirNilaiJual = totalAkhirNilaiJual.add(item.getPenjualanBarang());

      totalAngsuran = totalAngsuran.add(item.getPenerimaanPenjualan());
      totalAkhirAngsuran = totalAkhirAngsuran.add(item.getPenerimaanPenjualan());

      totalKomisi = totalKomisi.add(item.getKomisiPenjualan());
      totalAkhirKomisi = totalAkhirKomisi.add(item.getKomisiPenjualan());

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
    String totalKomisiStr = df.format(totalKomisi);
    setAlignRight(sb, WIDTH_FOOTER_COLUMN_F, totalKomisiStr);

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
    String titleReport = "SLIP PENDAPATAN MARKETING";
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

    setAlignLeft(sb, WIDTH_COLUMN_D, "Int");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignLeft(sb, WIDTH_COLUMN_E, "Nama Barang");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_F, "Qty");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_G, "Nilai Jual");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_H, "Angsuran 1");
    addWhiteSpace(sb, WIDTH_COLUMN_SEPERATE);

    setAlignRight(sb, WIDTH_COLUMN_I, "Komisi");


    addNewLine(sb, 1);
    addDoubleBorder(sb, pageWidth);
    addNewLine(sb, 1);
  }

  private void generateLastFooterReport(StringBuffer sb, Karyawan karyawan) {
    BonusTransportService bonusService =
        (BonusTransportService) SpringUtil.getBean("bonusTransportService");
    BigDecimal bonusSales = bonusService.getBonusSales(karyawan, totalQty);
    BigDecimal transportSales = bonusService.getTransportSales(karyawan, totalQty);
    BigDecimal total = totalAkhirKomisi.add(bonusSales).add(transportSales);

    addNewLine(sb, 1);
    sb.append("Bonus     : " + df.format(bonusSales));
    addNewLine(sb, 1);
    sb.append("Transport : " + df.format(transportSales));
    addNewLine(sb, 1);
    addSingleBorder(sb, 20);
    addNewLine(sb, 1);
    sb.append("Total     : " + df.format(total));
    addNewLine(sb, 1);
    sb.append("Tabungan  : " + df.format(totalAkhirTabungan));


  }

  private List<PerhitunganKomisi> generatePerhitunganKomisi(Karyawan karyawan,
      List<Penjualan> listPenjualan) {
    List<PerhitunganKomisi> komisiPenjualanList = new ArrayList<PerhitunganKomisi>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");

    for (Penjualan penjualan : listPenjualan) {
      List<PenjualanDetail> penjualanDetails =
          penjualanService.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail penjualanDetail : penjualanDetails) {

        PerhitunganKomisi data = new PerhitunganKomisi();
        data.setNomorFaktur(penjualan.getNoFaktur());
        data.setNamaPelanggan(penjualan.getNamaPelanggan());
        data.setIntervalKredit(penjualan.getIntervalKredit() + "");
        String kodePartner = "0000";
        Double qtyKirim = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));
        BigDecimal komisi = BigDecimal.ZERO;
        BigDecimal tabungan = BigDecimal.ZERO;
        if (penjualanDetail.getTabunganSales() == null) {
          penjualanDetail.setTabunganSales(BigDecimal.ZERO);
        }
        if (penjualanDetail.getKomisiSales() == null) {
          penjualanDetail.setKomisiSales(BigDecimal.ZERO);
        }

        tabungan =
            penjualanDetail.getTabunganSales().multiply(new BigDecimal(penjualanDetail.getQty()));
        komisi =
            penjualanDetail.getKomisiSales().multiply(new BigDecimal(penjualanDetail.getQty()));
        if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
            && penjualan.getSales2() != null) {
          kodePartner = penjualan.getSales2().getKodeKaryawan();
          qtyKirim = qtyKirim / 2;
          komisi = komisi.divide(new BigDecimal(2));
          tabungan = tabungan.divide(new BigDecimal(2));
        } else if (penjualan.getSales2() != null
            && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
            && penjualan.getSales1() != null) {
          kodePartner = penjualan.getSales1().getKodeKaryawan();
          qtyKirim = qtyKirim / 2;
          komisi = komisi.divide(new BigDecimal(2));
          tabungan = tabungan.divide(new BigDecimal(2));
        }
        data.setKodePartner(kodePartner);
        data.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
        data.setQtyKirim(qtyKirim);
        data.setPenjualanBarang(penjualanDetail.getTotal());
        data.setPenerimaanPenjualan(penjualanDetail.getDownPayment());
        data.setKomisiPenjualan(komisi);
        totalAkhirKomisi = totalAkhirKomisi.add(komisi);
        totalAkhirTabungan = totalAkhirTabungan.add(tabungan);
        totalAkhirNilaiJual = totalAkhirNilaiJual.add(penjualanDetail.getTotal());
        totalAkhirAngsuran = totalAkhirAngsuran.add(penjualanDetail.getDownPayment());
        totalAkhirQty = totalAkhirQty + qtyKirim;
        komisiPenjualanList.add(data);
      }
    }
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
