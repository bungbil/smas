package billy.webui.transaction.piutang.approval;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;

public class ApprovalPiutangDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(ApprovalPiutangDetailCtrl.class);

  protected Window windowApprovalPiutangDetail; // autowired

  protected Borderlayout borderlayout_ApprovalPiutangDetail; // autowired

  protected Textbox txtb_NoFaktur; // autowired
  protected Textbox txtb_NoKuitansi; // autowired
  protected Textbox txtb_PembayaranKe; // autowired
  protected Datebox txtb_TglJatuhTempo; // autowired
  protected Decimalbox txb_NilaiTagihan;
  protected Listbox lbox_Status;
  protected Listbox lbox_Kolektor;
  protected Datebox txtb_tglPembayaran; // autowired
  protected Decimalbox txtb_Pembayaran;
  protected Textbox txtb_Keterangan; // autowired

  protected Label label_butuhApproval;
  protected Textbox txtb_ReasonApproval;
  protected Button btnApprovePiutang;
  public Textbox txtb_ApprovedBy;
  public Textbox txtb_ApprovedRemark;


  // Databinding
  protected transient AnnotateDataBinder binder;
  private ApprovalPiutangMainCtrl approvalPiutangMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient PiutangService piutangService;
  private transient KaryawanService karyawanService;
  private transient StatusService statusService;

  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public ApprovalPiutangDetailCtrl() {
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
      setApprovalPiutangMainCtrl((ApprovalPiutangMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getApprovalPiutangMainCtrl().setApprovalPiutangDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getApprovalPiutangMainCtrl().getSelectedPiutang() != null) {
        setSelectedPiutang(getApprovalPiutangMainCtrl().getSelectedPiutang());
        doRefresh();
        txtb_ApprovedBy.focus();
      } else
        setSelectedPiutang(null);
    } else {
      setSelectedPiutang(null);
    }

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
    borderlayout_ApprovalPiutangDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowApprovalPiutangDetail.invalidate();
  }

  public void doReadOnlyMode(boolean b) {

    lbox_Kolektor.setDisabled(b);
    txtb_tglPembayaran.setDisabled(b);
    txtb_Pembayaran.setReadonly(b);
    txtb_Keterangan.setReadonly(b);

  }

  public void doRefresh() {

    if (getSelectedPiutang() != null) {
      List<Status> listStatus = getStatusService().getAllStatuss();
      lbox_Status.setModel(new ListModelList(listStatus));
      lbox_Status.setItemRenderer(new StatusListModelItemRenderer());
      if (getSelectedPiutang().getStatus() != null) {
        ListModelList lml = (ListModelList) lbox_Status.getModel();
        Status status = getStatusService().getStatusByID(getSelectedPiutang().getStatus().getId());
        lbox_Status.setSelectedIndex(lml.indexOf(status));
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
  }

  public void emptyAllValue() {
    txtb_tglPembayaran.setValue(null);
    txtb_Pembayaran.setValue(BigDecimal.ZERO);
    txtb_Keterangan.setValue(null);
    lbox_Kolektor.setSelectedIndex(-1);

  }

  public ApprovalPiutangMainCtrl getApprovalPiutangMainCtrl() {
    return this.approvalPiutangMainCtrl;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //


  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public KaryawanService getKaryawanService() {
    return karyawanService;
  }


  /* Master BEANS */
  public Piutang getPiutang() {
    // STORED IN THE module's MainController
    return getApprovalPiutangMainCtrl().getSelectedPiutang();
  }

  public BindingListModelList getPiutangs() {
    // STORED IN THE module's MainController
    return getApprovalPiutangMainCtrl().getPiutangs();
  }

  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public Piutang getSelectedPiutang() {
    // STORED IN THE module's MainController
    return getApprovalPiutangMainCtrl().getSelectedPiutang();
  }

  public StatusService getStatusService() {
    return statusService;
  }

  public void onClick$btnApprovePiutang(Event event) throws InterruptedException {
    final Piutang anPiutang = getSelectedPiutang();
    if (anPiutang != null) {

      // Show a confirm box
      String msg = "Apakah anda yakin mengapprove piutang ini? ";
      final String title = Labels.getLabel("message.Information");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            @Override
            public void onEvent(Event evt) {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    getApprovalPiutangMainCtrl().doSave(evt);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  break; //
                case MultiLineMessageBox.NO:
                  break; //
              }
            }
          }) == MultiLineMessageBox.YES) {
      }
    }


  }


  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowApprovalPiutangDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }


  /* CONTROLLERS */
  public void setApprovalPiutangMainCtrl(ApprovalPiutangMainCtrl approvalPiutangMainCtrl) {
    this.approvalPiutangMainCtrl = approvalPiutangMainCtrl;
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  public void setPiutang(Piutang anPiutang) {
    // STORED IN THE module's MainController
    getApprovalPiutangMainCtrl().setSelectedPiutang(anPiutang);
  }

  public void setPiutangs(BindingListModelList piutangs) {
    // STORED IN THE module's MainController
    getApprovalPiutangMainCtrl().setPiutangs(piutangs);
  }

  /* SERVICES */
  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  public void setSelectedPiutang(Piutang selectedPiutang) {
    // STORED IN THE module's MainController
    getApprovalPiutangMainCtrl().setSelectedPiutang(selectedPiutang);
  }

  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }


  /* COMPONENTS and OTHERS */

}
