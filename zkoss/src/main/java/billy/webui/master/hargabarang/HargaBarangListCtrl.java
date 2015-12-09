package billy.webui.master.hargabarang;

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
import billy.backend.model.HargaBarang;
import billy.backend.service.HargaBarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class HargaBarangListCtrl extends GFCBaseListCtrl<HargaBarang> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(HargaBarangListCtrl.class);

	protected Window windowHargaBarangList; // autowired
	protected Panel panelHargaBarangList; // autowired

	protected Borderlayout borderLayout_hargaBarangList; // autowired
	protected Paging paging_HargaBarangList; // autowired
	protected Listbox listBoxHargaBarang; // autowired
	protected Listheader listheader_HargaBarangList_Kode; // autowired
	protected Listheader listheader_HargaBarangList_Nama; // autowired
	protected Listheader listheader_HargaBarangList_Status; // autowired
	

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<HargaBarang> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private HargaBarangMainCtrl hargaBarangMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient HargaBarangService hargaBarangService;

	/**
	 * default constructor.<br>
	 */
	public HargaBarangListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
		
		this.self.setAttribute("controller", this, false);
		if (arg.containsKey("ModuleMainController")) {
			setHargaBarangMainCtrl((HargaBarangMainCtrl) arg.get("ModuleMainController"));
			getHargaBarangMainCtrl().setHargaBarangListCtrl(this);

			if (getHargaBarangMainCtrl().getSelectedHargaBarang() != null) {
				setSelectedHargaBarang(getHargaBarangMainCtrl().getSelectedHargaBarang());
			} else
				setSelectedHargaBarang(null);
		} else {
			setSelectedHargaBarang(null);
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

	public void onCreate$windowHargaBarangList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_HargaBarangList.setPageSize(getCountRows());
		paging_HargaBarangList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_HargaBarangList_Kode.setSortAscending(new FieldComparator("kodeHargaBarang", true));
		listheader_HargaBarangList_Kode.setSortDescending(new FieldComparator("kodeHargaBarang", false));
		
		listheader_HargaBarangList_Nama.setSortAscending(new FieldComparator("namaHargaBarang", true));
		listheader_HargaBarangList_Nama.setSortDescending(new FieldComparator("namaHargaBarang", false));
		
		listheader_HargaBarangList_Status.setSortAscending(new FieldComparator("status", true));
		listheader_HargaBarangList_Status.setSortDescending(new FieldComparator("status", false));
		
		
		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<HargaBarang>(HargaBarang.class, getCountRows());
		searchObj.addSort("kodeHargaBarang", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxHargaBarang(), paging_HargaBarangList);
		BindingListModelList lml = (BindingListModelList) getListBoxHargaBarang().getModel();
		setHargaBarangs(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedHargaBarang() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxHargaBarang().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedHargaBarang((HargaBarang) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxHargaBarang(), getSelectedHargaBarang()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedHargaBarangItem(Event event) {
		// logger.debug(event.toString());

		HargaBarang anHargaBarang = getSelectedHargaBarang();

		if (anHargaBarang != null) {
			setSelectedHargaBarang(anHargaBarang);
			setHargaBarang(anHargaBarang);

			// check first, if the tabs are created
			if (getHargaBarangMainCtrl().getHargaBarangDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getHargaBarangMainCtrl().tabHargaBarangDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getHargaBarangMainCtrl().getHargaBarangDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getHargaBarangMainCtrl().tabHargaBarangDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getHargaBarangMainCtrl().tabHargaBarangDetail, anHargaBarang));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxHargaBarang(Event event) {
		// logger.debug(event.toString());

		// selectedHargaBarang is filled by annotated databinding mechanism
		HargaBarang anHargaBarang = getSelectedHargaBarang();

		if (anHargaBarang == null) {
			return;
		}

		// check first, if the tabs are created
		if (getHargaBarangMainCtrl().getHargaBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getHargaBarangMainCtrl().tabHargaBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getHargaBarangMainCtrl().getHargaBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getHargaBarangMainCtrl().tabHargaBarangDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getHargaBarangMainCtrl().getHargaBarangDetailCtrl().setSelectedHargaBarang(anHargaBarang);
		getHargaBarangMainCtrl().getHargaBarangDetailCtrl().setHargaBarang(anHargaBarang);

		// store the selected bean values as current
		getHargaBarangMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.HargaBarang") + ": " + anHargaBarang.getKodeHargaBarang();
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
		borderLayout_hargaBarangList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowHargaBarangList.invalidate();
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
	public HargaBarang getHargaBarang() {
		// STORED IN THE module's MainController
		return getHargaBarangMainCtrl().getSelectedHargaBarang();
	}

	public void setHargaBarang(HargaBarang anHargaBarang) {
		// STORED IN THE module's MainController
		getHargaBarangMainCtrl().setSelectedHargaBarang(anHargaBarang);
	}

	public void setSelectedHargaBarang(HargaBarang selectedHargaBarang) {
		// STORED IN THE module's MainController
		getHargaBarangMainCtrl().setSelectedHargaBarang(selectedHargaBarang);
	}

	public HargaBarang getSelectedHargaBarang() {
		// STORED IN THE module's MainController
		return getHargaBarangMainCtrl().getSelectedHargaBarang();
	}

	public void setHargaBarangs(BindingListModelList hargaBarangs) {
		// STORED IN THE module's MainController
		getHargaBarangMainCtrl().setHargaBarangs(hargaBarangs);
	}

	public BindingListModelList getHargaBarangs() {
		// STORED IN THE module's MainController
		return getHargaBarangMainCtrl().getHargaBarangs();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setHargaBarangMainCtrl(HargaBarangMainCtrl hargaBarangMainCtrl) {
		this.hargaBarangMainCtrl = hargaBarangMainCtrl;
	}

	public HargaBarangMainCtrl getHargaBarangMainCtrl() {
		return this.hargaBarangMainCtrl;
	}

	/* SERVICES */
	public void setHargaBarangService(HargaBarangService hargaBarangService) {
		this.hargaBarangService = hargaBarangService;
	}

	public HargaBarangService getHargaBarangService() {
		return this.hargaBarangService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<HargaBarang> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<HargaBarang> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxHargaBarang() {
		return this.listBoxHargaBarang;
	}

	public void setListBoxHargaBarang(Listbox listBoxHargaBarang) {
		this.listBoxHargaBarang = listBoxHargaBarang;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
