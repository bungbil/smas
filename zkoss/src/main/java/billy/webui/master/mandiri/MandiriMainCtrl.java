package billy.webui.master.mandiri;

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

import billy.backend.model.Mandiri;
import billy.backend.service.MandiriService;

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

public class MandiriMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(MandiriMainCtrl.class);

  protected Window windowMandiriMain; // autowired

  // Tabs
  protected Tabbox tabbox_MandiriMain; // autowired
  protected Tab tabMandiriList; // autowired
  protected Tab tabMandiriDetail; // autowired
  protected Tabpanel tabPanelMandiriList; // autowired
  protected Tabpanel tabPanelMandiriDetail; // autowired

  // filter components
  protected Checkbox checkbox_MandiriList_ShowAll; // autowired
  protected Textbox txtb_Mandiri_Name; // aurowired
  protected Button button_MandiriList_SearchName; // aurowired

  // Button controller for the CRUD buttons
  private final String btnCtroller_ClassPrefix = "button_MandiriMain_";
  private ButtonStatusCtrl btnCtrlMandiri;
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
  private MandiriListCtrl mandiriListCtrl;
  private MandiriDetailCtrl mandiriDetailCtrl;

  // Databinding
  private Mandiri selectedMandiri;
  private BindingListModelList mandiris;

  // ServiceDAOs / Domain Classes
  private MandiriService mandiriService;

  // always a copy from the bean before modifying. Used for reseting
  private Mandiri originalMandiri;

  /**
   * default constructor.<br>
   */
  public MandiriMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    /**
     * 1. Set an 'alias' for this composer name to access it in the zul-file.<br>
     * 2. Set the mandiri 'recurse' to 'false' to avoid problems with managing more than one
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
    if (getMandiriDetailCtrl().getBinder() != null) {

      // refresh all dataBinder related controllers/components
      getMandiriDetailCtrl().getBinder().loadAll();

      // set editable Mode
      getMandiriDetailCtrl().doReadOnlyMode(true);

      btnCtrlMandiri.setInitEdit();
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
    button_MandiriList_SearchName.setVisible(workspace.isAllowed("button_MandiriList_SearchName"));
    tabMandiriList.setVisible(workspace.isAllowed("windowMandiriList"));
    tabMandiriDetail.setVisible(workspace.isAllowed("windowMandiriDetail"));
    btnEdit.setVisible(workspace.isAllowed("button_MandiriMain_btnEdit"));
    btnNew.setVisible(workspace.isAllowed("button_MandiriMain_btnNew"));
    btnDelete.setVisible(workspace.isAllowed("button_MandiriMain_btnDelete"));
    btnSave.setVisible(workspace.isAllowed("button_MandiriMain_btnSave"));
    btnCancel.setVisible(workspace.isAllowed("button_MandiriMain_btnCancel"));
    btnFirst.setVisible(workspace.isAllowed("button_MandiriMain_btnFirst"));
    btnPrevious.setVisible(workspace.isAllowed("button_MandiriMain_btnPrevious"));
    btnNext.setVisible(workspace.isAllowed("button_MandiriMain_btnNext"));
    btnLast.setVisible(workspace.isAllowed("button_MandiriMain_btnLast"));
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
    if (getMandiriDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabMandiriDetail, null));
    }

    // check first, if the tabs are created
    if (getMandiriDetailCtrl().getBinder() == null) {
      return;
    }

    final Mandiri anMandiri = getSelectedMandiri();
    if (anMandiri != null) {

      // Show a confirm box
      final String msg =
          Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
              + anMandiri.getKodeMandiri();
      final String title = Labels.getLabel("message.Deleting.Record");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            private void deleteBean() throws InterruptedException {
              try {
                getMandiriService().delete(anMandiri);
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

    btnCtrlMandiri.setInitEdit();

    setSelectedMandiri(null);
    // refresh the list
    getMandiriListCtrl().doFillListbox();

    // refresh all dataBinder related controllers
    getMandiriDetailCtrl().getBinder().loadAll();
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
    Tab currentTab = tabbox_MandiriMain.getSelectedTab();

    // check first, if the tabs are created, if not than create it
    if (getMandiriDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabMandiriDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getMandiriDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabMandiriDetail, null));
    }

    // check if the tab is one of the Detail tabs. If so do not change the
    // selection of it
    if (!currentTab.equals(tabMandiriDetail)) {
      tabMandiriDetail.setSelected(true);
    } else {
      currentTab.setSelected(true);
    }

    // remember the old vars
    doStoreInitValues();

    btnCtrlMandiri.setBtnStatus_Edit();

    getMandiriDetailCtrl().doReadOnlyMode(false);

    // refresh the UI, because we can click the EditBtn from every tab.
    getMandiriDetailCtrl().getBinder().loadAll();

    // set focus
    getMandiriDetailCtrl().txtb_KodeMandiri.focus();
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
    if (getMandiriDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabMandiriDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getMandiriDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabMandiriDetail, null));
    }

    // remember the current object
    doStoreInitValues();

    /** !!! DO NOT BREAK THE TIERS !!! */
    // We don't create a new DomainObject() in the frontend.
    // We GET it from the backend.
    final Mandiri anMandiri = getMandiriService().getNewMandiri();

    // set the beans in the related databinded controllers
    getMandiriDetailCtrl().setMandiri(anMandiri);
    getMandiriDetailCtrl().setSelectedMandiri(anMandiri);

    // Refresh the binding mechanism
    getMandiriDetailCtrl().setSelectedMandiri(getSelectedMandiri());
    try {
      getMandiriDetailCtrl().getBinder().loadAll();
    } catch (Exception e) {
      // do nothing
    }

    // set editable Mode
    getMandiriDetailCtrl().doReadOnlyMode(false);

    // set the ButtonStatus to New-Mode
    btnCtrlMandiri.setInitNew();

    tabMandiriDetail.setSelected(true);
    // set focus
    getMandiriDetailCtrl().txtb_KodeMandiri.focus();

  }

  /**
   * Reset the selected object to its origin property values.
   * 
   * @see doStoreInitValues()
   */
  public void doResetToInitValues() {

    if (getOriginalMandiri() != null) {

      try {
        setSelectedMandiri((Mandiri) ZksampleBeanUtils.cloneBean(getOriginalMandiri()));
        // TODO Bug in DataBinder??
        windowMandiriMain.invalidate();

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

    if (tabbox_MandiriMain.getSelectedTab() == tabMandiriDetail) {
      getMandiriDetailCtrl().doFitSize(event);
    } else if (tabbox_MandiriMain.getSelectedTab() == tabMandiriList) {
      // resize and fill Listbox new
      getMandiriListCtrl().doFillListbox();
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
    getMandiriDetailCtrl().getBinder().saveAll();

    try {

      String userName =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getUsername();
      getMandiriDetailCtrl().getMandiri().setLastUpdate(new Date());
      getMandiriDetailCtrl().getMandiri().setUpdatedBy(userName);
      // save it to database
      getMandiriService().saveOrUpdate(getMandiriDetailCtrl().getMandiri());
      // if saving is successfully than actualize the beans as
      // origins.
      doStoreInitValues();
      // refresh the list
      getMandiriListCtrl().doFillListbox();
      // later refresh StatusBar
      Events.postEvent("onSelect", getMandiriListCtrl().getListBoxMandiri(), getSelectedMandiri());

      // show the objects data in the statusBar
      String str = getSelectedMandiri().getKodeMandiri();
      EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
          new Event("onChangeSelectedObject", null, str));

    } catch (DataAccessException e) {
      ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

      // Reset to init values
      doResetToInitValues();

      return;

    } finally {
      btnCtrlMandiri.setInitEdit();
      getMandiriDetailCtrl().doReadOnlyMode(true);
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
        (BindingListModelList) getMandiriListCtrl().getListBoxMandiri().getModel();

    // check if data exists
    if (blml == null || blml.size() < 1)
      return;

    int index = blml.indexOf(getSelectedMandiri());

    /**
     * Check, if all tabs with data binded components are created So we work with spring
     * BeanCreation we must check a little bit deeper, because the Controller are preCreated ? After
     * that, go back to the current/selected tab.
     */
    Tab currentTab = tabbox_MandiriMain.getSelectedTab();

    if (getMandiriDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event(Events.ON_SELECT, tabMandiriDetail, null));
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

    getMandiriListCtrl().getListBoxMandiri().setSelectedIndex(index);
    setSelectedMandiri((Mandiri) blml.get(index));

    // call onSelect() for showing the objects data in the statusBar
    Events.sendEvent(new Event(Events.ON_SELECT, getMandiriListCtrl().getListBoxMandiri(),
        getSelectedMandiri()));

    // refresh master-detail MASTERS data
    getMandiriDetailCtrl().getBinder().loadAll();

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

    if (getSelectedMandiri() != null) {

      try {
        setOriginalMandiri((Mandiri) ZksampleBeanUtils.cloneBean(getSelectedMandiri()));
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

  public MandiriDetailCtrl getMandiriDetailCtrl() {
    return this.mandiriDetailCtrl;
  }

  public MandiriListCtrl getMandiriListCtrl() {
    return this.mandiriListCtrl;
  }

  public BindingListModelList getMandiris() {
    return this.mandiris;
  }

  /* SERVICES */
  public MandiriService getMandiriService() {
    return this.mandiriService;
  }

  public Mandiri getOriginalMandiri() {
    return this.originalMandiri;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public Mandiri getSelectedMandiri() {
    return this.selectedMandiri;
  }

  /**
   * when the checkBox 'Show All' for filtering is checked. <br>
   * 
   * @param event
   */
  public void onCheck$checkbox_MandiriList_ShowAll(Event event) {
    // logger.debug(event.toString());

    // empty the text search boxes
    txtb_Mandiri_Name.setValue(""); // clear

    // ++ create the searchObject and init sorting ++//
    HibernateSearchObject<Mandiri> soMandiri =
        new HibernateSearchObject<Mandiri>(Mandiri.class, getMandiriListCtrl().getCountRows());
    soMandiri.addSort("deskripsiMandiri", false);

    // Change the BindingListModel.
    if (getMandiriListCtrl().getBinder() != null) {
      getMandiriListCtrl().getPagedBindingListWrapper().setSearchObject(soMandiri);

      // get the current Tab for later checking if we must change it
      Tab currentTab = tabbox_MandiriMain.getSelectedTab();

      // check if the tab is one of the Detail tabs. If so do not
      // change the selection of it
      if (!currentTab.equals(tabMandiriList)) {
        tabMandiriList.setSelected(true);
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
   * Filter the mandiri list with 'like mandiri name'. <br>
   */
  public void onClick$button_MandiriList_SearchName(Event event) throws Exception {
    // logger.debug(event.toString());

    // if not empty
    if (!txtb_Mandiri_Name.getValue().isEmpty()) {
      checkbox_MandiriList_ShowAll.setChecked(false); // unCheck

      // ++ create the searchObject and init sorting ++//
      HibernateSearchObject<Mandiri> soMandiri =
          new HibernateSearchObject<Mandiri>(Mandiri.class, getMandiriListCtrl().getCountRows());
      soMandiri.addFilter(new Filter("deskripsiMandiri", "%" + txtb_Mandiri_Name.getValue() + "%",
          Filter.OP_ILIKE));
      soMandiri.addSort("deskripsiMandiri", false);

      // Change the BindingListModel.
      if (getMandiriListCtrl().getBinder() != null) {
        getMandiriListCtrl().getPagedBindingListWrapper().setSearchObject(soMandiri);

        // get the current Tab for later checking if we must change it
        Tab currentTab = tabbox_MandiriMain.getSelectedTab();

        // check if the tab is one of the Detail tabs. If so do not
        // change the selection of it
        if (!currentTab.equals(tabMandiriList)) {
          tabMandiriList.setSelected(true);
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
  public void onCreate$windowMandiriMain(Event event) throws Exception {
    windowMandiriMain.setContentStyle("padding:0px;");

    // create the Button Controller. Disable not used buttons during working
    btnCtrlMandiri =
        new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint,
            btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
            btnCancel, null);

    doCheckRights();

    /**
     * Initiate the first loading by selecting the customerList tab and create the components from
     * the zul-file.
     */
    tabMandiriList.setSelected(true);

    if (tabPanelMandiriList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelMandiriList, this,
          "ModuleMainController", "/WEB-INF/pages/master/mandiri/mandiriList.zul");
    }

    // init the buttons for editMode
    btnCtrlMandiri.setInitEdit();
  }

  /**
   * When the tab 'tabPanelMandiriDetail' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabMandiriDetail(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelMandiriDetail.getFirstChild() != null) {
      tabMandiriDetail.setSelected(true);

      // refresh the Binding mechanism
      getMandiriDetailCtrl().setMandiri(getSelectedMandiri());
      getMandiriDetailCtrl().getBinder().loadAll();

      return;
    }

    if (tabPanelMandiriDetail != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelMandiriDetail, this,
          "ModuleMainController", "/WEB-INF/pages/master/mandiri/mandiriDetail.zul");
    }
  }

  /**
   * When the tab 'tabMandiriList' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabMandiriList(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelMandiriList.getFirstChild() != null) {
      tabMandiriList.setSelected(true);

      return;
    }

    if (tabPanelMandiriList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelMandiriList, this,
          "ModuleMainController", "/WEB-INF/pages//mandiri/mandiriList.zul");
    }

  }

  public void setMandiriDetailCtrl(MandiriDetailCtrl mandiriDetailCtrl) {
    this.mandiriDetailCtrl = mandiriDetailCtrl;
  }

  /* CONTROLLERS */
  public void setMandiriListCtrl(MandiriListCtrl mandiriListCtrl) {
    this.mandiriListCtrl = mandiriListCtrl;
  }

  public void setMandiris(BindingListModelList mandiris) {
    this.mandiris = mandiris;
  }

  public void setMandiriService(MandiriService mandiriService) {
    this.mandiriService = mandiriService;
  }

  /* Master BEANS */
  public void setOriginalMandiri(Mandiri originalMandiri) {
    this.originalMandiri = originalMandiri;
  }

  public void setSelectedMandiri(Mandiri selectedMandiri) {
    this.selectedMandiri = selectedMandiri;
  }

  /* COMPONENTS and OTHERS */
}
