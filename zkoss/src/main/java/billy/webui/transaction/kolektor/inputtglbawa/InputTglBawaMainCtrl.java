package billy.webui.transaction.kolektor.inputtglbawa;


import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
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
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class InputTglBawaMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(InputTglBawaMainCtrl.class);


  protected Window windowInputTglBawaMain; // autowired

  protected Textbox txtb_SearchNoKwitansi;
  protected Button btnSave;
  protected Button btnSearch;
  protected Panel panelResult;


  protected Textbox txtb_NoFaktur;
  protected Textbox txtb_NoKuitansi;
  protected Textbox txtb_PembayaranKe;
  protected Datebox txtb_TglJatuhTempo;
  protected Decimalbox txtb_NilaiTagihan;
  protected Listbox lbox_Status;
  protected Textbox txtb_KodeKolektor;
  protected Listbox lbox_Kolektor;
  protected Datebox txtb_tglBawaKolektor;

  protected Datebox txtb_tglPembayaran;
  protected Decimalbox txtb_Pembayaran;
  protected Decimalbox txtb_Diskon;
  protected Textbox txtb_Keterangan;

  // ServiceDAOs / Domain Classes
  private PiutangService piutangService;
  private KaryawanService karyawanService;
  private StatusService statusService;
  DecimalFormat df = new DecimalFormat("#,###");
  Piutang piutang = new Piutang();

  /**
   * default constructor.<br>
   */
  public InputTglBawaMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
  }

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
    btnSave.setDisabled(!workspace.isAllowed("button_InputTglBawaMain_btnSave"));
    btnSearch.setDisabled(!workspace.isAllowed("button_InputTglBawaMain_btnSearch"));
  }

  private void doSave(Event event) throws Exception {
    if (txtb_tglBawaKolektor.getValue() != null) {
      piutang.setTglBawaKolektor(txtb_tglBawaKolektor.getValue());
    } else {
      piutang.setTglBawaKolektor(null);
    }

    Listitem itemKolektor = lbox_Kolektor.getSelectedItem();
    if (itemKolektor != null) {
      ListModelList lml1 = (ListModelList) lbox_Kolektor.getListModel();
      Karyawan karyawan = (Karyawan) lml1.get(itemKolektor.getIndex());
      piutang.setKolektor(karyawan);
      piutang.getPenjualan().setKolektor(karyawan);
    } else {
      piutang.setKolektor(null);
    }

    String userName =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsername();
    piutang.setLastUpdate(new Date());
    piutang.setUpdatedBy(userName);

    // save it to database
    getPiutangService().saveOrUpdate(piutang);

    panelResult.setVisible(false);
    piutang = null;
    txtb_SearchNoKwitansi.focus();
  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* SERVICES */
  public PiutangService getPiutangService() {
    return this.piutangService;
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

  public void onClick$btnSave(Event event) throws Exception {

    if (piutang != null) {

      // Show a confirm box
      final String msg =
          Labels.getLabel("message_Data_Modified_Save_Data_YesNo") + "\n\n --> "
              + piutang.getNoKuitansi();
      final String title = Labels.getLabel("message_Saving_Data");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {

            @Override
            public void onEvent(Event evt) throws Exception {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    doSave(evt);
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


  public void onClick$btnSearch(Event event) throws Exception {

    if (txtb_SearchNoKwitansi.getValue() != null) {
      piutang = piutangService.getPiutangByNoKuitansi(txtb_SearchNoKwitansi.getValue());
      if (piutang != null) {

        txtb_NoFaktur.setValue(piutang.getPenjualan().getNoFaktur());
        txtb_NoKuitansi.setValue(piutang.getNoKuitansi());
        txtb_PembayaranKe.setValue(String.valueOf(piutang.getPembayaranKe()));
        txtb_TglJatuhTempo.setValue(piutang.getTglJatuhTempo());
        txtb_NilaiTagihan.setValue(piutang.getNilaiTagihan());


        List<Status> listStatus = getStatusService().getAllStatuss();
        lbox_Status.setModel(new ListModelList(listStatus));
        lbox_Status.setItemRenderer(new StatusListModelItemRenderer());
        if (piutang.getStatus() != null) {
          ListModelList lml = (ListModelList) lbox_Status.getModel();
          Status status = getStatusService().getStatusByID(piutang.getStatus().getId());
          lbox_Status.setSelectedIndex(lml.indexOf(status));
        }

        List<Karyawan> listKaryawan = getKaryawanService().getKaryawansByJobTypeId(new Long(6));// Kolektor

        lbox_Kolektor.setModel(new ListModelList(listKaryawan));
        lbox_Kolektor.setItemRenderer(new KaryawanListModelItemRenderer());
        if (piutang.getKolektor() != null) {
          ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
          Karyawan karyawan = getKaryawanService().getKaryawanByID(piutang.getKolektor().getId());
          lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
          txtb_KodeKolektor.setValue(karyawan.getKodeKaryawan());
        }
        if (piutang.getTglBawaKolektor() != null) {
          txtb_tglBawaKolektor.setValue(piutang.getTglBawaKolektor());
        } else {
          txtb_tglBawaKolektor.setValue(new Date());
        }

        panelResult.setVisible(true);
        txtb_tglPembayaran.setValue(piutang.getTglPembayaran());
        txtb_Pembayaran.setValue(piutang.getPembayaran());
        txtb_Diskon.setValue(piutang.getDiskon());
        txtb_Keterangan.setValue(piutang.getKeterangan());

        txtb_KodeKolektor.focus();
      } else {
        panelResult.setVisible(false);
        ZksampleMessageUtils.showErrorMessage("No Kwitansi tidak ditemukan");
        return;
      }

    } else {
      ZksampleMessageUtils.showErrorMessage("Harap masukkan No Kwitansi");
      return;
    }

  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowInputTglBawaMain(Event event) throws Exception {
    windowInputTglBawaMain.setContentStyle("padding:0px;");
    panelResult.setVisible(false);
    doCheckRights();
    txtb_SearchNoKwitansi.focus();

  }

  public void onOK$txtb_KodeKolektor(Event event) throws InterruptedException {
    txtb_tglBawaKolektor.focus();
  }

  public void onOK$txtb_SearchNoKwitansi(Event event) throws InterruptedException {
    btnSearch.focus();
  }

  public void onOK$txtb_tglBawaKolektor(Event event) throws InterruptedException {
    btnSave.focus();
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */


  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  /* COMPONENTS and OTHERS */

  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }


}
