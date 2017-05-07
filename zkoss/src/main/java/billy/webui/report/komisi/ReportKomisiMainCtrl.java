package billy.webui.report.komisi;

import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.report.komisi.report.KomisiDivisiDJReport;
import billy.webui.report.komisi.report.KomisiDivisiTextPrinter;
import billy.webui.report.komisi.report.KomisiSalesDJReport;
import billy.webui.report.komisi.report.KomisiSalesTextPrinter;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportKomisiMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportKomisiMainCtrl.class);


  protected Window windowReportKomisiMain; // autowired
  protected Radiogroup radiogroup_laporan; // autowired
  protected Radio radioLaporanDivisi;
  protected Radio radioLaporanSales;
  protected Textbox txtb_KodeDivisi;
  protected Listbox lbox_Divisi;
  protected Datebox txtb_tanggalAwalPenjualan;
  protected Datebox txtb_tanggalAkhirPenjualan;
  protected Button btnView;
  protected Listbox lbox_Printer;
  protected Button btnCetak;

  protected Textbox txtb_KodeDivisi_per_divisi;
  protected Listbox lbox_Divisi_per_divisi;
  protected Datebox txtb_tanggalAwalPenjualan_per_divisi;
  protected Datebox txtb_tanggalAkhirPenjualan_per_divisi;
  protected Listbox lbox_Printer_per_divisi;
  protected Button btnCetak_per_divisi;

  private PrintService selectedPrinter;
  private PrintService selectedPrinter_per_divisi;
  // ServiceDAOs / Domain Classes
  private PenjualanService penjualanService;
  private KaryawanService karyawanService;

  List<Penjualan> listPenjualan = new ArrayList<Penjualan>();
  Karyawan karyawan = null;
  List<Penjualan> listPenjualan_per_divisi = new ArrayList<Penjualan>();
  Karyawan karyawan_per_divisi = null;


  /**
   * default constructor.<br>
   */
  public ReportKomisiMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    // PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    PrintService[] printServices = PrinterJob.lookupPrintServices();
    lbox_Printer.setModel(new ListModelList(printServices));
    lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    ListModelList lml = (ListModelList) lbox_Printer.getModel();
    lbox_Printer.setSelectedIndex(lml.indexOf(service));

    lbox_Printer_per_divisi.setModel(new ListModelList(printServices));
    lbox_Printer_per_divisi.setItemRenderer(new PrinterListModelItemRenderer());
    ListModelList lml2 = (ListModelList) lbox_Printer_per_divisi.getModel();
    lbox_Printer_per_divisi.setSelectedIndex(lml2.indexOf(service));

    this.self.setAttribute("controller", this, false);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * User rights check. <br>
   * Only components are set visible=true if the logged-in <br>
   * user have the right for it. <br>
   * The rights are getting from the spring framework users grantedAuthority(). Remember! A right is
   * only a string. <br>
   */
  // TODO move it to zul
  private void doCheckRights() {
    final UserWorkspace workspace = getUserWorkspace();
    btnView.setDisabled(!workspace.isAllowed("button_ReportKomisiMain_btnView"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();

    List<Karyawan> listSales = getKaryawanService().getAllSalesKaryawansByUserLogin(userLogin);

    lbox_Divisi.setModel(new ListModelList(listSales));
    lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());

    List<Karyawan> listDivisi = getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);
    lbox_Divisi_per_divisi.setModel(new ListModelList(listDivisi));
    lbox_Divisi_per_divisi.setItemRenderer(new KaryawanListModelItemRenderer());


    Date date = new Date(); // your date
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    cal.set(year, month, 1, 0, 0, 0);
    Date startDate = cal.getTime();
    txtb_tanggalAwalPenjualan.setValue(startDate);
    txtb_tanggalAkhirPenjualan.setValue(date);
    txtb_tanggalAwalPenjualan_per_divisi.setValue(startDate);
    txtb_tanggalAkhirPenjualan_per_divisi.setValue(date);

    listPenjualan = new ArrayList<Penjualan>();
    listPenjualan_per_divisi = new ArrayList<Penjualan>();


  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  /* SERVICES */
  public PenjualanService getPenjualanService() {
    return this.penjualanService;
  }

  public void onChange$txtb_KodeDivisi(Event event) throws InterruptedException {
    if (txtb_KodeDivisi.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Divisi.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(
              txtb_KodeDivisi.getValue().trim().toUpperCase());
      if (karyawan != null) {
        lbox_Divisi.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Divisi.setSelectedIndex(-1);
      }
    }
  }

  public void onChange$txtb_KodeDivisi_per_divisi(Event event) throws InterruptedException {
    if (txtb_KodeDivisi_per_divisi.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Divisi_per_divisi.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(
              txtb_KodeDivisi_per_divisi.getValue().trim().toUpperCase());
      if (karyawan != null) {
        lbox_Divisi_per_divisi.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Divisi_per_divisi.setSelectedIndex(-1);
      }
    }
  }

  public void onClick$btnCetak(Event event) throws Exception {
    if (validToPrint() && selectedPrinter != null) {

      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      if (radioLaporanSales.isSelected()) {
        new KomisiSalesTextPrinter(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
            txtb_tanggalAkhirPenjualan.getValue(), listPenjualan, selectedPrinter);
      } else if (radioLaporanDivisi.isSelected()) {
        new KomisiDivisiTextPrinter(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
            txtb_tanggalAkhirPenjualan.getValue(), listPenjualan, selectedPrinter);
      }

    } else {
      showErrorCetak();
    }
  }

  public void onClick$btnCetak_per_divisi(Event event) throws Exception {
    if (validToPrintPerDivisi() && selectedPrinter_per_divisi != null) {

      final Window win = (Window) Path.getComponent("/outerIndexWindow");

      new KomisiDivisiTextPrinter(win, karyawan_per_divisi,
          txtb_tanggalAwalPenjualan_per_divisi.getValue(),
          txtb_tanggalAkhirPenjualan_per_divisi.getValue(), listPenjualan_per_divisi,
          selectedPrinter_per_divisi);

      Set<Karyawan> listSales = new HashSet<Karyawan>();

      for (Penjualan penjualan : listPenjualan_per_divisi) {
        listSales.add(penjualan.getSales1());
        if (penjualan.getSales2() != null) {
          listSales.add(penjualan.getSales2());
        }
      }
      for (Karyawan sales : listSales) {
        List<Penjualan> listPenjualanPerSales = new ArrayList<Penjualan>();
        listPenjualanPerSales =
            getPenjualanService().getAllPenjualansBySalesAndRangeDateUnderDivisi(sales,
                txtb_tanggalAwalPenjualan_per_divisi.getValue(),
                txtb_tanggalAkhirPenjualan_per_divisi.getValue(), karyawan_per_divisi);

        new KomisiSalesTextPrinter(win, sales, txtb_tanggalAwalPenjualan_per_divisi.getValue(),
            txtb_tanggalAkhirPenjualan_per_divisi.getValue(), listPenjualanPerSales,
            selectedPrinter_per_divisi);
      }


    } else {
      showErrorCetak();
    }
  }

  public void onClick$btnRefeshPrinter(Event event) throws Exception {
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    lbox_Printer.setModel(new ListModelList(printServices));
    lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
  }

  public void onClick$btnRefeshPrinter_per_divisi(Event event) throws Exception {
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    lbox_Printer_per_divisi.setModel(new ListModelList(printServices));
    lbox_Printer_per_divisi.setItemRenderer(new PrinterListModelItemRenderer());
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */


  public void onClick$btnView(Event event) throws Exception {
    if (validToPrint()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");

      if (radioLaporanSales.isSelected()) {
        new KomisiSalesDJReport(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
            txtb_tanggalAkhirPenjualan.getValue(), listPenjualan);
      } else if (radioLaporanDivisi.isSelected()) {
        new KomisiDivisiDJReport(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
            txtb_tanggalAkhirPenjualan.getValue(), listPenjualan);
      }
    } else {
      showErrorCetak();
    }
  }

  /* COMPONENTS and OTHERS */

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowReportKomisiMain(Event event) throws Exception {
    windowReportKomisiMain.setContentStyle("padding:0px;");
    doCheckRights();
    doReset();
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }

  public void showErrorCetak() throws Exception {
    if (listPenjualan.size() == 0) {
      ZksampleMessageUtils.showErrorMessage("Tidak ada penjualan di range waktu ini");
    } else {
      ZksampleMessageUtils.showErrorMessage("Error!!!");
    }

  }

  public boolean validToPrint() throws Exception {
    Listitem itemDivisi = lbox_Divisi.getSelectedItem();
    if (itemDivisi != null) {
      ListModelList lml1 = (ListModelList) lbox_Divisi.getListModel();
      karyawan = (Karyawan) lml1.get(itemDivisi.getIndex());
    }
    PrintService printer = null;
    Listitem itemPrinter = lbox_Printer.getSelectedItem();
    if (itemPrinter != null) {
      ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
      printer = (PrintService) lml1.get(itemPrinter.getIndex());
      selectedPrinter = printer;
      logger.info("Printer : " + printer.getName());
    }
    if (karyawan != null && txtb_tanggalAwalPenjualan.getValue() != null
        && txtb_tanggalAkhirPenjualan.getValue() != null) {

      if (radioLaporanSales.isSelected()) {
        listPenjualan =
            getPenjualanService().getAllPenjualansBySalesAndRangeDate(karyawan,
                txtb_tanggalAwalPenjualan.getValue(), txtb_tanggalAkhirPenjualan.getValue());
      } else if (radioLaporanDivisi.isSelected()) {
        listPenjualan =
            getPenjualanService().getAllPenjualansByDivisiAndRangeDate(karyawan,
                txtb_tanggalAwalPenjualan.getValue(), txtb_tanggalAkhirPenjualan.getValue());
      }
      if (listPenjualan.size() > 0) {
        return true;
      }
    }
    return false;
  }


  public boolean validToPrintPerDivisi() throws Exception {
    Listitem itemDivisi = lbox_Divisi_per_divisi.getSelectedItem();
    if (itemDivisi != null) {
      ListModelList lml1 = (ListModelList) lbox_Divisi_per_divisi.getListModel();
      karyawan_per_divisi = (Karyawan) lml1.get(itemDivisi.getIndex());
    }
    PrintService printer = null;
    Listitem itemPrinter = lbox_Printer_per_divisi.getSelectedItem();
    if (itemPrinter != null) {
      ListModelList lml1 = (ListModelList) lbox_Printer_per_divisi.getListModel();
      printer = (PrintService) lml1.get(itemPrinter.getIndex());
      selectedPrinter_per_divisi = printer;
      logger.info("Printer : " + printer.getName());
    }
    if (karyawan_per_divisi != null && txtb_tanggalAwalPenjualan_per_divisi.getValue() != null
        && txtb_tanggalAkhirPenjualan_per_divisi.getValue() != null) {

      listPenjualan_per_divisi =
          getPenjualanService().getAllPenjualansByDivisiAndRangeDate(karyawan_per_divisi,
              txtb_tanggalAwalPenjualan_per_divisi.getValue(),
              txtb_tanggalAkhirPenjualan_per_divisi.getValue());

      if (listPenjualan_per_divisi.size() > 0) {
        return true;
      }
    }
    return false;
  }
}
