package billy.webui.master.barang;

import java.io.Serializable;

import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.Barang;
import billy.backend.model.JobType;
import billy.backend.model.Wilayah;
import billy.backend.service.BarangService;
import billy.backend.service.WilayahService;
import billy.webui.master.jobtype.model.JobTypeListModelItemRenderer;
import billy.webui.master.wilayah.model.WilayahListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;

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

			lbox_Wilayah.setModel(new ListModelList(getWilayahService().getAllWilayahs()));
			lbox_Wilayah.setItemRenderer(new WilayahListModelItemRenderer());
			
			
			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getBarangMainCtrl().getSelectedBarang() != null) {
				setSelectedBarang(getBarangMainCtrl().getSelectedBarang());		
				
				// +++++++++ DropDown ListBox
				// set listModel and itemRenderer for the dropdown listbox				
				// if available, select the object
				if(getSelectedBarang().getWilayah() != null){
					ListModelList lml = (ListModelList) lbox_Wilayah.getModel();		
					Wilayah wilayah = getWilayahService().getWilayahByID(getSelectedBarang().getWilayah().getId());
					lbox_Wilayah.setSelectedIndex(lml.indexOf(wilayah));					
				}	
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
