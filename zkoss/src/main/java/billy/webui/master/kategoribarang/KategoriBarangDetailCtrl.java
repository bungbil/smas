package billy.webui.master.kategoribarang;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.KategoriBarang;
import billy.backend.service.KategoriBarangService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class KategoriBarangDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(KategoriBarangDetailCtrl.class);

  protected Window windowKategoriBarangDetail; // autowired

  protected Borderlayout borderlayout_KategoriBarangDetail; // autowired

  protected Textbox txtb_KodeKategoriBarang; // autowired
  protected Textbox txtb_DeskripsiKategoriBarang; // autowired
  protected Label txtb_Status; // autowired

  // Databinding
  protected transient AnnotateDataBinder binder;
  private KategoriBarangMainCtrl kategoriBarangMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient KategoriBarangService kategoriBarangService;

  /**
   * default constructor.<br>
   */
  public KategoriBarangDetailCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setKategoriBarangMainCtrl((KategoriBarangMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getKategoriBarangMainCtrl().setKategoriBarangDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getKategoriBarangMainCtrl().getSelectedKategoriBarang() != null) {
        setSelectedKategoriBarang(getKategoriBarangMainCtrl().getSelectedKategoriBarang());
      } else
        setSelectedKategoriBarang(null);
    } else {
      setSelectedKategoriBarang(null);
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
    borderlayout_KategoriBarangDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowKategoriBarangDetail.invalidate();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doReadOnlyMode(boolean b) {
    txtb_KodeKategoriBarang.setReadonly(b);
    txtb_DeskripsiKategoriBarang.setReadonly(b);

  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */
  public KategoriBarang getKategoriBarang() {
    // STORED IN THE module's MainController
    return getKategoriBarangMainCtrl().getSelectedKategoriBarang();
  }

  public KategoriBarangMainCtrl getKategoriBarangMainCtrl() {
    return this.kategoriBarangMainCtrl;
  }

  public BindingListModelList getKategoriBarangs() {
    // STORED IN THE module's MainController
    return getKategoriBarangMainCtrl().getKategoriBarangs();
  }

  public KategoriBarangService getKategoriBarangService() {
    return this.kategoriBarangService;
  }

  public KategoriBarang getSelectedKategoriBarang() {
    // STORED IN THE module's MainController
    return getKategoriBarangMainCtrl().getSelectedKategoriBarang();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowKategoriBarangDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setKategoriBarang(KategoriBarang anKategoriBarang) {
    // STORED IN THE module's MainController
    getKategoriBarangMainCtrl().setSelectedKategoriBarang(anKategoriBarang);
  }

  /* CONTROLLERS */
  public void setKategoriBarangMainCtrl(KategoriBarangMainCtrl kategoriBarangMainCtrl) {
    this.kategoriBarangMainCtrl = kategoriBarangMainCtrl;
  }

  public void setKategoriBarangs(BindingListModelList kategoriBarangs) {
    // STORED IN THE module's MainController
    getKategoriBarangMainCtrl().setKategoriBarangs(kategoriBarangs);
  }

  /* SERVICES */
  public void setKategoriBarangService(KategoriBarangService kategoriBarangService) {
    this.kategoriBarangService = kategoriBarangService;
  }

  public void setSelectedKategoriBarang(KategoriBarang selectedKategoriBarang) {
    // STORED IN THE module's MainController
    getKategoriBarangMainCtrl().setSelectedKategoriBarang(selectedKategoriBarang);
  }

  /* COMPONENTS and OTHERS */

}
