package billy.webui.master.bonustransport;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import billy.backend.model.BonusTransport;
import billy.backend.model.JobType;
import billy.backend.service.BonusTransportService;
import billy.backend.service.JobTypeService;
import billy.webui.master.bonustransport.model.JobTypeListModelItemRenderer;
import de.forsthaus.backend.model.Language;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class BonusTransportMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BonusTransportMainCtrl.class);

	
	protected Window windowBonusTransportMain; // autowired

	// Tabs
	protected Tabbox tabbox_BonusTransportMain; // autowired
	protected Tab tabBonusTransportList; // autowired
	protected Tab tabBonusTransportDetail; // autowired
	protected Tabpanel tabPanelBonusTransportList; // autowired
	protected Tabpanel tabPanelBonusTransportDetail; // autowired

	// filter components
	protected Checkbox checkbox_BonusTransportList_ShowAll; // autowired
	protected Textbox txtb_BonusTransport_Name; // aurowired
	protected Button button_BonusTransportList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_BonusTransportMain_";
	private ButtonStatusCtrl btnCtrlBonusTransport;
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
	private BonusTransportListCtrl bonusTransportListCtrl;
	private BonusTransportDetailCtrl bonusTransportDetailCtrl;

	// Databinding
	private BonusTransport selectedBonusTransport;
	private BindingListModelList bonusTransports;

	// ServiceDAOs / Domain Classes
	private BonusTransportService bonusTransportService;
	
	// always a copy from the bean before modifying. Used for reseting
	private BonusTransport originalBonusTransport;

	/**
	 * default constructor.<br>
	 */
	public BonusTransportMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the bonusTransport 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowBonusTransportMain(Event event) throws Exception {
		windowBonusTransportMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlBonusTransport = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabBonusTransportList.setSelected(true);

		if (tabPanelBonusTransportList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelBonusTransportList, this, "ModuleMainController", "/WEB-INF/pages/master/bonustransport/bonusTransportList.zul");
		}

		// init the buttons for editMode
		btnCtrlBonusTransport.setInitEdit();
	}

	/**
	 * When the tab 'tabBonusTransportList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabBonusTransportList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelBonusTransportList.getFirstChild() != null) {
			tabBonusTransportList.setSelected(true);

			return;
		}

		if (tabPanelBonusTransportList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelBonusTransportList, this, "ModuleMainController", "/WEB-INF/pages//bonusTransport/bonustransportList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelBonusTransportDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabBonusTransportDetail(Event event) throws IOException {
		// logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelBonusTransportDetail.getFirstChild() != null) {
			tabBonusTransportDetail.setSelected(true);

			// refresh the Binding mechanism
			getBonusTransportDetailCtrl().setBonusTransport(getSelectedBonusTransport());
			getBonusTransportDetailCtrl().getBinder().loadAll();
	
			
			return;
		}

		if (tabPanelBonusTransportDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelBonusTransportDetail, this, "ModuleMainController", "/WEB-INF/pages/master/bonustransport/bonusTransportDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_BonusTransportList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_BonusTransport_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<BonusTransport> soBonusTransport = new HibernateSearchObject<BonusTransport>(BonusTransport.class, getBonusTransportListCtrl().getCountRows());
		soBonusTransport.addSort("deskripsiBonusTransport", false);

		// Change the BindingListModel.
		if (getBonusTransportListCtrl().getBinder() != null) {
			getBonusTransportListCtrl().getPagedBindingListWrapper().setSearchObject(soBonusTransport);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_BonusTransportMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabBonusTransportList)) {
				tabBonusTransportList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the bonusTransport list with 'like bonusTransport name'. <br>
	 */
	public void onClick$button_BonusTransportList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_BonusTransport_Name.getValue().isEmpty()) {
			checkbox_BonusTransportList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<BonusTransport> soBonusTransport = new HibernateSearchObject<BonusTransport>(BonusTransport.class, getBonusTransportListCtrl().getCountRows());
			soBonusTransport.addFilter(new Filter("deskripsiBonusTransport", "%" + txtb_BonusTransport_Name.getValue() + "%", Filter.OP_ILIKE));
			soBonusTransport.addSort("deskripsiBonusTransport", false);

			// Change the BindingListModel.
			if (getBonusTransportListCtrl().getBinder() != null) {
				getBonusTransportListCtrl().getPagedBindingListWrapper().setSearchObject(soBonusTransport);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_BonusTransportMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabBonusTransportList)) {
					tabBonusTransportList.setSelected(true);
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
		if (getBonusTransportDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getBonusTransportDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getBonusTransportDetailCtrl().doReadOnlyMode(true);

			btnCtrlBonusTransport.setInitEdit();
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
		Tab currentTab = tabbox_BonusTransportMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getBonusTransportDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabBonusTransportDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getBonusTransportDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabBonusTransportDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabBonusTransportDetail)) {
			tabBonusTransportDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlBonusTransport.setBtnStatus_Edit();

		getBonusTransportDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getBonusTransportDetailCtrl().getBinder().loadAll();

		// set focus
		getBonusTransportDetailCtrl().txtb_DeskripsiBonusTransport.focus();
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
		if (getBonusTransportDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabBonusTransportDetail, null));
		}

		// check first, if the tabs are created
		if (getBonusTransportDetailCtrl().getBinder() == null) {
			return;
		}

		final BonusTransport anBonusTransport = getSelectedBonusTransport();
		if (anBonusTransport != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anBonusTransport.getDeskripsiBonusTransport();
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
						getBonusTransportService().delete(anBonusTransport);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlBonusTransport.setInitEdit();

		setSelectedBonusTransport(null);
		// refresh the list
		getBonusTransportListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getBonusTransportDetailCtrl().getBinder().loadAll();
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
		getBonusTransportDetailCtrl().getBinder().saveAll();

		try {
			/* if a job type is selected get the object from the listbox */
			Listitem item = getBonusTransportDetailCtrl().lbox_JobType.getSelectedItem();

			if (item != null) {
				ListModelList lml1 = (ListModelList) getBonusTransportDetailCtrl().lbox_JobType.getListModel();
				JobType jobType = (JobType) lml1.get(item.getIndex());
				getBonusTransportDetailCtrl().getBonusTransport().setJobType(jobType);				
			}
			
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getBonusTransportDetailCtrl().getBonusTransport().setLastUpdate(new Date());			
			getBonusTransportDetailCtrl().getBonusTransport().setUpdatedBy(userName);			
			// save it to database
			getBonusTransportService().saveOrUpdate(getBonusTransportDetailCtrl().getBonusTransport());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getBonusTransportListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getBonusTransportListCtrl().getListBoxBonusTransport(), getSelectedBonusTransport());

			// show the objects data in the statusBar
			String str = getSelectedBonusTransport().getDeskripsiBonusTransport();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlBonusTransport.setInitEdit();
			getBonusTransportDetailCtrl().doReadOnlyMode(true);
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
		if (getBonusTransportDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabBonusTransportDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getBonusTransportDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabBonusTransportDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final BonusTransport anBonusTransport = getBonusTransportService().getNewBonusTransport();
		
		// set the beans in the related databinded controllers
		getBonusTransportDetailCtrl().setBonusTransport(anBonusTransport);
		getBonusTransportDetailCtrl().setSelectedBonusTransport(anBonusTransport);

		// Refresh the binding mechanism
		getBonusTransportDetailCtrl().setSelectedBonusTransport(getSelectedBonusTransport());
		try{
			getBonusTransportDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}
		// set editable Mode
		getBonusTransportDetailCtrl().doReadOnlyMode(false);
		
		// set listbox
		getBonusTransportDetailCtrl().lbox_JobType.setSelectedIndex(-1);
		
		// set the ButtonStatus to New-Mode
		btnCtrlBonusTransport.setInitNew();

		tabBonusTransportDetail.setSelected(true);
		// set focus
		getBonusTransportDetailCtrl().txtb_DeskripsiBonusTransport.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getBonusTransportListCtrl().getListBoxBonusTransport().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedBonusTransport());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_BonusTransportMain.getSelectedTab();

		if (getBonusTransportDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabBonusTransportDetail, null));
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

		getBonusTransportListCtrl().getListBoxBonusTransport().setSelectedIndex(index);
		setSelectedBonusTransport((BonusTransport) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getBonusTransportListCtrl().getListBoxBonusTransport(), getSelectedBonusTransport()));

		// refresh master-detail MASTERS data
		getBonusTransportDetailCtrl().getBinder().loadAll();

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

		if (tabbox_BonusTransportMain.getSelectedTab() == tabBonusTransportDetail) {
			getBonusTransportDetailCtrl().doFitSize(event);
		} else if (tabbox_BonusTransportMain.getSelectedTab() == tabBonusTransportList) {
			// resize and fill Listbox new
			getBonusTransportListCtrl().doFillListbox();
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

		if (getSelectedBonusTransport() != null) {

			try {
				setOriginalBonusTransport((BonusTransport) ZksampleBeanUtils.cloneBean(getSelectedBonusTransport()));
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

		if (getOriginalBonusTransport() != null) {

			try {
				setSelectedBonusTransport((BonusTransport) ZksampleBeanUtils.cloneBean(getOriginalBonusTransport()));
				// TODO Bug in DataBinder??
				windowBonusTransportMain.invalidate();

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
		button_BonusTransportList_SearchName.setVisible(workspace.isAllowed("button_BonusTransportList_SearchName"));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalBonusTransport(BonusTransport originalBonusTransport) {
		this.originalBonusTransport = originalBonusTransport;
	}

	public BonusTransport getOriginalBonusTransport() {
		return this.originalBonusTransport;
	}

	public void setSelectedBonusTransport(BonusTransport selectedBonusTransport) {
		this.selectedBonusTransport = selectedBonusTransport;
	}

	public BonusTransport getSelectedBonusTransport() {
		return this.selectedBonusTransport;
	}

	public void setBonusTransports(BindingListModelList bonusTransports) {
		this.bonusTransports = bonusTransports;
	}

	public BindingListModelList getBonusTransports() {
		return this.bonusTransports;
	}

	/* CONTROLLERS */
	public void setBonusTransportListCtrl(BonusTransportListCtrl bonusTransportListCtrl) {
		this.bonusTransportListCtrl = bonusTransportListCtrl;
	}

	public BonusTransportListCtrl getBonusTransportListCtrl() {
		return this.bonusTransportListCtrl;
	}

	public void setBonusTransportDetailCtrl(BonusTransportDetailCtrl bonusTransportDetailCtrl) {
		this.bonusTransportDetailCtrl = bonusTransportDetailCtrl;
	}

	public BonusTransportDetailCtrl getBonusTransportDetailCtrl() {
		return this.bonusTransportDetailCtrl;
	}

	/* SERVICES */
	public BonusTransportService getBonusTransportService() {
		return this.bonusTransportService;
	}

	public void setBonusTransportService(BonusTransportService bonusTransportService) {
		this.bonusTransportService = bonusTransportService;
	}


	/* COMPONENTS and OTHERS */
}
