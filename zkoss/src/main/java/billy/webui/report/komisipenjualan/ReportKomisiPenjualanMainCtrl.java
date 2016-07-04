package billy.webui.report.komisipenjualan;


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
import billy.webui.report.komisipenjualan.report.KomisiPenjualanDJReport;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportKomisiPenjualanMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReportKomisiPenjualanMainCtrl.class);

	
	protected Window windowReportKomisiPenjualanMain; // autowired
	protected Listbox lbox_Sales;	
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
	public ReportKomisiPenjualanMainCtrl() {
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
	public void onCreate$windowReportKomisiPenjualanMain(Event event) throws Exception {
		windowReportKomisiPenjualanMain.setContentStyle("padding:0px;");

		doCheckRights();
		
		doReset();
	}
	
	public void doReset(){
		SecUser userLogin = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecUser();	
		List<Karyawan> listSales= getKaryawanService().getAllSalesKaryawansByUserLogin(userLogin);
		
		lbox_Sales.setModel(new ListModelList(listSales));
		lbox_Sales.setItemRenderer(new KaryawanListModelItemRenderer());
		
		txtb_tanggalAwalPenjualan.setValue(null);
		txtb_tanggalAkhirPenjualan.setValue(null);
		listPenjualan = new ArrayList<Penjualan>();
		
		
	}
	
	
	public void onClick$btnView(Event event) throws Exception {
		if(validToPrint()){
			final Window win = (Window) Path.getComponent("/outerIndexWindow");
			new KomisiPenjualanDJReport(win,karyawan,txtb_tanggalAwalPenjualan.getValue(),txtb_tanggalAkhirPenjualan.getValue(),listPenjualan);
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
		Listitem itemSales = lbox_Sales.getSelectedItem();
		if (itemSales != null) {
			ListModelList lml1 = (ListModelList) lbox_Sales.getListModel();
			karyawan = (Karyawan) lml1.get(itemSales.getIndex());
		}
		
		if(karyawan != null 
				&& txtb_tanggalAwalPenjualan.getValue()!=null
				&& txtb_tanggalAkhirPenjualan.getValue()!=null){
			listPenjualan = getPenjualanService().getAllPenjualansBySalesAndRangeDate(karyawan,txtb_tanggalAwalPenjualan.getValue(),txtb_tanggalAkhirPenjualan.getValue());
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
		btnView.setDisabled(!workspace.isAllowed("button_ReportKomisiPenjualanMain_btnView"));
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
