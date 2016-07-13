package billy.webui.transaction.piutang.approval;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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

import billy.backend.model.Karyawan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
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

public class ApprovalPiutangMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ApprovalPiutangMainCtrl.class);


  protected Window windowApprovalPiutangMain; // autowired

  // Tabs
  protected Tabbox tabbox_ApprovalPiutangMain; // autowired
  protected Tab tabApprovalPiutangList; // autowired
  protected Tab tabApprovalPiutangDetail; // autowired
  protected Tabpanel tabPanelApprovalPiutangList; // autowired
  protected Tabpanel tabPanelApprovalPiutangDetail; // autowired

  // filter components
  protected Checkbox checkbox_ApprovalPiutangList_ShowAll; // autowired
  protected Textbox tb_Search_No_Faktur; // aurowired
  protected Textbox tb_Search_Nama_Pelanggan; // aurowired
  protected Textbox tb_Search_Alamat; // aurowired
  protected Button button_ApprovalPiutangList_Search; // aurowired


  // Button controller for the CRUD buttons
  private final String btnCtroller_ClassPrefix = "button_ApprovalPiutangMain_";
  private ButtonStatusCtrl btnCtrlPiutang;
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
  private ApprovalPiutangListCtrl approvalPiutangListCtrl;
  private ApprovalPiutangDetailCtrl approvalPiutangDetailCtrl;

  // Databinding
  private Piutang selectedPiutang;
  private BindingListModelList piutangs;

  // ServiceDAOs / Domain Classes
  private PiutangService piutangService;


  // always a copy from the bean before modifying. Used for reseting
  private Piutang originalPiutang;

  DecimalFormat df = new DecimalFormat("#,###");


  /**
   * default constructor.<br>
   */
  public ApprovalPiutangMainCtrl() {
    super();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    /**
     * 1. Set an 'alias' for this composer name to access it in the zul-file.<br>
     * 2. Set the piutang 'recurse' to 'false' to avoid problems with managing more than one
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
    if (getApprovalPiutangDetailCtrl().getBinder() != null) {

      // refresh all dataBinder related controllers/components
      getApprovalPiutangDetailCtrl().getBinder().loadAll();
      getApprovalPiutangDetailCtrl().doRefresh();
      // set editable Mode
      getApprovalPiutangDetailCtrl().doReadOnlyMode(true);

      btnCtrlPiutang.setInitEdit();
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
    button_ApprovalPiutangList_Search.setVisible(workspace
        .isAllowed("button_ApprovalPiutangList_Search"));
    tabApprovalPiutangList.setVisible(workspace.isAllowed("windowApprovalPiutangList"));
    tabApprovalPiutangDetail.setVisible(workspace.isAllowed("windowApprovalPiutangDetail"));
    btnFirst.setVisible(workspace.isAllowed("button_ApprovalPiutangMain_btnFirst"));
    btnPrevious.setVisible(workspace.isAllowed("button_ApprovalPiutangMain_btnPrevious"));
    btnNext.setVisible(workspace.isAllowed("button_ApprovalPiutangMain_btnNext"));
    btnLast.setVisible(workspace.isAllowed("button_ApprovalPiutangMain_btnLast"));
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
    if (getApprovalPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPiutangDetail, null));
    }

    // check first, if the tabs are created
    if (getApprovalPiutangDetailCtrl().getBinder() == null) {
      return;
    }

    final Piutang anPiutang = getSelectedPiutang();
    if (anPiutang != null) {

      // Show a confirm box
      final String msg =
          Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
              + anPiutang.getNoKuitansi();
      final String title = Labels.getLabel("message.Deleting.Record");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            private void deleteBean() throws InterruptedException {
              try {
                getPiutangService().delete(anPiutang);
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

    btnCtrlPiutang.setInitEdit();

    setSelectedPiutang(null);
    // refresh the list
    getApprovalPiutangListCtrl().doFillListbox();

    // refresh all dataBinder related controllers
    getApprovalPiutangDetailCtrl().getBinder().loadAll();
    getApprovalPiutangDetailCtrl().doRefresh();
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
    Tab currentTab = tabbox_ApprovalPiutangMain.getSelectedTab();

    // check first, if the tabs are created, if not than create it
    if (getApprovalPiutangDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPiutangDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getApprovalPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPiutangDetail, null));
    }

    // check if the tab is one of the Detail tabs. If so do not change the
    // selection of it
    if (!currentTab.equals(tabApprovalPiutangDetail)) {
      tabApprovalPiutangDetail.setSelected(true);
    } else {
      currentTab.setSelected(true);
    }

    // remember the old vars
    doStoreInitValues();

    btnCtrlPiutang.setBtnStatus_Edit();

    getApprovalPiutangDetailCtrl().doReadOnlyMode(false);

    // refresh the UI, because we can click the EditBtn from every tab.
    getApprovalPiutangDetailCtrl().getBinder().loadAll();
    getApprovalPiutangDetailCtrl().doRefresh();
    // set focus
    getApprovalPiutangDetailCtrl().txtb_tglPembayaran.focus();
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
    if (getApprovalPiutangDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPiutangDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getApprovalPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabApprovalPiutangDetail, null));
    }

    // remember the current object
    doStoreInitValues();

    /** !!! DO NOT BREAK THE TIERS !!! */
    // We don't create a new DomainObject() in the frontend.
    // We GET it from the backend.
    final Piutang anPiutang = getPiutangService().getNewPiutang();

    // set the beans in the related databinded controllers
    getApprovalPiutangDetailCtrl().setPiutang(anPiutang);
    getApprovalPiutangDetailCtrl().setSelectedPiutang(anPiutang);

    // Refresh the binding mechanism
    getApprovalPiutangDetailCtrl().setSelectedPiutang(getSelectedPiutang());

    try {
      getApprovalPiutangDetailCtrl().doRefresh();
      getApprovalPiutangDetailCtrl().getBinder().loadAll();

    } catch (Exception e) {
      // do nothing
    }
    // getPiutangDetailCtrl().emptyAllValue();
    // set editable Mode
    getApprovalPiutangDetailCtrl().doReadOnlyMode(false);
    // set the ButtonStatus to New-Mode
    btnCtrlPiutang.setInitNew();

    tabApprovalPiutangDetail.setSelected(true);
    // set focus
    getApprovalPiutangDetailCtrl().txtb_tglPembayaran.focus();

  }

  /**
   * Reset the selected object to its origin property values.
   * 
   * @see doStoreInitValues()
   */
  public void doResetToInitValues() {

    if (getOriginalPiutang() != null) {

      try {
        setSelectedPiutang((Piutang) ZksampleBeanUtils.cloneBean(getOriginalPiutang()));
        // TODO Bug in DataBinder??
        windowApprovalPiutangMain.invalidate();

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

    if (tabbox_ApprovalPiutangMain.getSelectedTab() == tabApprovalPiutangDetail) {
      getApprovalPiutangDetailCtrl().doFitSize(event);
    } else if (tabbox_ApprovalPiutangMain.getSelectedTab() == tabApprovalPiutangList) {
      // resize and fill Listbox new
      getApprovalPiutangListCtrl().doFillListbox();
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
    getApprovalPiutangDetailCtrl().getBinder().saveAll();

    try {


      getApprovalPiutangDetailCtrl().txtb_ApprovedBy.setReadonly(true);
      getApprovalPiutangDetailCtrl().txtb_ApprovedRemark.setReadonly(true);
      getApprovalPiutangDetailCtrl().getPiutang().setNeedApproval(false);


      String userName =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getUsername();
      getApprovalPiutangDetailCtrl().getPiutang().setLastUpdate(new Date());
      getApprovalPiutangDetailCtrl().getPiutang().setUpdatedBy(userName);

      // LOGIC untuk status DISKON, KURANG BAYAR, TARIK BARANG
      Status status = getApprovalPiutangDetailCtrl().getStatusService().getStatusByID(new Long(4)); // KURANG_BAYAR
      getApprovalPiutangDetailCtrl().getPiutang().setStatus(status);


      // save it to database
      getPiutangService().saveOrUpdate(getApprovalPiutangDetailCtrl().getPiutang());


      // if saving is successfully than actualize the beans as
      // origins.
      doStoreInitValues();
      // refresh the list
      getApprovalPiutangListCtrl().doFillListbox();
      // later refresh StatusBar
      Events.postEvent("onSelect", getApprovalPiutangListCtrl().getListBoxApprovalPiutang(),
          getSelectedPiutang());

      // show the objects data in the statusBar
      String str = getSelectedPiutang().getNoKuitansi();
      EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
          new Event("onChangeSelectedObject", null, str));

    } catch (DataAccessException e) {
      ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

      // Reset to init values
      doResetToInitValues();

      return;

    } finally {
      btnCtrlPiutang.setInitEdit();
      getApprovalPiutangDetailCtrl().doReadOnlyMode(true);
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
        (BindingListModelList) getApprovalPiutangListCtrl().getListBoxApprovalPiutang().getModel();

    // check if data exists
    if (blml == null || blml.size() < 1)
      return;

    int index = blml.indexOf(getSelectedPiutang());

    /**
     * Check, if all tabs with data binded components are created So we work with spring
     * BeanCreation we must check a little bit deeper, because the Controller are preCreated ? After
     * that, go back to the current/selected tab.
     */
    Tab currentTab = tabbox_ApprovalPiutangMain.getSelectedTab();

    if (getApprovalPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event(Events.ON_SELECT, tabApprovalPiutangDetail, null));
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

    getApprovalPiutangListCtrl().getListBoxApprovalPiutang().setSelectedIndex(index);
    setSelectedPiutang((Piutang) blml.get(index));

    // call onSelect() for showing the objects data in the statusBar
    Events.sendEvent(new Event(Events.ON_SELECT, getApprovalPiutangListCtrl()
        .getListBoxApprovalPiutang(), getSelectedPiutang()));

    // refresh master-detail MASTERS data
    getApprovalPiutangDetailCtrl().getBinder().loadAll();
    getApprovalPiutangDetailCtrl().doRefresh();
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

    if (getSelectedPiutang() != null) {

      try {
        setOriginalPiutang((Piutang) ZksampleBeanUtils.cloneBean(getSelectedPiutang()));
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

  public ApprovalPiutangDetailCtrl getApprovalPiutangDetailCtrl() {
    return this.approvalPiutangDetailCtrl;
  }

  public ApprovalPiutangListCtrl getApprovalPiutangListCtrl() {
    return this.approvalPiutangListCtrl;
  }

  /**
   * When the "save" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */

  public Piutang getOriginalPiutang() {
    return this.originalPiutang;
  }


  public BindingListModelList getPiutangs() {
    return this.piutangs;
  }

  public PiutangService getPiutangService() {
    return piutangService;
  }

  public Piutang getSelectedPiutang() {
    return this.selectedPiutang;
  }

  /**
   * when the checkBox 'Show All' for filtering is checked. <br>
   * 
   * @param event
   */
  public void onCheck$checkbox_ApprovalPiutangList_ShowAll(Event event) {
    // logger.debug(event.toString());

    // empty the text search boxes
    tb_Search_No_Faktur.setValue(""); // clear
    tb_Search_Nama_Pelanggan.setValue(""); // clear
    tb_Search_Alamat.setValue(""); // clear

    // ++ create the searchObject and init sorting ++//
    HibernateSearchObject<Piutang> soPiutang =
        new HibernateSearchObject<Piutang>(Piutang.class, getApprovalPiutangListCtrl()
            .getCountRows());
    soPiutang.addSort("penjualan.noFaktur", false);
    soPiutang.addFilter(new Filter("needApproval", true, Filter.OP_EQUAL));
    SecUser secUser =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    if (secUser.getKaryawan() != null) {
      Karyawan karyawan = secUser.getKaryawan();
      if (karyawan.getSupervisorDivisi() != null) {
        Karyawan supervisor = karyawan.getSupervisorDivisi();
        soPiutang.addFilter(new Filter("divisi.supervisorDivisi.id", supervisor.getId(),
            Filter.OP_EQUAL));
      }
    }

    // Change the BindingListModel.
    if (getApprovalPiutangListCtrl().getBinder() != null) {
      getApprovalPiutangListCtrl().getPagedBindingListWrapper().setSearchObject(soPiutang);

      // get the current Tab for later checking if we must change it
      Tab currentTab = tabbox_ApprovalPiutangMain.getSelectedTab();

      // check if the tab is one of the Detail tabs. If so do not
      // change the selection of it
      if (!currentTab.equals(tabApprovalPiutangList)) {
        tabApprovalPiutangList.setSelected(true);
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

  public void onClick$btnSave(Event event) throws InterruptedException {

    /*
     * validasi butuh approval
     */
    String message = "";
    if (getApprovalPiutangDetailCtrl().getPiutang().getNilaiTagihan()
        .compareTo(getApprovalPiutangDetailCtrl().getPiutang().getPembayaran()) == 1) {
      message += "- Kurang Pembayaran \n";
    }
    if (getApprovalPiutangDetailCtrl().getPiutang().getNilaiTagihan()
        .compareTo(getApprovalPiutangDetailCtrl().getPiutang().getPembayaran()) == -1) {
      message += "- Lebih Pembayaran \n";
    }

    if (message != "") {
      getApprovalPiutangDetailCtrl().getPiutang().setNeedApproval(true);
      getApprovalPiutangDetailCtrl().getPiutang().setReasonApproval(message);
      getApprovalPiutangDetailCtrl().txtb_ReasonApproval.setValue(message);
      getApprovalPiutangDetailCtrl().label_butuhApproval.setValue("Ya");
    } else {
      getApprovalPiutangDetailCtrl().getPiutang().setNeedApproval(false);
      getApprovalPiutangDetailCtrl().getPiutang().setReasonApproval("");
      getApprovalPiutangDetailCtrl().txtb_ReasonApproval.setValue("");
      getApprovalPiutangDetailCtrl().label_butuhApproval.setValue("Tidak");
    }

    if (getSelectedPiutang().isNeedApproval()) {
      Status status = getApprovalPiutangDetailCtrl().getStatusService().getStatusByID(new Long(1)); // BUTUH_APPROVAL
      getApprovalPiutangDetailCtrl().getPiutang().setStatus(status);
      final Piutang anPiutang = getSelectedPiutang();
      if (anPiutang != null) {

        // Show a confirm box
        String msg = message;
        final String title = Labels.getLabel("message.Information");

        MultiLineMessageBox.doSetTemplate();
        if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO,
            Messagebox.QUESTION, true, new EventListener() {
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


    } else {
      Status status = getApprovalPiutangDetailCtrl().getStatusService().getStatusByID(new Long(2));// LUNAS
      getApprovalPiutangDetailCtrl().getPiutang().setStatus(status);

      // set sisa piutang di penjualan
      BigDecimal sisaPiutangPenjualan =
          getApprovalPiutangDetailCtrl().getPiutang().getPenjualan().getPiutang();
      BigDecimal pembayaranPiutang = getApprovalPiutangDetailCtrl().getPiutang().getPembayaran();
      getApprovalPiutangDetailCtrl().getPiutang().getPenjualan()
          .setPiutang(sisaPiutangPenjualan.subtract(pembayaranPiutang));
      doSave(event);
    }
  }

  /**
   * Filter the piutang list <br>
   */
  public void onClick$button_ApprovalPiutangList_Search(Event event) throws Exception {
    // logger.debug(event.toString());

    // if not empty
    if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())
        || StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())
        || StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
      checkbox_ApprovalPiutangList_ShowAll.setChecked(false); // unCheck

      // ++ create the searchObject and init sorting ++//
      HibernateSearchObject<Piutang> soPiutang =
          new HibernateSearchObject<Piutang>(Piutang.class, getApprovalPiutangListCtrl()
              .getCountRows());
      // check which field have input
      soPiutang.addFilter(new Filter("needApproval", true, Filter.OP_EQUAL));
      if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())) {
        soPiutang.addFilter(new Filter("penjualan.noFaktur", tb_Search_No_Faktur.getValue(),
            Filter.OP_EQUAL));
      }

      if (StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())) {
        soPiutang.addFilter(new Filter("penjualan.namaPelanggan", "%"
            + tb_Search_Nama_Pelanggan.getValue().toUpperCase() + "%", Filter.OP_ILIKE));
      }

      if (StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
        soPiutang.addFilter(new Filter("penjualan.alamat", "%" + tb_Search_Alamat.getValue() + "%",
            Filter.OP_ILIKE));
      }
      SecUser secUser =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getSecUser();
      if (secUser.getKaryawan() != null) {
        Karyawan karyawan = secUser.getKaryawan();
        if (karyawan.getSupervisorDivisi() != null) {
          Karyawan supervisor = karyawan.getSupervisorDivisi();
          soPiutang.addFilter(new Filter("penjualan.divisi.supervisorDivisi.id",
              supervisor.getId(), Filter.OP_EQUAL));
        }
      }

      // Change the BindingListModel.
      if (getApprovalPiutangListCtrl().getBinder() != null) {
        getApprovalPiutangListCtrl().getPagedBindingListWrapper().setSearchObject(soPiutang);

        // get the current Tab for later checking if we must change it
        Tab currentTab = tabbox_ApprovalPiutangMain.getSelectedTab();

        // check if the tab is one of the Detail tabs. If so do not
        // change the selection of it
        if (!currentTab.equals(tabApprovalPiutangList)) {
          tabApprovalPiutangList.setSelected(true);
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
  public void onCreate$windowApprovalPiutangMain(Event event) throws Exception {
    windowApprovalPiutangMain.setContentStyle("padding:0px;");

    // create the Button Controller. Disable not used buttons during working
    btnCtrlPiutang =
        new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint,
            btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
            btnCancel, null);

    doCheckRights();

    /**
     * Initiate the first loading by selecting the customerList tab and create the components from
     * the zul-file.
     */
    tabApprovalPiutangList.setSelected(true);

    if (tabPanelApprovalPiutangList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelApprovalPiutangList, this,
          "ModuleMainController",
          "/WEB-INF/pages/transaction/approvalpiutang/approvalPiutangList.zul");
    }

    // init the buttons for editMode
    btnCtrlPiutang.setInitEdit();
  }

  /**
   * When the tab 'tabPanelPiutangDetail' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabApprovalPiutangDetail(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelApprovalPiutangDetail.getFirstChild() != null) {
      tabApprovalPiutangDetail.setSelected(true);

      // refresh the Binding mechanism
      getApprovalPiutangDetailCtrl().setPiutang(getSelectedPiutang());
      getApprovalPiutangDetailCtrl().getBinder().loadAll();
      getApprovalPiutangDetailCtrl().doRefresh();
      return;
    }

    if (tabPanelApprovalPiutangDetail != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelApprovalPiutangDetail, this,
          "ModuleMainController",
          "/WEB-INF/pages/transaction/approvalpiutang/approvalPiutangDetail.zul");
    }
  }

  /**
   * When the tab 'tabPiutangList' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabApprovalPiutangList(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelApprovalPiutangList.getFirstChild() != null) {
      tabApprovalPiutangList.setSelected(true);

      return;
    }

    if (tabPanelApprovalPiutangList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelApprovalPiutangList, this,
          "ModuleMainController",
          "/WEB-INF/pages/transaction/approvalpiutang/approvalPiutangList.zul");
    }

  }

  public void setApprovalPiutangDetailCtrl(ApprovalPiutangDetailCtrl piutangDetailCtrl) {
    this.approvalPiutangDetailCtrl = piutangDetailCtrl;
  }

  /* CONTROLLERS */
  public void setApprovalPiutangListCtrl(ApprovalPiutangListCtrl piutangListCtrl) {
    this.approvalPiutangListCtrl = piutangListCtrl;
  }

  /* Master BEANS */
  public void setOriginalPiutang(Piutang originalPiutang) {
    this.originalPiutang = originalPiutang;
  }

  public void setPiutangs(BindingListModelList piutangs) {
    this.piutangs = piutangs;
  }

  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  public void setSelectedPiutang(Piutang selectedPiutang) {
    this.selectedPiutang = selectedPiutang;
  }

  /* COMPONENTS and OTHERS */
}
