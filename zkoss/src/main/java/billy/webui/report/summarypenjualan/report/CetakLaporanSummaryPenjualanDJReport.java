package billy.webui.report.summarypenjualan.report;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.print.PrintService;
import simple.escp.SimpleEscp;
import simple.escp.Template;
import simple.escp.data.DataSources;
import simple.escp.json.JsonTemplate;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.PenjualanService;
import billy.webui.printer.model.Faktur;
import billy.webui.printer.model.ItemFaktur;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class CetakLaporanSummaryPenjualanDJReport extends Window implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CetakLaporanSummaryPenjualanDJReport.class);
	public CetakLaporanSummaryPenjualanDJReport(Component parent,Karyawan karyawan,Date startDate,Date endDate,List<Penjualan> listPenjualan,PrintService selectedPrinter) throws InterruptedException {
		super();
		this.setParent(parent);

		try {
			doPrint(karyawan,startDate,endDate, listPenjualan,selectedPrinter);
		} catch (final Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	
	public void doPrint(Karyawan karyawan,Date startDate,Date endDate,List<Penjualan> listPenjualan ,PrintService selectedPrinter){
		SimpleEscp simpleEscp = new SimpleEscp(selectedPrinter.getName());     
		Template template  = null;
        try {
            template = new JsonTemplate(CetakLaporanSummaryPenjualanDJReport.class.getResourceAsStream("fakturTemplate.json"));
            
			DecimalFormat df = new DecimalFormat("#,###");
			SimpleDateFormat formatDate = new SimpleDateFormat();
			formatDate = new SimpleDateFormat("dd MMMM yyyy");
			PenjualanService as = (PenjualanService) SpringUtil.getBean("penjualanService");
			List<Faktur> listFaktur = new ArrayList<Faktur>();
			for(Penjualan penjualan : listPenjualan){
				Faktur faktur = new Faktur();
				faktur.setNomorFaktur(penjualan.getNoFaktur());
				faktur.setKodeSales1(penjualan.getSales1().getKodeKaryawan());
				faktur.setNamaSales1(penjualan.getSales1().getNamaPanggilan() +" ("+penjualan.getSales1().getSupervisorDivisi().getInisialDivisi()+")");
				if(penjualan.getSales2()!=null){
					faktur.setKodeSales2(penjualan.getSales2().getKodeKaryawan());
					faktur.setNamaSales2(penjualan.getSales2().getNamaPanggilan() +" ("+penjualan.getSales2().getSupervisorDivisi().getInisialDivisi()+")");
				}else{
					faktur.setKodeSales2("");
					faktur.setNamaSales2("");
				}
				faktur.setIntervalKredit(penjualan.getIntervalKredit()+" Bulan");
				faktur.setNamaPelanggan(penjualan.getNamaPelanggan());
				faktur.setAlamat("");
				faktur.setAlamat2("");
				faktur.setAlamat3("");
				
				StringBuilder sb = new StringBuilder(penjualan.getAlamat());
				int i = 0;
				while (i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 35)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				String[] alamat = sb.toString().split("\n");
				int length = alamat.length;				
				for(int k=0;k<length;k++){
					if(k==0){
						faktur.setAlamat(alamat[0]);
					}else if (k==1){
						faktur.setAlamat2(alamat[1]);
					}else if (k==2){
						faktur.setAlamat3(alamat[2]);
					}
				}
				
				faktur.setTelepon(penjualan.getTelepon());
				faktur.setDp(df.format(penjualan.getDownPayment()));
				faktur.setTotal(df.format(penjualan.getTotal()));
				faktur.setTglPenjualan(formatDate.format(penjualan.getTglPenjualan()));
				faktur.setNamaSupervisor(penjualan.getDivisi().getSupervisorDivisi().getNamaPanggilan());
				faktur.setNamaPengirim(penjualan.getPengirim().getNamaPanggilan());
				
				List<PenjualanDetail> listPenjualanDetail = as.getPenjualanDetailsByPenjualan(penjualan);
				for(PenjualanDetail detail : listPenjualanDetail){
					faktur.tambahItemFaktur(new ItemFaktur(detail.getBarang().getNamaBarang(),String.valueOf(detail.getQty()),
							df.format(detail.getHarga()),df.format(detail.getTotal())));
				}
				if(listPenjualanDetail.size() < 9){
					for(int j = 0; j < 9 -listPenjualanDetail.size();j++){
						faktur.tambahItemFaktur(new ItemFaktur("","","",""));
					}
				}
				
				listFaktur.add(faktur);
				
				simpleEscp.print(template, DataSources.from(faktur));
			}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 	}
}
