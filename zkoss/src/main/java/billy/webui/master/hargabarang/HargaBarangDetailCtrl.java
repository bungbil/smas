package billy.webui.master.hargabarang;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.HargaBarang;
import billy.backend.service.HargaBarangService;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class HargaBarangDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(HargaBarangDetailCtrl.class);

	protected Window windowHargaBarangDetail; // autowired

	protected Borderlayout borderlayout_HargaBarangDetail; // autowired

	protected Textbox txtb_KodeHargaBarang; // autowired
	protected Textbox txtb_NamaHargaBarang; // autowired
	protected Label txtb_Status; // autowired	
	protected Radiogroup radiogroup_Status; // autowired
	protected Radio radioStatusPusat;
	protected Radio radioStatusDaerah;
	// Databinding
	protected transient AnnotateDataBinder binder;
	private HargaBarangMainCtrl hargaBarangMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient HargaBarangService hargaBarangService;

	/**
	 * default constructor.<br>
	 */
	public HargaBarangDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setHargaBarangMainCtrl((HargaBarangMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getHargaBarangMainCtrl().setHargaBarangDetailCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
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
	public void onCreate$windowHargaBarangDetail(Event event) throws Exception {
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
		borderlayout_HargaBarangDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowHargaBarangDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {
		txtb_KodeHargaBarang.setReadonly(b);
		txtb_NamaHargaBarang.setReadonly(b);		
		radioStatusPusat.setDisabled(b);
		radioStatusDaerah.setDisabled(b);
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public HargaBarang getHargaBarang() {
		// STORED IN THE module's MainController
		return getHargaBarangMainCtrl().getSelectedHargaBarang();
	}

	public void setHargaBarang(HargaBarang anHargaBarang) {
		// STORED IN THE module's MainController
		getHargaBarangMainCtrl().setSelectedHargaBarang(anHargaBarang);
	}

	public HargaBarang getSelectedHargaBarang() {
		// STORED IN THE module's MainController
		return getHargaBarangMainCtrl().getSelectedHargaBarang();
	}

	public void setSelectedHargaBarang(HargaBarang selectedHargaBarang) {
		// STORED IN THE module's MainController
		getHargaBarangMainCtrl().setSelectedHargaBarang(selectedHargaBarang);
	}

	public BindingListModelList getHargaBarangs() {
		// STORED IN THE module's MainController
		return getHargaBarangMainCtrl().getHargaBarangs();
	}

	public void setHargaBarangs(BindingListModelList hargaBarangs) {
		// STORED IN THE module's MainController
		getHargaBarangMainCtrl().setHargaBarangs(hargaBarangs);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
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

}
