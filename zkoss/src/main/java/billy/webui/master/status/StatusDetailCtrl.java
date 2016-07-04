package billy.webui.master.status;

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

import billy.backend.model.Status;
import billy.backend.service.StatusService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class StatusDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(StatusDetailCtrl.class);

  protected Window windowStatusDetail; // autowired
  protected Borderlayout borderlayout_StatusDetail; // autowired

  protected Textbox txtb_DeskripsiStatus; // autowired
  // Databinding
  protected transient AnnotateDataBinder binder;
  private StatusMainCtrl statusMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient StatusService statusService;

  /**
   * default constructor.<br>
   */
  public StatusDetailCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setStatusMainCtrl((StatusMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getStatusMainCtrl().setStatusDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getStatusMainCtrl().getSelectedStatus() != null) {
        setSelectedStatus(getStatusMainCtrl().getSelectedStatus());
      } else
        setSelectedStatus(null);
    } else {
      setSelectedStatus(null);
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
    borderlayout_StatusDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowStatusDetail.invalidate();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doReadOnlyMode(boolean b) {
    txtb_DeskripsiStatus.setReadonly(b);

  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public Status getSelectedStatus() {
    // STORED IN THE module's MainController
    return getStatusMainCtrl().getSelectedStatus();
  }

  /* Master BEANS */
  public Status getStatus() {
    // STORED IN THE module's MainController
    return getStatusMainCtrl().getSelectedStatus();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public StatusMainCtrl getStatusMainCtrl() {
    return this.statusMainCtrl;
  }

  public BindingListModelList getStatuss() {
    // STORED IN THE module's MainController
    return getStatusMainCtrl().getStatuss();
  }

  public StatusService getStatusService() {
    return this.statusService;
  }

  public void onBlur$txtb_DeskripsiStatus(Event event) throws InterruptedException {

    Status statusCheckKode = null;
    statusCheckKode =
        getStatusService().getStatusByDeskripsiStatus(getStatus().getDeskripsiStatus());

    if (statusCheckKode != null) {
      if (statusCheckKode.getId() != getStatus().getId()) {
        ZksampleMessageUtils.showErrorMessage("Status sudah ada");
        return;
      }
    }
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowStatusDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void onOK$txtb_DeskripsiStatus(Event event) throws InterruptedException {
    getStatusMainCtrl().btnSave.focus();
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setSelectedStatus(Status selectedStatus) {
    // STORED IN THE module's MainController
    getStatusMainCtrl().setSelectedStatus(selectedStatus);
  }

  public void setStatus(Status anStatus) {
    // STORED IN THE module's MainController
    getStatusMainCtrl().setSelectedStatus(anStatus);
  }

  /* CONTROLLERS */
  public void setStatusMainCtrl(StatusMainCtrl statusMainCtrl) {
    this.statusMainCtrl = statusMainCtrl;
  }

  public void setStatuss(BindingListModelList statuss) {
    // STORED IN THE module's MainController
    getStatusMainCtrl().setStatuss(statuss);
  }

  /* SERVICES */
  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  /* COMPONENTS and OTHERS */

}
