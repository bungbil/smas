package billy.webui.transaction.penjualan.cetak;


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
import billy.webui.transaction.penjualan.cetak.report.CetakFakturDJReport;
import billy.webui.transaction.penjualan.cetak.report.CetakKuitansiA2DJReport;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CetakPenjualanMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CetakPenjualanMainCtrl.class);

	
	protected Window windowCetakPenjualanMain; // autowired
	protected Listbox lbox_Divisi;
	protected Listbox lbox_Printer;
	protected Textbox txtb_NoFakturAwal;
	protected Textbox txtb_NoFakturAkhir;
	protected Button btnCetakFaktur;
	protected Button btnCetakKuitansiA2;
	protected Button btnReset;
	
	// ServiceDAOs / Domain Classes
	private PenjualanService penjualanService;
	private KaryawanService karyawanService;

	private PrintService selectedPrinter;
	List<String> listNoFaktur = new ArrayList<String>();
	List<Penjualan> listPenjualan = new ArrayList<Penjualan>();
	List<Penjualan> listPenjualanNeedApproval = new ArrayList<Penjualan>();
	
	DecimalFormat df = new DecimalFormat("#,###");
	/**
	 * default constructor.<br>
	 */
	public CetakPenjualanMainCtrl() {
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
	public void onCreate$windowCetakPenjualanMain(Event event) throws Exception {
		windowCetakPenjualanMain.setContentStyle("padding:0px;");

		doCheckRights();
		
		doReset();
	}
	
	public void doReset(){
		SecUser userLogin = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecUser();	
		List<Karyawan> listDivisi= getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);
		
		lbox_Divisi.setModel(new ListModelList(listDivisi));
		lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());
		
		txtb_NoFakturAwal.setValue("");
		txtb_NoFakturAkhir.setValue("");
		listNoFaktur = new ArrayList<String>();
		listPenjualan = new ArrayList<Penjualan>();
		listPenjualanNeedApproval = new ArrayList<Penjualan>();
		selectedPrinter = null;
	}
	
	public void onClick$btnReset(Event event) throws Exception {
		doReset();
	}
	
	public void onBlur$txtb_NoFakturAwal(Event event) throws Exception {
		if(txtb_NoFakturAwal.getValue().length() != 10){
			ZksampleMessageUtils.showErrorMessage("No Faktur Awal tidak valid, harap memasukkan 10 digit");
			return;
		}
	}
	
	public void onBlur$txtb_NoFakturAkhir(Event event) throws Exception {
		if(txtb_NoFakturAkhir.getValue().length() != 10){
			ZksampleMessageUtils.showErrorMessage("No Faktur Akhir tidak valid, harap memasukkan 10 digit");
			return;
		}
	}
	
	public void onClick$btnRefeshPrinter(Event event) throws Exception {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        lbox_Printer.setModel(new ListModelList(printServices));
        lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
	}
	public void onClick$btnCetakFaktur(Event event) throws Exception {
		
		
        
		if(validToPrint()){
			listPenjualan = getPenjualanService().getAllPenjualansByListNoFaktur(listNoFaktur);
			
			
			final Window win = (Window) Path.getComponent("/outerIndexWindow");
			new CetakFakturDJReport(win,listPenjualan,selectedPrinter);
		}else{
			showErrorCetak();
		}
	}
	
	public void onClick$btnCetakKuitansiA2(Event event) throws Exception {
		if(validToPrint()){
			final Window win = (Window) Path.getComponent("/outerIndexWindow");
			//new CetakKuitansiA2DJReport(win);
		}else{
			showErrorCetak();			
		}	
	}

	public void showErrorCetak() throws Exception{
		if(listPenjualanNeedApproval.size()>0){
			String noFaktur = "";
			for(Penjualan penjualan : listPenjualanNeedApproval){
				if(!noFaktur.isEmpty()){
					noFaktur += ",";
				}
				noFaktur += penjualan.getNoFaktur();
			}
			
			ZksampleMessageUtils.showErrorMessage("Masih ada No Faktur yang butuh approval : "+noFaktur);
			
			
		}else{
			ZksampleMessageUtils.showErrorMessage("No Faktur tidak ditemukan / Silakan dipilih Printernya");	
		}
				
	}
	
	public boolean validToPrint() throws Exception{
		
		Karyawan karyawan = null;
		Listitem itemDivisi = lbox_Divisi.getSelectedItem();
		if (itemDivisi != null) {
			ListModelList lml1 = (ListModelList) lbox_Divisi.getListModel();
			karyawan = (Karyawan) lml1.get(itemDivisi.getIndex());
		}
		PrintService printer = null;
		Listitem itemPrinter = lbox_Printer.getSelectedItem();
		if (itemPrinter != null) {
			ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
			printer = (PrintService) lml1.get(itemPrinter.getIndex());
			selectedPrinter =printer;
			logger.info("Printer : "+printer.getName());
		}
		if(karyawan != null && printer!=null
				&& txtb_NoFakturAwal.getValue().length() == 10
				&& txtb_NoFakturAkhir.getValue().length() == 10){
			
			listNoFaktur = generateSequenceNoFaktur();	
			if(listNoFaktur.size() > 0){
				listPenjualanNeedApproval = getPenjualanService().getListNeedApprovalPenjualansByListNoFaktur(listNoFaktur);
				if(listPenjualanNeedApproval.size()==0){
					return true;
				}				
			}else{				
				return false;
			}			
		}
		return false;
	}
	
	public List<String> generateSequenceNoFaktur(){
		List<String> list = new ArrayList<String>();
		
		String startNoFaktur = txtb_NoFakturAwal.getValue();
		String endNoFaktur = txtb_NoFakturAkhir.getValue();
		list.add(startNoFaktur);
		String twoFirstDigitNoFaktur = startNoFaktur.substring(0,2);
		String fourLastDigitNoFaktur = startNoFaktur.substring(6,10);
		int start = Integer.parseInt(startNoFaktur.substring(2,6));
		int end = Integer.parseInt(endNoFaktur.substring(2,6));
		logger.info("start : "+start+", end : " +end+ ", fourLastDigitNoFaktur : "+fourLastDigitNoFaktur+", twoFirstDigitNoFaktur : "+ twoFirstDigitNoFaktur);
		if(start < end){
			int tempInt = start+1;
			while(tempInt != end){
				
				String temp = String.valueOf(tempInt);
				if(temp.length() == 1){
					temp = "000"+temp;
				}else if (temp.length() == 2){
					temp = "00"+temp;
				}else if (temp.length() == 3){
					temp = "0"+temp;
				}
				
				String tempNoFaktur = twoFirstDigitNoFaktur+temp+fourLastDigitNoFaktur;
				logger.info("tempNoFaktur : "+tempNoFaktur);
				
				list.add(tempNoFaktur);
				tempInt++;
			}
		}
		if(!startNoFaktur.equals(endNoFaktur)){
			list.add(endNoFaktur);
		}
		logger.info("list No Faktur : "+list.toString());
		
		return list;
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
		btnCetakFaktur.setDisabled(!workspace.isAllowed("button_CetakPenjualanMain_btnCetakFaktur"));
		btnCetakKuitansiA2.setDisabled(!workspace.isAllowed("button_CetakPenjualanMain_btnCetakKuitansiA2"));
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
