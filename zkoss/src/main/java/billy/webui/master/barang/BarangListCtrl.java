package billy.webui.master.barang;

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
import billy.backend.model.Barang;
import billy.backend.service.BarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class BarangListCtrl extends GFCBaseListCtrl<Barang> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(BarangListCtrl.class);

	protected Window windowBarangList; // autowired
	protected Panel panelBarangList; // autowired

	protected Borderlayout borderLayout_barangList; // autowired
	protected Paging paging_BarangList; // autowired
	protected Listbox listBoxBarang; // autowired
	protected Listheader listheader_BarangList_Kode; // autowired
	protected Listheader listheader_BarangList_Nama; // autowired
	protected Listheader listheader_BarangList_Wilayah; // autowired
	protected Listheader listheader_BarangList_LastUpdate;
	protected Listheader listheader_BarangList_UpdatedBy;

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<Barang> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private BarangMainCtrl barangMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient BarangService barangService;

	/**
	 * default constructor.<br>
	 */
	public BarangListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
		
		this.self.setAttribute("controller", this, false);
		if (arg.containsKey("ModuleMainController")) {
			setBarangMainCtrl((BarangMainCtrl) arg.get("ModuleMainController"));
			getBarangMainCtrl().setBarangListCtrl(this);

			if (getBarangMainCtrl().getSelectedBarang() != null) {
				setSelectedBarang(getBarangMainCtrl().getSelectedBarang());
			} else
				setSelectedBarang(null);
		} else {
			setSelectedBarang(null);
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

	public void onCreate$windowBarangList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_BarangList.setPageSize(getCountRows());
		paging_BarangList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_BarangList_Kode.setSortAscending(new FieldComparator("kodeBarang", true));
		listheader_BarangList_Kode.setSortDescending(new FieldComparator("kodeBarang", false));
		
		listheader_BarangList_Nama.setSortAscending(new FieldComparator("namaBarang", true));
		listheader_BarangList_Nama.setSortDescending(new FieldComparator("namaBarang", false));
		
		listheader_BarangList_Wilayah.setSortAscending(new FieldComparator("wilayah.namaWilayah", true));
		listheader_BarangList_Wilayah.setSortDescending(new FieldComparator("wilayah.namaWilayah", false));
		
		
		listheader_BarangList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
		listheader_BarangList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));
		
		listheader_BarangList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
		listheader_BarangList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));
		
		
		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<Barang>(Barang.class, getCountRows());
		searchObj.addSort("kodeBarang", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxBarang(), paging_BarangList);
		BindingListModelList lml = (BindingListModelList) getListBoxBarang().getModel();
		setBarangs(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedBarang() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxBarang().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedBarang((Barang) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxBarang(), getSelectedBarang()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedBarangItem(Event event) {
		// logger.debug(event.toString());

		Barang anBarang = getSelectedBarang();

		if (anBarang != null) {
			setSelectedBarang(anBarang);
			setBarang(anBarang);

			// check first, if the tabs are created
			if (getBarangMainCtrl().getBarangDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getBarangMainCtrl().tabBarangDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getBarangMainCtrl().getBarangDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getBarangMainCtrl().tabBarangDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getBarangMainCtrl().tabBarangDetail, anBarang));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxBarang(Event event) {
		// logger.debug(event.toString());

		// selectedBarang is filled by annotated databinding mechanism
		Barang anBarang = getSelectedBarang();

		if (anBarang == null) {
			return;
		}

		// check first, if the tabs are created
		if (getBarangMainCtrl().getBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getBarangMainCtrl().tabBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getBarangMainCtrl().getBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getBarangMainCtrl().tabBarangDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getBarangMainCtrl().getBarangDetailCtrl().setSelectedBarang(anBarang);
		getBarangMainCtrl().getBarangDetailCtrl().setBarang(anBarang);

		// store the selected bean values as current
		getBarangMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.Barang") + ": " + anBarang.getKodeBarang();
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
		borderLayout_barangList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowBarangList.invalidate();
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
	public Barang getBarang() {
		// STORED IN THE module's MainController
		return getBarangMainCtrl().getSelectedBarang();
	}

	public void setBarang(Barang anBarang) {
		// STORED IN THE module's MainController
		getBarangMainCtrl().setSelectedBarang(anBarang);
	}

	public void setSelectedBarang(Barang selectedBarang) {
		// STORED IN THE module's MainController
		getBarangMainCtrl().setSelectedBarang(selectedBarang);
	}

	public Barang getSelectedBarang() {
		// STORED IN THE module's MainController
		return getBarangMainCtrl().getSelectedBarang();
	}

	public void setBarangs(BindingListModelList barangs) {
		// STORED IN THE module's MainController
		getBarangMainCtrl().setBarangs(barangs);
	}

	public BindingListModelList getBarangs() {
		// STORED IN THE module's MainController
		return getBarangMainCtrl().getBarangs();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setBarangMainCtrl(BarangMainCtrl barangMainCtrl) {
		this.barangMainCtrl = barangMainCtrl;
	}

	public BarangMainCtrl getBarangMainCtrl() {
		return this.barangMainCtrl;
	}

	/* SERVICES */
	public void setBarangService(BarangService barangService) {
		this.barangService = barangService;
	}

	public BarangService getBarangService() {
		return this.barangService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<Barang> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<Barang> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxBarang() {
		return this.listBoxBarang;
	}

	public void setListBoxBarang(Listbox listBoxBarang) {
		this.listBoxBarang = listBoxBarang;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
