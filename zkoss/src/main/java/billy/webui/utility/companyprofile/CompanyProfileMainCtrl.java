package billy.webui.utility.companyprofile;

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
import billy.backend.model.CompanyProfile;
import billy.backend.service.CompanyProfileService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CompanyProfileMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CompanyProfileMainCtrl.class);
	
	protected Window windowCompanyProfileMain; // autowired

	// Tabs
	protected Tabbox tabbox_CompanyProfileMain; // autowired
	protected Tab tabCompanyProfileList; // autowired
	protected Tab tabCompanyProfileDetail; // autowired
	protected Tabpanel tabPanelCompanyProfileDetail; // autowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_CompanyProfileMain_";
	private ButtonStatusCtrl btnCtrlCompanyProfile;
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
	private CompanyProfileDetailCtrl companyProfileDetailCtrl;

	// Databinding
	private CompanyProfile selectedCompanyProfile;
	private BindingListModelList companyProfiles;

	// ServiceDAOs / Domain Classes
	private CompanyProfileService companyProfileService;

	// always a copy from the bean before modifying. Used for reseting
	private CompanyProfile originalCompanyProfile;

	/**
	 * default constructor.<br>
	 */
	public CompanyProfileMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the companyProfile 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowCompanyProfileMain(Event event) throws Exception {
		windowCompanyProfileMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlCompanyProfile = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabCompanyProfileDetail.setSelected(true);

		if (tabCompanyProfileDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelCompanyProfileDetail, this, "ModuleMainController", "/WEB-INF/pages/utility/companyProfile/companyProfileDetail.zul");
		}

		// init the buttons for editMode
		btnCtrlCompanyProfile.setInitEdit();
	}

	

	/**
	 * When the tab 'tabPanelCompanyProfileDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabCompanyProfileDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelCompanyProfileDetail.getFirstChild() != null) {
			tabCompanyProfileDetail.setSelected(true);

			// refresh the Binding mechanism
			getCompanyProfileDetailCtrl().setCompanyProfile(getCompanyProfileService().getCompanyProfileByID(new Long(1)));
			getCompanyProfileDetailCtrl().getBinder().loadAll();
			return;
		}

		if (tabPanelCompanyProfileDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelCompanyProfileDetail, this, "ModuleMainController", "/WEB-INF/pages/utility/companyProfile/companyProfileDetail.zul");
		}
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
		if (getCompanyProfileDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getCompanyProfileDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getCompanyProfileDetailCtrl().doReadOnlyMode(true);

			btnCtrlCompanyProfile.setInitEdit();
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
		Tab currentTab = tabbox_CompanyProfileMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getCompanyProfileDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabCompanyProfileDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getCompanyProfileDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabCompanyProfileDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabCompanyProfileDetail)) {
			tabCompanyProfileDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlCompanyProfile.setBtnStatus_Edit();

		getCompanyProfileDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getCompanyProfileDetailCtrl().getBinder().loadAll();

		// set focus
		getCompanyProfileDetailCtrl().txtb_CompanyName.focus();
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
		getCompanyProfileDetailCtrl().getBinder().saveAll();

		try {
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getCompanyProfileDetailCtrl().getCompanyProfile().setLastUpdate(new Date());			
			getCompanyProfileDetailCtrl().getCompanyProfile().setUpdatedBy(userName);
			// save it to database
			getCompanyProfileService().saveOrUpdate(getCompanyProfileDetailCtrl().getCompanyProfile());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			
		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlCompanyProfile.setInitEdit();
			getCompanyProfileDetailCtrl().doReadOnlyMode(true);
		}
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

		if (tabbox_CompanyProfileMain.getSelectedTab() == tabCompanyProfileDetail) {
			getCompanyProfileDetailCtrl().doFitSize(event);
		} 
	}

	
	/**
	 * Saves the selected object's current properties. We can get them back if a
	 * modification is canceled.
	 * 
	 * @see doResetToInitValues()
	 */
	public void doStoreInitValues() {

		if (getSelectedCompanyProfile() != null) {

			try {
				setOriginalCompanyProfile((CompanyProfile) ZksampleBeanUtils.cloneBean(getSelectedCompanyProfile()));
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

		if (getOriginalCompanyProfile() != null) {

			try {
				setSelectedCompanyProfile((CompanyProfile) ZksampleBeanUtils.cloneBean(getOriginalCompanyProfile()));
				// TODO Bug in DataBinder??
				windowCompanyProfileMain.invalidate();

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
		
		//tabCompanyProfileList.setVisible(workspace.isAllowed("windowCompanyProfileList"));
		tabCompanyProfileDetail.setVisible(workspace.isAllowed("windowCompanyProfileDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnEdit"));
		//btnNew.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnNew"));
		//btnDelete.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnCancel"));
		//btnFirst.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnFirst"));
		//btnPrevious.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnPrevious"));
		//btnNext.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnNext"));
		//btnLast.setVisible(workspace.isAllowed("button_CompanyProfileMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalCompanyProfile(CompanyProfile originalCompanyProfile) {
		this.originalCompanyProfile = originalCompanyProfile;
	}

	public CompanyProfile getOriginalCompanyProfile() {
		return this.originalCompanyProfile;
	}

	public void setSelectedCompanyProfile(CompanyProfile selectedCompanyProfile) {
		this.selectedCompanyProfile = selectedCompanyProfile;
	}

	public CompanyProfile getSelectedCompanyProfile() {
		return this.selectedCompanyProfile;
	}

	public void setCompanyProfiles(BindingListModelList companyProfiles) {
		this.companyProfiles = companyProfiles;
	}

	public BindingListModelList getCompanyProfiles() {
		return this.companyProfiles;
	}

	/* CONTROLLERS */	

	public void setCompanyProfileDetailCtrl(CompanyProfileDetailCtrl companyProfileDetailCtrl) {
		this.companyProfileDetailCtrl = companyProfileDetailCtrl;
	}

	public CompanyProfileDetailCtrl getCompanyProfileDetailCtrl() {
		return this.companyProfileDetailCtrl;
	}

	/* SERVICES */
	public CompanyProfileService getCompanyProfileService() {
		return this.companyProfileService;
	}

	public void setCompanyProfileService(CompanyProfileService companyProfileService) {
		this.companyProfileService = companyProfileService;
	}

	/* COMPONENTS and OTHERS */
}
