package billy.webui.transaction.piutang.cetak;


import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Piutang;
import billy.backend.service.KaryawanService;
import billy.backend.service.PiutangService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.transaction.piutang.cetak.report.CetakKuitansiTextPrinter;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CetakPiutangMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(CetakPiutangMainCtrl.class);


  protected Window windowCetakPiutangMain; // autowired
  protected Listbox lbox_Divisi;
  protected Textbox txtb_KodeDivisi;
  protected Listbox lbox_Printer;
  protected Datebox txtb_tanggalAwalJatuhTempo;
  protected Datebox txtb_tanggalAkhirJatuhTempo;
  protected Button btnCetakKuitansi;
  protected Button btnReset;

  // ServiceDAOs / Domain Classes
  private PiutangService piutangService;
  private KaryawanService karyawanService;

  private PrintService selectedPrinter;
  List<Piutang> listPiutang = new ArrayList<Piutang>();

  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public CetakPiutangMainCtrl() {
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
    btnCetakKuitansi.setDisabled(!workspace.isAllowed("button_CetakPiutangMain_btnCetakKuitansi"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    List<Karyawan> listDivisi = getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);

    lbox_Divisi.setModel(new ListModelList(listDivisi));
    lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());

    txtb_tanggalAwalJatuhTempo.setValue(null);
    txtb_tanggalAkhirJatuhTempo.setValue(null);
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

  public void onChange$txtb_KodeDivisi(Event event) throws InterruptedException {
    if (txtb_KodeDivisi.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Divisi.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(txtb_KodeDivisi.getValue().trim());
      if (karyawan != null) {
        lbox_Divisi.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Divisi.setSelectedIndex(-1);
      }
    }
  }


  public void onClick$btnCetakKuitansi(Event event) throws Exception {
    if (validToPrint()) {

      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      // new CetakKuitansiA2DJReport(win);
      new CetakKuitansiTextPrinter(win, listPiutang, selectedPrinter);
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
  public void onCreate$windowCetakPiutangMain(Event event) throws Exception {
    windowCetakPiutangMain.setContentStyle("padding:0px;");

    doCheckRights();

    doReset();
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

    Karyawan karyawan = null;
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
    } else {
      ZksampleMessageUtils.showErrorMessage("Silakan pilih printer");
      return false;
    }
    if (karyawan != null && printer != null && txtb_tanggalAwalJatuhTempo.getValue() != null
        && txtb_tanggalAkhirJatuhTempo.getValue() != null) {

      listPiutang =
          getPiutangService().getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(karyawan,
              txtb_tanggalAwalJatuhTempo.getValue(), txtb_tanggalAkhirJatuhTempo.getValue());
      if (listPiutang.size() > 0) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }
}
