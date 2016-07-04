package billy.webui.report.summarypenjualan;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.report.summarypenjualan.report.SummaryPenjualanDJReport;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportSummaryPenjualanMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReportSummaryPenjualanMainCtrl.class);

	
	protected Window windowReportSummaryPenjualanMain; // autowired
	protected Listbox lbox_Divisi;	
	protected Datebox txtb_tanggalAwalPenjualan;
	protected Datebox txtb_tanggalAkhirPenjualan;	
	protected Button btnView;
	
	
	// ServiceDAOs / Domain Classes
	private PenjualanService penjualanService;
	private KaryawanService karyawanService;
		
	List<Penjualan> listPenjualan = new ArrayList<Penjualan>();
	Karyawan karyawan = null;
	
	/**
	 * default constructor.<br>
	 */
	public ReportSummaryPenjualanMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

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
	public void onCreate$windowReportSummaryPenjualanMain(Event event) throws Exception {
		windowReportSummaryPenjualanMain.setContentStyle("padding:0px;");
		doCheckRights();		
		doReset();
	}
	
	public void doReset(){
		SecUser userLogin = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecUser();	
		List<Karyawan> listDivisi= getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);
		
		lbox_Divisi.setModel(new ListModelList(listDivisi));
		lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());
		
		txtb_tanggalAwalPenjualan.setValue(null);
		txtb_tanggalAkhirPenjualan.setValue(null);
		listPenjualan = new ArrayList<Penjualan>();
		
	}
	
	public void onClick$btnView(Event event) throws Exception {
		if(validToPrint()){
			final Window win = (Window) Path.getComponent("/outerIndexWindow");
			new SummaryPenjualanDJReport(win,karyawan,txtb_tanggalAwalPenjualan.getValue(),txtb_tanggalAkhirPenjualan.getValue(),listPenjualan);
		}else{
			showErrorCetak();			
		}	
	}

	public void showErrorCetak() throws Exception{
		if(listPenjualan.size()==0){
			ZksampleMessageUtils.showErrorMessage("Tidak ada penjualan di range waktu ini");
		}else{
			ZksampleMessageUtils.showErrorMessage("Error!!!");
		}
				
	}
	
	public boolean validToPrint() throws Exception{			
		Listitem itemDivisi = lbox_Divisi.getSelectedItem();
		if (itemDivisi != null) {
			ListModelList lml1 = (ListModelList) lbox_Divisi.getListModel();
			karyawan = (Karyawan) lml1.get(itemDivisi.getIndex());
		}		
		if(karyawan != null 
				&& txtb_tanggalAwalPenjualan.getValue()!=null
				&& txtb_tanggalAkhirPenjualan.getValue()!=null){
			listPenjualan = getPenjualanService().getAllPenjualansByDivisiAndRangeDate(karyawan,txtb_tanggalAwalPenjualan.getValue(),txtb_tanggalAkhirPenjualan.getValue());
			if(listPenjualan.size() > 0){
				return true;
			}									
		}
		return false;
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
		btnView.setDisabled(!workspace.isAllowed("button_ReportSummaryPenjualanMain_btnView"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	

	/* SERVICES */
	public PenjualanService getPenjualanService() {
		return this.penjualanService;
	}

	public void setPenjualanService(PenjualanService penjualanService) {
		this.penjualanService = penjualanService;
	}

	public KaryawanService getKaryawanService() {
		return this.karyawanService;
	}

	public void setKaryawanService(KaryawanService karyawanService) {
		this.karyawanService = karyawanService;
	}
	/* COMPONENTS and OTHERS */
}
