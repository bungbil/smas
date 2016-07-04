package billy.webui.master.satuanbarang;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.SatuanBarang;
import billy.backend.service.SatuanBarangService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class SatuanBarangDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(SatuanBarangDetailCtrl.class);

  protected Window windowSatuanBarangDetail; // autowired

  protected Borderlayout borderlayout_SatuanBarangDetail; // autowired

  protected Textbox txtb_KodeSatuanBarang; // autowired
  protected Textbox txtb_DeskripsiSatuanBarang; // autowired
  protected Checkbox txtb_SatuanStandarBarang; // autowired
  protected Intbox txtb_NilaiStandarBarang;
  protected Intbox txtb_NilaiKonversi;
  // Databinding
  protected transient AnnotateDataBinder binder;
  private SatuanBarangMainCtrl satuanBarangMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient SatuanBarangService satuanBarangService;

  /**
   * default constructor.<br>
   */
  public SatuanBarangDetailCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setSatuanBarangMainCtrl((SatuanBarangMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getSatuanBarangMainCtrl().setSatuanBarangDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getSatuanBarangMainCtrl().getSelectedSatuanBarang() != null) {
        setSelectedSatuanBarang(getSatuanBarangMainCtrl().getSelectedSatuanBarang());
      } else
        setSelectedSatuanBarang(null);
    } else {
      setSelectedSatuanBarang(null);
    }

  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFitSize(Event event) {

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - 152;
    borderlayout_SatuanBarangDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowSatuanBarangDetail.invalidate();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doReadOnlyMode(boolean b) {
    txtb_KodeSatuanBarang.setReadonly(b);
    txtb_DeskripsiSatuanBarang.setReadonly(b);
    txtb_SatuanStandarBarang.setDisabled(b);
    txtb_NilaiStandarBarang.setReadonly(b);
    txtb_NilaiKonversi.setReadonly(b);

  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */
  public SatuanBarang getSatuanBarang() {
    // STORED IN THE module's MainController
    return getSatuanBarangMainCtrl().getSelectedSatuanBarang();
  }

  public SatuanBarangMainCtrl getSatuanBarangMainCtrl() {
    return this.satuanBarangMainCtrl;
  }

  public BindingListModelList getSatuanBarangs() {
    // STORED IN THE module's MainController
    return getSatuanBarangMainCtrl().getSatuanBarangs();
  }

  public SatuanBarangService getSatuanBarangService() {
    return this.satuanBarangService;
  }

  public SatuanBarang getSelectedSatuanBarang() {
    // STORED IN THE module's MainController
    return getSatuanBarangMainCtrl().getSelectedSatuanBarang();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowSatuanBarangDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setSatuanBarang(SatuanBarang anSatuanBarang) {
    // STORED IN THE module's MainController
    getSatuanBarangMainCtrl().setSelectedSatuanBarang(anSatuanBarang);
  }

  /* CONTROLLERS */
  public void setSatuanBarangMainCtrl(SatuanBarangMainCtrl satuanBarangMainCtrl) {
    this.satuanBarangMainCtrl = satuanBarangMainCtrl;
  }

  public void setSatuanBarangs(BindingListModelList satuanBarangs) {
    // STORED IN THE module's MainController
    getSatuanBarangMainCtrl().setSatuanBarangs(satuanBarangs);
  }

  /* SERVICES */
  public void setSatuanBarangService(SatuanBarangService satuanBarangService) {
    this.satuanBarangService = satuanBarangService;
  }

  public void setSelectedSatuanBarang(SatuanBarang selectedSatuanBarang) {
    // STORED IN THE module's MainController
    getSatuanBarangMainCtrl().setSelectedSatuanBarang(selectedSatuanBarang);
  }

  /* COMPONENTS and OTHERS */

}
