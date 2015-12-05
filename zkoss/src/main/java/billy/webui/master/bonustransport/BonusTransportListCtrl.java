package billy.webui.master.bonustransport;

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
import billy.backend.model.BonusTransport;
import billy.backend.service.BonusTransportService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class BonusTransportListCtrl extends GFCBaseListCtrl<BonusTransport> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(BonusTransportListCtrl.class);

	protected Window windowBonusTransportList; // autowired
	protected Panel panelBonusTransportList; // autowired

	protected Borderlayout borderLayout_bonusTransportList; // autowired
	protected Paging paging_BonusTransportList; // autowired
	protected Listbox listBoxBonusTransport; // autowired
	protected Listheader listheader_BonusTransportList_Deskripsi; // autowired
	protected Listheader listheader_BonusTransportList_Job; // autowired
	protected Listheader listheader_BonusTransportList_MultipleUnit; // autowired
	protected Listheader listheader_BonusTransportList_StartRangeUnit; // autowired
	protected Listheader listheader_BonusTransportList_EndRangeUnit; // autowired
	protected Listheader listheader_BonusTransportList_LastUpdate; // autowired
	protected Listheader listheader_BonusTransportList_UpdatedBy; // autowired
	
	
	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<BonusTransport> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private BonusTransportMainCtrl bonusTransportMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient BonusTransportService bonusTransportService;

	/**
	 * default constructor.<br>
	 */
	public BonusTransportListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
		
		this.self.setAttribute("controller", this, false);
		if (arg.containsKey("ModuleMainController")) {
			setBonusTransportMainCtrl((BonusTransportMainCtrl) arg.get("ModuleMainController"));
			getBonusTransportMainCtrl().setBonusTransportListCtrl(this);

			if (getBonusTransportMainCtrl().getSelectedBonusTransport() != null) {
				setSelectedBonusTransport(getBonusTransportMainCtrl().getSelectedBonusTransport());
			} else
				setSelectedBonusTransport(null);
		} else {
			setSelectedBonusTransport(null);
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

	public void onCreate$windowBonusTransportList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_BonusTransportList.setPageSize(getCountRows());
		paging_BonusTransportList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_BonusTransportList_Deskripsi.setSortAscending(new FieldComparator("deskripsiBonusTransport", true));
		listheader_BonusTransportList_Deskripsi.setSortDescending(new FieldComparator("deskripsiBonusTransport", false));
		
		listheader_BonusTransportList_Job.setSortAscending(new FieldComparator("jobType.namaJobType", true));
		listheader_BonusTransportList_Job.setSortDescending(new FieldComparator("jobType.namaJobType", false));
		
		listheader_BonusTransportList_MultipleUnit.setSortAscending(new FieldComparator("multipleUnit", true));
		listheader_BonusTransportList_MultipleUnit.setSortDescending(new FieldComparator("multipleUnit", false));
		
		listheader_BonusTransportList_StartRangeUnit.setSortAscending(new FieldComparator("startRangeUnit", true));
		listheader_BonusTransportList_StartRangeUnit.setSortDescending(new FieldComparator("startRangeUnit", false));
		
		listheader_BonusTransportList_EndRangeUnit.setSortAscending(new FieldComparator("endRangeUnit", true));
		listheader_BonusTransportList_EndRangeUnit.setSortDescending(new FieldComparator("endRangeUnit", false));
		
		listheader_BonusTransportList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
		listheader_BonusTransportList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));
		
		listheader_BonusTransportList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
		listheader_BonusTransportList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));
		
		
		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<BonusTransport>(BonusTransport.class, getCountRows());
		searchObj.addSort("deskripsiBonusTransport", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxBonusTransport(), paging_BonusTransportList);
		BindingListModelList lml = (BindingListModelList) getListBoxBonusTransport().getModel();
		setBonusTransports(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedBonusTransport() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxBonusTransport().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedBonusTransport((BonusTransport) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxBonusTransport(), getSelectedBonusTransport()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedBonusTransportItem(Event event) {
		// logger.debug(event.toString());

		BonusTransport anBonusTransport = getSelectedBonusTransport();

		if (anBonusTransport != null) {
			setSelectedBonusTransport(anBonusTransport);
			setBonusTransport(anBonusTransport);

			// check first, if the tabs are created
			if (getBonusTransportMainCtrl().getBonusTransportDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getBonusTransportMainCtrl().tabBonusTransportDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getBonusTransportMainCtrl().getBonusTransportDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getBonusTransportMainCtrl().tabBonusTransportDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getBonusTransportMainCtrl().tabBonusTransportDetail, anBonusTransport));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxBonusTransport(Event event) {
		// logger.debug(event.toString());

		// selectedBonusTransport is filled by annotated databinding mechanism
		BonusTransport anBonusTransport = getSelectedBonusTransport();

		if (anBonusTransport == null) {
			return;
		}

		// check first, if the tabs are created
		if (getBonusTransportMainCtrl().getBonusTransportDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getBonusTransportMainCtrl().tabBonusTransportDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getBonusTransportMainCtrl().getBonusTransportDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getBonusTransportMainCtrl().tabBonusTransportDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getBonusTransportMainCtrl().getBonusTransportDetailCtrl().setSelectedBonusTransport(anBonusTransport);
		getBonusTransportMainCtrl().getBonusTransportDetailCtrl().setBonusTransport(anBonusTransport);

		// store the selected bean values as current
		getBonusTransportMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.BonusTransport") + ": " + anBonusTransport.getDeskripsiBonusTransport();
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
		borderLayout_bonusTransportList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowBonusTransportList.invalidate();
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
	public BonusTransport getBonusTransport() {
		// STORED IN THE module's MainController
		return getBonusTransportMainCtrl().getSelectedBonusTransport();
	}

	public void setBonusTransport(BonusTransport anBonusTransport) {
		// STORED IN THE module's MainController
		getBonusTransportMainCtrl().setSelectedBonusTransport(anBonusTransport);
	}

	public void setSelectedBonusTransport(BonusTransport selectedBonusTransport) {
		// STORED IN THE module's MainController
		getBonusTransportMainCtrl().setSelectedBonusTransport(selectedBonusTransport);
	}

	public BonusTransport getSelectedBonusTransport() {
		// STORED IN THE module's MainController
		return getBonusTransportMainCtrl().getSelectedBonusTransport();
	}

	public void setBonusTransports(BindingListModelList bonusTransports) {
		// STORED IN THE module's MainController
		getBonusTransportMainCtrl().setBonusTransports(bonusTransports);
	}

	public BindingListModelList getBonusTransports() {
		// STORED IN THE module's MainController
		return getBonusTransportMainCtrl().getBonusTransports();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setBonusTransportMainCtrl(BonusTransportMainCtrl bonusTransportMainCtrl) {
		this.bonusTransportMainCtrl = bonusTransportMainCtrl;
	}

	public BonusTransportMainCtrl getBonusTransportMainCtrl() {
		return this.bonusTransportMainCtrl;
	}

	/* SERVICES */
	public void setBonusTransportService(BonusTransportService bonusTransportService) {
		this.bonusTransportService = bonusTransportService;
	}

	public BonusTransportService getBonusTransportService() {
		return this.bonusTransportService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<BonusTransport> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<BonusTransport> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxBonusTransport() {
		return this.listBoxBonusTransport;
	}

	public void setListBoxBonusTransport(Listbox listBoxBonusTransport) {
		this.listBoxBonusTransport = listBoxBonusTransport;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
