package billy.webui.transaction.kolektor.penerimaanpembayaran;


import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.backend.service.PiutangService;
import billy.backend.service.StatusService;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.master.status.model.StatusListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.transaction.piutang.cetak.report.CetakKuitansiTextPrinter;
import de.forsthaus.UserWorkspace;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PenerimaanPembayaranMainCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PenerimaanPembayaranMainCtrl.class);


  protected Window windowPenerimaanPembayaranMain; // autowired

  protected Textbox txtb_SearchNoKwitansi;
  protected Button btnSave;
  protected Button btnSearch;
  protected Panel panelResult;
  protected Panel panelApproval;

  protected Textbox txtb_NoFaktur;
  protected Textbox txtb_NoKuitansi;
  protected Textbox txtb_PembayaranKe;
  protected Datebox txtb_TglJatuhTempo;
  protected Decimalbox txtb_NilaiTagihan;
  protected Listbox lbox_Status;
  protected Textbox txtb_KodeKolektor;
  protected Listbox lbox_Kolektor;
  protected Datebox txtb_tglBawaKolektor;
  protected Decimalbox txb_KekuranganBayar;
  protected Datebox txtb_tglPembayaran;
  protected Decimalbox txtb_Pembayaran;
  protected Decimalbox txtb_Diskon;
  protected Textbox txtb_Keterangan;

  protected Combobox cmb_StatusFinal;

  protected Label label_butuhApproval;
  protected Textbox txtb_ReasonApproval;
  protected Button btnApprovePiutang;
  public Textbox txtb_ApprovedBy;
  public Textbox txtb_ApprovedRemark;

  protected Button btnCetak;
  protected Button btnCetakKwitansiSekarang;
  protected Listbox lbox_Printer;

  // ServiceDAOs / Domain Classes
  private PenjualanService penjualanService;
  private PiutangService piutangService;

  private KaryawanService karyawanService;
  private PrintService selectedPrinter;
  private StatusService statusService;
  DecimalFormat df = new DecimalFormat("#,###");
  Piutang piutang = new Piutang();

  /**
   * default constructor.<br>
   */
  public PenerimaanPembayaranMainCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    // PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    PrintService[] printServices = PrinterJob.lookupPrintServices();
    lbox_Printer.setModel(new ListModelList(printServices));
    lbox_Printer.setItemRenderer(new PrinterListModelItemRenderer());
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    ListModelList lml = (ListModelList) lbox_Printer.getModel();
    lbox_Printer.setSelectedIndex(lml.indexOf(service));

    this.self.setAttribute("controller", this, false);
  }

  private String doCheckApproval() throws Exception {
    /*
     * validasi butuh approval status final dipilih
     */
    String message = "";
    if (cmb_StatusFinal.getValue() != null) {
      if (cmb_StatusFinal.getValue() == "1") {
        message += "- Kwitansi akan di Finalkan dengan status DISKON \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(5)); // DISKON
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue() == "2") {
        message += "- Kwitansi akan di Finalkan dengan status TARIK BARANG \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(6)); // TARIK BARANG
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue() == "3") {
        message += "- Kwitansi akan di Finalkan dengan status MASALAH \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(7)); // MASALAH
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue() == "4") {
        message += "- Kwitansi akan di Finalkan dengan status FINAL \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(8)); // FINAL
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue() == "0") {
        message = "";
        piutang.setStatusFinal(null);

        piutang.setStatus(null);
        piutang.setNeedApproval(false);
        piutang.setReasonApproval("");
        txtb_ReasonApproval.setValue("");
        label_butuhApproval.setValue("Tidak");
      }
    }


    if (message != "") {
      piutang.setNeedApproval(true);
      piutang.setReasonApproval(message);
      txtb_ReasonApproval.setValue(message);
      label_butuhApproval.setValue("Ya");
    } else {
      piutang.setNeedApproval(false);
      piutang.setReasonApproval("");
      txtb_ReasonApproval.setValue("");
      label_butuhApproval.setValue("Tidak");
    }

    if (piutang.isNeedApproval()) {
      Status status = getStatusService().getStatusByID(new Long(1)); // BUTUH_APPROVAL
      piutang.setStatus(status);
      panelApproval.setVisible(piutang.isNeedApproval());
    }
    return message;
  }

  private String doCheckPembayaran() throws Exception {
    /*
     * validasi Kurang Bayar
     */
    String message = "";
    if (txtb_Pembayaran.getValue() != null && txtb_Diskon.getValue() != null) {
      BigDecimal totalBayar = txtb_Pembayaran.getValue().add(txtb_Diskon.getValue());
      BigDecimal kekuranganBayar = BigDecimal.ZERO;
      if (piutang.getKekuranganBayar() != null) {
        kekuranganBayar = piutang.getKekuranganBayar();
      }
      piutang.setKekuranganBayar(kekuranganBayar);
      BigDecimal totalTagihan = piutang.getNilaiTagihan().add(kekuranganBayar);
      if (totalTagihan.compareTo(totalBayar) == 1) {
        message += "- Pembayaran KURANG untuk kwitansi ini \n";
        // Status status = getStatusService().getStatusByID(new Long(2)); // LUNAS
        // piutang.setStatus(status);

      } else if (totalTagihan.compareTo(totalBayar) == -1) {
        message += "- Pembayaran LEBIH untuk kwitansi ini \n";
        Status status = getStatusService().getStatusByID(new Long(2)); // LUNAS
        piutang.setStatus(status);
        // piutang.setFullPayment(true);
      } else if (totalTagihan.compareTo(totalBayar) == 0) {
        // piutang.setFullPayment(true);
      }
    }

    return message;
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
    btnSave.setDisabled(!workspace.isAllowed("button_PenerimaanPembayaranMain_btnSave"));
    btnSearch.setDisabled(!workspace.isAllowed("button_PenerimaanPembayaranMain_btnSearch"));
  }

  private void doSave(Event event) throws Exception {

    Status statusLunas = getStatusService().getStatusByID(new Long(2)); // LUNAS
    Status statusProses = getStatusService().getStatusByID(new Long(3)); // PROSES

    if (txtb_tglPembayaran.getValue() != null) {
      piutang.setTglPembayaran(txtb_tglPembayaran.getValue());
    } else {
      piutang.setTglPembayaran(null);
    }

    BigDecimal diskon = BigDecimal.ZERO;
    if (txtb_Diskon.getValue() != null) {
      diskon = txtb_Diskon.getValue();
    }
    piutang.setDiskon(diskon);
    piutang.setKeterangan(txtb_Keterangan.getValue());

    BigDecimal pembayaran = BigDecimal.ZERO;
    if (txtb_Pembayaran.getValue() != null) {
      pembayaran = txtb_Pembayaran.getValue();
    }
    piutang.setPembayaran(pembayaran);
    if (txtb_NilaiTagihan.getValue().compareTo(pembayaran) == -1) {
      piutang.setFullPayment(true);
    }

    String userName =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsername();
    piutang.setLastUpdate(new Date());
    piutang.setUpdatedBy(userName);

    BigDecimal kekuranganBayar =
        piutang.getNilaiTagihan().add(piutang.getKekuranganBayar())
            .subtract(piutang.getPembayaran()).subtract(diskon);

    piutang.setStatus(statusLunas);
    piutang.setAktif(false);
    // if (piutang.getPenjualan().getPiutang().compareTo(BigDecimal.ZERO) == 0) {
    // piutang.setAktif(true);
    // }

    // save it to database
    getPiutangService().saveOrUpdate(piutang);

    // recalculate piutang at penjualan
    List<Piutang> piutangList = getPiutangService().getPiutangsByPenjualan(piutang.getPenjualan());
    BigDecimal totalPaid = BigDecimal.ZERO;
    Penjualan penjualan = piutang.getPenjualan();
    totalPaid = totalPaid.add(penjualan.getDownPayment());
    for (Piutang pp : piutangList) {
      if (null != pp.getPembayaran()) {
        totalPaid = totalPaid.add(pp.getPembayaran()).add(pp.getDiskon());

      }
    }
    penjualan.setPiutang(penjualan.getTotal().subtract(totalPaid));
    getPenjualanService().saveOrUpdate(penjualan);

    if (penjualan.getPiutang().compareTo(BigDecimal.ZERO) == 0) {
      penjualan.setStatus(statusLunas);
      getPiutangService().deleteNextPiutang(piutang);
      ZksampleMessageUtils
          .showErrorMessage("No Faktur " + piutang.getNoFaktur() + " sudah LUNAS!!");
    } else {
      printNextKuitansi(kekuranganBayar, statusProses);
    }


    panelResult.setVisible(false);
    piutang = null;
    txtb_SearchNoKwitansi.focus();
  }

  public void doSearchNoFaktur() throws Exception {
    if (txtb_SearchNoKwitansi.getValue() != null) {
      piutang = piutangService.getPiutangByNoFaktur(txtb_SearchNoKwitansi.getValue().toUpperCase());

      if (piutang != null) {

        txtb_NoFaktur.setValue(piutang.getPenjualan().getNoFaktur());
        txtb_NoKuitansi.setValue(piutang.getNoKuitansi());
        txtb_PembayaranKe.setValue(String.valueOf(piutang.getPembayaranKe()));
        txtb_TglJatuhTempo.setValue(piutang.getTglJatuhTempo());
        txtb_NilaiTagihan.setValue(piutang.getNilaiTagihan());
        txb_KekuranganBayar.setValue(piutang.getKekuranganBayar());

        List<Status> listStatus = getStatusService().getAllStatuss();
        lbox_Status.setModel(new ListModelList(listStatus));
        lbox_Status.setItemRenderer(new StatusListModelItemRenderer());
        if (piutang.getStatus() != null) {
          ListModelList lml = (ListModelList) lbox_Status.getModel();
          Status status = getStatusService().getStatusByID(piutang.getStatus().getId());
          lbox_Status.setSelectedIndex(lml.indexOf(status));
        }

        List<Karyawan> listKaryawan = getKaryawanService().getKaryawansByJobTypeId(new Long(6));// Kolektor

        lbox_Kolektor.setModel(new ListModelList(listKaryawan));
        lbox_Kolektor.setItemRenderer(new KaryawanListModelItemRenderer());
        if (piutang.getKolektor() != null) {
          ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
          Karyawan karyawan = getKaryawanService().getKaryawanByID(piutang.getKolektor().getId());
          lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
          txtb_KodeKolektor.setValue(karyawan.getKodeKaryawan());
        }
        txtb_tglBawaKolektor.setValue(piutang.getTglBawaKolektor());

        if (piutang.getTglPembayaran() != null) {
          txtb_tglPembayaran.setValue(piutang.getTglPembayaran());
        } else {
          txtb_tglPembayaran.setValue(new Date());
        }

        panelResult.setVisible(true);
        txtb_Pembayaran.setValue(piutang.getPembayaran());
        txtb_Diskon.setValue(piutang.getDiskon());
        if (piutang.getDiskon() == null) {
          txtb_Diskon.setValue(BigDecimal.ZERO);
        }
        txtb_Keterangan.setValue(piutang.getKeterangan());
        panelApproval.setVisible(piutang.isNeedApproval());

        txtb_tglPembayaran.focus();
      } else {
        panelResult.setVisible(false);
        panelApproval.setVisible(false);
        ZksampleMessageUtils.showErrorMessage("No Kwitansi tidak ditemukan");
        return;
      }

    } else {
      ZksampleMessageUtils.showErrorMessage("Harap masukkan No Kwitansi");
      return;
    }
  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  public PenjualanService getPenjualanService() {
    return penjualanService;
  }

  /* SERVICES */
  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public Piutang getSelectedPiutang() {
    // STORED IN THE module's MainController
    return this.piutang;
  }

  public StatusService getStatusService() {
    return statusService;
  }

  public void onBlur$txtb_KodeKolektor(Event event) throws InterruptedException {
    if (txtb_KodeKolektor.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(txtb_KodeKolektor.getValue().trim());
      if (karyawan != null) {
        lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Kolektor.setSelectedIndex(-1);
      }
    }
  }

  public void onChange$txtb_KodeKolektor(Event event) throws InterruptedException {
    if (txtb_KodeKolektor.getValue() != null) {
      ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
      Karyawan karyawan =
          getKaryawanService().getKaryawanByKodeKaryawan(txtb_KodeKolektor.getValue().trim());
      if (karyawan != null) {
        lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
      } else {
        lbox_Kolektor.setSelectedIndex(-1);
      }
    }
  }

  public void onClick$btnCetakKwitansiSekarang(Event event) throws Exception {
    if (validToCetak()) {
      PrintService printer = null;
      Listitem itemPrinter = lbox_Printer.getSelectedItem();
      if (itemPrinter != null) {
        ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
        printer = (PrintService) lml1.get(itemPrinter.getIndex());
        selectedPrinter = printer;


        final Piutang anPiutang = getSelectedPiutang();
        if (anPiutang != null) {

          // Show a confirm box
          final String msg = "Apakah anda yakin akan mencetak ulang kwitansi ini?? \n\n ";
          final String title = "";

          MultiLineMessageBox.doSetTemplate();
          if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO,
              Messagebox.QUESTION, true, new EventListener() {
                private void cetakBean() throws InterruptedException {
                  try {
                    // tutup piutang, set aktif = false
                    Piutang piutang = getSelectedPiutang();

                    if (piutang != null) {
                      final Window win = (Window) Path.getComponent("/outerIndexWindow");
                      List<Piutang> listPiutang = new ArrayList<Piutang>();
                      listPiutang.add(piutang);
                      new CetakKuitansiTextPrinter(win, listPiutang, selectedPrinter);
                    }
                  } catch (DataAccessException e) {
                    ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
                  }
                }

                @Override
                public void onEvent(Event evt) {
                  switch (((Integer) evt.getData()).intValue()) {
                    case MultiLineMessageBox.YES:
                      try {
                        cetakBean();
                      } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                      break; //
                    case MultiLineMessageBox.NO:
                      break; //
                  }
                }
              }

          ) == MultiLineMessageBox.YES) {
          }

        }
      } else {
        ZksampleMessageUtils.showErrorMessage("Silakan pilih printer");

      }
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void onClick$btnSave(Event event) throws Exception {
    String message = "";

    message += doCheckPembayaran();
    message += doCheckApproval();
    if (piutang != null) {

      if (message.equals("")) {
        // Show a confirm box
        message =
            Labels.getLabel("message_Data_Modified_Save_Data_YesNo") + "\n\n --> "
                + piutang.getNoKuitansi()
                + "\n\n dan akan melanjutkan mencetak kwitansi berikutnya??";
      }
      final String title = Labels.getLabel("message_Saving_Data");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(message, title, Messagebox.YES | Messagebox.NO,
          Messagebox.QUESTION, true, new EventListener() {

            @Override
            public void onEvent(Event evt) throws Exception {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    doSave(evt);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  break; //
                case MultiLineMessageBox.NO:
                  break; //
              }
            }
          }

      ) == MultiLineMessageBox.YES) {
      }

    }
  }

  public void onClick$btnSearch(Event event) throws Exception {

    doSearchNoFaktur();

  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowPenerimaanPembayaranMain(Event event) throws Exception {
    windowPenerimaanPembayaranMain.setContentStyle("padding:0px;");
    panelResult.setVisible(false);
    panelApproval.setVisible(false);
    doCheckRights();
    txtb_SearchNoKwitansi.focus();

  }

  public void onOK$txtb_Diskon(Event event) throws InterruptedException {
    txtb_Keterangan.focus();
  }

  public void onOK$txtb_Keterangan(Event event) throws InterruptedException {
    btnSave.focus();
  }

  public void onOK$txtb_Pembayaran(Event event) throws InterruptedException {
    txtb_Diskon.focus();
  }

  public void onOK$txtb_SearchNoKwitansi(Event event) throws InterruptedException {
    // btnSearch.focus();
    try {
      doSearchNoFaktur();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void onOK$txtb_tglPembayaran(Event event) throws InterruptedException {
    txtb_Pembayaran.focus();
  }

  public void printNextKuitansi(BigDecimal kekuranganBayar, Status statusProses) throws Exception {
    if (validToCetak()) {
      PrintService printer = null;
      Listitem itemPrinter = lbox_Printer.getSelectedItem();
      if (itemPrinter != null) {
        ListModelList lml1 = (ListModelList) lbox_Printer.getListModel();
        printer = (PrintService) lml1.get(itemPrinter.getIndex());
        selectedPrinter = printer;
        try {
          if (piutang.getPenjualan().getPiutang().compareTo(BigDecimal.ZERO) == 1) {
            // get next piutang, set aktif = true, kekurangan dari piutang sebelumnya
            Piutang nextPiutang = piutangService.getNextPiutang(piutang, statusProses);

            if (nextPiutang != null) {

              nextPiutang.setAktif(true);
              nextPiutang.setKekuranganBayar(kekuranganBayar);
              getPiutangService().saveOrUpdate(nextPiutang);

              final Window win = (Window) Path.getComponent("/outerIndexWindow");
              List<Piutang> listPiutang = new ArrayList<Piutang>();
              listPiutang.add(nextPiutang);
              new CetakKuitansiTextPrinter(win, listPiutang, selectedPrinter);
            }
          }
        } catch (DataAccessException e) {
          ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
        }
      } else {
        ZksampleMessageUtils.showErrorMessage("Silakan pilih printer");

      }
    }
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }


  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */


  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  /* COMPONENTS and OTHERS */

  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  public boolean validToCetak() {
    if (getSelectedPiutang().getStatus() != null) {
      if (getSelectedPiutang().getStatus().getId() == new Long(5)) {
        return false;
      } else if (getSelectedPiutang().getStatus().getId() == new Long(6)) {
        return false;
      } else if (getSelectedPiutang().getStatus().getId() == new Long(7)) {
        return false;
      } else if (getSelectedPiutang().getStatus().getId() == new Long(8)) {
        return false;
      }
    }
    return true;
  }


}
