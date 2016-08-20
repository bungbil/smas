package billy.webui.report.perhitungankomisi;


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
import billy.webui.report.perhitungankomisi.report.PerhitunganKomisiDJReport;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportPerhitunganKomisiMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportPerhitunganKomisiMainCtrl.class);


  protected Window windowReportPerhitunganKomisiMain; // autowired
  protected Textbox txtb_KodeSales;
  protected Listbox lbox_Sales;
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
  public ReportPerhitunganKomisiMainCtrl() {
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
    btnView.setDisabled(!workspace.isAllowed("button_ReportPerhitunganKomisiMain_btnView"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    List<Karyawan> listSales = getKaryawanService().getAllSalesKaryawansByUserLogin(userLogin);

    lbox_Sales.setModel(new ListModelList(listSales));
    lbox_Sales.setItemRenderer(new KaryawanListModelItemRenderer());

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

  public void onChange$txtb_KodeSales(Event event) throws InterruptedException {
    if (txtb_KodeSales.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Sales.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(txtb_KodeSales.getValue().trim());
      if (karyawan != null) {
        lbox_Sales.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Sales.setSelectedIndex(-1);
      }
    }
  }

  public void onClick$btnView(Event event) throws Exception {
    if (validToPrint()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      new PerhitunganKomisiDJReport(win, karyawan, txtb_tanggalAwalPenjualan.getValue(),
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
  public void onCreate$windowReportPerhitunganKomisiMain(Event event) throws Exception {
    windowReportPerhitunganKomisiMain.setContentStyle("padding:0px;");

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
    Listitem itemSales = lbox_Sales.getSelectedItem();
    if (itemSales != null) {
      ListModelList lml1 = (ListModelList) lbox_Sales.getListModel();
      karyawan = (Karyawan) lml1.get(itemSales.getIndex());
    }

    if (karyawan != null && txtb_tanggalAwalPenjualan.getValue() != null
        && txtb_tanggalAkhirPenjualan.getValue() != null) {
      listPenjualan =
          getPenjualanService().getAllPenjualansBySalesAndRangeDate(karyawan,
              txtb_tanggalAwalPenjualan.getValue(), txtb_tanggalAkhirPenjualan.getValue());
      if (listPenjualan.size() > 0) {
        return true;
      }
    }
    return false;
  }
}
