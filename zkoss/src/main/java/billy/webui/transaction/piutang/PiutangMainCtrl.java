package billy.webui.transaction.piutang;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
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

public class PiutangMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PiutangMainCtrl.class);


  protected Window windowPiutangMain; // autowired

  // Tabs
  protected Tabbox tabbox_PiutangMain; // autowired
  protected Tab tabPiutangList; // autowired
  protected Tab tabPiutangDetail; // autowired
  protected Tabpanel tabPanelPiutangList; // autowired
  protected Tabpanel tabPanelPiutangDetail; // autowired

  // filter components
  protected Checkbox checkbox_PiutangList_ShowAll; // autowired
  protected Textbox tb_Search_No_Faktur; // aurowired
  protected Textbox tb_Search_Nama_Pelanggan; // aurowired
  protected Textbox tb_Search_Alamat; // aurowired
  protected Button button_PiutangList_Search; // aurowired


  // Button controller for the CRUD buttons
  private final String btnCtroller_ClassPrefix = "button_PiutangMain_";
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
  private PiutangListCtrl piutangListCtrl;
  private PiutangDetailCtrl piutangDetailCtrl;

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
  public PiutangMainCtrl() {
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
    if (getPiutangDetailCtrl().getBinder() != null) {

      // refresh all dataBinder related controllers/components
      getPiutangDetailCtrl().getBinder().loadAll();
      getPiutangDetailCtrl().doRefresh();
      // set editable Mode
      getPiutangDetailCtrl().doReadOnlyMode(true);

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
    button_PiutangList_Search.setVisible(workspace.isAllowed("button_PiutangList_Search"));
    tabPiutangList.setVisible(workspace.isAllowed("windowPiutangList"));
    tabPiutangDetail.setVisible(workspace.isAllowed("windowPiutangDetail"));
    btnEdit.setVisible(workspace.isAllowed("button_PiutangMain_btnEdit"));
    btnNew.setVisible(workspace.isAllowed("button_PiutangMain_btnNew"));
    btnDelete.setVisible(workspace.isAllowed("button_PiutangMain_btnDelete"));
    btnSave.setVisible(workspace.isAllowed("button_PiutangMain_btnSave"));
    btnCancel.setVisible(workspace.isAllowed("button_PiutangMain_btnCancel"));
    btnFirst.setVisible(workspace.isAllowed("button_PiutangMain_btnFirst"));
    btnPrevious.setVisible(workspace.isAllowed("button_PiutangMain_btnPrevious"));
    btnNext.setVisible(workspace.isAllowed("button_PiutangMain_btnNext"));
    btnLast.setVisible(workspace.isAllowed("button_PiutangMain_btnLast"));
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
    if (getPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabPiutangDetail, null));
    }

    // check first, if the tabs are created
    if (getPiutangDetailCtrl().getBinder() == null) {
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
    getPiutangListCtrl().doFillListbox();

    // refresh all dataBinder related controllers
    getPiutangDetailCtrl().getBinder().loadAll();
    getPiutangDetailCtrl().doRefresh();
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
    Tab currentTab = tabbox_PiutangMain.getSelectedTab();

    // check first, if the tabs are created, if not than create it
    if (getPiutangDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabPiutangDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabPiutangDetail, null));
    }

    // check if the tab is one of the Detail tabs. If so do not change the
    // selection of it
    if (!currentTab.equals(tabPiutangDetail)) {
      tabPiutangDetail.setSelected(true);
    } else {
      currentTab.setSelected(true);
    }

    // remember the old vars
    doStoreInitValues();

    btnCtrlPiutang.setBtnStatus_Edit();

    getPiutangDetailCtrl().doReadOnlyMode(false);

    // refresh the UI, because we can click the EditBtn from every tab.
    getPiutangDetailCtrl().getBinder().loadAll();
    getPiutangDetailCtrl().doRefresh();
    // set focus
    getPiutangDetailCtrl().txtb_tglPembayaran.focus();
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
    if (getPiutangDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", tabPiutangDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", tabPiutangDetail, null));
    }

    // remember the current object
    doStoreInitValues();

    /** !!! DO NOT BREAK THE TIERS !!! */
    // We don't create a new DomainObject() in the frontend.
    // We GET it from the backend.
    final Piutang anPiutang = getPiutangService().getNewPiutang();

    // set the beans in the related databinded controllers
    getPiutangDetailCtrl().setPiutang(anPiutang);
    getPiutangDetailCtrl().setSelectedPiutang(anPiutang);

    // Refresh the binding mechanism
    getPiutangDetailCtrl().setSelectedPiutang(getSelectedPiutang());

    try {
      getPiutangDetailCtrl().doRefresh();
      getPiutangDetailCtrl().getBinder().loadAll();

    } catch (Exception e) {
      // do nothing
    }
    // getPiutangDetailCtrl().emptyAllValue();
    // set editable Mode
    getPiutangDetailCtrl().doReadOnlyMode(false);
    // set the ButtonStatus to New-Mode
    btnCtrlPiutang.setInitNew();

    tabPiutangDetail.setSelected(true);
    // set focus
    getPiutangDetailCtrl().txtb_tglPembayaran.focus();

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
        windowPiutangMain.invalidate();

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

    if (tabbox_PiutangMain.getSelectedTab() == tabPiutangDetail) {
      getPiutangDetailCtrl().doFitSize(event);
    } else if (tabbox_PiutangMain.getSelectedTab() == tabPiutangList) {
      // resize and fill Listbox new
      getPiutangListCtrl().doFillListbox();
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
    getPiutangDetailCtrl().getBinder().saveAll();

    try {
      Listitem itemKolektor = getPiutangDetailCtrl().lbox_Kolektor.getSelectedItem();
      if (itemKolektor != null) {
        ListModelList lml1 = (ListModelList) getPiutangDetailCtrl().lbox_Kolektor.getListModel();
        Karyawan karyawan = (Karyawan) lml1.get(itemKolektor.getIndex());
        getPiutangDetailCtrl().getPiutang().setKolektor(karyawan);
        getPiutangDetailCtrl().getPiutang().getPenjualan().setKolektor(karyawan);
      }

      String userName =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getUsername();
      getPiutangDetailCtrl().getPiutang().setLastUpdate(new Date());
      getPiutangDetailCtrl().getPiutang().setUpdatedBy(userName);

      // save it to database
      getPiutangService().saveOrUpdate(getPiutangDetailCtrl().getPiutang());


      // if saving is successfully than actualize the beans as
      // origins.
      doStoreInitValues();
      // refresh the list
      getPiutangListCtrl().doFillListbox();
      // later refresh StatusBar
      Events.postEvent("onSelect", getPiutangListCtrl().getListBoxPiutang(), getSelectedPiutang());

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
      getPiutangDetailCtrl().doReadOnlyMode(true);
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
        (BindingListModelList) getPiutangListCtrl().getListBoxPiutang().getModel();

    // check if data exists
    if (blml == null || blml.size() < 1)
      return;

    int index = blml.indexOf(getSelectedPiutang());

    /**
     * Check, if all tabs with data binded components are created So we work with spring
     * BeanCreation we must check a little bit deeper, because the Controller are preCreated ? After
     * that, go back to the current/selected tab.
     */
    Tab currentTab = tabbox_PiutangMain.getSelectedTab();

    if (getPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event(Events.ON_SELECT, tabPiutangDetail, null));
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

    getPiutangListCtrl().getListBoxPiutang().setSelectedIndex(index);
    setSelectedPiutang((Piutang) blml.get(index));

    // call onSelect() for showing the objects data in the statusBar
    Events.sendEvent(new Event(Events.ON_SELECT, getPiutangListCtrl().getListBoxPiutang(),
        getSelectedPiutang()));

    // refresh master-detail MASTERS data
    getPiutangDetailCtrl().getBinder().loadAll();
    getPiutangDetailCtrl().doRefresh();
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

  /**
   * When the "save" button is clicked.
   * 
   * @param event
   * @throws InterruptedException
   */

  public Piutang getOriginalPiutang() {
    return this.originalPiutang;
  }

  public PiutangDetailCtrl getPiutangDetailCtrl() {
    return this.piutangDetailCtrl;
  }

  public PiutangListCtrl getPiutangListCtrl() {
    return this.piutangListCtrl;
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
  public void onCheck$checkbox_PiutangList_ShowAll(Event event) {
    // logger.debug(event.toString());

    // empty the text search boxes
    tb_Search_No_Faktur.setValue(""); // clear
    tb_Search_Nama_Pelanggan.setValue(""); // clear
    tb_Search_Alamat.setValue(""); // clear

    // ++ create the searchObject and init sorting ++//
    HibernateSearchObject<Piutang> soPiutang =
        new HibernateSearchObject<Piutang>(Piutang.class, getPiutangListCtrl().getCountRows());
    soPiutang.addSort("penjualan.noFaktur", false);

    SecUser secUser =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    if (secUser.getKaryawan() != null) {
      Karyawan karyawan = secUser.getKaryawan();
      if (karyawan.getSupervisorDivisi() != null) {
        Karyawan supervisor = karyawan.getSupervisorDivisi();
        soPiutang.addFilter(new Filter("divisi.supervisorDivisi.id", supervisor.getId(),
            Filter.OP_EQUAL));
        soPiutang.addFilter(new Filter("aktif", true, Filter.OP_EQUAL));
      }
    }

    // Change the BindingListModel.
    if (getPiutangListCtrl().getBinder() != null) {
      getPiutangListCtrl().getPagedBindingListWrapper().setSearchObject(soPiutang);

      // get the current Tab for later checking if we must change it
      Tab currentTab = tabbox_PiutangMain.getSelectedTab();

      // check if the tab is one of the Detail tabs. If so do not
      // change the selection of it
      if (!currentTab.equals(tabPiutangList)) {
        tabPiutangList.setSelected(true);
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
    if (getPiutangDetailCtrl().getPiutang().getNilaiTagihan()
        .compareTo(getPiutangDetailCtrl().getPiutang().getPembayaran()) == 1) {
      message += "- Kurang Pembayaran \n";
    }
    if (getPiutangDetailCtrl().getPiutang().getNilaiTagihan()
        .compareTo(getPiutangDetailCtrl().getPiutang().getPembayaran()) == -1) {
      message += "- Lebih Pembayaran \n";
    }

    if (message != "") {
      getPiutangDetailCtrl().getPiutang().setNeedApproval(true);
      getPiutangDetailCtrl().getPiutang().setReasonApproval(message);
      getPiutangDetailCtrl().txtb_ReasonApproval.setValue(message);
      getPiutangDetailCtrl().label_butuhApproval.setValue("Ya");
    } else {
      getPiutangDetailCtrl().getPiutang().setNeedApproval(false);
      getPiutangDetailCtrl().getPiutang().setReasonApproval("");
      getPiutangDetailCtrl().txtb_ReasonApproval.setValue("");
      getPiutangDetailCtrl().label_butuhApproval.setValue("Tidak");
    }

    if (getSelectedPiutang().isNeedApproval()) {
      Status status = getPiutangDetailCtrl().getStatusService().getStatusByID(new Long(1)); // BUTUH_APPROVAL
      getPiutangDetailCtrl().getPiutang().setStatus(status);
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
      Status status = getPiutangDetailCtrl().getStatusService().getStatusByID(new Long(2));// LUNAS
      getPiutangDetailCtrl().getPiutang().setStatus(status);

      // set sisa piutang di penjualan
      BigDecimal sisaPiutangPenjualan =
          getPiutangDetailCtrl().getPiutang().getPenjualan().getPiutang();
      BigDecimal pembayaranPiutang = getPiutangDetailCtrl().getPiutang().getPembayaran();
      getPiutangDetailCtrl().getPiutang().getPenjualan()
          .setPiutang(sisaPiutangPenjualan.subtract(pembayaranPiutang));
      doSave(event);
    }
  }

  /**
   * Filter the piutang list <br>
   */
  public void onClick$button_PiutangList_Search(Event event) throws Exception {
    // logger.debug(event.toString());

    // if not empty
    if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())
        || StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())
        || StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
      checkbox_PiutangList_ShowAll.setChecked(false); // unCheck

      // ++ create the searchObject and init sorting ++//
      HibernateSearchObject<Piutang> soPiutang =
          new HibernateSearchObject<Piutang>(Piutang.class, getPiutangListCtrl().getCountRows());
      // check which field have input
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
          soPiutang.addFilter(new Filter("aktif", true, Filter.OP_EQUAL));
        }
      }

      // Change the BindingListModel.
      if (getPiutangListCtrl().getBinder() != null) {
        getPiutangListCtrl().getPagedBindingListWrapper().setSearchObject(soPiutang);

        // get the current Tab for later checking if we must change it
        Tab currentTab = tabbox_PiutangMain.getSelectedTab();

        // check if the tab is one of the Detail tabs. If so do not
        // change the selection of it
        if (!currentTab.equals(tabPiutangList)) {
          tabPiutangList.setSelected(true);
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
  public void onCreate$windowPiutangMain(Event event) throws Exception {
    windowPiutangMain.setContentStyle("padding:0px;");

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
    tabPiutangList.setSelected(true);

    if (tabPanelPiutangList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelPiutangList, this,
          "ModuleMainController", "/WEB-INF/pages/transaction/piutang/piutangList.zul");
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
  public void onSelect$tabPiutangDetail(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelPiutangDetail.getFirstChild() != null) {
      tabPiutangDetail.setSelected(true);

      // refresh the Binding mechanism
      getPiutangDetailCtrl().setPiutang(getSelectedPiutang());
      getPiutangDetailCtrl().getBinder().loadAll();
      getPiutangDetailCtrl().doRefresh();
      return;
    }

    if (tabPanelPiutangDetail != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelPiutangDetail, this,
          "ModuleMainController", "/WEB-INF/pages/transaction/piutang/piutangDetail.zul");
    }
  }

  /**
   * When the tab 'tabPiutangList' is selected.<br>
   * Loads the zul-file into the tab.
   * 
   * @param event
   * @throws IOException
   */
  public void onSelect$tabPiutangList(Event event) throws IOException {
    // logger.debug(event.toString());

    // Check if the tabpanel is already loaded
    if (tabPanelPiutangList.getFirstChild() != null) {
      tabPiutangList.setSelected(true);

      return;
    }

    if (tabPanelPiutangList != null) {
      ZksampleCommonUtils.createTabPanelContent(this.tabPanelPiutangList, this,
          "ModuleMainController", "/WEB-INF/pages/transaction/piutang/piutangList.zul");
    }

  }

  /* Master BEANS */
  public void setOriginalPiutang(Piutang originalPiutang) {
    this.originalPiutang = originalPiutang;
  }

  public void setPiutangDetailCtrl(PiutangDetailCtrl piutangDetailCtrl) {
    this.piutangDetailCtrl = piutangDetailCtrl;
  }

  /* CONTROLLERS */
  public void setPiutangListCtrl(PiutangListCtrl piutangListCtrl) {
    this.piutangListCtrl = piutangListCtrl;
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
