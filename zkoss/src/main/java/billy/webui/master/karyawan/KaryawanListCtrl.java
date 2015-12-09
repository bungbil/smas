package billy.webui.master.karyawan;

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
import billy.backend.model.Karyawan;
import billy.backend.service.KaryawanService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class KaryawanListCtrl extends GFCBaseListCtrl<Karyawan> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(KaryawanListCtrl.class);

	protected Window windowKaryawanList; // autowired
	protected Panel panelKaryawanList; // autowired

	protected Borderlayout borderLayout_karyawanList; // autowired
	protected Paging paging_KaryawanList; // autowired
	protected Listbox listBoxKaryawan; // autowired
	protected Listheader listheader_KaryawanList_Kode; // autowired
	protected Listheader listheader_KaryawanList_Nama; // autowired
	protected Listheader listheader_KaryawanList_JobType; // autowired
	protected Listheader listheader_KaryawanList_SupervisorDivisi; // autowired
	protected Listheader listheader_KaryawanList_Handphone; // autowired
	protected Listheader listheader_KaryawanList_LastUpdate; // autowired
	protected Listheader listheader_KaryawanList_UpdatedBy; // autowired
	

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<Karyawan> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private KaryawanMainCtrl karyawanMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient KaryawanService karyawanService;

	/**
	 * default constructor.<br>
	 */
	public KaryawanListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
		
		this.self.setAttribute("controller", this, false);
		if (arg.containsKey("ModuleMainController")) {
			setKaryawanMainCtrl((KaryawanMainCtrl) arg.get("ModuleMainController"));
			getKaryawanMainCtrl().setKaryawanListCtrl(this);

			if (getKaryawanMainCtrl().getSelectedKaryawan() != null) {
				setSelectedKaryawan(getKaryawanMainCtrl().getSelectedKaryawan());
			} else
				setSelectedKaryawan(null);
		} else {
			setSelectedKaryawan(null);
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

	public void onCreate$windowKaryawanList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_KaryawanList.setPageSize(getCountRows());
		paging_KaryawanList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_KaryawanList_Kode.setSortAscending(new FieldComparator("kodeKaryawan", true));
		listheader_KaryawanList_Kode.setSortDescending(new FieldComparator("kodeKaryawan", false));
		
		listheader_KaryawanList_Nama.setSortAscending(new FieldComparator("namaKaryawan", true));
		listheader_KaryawanList_Nama.setSortDescending(new FieldComparator("namaKaryawan", false));
		
		listheader_KaryawanList_JobType.setSortAscending(new FieldComparator("jobType.namaJobType", true));
		listheader_KaryawanList_JobType.setSortDescending(new FieldComparator("jobType.namaJobType", false));
		
		listheader_KaryawanList_SupervisorDivisi.setSortAscending(new FieldComparator("supervisorDivisi.namaKaryawan", true));
		listheader_KaryawanList_SupervisorDivisi.setSortDescending(new FieldComparator("supervisorDivisi.namaKaryawan", false));
		
		listheader_KaryawanList_Handphone.setSortAscending(new FieldComparator("handphone", true));
		listheader_KaryawanList_Handphone.setSortDescending(new FieldComparator("handphone", false));				
		
		listheader_KaryawanList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
		listheader_KaryawanList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));
		
		listheader_KaryawanList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
		listheader_KaryawanList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));	
		
		
		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<Karyawan>(Karyawan.class, getCountRows());
		searchObj.addSort("kodeKaryawan", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxKaryawan(), paging_KaryawanList);
		BindingListModelList lml = (BindingListModelList) getListBoxKaryawan().getModel();
		setKaryawans(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedKaryawan() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxKaryawan().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedKaryawan((Karyawan) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxKaryawan(), getSelectedKaryawan()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedKaryawanItem(Event event) {
		// logger.debug(event.toString());

		Karyawan anKaryawan = getSelectedKaryawan();

		if (anKaryawan != null) {
			setSelectedKaryawan(anKaryawan);
			setKaryawan(anKaryawan);

			// check first, if the tabs are created
			if (getKaryawanMainCtrl().getKaryawanDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getKaryawanMainCtrl().tabKaryawanDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getKaryawanMainCtrl().getKaryawanDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getKaryawanMainCtrl().tabKaryawanDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getKaryawanMainCtrl().tabKaryawanDetail, anKaryawan));
		}
		getKaryawanMainCtrl().getKaryawanDetailCtrl().loadListBox();
		
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxKaryawan(Event event) {
		// logger.debug(event.toString());

		// selectedKaryawan is filled by annotated databinding mechanism
		Karyawan anKaryawan = getSelectedKaryawan();

		if (anKaryawan == null) {
			return;
		}

		// check first, if the tabs are created
		if (getKaryawanMainCtrl().getKaryawanDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getKaryawanMainCtrl().tabKaryawanDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getKaryawanMainCtrl().getKaryawanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getKaryawanMainCtrl().tabKaryawanDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getKaryawanMainCtrl().getKaryawanDetailCtrl().setSelectedKaryawan(anKaryawan);
		getKaryawanMainCtrl().getKaryawanDetailCtrl().setKaryawan(anKaryawan);

		// store the selected bean values as current
		getKaryawanMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.Karyawan") + ": " + anKaryawan.getKodeKaryawan();
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
		borderLayout_karyawanList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowKaryawanList.invalidate();
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
	public Karyawan getKaryawan() {
		// STORED IN THE module's MainController
		return getKaryawanMainCtrl().getSelectedKaryawan();
	}

	public void setKaryawan(Karyawan anKaryawan) {
		// STORED IN THE module's MainController
		getKaryawanMainCtrl().setSelectedKaryawan(anKaryawan);
	}

	public void setSelectedKaryawan(Karyawan selectedKaryawan) {
		// STORED IN THE module's MainController
		getKaryawanMainCtrl().setSelectedKaryawan(selectedKaryawan);
	}

	public Karyawan getSelectedKaryawan() {
		// STORED IN THE module's MainController
		return getKaryawanMainCtrl().getSelectedKaryawan();
	}

	public void setKaryawans(BindingListModelList karyawans) {
		// STORED IN THE module's MainController
		getKaryawanMainCtrl().setKaryawans(karyawans);
	}

	public BindingListModelList getKaryawans() {
		// STORED IN THE module's MainController
		return getKaryawanMainCtrl().getKaryawans();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setKaryawanMainCtrl(KaryawanMainCtrl karyawanMainCtrl) {
		this.karyawanMainCtrl = karyawanMainCtrl;
	}

	public KaryawanMainCtrl getKaryawanMainCtrl() {
		return this.karyawanMainCtrl;
	}

	/* SERVICES */
	public void setKaryawanService(KaryawanService karyawanService) {
		this.karyawanService = karyawanService;
	}

	public KaryawanService getKaryawanService() {
		return this.karyawanService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<Karyawan> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<Karyawan> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxKaryawan() {
		return this.listBoxKaryawan;
	}

	public void setListBoxKaryawan(Listbox listBoxKaryawan) {
		this.listBoxKaryawan = listBoxKaryawan;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
