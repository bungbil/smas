package billy.webui.transaction.penjualan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import billy.backend.model.Penjualan;
import billy.backend.service.PenjualanService;
import billy.webui.transaction.penjualan.model.PenjualanListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;
import de.forsthaus.webui.util.pagging.PagedListWrapper;

public class PenjualanListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5710086946825179284L;
	private static final Logger logger = Logger.getLogger(PenjualanListCtrl.class);

	private PagedListWrapper<Penjualan> plwPenjualans;
	
	protected Panel panelPenjualanList;
	protected Window penjualanListWindow; // autowired
	// Listbox penjualans
	protected Paging paging_PenjualanList; // autowired
	protected Listbox listBoxPenjualan; // autowired
	protected Listheader listheader_PenjualanList_NoFaktur; // autowired
	protected Listheader listheader_PenjualanList_TglPenjualan; // autowired
	protected Listheader listheader_PenjualanList_MetodePembayaran; // autowired
	protected Listheader listheader_PenjualanList_Total; // autowired
	protected Listheader listheader_PenjualanList_Piutang; // autowired
	protected Listheader listheader_PenjualanList_Status; // autowired
	protected Listheader listheader_PenjualanList_NamaPelanggan; // autowired
	protected Listheader listheader_PenjualanList_Telepon; // autowired
	protected Listheader listheader_PenjualanList_Sales1; // autowired
	protected Listheader listheader_PenjualanList_Sales2; // autowired
	protected Listheader listheader_PenjualanList_LastUpdate; // autowired
	protected Listheader listheader_PenjualanList_UpdatedBy; // autowired
	
	
	protected Hbox hBoxSearch; // autowired

	
	protected Textbox tb_Search_No_Faktur; // autowired
	protected Button button_PenjualanList_SearchNoFaktur;
	protected Textbox tb_Search_Nama_Pelanggan; // autowired
	protected Button button_PenjualanList_SearchNamaPelanggan;
	protected Textbox tb_Search_Alamat; // autowired
	protected Button button_PenjualanList_SearchAlamat;
	
	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_PenjualanList_NewPenjualan; // autowired

	private transient int pageSizePenjualans;

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<Penjualan> searchObj;

	// row count for listbox
	private int countRows;
	
	// ServiceDAOs / Domain Classes
	private Penjualan penjualan;
	private transient PenjualanService penjualanService;


	/**
	 * default constructor.<br>
	 */
	public PenjualanListCtrl() {
		super();
	}

	public void onCreate$penjualanListWindow(Event event) throws Exception {
		/* autowire comps the vars */
		// doOnCreateCommon(penjualanListWindow, event);
		doFitSize();
		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		

		// check if the penjualanList is called from the Customer Dialog
		// and set the pageSizes
		if (args.containsKey("rowSizePenjualans")) {
			int rowSize = (Integer) args.get("rowSizePenjualans");
			setPageSizePenjualans(rowSize);
		} else {
			setPageSizePenjualans(getCountRows());
		}
		

		paintComponents();

	}

	private void paintComponents() {

		listheader_PenjualanList_NoFaktur.setSortAscending(new FieldComparator("noFaktur", true));
		listheader_PenjualanList_NoFaktur.setSortDescending(new FieldComparator("noFaktur", false));
		
		listheader_PenjualanList_TglPenjualan.setSortAscending(new FieldComparator("tglPenjualan", true));
		listheader_PenjualanList_TglPenjualan.setSortDescending(new FieldComparator("tglPenjualan", false));
		
		listheader_PenjualanList_MetodePembayaran.setSortAscending(new FieldComparator("metodePembayaran", true));
		listheader_PenjualanList_MetodePembayaran.setSortDescending(new FieldComparator("metodePembayaran", false));
		
		listheader_PenjualanList_Total.setSortAscending(new FieldComparator("grandTotal", true));
		listheader_PenjualanList_Total.setSortDescending(new FieldComparator("grandTotal", false));
		
		listheader_PenjualanList_Piutang.setSortAscending(new FieldComparator("piutang", true));
		listheader_PenjualanList_Piutang.setSortDescending(new FieldComparator("piutang", false));
		
		listheader_PenjualanList_Status.setSortAscending(new FieldComparator("status", true));
		listheader_PenjualanList_Status.setSortDescending(new FieldComparator("status", false));
		
		listheader_PenjualanList_NamaPelanggan.setSortAscending(new FieldComparator("namaPelanggan", true));
		listheader_PenjualanList_NamaPelanggan.setSortDescending(new FieldComparator("namaPelanggan", false));
		
		listheader_PenjualanList_Telepon.setSortAscending(new FieldComparator("telepon", true));
		listheader_PenjualanList_Telepon.setSortDescending(new FieldComparator("telepon", false));
		
		listheader_PenjualanList_Sales1.setSortAscending(new FieldComparator("sales1", true));
		listheader_PenjualanList_Sales1.setSortDescending(new FieldComparator("sales1", false));
		
		listheader_PenjualanList_Sales2.setSortAscending(new FieldComparator("sales2", true));
		listheader_PenjualanList_Sales2.setSortDescending(new FieldComparator("sales2", false));
		
		listheader_PenjualanList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
		listheader_PenjualanList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));
		
		listheader_PenjualanList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
		listheader_PenjualanList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));
				
		searchObj = new HibernateSearchObject<Penjualan>(Penjualan.class, getPageSizePenjualans());
		searchObj.addSort("noFaktur", false);

		// set the paging params
		paging_PenjualanList.setPageSize(getPageSizePenjualans());
		paging_PenjualanList.setDetailed(true);

		listBoxPenjualan.setItemRenderer(new PenjualanListModelItemRenderer());
		
		getPlwPenjualans().init(searchObj, listBoxPenjualan, paging_PenjualanList);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		//penjualanListWindow.setVisible(workspace.isAllowed("penjualanListWindow"));
		//btnHelp.setVisible(workspace.isAllowed("button_PenjualanList_btnHelp"));
		//button_PenjualanList_NewPenjualan.setVisible(workspace.isAllowed("button_PenjualanList_NewPenjualan"));

	}
	
	public void onDoubleClickedPenjualanItem(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxPenjualan.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Penjualan anPenjualan = (Penjualan) item.getAttribute("data");

			showDetailView(anPenjualan);

		}
	}

	public void onClick$button_PenjualanList_NewPenjualan(Event event) throws Exception {
		
		Penjualan anPenjualan = getPenjualanService().getNewPenjualan();

		showDetailView(anPenjualan);
	}

	private void showDetailView(Penjualan anPenjualan) throws Exception {
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("penjualan", anPenjualan);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxPenjualan", listBoxPenjualan);
		map.put("penjualanListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/transaction/penjualan/penjualanDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			ZksampleMessageUtils.showErrorMessage(e.toString());
		}

	}

	/**
	 * when the "Penjualan search" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PenjualanList_Search(Event event) throws InterruptedException {
		
		searchObj = new HibernateSearchObject<Penjualan>(Penjualan.class, getCountRows());

		// check which field have input
		if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())) {
			searchObj.addFilter(new Filter("noFaktur", tb_Search_No_Faktur.getValue(), Filter.OP_EQUAL));
		}

		if (StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())) {
			searchObj.addFilter(new Filter("namaPelanggan", "%" + tb_Search_Nama_Pelanggan.getValue().toUpperCase() + "%", Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
			searchObj.addFilter(new Filter("alamat", "%" + tb_Search_Alamat.getValue() + "%", Filter.OP_ILIKE));
		}

		setSearchObj(this.searchObj);

		// Set the ListModel.
		getPlwPenjualans().init(getSearchObj(), listBoxPenjualan, paging_PenjualanList);

	}


	public void onClick$btnHelp(Event event) throws InterruptedException {

		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	public void onClick$btnRefresh(Event event) throws InterruptedException {

		paintComponents();
		penjualanListWindow.invalidate();
	}
	
	public void doFitSize() {

		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 5;

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - specialSize - 148;
		setCountRows((int) Math.round(maxListBoxHeight / 17.7));
		panelPenjualanList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		penjualanListWindow.invalidate();
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setPenjualan(Penjualan penjualan) {
		this.penjualan = penjualan;
	}

	public Penjualan getPenjualan() {
		return this.penjualan;
	}

	public void setPenjualanService(PenjualanService penjualanService) {
		this.penjualanService = penjualanService;
	}

	public PenjualanService getPenjualanService() {
		return this.penjualanService;
	}


	public void setPageSizePenjualans(int pageSizePenjualans) {
		this.pageSizePenjualans = pageSizePenjualans;
	}

	public int getPageSizePenjualans() {
		return this.pageSizePenjualans;
	}

	public Window getPenjualanListWindow() {
		return this.penjualanListWindow;
	}	

	public void setPlwPenjualans(PagedListWrapper<Penjualan> plwPenjualans) {
		this.plwPenjualans = plwPenjualans;
	}

	public PagedListWrapper<Penjualan> getPlwPenjualans() {
		return this.plwPenjualans;
	}
	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public HibernateSearchObject<Penjualan> getSearchObj() {
		return searchObj;
	}

	public void setSearchObj(HibernateSearchObject<Penjualan> searchObj) {
		this.searchObj = searchObj;
	}
}
