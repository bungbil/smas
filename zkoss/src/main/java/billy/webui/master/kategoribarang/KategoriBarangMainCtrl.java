package billy.webui.master.kategoribarang;

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
import billy.backend.model.KategoriBarang;
import billy.backend.service.KategoriBarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class KategoriBarangMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(KategoriBarangMainCtrl.class);

	
	protected Window windowKategoriBarangMain; // autowired

	// Tabs
	protected Tabbox tabbox_KategoriBarangMain; // autowired
	protected Tab tabKategoriBarangList; // autowired
	protected Tab tabKategoriBarangDetail; // autowired
	protected Tabpanel tabPanelKategoriBarangList; // autowired
	protected Tabpanel tabPanelKategoriBarangDetail; // autowired

	// filter components
	protected Checkbox checkbox_KategoriBarangList_ShowAll; // autowired
	protected Textbox txtb_KategoriBarang_Name; // aurowired
	protected Button button_KategoriBarangList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_KategoriBarangMain_";
	private ButtonStatusCtrl btnCtrlKategoriBarang;
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
	private KategoriBarangListCtrl kategoriBarangListCtrl;
	private KategoriBarangDetailCtrl kategoriBarangDetailCtrl;

	// Databinding
	private KategoriBarang selectedKategoriBarang;
	private BindingListModelList kategoriBarangs;

	// ServiceDAOs / Domain Classes
	private KategoriBarangService kategoriBarangService;

	// always a copy from the bean before modifying. Used for reseting
	private KategoriBarang originalKategoriBarang;

	/**
	 * default constructor.<br>
	 */
	public KategoriBarangMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the kategoriBarang 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowKategoriBarangMain(Event event) throws Exception {
		windowKategoriBarangMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlKategoriBarang = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabKategoriBarangList.setSelected(true);

		if (tabPanelKategoriBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelKategoriBarangList, this, "ModuleMainController", "/WEB-INF/pages/master/kategoriBarang/kategoriBarangList.zul");
		}

		// init the buttons for editMode
		btnCtrlKategoriBarang.setInitEdit();
	}

	/**
	 * When the tab 'tabKategoriBarangList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabKategoriBarangList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelKategoriBarangList.getFirstChild() != null) {
			tabKategoriBarangList.setSelected(true);

			return;
		}

		if (tabPanelKategoriBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelKategoriBarangList, this, "ModuleMainController", "/WEB-INF/pages//kategoriBarang/kategoriBarangList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelKategoriBarangDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabKategoriBarangDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelKategoriBarangDetail.getFirstChild() != null) {
			tabKategoriBarangDetail.setSelected(true);

			// refresh the Binding mechanism
			getKategoriBarangDetailCtrl().setKategoriBarang(getSelectedKategoriBarang());
			getKategoriBarangDetailCtrl().getBinder().loadAll();
			
			return;
		}

		if (tabPanelKategoriBarangDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelKategoriBarangDetail, this, "ModuleMainController", "/WEB-INF/pages/master/kategoriBarang/kategoriBarangDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_KategoriBarangList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_KategoriBarang_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<KategoriBarang> soKategoriBarang = new HibernateSearchObject<KategoriBarang>(KategoriBarang.class, getKategoriBarangListCtrl().getCountRows());
		soKategoriBarang.addSort("deskripsiKategoriBarang", false);

		// Change the BindingListModel.
		if (getKategoriBarangListCtrl().getBinder() != null) {
			getKategoriBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soKategoriBarang);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_KategoriBarangMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabKategoriBarangList)) {
				tabKategoriBarangList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the kategoriBarang list with 'like kategoriBarang name'. <br>
	 */
	public void onClick$button_KategoriBarangList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_KategoriBarang_Name.getValue().isEmpty()) {
			checkbox_KategoriBarangList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<KategoriBarang> soKategoriBarang = new HibernateSearchObject<KategoriBarang>(KategoriBarang.class, getKategoriBarangListCtrl().getCountRows());
			soKategoriBarang.addFilter(new Filter("deskripsiKategoriBarang", "%" + txtb_KategoriBarang_Name.getValue() + "%", Filter.OP_ILIKE));
			soKategoriBarang.addSort("deskripsiKategoriBarang", false);

			// Change the BindingListModel.
			if (getKategoriBarangListCtrl().getBinder() != null) {
				getKategoriBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soKategoriBarang);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_KategoriBarangMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabKategoriBarangList)) {
					tabKategoriBarangList.setSelected(true);
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
		if (getKategoriBarangDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getKategoriBarangDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getKategoriBarangDetailCtrl().doReadOnlyMode(true);

			btnCtrlKategoriBarang.setInitEdit();
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
		Tab currentTab = tabbox_KategoriBarangMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getKategoriBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabKategoriBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getKategoriBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabKategoriBarangDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabKategoriBarangDetail)) {
			tabKategoriBarangDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlKategoriBarang.setBtnStatus_Edit();

		getKategoriBarangDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getKategoriBarangDetailCtrl().getBinder().loadAll();

		// set focus
		getKategoriBarangDetailCtrl().txtb_KodeKategoriBarang.focus();
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
		if (getKategoriBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabKategoriBarangDetail, null));
		}

		// check first, if the tabs are created
		if (getKategoriBarangDetailCtrl().getBinder() == null) {
			return;
		}

		final KategoriBarang anKategoriBarang = getSelectedKategoriBarang();
		if (anKategoriBarang != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anKategoriBarang.getKodeKategoriBarang();
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
						getKategoriBarangService().delete(anKategoriBarang);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlKategoriBarang.setInitEdit();

		setSelectedKategoriBarang(null);
		// refresh the list
		getKategoriBarangListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getKategoriBarangDetailCtrl().getBinder().loadAll();
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
		getKategoriBarangDetailCtrl().getBinder().saveAll();

		try {
			
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getKategoriBarangDetailCtrl().getKategoriBarang().setLastUpdate(new Date());			
			getKategoriBarangDetailCtrl().getKategoriBarang().setUpdatedBy(userName);			
			// save it to database
			getKategoriBarangService().saveOrUpdate(getKategoriBarangDetailCtrl().getKategoriBarang());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getKategoriBarangListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getKategoriBarangListCtrl().getListBoxKategoriBarang(), getSelectedKategoriBarang());

			// show the objects data in the statusBar
			String str = getSelectedKategoriBarang().getKodeKategoriBarang();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlKategoriBarang.setInitEdit();
			getKategoriBarangDetailCtrl().doReadOnlyMode(true);
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
		if (getKategoriBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabKategoriBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getKategoriBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabKategoriBarangDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final KategoriBarang anKategoriBarang = getKategoriBarangService().getNewKategoriBarang();

		// set the beans in the related databinded controllers
		getKategoriBarangDetailCtrl().setKategoriBarang(anKategoriBarang);
		getKategoriBarangDetailCtrl().setSelectedKategoriBarang(anKategoriBarang);

		// Refresh the binding mechanism
		getKategoriBarangDetailCtrl().setSelectedKategoriBarang(getSelectedKategoriBarang());
		try{
			getKategoriBarangDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}

		// set editable Mode
		getKategoriBarangDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlKategoriBarang.setInitNew();

		tabKategoriBarangDetail.setSelected(true);
		// set focus
		getKategoriBarangDetailCtrl().txtb_KodeKategoriBarang.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getKategoriBarangListCtrl().getListBoxKategoriBarang().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedKategoriBarang());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_KategoriBarangMain.getSelectedTab();

		if (getKategoriBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabKategoriBarangDetail, null));
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

		getKategoriBarangListCtrl().getListBoxKategoriBarang().setSelectedIndex(index);
		setSelectedKategoriBarang((KategoriBarang) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getKategoriBarangListCtrl().getListBoxKategoriBarang(), getSelectedKategoriBarang()));

		// refresh master-detail MASTERS data
		getKategoriBarangDetailCtrl().getBinder().loadAll();

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

		if (tabbox_KategoriBarangMain.getSelectedTab() == tabKategoriBarangDetail) {
			getKategoriBarangDetailCtrl().doFitSize(event);
		} else if (tabbox_KategoriBarangMain.getSelectedTab() == tabKategoriBarangList) {
			// resize and fill Listbox new
			getKategoriBarangListCtrl().doFillListbox();
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

		if (getSelectedKategoriBarang() != null) {

			try {
				setOriginalKategoriBarang((KategoriBarang) ZksampleBeanUtils.cloneBean(getSelectedKategoriBarang()));
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

		if (getOriginalKategoriBarang() != null) {

			try {
				setSelectedKategoriBarang((KategoriBarang) ZksampleBeanUtils.cloneBean(getOriginalKategoriBarang()));
				// TODO Bug in DataBinder??
				windowKategoriBarangMain.invalidate();

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
		button_KategoriBarangList_SearchName.setVisible(workspace.isAllowed("button_KategoriBarangList_SearchName"));
		tabKategoriBarangList.setVisible(workspace.isAllowed("windowKategoriBarangList"));
		tabKategoriBarangDetail.setVisible(workspace.isAllowed("windowKategoriBarangDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnEdit"));
		btnSave.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_KategoriBarangMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalKategoriBarang(KategoriBarang originalKategoriBarang) {
		this.originalKategoriBarang = originalKategoriBarang;
	}

	public KategoriBarang getOriginalKategoriBarang() {
		return this.originalKategoriBarang;
	}

	public void setSelectedKategoriBarang(KategoriBarang selectedKategoriBarang) {
		this.selectedKategoriBarang = selectedKategoriBarang;
	}

	public KategoriBarang getSelectedKategoriBarang() {
		return this.selectedKategoriBarang;
	}

	public void setKategoriBarangs(BindingListModelList kategoriBarangs) {
		this.kategoriBarangs = kategoriBarangs;
	}

	public BindingListModelList getKategoriBarangs() {
		return this.kategoriBarangs;
	}

	/* CONTROLLERS */
	public void setKategoriBarangListCtrl(KategoriBarangListCtrl kategoriBarangListCtrl) {
		this.kategoriBarangListCtrl = kategoriBarangListCtrl;
	}

	public KategoriBarangListCtrl getKategoriBarangListCtrl() {
		return this.kategoriBarangListCtrl;
	}

	public void setKategoriBarangDetailCtrl(KategoriBarangDetailCtrl kategoriBarangDetailCtrl) {
		this.kategoriBarangDetailCtrl = kategoriBarangDetailCtrl;
	}

	public KategoriBarangDetailCtrl getKategoriBarangDetailCtrl() {
		return this.kategoriBarangDetailCtrl;
	}

	/* SERVICES */
	public KategoriBarangService getKategoriBarangService() {
		return this.kategoriBarangService;
	}

	public void setKategoriBarangService(KategoriBarangService kategoriBarangService) {
		this.kategoriBarangService = kategoriBarangService;
	}

	/* COMPONENTS and OTHERS */
}
