package billy.webui.master.hargabarang;

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
import billy.backend.model.HargaBarang;
import billy.backend.service.HargaBarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class HargaBarangMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(HargaBarangMainCtrl.class);

	
	protected Window windowHargaBarangMain; // autowired

	// Tabs
	protected Tabbox tabbox_HargaBarangMain; // autowired
	protected Tab tabHargaBarangList; // autowired
	protected Tab tabHargaBarangDetail; // autowired
	protected Tabpanel tabPanelHargaBarangList; // autowired
	protected Tabpanel tabPanelHargaBarangDetail; // autowired

	// filter components
	protected Checkbox checkbox_HargaBarangList_ShowAll; // autowired
	protected Textbox txtb_HargaBarang_Name; // aurowired
	protected Button button_HargaBarangList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_HargaBarangMain_";
	private ButtonStatusCtrl btnCtrlHargaBarang;
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
	private HargaBarangListCtrl hargaBarangListCtrl;
	private HargaBarangDetailCtrl hargaBarangDetailCtrl;

	// Databinding
	private HargaBarang selectedHargaBarang;
	private BindingListModelList hargaBarangs;

	// ServiceDAOs / Domain Classes
	private HargaBarangService hargaBarangService;

	// always a copy from the bean before modifying. Used for reseting
	private HargaBarang originalHargaBarang;

	/**
	 * default constructor.<br>
	 */
	public HargaBarangMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the hargaBarang 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowHargaBarangMain(Event event) throws Exception {
		windowHargaBarangMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlHargaBarang = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabHargaBarangList.setSelected(true);

		if (tabPanelHargaBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelHargaBarangList, this, "ModuleMainController", "/WEB-INF/pages/master/hargaBarang/hargaBarangList.zul");
		}

		// init the buttons for editMode
		btnCtrlHargaBarang.setInitEdit();
	}

	/**
	 * When the tab 'tabHargaBarangList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabHargaBarangList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelHargaBarangList.getFirstChild() != null) {
			tabHargaBarangList.setSelected(true);

			return;
		}

		if (tabPanelHargaBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelHargaBarangList, this, "ModuleMainController", "/WEB-INF/pages//hargaBarang/hargaBarangList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelHargaBarangDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabHargaBarangDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelHargaBarangDetail.getFirstChild() != null) {
			tabHargaBarangDetail.setSelected(true);

			// refresh the Binding mechanism
			getHargaBarangDetailCtrl().setHargaBarang(getSelectedHargaBarang());
			getHargaBarangDetailCtrl().getBinder().loadAll();
			if(getSelectedHargaBarang().getStatus().equals(getHargaBarangDetailCtrl().radioStatusPusat.getLabel())){
				getHargaBarangDetailCtrl().radioStatusPusat.setSelected(true);
			}
			if(getSelectedHargaBarang().getStatus().equals(getHargaBarangDetailCtrl().radioStatusDaerah.getLabel())){
				getHargaBarangDetailCtrl().radioStatusDaerah.setSelected(true);
			}
			return;
		}

		if (tabPanelHargaBarangDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelHargaBarangDetail, this, "ModuleMainController", "/WEB-INF/pages/master/hargaBarang/hargaBarangDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_HargaBarangList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_HargaBarang_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<HargaBarang> soHargaBarang = new HibernateSearchObject<HargaBarang>(HargaBarang.class, getHargaBarangListCtrl().getCountRows());
		soHargaBarang.addSort("namaHargaBarang", false);

		// Change the BindingListModel.
		if (getHargaBarangListCtrl().getBinder() != null) {
			getHargaBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soHargaBarang);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_HargaBarangMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabHargaBarangList)) {
				tabHargaBarangList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the hargaBarang list with 'like hargaBarang name'. <br>
	 */
	public void onClick$button_HargaBarangList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_HargaBarang_Name.getValue().isEmpty()) {
			checkbox_HargaBarangList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<HargaBarang> soHargaBarang = new HibernateSearchObject<HargaBarang>(HargaBarang.class, getHargaBarangListCtrl().getCountRows());
			soHargaBarang.addFilter(new Filter("namaHargaBarang", "%" + txtb_HargaBarang_Name.getValue() + "%", Filter.OP_ILIKE));
			soHargaBarang.addSort("namaHargaBarang", false);

			// Change the BindingListModel.
			if (getHargaBarangListCtrl().getBinder() != null) {
				getHargaBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soHargaBarang);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_HargaBarangMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabHargaBarangList)) {
					tabHargaBarangList.setSelected(true);
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
		if (getHargaBarangDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getHargaBarangDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getHargaBarangDetailCtrl().doReadOnlyMode(true);

			btnCtrlHargaBarang.setInitEdit();
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
		Tab currentTab = tabbox_HargaBarangMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getHargaBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabHargaBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getHargaBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabHargaBarangDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabHargaBarangDetail)) {
			tabHargaBarangDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlHargaBarang.setBtnStatus_Edit();

		getHargaBarangDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getHargaBarangDetailCtrl().getBinder().loadAll();

		// set focus
		getHargaBarangDetailCtrl().txtb_KodeHargaBarang.focus();
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
		if (getHargaBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabHargaBarangDetail, null));
		}

		// check first, if the tabs are created
		if (getHargaBarangDetailCtrl().getBinder() == null) {
			return;
		}

		final HargaBarang anHargaBarang = getSelectedHargaBarang();
		if (anHargaBarang != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anHargaBarang.getKodeHargaBarang();
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
						getHargaBarangService().delete(anHargaBarang);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlHargaBarang.setInitEdit();

		setSelectedHargaBarang(null);
		// refresh the list
		getHargaBarangListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getHargaBarangDetailCtrl().getBinder().loadAll();
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
		getHargaBarangDetailCtrl().getBinder().saveAll();

		try {
			if(getHargaBarangDetailCtrl().radioStatusPusat.isSelected()){
				getHargaBarangDetailCtrl().getHargaBarang().setStatus(getHargaBarangDetailCtrl().radioStatusPusat.getLabel());
			}
			if(getHargaBarangDetailCtrl().radioStatusDaerah.isSelected()){
				getHargaBarangDetailCtrl().getHargaBarang().setStatus(getHargaBarangDetailCtrl().radioStatusDaerah.getLabel());
			}
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getHargaBarangDetailCtrl().getHargaBarang().setLastUpdate(new Date());			
			getHargaBarangDetailCtrl().getHargaBarang().setUpdatedBy(userName);			
			// save it to database
			getHargaBarangService().saveOrUpdate(getHargaBarangDetailCtrl().getHargaBarang());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getHargaBarangListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getHargaBarangListCtrl().getListBoxHargaBarang(), getSelectedHargaBarang());

			// show the objects data in the statusBar
			String str = getSelectedHargaBarang().getKodeHargaBarang();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlHargaBarang.setInitEdit();
			getHargaBarangDetailCtrl().doReadOnlyMode(true);
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
		if (getHargaBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabHargaBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getHargaBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabHargaBarangDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final HargaBarang anHargaBarang = getHargaBarangService().getNewHargaBarang();

		// set the beans in the related databinded controllers
		getHargaBarangDetailCtrl().setHargaBarang(anHargaBarang);
		getHargaBarangDetailCtrl().setSelectedHargaBarang(anHargaBarang);

		// Refresh the binding mechanism
		getHargaBarangDetailCtrl().setSelectedHargaBarang(getSelectedHargaBarang());
		try{
			getHargaBarangDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}
		// set editable Mode
		getHargaBarangDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlHargaBarang.setInitNew();

		tabHargaBarangDetail.setSelected(true);
		// set focus
		getHargaBarangDetailCtrl().txtb_KodeHargaBarang.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getHargaBarangListCtrl().getListBoxHargaBarang().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedHargaBarang());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_HargaBarangMain.getSelectedTab();

		if (getHargaBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabHargaBarangDetail, null));
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

		getHargaBarangListCtrl().getListBoxHargaBarang().setSelectedIndex(index);
		setSelectedHargaBarang((HargaBarang) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getHargaBarangListCtrl().getListBoxHargaBarang(), getSelectedHargaBarang()));

		// refresh master-detail MASTERS data
		getHargaBarangDetailCtrl().getBinder().loadAll();

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

		if (tabbox_HargaBarangMain.getSelectedTab() == tabHargaBarangDetail) {
			getHargaBarangDetailCtrl().doFitSize(event);
		} else if (tabbox_HargaBarangMain.getSelectedTab() == tabHargaBarangList) {
			// resize and fill Listbox new
			getHargaBarangListCtrl().doFillListbox();
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

		if (getSelectedHargaBarang() != null) {

			try {
				setOriginalHargaBarang((HargaBarang) ZksampleBeanUtils.cloneBean(getSelectedHargaBarang()));
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

		if (getOriginalHargaBarang() != null) {

			try {
				setSelectedHargaBarang((HargaBarang) ZksampleBeanUtils.cloneBean(getOriginalHargaBarang()));
				// TODO Bug in DataBinder??
				windowHargaBarangMain.invalidate();

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
		button_HargaBarangList_SearchName.setVisible(workspace.isAllowed("button_HargaBarangList_SearchName"));
		tabHargaBarangList.setVisible(workspace.isAllowed("windowHargaBarangList"));
		tabHargaBarangDetail.setVisible(workspace.isAllowed("windowHargaBarangDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_HargaBarangMain_btnEdit"));
		btnSave.setVisible(workspace.isAllowed("button_HargaBarangMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_HargaBarangMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_HargaBarangMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_HargaBarangMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_HargaBarangMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_HargaBarangMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalHargaBarang(HargaBarang originalHargaBarang) {
		this.originalHargaBarang = originalHargaBarang;
	}

	public HargaBarang getOriginalHargaBarang() {
		return this.originalHargaBarang;
	}

	public void setSelectedHargaBarang(HargaBarang selectedHargaBarang) {
		this.selectedHargaBarang = selectedHargaBarang;
	}

	public HargaBarang getSelectedHargaBarang() {
		return this.selectedHargaBarang;
	}

	public void setHargaBarangs(BindingListModelList hargaBarangs) {
		this.hargaBarangs = hargaBarangs;
	}

	public BindingListModelList getHargaBarangs() {
		return this.hargaBarangs;
	}

	/* CONTROLLERS */
	public void setHargaBarangListCtrl(HargaBarangListCtrl hargaBarangListCtrl) {
		this.hargaBarangListCtrl = hargaBarangListCtrl;
	}

	public HargaBarangListCtrl getHargaBarangListCtrl() {
		return this.hargaBarangListCtrl;
	}

	public void setHargaBarangDetailCtrl(HargaBarangDetailCtrl hargaBarangDetailCtrl) {
		this.hargaBarangDetailCtrl = hargaBarangDetailCtrl;
	}

	public HargaBarangDetailCtrl getHargaBarangDetailCtrl() {
		return this.hargaBarangDetailCtrl;
	}

	/* SERVICES */
	public HargaBarangService getHargaBarangService() {
		return this.hargaBarangService;
	}

	public void setHargaBarangService(HargaBarangService hargaBarangService) {
		this.hargaBarangService = hargaBarangService;
	}

	/* COMPONENTS and OTHERS */
}
