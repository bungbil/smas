package billy.webui.master.status;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.Status;
import billy.backend.service.StatusService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class StatusListCtrl extends GFCBaseListCtrl<Status> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(StatusListCtrl.class);

	protected Window windowStatusList; // autowired
	protected Panel panelStatusList; // autowired

	protected Borderlayout borderLayout_statusList; // autowired
	protected Paging paging_StatusList; // autowired
	protected Listbox listBoxStatus; // autowired
	protected Listheader listheader_StatusList_Kode; // autowired
	protected Listheader listheader_StatusList_Deskripsi; // autowired
	protected Listheader listheader_StatusList_StatusType; // autowired
	protected Listheader listheader_StatusList_LastUpdate;
	protected Listheader listheader_StatusList_UpdatedBy;

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<Status> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private StatusMainCtrl statusMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient StatusService statusService;

	/**
	 * default constructor.<br>
	 */
	public StatusListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
		
		this.self.setAttribute("controller", this, false);
		if (arg.containsKey("ModuleMainController")) {
			setStatusMainCtrl((StatusMainCtrl) arg.get("ModuleMainController"));
			getStatusMainCtrl().setStatusListCtrl(this);

			if (getStatusMainCtrl().getSelectedStatus() != null) {
				setSelectedStatus(getStatusMainCtrl().getSelectedStatus());
			} else
				setSelectedStatus(null);
		} else {
			setSelectedStatus(null);
		}
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

	public void onCreate$windowStatusList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_StatusList.setPageSize(getCountRows());
		paging_StatusList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_StatusList_Kode.setSortAscending(new FieldComparator("kodeStatus", true));
		listheader_StatusList_Kode.setSortDescending(new FieldComparator("kodeStatus", false));
		
		listheader_StatusList_Deskripsi.setSortAscending(new FieldComparator("deskripsiStatus", true));
		listheader_StatusList_Deskripsi.setSortDescending(new FieldComparator("deskripsiStatus", false));
		
		listheader_StatusList_StatusType.setSortAscending(new FieldComparator("statusType", true));
		listheader_StatusList_StatusType.setSortDescending(new FieldComparator("statusType", false));
		
		listheader_StatusList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
		listheader_StatusList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));
		
		listheader_StatusList_UpdatedBy.setSortAscending(new FieldComparator("UpdatedBy", true));
		listheader_StatusList_UpdatedBy.setSortDescending(new FieldComparator("UpdatedBy", false));
		
		
		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<Status>(Status.class, getCountRows());
		searchObj.addSort("kodeStatus", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxStatus(), paging_StatusList);
		BindingListModelList lml = (BindingListModelList) getListBoxStatus().getModel();
		setStatuss(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedStatus() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxStatus().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedStatus((Status) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxStatus(), getSelectedStatus()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedStatusItem(Event event) {
		// logger.debug(event.toString());

		Status anStatus = getSelectedStatus();

		if (anStatus != null) {
			setSelectedStatus(anStatus);
			setStatus(anStatus);

			// check first, if the tabs are created
			if (getStatusMainCtrl().getStatusDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getStatusMainCtrl().tabStatusDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getStatusMainCtrl().getStatusDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getStatusMainCtrl().tabStatusDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getStatusMainCtrl().tabStatusDetail, anStatus));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxStatus(Event event) {
		// logger.debug(event.toString());

		// selectedStatus is filled by annotated databinding mechanism
		Status anStatus = getSelectedStatus();

		if (anStatus == null) {
			return;
		}

		// check first, if the tabs are created
		if (getStatusMainCtrl().getStatusDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getStatusMainCtrl().tabStatusDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getStatusMainCtrl().getStatusDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getStatusMainCtrl().tabStatusDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getStatusMainCtrl().getStatusDetailCtrl().setSelectedStatus(anStatus);
		getStatusMainCtrl().getStatusDetailCtrl().setStatus(anStatus);

		// store the selected bean values as current
		getStatusMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.Status") + ": " + anStatus.getKodeStatus();
		EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Recalculates the container size for this controller and resize them.
	 * 
	 * Calculate how many rows have been place in the listbox. Get the
	 * currentDesktopHeight from a hidden Intbox from the index.zul that are
	 * filled by onClientInfo() in the indexCtroller.
	 */
	public void doFitSize() {

		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 5;

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - specialSize - 148;
		setCountRows((int) Math.round(maxListBoxHeight / 17.7));
		borderLayout_statusList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowStatusList.invalidate();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Best Pratice Hint:<br>
	 * The setters/getters for the local annotated data binded Beans/Sets are
	 * administered in the module's mainController. Working in this way you have
	 * clean line to share this beans/sets with other controllers.
	 */
	/* Master BEANS */
	public Status getStatus() {
		// STORED IN THE module's MainController
		return getStatusMainCtrl().getSelectedStatus();
	}

	public void setStatus(Status anStatus) {
		// STORED IN THE module's MainController
		getStatusMainCtrl().setSelectedStatus(anStatus);
	}

	public void setSelectedStatus(Status selectedStatus) {
		// STORED IN THE module's MainController
		getStatusMainCtrl().setSelectedStatus(selectedStatus);
	}

	public Status getSelectedStatus() {
		// STORED IN THE module's MainController
		return getStatusMainCtrl().getSelectedStatus();
	}

	public void setStatuss(BindingListModelList statuss) {
		// STORED IN THE module's MainController
		getStatusMainCtrl().setStatuss(statuss);
	}

	public BindingListModelList getStatuss() {
		// STORED IN THE module's MainController
		return getStatusMainCtrl().getStatuss();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setStatusMainCtrl(StatusMainCtrl statusMainCtrl) {
		this.statusMainCtrl = statusMainCtrl;
	}

	public StatusMainCtrl getStatusMainCtrl() {
		return this.statusMainCtrl;
	}

	/* SERVICES */
	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public StatusService getStatusService() {
		return this.statusService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<Status> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<Status> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxStatus() {
		return this.listBoxStatus;
	}

	public void setListBoxStatus(Listbox listBoxStatus) {
		this.listBoxStatus = listBoxStatus;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
