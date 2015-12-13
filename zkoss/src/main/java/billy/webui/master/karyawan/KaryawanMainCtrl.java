package billy.webui.master.karyawan;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

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
import billy.backend.model.JobType;
import billy.backend.model.Karyawan;
import billy.backend.service.KaryawanService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class KaryawanMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(KaryawanMainCtrl.class);

	
	protected Window windowKaryawanMain; // autowired

	// Tabs
	protected Tabbox tabbox_KaryawanMain; // autowired
	protected Tab tabKaryawanList; // autowired
	protected Tab tabKaryawanDetail; // autowired
	protected Tabpanel tabPanelKaryawanList; // autowired
	protected Tabpanel tabPanelKaryawanDetail; // autowired

	// filter components
	protected Checkbox checkbox_KaryawanList_ShowAll; // autowired
	protected Textbox txtb_Karyawan_Name; // aurowired
	protected Button button_KaryawanList_SearchName; // aurowired

	protected Textbox txtb_Karyawan_Ktp; // aurowired
	protected Button button_KaryawanList_SearchKtp; // aurowired


	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_KaryawanMain_";
	private ButtonStatusCtrl btnCtrlKaryawan;
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
	private KaryawanListCtrl karyawanListCtrl;
	private KaryawanDetailCtrl karyawanDetailCtrl;

	// Databinding
	private Karyawan selectedKaryawan;
	private BindingListModelList karyawans;

	// ServiceDAOs / Domain Classes
	private KaryawanService karyawanService;

	// always a copy from the bean before modifying. Used for reseting
	private Karyawan originalKaryawan;

	/**
	 * default constructor.<br>
	 */
	public KaryawanMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the karyawan 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowKaryawanMain(Event event) throws Exception {
		windowKaryawanMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlKaryawan = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabKaryawanList.setSelected(true);

		if (tabPanelKaryawanList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelKaryawanList, this, "ModuleMainController", "/WEB-INF/pages/master/karyawan/karyawanList.zul");
		}

		// init the buttons for editMode
		btnCtrlKaryawan.setInitEdit();
	}

	/**
	 * When the tab 'tabKaryawanList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabKaryawanList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelKaryawanList.getFirstChild() != null) {
			tabKaryawanList.setSelected(true);

			return;
		}

		if (tabPanelKaryawanList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelKaryawanList, this, "ModuleMainController", "/WEB-INF/pages//karyawan/karyawanList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelKaryawanDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabKaryawanDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelKaryawanDetail.getFirstChild() != null) {
			tabKaryawanDetail.setSelected(true);

			// refresh the Binding mechanism
			getKaryawanDetailCtrl().setKaryawan(getSelectedKaryawan());
			getKaryawanDetailCtrl().getBinder().loadAll();
			if(getSelectedKaryawan().getStatusDivisi()!=null){
				if(getSelectedKaryawan().getStatusDivisi().equals(getKaryawanDetailCtrl().radioStatusPusat.getLabel())){
					getKaryawanDetailCtrl().radioStatusPusat.setSelected(true);
				}
				if(getSelectedKaryawan().getStatusDivisi().equals(getKaryawanDetailCtrl().radioStatusDaerah.getLabel())){
					getKaryawanDetailCtrl().radioStatusDaerah.setSelected(true);
				}
			}
			return;
		}

		if (tabPanelKaryawanDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelKaryawanDetail, this, "ModuleMainController", "/WEB-INF/pages/master/karyawan/karyawanDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_KaryawanList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_Karyawan_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Karyawan> soKaryawan = new HibernateSearchObject<Karyawan>(Karyawan.class, getKaryawanListCtrl().getCountRows());
		soKaryawan.addSort("namaKaryawan", false);

		// Change the BindingListModel.
		if (getKaryawanListCtrl().getBinder() != null) {
			getKaryawanListCtrl().getPagedBindingListWrapper().setSearchObject(soKaryawan);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_KaryawanMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabKaryawanList)) {
				tabKaryawanList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the karyawan list with 'like karyawan name'. <br>
	 */
	public void onClick$button_KaryawanList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Karyawan_Name.getValue().isEmpty()) {
			checkbox_KaryawanList_ShowAll.setChecked(false); // unCheck
			txtb_Karyawan_Ktp.setValue("");
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Karyawan> soKaryawan = new HibernateSearchObject<Karyawan>(Karyawan.class, getKaryawanListCtrl().getCountRows());
			soKaryawan.addFilter(new Filter("namaKtp", "%" + txtb_Karyawan_Name.getValue() + "%", Filter.OP_ILIKE));
			soKaryawan.addSort("namaKtp", false);

			// Change the BindingListModel.
			if (getKaryawanListCtrl().getBinder() != null) {
				getKaryawanListCtrl().getPagedBindingListWrapper().setSearchObject(soKaryawan);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_KaryawanMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabKaryawanList)) {
					tabKaryawanList.setSelected(true);
				} else {
					currentTab.setSelected(true);
				}
			}
		}
	}

	/**
	 * Filter the karyawan list with 'like karyawan ktp'. <br>
	 */
	public void onClick$button_KaryawanList_SearchKtp(Event event) throws Exception {
		// logger.debug(event.toString());
		
		// if not empty
		if (!txtb_Karyawan_Ktp.getValue().isEmpty()) {
			checkbox_KaryawanList_ShowAll.setChecked(false); // unCheck
			txtb_Karyawan_Name.setValue("");
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Karyawan> soKaryawan = new HibernateSearchObject<Karyawan>(Karyawan.class, getKaryawanListCtrl().getCountRows());
			soKaryawan.addFilter(new Filter("ktp", "%" + txtb_Karyawan_Ktp.getValue() + "%", Filter.OP_ILIKE));
			soKaryawan.addSort("ktp", false);

			// Change the BindingListModel.
			if (getKaryawanListCtrl().getBinder() != null) {
				getKaryawanListCtrl().getPagedBindingListWrapper().setSearchObject(soKaryawan);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_KaryawanMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabKaryawanList)) {
					tabKaryawanList.setSelected(true);
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
		if (getKaryawanDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getKaryawanDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getKaryawanDetailCtrl().doReadOnlyMode(true);

			btnCtrlKaryawan.setInitEdit();
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
		Tab currentTab = tabbox_KaryawanMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getKaryawanDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabKaryawanDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getKaryawanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabKaryawanDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabKaryawanDetail)) {
			tabKaryawanDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlKaryawan.setBtnStatus_Edit();

		getKaryawanDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getKaryawanDetailCtrl().getBinder().loadAll();

		// set focus
		getKaryawanDetailCtrl().txtb_KodeKaryawan.focus();
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
		if (getKaryawanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabKaryawanDetail, null));
		}

		// check first, if the tabs are created
		if (getKaryawanDetailCtrl().getBinder() == null) {
			return;
		}

		final Karyawan anKaryawan = getSelectedKaryawan();
		if (anKaryawan != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anKaryawan.getKodeKaryawan();
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
						getKaryawanService().delete(anKaryawan);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlKaryawan.setInitEdit();

		setSelectedKaryawan(null);
		// refresh the list
		getKaryawanListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getKaryawanDetailCtrl().getBinder().loadAll();
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
		getKaryawanDetailCtrl().getBinder().saveAll();
		
		boolean duplicateKtp = false;
		Karyawan karyawanCheckKtp = null;
		karyawanCheckKtp = getKaryawanService().getKaryawanByKtp(getKaryawanDetailCtrl().getKaryawan().getKtp());
		
		
		if(karyawanCheckKtp!=null){
			if(karyawanCheckKtp.getId()!=getKaryawanDetailCtrl().getKaryawan().getId()){
				ZksampleMessageUtils.showErrorMessage("No KTP sudah digunakan oleh karyawan bernama panggilan : " +karyawanCheckKtp.getNamaPanggilan());
				return;
			}
		}		
		
		try {
			
			if(getKaryawanDetailCtrl().radioStatusPusat.isSelected()){
				getKaryawanDetailCtrl().getKaryawan().setStatusDivisi(getKaryawanDetailCtrl().radioStatusPusat.getLabel());
			}
			if(getKaryawanDetailCtrl().radioStatusDaerah.isSelected()){
				getKaryawanDetailCtrl().getKaryawan().setStatusDivisi(getKaryawanDetailCtrl().radioStatusDaerah.getLabel());
			}
			
			/* if a job type is selected get the object from the listbox */
			Listitem item = getKaryawanDetailCtrl().lbox_JobType.getSelectedItem();

			if (item != null) {
				ListModelList lml1 = (ListModelList) getKaryawanDetailCtrl().lbox_JobType.getListModel();
				JobType jobType = (JobType) lml1.get(item.getIndex());
				getKaryawanDetailCtrl().getKaryawan().setJobType(jobType);				
			}
			
			Listitem item2 = getKaryawanDetailCtrl().lbox_SupervisorDivisi.getSelectedItem();

			if (item2 != null) {
				ListModelList lml2 = (ListModelList) getKaryawanDetailCtrl().lbox_SupervisorDivisi.getListModel();
				Karyawan karyawan = (Karyawan) lml2.get(item2.getIndex());
				getKaryawanDetailCtrl().getKaryawan().setSupervisorDivisi(karyawan);				
			}
			
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getKaryawanDetailCtrl().getKaryawan().setLastUpdate(new Date());			
			getKaryawanDetailCtrl().getKaryawan().setUpdatedBy(userName);			
			// save it to database
			getKaryawanService().saveOrUpdate(getKaryawanDetailCtrl().getKaryawan());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getKaryawanListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getKaryawanListCtrl().getListBoxKaryawan(), getSelectedKaryawan());

			// show the objects data in the statusBar
			String str = getSelectedKaryawan().getKodeKaryawan();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlKaryawan.setInitEdit();
			getKaryawanDetailCtrl().doReadOnlyMode(true);
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
		if (getKaryawanDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabKaryawanDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getKaryawanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabKaryawanDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Karyawan anKaryawan = getKaryawanService().getNewKaryawan();

		// set the beans in the related databinded controllers
		getKaryawanDetailCtrl().setKaryawan(anKaryawan);
		getKaryawanDetailCtrl().setSelectedKaryawan(anKaryawan);

		// Refresh the binding mechanism
		getKaryawanDetailCtrl().setSelectedKaryawan(getSelectedKaryawan());
		try{
			getKaryawanDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}

		// set editable Mode
		getKaryawanDetailCtrl().doReadOnlyMode(false);
		
		// set listbox
		getKaryawanDetailCtrl().lbox_JobType.setSelectedIndex(-1);
		getKaryawanDetailCtrl().lbox_SupervisorDivisi.setSelectedIndex(-1);
		getKaryawanDetailCtrl().lbox_SupervisorDivisi.setVisible(false);
		getKaryawanDetailCtrl().label_SupervisorDivisi.setVisible(false);
		
		getKaryawanDetailCtrl().txtb_InisialDivisi.setVisible(false);
		getKaryawanDetailCtrl().label_InisialDivisi.setVisible(false);
		
		getKaryawanDetailCtrl().radiogroup_Status.setVisible(false);
		getKaryawanDetailCtrl().label_StatusDivisi.setVisible(false);
		
		
		// set the ButtonStatus to New-Mode
		btnCtrlKaryawan.setInitNew();

		tabKaryawanDetail.setSelected(true);
		// set focus
		getKaryawanDetailCtrl().txtb_KodeKaryawan.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getKaryawanListCtrl().getListBoxKaryawan().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedKaryawan());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_KaryawanMain.getSelectedTab();

		if (getKaryawanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabKaryawanDetail, null));
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

		getKaryawanListCtrl().getListBoxKaryawan().setSelectedIndex(index);
		setSelectedKaryawan((Karyawan) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getKaryawanListCtrl().getListBoxKaryawan(), getSelectedKaryawan()));

		// refresh master-detail MASTERS data
		getKaryawanDetailCtrl().getBinder().loadAll();
		
		getKaryawanDetailCtrl().loadListBox();
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

		if (tabbox_KaryawanMain.getSelectedTab() == tabKaryawanDetail) {
			getKaryawanDetailCtrl().doFitSize(event);
		} else if (tabbox_KaryawanMain.getSelectedTab() == tabKaryawanList) {
			// resize and fill Listbox new
			getKaryawanListCtrl().doFillListbox();
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

		if (getSelectedKaryawan() != null) {

			try {
				setOriginalKaryawan((Karyawan) ZksampleBeanUtils.cloneBean(getSelectedKaryawan()));
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

		if (getOriginalKaryawan() != null) {

			try {
				setSelectedKaryawan((Karyawan) ZksampleBeanUtils.cloneBean(getOriginalKaryawan()));
				// TODO Bug in DataBinder??
				windowKaryawanMain.invalidate();

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
		button_KaryawanList_SearchName.setVisible(workspace.isAllowed("button_KaryawanList_SearchName"));
		tabKaryawanList.setVisible(workspace.isAllowed("windowKaryawanList"));
		tabKaryawanDetail.setVisible(workspace.isAllowed("windowKaryawanDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_KaryawanMain_btnEdit"));
		btnSave.setVisible(workspace.isAllowed("button_KaryawanMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_KaryawanMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_KaryawanMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_KaryawanMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_KaryawanMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_KaryawanMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalKaryawan(Karyawan originalKaryawan) {
		this.originalKaryawan = originalKaryawan;
	}

	public Karyawan getOriginalKaryawan() {
		return this.originalKaryawan;
	}

	public void setSelectedKaryawan(Karyawan selectedKaryawan) {
		this.selectedKaryawan = selectedKaryawan;
	}

	public Karyawan getSelectedKaryawan() {
		return this.selectedKaryawan;
	}

	public void setKaryawans(BindingListModelList karyawans) {
		this.karyawans = karyawans;
	}

	public BindingListModelList getKaryawans() {
		return this.karyawans;
	}

	/* CONTROLLERS */
	public void setKaryawanListCtrl(KaryawanListCtrl karyawanListCtrl) {
		this.karyawanListCtrl = karyawanListCtrl;
	}

	public KaryawanListCtrl getKaryawanListCtrl() {
		return this.karyawanListCtrl;
	}

	public void setKaryawanDetailCtrl(KaryawanDetailCtrl karyawanDetailCtrl) {
		this.karyawanDetailCtrl = karyawanDetailCtrl;
	}

	public KaryawanDetailCtrl getKaryawanDetailCtrl() {
		return this.karyawanDetailCtrl;
	}

	/* SERVICES */
	public KaryawanService getKaryawanService() {
		return this.karyawanService;
	}

	public void setKaryawanService(KaryawanService karyawanService) {
		this.karyawanService = karyawanService;
	}

	/* COMPONENTS and OTHERS */
}
