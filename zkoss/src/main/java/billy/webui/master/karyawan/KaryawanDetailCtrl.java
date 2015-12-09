package billy.webui.master.karyawan;

import java.io.Serializable;

import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.JobType;
import billy.backend.model.Karyawan;
import billy.backend.service.JobTypeService;
import billy.backend.service.KaryawanService;
import billy.webui.master.bonustransport.model.JobTypeListModelItemRenderer;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class KaryawanDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(KaryawanDetailCtrl.class);

	protected Window windowKaryawanDetail; // autowired

	protected Borderlayout borderlayout_KaryawanDetail; // autowired

	protected Textbox txtb_KodeKaryawan; // autowired
	protected Textbox txtb_NamaKaryawan; // autowired
	protected Datebox txtb_TanggalLahir; // autowired	
	protected Textbox txtb_Telepon; // autowired
	protected Textbox txtb_Handphone; // autowired
	protected Textbox txtb_Email; // autowired
	protected Textbox txtb_Alamat; // autowired
	protected Listbox lbox_JobType; // autowired
	protected Listbox lbox_SupervisorDivisi; // autowired
	protected Label label_SupervisorDivisi;
	// Databinding
	protected transient AnnotateDataBinder binder;
	private KaryawanMainCtrl karyawanMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient KaryawanService karyawanService;
	private JobTypeService jobTypeService;
	/**
	 * default constructor.<br>
	 */
	public KaryawanDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		this.self.setAttribute("controller", this, false);

		if (arg.containsKey("ModuleMainController")) {
			setKaryawanMainCtrl((KaryawanMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getKaryawanMainCtrl().setKaryawanDetailCtrl(this);
			lbox_JobType.setModel(new ListModelList(getJobTypeService().getAllJobTypes()));
			lbox_JobType.setItemRenderer(new JobTypeListModelItemRenderer());
			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getKaryawanMainCtrl().getSelectedKaryawan() != null) {
				setSelectedKaryawan(getKaryawanMainCtrl().getSelectedKaryawan());		
				loadListBox();
			} else
				setSelectedKaryawan(null);
		} else {
			setSelectedKaryawan(null);
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
	public void onCreate$windowKaryawanDetail(Event event) throws Exception {
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
		borderlayout_KaryawanDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowKaryawanDetail.invalidate();
	}

	public void doReadOnlyMode(boolean b) {
		txtb_KodeKaryawan.setReadonly(b);
		txtb_NamaKaryawan.setReadonly(b);
		txtb_TanggalLahir.setDisabled(b);
		txtb_Telepon.setReadonly(b);
		txtb_Handphone.setReadonly(b);
		txtb_Email.setReadonly(b);
		txtb_Alamat.setReadonly(b);
		lbox_JobType.setDisabled(b);
		lbox_SupervisorDivisi.setDisabled(b);
		
	}
	public void onSelect$lbox_JobType(Event event) throws InterruptedException {
		logger.info("masuk onchange jobtype");
		/* if a job type is selected get the object from the listbox */
		Listitem item = lbox_JobType.getSelectedItem();

		if (item != null) {
			ListModelList lml1 = (ListModelList) lbox_JobType.getListModel();
			JobType jobType = (JobType) lml1.get(item.getIndex());
			
			/*(1,'Supervisor',1),
			(2,'Divisi',1),
			(3,'Leader',1),
			(4,'Sales',1),
			(5,'Sopir',1),
			(6,'Kolektor',1),
			(7,'Staf',1);*/
			long supervisorDivisiId = 0;
			boolean showSupervisorDivisi = false;
			if(jobType.getId() == 1 ){
				showSupervisorDivisi = false;
				supervisorDivisiId = 0;
			}else if(jobType.getId() == 6){
				showSupervisorDivisi = false;
				supervisorDivisiId = 0;
			}else if(jobType.getId() == 2){
				supervisorDivisiId = 1;
				showSupervisorDivisi = true;
			}else if(jobType.getId() == 7){
				supervisorDivisiId = 1;
				showSupervisorDivisi = true;
			}else{
				supervisorDivisiId = 2;
				showSupervisorDivisi = true;
			}
			label_SupervisorDivisi.setVisible(showSupervisorDivisi);
			lbox_SupervisorDivisi.setVisible(showSupervisorDivisi);
			
			if(supervisorDivisiId > 0){
				lbox_SupervisorDivisi.setModel(new ListModelList(getKaryawanService().getKaryawansByJobTypeId(supervisorDivisiId)));						
				lbox_SupervisorDivisi.setItemRenderer(new KaryawanListModelItemRenderer());				
			}else{
				getKaryawan().setSupervisorDivisi(null);
			}
		}
	}
	public void loadListBox() {
		// +++++++++ DropDown ListBox
		// set listModel and itemRenderer for the dropdown listbox
		
		// if available, select the object
		if(getSelectedKaryawan().getJobType() != null){
			ListModelList lml = (ListModelList) lbox_JobType.getModel();		
			JobType jobType = getJobTypeService().getJobTypeByID(getSelectedKaryawan().getJobType().getId());
			lbox_JobType.setSelectedIndex(lml.indexOf(jobType));					
		}	
		
		/* if a job type is selected get the object from the listbox */
		Listitem item = lbox_JobType.getSelectedItem();

		if (item != null) {
			ListModelList lml1 = (ListModelList) lbox_JobType.getListModel();
			JobType jobType = (JobType) lml1.get(item.getIndex());
			
			/*(1,'Supervisor',1),
			(2,'Divisi',1),
			(3,'Leader',1),
			(4,'Sales',1),
			(5,'Sopir',1),
			(6,'Kolektor',1);*/
			long supervisorDivisiId = 0;
			boolean showSupervisorDivisi = false;
			if(jobType.getId() == 1 ){
				showSupervisorDivisi = false;
			}else if(jobType.getId() == 6){
				showSupervisorDivisi = false;
			}else if(jobType.getId() == 2){
				supervisorDivisiId = 1;
				showSupervisorDivisi = true;
			}else{
				supervisorDivisiId = 2;
				showSupervisorDivisi = true;
			}
			label_SupervisorDivisi.setVisible(showSupervisorDivisi);
			lbox_SupervisorDivisi.setVisible(showSupervisorDivisi);
			
			if(supervisorDivisiId > 0){
				lbox_SupervisorDivisi.setModel(new ListModelList(getKaryawanService().getKaryawansByJobTypeId(supervisorDivisiId)));						
				lbox_SupervisorDivisi.setItemRenderer(new KaryawanListModelItemRenderer());
				
				// if available, select the object
				if(getSelectedKaryawan().getSupervisorDivisi() != null){
					ListModelList lml = (ListModelList) lbox_SupervisorDivisi.getModel();		
					Karyawan supervisorDivisi = getKaryawanService().getKaryawanByID(getSelectedKaryawan().getSupervisorDivisi().getId());
					lbox_SupervisorDivisi.setSelectedIndex(lml.indexOf(supervisorDivisi));					
				}	
			}
		}
		
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public Karyawan getKaryawan() {
		// STORED IN THE module's MainController
		return getKaryawanMainCtrl().getSelectedKaryawan();
	}

	public void setKaryawan(Karyawan anKaryawan) {
		// STORED IN THE module's MainController
		getKaryawanMainCtrl().setSelectedKaryawan(anKaryawan);
	}

	public Karyawan getSelectedKaryawan() {
		// STORED IN THE module's MainController
		return getKaryawanMainCtrl().getSelectedKaryawan();
	}

	public void setSelectedKaryawan(Karyawan selectedKaryawan) {
		// STORED IN THE module's MainController
		getKaryawanMainCtrl().setSelectedKaryawan(selectedKaryawan);
	}

	public BindingListModelList getKaryawans() {
		// STORED IN THE module's MainController
		return getKaryawanMainCtrl().getKaryawans();
	}

	public void setKaryawans(BindingListModelList karyawans) {
		// STORED IN THE module's MainController
		getKaryawanMainCtrl().setKaryawans(karyawans);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setKaryawanMainCtrl(KaryawanMainCtrl karyawanMainCtrl) {
		this.karyawanMainCtrl = karyawanMainCtrl;
	}

	public KaryawanMainCtrl getKaryawanMainCtrl() {
		return this.karyawanMainCtrl;
	}

	/* SERVICES */
	public void setKaryawanService(KaryawanService karyawanService) {
		this.karyawanService = karyawanService;
	}

	public KaryawanService getKaryawanService() {
		return this.karyawanService;
	}

	public JobTypeService getJobTypeService() {
		return jobTypeService;
	}

	public void setJobTypeService(JobTypeService jobTypeService) {
		this.jobTypeService = jobTypeService;
	}

	/* COMPONENTS and OTHERS */

}
