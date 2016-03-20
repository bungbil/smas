package billy.webui.master.bonustransport;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Listbox;
import de.forsthaus.UserWorkspace;
import billy.backend.model.BonusTransport;
import billy.backend.model.JobType;
import billy.backend.model.Status;
import billy.backend.service.BonusTransportService;
import billy.backend.service.JobTypeService;
import billy.webui.master.jobtype.model.JobTypeListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class BonusTransportDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(BonusTransportDetailCtrl.class);

	protected Window windowBonusTransportDetail; // autowired

	protected Borderlayout borderlayout_BonusTransportDetail; // autowired
	
	protected Textbox txtb_DeskripsiBonusTransport; // autowired	
	protected Listbox lbox_JobType; // autowired
	protected Checkbox txtb_MultipleUnit; // autowired
	protected Checkbox pusatCheckbox; // autowired
	protected Checkbox daerahCheckbox; // autowired
	
	protected Intbox txtb_StartRangeUnit; // autowired
	protected Intbox txtb_EndRangeUnit; // autowired
	protected Decimalbox txtb_Honor;
	protected Decimalbox txtb_Or;	
	protected Decimalbox txtb_Transport;
	protected Decimalbox txtb_Bonus;
	
	// Databinding
	protected transient AnnotateDataBinder binder;
	private BonusTransportMainCtrl bonusTransportMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient BonusTransportService bonusTransportService;	
	private JobTypeService jobTypeService;
	/**
	 * default constructor.<br>
	 */
	public BonusTransportDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setBonusTransportMainCtrl((BonusTransportMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			
			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getBonusTransportMainCtrl().getSelectedBonusTransport() != null) {
				setSelectedBonusTransport(getBonusTransportMainCtrl().getSelectedBonusTransport());		
				// +++++++++ DropDown ListBox
				// set listModel and itemRenderer for the dropdown listbox
				
				// if available, select the object
				doRefresh();
				
			} else
				setSelectedBonusTransport(null);
		} else {
			setSelectedBonusTransport(null);
		}
	}
	
	public void doRefresh(){
		getBonusTransportMainCtrl().setBonusTransportDetailCtrl(this);
		lbox_JobType.setModel(new ListModelList(getJobTypeService().getAllJobTypes()));
		lbox_JobType.setItemRenderer(new JobTypeListModelItemRenderer());
		
		
		if(getSelectedBonusTransport().getJobType() != null){
			ListModelList lml = (ListModelList) lbox_JobType.getModel();		
			JobType jobType = getJobTypeService().getJobTypeByID(getSelectedBonusTransport().getJobType().getId());
			lbox_JobType.setSelectedIndex(lml.indexOf(jobType));					
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
	public void onCreate$windowBonusTransportDetail(Event event) throws Exception {
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
		borderlayout_BonusTransportDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowBonusTransportDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {
		txtb_DeskripsiBonusTransport.setReadonly(b);
		lbox_JobType.setDisabled(b);		
		txtb_MultipleUnit.setDisabled(b);
		pusatCheckbox.setDisabled(b);
		daerahCheckbox.setDisabled(b);
		txtb_StartRangeUnit.setReadonly(b);
		txtb_EndRangeUnit.setReadonly(b);
		txtb_Honor.setReadonly(b);
		txtb_Or.setReadonly(b);		
		txtb_Transport.setReadonly(b);
		txtb_Bonus.setReadonly(b);
	}

	public void onOK$txtb_DeskripsiBonusTransport(Event event) throws InterruptedException {		
		lbox_JobType.focus();			
	}
	public void onOK$txtb_StartRangeUnit(Event event) throws InterruptedException {		
		txtb_EndRangeUnit.focus();			
	}
	public void onOK$txtb_EndRangeUnit(Event event) throws InterruptedException {		
		txtb_Honor.focus();			
	}
	public void onOK$txtb_Honor(Event event) throws InterruptedException {		
		txtb_Or.focus();			
	}
	public void onOK$txtb_Or(Event event) throws InterruptedException {		
		txtb_Transport.focus();			
	}
	public void onOK$txtb_Transport(Event event) throws InterruptedException {		
		txtb_Bonus.focus();			
	}
	public void onOK$txtb_Bonus(Event event) throws InterruptedException {		
		getBonusTransportMainCtrl().btnSave.focus();			
	}
	
	public void onBlur$txtb_DeskripsiBonusTransport(Event event) throws InterruptedException {
		
		BonusTransport bonusTransportCheckKode = null;
		bonusTransportCheckKode = getBonusTransportService().getBonusTransportByDeskripsiBonusTransport(getBonusTransport().getDeskripsiBonusTransport());
		
		if(bonusTransportCheckKode!=null){
			if(bonusTransportCheckKode.getId()!=getBonusTransport().getId()){
				ZksampleMessageUtils.showErrorMessage("Bonus Transport sudah ada");
				return;
			}
		}		
	}
	
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public BonusTransport getBonusTransport() {
		// STORED IN THE module's MainController
		return getBonusTransportMainCtrl().getSelectedBonusTransport();
	}

	public void setBonusTransport(BonusTransport anBonusTransport) {
		// STORED IN THE module's MainController
		getBonusTransportMainCtrl().setSelectedBonusTransport(anBonusTransport);
	}

	public BonusTransport getSelectedBonusTransport() {
		// STORED IN THE module's MainController
		return getBonusTransportMainCtrl().getSelectedBonusTransport();
	}

	public void setSelectedBonusTransport(BonusTransport selectedBonusTransport) {
		// STORED IN THE module's MainController
		getBonusTransportMainCtrl().setSelectedBonusTransport(selectedBonusTransport);
	}

	public BindingListModelList getBonusTransports() {
		// STORED IN THE module's MainController
		return getBonusTransportMainCtrl().getBonusTransports();
	}

	public void setBonusTransports(BindingListModelList bonusTransports) {
		// STORED IN THE module's MainController
		getBonusTransportMainCtrl().setBonusTransports(bonusTransports);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setBonusTransportMainCtrl(BonusTransportMainCtrl bonusTransportMainCtrl) {
		this.bonusTransportMainCtrl = bonusTransportMainCtrl;
	}

	public BonusTransportMainCtrl getBonusTransportMainCtrl() {
		return this.bonusTransportMainCtrl;
	}

	/* SERVICES */
	public void setBonusTransportService(BonusTransportService bonusTransportService) {
		this.bonusTransportService = bonusTransportService;
	}

	public BonusTransportService getBonusTransportService() {
		return this.bonusTransportService;
	}

	public JobTypeService getJobTypeService() {
		return jobTypeService;
	}

	public void setJobTypeService(JobTypeService jobTypeService) {
		this.jobTypeService = jobTypeService;
	}

	/* COMPONENTS and OTHERS */

}
