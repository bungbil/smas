package billy.webui.report.penerimaanpembayaran;

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
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Piutang;
import billy.backend.service.KaryawanService;
import billy.backend.service.PiutangService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.report.penerimaanpembayaran.report.PenerimaanPembayaranDJReport;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportPenerimaanPembayaranMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportPenerimaanPembayaranMainCtrl.class);


  protected Window windowReportPenerimaanPembayaranMain; // autowired
  protected Listbox lbox_Kolektor;
  protected Datebox txtb_tanggalAwalPembayaran;
  protected Datebox txtb_tanggalAkhirPembayaran;
  protected Button btnView;


  // ServiceDAOs / Domain Classes
  private PiutangService piutangService;
  private KaryawanService karyawanService;

  List<Piutang> listPiutang = new ArrayList<Piutang>();
  Karyawan karyawan = null;

  /**
   * default constructor.<br>
   */
  public ReportPenerimaanPembayaranMainCtrl() {
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
    btnView.setDisabled(!workspace.isAllowed("button_ReportPenerimaanPembayaranMain_btnView"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    List<Karyawan> listKolektor = getKaryawanService().getKaryawansByJobTypeId(new Long(6));// Kolektor

    lbox_Kolektor.setModel(new ListModelList(listKolektor));
    lbox_Kolektor.setItemRenderer(new KaryawanListModelItemRenderer());

    Date date = new Date(); // your date
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    cal.set(year, month, 1, 0, 0, 0);
    Date startDate = cal.getTime();
    txtb_tanggalAwalPembayaran.setValue(startDate);
    txtb_tanggalAkhirPembayaran.setValue(date);
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
      new PenerimaanPembayaranDJReport(win, karyawan, txtb_tanggalAwalPembayaran.getValue(),
          txtb_tanggalAkhirPembayaran.getValue(), listPiutang);
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
  public void onCreate$windowReportPenerimaanPembayaranMain(Event event) throws Exception {
    windowReportPenerimaanPembayaranMain.setContentStyle("padding:0px;");
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
    Listitem itemDivisi = lbox_Kolektor.getSelectedItem();
    if (itemDivisi != null) {
      ListModelList lml1 = (ListModelList) lbox_Kolektor.getListModel();
      karyawan = (Karyawan) lml1.get(itemDivisi.getIndex());
    }
    if (karyawan != null && txtb_tanggalAwalPembayaran.getValue() != null
        && txtb_tanggalAkhirPembayaran.getValue() != null) {

      listPiutang =
          getPiutangService().getAllPiutangsByKolektorAndRangeDate(karyawan,
              txtb_tanggalAwalPembayaran.getValue(), txtb_tanggalAkhirPembayaran.getValue());
      if (listPiutang.size() > 0) {
        return true;
      }
    }
    return false;
  }
}
