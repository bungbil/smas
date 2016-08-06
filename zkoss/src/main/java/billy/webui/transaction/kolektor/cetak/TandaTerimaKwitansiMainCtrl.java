package billy.webui.transaction.kolektor.cetak;


import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Piutang;
import billy.backend.service.KaryawanService;
import billy.backend.service.PiutangService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.transaction.kolektor.cetak.report.CetakTandaTerimaKwitansiTextPrinter;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class TandaTerimaKwitansiMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(TandaTerimaKwitansiMainCtrl.class);


  protected Window windowTandaTerimaKwitansiMain; // autowired
  protected Listbox lbox_Kolektor;
  protected Textbox txtb_KodeKolektor;
  protected Listbox lbox_Printer;
  protected Datebox txtb_tanggalAwal;
  protected Datebox txtb_tanggalAkhir;
  protected Button btnCetak;
  protected Button btnReset;

  // ServiceDAOs / Domain Classes
  private PiutangService piutangService;
  private KaryawanService karyawanService;

  private PrintService selectedPrinter;
  List<Piutang> listPiutang = new ArrayList<Piutang>();
  Karyawan karyawan = null;
  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public TandaTerimaKwitansiMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    lbox_Printer.setModel(new ListModelList(printServices));
    lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    ListModelList lml = (ListModelList) lbox_Printer.getModel();
    lbox_Printer.setSelectedIndex(lml.indexOf(service));

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
    btnCetak.setDisabled(!workspace.isAllowed("button_CetakTandaTerimaKwitansiMain_btnCetak"));
  }

  public void doReset() {

    List<Karyawan> listKolektor = getKaryawanService().getKaryawansByJobTypeId(new Long(6)); // Kolektor

    lbox_Kolektor.setModel(new ListModelList(listKolektor));
    lbox_Kolektor.setItemRenderer(new KaryawanListModelItemRenderer());

    txtb_tanggalAwal.setValue(null);
    txtb_tanggalAkhir.setValue(null);
    listPiutang = new ArrayList<Piutang>();

    selectedPrinter = null;

  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  /* SERVICES */
  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public void onChange$txtb_KodeKolektor(Event event) throws InterruptedException {
    if (txtb_KodeKolektor.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(txtb_KodeKolektor.getValue().trim());
      if (karyawan != null) {
        lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Kolektor.setSelectedIndex(-1);
      }
    }
  }

  public void onClick$btnCetak(Event event) throws Exception {
    if (validToPrint()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      new CetakTandaTerimaKwitansiTextPrinter(win, karyawan, txtb_tanggalAwal.getValue(),
          txtb_tanggalAkhir.getValue(), listPiutang, selectedPrinter);
    } else {
      showErrorCetak();
    }
  }

  public void onClick$btnRefeshPrinter(Event event) throws Exception {
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    lbox_Printer.setModel(new ListModelList(printServices));
    lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
  }

  public void onClick$btnReset(Event event) throws Exception {
    doReset();
  }


  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowTandaTerimaKwitansiMain(Event event) throws Exception {
    windowTandaTerimaKwitansiMain.setContentStyle("padding:0px;");

    doCheckRights();

    doReset();
    txtb_KodeKolektor.focus();
  }

  public void onOK$txtb_KodeKolektor(Event event) throws InterruptedException {
    txtb_tanggalAwal.focus();
  }

  public void onOK$txtb_tanggalAkhir(Event event) throws InterruptedException {
    btnCetak.focus();
  }

  public void onOK$txtb_tanggalAwal(Event event) throws InterruptedException {
    txtb_tanggalAkhir.focus();
  }


  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */


  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  /* COMPONENTS and OTHERS */

  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  public void showErrorCetak() throws Exception {

    ZksampleMessageUtils
        .showErrorMessage("List Kuitansi tidak ditemukan / Silakan dipilih Printernya");

  }

  public boolean validToPrint() throws Exception {

    Listitem itemKolektor = lbox_Kolektor.getSelectedItem();
    if (itemKolektor != null) {
      ListModelList lml1 = (ListModelList) lbox_Kolektor.getListModel();
      karyawan = (Karyawan) lml1.get(itemKolektor.getIndex());
    }
    PrintService printer = null;
    Listitem itemPrinter = lbox_Printer.getSelectedItem();
    if (itemPrinter != null) {
      ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
      printer = (PrintService) lml1.get(itemPrinter.getIndex());
      selectedPrinter = printer;
      // logger.info("Printer : " + printer.getName());
    }
    if (karyawan != null && printer != null && txtb_tanggalAwal.getValue() != null
        && txtb_tanggalAkhir.getValue() != null) {

      listPiutang =
          getPiutangService().getAllPiutangsByKolektorAndRangeDateTglBawaBelumBayar(karyawan,
              txtb_tanggalAwal.getValue(), txtb_tanggalAkhir.getValue());
      if (listPiutang.size() > 0) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }
}
