package billy.webui.report.piutang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Piutang;
import billy.backend.service.KaryawanService;
import billy.backend.service.PiutangService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.report.piutang.report.PiutangDJReport;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportPiutangMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportPiutangMainCtrl.class);


  protected Window windowReportPiutangMain; // autowired
  protected Listbox lbox_Divisi;
  protected Datebox txtb_tanggalAwal;
  protected Datebox txtb_tanggalAkhir;
  protected Radiogroup radiogroup_Tanggal;
  protected Radio radioTglJatuhTempo;
  protected Radio radioTglPembayaran;
  protected Button btnView;


  // ServiceDAOs / Domain Classes
  private PiutangService piutangService;
  private KaryawanService karyawanService;

  List<Piutang> listPiutang = new ArrayList<Piutang>();
  Karyawan karyawan = null;

  /**
   * default constructor.<br>
   */
  public ReportPiutangMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

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
    btnView.setDisabled(!workspace.isAllowed("button_ReportPiutangMain_btnView"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    List<Karyawan> listDivisi = getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);

    lbox_Divisi.setModel(new ListModelList(listDivisi));
    lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());
    radioTglJatuhTempo.setSelected(true);

    Date date = new Date(); // your date
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    cal.set(year, month, 1, 0, 0, 0);
    Date startDate = cal.getTime();
    txtb_tanggalAwal.setValue(startDate);
    txtb_tanggalAkhir.setValue(date);
    listPiutang = new ArrayList<Piutang>();

  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  /* SERVICES */
  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public void onClick$btnView(Event event) throws Exception {
    if (validToPrint()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      if (radioTglJatuhTempo.isSelected()) {
        new PiutangDJReport(win, karyawan, txtb_tanggalAwal.getValue(),
            txtb_tanggalAkhir.getValue(), listPiutang, radioTglJatuhTempo.getLabel());
      } else if (radioTglPembayaran.isSelected()) {
        new PiutangDJReport(win, karyawan, txtb_tanggalAwal.getValue(),
            txtb_tanggalAkhir.getValue(), listPiutang, radioTglPembayaran.getLabel());
      }
    } else {
      showErrorCetak();
    }
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowReportPiutangMain(Event event) throws Exception {
    windowReportPiutangMain.setContentStyle("padding:0px;");
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
    if (listPiutang.size() == 0) {
      ZksampleMessageUtils
          .showErrorMessage("Tidak ada piutang terbayar di range waktu pembayaran ini");
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

    if (karyawan != null && txtb_tanggalAwal.getValue() != null
        && txtb_tanggalAkhir.getValue() != null) {

      if (radioTglJatuhTempo.isSelected()) {
        listPiutang =
            getPiutangService().getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(karyawan,
                txtb_tanggalAwal.getValue(), txtb_tanggalAkhir.getValue());

      } else if (radioTglPembayaran.isSelected()) {
        listPiutang =
            getPiutangService().getAllPiutangsByDivisiAndRangeDateTglPembayaran(karyawan,
                txtb_tanggalAwal.getValue(), txtb_tanggalAkhir.getValue());

      }


      if (listPiutang.size() > 0) {
        return true;
      }
    }
    return false;
  }
}
