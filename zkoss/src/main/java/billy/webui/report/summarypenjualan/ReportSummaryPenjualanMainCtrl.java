package billy.webui.report.summarypenjualan;


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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.report.summarypenjualan.report.SummaryPenjualanDJReport;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportSummaryPenjualanMainCtrl extends GFCBaseCtrl implements Serializable {


  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportSummaryPenjualanMainCtrl.class);


  protected Window windowReportSummaryPenjualanMain; // autowired
  protected Textbox txtb_KodeDivisi;
  protected Listbox lbox_Divisi;
  protected Datebox txtb_tanggalAwalPenjualan;
  protected Datebox txtb_tanggalAkhirPenjualan;
  protected Button btnView;


  // ServiceDAOs / Domain Classes
  private PenjualanService penjualanService;
  private KaryawanService karyawanService;

  List<Penjualan> listPenjualan = new ArrayList<Penjualan>();
  Karyawan karyawan = null;

  /**
   * default constructor.<br>
   */
  public ReportSummaryPenjualanMainCtrl() {
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
    btnView.setDisabled(!workspace.isAllowed("button_ReportSummaryPenjualanMain_btnView"));
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    List<Karyawan> listDivisi = getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);

    lbox_Divisi.setModel(new ListModelList(listDivisi));
    lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());

    Date date = new Date(); // your date
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    cal.set(year, month, 1, 0, 0, 0);
    Date startDate = cal.getTime();
    txtb_tanggalAwalPenjualan.setValue(startDate);
    txtb_tanggalAkhirPenjualan.setValue(date);
    listPenjualan = new ArrayList<Penjualan>();

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

  public void onClick$btnView(Event event) throws Exception {
    if (validToPrint()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      new SummaryPenjualanDJReport(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
          txtb_tanggalAkhirPenjualan.getValue(), listPenjualan);
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
  public void onCreate$windowReportSummaryPenjualanMain(Event event) throws Exception {
    windowReportSummaryPenjualanMain.setContentStyle("padding:0px;");
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
    if (karyawan != null && txtb_tanggalAwalPenjualan.getValue() != null
        && txtb_tanggalAkhirPenjualan.getValue() != null) {
      listPenjualan =
          getPenjualanService().getAllPenjualansByDivisiAndRangeDate(karyawan,
              txtb_tanggalAwalPenjualan.getValue(), txtb_tanggalAkhirPenjualan.getValue());
      if (listPenjualan.size() > 0) {
        return true;
      }
    }
    return false;
  }
}
