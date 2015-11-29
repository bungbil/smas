package billy.webui.master.kategoribarang;

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
import billy.backend.model.KategoriBarang;
import billy.backend.service.KategoriBarangService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class KategoriBarangListCtrl extends GFCBaseListCtrl<KategoriBarang> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(KategoriBarangListCtrl.class);

	protected Window windowKategoriBarangList; // autowired
	protected Panel panelKategoriBarangList; // autowired

	protected Borderlayout borderLayout_kategoriBarangList; // autowired
	protected Paging paging_KategoriBarangList; // autowired
	protected Listbox listBoxKategoriBarang; // autowired
	protected Listheader listheader_KategoriBarangList_Kode; // autowired
	protected Listheader listheader_KategoriBarangList_Deskripsi; // autowired
	protected Listheader listheader_KategoriBarangList_LastUpdate;
	protected Listheader listheader_KategoriBarangList_UpdatedBy;	

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<KategoriBarang> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private KategoriBarangMainCtrl kategoriBarangMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient KategoriBarangService kategoriBarangService;

	/**
	 * default constructor.<br>
	 */
	public KategoriBarangListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
		
		this.self.setAttribute("controller", this, false);
		if (arg.containsKey("ModuleMainController")) {
			setKategoriBarangMainCtrl((KategoriBarangMainCtrl) arg.get("ModuleMainController"));
			getKategoriBarangMainCtrl().setKategoriBarangListCtrl(this);

			if (getKategoriBarangMainCtrl().getSelectedKategoriBarang() != null) {
				setSelectedKategoriBarang(getKategoriBarangMainCtrl().getSelectedKategoriBarang());
			} else
				setSelectedKategoriBarang(null);
		} else {
			setSelectedKategoriBarang(null);
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

	public void onCreate$windowKategoriBarangList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_KategoriBarangList.setPageSize(getCountRows());
		paging_KategoriBarangList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_KategoriBarangList_Kode.setSortAscending(new FieldComparator("kodeKategoriBarang", true));
		listheader_KategoriBarangList_Kode.setSortDescending(new FieldComparator("kodeKategoriBarang", false));
		
		listheader_KategoriBarangList_Deskripsi.setSortAscending(new FieldComparator("deskripsiKategoriBarang", true));
		listheader_KategoriBarangList_Deskripsi.setSortDescending(new FieldComparator("deskripsiKategoriBarang", false));		
		
		listheader_KategoriBarangList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
		listheader_KategoriBarangList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));
		
		listheader_KategoriBarangList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
		listheader_KategoriBarangList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));	
		
		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<KategoriBarang>(KategoriBarang.class, getCountRows());
		searchObj.addSort("kodeKategoriBarang", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxKategoriBarang(), paging_KategoriBarangList);
		BindingListModelList lml = (BindingListModelList) getListBoxKategoriBarang().getModel();
		setKategoriBarangs(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedKategoriBarang() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxKategoriBarang().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedKategoriBarang((KategoriBarang) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxKategoriBarang(), getSelectedKategoriBarang()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedKategoriBarangItem(Event event) {
		// logger.debug(event.toString());

		KategoriBarang anKategoriBarang = getSelectedKategoriBarang();

		if (anKategoriBarang != null) {
			setSelectedKategoriBarang(anKategoriBarang);
			setKategoriBarang(anKategoriBarang);

			// check first, if the tabs are created
			if (getKategoriBarangMainCtrl().getKategoriBarangDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getKategoriBarangMainCtrl().tabKategoriBarangDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getKategoriBarangMainCtrl().getKategoriBarangDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getKategoriBarangMainCtrl().tabKategoriBarangDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getKategoriBarangMainCtrl().tabKategoriBarangDetail, anKategoriBarang));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxKategoriBarang(Event event) {
		// logger.debug(event.toString());

		// selectedKategoriBarang is filled by annotated databinding mechanism
		KategoriBarang anKategoriBarang = getSelectedKategoriBarang();

		if (anKategoriBarang == null) {
			return;
		}

		// check first, if the tabs are created
		if (getKategoriBarangMainCtrl().getKategoriBarangDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getKategoriBarangMainCtrl().tabKategoriBarangDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getKategoriBarangMainCtrl().getKategoriBarangDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getKategoriBarangMainCtrl().tabKategoriBarangDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getKategoriBarangMainCtrl().getKategoriBarangDetailCtrl().setSelectedKategoriBarang(anKategoriBarang);
		getKategoriBarangMainCtrl().getKategoriBarangDetailCtrl().setKategoriBarang(anKategoriBarang);

		// store the selected bean values as current
		getKategoriBarangMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.KategoriBarang") + ": " + anKategoriBarang.getKodeKategoriBarang();
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
		borderLayout_kategoriBarangList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowKategoriBarangList.invalidate();
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
	public KategoriBarang getKategoriBarang() {
		// STORED IN THE module's MainController
		return getKategoriBarangMainCtrl().getSelectedKategoriBarang();
	}

	public void setKategoriBarang(KategoriBarang anKategoriBarang) {
		// STORED IN THE module's MainController
		getKategoriBarangMainCtrl().setSelectedKategoriBarang(anKategoriBarang);
	}

	public void setSelectedKategoriBarang(KategoriBarang selectedKategoriBarang) {
		// STORED IN THE module's MainController
		getKategoriBarangMainCtrl().setSelectedKategoriBarang(selectedKategoriBarang);
	}

	public KategoriBarang getSelectedKategoriBarang() {
		// STORED IN THE module's MainController
		return getKategoriBarangMainCtrl().getSelectedKategoriBarang();
	}

	public void setKategoriBarangs(BindingListModelList kategoriBarangs) {
		// STORED IN THE module's MainController
		getKategoriBarangMainCtrl().setKategoriBarangs(kategoriBarangs);
	}

	public BindingListModelList getKategoriBarangs() {
		// STORED IN THE module's MainController
		return getKategoriBarangMainCtrl().getKategoriBarangs();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setKategoriBarangMainCtrl(KategoriBarangMainCtrl kategoriBarangMainCtrl) {
		this.kategoriBarangMainCtrl = kategoriBarangMainCtrl;
	}

	public KategoriBarangMainCtrl getKategoriBarangMainCtrl() {
		return this.kategoriBarangMainCtrl;
	}

	/* SERVICES */
	public void setKategoriBarangService(KategoriBarangService kategoriBarangService) {
		this.kategoriBarangService = kategoriBarangService;
	}

	public KategoriBarangService getKategoriBarangService() {
		return this.kategoriBarangService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<KategoriBarang> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<KategoriBarang> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxKategoriBarang() {
		return this.listBoxKategoriBarang;
	}

	public void setListBoxKategoriBarang(Listbox listBoxKategoriBarang) {
		this.listBoxKategoriBarang = listBoxKategoriBarang;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
