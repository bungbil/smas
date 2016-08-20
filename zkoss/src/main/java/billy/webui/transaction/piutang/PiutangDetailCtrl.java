package billy.webui.transaction.piutang;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.KaryawanService;
import billy.backend.service.PiutangService;
import billy.backend.service.StatusService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.master.status.model.StatusListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.transaction.piutang.cetak.report.CetakKuitansiTextPrinter;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PiutangDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(PiutangDetailCtrl.class);

  protected Window windowPiutangDetail; // autowired

  protected Borderlayout borderlayout_PiutangDetail; // autowired

  protected Textbox txtb_NoFaktur; // autowired
  protected Textbox txtb_NoKuitansi; // autowired
  protected Textbox txtb_PembayaranKe; // autowired
  protected Datebox txtb_TglJatuhTempo; // autowired
  protected Decimalbox txb_NilaiTagihan;
  protected Listbox lbox_Status;
  protected Listbox lbox_Kolektor;
  protected Textbox txtb_KodeKolektor;
  protected Datebox txtb_tglPembayaran; // autowired
  protected Decimalbox txtb_Pembayaran;
  protected Textbox txtb_Keterangan; // autowired

  protected Datebox txtb_tglBawaKolektor;
  protected Decimalbox txtb_Diskon;
  protected Listbox lbox_StatusFinal;
  protected Button btnCetak;
  protected Listbox lbox_Printer;

  protected Label label_butuhApproval;
  protected Textbox txtb_ReasonApproval;
  protected Button btnApprovePiutang;
  public Textbox txtb_ApprovedBy;
  public Textbox txtb_ApprovedRemark;


  // Databinding
  protected transient AnnotateDataBinder binder;
  private PiutangMainCtrl piutangMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient PiutangService piutangService;
  private transient KaryawanService karyawanService;
  private transient StatusService statusService;
  private PrintService selectedPrinter;
  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public PiutangDetailCtrl() {
    super();
  }


  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //


  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setPiutangMainCtrl((PiutangMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getPiutangMainCtrl().setPiutangDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getPiutangMainCtrl().getSelectedPiutang() != null) {
        setSelectedPiutang(getPiutangMainCtrl().getSelectedPiutang());
        doRefresh();
      } else
        setSelectedPiutang(null);
    } else {
      setSelectedPiutang(null);
    }

    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    lbox_Printer.setModel(new ListModelList(printServices));
    lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    ListModelList lml = (ListModelList) lbox_Printer.getModel();
    lbox_Printer.setSelectedIndex(lml.indexOf(service));
  }

  public void doApprovalMode() {
    // doReadOnlyMode(true);
    txtb_ApprovedBy.setReadonly(false);
    txtb_ApprovedRemark.setReadonly(false);
    btnApprovePiutang.setVisible(true);
  }

  public void doFitSize(Event event) {

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - 152;
    borderlayout_PiutangDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowPiutangDetail.invalidate();
  }

  public void doReadOnlyMode(boolean b) {

    lbox_Kolektor.setDisabled(b);
    txtb_tglPembayaran.setDisabled(b);
    txtb_Pembayaran.setReadonly(b);
    txtb_Keterangan.setReadonly(b);
    txtb_KodeKolektor.setReadonly(b);
    txtb_tglBawaKolektor.setDisabled(b);
    txtb_Diskon.setReadonly(b);
    lbox_StatusFinal.setDisabled(b);
  }

  public void doRefresh() {
    List<Status> listStatus = getStatusService().getAllStatuss();
    lbox_Status.setModel(new ListModelList(listStatus));
    lbox_Status.setItemRenderer(new StatusListModelItemRenderer());
    if (getSelectedPiutang().getStatus() != null) {
      ListModelList lml = (ListModelList) lbox_Status.getModel();
      Status status = getStatusService().getStatusByID(getSelectedPiutang().getStatus().getId());
      lbox_Status.setSelectedIndex(lml.indexOf(status));
    }

    lbox_StatusFinal.setModel(new ListModelList(listStatus));
    lbox_StatusFinal.setItemRenderer(new StatusListModelItemRenderer());
    if (getSelectedPiutang().getStatusFinal() != null) {
      ListModelList lml = (ListModelList) lbox_StatusFinal.getModel();
      Status status =
          getStatusService().getStatusByID(getSelectedPiutang().getStatusFinal().getId());
      lbox_StatusFinal.setSelectedIndex(lml.indexOf(status));
    }


    List<Karyawan> listKaryawan = getKaryawanService().getKaryawansByJobTypeId(new Long(6));// Kolektor

    lbox_Kolektor.setModel(new ListModelList(listKaryawan));
    lbox_Kolektor.setItemRenderer(new KaryawanListModelItemRenderer());
    if (getSelectedPiutang().getKolektor() != null) {
      ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByID(getSelectedPiutang().getKolektor().getId());
      lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
    }


  }

  public void emptyAllValue() {
    txtb_tglBawaKolektor.setValue(null);
    txtb_tglPembayaran.setValue(null);
    txtb_Pembayaran.setValue(BigDecimal.ZERO);
    txtb_Diskon.setValue(BigDecimal.ZERO);
    txtb_Keterangan.setValue(null);
    lbox_Kolektor.setSelectedIndex(-1);
    lbox_StatusFinal.setSelectedIndex(-1);
    selectedPrinter = null;
  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //


  public KaryawanService getKaryawanService() {
    return karyawanService;
  }

  /* Master BEANS */
  public Piutang getPiutang() {
    // STORED IN THE module's MainController
    return getPiutangMainCtrl().getSelectedPiutang();
  }


  public PiutangMainCtrl getPiutangMainCtrl() {
    return this.piutangMainCtrl;
  }

  public BindingListModelList getPiutangs() {
    // STORED IN THE module's MainController
    return getPiutangMainCtrl().getPiutangs();
  }

  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public Piutang getSelectedPiutang() {
    // STORED IN THE module's MainController
    return getPiutangMainCtrl().getSelectedPiutang();
  }

  public StatusService getStatusService() {
    return statusService;
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
    if (validToCetak()) {
      PrintService printer = null;
      Listitem itemPrinter = lbox_Printer.getSelectedItem();
      if (itemPrinter != null) {
        ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
        printer = (PrintService) lml1.get(itemPrinter.getIndex());
        selectedPrinter = printer;
        logger.info("Printer : " + printer.getName());
      }

      final Piutang anPiutang = getSelectedPiutang();
      if (anPiutang != null) {

        // Show a confirm box
        final String msg =
            "Apakah anda yakin akan melanjutkan mencetak kwitansi berikutnya?? \n\n ";
        final String title = "";

        MultiLineMessageBox.doSetTemplate();
        if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO,
            Messagebox.QUESTION, true, new EventListener() {
              private void cetakBean() throws InterruptedException {
                try {
                  // tutup piutang, set aktif = false
                  Piutang piutang = getSelectedPiutang();
                  Status statusLunas = getStatusService().getStatusByID(new Long(2)); // LUNAS
                  BigDecimal lastPiutang = piutang.getPenjualan().getPiutang();
                  BigDecimal totalPembayaran = piutang.getPembayaran().add(piutang.getDiskon());
                  piutang.getPenjualan().setPiutang(lastPiutang.subtract(totalPembayaran));
                  piutang.setStatus(statusLunas);
                  piutang.setAktif(false);
                  piutangService.saveOrUpdate(piutang);

                  BigDecimal kekuranganBayar =
                      piutang.getNilaiTagihan().subtract(piutang.getPembayaran())
                          .subtract(piutang.getDiskon());
                  // get next piutang, set aktif = true, kekurangan dari piutang sebelumnya
                  Piutang nextPiutang = piutangService.getNextPiutang(piutang);
                  nextPiutang.setAktif(true);
                  nextPiutang.setKekuranganBayar(kekuranganBayar);
                  getPiutangService().saveOrUpdate(nextPiutang);

                  if (nextPiutang != null) {
                    final Window win = (Window) Path.getComponent("/outerIndexWindow");
                    List<Piutang> listPiutang = new ArrayList<Piutang>();
                    listPiutang.add(nextPiutang);
                    new CetakKuitansiTextPrinter(win, listPiutang, selectedPrinter);
                  }
                } catch (DataAccessException e) {
                  ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
                }
              }

              @Override
              public void onEvent(Event evt) {
                switch (((Integer) evt.getData()).intValue()) {
                  case MultiLineMessageBox.YES:
                    try {
                      cetakBean();
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                    break; //
                  case MultiLineMessageBox.NO:
                    break; //
                }
              }
            }

        ) == MultiLineMessageBox.YES) {
        }

      }
    }
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowPiutangDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  /* COMPONENTS and OTHERS */


  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  public void setPiutang(Piutang anPiutang) {
    // STORED IN THE module's MainController
    getPiutangMainCtrl().setSelectedPiutang(anPiutang);
  }

  /* CONTROLLERS */
  public void setPiutangMainCtrl(PiutangMainCtrl piutangMainCtrl) {
    this.piutangMainCtrl = piutangMainCtrl;
  }

  public void setPiutangs(BindingListModelList piutangs) {
    // STORED IN THE module's MainController
    getPiutangMainCtrl().setPiutangs(piutangs);
  }

  /* SERVICES */
  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  public void setSelectedPiutang(Piutang selectedPiutang) {
    // STORED IN THE module's MainController
    getPiutangMainCtrl().setSelectedPiutang(selectedPiutang);
  }

  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  public boolean validToCetak() {
    if (getSelectedPiutang().getStatus().getId() == new Long(5)) {
      return false;
    } else if (getSelectedPiutang().getStatus().getId() == new Long(6)) {
      return false;
    } else if (getSelectedPiutang().getStatus().getId() == new Long(7)) {
      return false;
    } else if (getSelectedPiutang().getStatus().getId() == new Long(8)) {
      return false;
    }

    return true;
  }
}
