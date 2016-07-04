package billy.webui.utility.parameter;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import billy.backend.model.Parameter;
import billy.backend.service.ParameterService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class ParameterListCtrl extends GFCBaseListCtrl<Parameter> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(ParameterListCtrl.class);

  protected Window windowParameterList; // autowired
  protected Panel panelParameterList; // autowired

  protected Borderlayout borderLayout_parameterList; // autowired
  protected Paging paging_ParameterList; // autowired
  protected Listbox listBoxParameter; // autowired
  protected Listheader listheader_ParameterList_Name; // autowired
  protected Listheader listheader_ParameterList_Value; // autowired
  protected Listheader listheader_ParameterList_Description; // autowired
  protected Listheader listheader_ParameterList_LastUpdate;
  protected Listheader listheader_ParameterList_UpdatedBy;

  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<Parameter> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private ParameterMainCtrl parameterMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient ParameterService parameterService;

  /**
   * default constructor.<br>
   */
  public ParameterListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setParameterMainCtrl((ParameterMainCtrl) arg.get("ModuleMainController"));
      getParameterMainCtrl().setParameterListCtrl(this);

      if (getParameterMainCtrl().getSelectedParameter() != null) {
        setSelectedParameter(getParameterMainCtrl().getSelectedParameter());
      } else
        setSelectedParameter(null);
    } else {
      setSelectedParameter(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_ParameterList.setPageSize(getCountRows());
    paging_ParameterList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")
    listheader_ParameterList_Name.setSortAscending(new FieldComparator("paramName", true));
    listheader_ParameterList_Name.setSortDescending(new FieldComparator("paramName", false));

    listheader_ParameterList_Description.setSortAscending(new FieldComparator("description", true));
    listheader_ParameterList_Description
        .setSortDescending(new FieldComparator("description", false));

    listheader_ParameterList_Value.setSortAscending(new FieldComparator("paramValue", true));
    listheader_ParameterList_Value.setSortDescending(new FieldComparator("paramValue", false));

    listheader_ParameterList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_ParameterList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));

    listheader_ParameterList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_ParameterList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));

    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<Parameter>(Parameter.class, getCountRows());
    searchObj.addSort("paramName", false);
    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxParameter(), paging_ParameterList);
    BindingListModelList lml = (BindingListModelList) getListBoxParameter().getModel();
    setParameters(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedParameter() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxParameter().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedParameter((Parameter) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events.sendEvent(new Event("onSelect", getListBoxParameter(), getSelectedParameter()));
      }
    }

  }

  /**
   * Recalculates the container size for this controller and resize them. Calculate how many rows
   * have been place in the listbox. Get the currentDesktopHeight from a hidden Intbox from the
   * index.zul that are filled by onClientInfo() in the indexCtroller.
   */
  public void doFitSize() {

    // normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
    final int specialSize = 5;

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - specialSize - 148;
    setCountRows((int) Math.round(maxListBoxHeight / 17.7));
    borderLayout_parameterList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowParameterList.invalidate();
  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public int getCountRows() {
    return this.countRows;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public Listbox getListBoxParameter() {
    return this.listBoxParameter;
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
  // ++++++++++++++++++ getter / setter +++++++++++++++++++//
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

  /**
   * Best Pratice Hint:<br>
   * The setters/getters for the local annotated data binded Beans/Sets are administered in the
   * module's mainController. Working in this way you have clean line to share this beans/sets with
   * other controllers.
   */
  /* Master BEANS */
  public Parameter getParameter() {
    // STORED IN THE module's MainController
    return getParameterMainCtrl().getSelectedParameter();
  }

  public ParameterMainCtrl getParameterMainCtrl() {
    return this.parameterMainCtrl;
  }

  public BindingListModelList getParameters() {
    // STORED IN THE module's MainController
    return getParameterMainCtrl().getParameters();
  }

  public ParameterService getParameterService() {
    return this.parameterService;
  }

  public HibernateSearchObject<Parameter> getSearchObj() {
    return this.searchObj;
  }

  public Parameter getSelectedParameter() {
    // STORED IN THE module's MainController
    return getParameterMainCtrl().getSelectedParameter();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowParameterList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedParameterItem(Event event) {
    // logger.debug(event.toString());

    Parameter anParameter = getSelectedParameter();

    if (anParameter != null) {
      setSelectedParameter(anParameter);
      setParameter(anParameter);

      // check first, if the tabs are created
      if (getParameterMainCtrl().getParameterDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect", getParameterMainCtrl().tabParameterDetail, null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getParameterMainCtrl().getParameterDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect", getParameterMainCtrl().tabParameterDetail, null));
      }

      Events
          .sendEvent(new Event("onSelect", getParameterMainCtrl().tabParameterDetail, anParameter));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxParameter(Event event) {
    // logger.debug(event.toString());

    // selectedParameter is filled by annotated databinding mechanism
    Parameter anParameter = getSelectedParameter();

    if (anParameter == null) {
      return;
    }

    // check first, if the tabs are created
    if (getParameterMainCtrl().getParameterDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", getParameterMainCtrl().tabParameterDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getParameterMainCtrl().getParameterDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", getParameterMainCtrl().tabParameterDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getParameterMainCtrl().getParameterDetailCtrl().setSelectedParameter(anParameter);
    getParameterMainCtrl().getParameterDetailCtrl().setParameter(anParameter);

    // store the selected bean values as current
    getParameterMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str = Labels.getLabel("common.Parameter") + ": " + anParameter.getParamName();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setListBoxParameter(Listbox listBoxParameter) {
    this.listBoxParameter = listBoxParameter;
  }

  public void setParameter(Parameter anParameter) {
    // STORED IN THE module's MainController
    getParameterMainCtrl().setSelectedParameter(anParameter);
  }

  /* CONTROLLERS */
  public void setParameterMainCtrl(ParameterMainCtrl parameterMainCtrl) {
    this.parameterMainCtrl = parameterMainCtrl;
  }

  public void setParameters(BindingListModelList parameters) {
    // STORED IN THE module's MainController
    getParameterMainCtrl().setParameters(parameters);
  }

  /* SERVICES */
  public void setParameterService(ParameterService parameterService) {
    this.parameterService = parameterService;
  }

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<Parameter> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedParameter(Parameter selectedParameter) {
    // STORED IN THE module's MainController
    getParameterMainCtrl().setSelectedParameter(selectedParameter);
  }

}
