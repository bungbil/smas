package billy.webui.master.wilayah;

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
import billy.backend.model.Wilayah;
import billy.backend.service.WilayahService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class WilayahMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(WilayahMainCtrl.class);

	
	protected Window windowWilayahMain; // autowired

	// Tabs
	protected Tabbox tabbox_WilayahMain; // autowired
	protected Tab tabWilayahList; // autowired
	protected Tab tabWilayahDetail; // autowired
	protected Tabpanel tabPanelWilayahList; // autowired
	protected Tabpanel tabPanelWilayahDetail; // autowired

	// filter components
	protected Checkbox checkbox_WilayahList_ShowAll; // autowired
	protected Textbox txtb_Wilayah_Name; // aurowired
	protected Button button_WilayahList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_WilayahMain_";
	private ButtonStatusCtrl btnCtrlWilayah;
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
	private WilayahListCtrl wilayahListCtrl;
	private WilayahDetailCtrl wilayahDetailCtrl;

	// Databinding
	private Wilayah selectedWilayah;
	private BindingListModelList wilayahs;

	// ServiceDAOs / Domain Classes
	private WilayahService wilayahService;

	// always a copy from the bean before modifying. Used for reseting
	private Wilayah originalWilayah;

	/**
	 * default constructor.<br>
	 */
	public WilayahMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the wilayah 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowWilayahMain(Event event) throws Exception {
		windowWilayahMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlWilayah = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabWilayahList.setSelected(true);

		if (tabPanelWilayahList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelWilayahList, this, "ModuleMainController", "/WEB-INF/pages/master/wilayah/wilayahList.zul");
		}

		// init the buttons for editMode
		btnCtrlWilayah.setInitEdit();
	}

	/**
	 * When the tab 'tabWilayahList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabWilayahList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelWilayahList.getFirstChild() != null) {
			tabWilayahList.setSelected(true);

			return;
		}

		if (tabPanelWilayahList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelWilayahList, this, "ModuleMainController", "/WEB-INF/pages//wilayah/wilayahList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelWilayahDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabWilayahDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelWilayahDetail.getFirstChild() != null) {
			tabWilayahDetail.setSelected(true);

			// refresh the Binding mechanism
			getWilayahDetailCtrl().setWilayah(getSelectedWilayah());
			getWilayahDetailCtrl().getBinder().loadAll();
			if(getSelectedWilayah().getStatus().equals(getWilayahDetailCtrl().radioStatusPusat.getLabel())){
				getWilayahDetailCtrl().radioStatusPusat.setSelected(true);
			}
			if(getSelectedWilayah().getStatus().equals(getWilayahDetailCtrl().radioStatusDaerah.getLabel())){
				getWilayahDetailCtrl().radioStatusDaerah.setSelected(true);
			}
			return;
		}

		if (tabPanelWilayahDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelWilayahDetail, this, "ModuleMainController", "/WEB-INF/pages/master/wilayah/wilayahDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_WilayahList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_Wilayah_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Wilayah> soWilayah = new HibernateSearchObject<Wilayah>(Wilayah.class, getWilayahListCtrl().getCountRows());
		soWilayah.addSort("namaWilayah", false);

		// Change the BindingListModel.
		if (getWilayahListCtrl().getBinder() != null) {
			getWilayahListCtrl().getPagedBindingListWrapper().setSearchObject(soWilayah);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_WilayahMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabWilayahList)) {
				tabWilayahList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the wilayah list with 'like wilayah name'. <br>
	 */
	public void onClick$button_WilayahList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Wilayah_Name.getValue().isEmpty()) {
			checkbox_WilayahList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Wilayah> soWilayah = new HibernateSearchObject<Wilayah>(Wilayah.class, getWilayahListCtrl().getCountRows());
			soWilayah.addFilter(new Filter("namaWilayah", "%" + txtb_Wilayah_Name.getValue() + "%", Filter.OP_ILIKE));
			soWilayah.addSort("namaWilayah", false);

			// Change the BindingListModel.
			if (getWilayahListCtrl().getBinder() != null) {
				getWilayahListCtrl().getPagedBindingListWrapper().setSearchObject(soWilayah);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_WilayahMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabWilayahList)) {
					tabWilayahList.setSelected(true);
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
		if (getWilayahDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getWilayahDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getWilayahDetailCtrl().doReadOnlyMode(true);

			btnCtrlWilayah.setInitEdit();
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
		Tab currentTab = tabbox_WilayahMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getWilayahDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabWilayahDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getWilayahDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabWilayahDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabWilayahDetail)) {
			tabWilayahDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlWilayah.setBtnStatus_Edit();

		getWilayahDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getWilayahDetailCtrl().getBinder().loadAll();

		// set focus
		getWilayahDetailCtrl().txtb_KodeWilayah.focus();
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
		if (getWilayahDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabWilayahDetail, null));
		}

		// check first, if the tabs are created
		if (getWilayahDetailCtrl().getBinder() == null) {
			return;
		}

		final Wilayah anWilayah = getSelectedWilayah();
		if (anWilayah != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anWilayah.getKodeWilayah();
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
						getWilayahService().delete(anWilayah);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlWilayah.setInitEdit();

		setSelectedWilayah(null);
		// refresh the list
		getWilayahListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getWilayahDetailCtrl().getBinder().loadAll();
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
		getWilayahDetailCtrl().getBinder().saveAll();

		try {
			if(getWilayahDetailCtrl().radioStatusPusat.isSelected()){
				getWilayahDetailCtrl().getWilayah().setStatus(getWilayahDetailCtrl().radioStatusPusat.getLabel());
			}
			if(getWilayahDetailCtrl().radioStatusDaerah.isSelected()){
				getWilayahDetailCtrl().getWilayah().setStatus(getWilayahDetailCtrl().radioStatusDaerah.getLabel());
			}
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getWilayahDetailCtrl().getWilayah().setLastUpdate(new Date());			
			getWilayahDetailCtrl().getWilayah().setUpdatedBy(userName);			
			// save it to database
			getWilayahService().saveOrUpdate(getWilayahDetailCtrl().getWilayah());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getWilayahListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getWilayahListCtrl().getListBoxWilayah(), getSelectedWilayah());

			// show the objects data in the statusBar
			String str = getSelectedWilayah().getKodeWilayah();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlWilayah.setInitEdit();
			getWilayahDetailCtrl().doReadOnlyMode(true);
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
		if (getWilayahDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabWilayahDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getWilayahDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabWilayahDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Wilayah anWilayah = getWilayahService().getNewWilayah();

		// set the beans in the related databinded controllers
		getWilayahDetailCtrl().setWilayah(anWilayah);
		getWilayahDetailCtrl().setSelectedWilayah(anWilayah);

		// Refresh the binding mechanism
		getWilayahDetailCtrl().setSelectedWilayah(getSelectedWilayah());
		try{
			getWilayahDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}

		// set editable Mode
		getWilayahDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlWilayah.setInitNew();

		tabWilayahDetail.setSelected(true);
		// set focus
		getWilayahDetailCtrl().txtb_KodeWilayah.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getWilayahListCtrl().getListBoxWilayah().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedWilayah());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_WilayahMain.getSelectedTab();

		if (getWilayahDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabWilayahDetail, null));
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

		getWilayahListCtrl().getListBoxWilayah().setSelectedIndex(index);
		setSelectedWilayah((Wilayah) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getWilayahListCtrl().getListBoxWilayah(), getSelectedWilayah()));

		// refresh master-detail MASTERS data
		getWilayahDetailCtrl().getBinder().loadAll();

		if(getSelectedWilayah().getStatus().equals(getWilayahDetailCtrl().radioStatusPusat.getLabel())){
			getWilayahDetailCtrl().radioStatusPusat.setSelected(true);
		}
		if(getSelectedWilayah().getStatus().equals(getWilayahDetailCtrl().radioStatusDaerah.getLabel())){
			getWilayahDetailCtrl().radioStatusDaerah.setSelected(true);
		}
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

		if (tabbox_WilayahMain.getSelectedTab() == tabWilayahDetail) {
			getWilayahDetailCtrl().doFitSize(event);
		} else if (tabbox_WilayahMain.getSelectedTab() == tabWilayahList) {
			// resize and fill Listbox new
			getWilayahListCtrl().doFillListbox();
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

		if (getSelectedWilayah() != null) {

			try {
				setOriginalWilayah((Wilayah) ZksampleBeanUtils.cloneBean(getSelectedWilayah()));
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

		if (getOriginalWilayah() != null) {

			try {
				setSelectedWilayah((Wilayah) ZksampleBeanUtils.cloneBean(getOriginalWilayah()));
				// TODO Bug in DataBinder??
				windowWilayahMain.invalidate();

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
		button_WilayahList_SearchName.setVisible(workspace.isAllowed("button_WilayahList_SearchName"));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalWilayah(Wilayah originalWilayah) {
		this.originalWilayah = originalWilayah;
	}

	public Wilayah getOriginalWilayah() {
		return this.originalWilayah;
	}

	public void setSelectedWilayah(Wilayah selectedWilayah) {
		this.selectedWilayah = selectedWilayah;
	}

	public Wilayah getSelectedWilayah() {
		return this.selectedWilayah;
	}

	public void setWilayahs(BindingListModelList wilayahs) {
		this.wilayahs = wilayahs;
	}

	public BindingListModelList getWilayahs() {
		return this.wilayahs;
	}

	/* CONTROLLERS */
	public void setWilayahListCtrl(WilayahListCtrl wilayahListCtrl) {
		this.wilayahListCtrl = wilayahListCtrl;
	}

	public WilayahListCtrl getWilayahListCtrl() {
		return this.wilayahListCtrl;
	}

	public void setWilayahDetailCtrl(WilayahDetailCtrl wilayahDetailCtrl) {
		this.wilayahDetailCtrl = wilayahDetailCtrl;
	}

	public WilayahDetailCtrl getWilayahDetailCtrl() {
		return this.wilayahDetailCtrl;
	}

	/* SERVICES */
	public WilayahService getWilayahService() {
		return this.wilayahService;
	}

	public void setWilayahService(WilayahService wilayahService) {
		this.wilayahService = wilayahService;
	}

	/* COMPONENTS and OTHERS */
}
