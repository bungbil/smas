package billy.webui.master.jobtype;

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

import billy.backend.model.JobType;
import billy.backend.service.JobTypeService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class JobTypeListCtrl extends GFCBaseListCtrl<JobType> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(JobTypeListCtrl.class);

  protected Window windowJobTypeList; // autowired
  protected Panel panelJobTypeList; // autowired

  protected Borderlayout borderLayout_jobTypeList; // autowired
  protected Paging paging_JobTypeList; // autowired
  protected Listbox listBoxJobType; // autowired
  protected Listheader listheader_JobTypeList_Nama; // autowired
  protected Listheader listheader_JobTypeList_LastUpdate;
  protected Listheader listheader_JobTypeList_UpdatedBy;
  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<JobType> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private JobTypeMainCtrl jobTypeMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient JobTypeService jobTypeService;

  /**
   * default constructor.<br>
   */
  public JobTypeListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setJobTypeMainCtrl((JobTypeMainCtrl) arg.get("ModuleMainController"));
      getJobTypeMainCtrl().setJobTypeListCtrl(this);

      if (getJobTypeMainCtrl().getSelectedJobType() != null) {
        setSelectedJobType(getJobTypeMainCtrl().getSelectedJobType());
      } else
        setSelectedJobType(null);
    } else {
      setSelectedJobType(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_JobTypeList.setPageSize(getCountRows());
    paging_JobTypeList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")

    listheader_JobTypeList_Nama.setSortAscending(new FieldComparator("namaJobType", true));
    listheader_JobTypeList_Nama.setSortDescending(new FieldComparator("namaJobType", false));

    listheader_JobTypeList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_JobTypeList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));

    listheader_JobTypeList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_JobTypeList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));


    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<JobType>(JobType.class, getCountRows());
    searchObj.addSort("namaJobType", false);
    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxJobType(), paging_JobTypeList);
    BindingListModelList lml = (BindingListModelList) getListBoxJobType().getModel();
    setJobTypes(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedJobType() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxJobType().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedJobType((JobType) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events.sendEvent(new Event("onSelect", getListBoxJobType(), getSelectedJobType()));
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
    borderLayout_jobTypeList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowJobTypeList.invalidate();
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

  /**
   * Best Pratice Hint:<br>
   * The setters/getters for the local annotated data binded Beans/Sets are administered in the
   * module's mainController. Working in this way you have clean line to share this beans/sets with
   * other controllers.
   */
  /* Master BEANS */
  public JobType getJobType() {
    // STORED IN THE module's MainController
    return getJobTypeMainCtrl().getSelectedJobType();
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
  // ++++++++++++++++++ getter / setter +++++++++++++++++++//
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

  public JobTypeMainCtrl getJobTypeMainCtrl() {
    return this.jobTypeMainCtrl;
  }

  public BindingListModelList getJobTypes() {
    // STORED IN THE module's MainController
    return getJobTypeMainCtrl().getJobTypes();
  }

  public JobTypeService getJobTypeService() {
    return this.jobTypeService;
  }

  public Listbox getListBoxJobType() {
    return this.listBoxJobType;
  }

  public HibernateSearchObject<JobType> getSearchObj() {
    return this.searchObj;
  }

  public JobType getSelectedJobType() {
    // STORED IN THE module's MainController
    return getJobTypeMainCtrl().getSelectedJobType();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowJobTypeList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedJobTypeItem(Event event) {
    // logger.debug(event.toString());

    JobType anJobType = getSelectedJobType();

    if (anJobType != null) {
      setSelectedJobType(anJobType);
      setJobType(anJobType);

      // check first, if the tabs are created
      if (getJobTypeMainCtrl().getJobTypeDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect", getJobTypeMainCtrl().tabJobTypeDetail, null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getJobTypeMainCtrl().getJobTypeDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect", getJobTypeMainCtrl().tabJobTypeDetail, null));
      }

      Events.sendEvent(new Event("onSelect", getJobTypeMainCtrl().tabJobTypeDetail, anJobType));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxJobType(Event event) {
    // logger.debug(event.toString());

    // selectedJobType is filled by annotated databinding mechanism
    JobType anJobType = getSelectedJobType();

    if (anJobType == null) {
      return;
    }

    // check first, if the tabs are created
    if (getJobTypeMainCtrl().getJobTypeDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", getJobTypeMainCtrl().tabJobTypeDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getJobTypeMainCtrl().getJobTypeDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", getJobTypeMainCtrl().tabJobTypeDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getJobTypeMainCtrl().getJobTypeDetailCtrl().setSelectedJobType(anJobType);
    getJobTypeMainCtrl().getJobTypeDetailCtrl().setJobType(anJobType);

    // store the selected bean values as current
    getJobTypeMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str = Labels.getLabel("common.JobType") + ": " + anJobType.getNamaJobType();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setJobType(JobType anJobType) {
    // STORED IN THE module's MainController
    getJobTypeMainCtrl().setSelectedJobType(anJobType);
  }

  /* CONTROLLERS */
  public void setJobTypeMainCtrl(JobTypeMainCtrl jobTypeMainCtrl) {
    this.jobTypeMainCtrl = jobTypeMainCtrl;
  }

  public void setJobTypes(BindingListModelList jobTypes) {
    // STORED IN THE module's MainController
    getJobTypeMainCtrl().setJobTypes(jobTypes);
  }

  /* SERVICES */
  public void setJobTypeService(JobTypeService jobTypeService) {
    this.jobTypeService = jobTypeService;
  }

  public void setListBoxJobType(Listbox listBoxJobType) {
    this.listBoxJobType = listBoxJobType;
  }

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<JobType> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedJobType(JobType selectedJobType) {
    // STORED IN THE module's MainController
    getJobTypeMainCtrl().setSelectedJobType(selectedJobType);
  }

}
