package billy.webui.master.karyawan;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
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
import billy.webui.master.jobtype.model.JobTypeListModelItemRenderer;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class KaryawanDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(KaryawanDetailCtrl.class);

	protected Window windowKaryawanDetail; // autowired

	protected Borderlayout borderlayout_KaryawanDetail; // autowired

	protected Textbox txtb_KodeKaryawan; // autowired
	protected Textbox txtb_NamaPanggilan; // autowired
	protected Textbox txtb_NamaKtp; // autowired
	protected Textbox txtb_Ktp; // autowired	
	protected Datebox txtb_TanggalLahir; // autowired	
	protected Datebox txtb_TanggalMulaiKerja; // autowired	
	protected Datebox txtb_TanggalBerhentiKerja; // autowired	
	protected Textbox txtb_Telepon; // autowired
	protected Textbox txtb_Handphone; // autowired
	protected Textbox txtb_Email; // autowired
	protected Textbox txtb_Alamat; // autowired
	protected Textbox txtb_Catatan; // autowired
	protected Listbox lbox_JobType; // autowired
	protected Listbox lbox_SupervisorDivisi; // autowired
	protected Label label_SupervisorDivisi;
	protected Label label_InisialDivisi;
	protected Textbox txtb_InisialDivisi; // autowired
	protected Label label_StatusDivisi;
	protected Radiogroup radiogroup_Status; // autowired
	protected Radio radioStatusPusat;
	protected Radio radioStatusDaerah;
	
	protected Button uploadProfile;
	protected Button uploadKtp;
	protected Button downloadProfile;
	protected Button downloadKtp;
	
	protected Image profileImage;
	protected Image ktpImage;
	
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
			
			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getKaryawanMainCtrl().getSelectedKaryawan() != null) {
				setSelectedKaryawan(getKaryawanMainCtrl().getSelectedKaryawan());		
				doRefresh();
			} else
				setSelectedKaryawan(null);
		} else {
			setSelectedKaryawan(null);
		}
		
		
	}

	public void doRefresh(){
		getKaryawanMainCtrl().setKaryawanDetailCtrl(this);
		lbox_JobType.setModel(new ListModelList(getJobTypeService().getAllJobTypes()));
		lbox_JobType.setItemRenderer(new JobTypeListModelItemRenderer());
		
		if(getSelectedKaryawan().getJobType() != null){
			ListModelList lml = (ListModelList) lbox_JobType.getModel();		
			JobType jobType = getJobTypeService().getJobTypeByID(getSelectedKaryawan().getJobType().getId());
			lbox_JobType.setSelectedIndex(lml.indexOf(jobType));					
		}
		
		loadListBox();
		
		if(getSelectedKaryawan().getStatusDivisi()!=null){
			if(getSelectedKaryawan().getStatusDivisi().equals(radioStatusPusat.getLabel())){
				radioStatusPusat.setSelected(true);
			}
			if(getSelectedKaryawan().getStatusDivisi().equals(radioStatusDaerah.getLabel())){
				radioStatusDaerah.setSelected(true);
			}
		}
		try {
			if(getSelectedKaryawan().getProfileImage()!=null){
				profileImage.setContent(new AImage("profileImage",getSelectedKaryawan().getProfileImage()));
			}else{
				profileImage.setSrc("/images/icon-no-image.png");
			}
			if(getSelectedKaryawan().getKtpImage()!=null){
				ktpImage.setContent(new AImage("ktpImage",getSelectedKaryawan().getKtpImage()));
			}else{
				ktpImage.setSrc("/images/icon-no-image.png");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		doCheckRights();
	}
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();
		downloadProfile.setVisible(workspace.isAllowed("button_KaryawanDetail_DownloadProfile"));
		downloadKtp.setVisible(workspace.isAllowed("button_KaryawanDetail_DownloadKtp"));
		
	}
	public void onClick$downloadProfile(Event event) throws Exception {
		if(profileImage.getContent()!=null){
			String fileName = getKaryawan().getNamaKtp() + "-Profile.jpg";
			Filedownload.save(profileImage.getContent().getByteData(), "image/jpeg", fileName);		
		}
		
	}
	public void onClick$downloadKtp(Event event) throws Exception {
		if(ktpImage.getContent()!=null){
			String fileName = getKaryawan().getNamaKtp() + "-KTP.jpg";
			Filedownload.save(ktpImage.getContent().getByteData(), "image/jpeg", fileName);
		}
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
		txtb_NamaPanggilan.setReadonly(b);
		txtb_NamaKtp.setReadonly(b);
		txtb_Ktp.setReadonly(b);
		txtb_TanggalLahir.setDisabled(b);
		txtb_TanggalMulaiKerja.setDisabled(b);
		txtb_TanggalBerhentiKerja.setDisabled(b);
		txtb_Telepon.setReadonly(b);
		txtb_Handphone.setReadonly(b);
		txtb_Email.setReadonly(b);
		txtb_Alamat.setReadonly(b);
		txtb_Catatan.setReadonly(b);
		lbox_JobType.setDisabled(b);
		lbox_SupervisorDivisi.setDisabled(b);
		txtb_InisialDivisi.setReadonly(b);
		radioStatusPusat.setDisabled(b);
		radioStatusDaerah.setDisabled(b);
		uploadProfile.setDisabled(b);
		uploadKtp.setDisabled(b);
		downloadProfile.setDisabled(b);
		downloadKtp.setDisabled(b);
	}
	
	public void onOK$txtb_KodeKaryawan(Event event) throws InterruptedException {		
		txtb_NamaPanggilan.focus();			
	}
	public void onOK$txtb_NamaPanggilan(Event event) throws InterruptedException {
		txtb_NamaKtp.focus();			
	}
	public void onOK$txtb_NamaKtp(Event event) throws InterruptedException {
		txtb_Ktp.focus();			
	}
	public void onOK$txtb_Ktp(Event event) throws InterruptedException {
		txtb_TanggalLahir.focus();			
	}
	public void onOK$txtb_TanggalLahir(Event event) throws InterruptedException {
		txtb_TanggalMulaiKerja.focus();			
	}
	public void onOK$txtb_TanggalMulaiKerja(Event event) throws InterruptedException {
		txtb_TanggalBerhentiKerja.focus();			
	}
	public void onOK$txtb_TanggalBerhentiKerja(Event event) throws InterruptedException {
		txtb_Telepon.focus();			
	}
	public void onOK$txtb_Telepon(Event event) throws InterruptedException {
		txtb_Handphone.focus();			
	}
	public void onOK$txtb_Handphone(Event event) throws InterruptedException {
		txtb_Email.focus();			
	}
	public void onOK$txtb_Email(Event event) throws InterruptedException {
		txtb_Alamat.focus();			
	}
	
	

	public void onBlur$txtb_Ktp(Event event) throws InterruptedException {
		
		Karyawan karyawanCheckKtp = null;
		karyawanCheckKtp = getKaryawanService().getKaryawanByKtp(getKaryawan().getKtp());
		
		
		if(karyawanCheckKtp!=null){
			if(karyawanCheckKtp.getId()!=getKaryawan().getId()){
				ZksampleMessageUtils.showErrorMessage("No KTP sudah digunakan oleh karyawan bernama panggilan : " +karyawanCheckKtp.getNamaPanggilan());
				return;
			}
		}		
	}
	
	public void onBlur$txtb_KodeKaryawan(Event event) throws InterruptedException {
		
		Karyawan karyawanCheckKode = null;
		karyawanCheckKode = getKaryawanService().getKaryawanByKodeKaryawan(getKaryawan().getKodeKaryawan());
		
		if(karyawanCheckKode!=null){
			if(karyawanCheckKode.getId()!=getKaryawan().getId()){
				ZksampleMessageUtils.showErrorMessage("Kode Karyawan sudah digunakan oleh karyawan bernama panggilan : " +karyawanCheckKode.getNamaPanggilan());
				return;
			}
		}		
	}
	public void onSelect$lbox_JobType(Event event) throws InterruptedException {
		
		loadListBox();
	}
	public void loadListBox() {
		
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
			(7,'Staf',1);
			(8,'Auditor',1);
			*/
			long supervisorDivisiId = 0;
			boolean showSupervisorDivisi = false;
			boolean showDivisiData = false;
			if(jobType.getId() == 1 ){
				showSupervisorDivisi = false;
				showDivisiData=false;
				supervisorDivisiId = 0;
			}else if(jobType.getId() == 8){
				showSupervisorDivisi = false;
				showDivisiData=false;
				supervisorDivisiId = 0;
			}else if(jobType.getId() == 6){
				showSupervisorDivisi = false;
				showDivisiData=false;
				supervisorDivisiId = 0;
			}else if(jobType.getId() == 2){
				supervisorDivisiId = 1;
				showSupervisorDivisi = true;				
				showDivisiData=true;
			}else if(jobType.getId() == 7){
				supervisorDivisiId = 1;
				showSupervisorDivisi = true;
				showDivisiData=false;
			}else{
				supervisorDivisiId = 2;
				showSupervisorDivisi = true;
				showDivisiData=false;
			}
			label_SupervisorDivisi.setVisible(showSupervisorDivisi);
			lbox_SupervisorDivisi.setVisible(showSupervisorDivisi);
			
			label_InisialDivisi.setVisible(showDivisiData);
			txtb_InisialDivisi.setVisible(showDivisiData);
			label_StatusDivisi.setVisible(showDivisiData);
			radiogroup_Status.setVisible(showDivisiData);
			
			
			if(supervisorDivisiId > 0){
				List<Karyawan> listSupervisorDivisi= getKaryawanService().getKaryawansByJobTypeId(supervisorDivisiId);								
				lbox_SupervisorDivisi.setModel(new ListModelList(listSupervisorDivisi));						
				lbox_SupervisorDivisi.setItemRenderer(new KaryawanListModelItemRenderer());		
				// if available, select the object
				if(getSelectedKaryawan().getSupervisorDivisi() != null){
					ListModelList lml = (ListModelList) lbox_SupervisorDivisi.getModel();		
					Karyawan supervisorDivisi = getKaryawanService().getKaryawanByID(getSelectedKaryawan().getSupervisorDivisi().getId());
					lbox_SupervisorDivisi.setSelectedIndex(lml.indexOf(supervisorDivisi));					
				}	
			}else{
				getKaryawan().setSupervisorDivisi(null);
				getKaryawan().setStatusDivisi(null);
				getKaryawan().setInisialDivisi(null);
				radioStatusPusat.setChecked(false);
				radioStatusDaerah.setChecked(false);
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
