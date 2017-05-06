package billy.webui.master.mandiri;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Mandiri;
import billy.backend.service.MandiriService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class MandiriDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(MandiriDetailCtrl.class);

  protected Window windowMandiriDetail; // autowired

  protected Borderlayout borderlayout_MandiriDetail; // autowired

  protected Textbox txtb_KodeMandiri; // autowired
  protected Textbox txtb_DeskripsiMandiri; // autowired
  // protected Label txtb_Status; // autowired

  // Databinding
  protected transient AnnotateDataBinder binder;
  private MandiriMainCtrl mandiriMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient MandiriService mandiriService;

  /**
   * default constructor.<br>
   */
  public MandiriDetailCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setMandiriMainCtrl((MandiriMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getMandiriMainCtrl().setMandiriDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getMandiriMainCtrl().getSelectedMandiri() != null) {
        setSelectedMandiri(getMandiriMainCtrl().getSelectedMandiri());
      } else
        setSelectedMandiri(null);
    } else {
      setSelectedMandiri(null);
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
    borderlayout_MandiriDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowMandiriDetail.invalidate();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doReadOnlyMode(boolean b) {
    txtb_KodeMandiri.setReadonly(b);
    txtb_DeskripsiMandiri.setReadonly(b);

  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */
  public Mandiri getMandiri() {
    // STORED IN THE module's MainController
    return getMandiriMainCtrl().getSelectedMandiri();
  }

  public MandiriMainCtrl getMandiriMainCtrl() {
    return this.mandiriMainCtrl;
  }

  public BindingListModelList getMandiris() {
    // STORED IN THE module's MainController
    return getMandiriMainCtrl().getMandiris();
  }

  public MandiriService getMandiriService() {
    return this.mandiriService;
  }

  public Mandiri getSelectedMandiri() {
    // STORED IN THE module's MainController
    return getMandiriMainCtrl().getSelectedMandiri();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowMandiriDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setMandiri(Mandiri anMandiri) {
    // STORED IN THE module's MainController
    getMandiriMainCtrl().setSelectedMandiri(anMandiri);
  }

  /* CONTROLLERS */
  public void setMandiriMainCtrl(MandiriMainCtrl mandiriMainCtrl) {
    this.mandiriMainCtrl = mandiriMainCtrl;
  }

  public void setMandiris(BindingListModelList mandiris) {
    // STORED IN THE module's MainController
    getMandiriMainCtrl().setMandiris(mandiris);
  }

  /* SERVICES */
  public void setMandiriService(MandiriService mandiriService) {
    this.mandiriService = mandiriService;
  }

  public void setSelectedMandiri(Mandiri selectedMandiri) {
    // STORED IN THE module's MainController
    getMandiriMainCtrl().setSelectedMandiri(selectedMandiri);
  }

  /* COMPONENTS and OTHERS */

}
