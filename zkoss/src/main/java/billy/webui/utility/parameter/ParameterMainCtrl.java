package billy.webui.utility.parameter;

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
import billy.backend.model.Parameter;
import billy.backend.service.ParameterService;
import billy.webui.utility.parameter.report.ParameterSimpleDJReport;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ParameterMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ParameterMainCtrl.class);

	
	protected Window windowParameterMain; // autowired

	// Tabs
	protected Tabbox tabbox_ParameterMain; // autowired
	protected Tab tabParameterList; // autowired
	protected Tab tabParameterDetail; // autowired
	protected Tabpanel tabPanelParameterList; // autowired
	protected Tabpanel tabPanelParameterDetail; // autowired

	// filter components
	protected Checkbox checkbox_ParameterList_ShowAll; // autowired
	protected Textbox txtb_Parameter_Name; // aurowired
	protected Button button_ParameterList_SearchName; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_ParameterMain_";
	private ButtonStatusCtrl btnCtrlParameter;
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
	private ParameterListCtrl parameterListCtrl;
	private ParameterDetailCtrl parameterDetailCtrl;

	// Databinding
	private Parameter selectedParameter;
	private BindingListModelList parameters;

	// ServiceDAOs / Domain Classes
	private ParameterService parameterService;

	// always a copy from the bean before modifying. Used for reseting
	private Parameter originalParameter;

	/**
	 * default constructor.<br>
	 */
	public ParameterMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the parameter 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowParameterMain(Event event) throws Exception {
		windowParameterMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlParameter = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabParameterList.setSelected(true);

		if (tabPanelParameterList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelParameterList, this, "ModuleMainController", "/WEB-INF/pages/utility/parameter/parameterList.zul");
		}

		// init the buttons for editMode
		btnCtrlParameter.setInitEdit();
	}

	/**
	 * When the tab 'tabParameterList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabParameterList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelParameterList.getFirstChild() != null) {
			tabParameterList.setSelected(true);

			return;
		}

		if (tabPanelParameterList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelParameterList, this, "ModuleMainController", "/WEB-INF/pages/utility/parameter/parameterList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelParameterDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabParameterDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelParameterDetail.getFirstChild() != null) {
			tabParameterDetail.setSelected(true);

			// refresh the Binding mechanism
			getParameterDetailCtrl().setParameter(getSelectedParameter());
			getParameterDetailCtrl().getBinder().loadAll();
			return;
		}

		if (tabPanelParameterDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelParameterDetail, this, "ModuleMainController", "/WEB-INF/pages/utility/parameter/parameterDetail.zul");
		}
	}

	/**
	 * when the "print parameters list" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		new ParameterSimpleDJReport(win);
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_ParameterList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_Parameter_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Parameter> soParameter = new HibernateSearchObject<Parameter>(Parameter.class, getParameterListCtrl().getCountRows());
		soParameter.addSort("paramName", false);

		// Change the BindingListModel.
		if (getParameterListCtrl().getBinder() != null) {
			getParameterListCtrl().getPagedBindingListWrapper().setSearchObject(soParameter);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_ParameterMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabParameterList)) {
				tabParameterList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the parameter list with 'like parameter name'. <br>
	 */
	public void onClick$button_ParameterList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Parameter_Name.getValue().isEmpty()) {
			checkbox_ParameterList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Parameter> soParameter = new HibernateSearchObject<Parameter>(Parameter.class, getParameterListCtrl().getCountRows());
			soParameter.addFilter(new Filter("paramName", "%" + txtb_Parameter_Name.getValue() + "%", Filter.OP_ILIKE));
			soParameter.addSort("paramName", false);

			// Change the BindingListModel.
			if (getParameterListCtrl().getBinder() != null) {
				getParameterListCtrl().getPagedBindingListWrapper().setSearchObject(soParameter);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_ParameterMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabParameterList)) {
					tabParameterList.setSelected(true);
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
		if (getParameterDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getParameterDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getParameterDetailCtrl().doReadOnlyMode(true);

			btnCtrlParameter.setInitEdit();
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
		Tab currentTab = tabbox_ParameterMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getParameterDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabParameterDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getParameterDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabParameterDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabParameterDetail)) {
			tabParameterDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlParameter.setBtnStatus_Edit();

		getParameterDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getParameterDetailCtrl().getBinder().loadAll();

		// set focus
		getParameterDetailCtrl().txtb_ParamName.focus();
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
		if (getParameterDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabParameterDetail, null));
		}

		// check first, if the tabs are created
		if (getParameterDetailCtrl().getBinder() == null) {
			return;
		}

		final Parameter anParameter = getSelectedParameter();
		if (anParameter != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anParameter.getParamName();
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
						getParameterService().delete(anParameter);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlParameter.setInitEdit();

		setSelectedParameter(null);
		// refresh the list
		getParameterListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getParameterDetailCtrl().getBinder().loadAll();
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
		getParameterDetailCtrl().getBinder().saveAll();

		try {
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();			
			getParameterDetailCtrl().getParameter().setLastUpdate(new Date());			
			getParameterDetailCtrl().getParameter().setUpdatedBy(userName);
			// save it to database
			getParameterService().saveOrUpdate(getParameterDetailCtrl().getParameter());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getParameterListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getParameterListCtrl().getListBoxParameter(), getSelectedParameter());

			// show the objects data in the statusBar
			String str = getSelectedParameter().getParamName();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlParameter.setInitEdit();
			getParameterDetailCtrl().doReadOnlyMode(true);
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
		if (getParameterDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabParameterDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getParameterDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabParameterDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Parameter anParameter = getParameterService().getNewParameter();

		// set the beans in the related databinded controllers
		getParameterDetailCtrl().setParameter(anParameter);
		getParameterDetailCtrl().setSelectedParameter(anParameter);

		// Refresh the binding mechanism
		getParameterDetailCtrl().setSelectedParameter(getSelectedParameter());
		try{
			getParameterDetailCtrl().getBinder().loadAll();
		}catch(Exception e){
			//do nothing
		}

		// set editable Mode
		getParameterDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlParameter.setInitNew();

		tabParameterDetail.setSelected(true);
		// set focus
		getParameterDetailCtrl().txtb_ParamName.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getParameterListCtrl().getListBoxParameter().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedParameter());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_ParameterMain.getSelectedTab();

		if (getParameterDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabParameterDetail, null));
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

		getParameterListCtrl().getListBoxParameter().setSelectedIndex(index);
		setSelectedParameter((Parameter) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getParameterListCtrl().getListBoxParameter(), getSelectedParameter()));

		// refresh master-detail MASTERS data
		getParameterDetailCtrl().getBinder().loadAll();

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

		if (tabbox_ParameterMain.getSelectedTab() == tabParameterDetail) {
			getParameterDetailCtrl().doFitSize(event);
		} else if (tabbox_ParameterMain.getSelectedTab() == tabParameterList) {
			// resize and fill Listbox new
			getParameterListCtrl().doFillListbox();
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

		if (getSelectedParameter() != null) {

			try {
				setOriginalParameter((Parameter) ZksampleBeanUtils.cloneBean(getSelectedParameter()));
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

		if (getOriginalParameter() != null) {

			try {
				setSelectedParameter((Parameter) ZksampleBeanUtils.cloneBean(getOriginalParameter()));
				// TODO Bug in DataBinder??
				windowParameterMain.invalidate();

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
		button_ParameterList_SearchName.setVisible(workspace.isAllowed("button_ParameterList_SearchName"));
		
		tabParameterList.setVisible(workspace.isAllowed("windowParameterList"));
		tabParameterDetail.setVisible(workspace.isAllowed("windowParameterDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_ParameterMain_btnEdit"));
		//btnNew.setVisible(workspace.isAllowed("button_ParameterMain_btnNew"));
		//btnDelete.setVisible(workspace.isAllowed("button_ParameterMain_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_ParameterMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_ParameterMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_ParameterMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_ParameterMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_ParameterMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_ParameterMain_btnLast"));
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalParameter(Parameter originalParameter) {
		this.originalParameter = originalParameter;
	}

	public Parameter getOriginalParameter() {
		return this.originalParameter;
	}

	public void setSelectedParameter(Parameter selectedParameter) {
		this.selectedParameter = selectedParameter;
	}

	public Parameter getSelectedParameter() {
		return this.selectedParameter;
	}

	public void setParameters(BindingListModelList parameters) {
		this.parameters = parameters;
	}

	public BindingListModelList getParameters() {
		return this.parameters;
	}

	/* CONTROLLERS */
	public void setParameterListCtrl(ParameterListCtrl parameterListCtrl) {
		this.parameterListCtrl = parameterListCtrl;
	}

	public ParameterListCtrl getParameterListCtrl() {
		return this.parameterListCtrl;
	}

	public void setParameterDetailCtrl(ParameterDetailCtrl parameterDetailCtrl) {
		this.parameterDetailCtrl = parameterDetailCtrl;
	}

	public ParameterDetailCtrl getParameterDetailCtrl() {
		return this.parameterDetailCtrl;
	}

	/* SERVICES */
	public ParameterService getParameterService() {
		return this.parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	/* COMPONENTS and OTHERS */
}
