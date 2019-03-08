package billy.webui.report.piutangm3;


import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.backend.service.PiutangService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.report.piutangm3.model.ReportPiutangM3;
import billy.webui.report.piutangm3.report.PiutangM3DJReport;
import billy.webui.report.piutangm3.report.PiutangM3TextPrinter;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ReportPiutangM3MainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(ReportPiutangM3MainCtrl.class);


  protected Window windowReportPiutangM3Main; // autowired
  protected Textbox txtb_KodeDivisi;
  protected Listbox lbox_Divisi;
  protected Datebox txtb_tanggalAwalPenjualan_per_divisi;
  protected Datebox txtb_tanggalAkhirPenjualan_per_divisi;
  protected Button btnView;
  protected Listbox lbox_Printer_per_divisi;
  protected Button btnCetak_per_divisi;

  // ServiceDAOs / Domain Classes
  private PenjualanService penjualanService;
  private PiutangService piutangService;
  private KaryawanService karyawanService;

  private PrintService selectedPrinter_per_divisi;
  List<ReportPiutangM3> listPenjualanM3 = new ArrayList<ReportPiutangM3>();
  Karyawan karyawan_per_divisi = null;

  /**
   * default constructor.<br>
   */
  public ReportPiutangM3MainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    // PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    PrintService[] printServices = PrinterJob.lookupPrintServices();

    lbox_Printer_per_divisi.setModel(new ListModelList(printServices));
    lbox_Printer_per_divisi.setItemRenderer(new PrinterListModelItemRenderer());
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    ListModelList lml2 = (ListModelList) lbox_Printer_per_divisi.getModel();
    lbox_Printer_per_divisi.setSelectedIndex(lml2.indexOf(service));

    this.self.setAttribute("controller", this, false);
  }


  public void doCetakPerDivisi() throws Exception {
    final Window win = (Window) Path.getComponent("/outerIndexWindow");

    new PiutangM3TextPrinter(win, karyawan_per_divisi,
        txtb_tanggalAwalPenjualan_per_divisi.getValue(),
        txtb_tanggalAkhirPenjualan_per_divisi.getValue(), listPenjualanM3,
        selectedPrinter_per_divisi);

  }

  /**
   * User rights check. <br>
   * Only components are set visible=true if the logged-in <br>
   * user have the right for it. <br>
   * The rights are getting from the spring framework users grantedAuthority(). Remember! A right is
   * only a string. <br>
   */
  // TODO move it to zul
  private void doCheckRights() {

    final UserWorkspace workspace = getUserWorkspace();
    // btnView.setDisabled(!workspace.isAllowed("button_ReportPiutangM3Main_btnView"));
  }

  public void doReset() {
    SecUser userLogin =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();

    List<Karyawan> listDivisi = getKaryawanService().getAllDivisiKaryawansByUserLogin(userLogin);
    lbox_Divisi.setModel(new ListModelList(listDivisi));
    lbox_Divisi.setItemRenderer(new KaryawanListModelItemRenderer());

    Date date = new Date(); // your date
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    cal.set(year, month, 1, 0, 0, 0);
    Date startDate = cal.getTime();

    txtb_tanggalAwalPenjualan_per_divisi.setValue(startDate);
    txtb_tanggalAkhirPenjualan_per_divisi.setValue(date);
    listPenjualanM3 = new ArrayList<ReportPiutangM3>();

  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  /* SERVICES */
  public PenjualanService getPenjualanService() {
    return this.penjualanService;
  }

  public PiutangService getPiutangService() {
    return piutangService;
  }

  public void onChange$txtb_KodeDivisi(Event event) throws InterruptedException {
    if (txtb_KodeDivisi.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Divisi.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(
              txtb_KodeDivisi.getValue().trim().toUpperCase());
      if (karyawan != null) {
        lbox_Divisi.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Divisi.setSelectedIndex(-1);
      }
    }
  }

  public void onClick$btnCetak_per_divisi(Event event) throws Exception {
    if (validToPrintPerDivisi() && selectedPrinter_per_divisi != null) {
      // Show a confirm box
      String msg = "Apakah anda yakin ingin mencetak laporan ini ?";
      final String title = Labels.getLabel("message.Information");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            @Override
            public void onEvent(Event evt) {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    doCetakPerDivisi();
                  } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  break; //
                case MultiLineMessageBox.NO:
                  break; //
              }
            }
          }) == MultiLineMessageBox.YES) {
      }


    } else {
      showErrorCetak();
    }
  }


  public void onClick$btnRefeshPrinter_per_divisi(Event event) throws Exception {
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    lbox_Printer_per_divisi.setModel(new ListModelList(printServices));
    lbox_Printer_per_divisi.setItemRenderer(new PrinterListModelItemRenderer());
  }


  public void onClick$btnView(Event event) throws Exception {
    if (validToPrintPerDivisi()) {
      final Window win = (Window) Path.getComponent("/outerIndexWindow");
      new PiutangM3DJReport(win, karyawan_per_divisi,
          txtb_tanggalAwalPenjualan_per_divisi.getValue(),
          txtb_tanggalAkhirPenjualan_per_divisi.getValue(), listPenjualanM3);
    } else {
      showErrorCetak();
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
  public void onCreate$windowReportPiutangM3Main(Event event) throws Exception {
    windowReportPiutangM3Main.setContentStyle("padding:0px;");

    doCheckRights();

    doReset();
  }


  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */


  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }

  /* COMPONENTS and OTHERS */

  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  public void showErrorCetak() throws Exception {
    if (listPenjualanM3.size() == 0) {
      ZksampleMessageUtils.showErrorMessage("Tidak ada penjualan di range waktu ini");
    } else {
      ZksampleMessageUtils.showErrorMessage("Error!!!");
    }

  }


  public boolean validToPrintPerDivisi() throws Exception {
    Listitem itemSales = lbox_Divisi.getSelectedItem();
    if (itemSales != null) {
      ListModelList lml1 = (ListModelList) lbox_Divisi.getListModel();
      karyawan_per_divisi = (Karyawan) lml1.get(itemSales.getIndex());
    }
    PrintService printer = null;
    Listitem itemPrinter = lbox_Printer_per_divisi.getSelectedItem();
    if (itemPrinter != null) {
      ListModelList lml1 = (ListModelList) lbox_Printer_per_divisi.getListModel();
      printer = (PrintService) lml1.get(itemPrinter.getIndex());
      selectedPrinter_per_divisi = printer;
      logger.info("Printer : " + printer.getName());
    }

    listPenjualanM3 = new ArrayList<ReportPiutangM3>();
    if (karyawan_per_divisi != null && txtb_tanggalAwalPenjualan_per_divisi.getValue() != null
        && txtb_tanggalAkhirPenjualan_per_divisi.getValue() != null) {

      List<Penjualan> listPenjualanNotM3 = new ArrayList<Penjualan>();
      listPenjualanNotM3 =
          getPenjualanService().getAllPenjualansByDivisiAndRangeDate(karyawan_per_divisi,
              txtb_tanggalAwalPenjualan_per_divisi.getValue(),
              txtb_tanggalAkhirPenjualan_per_divisi.getValue());
      Collections.sort(listPenjualanNotM3, new Comparator<Penjualan>() {
        @Override
        public int compare(Penjualan obj1, Penjualan obj2) {
          return obj1.getNoFaktur().compareTo(obj2.getNoFaktur());
        }
      });
      Integer index = 1;

      for (Penjualan penjualan : listPenjualanNotM3) {
        Piutang piutang = getPiutangService().getPiutangM3(penjualan);
        if (piutang != null) {
          logger.info("NoFaktur:" + piutang.getNoFaktur());
          ReportPiutangM3 data = new ReportPiutangM3();
          data.setNo(index.toString());
          data.setNamaCustomer(penjualan.getNamaPelanggan());
          data.setNoFaktur(penjualan.getNoFaktur());
          data.setSisaPiutang(piutang.getNilaiTagihan().add(piutang.getKekuranganBayar()));

          Piutang piutangTerakhirDibayar = getPiutangService().getPiutangTerakhirDibayar(penjualan);
          if (piutangTerakhirDibayar != null) {
            data.setTglBayarTerakhir(piutangTerakhirDibayar.getTglPembayaran());
            data.setNamaKolektorBayarTerakhir(piutangTerakhirDibayar.getKolektor()
                .getNamaPanggilan());
          }
          listPenjualanM3.add(data);
          index++;
        }
      }

      if (listPenjualanM3.size() > 0) {

        return true;
      }
    }
    return false;
  }
}
