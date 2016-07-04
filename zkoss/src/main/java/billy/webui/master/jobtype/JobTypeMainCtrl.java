package billy.webui.master.jobtype;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.JobType;
import billy.backend.service.JobTypeService;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class JobTypeMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(JobTypeMainCtrl.class);

  protected Window windowJobTypeMain; // autowired

  // Tabs
  protected Tabbox tabbox_JobTypeMain; // autowired
  protected Tab tabJobTypeList; // autowired
  protected Tab tabJobTypeDetail; // autowired
  protected Tabpanel tabPanelJobTypeList; // autowired
  protected Tabpanel tabPanelJobTypeDetail; // autowired

  // filter components
  protected Checkbox checkbox_JobTypeList_ShowAll; // autowired
  protected Textbox txtb_JobType_Name; // aurowired
  protected Button button_JobTypeList_SearchName; // aurowired

  // Button controller for the CRUD buttons
  private final String btnCtroller_ClassPrefix = "button_JobTypeMain_";
  private ButtonStatusCtrl btnCtrlJobType;
  protected Button btnNew; // autowired
  protected Button btnEdit; // autowired
  protected Button btnDelete; // autowired
  protected Button btnSave; // autowired
  protected Button btnCancel; // autowired

  protected Button btnFirst; // autowire
  protected Button btnPrevious; // autowire
  protected Button btnNext; // autowire
  protected Button btnLast; // autowire

  protected Button btnPrint; // autowire

  protected Button btnHelp;

  // Tab-Controllers for getting the binders
  private JobTypeListCtrl jobTypeListCtrl;
  private JobTypeDetailCtrl jobTypeDetailCtrl;

  // Databinding
  private JobType selectedJobType;
  private BindingListModelList jobTypes;

  // ServiceDAOs / Domain Classes
  private JobTypeService jobTypeService;

  // always a copy from the bean before modifying. Used for reseting
  private JobType originalJobType;

  /**
   * default constructor.<br>
   */
  public JobTypeMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    /**
     * 1. Set an 'alias' for this composer name to access it in the zul-file.<br>
     * 2. Set the jobType 'recurse' to 'false' to avoid problems with managing more than one
     * zul-file in one page. Otherwise it would be overridden and can ends in curious error
     * messages.
     */
    this.self.setAttribute("controller", this, false);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * 1. Cancel the current action.<br>
   * 2. Reset the values to its origin.<br>
   * 3. Set UI components back to readonly/disable mode.<br>
   * 4. Set the buttons in edit mode.<br>
   * 
   * @param event
   * @throws InterruptedException
   */
  private void doCancel(Event event) throws InterruptedException {
    // logger.debug(event.toString());
    // reset to the original object
    doResetToInitValues();

    // check first, if the tabs are created
    if (getJobTypeDetailCtrl().getBinder() != null) {

      // refresh all dataBinder related controllers/components
      getJobTypeDetailCtrl().getBinder().loadAll();

      // set editable Mode
      getJobTypeDetailCtrl().doReadOnlyMode(true);

      btnCtrlJobType.setInitEdit();
    }
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
    button_JobTypeList_SearchName.setVisible(workspace.isAllowed("button_JobTypeList_SearchName"));
    tabJobTypeList.setVisible(workspace.isAllowed("windowJobTypeList"));
    tabJobTypeDetail.setVisible(workspace.isAllowed("windowJobTypeDetail"));
    btnEdit.setVisible(workspace.isAllowed("button_JobTypeMain_btnEdit"));
    btnNew.setVisible(workspace.isAllowed("button_JobTypeMain_btnNew"));
    btnDelete.setVisible(workspace.isAllowed("button_JobTypeMain_btnDelete"));
    btnSave.setVisible(workspace.isAllowed("button_JobTypeMain_btnSave"));
    btnCancel.setVisible(workspace.isAllowed("button_JobTypeMain_btnCancel"));
    btnFirst.setVisible(workspace.isAllowed("button_JobTypeMain_btnFirst"));
    btnPrevious.setVisible(workspace.isAllowed("button_JobTypeMain_btnPrevious"));
    btnNext.setVisible(workspace.isAllowed("button_JobTypeMain_btnNext"));
    btnLast.setVisible(workspace.isAllowed("button_JobTypeMain_btnLast"));
  }

  /**
   * Deletes the selected Bean from the DB.
   * 
   * @param event
   * @throws InterruptedException
   * @throws InterruptedException
   */
  private void doDelete(Event event) throws InterruptedException {
    // logger.debug(event.toString());
    // check first, if the tabs are created, if not than create them
    if (getJobTypeDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabJobTypeDetail, null));
    }

    // check first, if the tabs are created
    if (getJobTypeDetailCtrl().getBinder() == null) {
      return;
    }

    final JobType anJobType = getSelectedJobType();
    if (anJobType != null) {

      // Show a confirm box
      final String msg =
          Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
              + anJobType.getNamaJobType();
      final String title = Labels.getLabel("message.Deleting.Record");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            private void deleteBean() throws InterruptedException {
              try {
                getJobTypeService().delete(anJobType);
              } catch (DataAccessException e) {
                ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
              }
            }

            @Override
            public void onEvent(Event evt) {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    deleteBean();
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  break; //
                case MultiLineMessageBox.NO:
                  break; //
              }
            }
          }

      ) == MultiLineMessageBox.YES) {
      }

    }

    btnCtrlJobType.setInitEdit();

    setSelectedJobType(null);
    // refresh the list
    getJobTypeListCtrl().doFillListbox();

    // refresh all dataBinder related controllers
    getJobTypeDetailCtrl().getBinder().loadAll();
  }


  /**
   * Sets all UI-components to writable-mode. Sets the buttons to edit-Mode. Checks first, if the
   * NEEDED TABS with its contents are created. If not, than create it by simulate an onSelect()
   * with calling Events.sendEvent()
   * 
   * @param event
   * @throws InterruptedException
   */
  private void doEdit(Event event) {
    // logger.debug(event.toString());
    // get the current Tab for later checking if we must change it
    Tab currentTab = tabbox_JobTypeMain.getSelectedTab();

    // check first, if the tabs are created, if not than create it
    if (getJobTypeDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabJobTypeDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getJobTypeDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabJobTypeDetail, null));
    }

    // check if the tab is one of the Detail tabs. If so do not change the
    // selection of it
    if (!currentTab.equals(tabJobTypeDetail)) {
      tabJobTypeDetail.setSelected(true);
    } else {
      currentTab.setSelected(true);
    }

    // remember the old vars
    doStoreInitValues();

    btnCtrlJobType.setBtnStatus_Edit();

    getJobTypeDetailCtrl().doReadOnlyMode(false);

    // refresh the UI, because we can click the EditBtn from every tab.
    getJobTypeDetailCtrl().getBinder().loadAll();

    // set focus
    getJobTypeDetailCtrl().txtb_NamaJobType.focus();
  }


  /**
   * Opens the help screen for the current module.
   * 
   * @param event
   * @throws InterruptedException
   */
  private void doHelp(Event event) throws InterruptedException {

    ZksampleMessageUtils.doShowNotImplementedMessage();
    event.stopPropagation();
  }


  /**
   * Sets all UI-components to writable-mode. Stores the current Beans as originBeans and get new
   * Objects from the backend.
   * 
   * @param event
   * @throws InterruptedException
   */
  private void doNew(Event event) {
    // logger.debug(event.toString());
    // check first, if the tabs are created
    if (getJobTypeDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabJobTypeDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getJobTypeDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabJobTypeDetail, null));
    }

    // remember the current object
    doStoreInitValues();

    /** !!! DO NOT BREAK THE TIERS !!! */
    // We don't create a new DomainObject() in the frontend.
    // We GET it from the backend.
    final JobType anJobType = getJobTypeService().getNewJobType();

    // set the beans in the related databinded controllers
    getJobTypeDetailCtrl().setJobType(anJobType);
    getJobTypeDetailCtrl().setSelectedJobType(anJobType);

    // Refresh the binding mechanism
    getJobTypeDetailCtrl().setSelectedJobType(getSelectedJobType());
    try {
      getJobTypeDetailCtrl().getBinder().loadAll();
    } catch (Exception e) {
      // do nothing
    }

    // set editable Mode
    getJobTypeDetailCtrl().doReadOnlyMode(false);

    // set the ButtonStatus to New-Mode
    btnCtrlJobType.setInitNew();

    tabJobTypeDetail.setSelected(true);
    // set focus
    getJobTypeDetailCtrl().txtb_NamaJobType.focus();

  }

  /**
   * Reset the selected object to its origin property values.
   * 
   * @see doStoreInitValues()
   */
  public void doResetToInitValues() {

    if (getOriginalJobType() != null) {

      try {
        setSelectedJobType((JobType) ZksampleBeanUtils.cloneBean(getOriginalJobType()));
        // TODO Bug in DataBinder??
        windowJobTypeMain.invalidate();

      } catch (final IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (final InstantiationException e) {
        throw new RuntimeException(e);
      } catch (final InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (final NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Resizes the container from the selected Tab.
   * 
   * @param event
   */
  private void doResizeSelectedTab(Event event) {
    // logger.debug(event.toString());

    if (tabbox_JobTypeMain.getSelectedTab() == tabJobTypeDetail) {
      getJobTypeDetailCtrl().doFitSize(event);
    } else if (tabbox_JobTypeMain.getSelectedTab() == tabJobTypeList) {
      // resize and fill Listbox new
      getJobTypeListCtrl().doFillListbox();
    }
  }

  /**
   * Saves all involved Beans to the DB.
   * 
   * @param event
   * @throws InterruptedException
   */
  private void doSave(Event event) throws InterruptedException {
    // logger.debug(event.toString());
    // save all components data in the several tabs to the bean
    getJobTypeDetailCtrl().getBinder().saveAll();

    try {

      String userName =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getUsername();
      getJobTypeDetailCtrl().getJobType().setLastUpdate(new Date());
      getJobTypeDetailCtrl().getJobType().setUpdatedBy(userName);
      // save it to database
      getJobTypeService().saveOrUpdate(getJobTypeDetailCtrl().getJobType());
      // if saving is successfully than actualize the beans as
      // origins.
      doStoreInitValues();
      // refresh the list
      getJobTypeListCtrl().doFillListbox();
      // later refresh StatusBar
      Events.postEvent("onSelect", getJobTypeListCtrl().getListBoxJobType(), getSelectedJobType());

      // show the objects data in the statusBar
      String str = getSelectedJobType().getNamaJobType();
      EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
          new Event("onChangeSelectedObject", null, str));

    } catch (DataAccessException e) {
      ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

      // Reset to init values
      doResetToInitValues();

      return;

    } finally {
      btnCtrlJobType.setInitEdit();
      getJobTypeDetailCtrl().doReadOnlyMode(true);
    }
  }

  /**
   * Skip/Leaf through the models data according the navigation buttons and selected the according
   * row in the listbox.
   * 
   * @param event
   */
  private void doSkip(Event event) {

    // get the model and the current selected record
    BindingListModelList blml =
        (BindingListModelList) getJobTypeListCtrl().getListBoxJobType().getModel();

    // check if data exists
    if (blml == null || blml.size() < 1)
      return;

    int index = blml.indexOf(getSelectedJobType());

    /**
     * Check, if all tabs with data binded components are created So we work with spring
     * BeanCreation we must check a little bit deeper, because the Controller are preCreated ? After
     * that, go back to the current/selected tab.
     */
    Tab currentTab = tabbox_JobTypeMain.getSelectedTab();

    if (getJobTypeDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event(Events.ON_SELECT, tabJobTypeDetail, null));
    }

    // go back to selected tab
    currentTab.setSelected(true);

    // Check which button is clicked and calculate the rowIndex
    if (((ForwardEvent) event).getOrigin().getTarget() == btnNext) {
      if (index < (blml.size() - 1)) {
        index = index + 1;
      }
    } else if (((ForwardEvent) event).getOrigin().getTarget() == btnPrevious) {
      if (index > 0) {
        index = index - 1;
      }
    } else if (((ForwardEvent) event).getOrigin().getTarget() == btnFirst) {
      if (index != 0) {
        index = 0;
      }
    } else if (((ForwardEvent) event).getOrigin().getTarget() == btnLast) {
      if (index != blml.size()) {
        index = (blml.size() - 1);
      }
    }

    getJobTypeListCtrl().getListBoxJobType().setSelectedIndex(index);
    setSelectedJobType((JobType) blml.get(index));

    // call onSelect() for showing the objects data in the statusBar
    Events.sendEvent(new Event(Events.ON_SELECT, getJobTypeListCtrl().getListBoxJobType(),
        getSelectedJobType()));

    // refresh master-detail MASTERS data
    getJobTypeDetailCtrl().getBinder().loadAll();

    // EXTRA: if we have a longtext field under the listbox, so we must
    // refresh
    // this binded component too
    // getArticleListCtrl().getBinder().loadComponent(getArticleListCtrl().longBoxArt_LangBeschreibung);
  }

  /**
   * Saves the selected object's current properties. We can get them back if a modification is
   * canceled.
   * 
   * @see doResetToInitValues()
   */
  public void doStoreInitValues() {

    if (getSelectedJobType() != null) {

      try {
        setOriginalJobType((JobType) ZksampleBeanUtils.cloneBean(getSelectedJobType()));
      } catch (final IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (final InstantiationException e) {
        throw new RuntimeException(e);
      } catch (final InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (final NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public JobTypeDetailCtrl getJobTypeDetailCtrl() {
    return this.jobTypeDetailCtrl;
  }

  public JobTypeListCtrl getJobTypeListCtrl() {
    return this.jobTypeListCtrl;
  }

  public BindingListModelList getJobTypes() {
    return this.jobTypes;
  }

  /* SERVICES */
  public JobTypeService getJobTypeService() {
    return this.jobTypeService;
  }

  public JobType getOriginalJobType() {
    return this.originalJobType;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public JobType getSelectedJobType() {
    return this.selectedJobType;
  }

  /**
   * when the checkBox 'Show All' for filtering is checked. <br>
   * 
   * @param event
   */
  public void onCheck$checkbox_JobTypeList_ShowAll(Event event) {
    // logger.debug(event.toString());

    // empty the text search boxes
    txtb_JobType_Name.setValue(""); // clear

    // ++ create the searchObject and init sorting ++//
    HibernateSearchObject<JobType> soJobType =
        new HibernateSearchObject<JobType>(JobType.class, getJobTypeListCtrl().getCountRows());
    soJobType.addSort("namaJobType", false);

    // Change the BindingListModel.
    if (getJobTypeListCtrl().getBinder() != null) {
      getJobTypeListCtrl().getPagedBindingListWrapper().setSearchObject(soJobType);

      // get the current Tab for later checking if we must change it
      Tab currentTab = tabbox_JobTypeMain.getSelectedTab();

      // check if the tab is one of the Detail tabs. If so do not
      // change the selection of it
      if (!currentTab.equals(tabJobTypeList)) {
        tabJobTypeList.setSelected(true);
      } else {
        currentTab.setSelected(true);
      }
    }

  }

  /**
   * When the "cancel" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnCancel(Event event) throws InterruptedException {
    doCancel(event);
  }

  /**
   * When the "delete" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnDelete(Event event) throws InterruptedException {
    doDelete(event);
  }

  /**
   * When the "cancel" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnEdit(Event event) throws InterruptedException {
    doEdit(event);
  }

  /**
   * when the "go first record" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnFirst(Event event) throws InterruptedException {
    doSkip(event);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * When the "help" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnHelp(Event event) throws InterruptedException {
    doHelp(event);
  }

  /**
   * when the "go last record" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnLast(Event event) throws InterruptedException {
    doSkip(event);
  }

  /**
   * When the "new" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnNew(Event event) throws InterruptedException {
    doNew(event);
  }

  /**
   * when the "go next record" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnNext(Event event) throws InterruptedException {
    doSkip(event);
  }

  /**
   * when the "go previous record" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnPrevious(Event event) throws InterruptedException {
    doSkip(event);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * when the "refresh" button is clicked. <br>
   * <br>
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnRefresh(Event event) throws InterruptedException {
    doResizeSelectedTab(event);
  }

  /**
   * When the "save" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnSave(Event event) throws InterruptedException {
    doSave(event);
  }

  /**
   * Filter the jobType list with 'like jobType name'. <br>
   */
  public void onClick$button_JobTypeList_SearchName(Event event) throws Exception {
    // logger.debug(event.toString());

    // if not empty
    if (!txtb_JobType_Name.getValue().isEmpty()) {
      checkbox_JobTypeList_ShowAll.setChecked(false); // unCheck

      // ++ create the searchObject and init sorting ++//
      HibernateSearchObject<JobType> soJobType =
          new HibernateSearchObject<JobType>(JobType.class, getJobTypeListCtrl().getCountRows());
      soJobType.addFilter(new Filter("namaJobType", "%" + txtb_JobType_Name.getValue() + "%",
          Filter.OP_ILIKE));
      soJobType.addSort("namaJobType", false);

      // Change the BindingListModel.
      if (getJobTypeListCtrl().getBinder() != null) {
        getJobTypeListCtrl().getPagedBindingListWrapper().setSearchObject(soJobType);

        // get the current Tab for later checking if we must change it
        Tab currentTab = tabbox_JobTypeMain.getSelectedTab();

        // check if the tab is one of the Detail tabs. If so do not
        // change the selection of it
        if (!currentTab.equals(tabJobTypeList)) {
          tabJobTypeList.setSelected(true);
        } else {
          currentTab.setSelected(true);
        }
      }
    }
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowJobTypeMain(Event event) throws Exception {
    windowJobTypeMain.setContentStyle("padding:0px;");

    // create the Button Controller. Disable not used buttons during working
    btnCtrlJobType =
        new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint,
            btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
            btnCancel, null);

    doCheckRights();

    /**
     * Initiate the first loading by selecting the customerList tab and create the components from
     * the zul-file.
     */
    tabJobTypeList.setSelected(true);

    if (tabPanelJobTypeList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelJobTypeList, this,
          "ModuleMainController", "/WEB-INF/pages/master/jobtype/jobTypeList.zul");
    }

    // init the buttons for editMode
    btnCtrlJobType.setInitEdit();
  }

  /**
   * When the tab 'tabPanelJobTypeDetail' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabJobTypeDetail(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelJobTypeDetail.getFirstChild() != null) {
      tabJobTypeDetail.setSelected(true);

      // refresh the Binding mechanism
      getJobTypeDetailCtrl().setJobType(getSelectedJobType());
      getJobTypeDetailCtrl().getBinder().loadAll();

      return;
    }

    if (tabPanelJobTypeDetail != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelJobTypeDetail, this,
          "ModuleMainController", "/WEB-INF/pages/master/jobtype/jobTypeDetail.zul");
    }
  }

  /**
   * When the tab 'tabJobTypeList' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabJobTypeList(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelJobTypeList.getFirstChild() != null) {
      tabJobTypeList.setSelected(true);

      return;
    }

    if (tabPanelJobTypeList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelJobTypeList, this,
          "ModuleMainController", "/WEB-INF/pages//jobtype/jobTypeList.zul");
    }

  }

  public void setJobTypeDetailCtrl(JobTypeDetailCtrl jobTypeDetailCtrl) {
    this.jobTypeDetailCtrl = jobTypeDetailCtrl;
  }

  /* CONTROLLERS */
  public void setJobTypeListCtrl(JobTypeListCtrl jobTypeListCtrl) {
    this.jobTypeListCtrl = jobTypeListCtrl;
  }

  public void setJobTypes(BindingListModelList jobTypes) {
    this.jobTypes = jobTypes;
  }

  public void setJobTypeService(JobTypeService jobTypeService) {
    this.jobTypeService = jobTypeService;
  }

  /* Master BEANS */
  public void setOriginalJobType(JobType originalJobType) {
    this.originalJobType = originalJobType;
  }

  public void setSelectedJobType(JobType selectedJobType) {
    this.selectedJobType = selectedJobType;
  }

  /* COMPONENTS and OTHERS */
}
