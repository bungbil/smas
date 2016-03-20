package billy.webui.master.barang;

import java.io.Serializable;
import java.util.List;


import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.Barang;
import billy.backend.model.Wilayah;
import billy.backend.service.BarangService;
import billy.backend.service.WilayahService;
import billy.webui.master.wilayah.model.WilayahListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class BarangDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(BarangDetailCtrl.class);

	protected Window windowBarangDetail; // autowired

	protected Borderlayout borderlayout_BarangDetail; // autowired

	protected Textbox txtb_KodeBarang; // autowired
	protected Textbox txtb_NamaBarang; // autowired
	protected Decimalbox txtb_DivisiOpr; // autowired
	protected Decimalbox txtb_DivisiOr; // autowired
	protected Listbox lbox_Wilayah;	
	
	protected Decimalbox txtb_HargaBarang1x; // autowired
	protected Decimalbox txtb_CicilanPerBulan1x; // autowired
	protected Decimalbox txtb_KomisiSales1x; // autowired
	protected Decimalbox txtb_TabunganSales1x; // autowired

	protected Decimalbox txtb_HargaBarang2x; // autowired
	protected Decimalbox txtb_CicilanPerBulan2x; // autowired
	protected Decimalbox txtb_KomisiSales2x; // autowired
	protected Decimalbox txtb_TabunganSales2x; // autowired

	protected Decimalbox txtb_HargaBarang3x; // autowired
	protected Decimalbox txtb_CicilanPerBulan3x; // autowired
	protected Decimalbox txtb_KomisiSales3x; // autowired
	protected Decimalbox txtb_TabunganSales3x; // autowired

	protected Decimalbox txtb_HargaBarang4x; // autowired
	protected Decimalbox txtb_CicilanPerBulan4x; // autowired
	protected Decimalbox txtb_KomisiSales4x; // autowired
	protected Decimalbox txtb_TabunganSales4x; // autowired

	protected Decimalbox txtb_HargaBarang5x; // autowired
	protected Decimalbox txtb_CicilanPerBulan5x; // autowired
	protected Decimalbox txtb_KomisiSales5x; // autowired
	protected Decimalbox txtb_TabunganSales5x; // autowired

	protected Decimalbox txtb_HargaBarang6x; // autowired
	protected Decimalbox txtb_CicilanPerBulan6x; // autowired
	protected Decimalbox txtb_KomisiSales6x; // autowired
	protected Decimalbox txtb_TabunganSales6x; // autowired

	protected Decimalbox txtb_HargaBarang7x; // autowired
	protected Decimalbox txtb_CicilanPerBulan7x; // autowired
	protected Decimalbox txtb_KomisiSales7x; // autowired
	protected Decimalbox txtb_TabunganSales7x; // autowired

	protected Decimalbox txtb_HargaBarang8x; // autowired
	protected Decimalbox txtb_CicilanPerBulan8x; // autowired
	protected Decimalbox txtb_KomisiSales8x; // autowired
	protected Decimalbox txtb_TabunganSales8x; // autowired

	protected Decimalbox txtb_HargaBarang9x; // autowired
	protected Decimalbox txtb_CicilanPerBulan9x; // autowired
	protected Decimalbox txtb_KomisiSales9x; // autowired
	protected Decimalbox txtb_TabunganSales9x; // autowired

	protected Decimalbox txtb_HargaBarang10x; // autowired
	protected Decimalbox txtb_CicilanPerBulan10x; // autowired
	protected Decimalbox txtb_KomisiSales10x; // autowired
	protected Decimalbox txtb_TabunganSales10x; // autowired
	
	// Databinding
	protected transient AnnotateDataBinder binder;
	private BarangMainCtrl barangMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient BarangService barangService;
	private transient WilayahService wilayahService;

	/**
	 * default constructor.<br>
	 */
	public BarangDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setBarangMainCtrl((BarangMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getBarangMainCtrl().setBarangDetailCtrl(this);
			
			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getBarangMainCtrl().getSelectedBarang() != null) {
				setSelectedBarang(getBarangMainCtrl().getSelectedBarang());		
				
				// +++++++++ DropDown ListBox
				// set listModel and itemRenderer for the dropdown listbox				
				// if available, select the object
				doRefresh();
			} else
				setSelectedBarang(null);
		} else {
			setSelectedBarang(null);
		}

	}
	
	public void doRefresh(){
		List<Wilayah> listWilayah= getWilayahService().getAllWilayahs();			
		lbox_Wilayah.setModel(new ListModelList(listWilayah));
		lbox_Wilayah.setItemRenderer(new WilayahListModelItemRenderer());
		
		if(getSelectedBarang().getWilayah() != null){
			ListModelList lml = (ListModelList) lbox_Wilayah.getModel();		
			Wilayah wilayah = getWilayahService().getWilayahByID(getSelectedBarang().getWilayah().getId());
			lbox_Wilayah.setSelectedIndex(lml.indexOf(wilayah));					
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
	public void onCreate$windowBarangDetail(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		binder.loadAll();

		doFitSize(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void doFitSize(Event event) {

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - 152;
		borderlayout_BarangDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowBarangDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {
		txtb_KodeBarang.setReadonly(b);
		txtb_NamaBarang.setReadonly(b);		
		txtb_DivisiOpr.setReadonly(b);
		txtb_DivisiOr.setReadonly(b);
		lbox_Wilayah.setDisabled(b);
		
		txtb_HargaBarang1x.setReadonly(b);
		txtb_CicilanPerBulan1x.setReadonly(b);		
		txtb_KomisiSales1x.setReadonly(b);
		txtb_TabunganSales1x.setReadonly(b);
		
		txtb_HargaBarang2x.setReadonly(b);
		txtb_CicilanPerBulan2x.setReadonly(b);		
		txtb_KomisiSales2x.setReadonly(b);
		txtb_TabunganSales2x.setReadonly(b);
		
		txtb_HargaBarang3x.setReadonly(b);
		txtb_CicilanPerBulan3x.setReadonly(b);		
		txtb_KomisiSales3x.setReadonly(b);
		txtb_TabunganSales3x.setReadonly(b);
		
		txtb_HargaBarang4x.setReadonly(b);
		txtb_CicilanPerBulan4x.setReadonly(b);		
		txtb_KomisiSales4x.setReadonly(b);
		txtb_TabunganSales4x.setReadonly(b);
		
		txtb_HargaBarang5x.setReadonly(b);
		txtb_CicilanPerBulan5x.setReadonly(b);		
		txtb_KomisiSales5x.setReadonly(b);
		txtb_TabunganSales5x.setReadonly(b);
		
		txtb_HargaBarang6x.setReadonly(b);
		txtb_CicilanPerBulan6x.setReadonly(b);		
		txtb_KomisiSales6x.setReadonly(b);
		txtb_TabunganSales6x.setReadonly(b);
		
		txtb_HargaBarang7x.setReadonly(b);
		txtb_CicilanPerBulan7x.setReadonly(b);		
		txtb_KomisiSales7x.setReadonly(b);
		txtb_TabunganSales7x.setReadonly(b);
		
		txtb_HargaBarang8x.setReadonly(b);
		txtb_CicilanPerBulan8x.setReadonly(b);		
		txtb_KomisiSales8x.setReadonly(b);
		txtb_TabunganSales8x.setReadonly(b);
		
		txtb_HargaBarang9x.setReadonly(b);
		txtb_CicilanPerBulan9x.setReadonly(b);		
		txtb_KomisiSales9x.setReadonly(b);
		txtb_TabunganSales9x.setReadonly(b);
		
		txtb_HargaBarang10x.setReadonly(b);
		txtb_CicilanPerBulan10x.setReadonly(b);		
		txtb_KomisiSales10x.setReadonly(b);
		txtb_TabunganSales10x.setReadonly(b);
		
	}

	public void onOK$txtb_KodeBarang(Event event) throws InterruptedException {		
		txtb_NamaBarang.focus();			
	}
	public void onOK$txtb_NamaBarang(Event event) throws InterruptedException {		
		lbox_Wilayah.focus();			
	}
	
	public void onOK$txtb_DivisiOpr(Event event) throws InterruptedException {		
		txtb_DivisiOr.focus();			
	}
	public void onOK$txtb_DivisiOr(Event event) throws InterruptedException {		
		txtb_HargaBarang1x.focus();			
	}
	public void onOK$txtb_HargaBarang1x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan1x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan1x(Event event) throws InterruptedException {		
		txtb_KomisiSales1x.focus();			
	}
	public void onOK$txtb_KomisiSales1x(Event event) throws InterruptedException {		
		txtb_TabunganSales1x.focus();			
	}
	public void onOK$txtb_TabunganSales1x(Event event) throws InterruptedException {		
		txtb_HargaBarang2x.focus();			
	}
	public void onOK$txtb_HargaBarang2x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan2x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan2x(Event event) throws InterruptedException {		
		txtb_KomisiSales2x.focus();			
	}
	public void onOK$txtb_KomisiSales2x(Event event) throws InterruptedException {		
		txtb_TabunganSales2x.focus();			
	}
	
	public void onOK$txtb_TabunganSales2x(Event event) throws InterruptedException {		
		txtb_HargaBarang3x.focus();			
	}
	public void onOK$txtb_HargaBarang3x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan3x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan3x(Event event) throws InterruptedException {		
		txtb_KomisiSales3x.focus();			
	}
	public void onOK$txtb_KomisiSales3x(Event event) throws InterruptedException {		
		txtb_TabunganSales3x.focus();			
	}
	public void onOK$txtb_TabunganSales3x(Event event) throws InterruptedException {		
		txtb_HargaBarang4x.focus();			
	}
	public void onOK$txtb_HargaBarang4x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan4x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan4x(Event event) throws InterruptedException {		
		txtb_KomisiSales4x.focus();			
	}
	public void onOK$txtb_KomisiSales4x(Event event) throws InterruptedException {		
		txtb_TabunganSales4x.focus();			
	}
	public void onOK$txtb_TabunganSales4x(Event event) throws InterruptedException {		
		txtb_HargaBarang5x.focus();			
	}
	public void onOK$txtb_HargaBarang5x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan5x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan5x(Event event) throws InterruptedException {		
		txtb_KomisiSales5x.focus();			
	}
	public void onOK$txtb_KomisiSales5x(Event event) throws InterruptedException {		
		txtb_TabunganSales5x.focus();			
	}
	public void onOK$txtb_TabunganSales5x(Event event) throws InterruptedException {		
		txtb_HargaBarang6x.focus();			
	}
	public void onOK$txtb_HargaBarang6x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan6x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan6x(Event event) throws InterruptedException {		
		txtb_KomisiSales6x.focus();			
	}
	public void onOK$txtb_KomisiSales6x(Event event) throws InterruptedException {		
		txtb_TabunganSales6x.focus();			
	}
	public void onOK$txtb_TabunganSales6x(Event event) throws InterruptedException {		
		txtb_HargaBarang7x.focus();			
	}
	public void onOK$txtb_HargaBarang7x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan7x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan7x(Event event) throws InterruptedException {		
		txtb_KomisiSales7x.focus();			
	}
	public void onOK$txtb_KomisiSales7x(Event event) throws InterruptedException {		
		txtb_TabunganSales7x.focus();			
	}
	public void onOK$txtb_TabunganSales7x(Event event) throws InterruptedException {		
		txtb_HargaBarang8x.focus();			
	}
	public void onOK$txtb_HargaBarang8x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan8x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan8x(Event event) throws InterruptedException {		
		txtb_KomisiSales8x.focus();			
	}
	public void onOK$txtb_KomisiSales8x(Event event) throws InterruptedException {		
		txtb_TabunganSales8x.focus();			
	}
	public void onOK$txtb_TabunganSales8x(Event event) throws InterruptedException {		
		txtb_HargaBarang9x.focus();			
	}
	public void onOK$txtb_HargaBarang9x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan9x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan9x(Event event) throws InterruptedException {		
		txtb_KomisiSales9x.focus();			
	}
	public void onOK$txtb_KomisiSales9x(Event event) throws InterruptedException {		
		txtb_TabunganSales9x.focus();			
	}
	public void onOK$txtb_TabunganSales9x(Event event) throws InterruptedException {		
		txtb_HargaBarang10x.focus();			
	}
	public void onOK$txtb_HargaBarang10x(Event event) throws InterruptedException {		
		txtb_CicilanPerBulan10x.focus();			
	}
	public void onOK$txtb_CicilanPerBulan10x(Event event) throws InterruptedException {		
		txtb_KomisiSales10x.focus();			
	}
	public void onOK$txtb_KomisiSales10x(Event event) throws InterruptedException {		
		txtb_TabunganSales10x.focus();			
	}
	public void onOK$txtb_TabunganSales10x(Event event) throws InterruptedException {		
		getBarangMainCtrl().btnSave.focus();			
	}
	
	public void onBlur$txtb_KodeBarang(Event event) throws InterruptedException {
		
		Barang barangCheckKode = null;
		barangCheckKode = getBarangService().getBarangByKodeBarang(getBarang().getKodeBarang());
		
		if(barangCheckKode!=null){
			if(barangCheckKode.getId()!=getBarang().getId()){
				ZksampleMessageUtils.showErrorMessage("Kode Barang sudah digunakan oleh barang : " +barangCheckKode.getNamaBarang());
				return;
			}
		}		
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public Barang getBarang() {
		// STORED IN THE module's MainController
		return getBarangMainCtrl().getSelectedBarang();
	}

	public void setBarang(Barang anBarang) {
		// STORED IN THE module's MainController
		getBarangMainCtrl().setSelectedBarang(anBarang);
	}

	public Barang getSelectedBarang() {
		// STORED IN THE module's MainController
		return getBarangMainCtrl().getSelectedBarang();
	}

	public void setSelectedBarang(Barang selectedBarang) {
		// STORED IN THE module's MainController
		getBarangMainCtrl().setSelectedBarang(selectedBarang);
	}

	public BindingListModelList getBarangs() {
		// STORED IN THE module's MainController
		return getBarangMainCtrl().getBarangs();
	}

	public void setBarangs(BindingListModelList barangs) {
		// STORED IN THE module's MainController
		getBarangMainCtrl().setBarangs(barangs);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
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

	public WilayahService getWilayahService() {
		return wilayahService;
	}

	public void setWilayahService(WilayahService wilayahService) {
		this.wilayahService = wilayahService;
	}

	/* COMPONENTS and OTHERS */

}
