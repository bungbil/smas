package billy.webui.report.komisi;

import java.io.Serializable;
import java.util.ArrayList;
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
import billy.backend.model.Penjualan;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.report.komisi.report.KomisiDivisiDJReport;
import billy.webui.report.komisi.report.KomisiSalesDJReport;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportKomisiMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportKomisiMainCtrl.class);


  protected Window windowReportKomisiMain; // autowired
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
  public ReportKomisiMainCtrl() {
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
    btnView.setDisabled(!workspace.isAllowed("button_ReportKomisiMain_btnView"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    List<Karyawan> listDivisi = getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);
    List<Karyawan> listSales = getKaryawanService().getAllSalesKaryawansByUserLogin(userLogin);
    listDivisi.addAll(listSales);
    lbox_Divisi.setModel(new ListModelList(listDivisi));
    lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());

    txtb_tanggalAwalPenjualan.setValue(null);
    txtb_tanggalAkhirPenjualan.setValue(null);
    listPenjualan = new ArrayList<Penjualan>();

  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  /* SERVICES */
  public PenjualanService getPenjualanService() {
    return this.penjualanService;
  }

  public void onClick$btnView(Event event) throws Exception {
    if (validToPrint()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");

      if (karyawan.getJobType().getId() == 4) {
        new KomisiSalesDJReport(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
            txtb_tanggalAkhirPenjualan.getValue(), listPenjualan);
      } else if (karyawan.getJobType().getId() == 2) {
        new KomisiDivisiDJReport(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
            txtb_tanggalAkhirPenjualan.getValue(), listPenjualan);
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
  public void onCreate$windowReportKomisiMain(Event event) throws Exception {
    windowReportKomisiMain.setContentStyle("padding:0px;");
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

      if (karyawan.getJobType().getId() == 4) {
        listPenjualan =
            getPenjualanService().getAllPenjualansBySalesAndRangeDate(karyawan,
                txtb_tanggalAwalPenjualan.getValue(), txtb_tanggalAkhirPenjualan.getValue());
      } else if (karyawan.getJobType().getId() == 2) {
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
}
