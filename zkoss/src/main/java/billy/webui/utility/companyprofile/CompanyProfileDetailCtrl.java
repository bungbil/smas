package billy.webui.utility.companyprofile;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.CompanyProfile;
import billy.backend.service.CompanyProfileService;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class CompanyProfileDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(CompanyProfileDetailCtrl.class);

	protected Window windowCompanyProfileDetail; // autowired

	protected Borderlayout borderlayout_CompanyProfileDetail; // autowired

	protected Textbox txtb_CompanyName; // autowired
	protected Textbox txtb_Address; // autowired
	protected Textbox txtb_Phone; // autowired
	protected Textbox txtb_Email; // autowired
	

	// Databinding
	protected transient AnnotateDataBinder binder;
	private CompanyProfileMainCtrl companyProfileMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient CompanyProfileService companyProfileService;

	/**
	 * default constructor.<br>
	 */
	public CompanyProfileDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setCompanyProfileMainCtrl((CompanyProfileMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getCompanyProfileMainCtrl().setCompanyProfileDetailCtrl(this);
		}
		setSelectedCompanyProfile(getCompanyProfileService().getCompanyProfileByID(new Long(1)));
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
	public void onCreate$windowCompanyProfileDetail(Event event) throws Exception {
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
		borderlayout_CompanyProfileDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowCompanyProfileDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {
		txtb_CompanyName.setReadonly(b);
		txtb_Address.setReadonly(b);
		txtb_Phone.setReadonly(b);
		txtb_Email.setReadonly(b);
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public CompanyProfile getCompanyProfile() {
		// STORED IN THE module's MainController
		return getCompanyProfileMainCtrl().getSelectedCompanyProfile();
	}

	public void setCompanyProfile(CompanyProfile anCompanyProfile) {
		// STORED IN THE module's MainController
		getCompanyProfileMainCtrl().setSelectedCompanyProfile(anCompanyProfile);
	}

	public CompanyProfile getSelectedCompanyProfile() {
		// STORED IN THE module's MainController
		return getCompanyProfileMainCtrl().getSelectedCompanyProfile();
	}

	public void setSelectedCompanyProfile(CompanyProfile selectedCompanyProfile) {
		// STORED IN THE module's MainController
		getCompanyProfileMainCtrl().setSelectedCompanyProfile(selectedCompanyProfile);
	}

	public BindingListModelList getCompanyProfiles() {
		// STORED IN THE module's MainController
		return getCompanyProfileMainCtrl().getCompanyProfiles();
	}

	public void setCompanyProfiles(BindingListModelList companyProfiles) {
		// STORED IN THE module's MainController
		getCompanyProfileMainCtrl().setCompanyProfiles(companyProfiles);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setCompanyProfileMainCtrl(CompanyProfileMainCtrl companyProfileMainCtrl) {
		this.companyProfileMainCtrl = companyProfileMainCtrl;
	}

	public CompanyProfileMainCtrl getCompanyProfileMainCtrl() {
		return this.companyProfileMainCtrl;
	}

	/* SERVICES */
	public void setCompanyProfileService(CompanyProfileService companyProfileService) {
		this.companyProfileService = companyProfileService;
	}

	public CompanyProfileService getCompanyProfileService() {
		return this.companyProfileService;
	}

	/* COMPONENTS and OTHERS */

}
