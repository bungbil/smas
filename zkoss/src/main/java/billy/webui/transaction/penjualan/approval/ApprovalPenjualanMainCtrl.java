package billy.webui.transaction.penjualan.approval;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Status;
import billy.backend.service.PenjualanService;
import billy.backend.service.PiutangService;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ApprovalPenjualanMainCtrl extends GFCBaseCtrl implements Serializable {


  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ApprovalPenjualanMainCtrl.class);


  protected Window windowApprovalPenjualanMain; // autowired

  // Tabs
  protected Tabbox tabbox_ApprovalPenjualanMain; // autowired
  protected Tab tabApprovalPenjualanList; // autowired
  protected Tab tabApprovalPenjualanDetail; // autowired
  protected Tabpanel tabPanelApprovalPenjualanList; // autowired
  protected Tabpanel tabPanelApprovalPenjualanDetail; // autowired

  // filter components
  protected Checkbox checkbox_ApprovalPenjualanList_ShowAll; // autowired
  protected Textbox tb_Search_No_Faktur; // aurowired
  protected Textbox tb_Search_Nama_Pelanggan; // aurowired
  protected Textbox tb_Search_Alamat; // aurowired
  protected Button button_ApprovalPenjualanList_Search; // aurowired


  // Button controller for the CRUD buttons
  private final String btnCtroller_ClassPrefix = "button_ApprovalPenjualanMain_";
  private ButtonStatusCtrl btnCtrlApprovalPenjualan;
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
  private ApprovalPenjualanListCtrl approvalPenjualanListCtrl;
  private ApprovalPenjualanDetailCtrl approvalPenjualanDetailCtrl;

  // Databinding
  private Penjualan selectedPenjualan;
  private BindingListModelList penjualans;

  // ServiceDAOs / Domain Classes
  private PenjualanService penjualanService;
  private PiutangService piutangService;
  // always a copy from the bean before modifying. Used for reseting
  private Penjualan originalPenjualan;

  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public ApprovalPenjualanMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    /**
     * 1. Set an 'alias' for this composer name to access it in the zul-file.<br>
     * 2. Set the penjualan 'recurse' to 'false' to avoid problems with managing more than one
     * zul-file in one page. Otherwise it would be overridden and can ends in curious error
     * messages.
     */
    this.self.setAttribute("controller", this, false);
  }

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
    if (getApprovalPenjualanDetailCtrl().getBinder() != null) {

      // refresh all dataBinder related controllers/components
      getApprovalPenjualanDetailCtrl().getBinder().loadAll();
      getApprovalPenjualanDetailCtrl().doRefresh();
      // set editable Mode
      getApprovalPenjualanDetailCtrl().doReadOnlyMode(true);

      btnCtrlApprovalPenjualan.setInitEdit();
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
    button_ApprovalPenjualanList_Search.setVisible(workspace
        .isAllowed("button_ApprovalPenjualanList_Search"));
    tabApprovalPenjualanList.setVisible(workspace.isAllowed("windowApprovalPenjualanList"));
    tabApprovalPenjualanDetail.setVisible(workspace.isAllowed("windowApprovalPenjualanDetail"));
    btnFirst.setVisible(workspace.isAllowed("button_ApprovalPenjualanMain_btnFirst"));
    btnPrevious.setVisible(workspace.isAllowed("button_ApprovalPenjualanMain_btnPrevious"));
    btnNext.setVisible(workspace.isAllowed("button_ApprovalPenjualanMain_btnNext"));
    btnLast.setVisible(workspace.isAllowed("button_ApprovalPenjualanMain_btnLast"));
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
    if (getApprovalPenjualanDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPenjualanDetail, null));
    }

    // check first, if the tabs are created
    if (getApprovalPenjualanDetailCtrl().getBinder() == null) {
      return;
    }

    final Penjualan anPenjualan = getSelectedPenjualan();
    if (anPenjualan != null) {

      // Show a confirm box
      final String msg =
          Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
              + anPenjualan.getNoFaktur();
      final String title = Labels.getLabel("message.Deleting.Record");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            private void deleteBean() throws InterruptedException {
              try {
                getPenjualanService().delete(anPenjualan);
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

    btnCtrlApprovalPenjualan.setInitEdit();

    setSelectedPenjualan(null);
    // refresh the list
    getApprovalPenjualanListCtrl().doFillListbox();

    // refresh all dataBinder related controllers
    getApprovalPenjualanDetailCtrl().getBinder().loadAll();
    getApprovalPenjualanDetailCtrl().doRefresh();
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
    Tab currentTab = tabbox_ApprovalPenjualanMain.getSelectedTab();

    // check first, if the tabs are created, if not than create it
    if (getApprovalPenjualanDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPenjualanDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getApprovalPenjualanDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPenjualanDetail, null));
    }

    // check if the tab is one of the Detail tabs. If so do not change the
    // selection of it
    if (!currentTab.equals(tabApprovalPenjualanDetail)) {
      tabApprovalPenjualanDetail.setSelected(true);
    } else {
      currentTab.setSelected(true);
    }

    // remember the old vars
    doStoreInitValues();

    btnCtrlApprovalPenjualan.setBtnStatus_Edit();

    getApprovalPenjualanDetailCtrl().doReadOnlyMode(false);

    // refresh the UI, because we can click the EditBtn from every tab.
    getApprovalPenjualanDetailCtrl().getBinder().loadAll();
    getApprovalPenjualanDetailCtrl().doRefresh();
    // set focus
    // getApprovalPenjualanDetailCtrl().txtb_NoOrderSheet.focus();
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
    if (getApprovalPenjualanDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPenjualanDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getApprovalPenjualanDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPenjualanDetail, null));
    }

    // remember the current object
    doStoreInitValues();

    /** !!! DO NOT BREAK THE TIERS !!! */
    // We don't create a new DomainObject() in the frontend.
    // We GET it from the backend.
    final Penjualan anPenjualan = getPenjualanService().getNewPenjualan();

    // set the beans in the related databinded controllers
    getApprovalPenjualanDetailCtrl().setPenjualan(anPenjualan);
    getApprovalPenjualanDetailCtrl().setSelectedPenjualan(anPenjualan);

    // Refresh the binding mechanism
    getApprovalPenjualanDetailCtrl().setSelectedPenjualan(getSelectedPenjualan());

    try {
      getApprovalPenjualanDetailCtrl().doRefresh();
      getApprovalPenjualanDetailCtrl().getBinder().loadAll();

    } catch (Exception e) {
      // do nothing
    }
    // getPenjualanDetailCtrl().emptyAllValue();
    // set editable Mode
    getApprovalPenjualanDetailCtrl().doReadOnlyMode(false);
    // set the ButtonStatus to New-Mode
    btnCtrlApprovalPenjualan.setInitNew();

    tabApprovalPenjualanDetail.setSelected(true);
    // set focus
    // getApprovalPenjualanDetailCtrl().txtb_NoOrderSheet.focus();

  }


  /**
   * Reset the selected object to its origin property values.
   * 
   * @see doStoreInitValues()
   */
  public void doResetToInitValues() {

    if (getOriginalPenjualan() != null) {

      try {
        setSelectedPenjualan((Penjualan) ZksampleBeanUtils.cloneBean(getOriginalPenjualan()));
        // TODO Bug in DataBinder??
        windowApprovalPenjualanMain.invalidate();

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

    if (tabbox_ApprovalPenjualanMain.getSelectedTab() == tabApprovalPenjualanDetail) {
      getApprovalPenjualanDetailCtrl().doFitSize(event);
    } else if (tabbox_ApprovalPenjualanMain.getSelectedTab() == tabApprovalPenjualanList) {
      // resize and fill Listbox new
      getApprovalPenjualanListCtrl().doFillListbox();
    }
  }

  /**
   * Saves all involved Beans to the DB.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void doSave(Event event) throws InterruptedException {
    // logger.debug(event.toString());
    // save all components data in the several tabs to the bean
    getApprovalPenjualanDetailCtrl().getBinder().saveAll();

    try {
      // getApprovalPenjualanDetailCtrl().txtb_ApprovedBy.setReadonly(true);
      // getApprovalPenjualanDetailCtrl().txtb_ApprovedRemark.setReadonly(true);
      getApprovalPenjualanDetailCtrl().getPenjualan().setNeedApproval(false);
      String userName =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getUsername();
      getApprovalPenjualanDetailCtrl().getPenjualan().setLastUpdate(new Date());
      getApprovalPenjualanDetailCtrl().getPenjualan().setUpdatedBy(userName);
      getApprovalPenjualanDetailCtrl().getPenjualan().setApprovedBy(userName);


      Status status =
          getApprovalPenjualanDetailCtrl().getStatusService().getStatusByID(new Long(3)); // PROSES
      getApprovalPenjualanDetailCtrl().getPenjualan().setStatus(status);

      // save it to database
      getPenjualanService().saveOrUpdate(getApprovalPenjualanDetailCtrl().getPenjualan());

      // generatePiutang
      if (getApprovalPenjualanDetailCtrl().getPenjualan().getIntervalKredit() > 1) {
        getPiutangService().deleteAllPiutang(getApprovalPenjualanDetailCtrl().getPenjualan());
        getPiutangService().generatePiutangByIntervalKredit(
            getApprovalPenjualanDetailCtrl().getPenjualan(),
            getApprovalPenjualanDetailCtrl().getPenjualan().getIntervalKredit(), status);
      }

      // if saving is successfully than actualize the beans as
      // origins.
      doStoreInitValues();
      // refresh the list
      getApprovalPenjualanListCtrl().doFillListbox();
      // later refresh StatusBar
      /*
       * Events.postEvent("onSelect", getApprovalPenjualanListCtrl().getListBoxApprovalPenjualan(),
       * getSelectedPenjualan()); // show the objects data in the statusBar String str =
       * getSelectedPenjualan().getNoFaktur(); EventQueues.lookup("selectedObjectEventQueue",
       * EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));
       */
    } catch (DataAccessException e) {
      ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

      // Reset to init values
      doResetToInitValues();

      return;

    } finally {
      btnCtrlApprovalPenjualan.setInitEdit();
      getApprovalPenjualanDetailCtrl().doReadOnlyMode(true);

      setSelectedPenjualan(null);
      // refresh the list
      getApprovalPenjualanListCtrl().doFillListbox();

      // refresh all dataBinder related controllers
      getApprovalPenjualanDetailCtrl().getBinder().loadAll();
      if (getApprovalPenjualanDetailCtrl().getSelectedPenjualan() != null) {
        getApprovalPenjualanDetailCtrl().doRefresh();
      }
      tabApprovalPenjualanList.setSelected(true);
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
        (BindingListModelList) getApprovalPenjualanListCtrl().getListBoxApprovalPenjualan()
            .getModel();

    // check if data exists
    if (blml == null || blml.size() < 1)
      return;

    int index = blml.indexOf(getSelectedPenjualan());

    /**
     * Check, if all tabs with data binded components are created So we work with spring
     * BeanCreation we must check a little bit deeper, because the Controller are preCreated ? After
     * that, go back to the current/selected tab.
     */
    Tab currentTab = tabbox_ApprovalPenjualanMain.getSelectedTab();

    if (getApprovalPenjualanDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event(Events.ON_SELECT, tabApprovalPenjualanDetail, null));
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

    getApprovalPenjualanListCtrl().getListBoxApprovalPenjualan().setSelectedIndex(index);
    setSelectedPenjualan((Penjualan) blml.get(index));

    // call onSelect() for showing the objects data in the statusBar
    Events.sendEvent(new Event(Events.ON_SELECT, getApprovalPenjualanListCtrl()
        .getListBoxApprovalPenjualan(), getSelectedPenjualan()));

    // refresh master-detail MASTERS data
    getApprovalPenjualanDetailCtrl().getBinder().loadAll();
    getApprovalPenjualanDetailCtrl().doRefresh();
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

    if (getSelectedPenjualan() != null) {

      try {
        setOriginalPenjualan((Penjualan) ZksampleBeanUtils.cloneBean(getSelectedPenjualan()));
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

  public ApprovalPenjualanDetailCtrl getApprovalPenjualanDetailCtrl() {
    return this.approvalPenjualanDetailCtrl;
  }

  public ApprovalPenjualanListCtrl getApprovalPenjualanListCtrl() {
    return this.approvalPenjualanListCtrl;
  }

  public Penjualan getOriginalPenjualan() {
    return this.originalPenjualan;
  }

  public BindingListModelList getPenjualans() {
    return this.penjualans;
  }

  /* SERVICES */
  public PenjualanService getPenjualanService() {
    return this.penjualanService;
  }

  public PiutangService getPiutangService() {
    return piutangService;
  }

  public Penjualan getSelectedPenjualan() {
    return this.selectedPenjualan;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * when the checkBox 'Show All' for filtering is checked. <br>
   * 
   * @param event
   */
  public void onCheck$checkbox_ApprovalPenjualanList_ShowAll(Event event) {
    // logger.debug(event.toString());

    // empty the text search boxes
    tb_Search_No_Faktur.setValue(""); // clear
    tb_Search_Nama_Pelanggan.setValue(""); // clear
    tb_Search_Alamat.setValue(""); // clear

    // ++ create the searchObject and init sorting ++//
    HibernateSearchObject<Penjualan> soPenjualan =
        new HibernateSearchObject<Penjualan>(Penjualan.class, getApprovalPenjualanListCtrl()
            .getCountRows());
    soPenjualan.addSort("noFaktur", false);
    soPenjualan.addFilter(new Filter("needApproval", true, Filter.OP_EQUAL));

    SecUser secUser =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    if (secUser.getKaryawan() != null) {
      Karyawan karyawan = secUser.getKaryawan();
      if (karyawan.getSupervisorDivisi() != null) {
        Karyawan supervisor = karyawan.getSupervisorDivisi();
        soPenjualan.addFilter(new Filter("divisi.supervisorDivisi.id", supervisor.getId(),
            Filter.OP_EQUAL));
      }
    }

    // Change the BindingListModel.
    if (getApprovalPenjualanListCtrl().getBinder() != null) {
      getApprovalPenjualanListCtrl().getPagedBindingListWrapper().setSearchObject(soPenjualan);

      // get the current Tab for later checking if we must change it
      Tab currentTab = tabbox_ApprovalPenjualanMain.getSelectedTab();

      // check if the tab is one of the Detail tabs. If so do not
      // change the selection of it
      if (!currentTab.equals(tabApprovalPenjualanList)) {
        tabApprovalPenjualanList.setSelected(true);
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

  /**
   * When the "help" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */
  public void onClick$btnHelp(Event event) throws InterruptedException {
    doHelp(event);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

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

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * When the "save" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */


  public void onClick$btnSave(Event event) throws InterruptedException {


    final Penjualan anPenjualan = getSelectedPenjualan();
    if (anPenjualan != null) {

      // Show a confirm box
      String msg = "Apakah anda yakin mengapprove penjualan ini? ";
      final String title = Labels.getLabel("message.Information");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            @Override
            public void onEvent(Event evt) {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    doSave(evt);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  break; //
                case MultiLineMessageBox.NO:
                  break; //
              }
            }
          }) == MultiLineMessageBox.YES) {
      }
    }

  }

  /**
   * Filter the penjualan list <br>
   */
  public void onClick$button_ApprovalPenjualanList_Search(Event event) throws Exception {
    // logger.debug(event.toString());

    // if not empty
    if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())
        || StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())
        || StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
      checkbox_ApprovalPenjualanList_ShowAll.setChecked(false); // unCheck

      // ++ create the searchObject and init sorting ++//
      HibernateSearchObject<Penjualan> soPenjualan =
          new HibernateSearchObject<Penjualan>(Penjualan.class, getApprovalPenjualanListCtrl()
              .getCountRows());
      // check which field have input
      soPenjualan.addFilter(new Filter("needApproval", true, Filter.OP_EQUAL));
      if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())) {
        soPenjualan.addFilter(new Filter("noFaktur", tb_Search_No_Faktur.getValue().toUpperCase(),
            Filter.OP_EQUAL));
      }

      if (StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())) {
        soPenjualan.addFilter(new Filter("namaPelanggan", "%"
            + tb_Search_Nama_Pelanggan.getValue().toUpperCase() + "%", Filter.OP_ILIKE));
      }

      if (StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
        soPenjualan.addFilter(new Filter("alamat", "%" + tb_Search_Alamat.getValue() + "%",
            Filter.OP_ILIKE));
      }
      SecUser secUser =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getSecUser();
      if (secUser.getKaryawan() != null) {
        Karyawan karyawan = secUser.getKaryawan();
        if (karyawan.getSupervisorDivisi() != null) {
          Karyawan supervisor = karyawan.getSupervisorDivisi();
          soPenjualan.addFilter(new Filter("divisi.supervisorDivisi.id", supervisor.getId(),
              Filter.OP_EQUAL));
        }
      }

      // Change the BindingListModel.
      if (getApprovalPenjualanListCtrl().getBinder() != null) {
        getApprovalPenjualanListCtrl().getPagedBindingListWrapper().setSearchObject(soPenjualan);

        // get the current Tab for later checking if we must change it
        Tab currentTab = tabbox_ApprovalPenjualanMain.getSelectedTab();

        // check if the tab is one of the Detail tabs. If so do not
        // change the selection of it
        if (!currentTab.equals(tabApprovalPenjualanList)) {
          tabApprovalPenjualanList.setSelected(true);
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
  public void onCreate$windowApprovalPenjualanMain(Event event) throws Exception {
    windowApprovalPenjualanMain.setContentStyle("padding:0px;");

    // create the Button Controller. Disable not used buttons during working
    btnCtrlApprovalPenjualan =
        new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint,
            btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
            btnCancel, null);

    doCheckRights();

    /**
     * Initiate the first loading by selecting the customerList tab and create the components from
     * the zul-file.
     */
    tabApprovalPenjualanList.setSelected(true);

    if (tabPanelApprovalPenjualanList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelApprovalPenjualanList, this,
          "ModuleMainController",
          "/WEB-INF/pages/transaction/penjualan/approval/approvalPenjualanList.zul");
    }

    // init the buttons for editMode
    btnCtrlApprovalPenjualan.setInitEdit();
  }

  /**
   * When the tab 'tabPanelPenjualanDetail' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabApprovalPenjualanDetail(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelApprovalPenjualanDetail.getFirstChild() != null) {
      tabApprovalPenjualanDetail.setSelected(true);

      // refresh the Binding mechanism
      getApprovalPenjualanDetailCtrl().setPenjualan(getSelectedPenjualan());
      getApprovalPenjualanDetailCtrl().getBinder().loadAll();
      getApprovalPenjualanDetailCtrl().doRefresh();
      return;
    }

    if (tabPanelApprovalPenjualanDetail != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelApprovalPenjualanDetail, this,
          "ModuleMainController",
          "/WEB-INF/pages/transaction/penjualan/approval/approvalPenjualanDetail.zul");
    }
  }

  /**
   * When the tab 'tabPenjualanList' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabApprovalPenjualanList(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelApprovalPenjualanList.getFirstChild() != null) {
      tabApprovalPenjualanList.setSelected(true);

      return;
    }

    if (tabPanelApprovalPenjualanList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelApprovalPenjualanList, this,
          "ModuleMainController",
          "/WEB-INF/pages/transaction/penjualan/approval/approvalPenjualanList.zul");
    }

  }

  public void setApprovalPenjualanDetailCtrl(ApprovalPenjualanDetailCtrl approvalPenjualanDetailCtrl) {
    this.approvalPenjualanDetailCtrl = approvalPenjualanDetailCtrl;
  }

  /* CONTROLLERS */
  public void setApprovalPenjualanListCtrl(ApprovalPenjualanListCtrl approvalPenjualanListCtrl) {
    this.approvalPenjualanListCtrl = approvalPenjualanListCtrl;
  }

  /* Master BEANS */
  public void setOriginalPenjualan(Penjualan originalPenjualan) {
    this.originalPenjualan = originalPenjualan;
  }

  public void setPenjualans(BindingListModelList penjualans) {
    this.penjualans = penjualans;
  }

  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }

  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  public void setSelectedPenjualan(Penjualan selectedPenjualan) {
    this.selectedPenjualan = selectedPenjualan;
  }

  /* COMPONENTS and OTHERS */
}
