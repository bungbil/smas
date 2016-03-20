package billy.webui.transaction.penjualan;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Status;
import billy.backend.model.Wilayah;
import billy.backend.service.PenjualanService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PenjualanMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PenjualanMainCtrl.class);

	
	protected Window windowPenjualanMain; // autowired

	// Tabs
	protected Tabbox tabbox_PenjualanMain; // autowired
	protected Tab tabPenjualanList; // autowired
	protected Tab tabPenjualanDetail; // autowired
	protected Tabpanel tabPanelPenjualanList; // autowired
	protected Tabpanel tabPanelPenjualanDetail; // autowired

	// filter components
	protected Checkbox checkbox_PenjualanList_ShowAll; // autowired
	protected Textbox tb_Search_No_Faktur; // aurowired
	protected Textbox tb_Search_Nama_Pelanggan; // aurowired
	protected Textbox tb_Search_Alamat; // aurowired
	protected Button button_PenjualanList_Search; // aurowired
	
	
	

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_PenjualanMain_";
	private ButtonStatusCtrl btnCtrlPenjualan;
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
	private PenjualanListCtrl penjualanListCtrl;
	private PenjualanDetailCtrl penjualanDetailCtrl;

	// Databinding
	private Penjualan selectedPenjualan;
	private BindingListModelList penjualans;

	// ServiceDAOs / Domain Classes
	private PenjualanService penjualanService;

	// always a copy from the bean before modifying. Used for reseting
	private Penjualan originalPenjualan;

	DecimalFormat df = new DecimalFormat("#,###");
	/**
	 * default constructor.<br>
	 */
	public PenjualanMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the penjualan 'recurse' to 'false' to avoid problems with
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
	public void onCreate$windowPenjualanMain(Event event) throws Exception {
		windowPenjualanMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlPenjualan = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabPenjualanList.setSelected(true);

		if (tabPanelPenjualanList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelPenjualanList, this, "ModuleMainController", "/WEB-INF/pages/transaction/penjualan/penjualanList.zul");
		}

		// init the buttons for editMode
		btnCtrlPenjualan.setInitEdit();
	}

	/**
	 * When the tab 'tabPenjualanList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabPenjualanList(Event event) throws IOException {
		//logger.debug(event.toString());
		
		// Check if the tabpanel is already loaded
		if (tabPanelPenjualanList.getFirstChild() != null) {
			tabPenjualanList.setSelected(true);

			return;
		}

		if (tabPanelPenjualanList != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelPenjualanList, this, "ModuleMainController", "/WEB-INF/pages/transaction/penjualan/penjualanList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelPenjualanDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabPenjualanDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelPenjualanDetail.getFirstChild() != null) {
			tabPenjualanDetail.setSelected(true);

			// refresh the Binding mechanism
			getPenjualanDetailCtrl().setPenjualan(getSelectedPenjualan());
			getPenjualanDetailCtrl().getBinder().loadAll();
			getPenjualanDetailCtrl().doRefresh();
			return;
		}

		if (tabPanelPenjualanDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(this.tabPanelPenjualanDetail, this, "ModuleMainController", "/WEB-INF/pages/transaction/penjualan/penjualanDetail.zul");
		}
	}


	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_PenjualanList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		tb_Search_No_Faktur.setValue(""); // clear
		tb_Search_Nama_Pelanggan.setValue(""); // clear
		tb_Search_Alamat.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Penjualan> soPenjualan = new HibernateSearchObject<Penjualan>(Penjualan.class, getPenjualanListCtrl().getCountRows());
		soPenjualan.addSort("noFaktur", false);

		// Change the BindingListModel.
		if (getPenjualanListCtrl().getBinder() != null) {
			getPenjualanListCtrl().getPagedBindingListWrapper().setSearchObject(soPenjualan);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_PenjualanMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabPenjualanList)) {
				tabPenjualanList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	

	/**
	 * Filter the penjualan list <br>
	 */
	public void onClick$button_PenjualanList_Search(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())
				|| StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())
				|| StringUtils.isNotEmpty(tb_Search_Alamat.getValue())				
				) {
			checkbox_PenjualanList_ShowAll.setChecked(false); // unCheck
	
			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Penjualan> soPenjualan = new HibernateSearchObject<Penjualan>(Penjualan.class, getPenjualanListCtrl().getCountRows());
			// check which field have input
			if (StringUtils.isNotEmpty(tb_Search_No_Faktur.getValue())) {
				soPenjualan.addFilter(new Filter("noFaktur", tb_Search_No_Faktur.getValue(), Filter.OP_EQUAL));
			}

			if (StringUtils.isNotEmpty(tb_Search_Nama_Pelanggan.getValue())) {
				soPenjualan.addFilter(new Filter("namaPelanggan", "%" + tb_Search_Nama_Pelanggan.getValue().toUpperCase() + "%", Filter.OP_ILIKE));
			}

			if (StringUtils.isNotEmpty(tb_Search_Alamat.getValue())) {
				soPenjualan.addFilter(new Filter("alamat", "%" + tb_Search_Alamat.getValue() + "%", Filter.OP_ILIKE));
			}
			
			// Change the BindingListModel.
			if (getPenjualanListCtrl().getBinder() != null) {
				getPenjualanListCtrl().getPagedBindingListWrapper().setSearchObject(soPenjualan);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_PenjualanMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabPenjualanList)) {
					tabPenjualanList.setSelected(true);
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
	
	public long getDiffDays(Date start, Date end){
		long diff = end.getTime() - start.getTime();
		
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays;
	}
	public void onClick$btnSave(Event event) throws InterruptedException {
		
		/*validasi butuh approval
		- tgl angsuran ke 2 > 45 hari tgl penjualan
		- harga barang berubah
	
		*/
		String message = "";
		if(getDiffDays(getPenjualanDetailCtrl().getPenjualan().getTglPenjualan(), getPenjualanDetailCtrl().getPenjualan().getTglAngsuran2()) > 45 ){
			message += "- Tanggal Angsuran ke 2 melebihi 45 hari \n";
		}
		
		int interval = Integer.parseInt(getPenjualanDetailCtrl().cmb_IntervalKredit.getValue());
		for(PenjualanDetail penjualanDetail : getPenjualanDetailCtrl().getListPenjualanDetail()){
			BigDecimal hargaBarang = getPenjualanDetailCtrl().getBarangService().getHargaBarangByIntervalKredit(penjualanDetail.getBarang(), interval);
			BigDecimal hargaBarangDiList = penjualanDetail.getHarga();
			
			if(hargaBarang.compareTo(hargaBarangDiList)!=0){
				message += "- Harga Barang "+penjualanDetail.getBarang().getNamaBarang()+" berubah dari "+ df.format(hargaBarang)+" ke "+df.format(hargaBarangDiList)+" \n";
			}			
		}		
		
		if(message!=""){
			getPenjualanDetailCtrl().getPenjualan().setNeedApproval(true);
			getPenjualanDetailCtrl().getPenjualan().setReasonApproval(message);
			getPenjualanDetailCtrl().label_butuhApproval.setValue("Ya");
		}else{
			getPenjualanDetailCtrl().getPenjualan().setNeedApproval(false);
			getPenjualanDetailCtrl().getPenjualan().setReasonApproval("");
			getPenjualanDetailCtrl().label_butuhApproval.setValue("Tidak");
		}
		
		if(getSelectedPenjualan().isNeedApproval()){
			final Penjualan anPenjualan = getSelectedPenjualan();
			if (anPenjualan != null) {
	
				// Show a confirm box
				String msg = message;
				final String title = Labels.getLabel("message.Information");
	
				MultiLineMessageBox.doSetTemplate();
				if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
					@Override
					public void onEvent(Event evt) {
						switch (((Integer) evt.getData()).intValue()) {
						case MultiLineMessageBox.YES:
							try {
								doSave(evt);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break; //
						case MultiLineMessageBox.NO:
							break; //
						}
					}				
				}
				) == MultiLineMessageBox.YES) {
				}
			}	
		}else{
			doSave(event);
		}
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
		if (getPenjualanDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getPenjualanDetailCtrl().getBinder().loadAll();
			getPenjualanDetailCtrl().doRefresh();
			// set editable Mode
			getPenjualanDetailCtrl().doReadOnlyMode(true);

			btnCtrlPenjualan.setInitEdit();
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
		Tab currentTab = tabbox_PenjualanMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getPenjualanDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabPenjualanDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getPenjualanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabPenjualanDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabPenjualanDetail)) {
			tabPenjualanDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlPenjualan.setBtnStatus_Edit();

		getPenjualanDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getPenjualanDetailCtrl().getBinder().loadAll();
		getPenjualanDetailCtrl().doRefresh();
		// set focus
		getPenjualanDetailCtrl().txtb_NoFaktur.focus();
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
		if (getPenjualanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabPenjualanDetail, null));
		}

		// check first, if the tabs are created
		if (getPenjualanDetailCtrl().getBinder() == null) {
			return;
		}

		final Penjualan anPenjualan = getSelectedPenjualan();
		if (anPenjualan != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anPenjualan.getNoFaktur();
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
						getPenjualanService().delete(anPenjualan);
					} catch (DataAccessException e) {
						ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlPenjualan.setInitEdit();

		setSelectedPenjualan(null);
		// refresh the list
		getPenjualanListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getPenjualanDetailCtrl().getBinder().loadAll();
		getPenjualanDetailCtrl().doRefresh();
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
		getPenjualanDetailCtrl().getBinder().saveAll();

		try {
			/* if a job type is selected get the object from the listbox */
			Listitem item = getPenjualanDetailCtrl().lbox_Wilayah.getSelectedItem();
			if (item != null) {
				ListModelList lml1 = (ListModelList) getPenjualanDetailCtrl().lbox_Wilayah.getListModel();
				Wilayah wilayah = (Wilayah) lml1.get(item.getIndex());
				getPenjualanDetailCtrl().getPenjualan().setWilayah(wilayah);				
			}
			
			Listitem itemStatus = getPenjualanDetailCtrl().lbox_Status.getSelectedItem();
			if (itemStatus != null) {
				ListModelList lml1 = (ListModelList) getPenjualanDetailCtrl().lbox_Status.getListModel();
				Status status = (Status) lml1.get(itemStatus.getIndex());
				getPenjualanDetailCtrl().getPenjualan().setStatus(status);				
			}
			
			Listitem itemSales1 = getPenjualanDetailCtrl().lbox_Sales1.getSelectedItem();
			if (itemSales1 != null) {
				ListModelList lml1 = (ListModelList) getPenjualanDetailCtrl().lbox_Sales1.getListModel();
				Karyawan karyawan = (Karyawan) lml1.get(itemSales1.getIndex());
				getPenjualanDetailCtrl().getPenjualan().setSales1(karyawan);
				getPenjualanDetailCtrl().getPenjualan().setDivisi(karyawan.getSupervisorDivisi());
			}
			
			Listitem itemSales2 = getPenjualanDetailCtrl().lbox_Sales2.getSelectedItem();
			if (itemSales2 != null) {
				ListModelList lml1 = (ListModelList) getPenjualanDetailCtrl().lbox_Sales2.getListModel();
				Karyawan karyawan = (Karyawan) lml1.get(itemSales2.getIndex());
				getPenjualanDetailCtrl().getPenjualan().setSales2(karyawan);				
			}
			
			Listitem itemPengirim = getPenjualanDetailCtrl().lbox_Pengirim.getSelectedItem();
			if (itemPengirim != null) {
				ListModelList lml1 = (ListModelList) getPenjualanDetailCtrl().lbox_Pengirim.getListModel();
				Karyawan karyawan = (Karyawan) lml1.get(itemPengirim.getIndex());
				getPenjualanDetailCtrl().getPenjualan().setPengirim(karyawan);				
			}
			
			if(getPenjualanDetailCtrl().cmb_IntervalKredit.getValue()!=null){
				int intervalKredit = Integer.parseInt(getPenjualanDetailCtrl().cmb_IntervalKredit.getValue());
				getPenjualanDetailCtrl().getPenjualan().setIntervalKredit(intervalKredit);
			}
			
			if(getPenjualanDetailCtrl().radioStatusCash.isSelected()){
				getPenjualanDetailCtrl().getPenjualan().setMetodePembayaran(getPenjualanDetailCtrl().radioStatusCash.getLabel());
			}
			if(getPenjualanDetailCtrl().radioStatusKredit.isSelected()){
				getPenjualanDetailCtrl().getPenjualan().setMetodePembayaran(getPenjualanDetailCtrl().radioStatusKredit.getLabel());
			}
			
			getPenjualanDetailCtrl().getPenjualan().setPiutang(getPenjualanDetailCtrl().getPenjualan().getGrandTotal());	
			
			String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();				
			getPenjualanDetailCtrl().getPenjualan().setLastUpdate(new Date());			
			getPenjualanDetailCtrl().getPenjualan().setUpdatedBy(userName);	
						
			// save it to database
			getPenjualanService().saveOrUpdate(getPenjualanDetailCtrl().getPenjualan());
			
			//getPenjualanService().deletePenjualanDetailsByPenjualan(getPenjualanDetailCtrl().getPenjualan());
			for(PenjualanDetail penjualanDetail : getPenjualanDetailCtrl().getListPenjualanDetail()){
				getPenjualanService().saveOrUpdate(penjualanDetail);
			}
			
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getPenjualanListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getPenjualanListCtrl().getListBoxPenjualan(), getSelectedPenjualan());

			// show the objects data in the statusBar
			String str = getSelectedPenjualan().getNoFaktur();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlPenjualan.setInitEdit();
			getPenjualanDetailCtrl().doReadOnlyMode(true);
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
		if (getPenjualanDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabPenjualanDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getPenjualanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabPenjualanDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Penjualan anPenjualan = getPenjualanService().getNewPenjualan();

		// set the beans in the related databinded controllers
		getPenjualanDetailCtrl().setPenjualan(anPenjualan);
		getPenjualanDetailCtrl().setSelectedPenjualan(anPenjualan);

		// Refresh the binding mechanism
		getPenjualanDetailCtrl().setSelectedPenjualan(getSelectedPenjualan());
		
		try{
			getPenjualanDetailCtrl().doRefresh();			
			getPenjualanDetailCtrl().getBinder().loadAll();
			
		}catch(Exception e){
			//do nothing
		}
		//getPenjualanDetailCtrl().emptyAllValue();
		// set editable Mode
		getPenjualanDetailCtrl().doReadOnlyMode(false);
		// set the ButtonStatus to New-Mode
		btnCtrlPenjualan.setInitNew();

		tabPenjualanDetail.setSelected(true);
		// set focus
		getPenjualanDetailCtrl().txtb_NoFaktur.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getPenjualanListCtrl().getListBoxPenjualan().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedPenjualan());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_PenjualanMain.getSelectedTab();

		if (getPenjualanDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabPenjualanDetail, null));
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

		getPenjualanListCtrl().getListBoxPenjualan().setSelectedIndex(index);
		setSelectedPenjualan((Penjualan) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getPenjualanListCtrl().getListBoxPenjualan(), getSelectedPenjualan()));

		// refresh master-detail MASTERS data
		getPenjualanDetailCtrl().getBinder().loadAll();
		getPenjualanDetailCtrl().doRefresh();
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

		if (tabbox_PenjualanMain.getSelectedTab() == tabPenjualanDetail) {
			getPenjualanDetailCtrl().doFitSize(event);
		} else if (tabbox_PenjualanMain.getSelectedTab() == tabPenjualanList) {
			// resize and fill Listbox new
			getPenjualanListCtrl().doFillListbox();
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

		if (getSelectedPenjualan() != null) {

			try {
				setOriginalPenjualan((Penjualan) ZksampleBeanUtils.cloneBean(getSelectedPenjualan()));
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

		if (getOriginalPenjualan() != null) {

			try {
				setSelectedPenjualan((Penjualan) ZksampleBeanUtils.cloneBean(getOriginalPenjualan()));
				// TODO Bug in DataBinder??
				windowPenjualanMain.invalidate();

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
		button_PenjualanList_Search.setVisible(workspace.isAllowed("button_PenjualanList_Search"));
		tabPenjualanList.setVisible(workspace.isAllowed("windowPenjualanList"));
		tabPenjualanDetail.setVisible(workspace.isAllowed("windowPenjualanDetail"));
		btnEdit.setVisible(workspace.isAllowed("button_PenjualanMain_btnEdit"));
		btnNew.setVisible(workspace.isAllowed("button_PenjualanMain_btnNew"));
		btnDelete.setVisible(workspace.isAllowed("button_PenjualanMain_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_PenjualanMain_btnSave"));		
		btnCancel.setVisible(workspace.isAllowed("button_PenjualanMain_btnCancel"));
		btnFirst.setVisible(workspace.isAllowed("button_PenjualanMain_btnFirst"));
		btnPrevious.setVisible(workspace.isAllowed("button_PenjualanMain_btnPrevious"));
		btnNext.setVisible(workspace.isAllowed("button_PenjualanMain_btnNext"));
		btnLast.setVisible(workspace.isAllowed("button_PenjualanMain_btnLast"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalPenjualan(Penjualan originalPenjualan) {
		this.originalPenjualan = originalPenjualan;
	}

	public Penjualan getOriginalPenjualan() {
		return this.originalPenjualan;
	}

	public void setSelectedPenjualan(Penjualan selectedPenjualan) {
		this.selectedPenjualan = selectedPenjualan;
	}

	public Penjualan getSelectedPenjualan() {
		return this.selectedPenjualan;
	}

	public void setPenjualans(BindingListModelList penjualans) {
		this.penjualans = penjualans;
	}

	public BindingListModelList getPenjualans() {
		return this.penjualans;
	}

	/* CONTROLLERS */
	public void setPenjualanListCtrl(PenjualanListCtrl penjualanListCtrl) {
		this.penjualanListCtrl = penjualanListCtrl;
	}

	public PenjualanListCtrl getPenjualanListCtrl() {
		return this.penjualanListCtrl;
	}

	public void setPenjualanDetailCtrl(PenjualanDetailCtrl penjualanDetailCtrl) {
		this.penjualanDetailCtrl = penjualanDetailCtrl;
	}

	public PenjualanDetailCtrl getPenjualanDetailCtrl() {
		return this.penjualanDetailCtrl;
	}

	/* SERVICES */
	public PenjualanService getPenjualanService() {
		return this.penjualanService;
	}

	public void setPenjualanService(PenjualanService penjualanService) {
		this.penjualanService = penjualanService;
	}

	/* COMPONENTS and OTHERS */
}
