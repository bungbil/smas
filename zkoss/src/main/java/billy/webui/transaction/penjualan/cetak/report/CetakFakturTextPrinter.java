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
    Locale id = new Locale("in", "ID");
    formatDate = new SimpleDateFormat("dd MMMM yyyy", id);
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
      while (i + 45 < sb.length() && (i = sb.lastIndexOf(" ", i + 45)) != -1) {
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
      if (penjualan.getTelepon() != null) {
        faktur.setTelepon(penjualan.getTelepon());
      } else {
        faktur.setTelepon("-");
      }
      faktur.setDp(df.format(penjualan.getDownPayment()));
      faktur.setTotal(df.format(penjualan.getTotal()));
      faktur.setTglPenjualan(formatDate.format(penjualan.getTglPenjualan()));
      faktur.setNamaSupervisor(penjualan.getDivisi().getSupervisorDivisi().getNamaPanggilan());
      if (penjualan.getPengirim() != null) {
        faktur.setNamaPengirim(penjualan.getPengirim().getNamaPanggilan());
      } else {
        faktur.setNamaPengirim("-");
      }

      List<PenjualanDetail> listPenjualanDetail = as.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail detail : listPenjualanDetail) {
        faktur.tambahItemFaktur(new ItemFaktur(detail.getBarang().getKodeBarang(), detail
            .getBarang().getNamaBarang(), String.valueOf(detail.getQty()), df.format(detail
            .getHarga()), df.format(detail.getTotal())));
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
    // InputStream ff = new ByteArrayInputStream("\f".getBytes());
    // Doc docff = new SimpleDoc(ff, flavor, null);
    // DocPrintJob jobff = selectedPrinter.createPrintJob();
    // pjw = new PrintJobWatcher(jobff);
    // jobff.print(docff, null);
    // pjw.waitForDone();

  }

  private String generateData(List<Faktur> listFaktur) {
    StringBuffer sb = new StringBuffer();

    for (Faktur faktur : listFaktur) {

      addWhiteSpace(sb, 66);
      sb.append(faktur.getNomorFaktur());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 13);
      int maxLengthNama = 37;
      setAlignLeft(sb, maxLengthNama, faktur.getNamaPelanggan());
      int maxLengthKodeSales = 8;
      String kodeSales = faktur.getKodeSales1();
      if (kodeSales.length() > maxLengthKodeSales) {
        kodeSales.subSequence(0, maxLengthKodeSales).toString();
      }
      setAlignRight(sb, maxLengthKodeSales, kodeSales);
      addWhiteSpace(sb, 10);
      String kodeSales2 = faktur.getKodeSales2();
      if (kodeSales2.length() > maxLengthKodeSales) {
        kodeSales2.subSequence(0, maxLengthKodeSales).toString();
      }
      setAlignLeft(sb, maxLengthKodeSales, kodeSales2);

      addNewLine(sb, 1);
      addWhiteSpace(sb, 22);
      int maxLengthTelepon = 27;
      setAlignLeft(sb, maxLengthTelepon, faktur.getTelepon());

      int maxLengthNamaSales = 14;
      String namaSales = faktur.getNamaSales1();
      if (namaSales.length() > maxLengthNamaSales) {
        namaSales.subSequence(0, maxLengthNamaSales).toString();
      }
      setAlignRight(sb, maxLengthNamaSales, namaSales);
      addWhiteSpace(sb, 2);
      String namaSales2 = faktur.getNamaSales2();
      if (namaSales2.length() > maxLengthNamaSales) {
        namaSales2.subSequence(0, maxLengthNamaSales).toString();
      }
      setAlignLeft(sb, maxLengthNamaSales, namaSales2);


      addNewLine(sb, 1);
      addWhiteSpace(sb, 1);
      sb.append(faktur.getAlamat());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 1);
      sb.append(faktur.getAlamat2());
      addNewLine(sb, 1);
      addWhiteSpace(sb, 1);
      int maxLengthAlamat3 = 57;
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
        int maxLengthNamaBarang = 20;
        String namaBarang = item.getNamaBarang();
        if (namaBarang.length() > maxLengthNamaBarang) {
          namaBarang = namaBarang.subSequence(0, maxLengthNamaBarang).toString();
        }
        sb.append(namaBarang);
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
      int maxLengthTotal = 45;
      addWhiteSpace(sb, maxLengthTotal - faktur.getTotal().length());
      sb.append(faktur.getTotal());
      addNewLine(sb, 2);
      sb.append(" DPAY  Rp. " + faktur.getDp());
      addNewLine(sb, 3);
      addWhiteSpace(sb, 4);
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

  private void setAlignLeft(StringBuffer sb, int width, String value) {
    sb.append(value);
    addWhiteSpace(sb, width - value.length());
  }

  private void setAlignRight(StringBuffer sb, int width, String value) {
    addWhiteSpace(sb, width - value.length());
    sb.append(value);
  }
}
