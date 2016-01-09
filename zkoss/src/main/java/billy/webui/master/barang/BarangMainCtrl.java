package billy.webui.master.barang;

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
import billy.backend.model.Barang;
import billy.backend.model.Wilayah;
import billy.backend.service.BarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class BarangMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BarangMainCtrl.class);

	
	protected Window windowBarangMain; // autowired

	// Tabs
	protected Tabbox tabbox_BarangMain; // autowired
	protected Tab tabBarangList; // autowired
	protected Tab tabBarangDetail; // autowired
	protected Tabpanel tabPanelBarangList; // autowired
	protected Tabpanel tabPanelBarangDetail; // autowired

	// filter components
	protected Checkbox checkbox_BarangList_ShowAll; // autowired
	protected Textbox txtb_Barang_Name; // aurowired
	protected Button button_BarangList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_BarangMain_";
	private ButtonStatusCtrl btnCtrlBarang;
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
	private BarangListCtrl barangListCtrl;
	private BarangDetailCtrl barangDetailCtrl;

	// Databinding
	private Barang selectedBarang;
	private BindingListModelList barangs;

	// ServiceDAOs / Domain Classes
	private BarangService barangService;

	// always a copy from the bean before modifying. Used for reseting
	private Barang originalBarang;

	/**
	 * default constructor.<br>
	 */
	public BarangMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the barang 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowBarangMain(Event event) throws Exception {
		windowBarangMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlBarang = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabBarangList.setSelected(true);

		if (tabPanelBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelBarangList, this, "ModuleMainController", "/WEB-INF/pages/master/barang/barangList.zul");
		}

		// init the buttons for editMode
		btnCtrlBarang.setInitEdit();
	}

	/**
	 * When the tab 'tabBarangList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabBarangList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelBarangList.getFirstChild() != null) {
			tabBarangList.setSelected(true);

			return;
		}

		if (tabPanelBarangList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelBarangList, this, "ModuleMainController", "/WEB-INF/pages//barang/barangList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelBarangDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabBarangDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelBarangDetail.getFirstChild() != null) {
			tabBarangDetail.setSelected(true);

			// refresh the Binding mechanism
			getBarangDetailCtrl().setBarang(getSelectedBarang());
			getBarangDetailCtrl().getBinder().loadAll();
			
			return;
		}

		if (tabPanelBarangDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelBarangDetail, this, "ModuleMainController", "/WEB-INF/pages/master/barang/barangDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_BarangList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_Barang_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Barang> soBarang = new HibernateSearchObject<Barang>(Barang.class, getBarangListCtrl().getCountRows());
		soBarang.addSort("namaBarang", false);

		// Change the BindingListModel.
		if (getBarangListCtrl().getBinder() != null) {
			getBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soBarang);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_BarangMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabBarangList)) {
				tabBarangList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the barang list with 'like barang name'. <br>
	 */
	public void onClick$button_BarangList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Barang_Name.getValue().isEmpty()) {
			checkbox_BarangList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Barang> soBarang = new HibernateSearchObject<Barang>(Barang.class, getBarangListCtrl().getCountRows());
			soBarang.addFilter(new Filter("namaBarang", "%" + txtb_Barang_Name.getValue() + "%", Filter.OP_ILIKE));
			soBarang.addSort("namaBarang", false);

			// Change the BindingListModel.
			if (getBarangListCtrl().getBinder() != null) {
				getBarangListCtrl().getPagedBindingListWrapper().setSearchObject(soBarang);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_BarangMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabBarangList)) {
					tabBarangList.setSelected(true);
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
		if (getBarangDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getBarangDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getBarangDetailCtrl().doReadOnlyMode(true);

			btnCtrlBarang.setInitEdit();
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
		Tab currentTab = tabbox_BarangMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabBarangDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabBarangDetail)) {
			tabBarangDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlBarang.setBtnStatus_Edit();

		getBarangDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getBarangDetailCtrl().getBinder().loadAll();

		// set focus
		getBarangDetailCtrl().txtb_KodeBarang.focus();
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
		if (getBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabBarangDetail, null));
		}

		// check first, if the tabs are created
		if (getBarangDetailCtrl().getBinder() == null) {
			return;
		}

		final Barang anBarang = getSelectedBarang();
		if (anBarang != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anBarang.getKodeBarang();
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
						getBarangService().delete(anBarang);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlBarang.setInitEdit();

		setSelectedBarang(null);
		// refresh the list
		getBarangListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getBarangDetailCtrl().getBinder().loadAll();
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
		getBarangDetailCtrl().getBinder().saveAll();

		try {
			/* if a job type is selected get the object from the listbox */
			Listitem item = getBarangDetailCtrl().lbox_Wilayah.getSelectedItem();

			if (item != null) {
				ListModelList lml1 = (ListModelList) getBarangDetailCtrl().lbox_Wilayah.getListModel();
				Wilayah wilayah = (Wilayah) lml1.get(item.getIndex());
				getBarangDetailCtrl().getBarang().setWilayah(wilayah);				
			}
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getBarangDetailCtrl().getBarang().setLastUpdate(new Date());			
			getBarangDetailCtrl().getBarang().setUpdatedBy(userName);	
			
			Barang barangCheckKode = null;
			barangCheckKode = getBarangService().getBarangByKodeBarang(getBarangDetailCtrl().getBarang().getKodeBarang());
			
			if(barangCheckKode!=null){
				if(barangCheckKode.getId()!=getBarangDetailCtrl().getBarang().getId()){
					ZksampleMessageUtils.showErrorMessage("Kode Barang sudah digunakan oleh barang : " +barangCheckKode.getNamaBarang());
					return;
				}
			}		
			
			// save it to database
			getBarangService().saveOrUpdate(getBarangDetailCtrl().getBarang());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getBarangListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getBarangListCtrl().getListBoxBarang(), getSelectedBarang());

			// show the objects data in the statusBar
			String str = getSelectedBarang().getKodeBarang();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlBarang.setInitEdit();
			getBarangDetailCtrl().doReadOnlyMode(true);
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
		if (getBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabBarangDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Barang anBarang = getBarangService().getNewBarang();

		// set the beans in the related databinded controllers
		getBarangDetailCtrl().setBarang(anBarang);
		getBarangDetailCtrl().setSelectedBarang(anBarang);

		// Refresh the binding mechanism
		getBarangDetailCtrl().setSelectedBarang(getSelectedBarang());
		try{
			getBarangDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}
		// set editable Mode
		getBarangDetailCtrl().doReadOnlyMode(false);
		getBarangDetailCtrl().lbox_Wilayah.setSelectedIndex(-1);
		// set the ButtonStatus to New-Mode
		btnCtrlBarang.setInitNew();

		tabBarangDetail.setSelected(true);
		// set focus
		getBarangDetailCtrl().txtb_KodeBarang.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getBarangListCtrl().getListBoxBarang().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedBarang());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_BarangMain.getSelectedTab();

		if (getBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabBarangDetail, null));
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

		getBarangListCtrl().getListBoxBarang().setSelectedIndex(index);
		setSelectedBarang((Barang) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getBarangListCtrl().getListBoxBarang(), getSelectedBarang()));

		// refresh master-detail MASTERS data
		getBarangDetailCtrl().getBinder().loadAll();

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

		if (tabbox_BarangMain.getSelectedTab() == tabBarangDetail) {
			getBarangDetailCtrl().doFitSize(event);
		} else if (tabbox_BarangMain.getSelectedTab() == tabBarangList) {
			// resize and fill Listbox new
			getBarangListCtrl().doFillListbox();
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

		if (getSelectedBarang() != null) {

			try {
				setOriginalBarang((Barang) ZksampleBeanUtils.cloneBean(getSelectedBarang()));
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

		if (getOriginalBarang() != null) {

			try {
				setSelectedBarang((Barang) ZksampleBeanUtils.cloneBean(getOriginalBarang()));
				// TODO Bug in DataBinder??
				windowBarangMain.invalidate();

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
		button_BarangList_SearchName.setVisible(workspace.isAllowed("button_BarangList_SearchName"));
		tabBarangList.setVisible(workspace.isAllowed("windowBarangList"));
		tabBarangDetail.setVisible(workspace.isAllowed("windowBarangDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_BarangMain_btnEdit"));
		btnNew.setVisible(workspace.isAllowed("button_BarangMain_btnNew"));
		btnDelete.setVisible(workspace.isAllowed("button_BarangMain_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_BarangMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_BarangMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_BarangMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_BarangMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_BarangMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_BarangMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalBarang(Barang originalBarang) {
		this.originalBarang = originalBarang;
	}

	public Barang getOriginalBarang() {
		return this.originalBarang;
	}

	public void setSelectedBarang(Barang selectedBarang) {
		this.selectedBarang = selectedBarang;
	}

	public Barang getSelectedBarang() {
		return this.selectedBarang;
	}

	public void setBarangs(BindingListModelList barangs) {
		this.barangs = barangs;
	}

	public BindingListModelList getBarangs() {
		return this.barangs;
	}

	/* CONTROLLERS */
	public void setBarangListCtrl(BarangListCtrl barangListCtrl) {
		this.barangListCtrl = barangListCtrl;
	}

	public BarangListCtrl getBarangListCtrl() {
		return this.barangListCtrl;
	}

	public void setBarangDetailCtrl(BarangDetailCtrl barangDetailCtrl) {
		this.barangDetailCtrl = barangDetailCtrl;
	}

	public BarangDetailCtrl getBarangDetailCtrl() {
		return this.barangDetailCtrl;
	}

	/* SERVICES */
	public BarangService getBarangService() {
		return this.barangService;
	}

	public void setBarangService(BarangService barangService) {
		this.barangService = barangService;
	}

	/* COMPONENTS and OTHERS */
}
