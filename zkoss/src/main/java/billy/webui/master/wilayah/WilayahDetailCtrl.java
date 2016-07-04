package billy.webui.master.wilayah;

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
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Wilayah;
import billy.backend.service.WilayahService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class WilayahDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(WilayahDetailCtrl.class);

  protected Window windowWilayahDetail; // autowired

  protected Borderlayout borderlayout_WilayahDetail; // autowired

  protected Textbox txtb_KodeWilayah; // autowired
  protected Textbox txtb_NamaWilayah; // autowired
  protected Label txtb_Status; // autowired
  protected Radiogroup radiogroup_Status; // autowired
  protected Radio radioStatusPusat;
  protected Radio radioStatusDaerah;
  // Databinding
  protected transient AnnotateDataBinder binder;
  private WilayahMainCtrl wilayahMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient WilayahService wilayahService;

  /**
   * default constructor.<br>
   */
  public WilayahDetailCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setWilayahMainCtrl((WilayahMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getWilayahMainCtrl().setWilayahDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getWilayahMainCtrl().getSelectedWilayah() != null) {
        setSelectedWilayah(getWilayahMainCtrl().getSelectedWilayah());
        doRefresh();
      } else
        setSelectedWilayah(null);
    } else {
      setSelectedWilayah(null);
    }

  }

  public void doFitSize(Event event) {

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - 152;
    borderlayout_WilayahDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowWilayahDetail.invalidate();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doReadOnlyMode(boolean b) {
    txtb_KodeWilayah.setReadonly(b);
    txtb_NamaWilayah.setReadonly(b);
    radioStatusPusat.setDisabled(b);
    radioStatusDaerah.setDisabled(b);

  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doRefresh() {
    if (getSelectedWilayah().getStatus().equals(radioStatusPusat.getLabel())) {
      radioStatusPusat.setSelected(true);
    }
    if (getSelectedWilayah().getStatus().equals(radioStatusDaerah.getLabel())) {
      radioStatusDaerah.setSelected(true);
    }

  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public Wilayah getSelectedWilayah() {
    // STORED IN THE module's MainController
    return getWilayahMainCtrl().getSelectedWilayah();
  }

  /* Master BEANS */
  public Wilayah getWilayah() {
    // STORED IN THE module's MainController
    return getWilayahMainCtrl().getSelectedWilayah();
  }


  public WilayahMainCtrl getWilayahMainCtrl() {
    return this.wilayahMainCtrl;
  }

  public BindingListModelList getWilayahs() {
    // STORED IN THE module's MainController
    return getWilayahMainCtrl().getWilayahs();
  }

  public WilayahService getWilayahService() {
    return this.wilayahService;
  }

  public void onBlur$txtb_KodeWilayah(Event event) throws InterruptedException {

    Wilayah wilayahCheckKode = null;
    wilayahCheckKode = getWilayahService().getWilayahByKodeWilayah(getWilayah().getKodeWilayah());

    if (wilayahCheckKode != null) {
      if (wilayahCheckKode.getId() != getWilayah().getId()) {
        ZksampleMessageUtils.showErrorMessage("Kode Wilayah sudah digunakan oleh wilayah : "
            + wilayahCheckKode.getNamaWilayah());
        return;
      }
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowWilayahDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void onOK$txtb_KodeWilayah(Event event) throws InterruptedException {
    txtb_NamaWilayah.focus();
  }

  public void onOK$txtb_NamaWilayah(Event event) throws InterruptedException {
    radiogroup_Status.focus();
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setSelectedWilayah(Wilayah selectedWilayah) {
    // STORED IN THE module's MainController
    getWilayahMainCtrl().setSelectedWilayah(selectedWilayah);
  }

  public void setWilayah(Wilayah anWilayah) {
    // STORED IN THE module's MainController
    getWilayahMainCtrl().setSelectedWilayah(anWilayah);
  }

  /* CONTROLLERS */
  public void setWilayahMainCtrl(WilayahMainCtrl wilayahMainCtrl) {
    this.wilayahMainCtrl = wilayahMainCtrl;
  }

  public void setWilayahs(BindingListModelList wilayahs) {
    // STORED IN THE module's MainController
    getWilayahMainCtrl().setWilayahs(wilayahs);
  }

  /* SERVICES */
  public void setWilayahService(WilayahService wilayahService) {
    this.wilayahService = wilayahService;
  }

  /* COMPONENTS and OTHERS */

}
