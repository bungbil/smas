package billy.webui.report.summarypenjualan;


import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.print.*;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.report.summarypenjualan.report.CetakLaporanSummaryPenjualanDJReport;
import billy.webui.report.summarypenjualan.report.ViewPdfLaporanSummaryPenjualanDJReport;
import billy.webui.transaction.penjualan.cetak.report.CetakFakturDJReport;
import billy.webui.transaction.penjualan.cetak.report.CetakKuitansiA2DJReport;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportSummaryPenjualanMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReportSummaryPenjualanMainCtrl.class);

	
	protected Window windowReportSummaryPenjualanMain; // autowired
	protected Listbox lbox_Divisi;
	protected Listbox lbox_Printer;
	protected Datebox txtb_tanggalAwalPenjualan;
	protected Datebox txtb_tanggalAkhirPenjualan;
	protected Button btnCetakLaporan;
	protected Button btnViewPdf;
	protected Button btnReset;
	
	// ServiceDAOs / Domain Classes
	private PenjualanService penjualanService;
	private KaryawanService karyawanService;
	

	private PrintService selectedPrinter;	
	List<Penjualan> listPenjualan = new ArrayList<Penjualan>();
	Karyawan karyawan = null;
	
	DecimalFormat df = new DecimalFormat("#,###");
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
		
		selectedPrinter = null;
	}
	
	public void onClick$btnReset(Event event) throws Exception {
		doReset();
	}
	
	public void onClick$btnRefeshPrinter(Event event) throws Exception {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        lbox_Printer.setModel(new ListModelList(printServices));
        lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
	}
	public void onClick$btnCetakLaporan(Event event) throws Exception {
		if(validToPrint()){
			final Window win = (Window) Path.getComponent("/outerIndexWindow");
			new CetakLaporanSummaryPenjualanDJReport(win,karyawan,txtb_tanggalAwalPenjualan.getValue(),txtb_tanggalAkhirPenjualan.getValue(),listPenjualan,selectedPrinter);
		}else{
			showErrorCetak();			
		}	
	}
	
	public void onClick$btnViewPdf(Event event) throws Exception {
		if(validToPrint()){
			final Window win = (Window) Path.getComponent("/outerIndexWindow");
			new ViewPdfLaporanSummaryPenjualanDJReport(win,karyawan,txtb_tanggalAwalPenjualan.getValue(),txtb_tanggalAkhirPenjualan.getValue(),listPenjualan);
		}else{
			showErrorCetak();			
		}	
	}

	public void showErrorCetak() throws Exception{
		if(listPenjualan.size()==0){
			ZksampleMessageUtils.showErrorMessage("Tidak ada penjualan di range waktu ini");
		}else if (selectedPrinter==null){
			ZksampleMessageUtils.showErrorMessage("Silakan dipilih Printernya");	
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
		PrintService printer = null;
		/*Listitem itemPrinter = lbox_Printer.getSelectedItem();
		if (itemPrinter != null) {
			ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
			printer = (PrintService) lml1.get(itemPrinter.getIndex());
			selectedPrinter =printer;
			logger.info("Printer : "+printer.getName());
		}*/
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
		//btnCetakLaporan.setDisabled(!workspace.isAllowed("button_CetakPenjualanMain_btnCetakFaktur"));
		//btnViewPdf.setDisabled(!workspace.isAllowed("button_CetakPenjualanMain_btnCetakKuitansiA2"));
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
