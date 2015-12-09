package billy.webui.master.satuanbarang;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
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

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import billy.backend.model.SatuanBarang;
import billy.backend.service.SatuanBarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class SatuanBarangMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SatuanBarangMainCtrl.class);

	
	protected Window windowSatuanBarangMain; // autowired

	// Tabs
	protected Tabbox tabbox_SatuanBarangMain; // autowired
	protected Tab tabSatuanBarangList; // autowired
	protected Tab tabSatuanBarangDetail; // autowired
	protected Tabpanel tabPanelSatuanBarangList; // autowired
	protected Tabpanel tabPanelSatuanBarangDetail; // autowired

	// filter components
	protected Checkbox checkbox_SatuanBarangList_ShowAll; // autowired
	protected Textbox txtb_SatuanBarang_Name; // aurowired
	protected Button button_SatuanBarangList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_SatuanBarangMain_";
	private ButtonStatusCtrl btnCtrlSatuanBarang;
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
	private SatuanBarangListCtrl satuanBarangListCtrl;
	private SatuanBarangDetailCtrl satuanBarangDetailCtrl;

	// Databinding
	private SatuanBarang selectedSatuanBarang;
	private BindingListModelList satuanBarangs;

	// ServiceDAOs / Domain Classes
	private SatuanBarangService satuanBarangService;

	// always a copy from the bean before modifying. Used for reseting
	private SatuanBarang originalSatuanBarang;

	/**
	 * default constructor.<br>
	 */
	public SatuanBarangMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the satuanBarang 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$windowSatuanBarangMain(Event event) throws Exception {
		windowSatuanBarangMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlSatuanBarang = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabSatuanBarangList.setSelected(true);

		if (tabPanelSatuanBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelSatuanBarangList, this, "ModuleMainController", "/WEB-INF/pages/master/satuanBarang/satuanBarangList.zul");
		}

		// init the buttons for editMode
		btnCtrlSatuanBarang.setInitEdit();
	}

	/**
	 * When the tab 'tabSatuanBarangList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabSatuanBarangList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelSatuanBarangList.getFirstChild() != null) {
			tabSatuanBarangList.setSelected(true);

			return;
		}

		if (tabPanelSatuanBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelSatuanBarangList, this, "ModuleMainController", "/WEB-INF/pages//satuanBarang/satuanBarangList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelSatuanBarangDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabSatuanBarangDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelSatuanBarangDetail.getFirstChild() != null) {
			tabSatuanBarangDetail.setSelected(true);

			// refresh the Binding mechanism
			getSatuanBarangDetailCtrl().setSatuanBarang(getSelectedSatuanBarang());
			getSatuanBarangDetailCtrl().getBinder().loadAll();
			
			return;
		}

		if (tabPanelSatuanBarangDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelSatuanBarangDetail, this, "ModuleMainController", "/WEB-INF/pages/master/satuanBarang/satuanBarangDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SatuanBarangList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_SatuanBarang_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SatuanBarang> soSatuanBarang = new HibernateSearchObject<SatuanBarang>(SatuanBarang.class, getSatuanBarangListCtrl().getCountRows());
		soSatuanBarang.addSort("deskripsiSatuanBarang", false);

		// Change the BindingListModel.
		if (getSatuanBarangListCtrl().getBinder() != null) {
			getSatuanBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soSatuanBarang);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_SatuanBarangMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabSatuanBarangList)) {
				tabSatuanBarangList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the satuanBarang list with 'like satuanBarang name'. <br>
	 */
	public void onClick$button_SatuanBarangList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_SatuanBarang_Name.getValue().isEmpty()) {
			checkbox_SatuanBarangList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<SatuanBarang> soSatuanBarang = new HibernateSearchObject<SatuanBarang>(SatuanBarang.class, getSatuanBarangListCtrl().getCountRows());
			soSatuanBarang.addFilter(new Filter("deskripsiSatuanBarang", "%" + txtb_SatuanBarang_Name.getValue() + "%", Filter.OP_ILIKE));
			soSatuanBarang.addSort("deskripsiSatuanBarang", false);

			// Change the BindingListModel.
			if (getSatuanBarangListCtrl().getBinder() != null) {
				getSatuanBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soSatuanBarang);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_SatuanBarangMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabSatuanBarangList)) {
					tabSatuanBarangList.setSelected(true);
				} else {
					currentTab.setSelected(true);
				}
			}
		}
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
	 * When the "save" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		doSave(event);
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
	public void onClick$btnCancel(Event event) throws InterruptedException {
		doCancel(event);
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
	 * when the "go previous record" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrevious(Event event) throws InterruptedException {
		doSkip(event);
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
	 * when the "go last record" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnLast(Event event) throws InterruptedException {
		doSkip(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
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
		if (getSatuanBarangDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getSatuanBarangDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getSatuanBarangDetailCtrl().doReadOnlyMode(true);

			btnCtrlSatuanBarang.setInitEdit();
		}
	}

	/**
	 * Sets all UI-components to writable-mode. Sets the buttons to edit-Mode.
	 * Checks first, if the NEEDED TABS with its contents are created. If not,
	 * than create it by simulate an onSelect() with calling Events.sendEvent()
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doEdit(Event event) {
		// logger.debug(event.toString());
		// get the current Tab for later checking if we must change it
		Tab currentTab = tabbox_SatuanBarangMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getSatuanBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabSatuanBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getSatuanBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabSatuanBarangDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabSatuanBarangDetail)) {
			tabSatuanBarangDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlSatuanBarang.setBtnStatus_Edit();

		getSatuanBarangDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getSatuanBarangDetailCtrl().getBinder().loadAll();

		// set focus
		getSatuanBarangDetailCtrl().txtb_KodeSatuanBarang.focus();
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
		if (getSatuanBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabSatuanBarangDetail, null));
		}

		// check first, if the tabs are created
		if (getSatuanBarangDetailCtrl().getBinder() == null) {
			return;
		}

		final SatuanBarang anSatuanBarang = getSelectedSatuanBarang();
		if (anSatuanBarang != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anSatuanBarang.getKodeSatuanBarang();
			final String title = Labels.getLabel("message.Deleting.Record");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
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

				private void deleteBean() throws InterruptedException {
					try {
						getSatuanBarangService().delete(anSatuanBarang);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlSatuanBarang.setInitEdit();

		setSelectedSatuanBarang(null);
		// refresh the list
		getSatuanBarangListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getSatuanBarangDetailCtrl().getBinder().loadAll();
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
		getSatuanBarangDetailCtrl().getBinder().saveAll();

		try {			
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getSatuanBarangDetailCtrl().getSatuanBarang().setLastUpdate(new Date());			
			getSatuanBarangDetailCtrl().getSatuanBarang().setUpdatedBy(userName);			
			// save it to database
			getSatuanBarangService().saveOrUpdate(getSatuanBarangDetailCtrl().getSatuanBarang());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getSatuanBarangListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getSatuanBarangListCtrl().getListBoxSatuanBarang(), getSelectedSatuanBarang());

			// show the objects data in the statusBar
			String str = getSelectedSatuanBarang().getKodeSatuanBarang();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlSatuanBarang.setInitEdit();
			getSatuanBarangDetailCtrl().doReadOnlyMode(true);
		}
	}

	/**
	 * Sets all UI-components to writable-mode. Stores the current Beans as
	 * originBeans and get new Objects from the backend.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doNew(Event event) {
		// logger.debug(event.toString());
		// check first, if the tabs are created
		if (getSatuanBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabSatuanBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getSatuanBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabSatuanBarangDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final SatuanBarang anSatuanBarang = getSatuanBarangService().getNewSatuanBarang();

		// set the beans in the related databinded controllers
		getSatuanBarangDetailCtrl().setSatuanBarang(anSatuanBarang);
		getSatuanBarangDetailCtrl().setSelectedSatuanBarang(anSatuanBarang);

		// Refresh the binding mechanism
		getSatuanBarangDetailCtrl().setSelectedSatuanBarang(getSelectedSatuanBarang());
		try{
			getSatuanBarangDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}

		// set editable Mode
		getSatuanBarangDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlSatuanBarang.setInitNew();

		tabSatuanBarangDetail.setSelected(true);
		// set focus
		getSatuanBarangDetailCtrl().txtb_KodeSatuanBarang.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getSatuanBarangListCtrl().getListBoxSatuanBarang().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedSatuanBarang());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_SatuanBarangMain.getSelectedTab();

		if (getSatuanBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabSatuanBarangDetail, null));
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

		getSatuanBarangListCtrl().getListBoxSatuanBarang().setSelectedIndex(index);
		setSelectedSatuanBarang((SatuanBarang) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getSatuanBarangListCtrl().getListBoxSatuanBarang(), getSelectedSatuanBarang()));

		// refresh master-detail MASTERS data
		getSatuanBarangDetailCtrl().getBinder().loadAll();

		// EXTRA: if we have a longtext field under the listbox, so we must
		// refresh
		// this binded component too
		// getArticleListCtrl().getBinder().loadComponent(getArticleListCtrl().longBoxArt_LangBeschreibung);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Resizes the container from the selected Tab.
	 * 
	 * @param event
	 */
	private void doResizeSelectedTab(Event event) {
		// logger.debug(event.toString());

		if (tabbox_SatuanBarangMain.getSelectedTab() == tabSatuanBarangDetail) {
			getSatuanBarangDetailCtrl().doFitSize(event);
		} else if (tabbox_SatuanBarangMain.getSelectedTab() == tabSatuanBarangList) {
			// resize and fill Listbox new
			getSatuanBarangListCtrl().doFillListbox();
		}
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
	 * Saves the selected object's current properties. We can get them back if a
	 * modification is canceled.
	 * 
	 * @see doResetToInitValues()
	 */
	public void doStoreInitValues() {

		if (getSelectedSatuanBarang() != null) {

			try {
				setOriginalSatuanBarang((SatuanBarang) ZksampleBeanUtils.cloneBean(getSelectedSatuanBarang()));
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
	 * Reset the selected object to its origin property values.
	 * 
	 * @see doStoreInitValues()
	 * 
	 */
	public void doResetToInitValues() {

		if (getOriginalSatuanBarang() != null) {

			try {
				setSelectedSatuanBarang((SatuanBarang) ZksampleBeanUtils.cloneBean(getOriginalSatuanBarang()));
				// TODO Bug in DataBinder??
				windowSatuanBarangMain.invalidate();

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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are getting from the spring framework users
	 * grantedAuthority(). Remember! A right is only a string. <br>
	 */
	// TODO move it to zul
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();
		button_SatuanBarangList_SearchName.setVisible(workspace.isAllowed("button_SatuanBarangList_SearchName"));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalSatuanBarang(SatuanBarang originalSatuanBarang) {
		this.originalSatuanBarang = originalSatuanBarang;
	}

	public SatuanBarang getOriginalSatuanBarang() {
		return this.originalSatuanBarang;
	}

	public void setSelectedSatuanBarang(SatuanBarang selectedSatuanBarang) {
		this.selectedSatuanBarang = selectedSatuanBarang;
	}

	public SatuanBarang getSelectedSatuanBarang() {
		return this.selectedSatuanBarang;
	}

	public void setSatuanBarangs(BindingListModelList satuanBarangs) {
		this.satuanBarangs = satuanBarangs;
	}

	public BindingListModelList getSatuanBarangs() {
		return this.satuanBarangs;
	}

	/* CONTROLLERS */
	public void setSatuanBarangListCtrl(SatuanBarangListCtrl satuanBarangListCtrl) {
		this.satuanBarangListCtrl = satuanBarangListCtrl;
	}

	public SatuanBarangListCtrl getSatuanBarangListCtrl() {
		return this.satuanBarangListCtrl;
	}

	public void setSatuanBarangDetailCtrl(SatuanBarangDetailCtrl satuanBarangDetailCtrl) {
		this.satuanBarangDetailCtrl = satuanBarangDetailCtrl;
	}

	public SatuanBarangDetailCtrl getSatuanBarangDetailCtrl() {
		return this.satuanBarangDetailCtrl;
	}

	/* SERVICES */
	public SatuanBarangService getSatuanBarangService() {
		return this.satuanBarangService;
	}

	public void setSatuanBarangService(SatuanBarangService satuanBarangService) {
		this.satuanBarangService = satuanBarangService;
	}

	/* COMPONENTS and OTHERS */
}
