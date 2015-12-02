package billy.webui.master.jobtype;

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
import billy.backend.model.JobType;
import billy.backend.service.JobTypeService;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class JobTypeDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(JobTypeDetailCtrl.class);

	protected Window windowJobTypeDetail; // autowired

	protected Borderlayout borderlayout_JobTypeDetail; // autowired

	protected Textbox txtb_NamaJobType; // autowired

	// Databinding
	protected transient AnnotateDataBinder binder;
	private JobTypeMainCtrl jobTypeMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient JobTypeService jobTypeService;

	/**
	 * default constructor.<br>
	 */
	public JobTypeDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setJobTypeMainCtrl((JobTypeMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getJobTypeMainCtrl().setJobTypeDetailCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getJobTypeMainCtrl().getSelectedJobType() != null) {
				setSelectedJobType(getJobTypeMainCtrl().getSelectedJobType());				
			} else
				setSelectedJobType(null);
		} else {
			setSelectedJobType(null);
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
	public void onCreate$windowJobTypeDetail(Event event) throws Exception {
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
		borderlayout_JobTypeDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowJobTypeDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {		
		txtb_NamaJobType.setReadonly(b);				
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public JobType getJobType() {
		// STORED IN THE module's MainController
		return getJobTypeMainCtrl().getSelectedJobType();
	}

	public void setJobType(JobType anJobType) {
		// STORED IN THE module's MainController
		getJobTypeMainCtrl().setSelectedJobType(anJobType);
	}

	public JobType getSelectedJobType() {
		// STORED IN THE module's MainController
		return getJobTypeMainCtrl().getSelectedJobType();
	}

	public void setSelectedJobType(JobType selectedJobType) {
		// STORED IN THE module's MainController
		getJobTypeMainCtrl().setSelectedJobType(selectedJobType);
	}

	public BindingListModelList getJobTypes() {
		// STORED IN THE module's MainController
		return getJobTypeMainCtrl().getJobTypes();
	}

	public void setJobTypes(BindingListModelList jobTypes) {
		// STORED IN THE module's MainController
		getJobTypeMainCtrl().setJobTypes(jobTypes);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setJobTypeMainCtrl(JobTypeMainCtrl jobTypeMainCtrl) {
		this.jobTypeMainCtrl = jobTypeMainCtrl;
	}

	public JobTypeMainCtrl getJobTypeMainCtrl() {
		return this.jobTypeMainCtrl;
	}

	/* SERVICES */
	public void setJobTypeService(JobTypeService jobTypeService) {
		this.jobTypeService = jobTypeService;
	}

	public JobTypeService getJobTypeService() {
		return this.jobTypeService;
	}

	/* COMPONENTS and OTHERS */

}
