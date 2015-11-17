package billy.webui.utility.parameter;

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
import billy.backend.model.Parameter;
import billy.backend.service.ParameterService;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class ParameterDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(ParameterDetailCtrl.class);

	protected Window windowParameterDetail; // autowired

	protected Borderlayout borderlayout_ParameterDetail; // autowired

	protected Textbox txtb_ParamName; // autowired
	protected Textbox txtb_ParamValue; // autowired
	protected Textbox txtb_Description; // autowired
	protected Button button_ParameterDialog_PrintParameter; // autowired

	// Databinding
	protected transient AnnotateDataBinder binder;
	private ParameterMainCtrl parameterMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient ParameterService parameterService;

	/**
	 * default constructor.<br>
	 */
	public ParameterDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setParameterMainCtrl((ParameterMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getParameterMainCtrl().setParameterDetailCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getParameterMainCtrl().getSelectedParameter() != null) {
				setSelectedParameter(getParameterMainCtrl().getSelectedParameter());
			} else
				setSelectedParameter(null);
		} else {
			setSelectedParameter(null);
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
	public void onCreate$windowParameterDetail(Event event) throws Exception {
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
		borderlayout_ParameterDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowParameterDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {
		txtb_ParamName.setReadonly(b);
		txtb_ParamValue.setReadonly(b);
		txtb_Description.setReadonly(b);
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public Parameter getParameter() {
		// STORED IN THE module's MainController
		return getParameterMainCtrl().getSelectedParameter();
	}

	public void setParameter(Parameter anParameter) {
		// STORED IN THE module's MainController
		getParameterMainCtrl().setSelectedParameter(anParameter);
	}

	public Parameter getSelectedParameter() {
		// STORED IN THE module's MainController
		return getParameterMainCtrl().getSelectedParameter();
	}

	public void setSelectedParameter(Parameter selectedParameter) {
		// STORED IN THE module's MainController
		getParameterMainCtrl().setSelectedParameter(selectedParameter);
	}

	public BindingListModelList getParameters() {
		// STORED IN THE module's MainController
		return getParameterMainCtrl().getParameters();
	}

	public void setParameters(BindingListModelList parameters) {
		// STORED IN THE module's MainController
		getParameterMainCtrl().setParameters(parameters);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setParameterMainCtrl(ParameterMainCtrl parameterMainCtrl) {
		this.parameterMainCtrl = parameterMainCtrl;
	}

	public ParameterMainCtrl getParameterMainCtrl() {
		return this.parameterMainCtrl;
	}

	/* SERVICES */
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public ParameterService getParameterService() {
		return this.parameterService;
	}

	/* COMPONENTS and OTHERS */

}
