package billy.webui.master.status;

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
import billy.backend.model.Status;
import billy.backend.service.StatusService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class StatusMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StatusMainCtrl.class);

	
	protected Window windowStatusMain; // autowired

	// Tabs
	protected Tabbox tabbox_StatusMain; // autowired
	protected Tab tabStatusList; // autowired
	protected Tab tabStatusDetail; // autowired
	protected Tabpanel tabPanelStatusList; // autowired
	protected Tabpanel tabPanelStatusDetail; // autowired

	// filter components
	protected Checkbox checkbox_StatusList_ShowAll; // autowired
	protected Textbox txtb_Status_Name; // aurowired
	protected Button button_StatusList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_StatusMain_";
	private ButtonStatusCtrl btnCtrlStatus;
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
	private StatusListCtrl statusListCtrl;
	private StatusDetailCtrl statusDetailCtrl;

	// Databinding
	private Status selectedStatus;
	private BindingListModelList statuss;

	// ServiceDAOs / Domain Classes
	private StatusService statusService;

	// always a copy from the bean before modifying. Used for reseting
	private Status originalStatus;

	/**
	 * default constructor.<br>
	 */
	public StatusMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the status 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowStatusMain(Event event) throws Exception {
		windowStatusMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlStatus = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabStatusList.setSelected(true);

		if (tabPanelStatusList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelStatusList, this, "ModuleMainController", "/WEB-INF/pages/master/status/statusList.zul");
		}

		// init the buttons for editMode
		btnCtrlStatus.setInitEdit();
	}

	/**
	 * When the tab 'tabStatusList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabStatusList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelStatusList.getFirstChild() != null) {
			tabStatusList.setSelected(true);

			return;
		}

		if (tabPanelStatusList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelStatusList, this, "ModuleMainController", "/WEB-INF/pages//status/statusList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelStatusDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabStatusDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelStatusDetail.getFirstChild() != null) {
			tabStatusDetail.setSelected(true);

			// refresh the Binding mechanism
			getStatusDetailCtrl().setStatus(getSelectedStatus());
			getStatusDetailCtrl().getBinder().loadAll();
			
			return;
		}

		if (tabPanelStatusDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelStatusDetail, this, "ModuleMainController", "/WEB-INF/pages/master/status/statusDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_StatusList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_Status_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Status> soStatus = new HibernateSearchObject<Status>(Status.class, getStatusListCtrl().getCountRows());
		soStatus.addSort("deskripsiStatus", false);

		// Change the BindingListModel.
		if (getStatusListCtrl().getBinder() != null) {
			getStatusListCtrl().getPagedBindingListWrapper().setSearchObject(soStatus);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_StatusMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabStatusList)) {
				tabStatusList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the status list with 'like status name'. <br>
	 */
	public void onClick$button_StatusList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Status_Name.getValue().isEmpty()) {
			checkbox_StatusList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Status> soStatus = new HibernateSearchObject<Status>(Status.class, getStatusListCtrl().getCountRows());
			soStatus.addFilter(new Filter("deskripsiStatus", "%" + txtb_Status_Name.getValue() + "%", Filter.OP_ILIKE));
			soStatus.addSort("deskripsiStatus", false);

			// Change the BindingListModel.
			if (getStatusListCtrl().getBinder() != null) {
				getStatusListCtrl().getPagedBindingListWrapper().setSearchObject(soStatus);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_StatusMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabStatusList)) {
					tabStatusList.setSelected(true);
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
		if (getStatusDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getStatusDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getStatusDetailCtrl().doReadOnlyMode(true);

			btnCtrlStatus.setInitEdit();
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
		Tab currentTab = tabbox_StatusMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getStatusDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabStatusDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getStatusDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabStatusDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabStatusDetail)) {
			tabStatusDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlStatus.setBtnStatus_Edit();

		getStatusDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getStatusDetailCtrl().getBinder().loadAll();

		// set focus
		getStatusDetailCtrl().txtb_DeskripsiStatus.focus();
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
		if (getStatusDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabStatusDetail, null));
		}

		// check first, if the tabs are created
		if (getStatusDetailCtrl().getBinder() == null) {
			return;
		}

		final Status anStatus = getSelectedStatus();
		if (anStatus != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anStatus.getDeskripsiStatus();
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
						getStatusService().delete(anStatus);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlStatus.setInitEdit();

		setSelectedStatus(null);
		// refresh the list
		getStatusListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getStatusDetailCtrl().getBinder().loadAll();
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
		getStatusDetailCtrl().getBinder().saveAll();

		try {			
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getStatusDetailCtrl().getStatus().setLastUpdate(new Date());			
			getStatusDetailCtrl().getStatus().setUpdatedBy(userName);			
			// save it to database
			getStatusService().saveOrUpdate(getStatusDetailCtrl().getStatus());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getStatusListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getStatusListCtrl().getListBoxStatus(), getSelectedStatus());

			// show the objects data in the statusBar
			String str = getSelectedStatus().getDeskripsiStatus();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlStatus.setInitEdit();
			getStatusDetailCtrl().doReadOnlyMode(true);
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
		if (getStatusDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabStatusDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getStatusDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabStatusDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Status anStatus = getStatusService().getNewStatus();

		// set the beans in the related databinded controllers
		getStatusDetailCtrl().setStatus(anStatus);
		getStatusDetailCtrl().setSelectedStatus(anStatus);

		// Refresh the binding mechanism
		getStatusDetailCtrl().setSelectedStatus(getSelectedStatus());
		try{
			getStatusDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}

		// set editable Mode
		getStatusDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlStatus.setInitNew();

		tabStatusDetail.setSelected(true);
		// set focus
		getStatusDetailCtrl().txtb_DeskripsiStatus.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getStatusListCtrl().getListBoxStatus().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedStatus());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_StatusMain.getSelectedTab();

		if (getStatusDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabStatusDetail, null));
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

		getStatusListCtrl().getListBoxStatus().setSelectedIndex(index);
		setSelectedStatus((Status) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getStatusListCtrl().getListBoxStatus(), getSelectedStatus()));

		// refresh master-detail MASTERS data
		getStatusDetailCtrl().getBinder().loadAll();

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

		if (tabbox_StatusMain.getSelectedTab() == tabStatusDetail) {
			getStatusDetailCtrl().doFitSize(event);
		} else if (tabbox_StatusMain.getSelectedTab() == tabStatusList) {
			// resize and fill Listbox new
			getStatusListCtrl().doFillListbox();
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

		if (getSelectedStatus() != null) {

			try {
				setOriginalStatus((Status) ZksampleBeanUtils.cloneBean(getSelectedStatus()));
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

		if (getOriginalStatus() != null) {

			try {
				setSelectedStatus((Status) ZksampleBeanUtils.cloneBean(getOriginalStatus()));
				// TODO Bug in DataBinder??
				windowStatusMain.invalidate();

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
		button_StatusList_SearchName.setVisible(workspace.isAllowed("button_StatusList_SearchName"));
		tabStatusList.setVisible(workspace.isAllowed("windowStatusList"));
		tabStatusDetail.setVisible(workspace.isAllowed("windowStatusDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_StatusMain_btnEdit"));
		btnNew.setVisible(workspace.isAllowed("button_StatusMain_btnNew"));
		btnDelete.setVisible(workspace.isAllowed("button_StatusMain_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_StatusMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_StatusMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_StatusMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_StatusMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_StatusMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_StatusMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalStatus(Status originalStatus) {
		this.originalStatus = originalStatus;
	}

	public Status getOriginalStatus() {
		return this.originalStatus;
	}

	public void setSelectedStatus(Status selectedStatus) {
		this.selectedStatus = selectedStatus;
	}

	public Status getSelectedStatus() {
		return this.selectedStatus;
	}

	public void setStatuss(BindingListModelList statuss) {
		this.statuss = statuss;
	}

	public BindingListModelList getStatuss() {
		return this.statuss;
	}

	/* CONTROLLERS */
	public void setStatusListCtrl(StatusListCtrl statusListCtrl) {
		this.statusListCtrl = statusListCtrl;
	}

	public StatusListCtrl getStatusListCtrl() {
		return this.statusListCtrl;
	}

	public void setStatusDetailCtrl(StatusDetailCtrl statusDetailCtrl) {
		this.statusDetailCtrl = statusDetailCtrl;
	}

	public StatusDetailCtrl getStatusDetailCtrl() {
		return this.statusDetailCtrl;
	}

	/* SERVICES */
	public StatusService getStatusService() {
		return this.statusService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	/* COMPONENTS and OTHERS */
}
