package billy.webui.transaction.penjualan.cetak.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import billy.backend.service.PenjualanService;
import billy.webui.printer.PrintJobWatcher;
import billy.webui.printer.model.Faktur;
import billy.webui.printer.model.ItemFaktur;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CetakFakturTextPrinter extends Window implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(CetakFakturTextPrinter.class);

  public CetakFakturTextPrinter(Component parent, List<Penjualan> listPenjualan,
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
    List<Faktur> listFaktur = new ArrayList<Faktur>();
    for (Penjualan penjualan : listPenjualan) {
      Faktur faktur = new Faktur();
      faktur.setNomorFaktur(penjualan.getNoFaktur());
      faktur.setKodeSales1(penjualan.getSales1().getKodeKaryawan());
      faktur.setNamaSales1(penjualan.getSales1().getNamaPanggilan() + "("
          + penjualan.getSales1().getSupervisorDivisi().getInisialDivisi() + ")");
      if (penjualan.getSales2() != null) {
        faktur.setKodeSales2(penjualan.getSales2().getKodeKaryawan());
        faktur.setNamaSales2(penjualan.getSales2().getNamaPanggilan() + "("
            + penjualan.getSales2().getSupervisorDivisi().getInisialDivisi() + ")");
      } else {
        faktur.setKodeSales2("");
        faktur.setNamaSales2("");
      }
      faktur.setIntervalKredit(penjualan.getIntervalKredit() + " Bulan");
      faktur.setNamaPelanggan(penjualan.getNamaPelanggan());
      faktur.setAlamat("");
      faktur.setAlamat2("");
      faktur.setAlamat3("");

      StringBuilder sb = new StringBuilder(penjualan.getAlamat());
      int i = 0;
      while (i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 35)) != -1) {
        sb.replace(i, i + 1, "\n");
      }
      String[] alamat = sb.toString().split("\n");
      int length = alamat.length;
      for (int k = 0; k < length; k++) {
        if (k == 0) {
          faktur.setAlamat(alamat[0]);
        } else if (k == 1) {
          faktur.setAlamat2(alamat[1]);
        } else if (k == 2) {
          faktur.setAlamat3(alamat[2]);
        }
      }
      faktur.setTelepon(penjualan.getTelepon());
      faktur.setDp(df.format(penjualan.getDownPayment()));
      faktur.setTotal(df.format(penjualan.getTotal()));
      faktur.setTglPenjualan(formatDate.format(penjualan.getTglPenjualan()));
      faktur.setNamaSupervisor(penjualan.getDivisi().getSupervisorDivisi().getNamaPanggilan());
      faktur.setNamaPengirim(penjualan.getPengirim().getNamaPanggilan());

      List<PenjualanDetail> listPenjualanDetail = as.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail detail : listPenjualanDetail) {
        faktur.tambahItemFaktur(new ItemFaktur(detail.getBarang().getNamaBarang(), String
            .valueOf(detail.getQty()), df.format(detail.getHarga()), df.format(detail.getTotal())));
      }

      listFaktur.add(faktur);
    }

    // prints the famous hello world! plus a form feed
    InputStream is = new ByteArrayInputStream(generateData(listFaktur).getBytes("UTF8"));
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

  private String generateData(List<Faktur> listFaktur) {
    StringBuffer sb = new StringBuffer();

    for (Faktur faktur : listFaktur) {

      addWhiteSpace(sb, 67);
      sb.append(faktur.getNomorFaktur());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 13);
      int maxLengthNama = 43;
      sb.append(faktur.getNamaPelanggan());
      addWhiteSpace(sb, maxLengthNama - faktur.getNamaPelanggan().length());
      sb.append(faktur.getKodeSales1());
      addWhiteSpace(sb, 8);
      sb.append(faktur.getKodeSales2());
      addNewLine(sb, 1);
      int maxLengthTelepon = 36;
      addWhiteSpace(sb, maxLengthTelepon - faktur.getTelepon().length());
      sb.append(faktur.getTelepon());
      addWhiteSpace(sb, 16);
      sb.append(faktur.getNamaSales1());
      addWhiteSpace(sb, 3);
      sb.append(faktur.getNamaSales2());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append(faktur.getAlamat());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      sb.append(faktur.getAlamat2());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 5);
      int maxLengthAlamat3 = 54;
      sb.append(faktur.getAlamat3());
      addWhiteSpace(sb, maxLengthAlamat3 - faktur.getAlamat3().length());
      sb.append(faktur.getIntervalKredit());
      addNewLine(sb, 3);
      int totalRowItem = 8;
      for (ItemFaktur item : faktur.getListItemFaktur()) {
        int maxLengthQty = 3;
        addWhiteSpace(sb, maxLengthQty - item.getQty().length());
        sb.append(item.getQty());
        addWhiteSpace(sb, 2);
        sb.append(item.getNamaBarang());
        int maxLengthNamaBarang = 20;
        addWhiteSpace(sb, maxLengthNamaBarang - item.getNamaBarang().length());
        int maxLengthHarga = 10;
        addWhiteSpace(sb, maxLengthHarga - item.getHarga().length());
        sb.append(item.getHarga());
        int maxLengthJumlah = 10;
        addWhiteSpace(sb, maxLengthJumlah - item.getJumlah().length());
        sb.append(item.getJumlah());

        addNewLine(sb, 1);
        totalRowItem--;
      }
      addNewLine(sb, totalRowItem);
      int maxLengthTotal = 43;
      addWhiteSpace(sb, maxLengthTotal - faktur.getTotal().length());
      sb.append(faktur.getTotal());
      addNewLine(sb, 2);
      sb.append(" DPAY  Rp. " + faktur.getDp());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 6);
      sb.append(faktur.getTglPenjualan());
      addNewLine(sb, 5);
      addWhiteSpace(sb, 6);
      sb.append(faktur.getNamaSupervisor());
      int maxLengthSupervisor = 17;
      addWhiteSpace(sb, maxLengthSupervisor - faktur.getNamaSupervisor().length());
      sb.append(faktur.getNamaPengirim());
      addNewLine(sb, 5);
    }

    return sb.toString();
  }
}