package billy.webui.transaction.kolektor.penerimaanpembayaran;


import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.commons.lang.StringUtils;
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

import billy.backend.model.Barang;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.BarangService;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.backend.service.PiutangService;
import billy.backend.service.StatusService;
import billy.webui.master.barang.model.BarangListModelItemRenderer;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.master.status.model.StatusListModelItemRenderer;
import billy.webui.printer.model.PrinterListModelItemRenderer;
import billy.webui.transaction.penjualan.model.PenjualanDetailListModelItemRenderer;
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
  protected Datebox txtb_tglJatuhTempoBerikut;

  protected Decimalbox txtb_Pembayaran;
  protected Decimalbox txtb_Diskon;
  protected Textbox txtb_Keterangan;
  protected Textbox txtb_NamaPelanggan;
  protected Combobox cmb_StatusFinal;


  protected Textbox txtb_NamaPelangganPiutang; // autowired
  protected Textbox txtb_Telepon; // autowired
  protected Textbox txtb_Alamat; // autowired
  protected Textbox txtb_Alamat2; // autowired
  protected Textbox txtb_Alamat3; // autowired


  protected Listbox lbox_Barang;
  protected Textbox txtb_KodeBarang;
  protected Decimalbox txtb_TotalPembayaran;
  protected Decimalbox txtb_HargaBarang;
  protected Decimalbox txtb_NextCicilan;

  protected Listbox lbox_OldBarang;
  protected Textbox txtb_OldKodeBarang;
  protected Decimalbox txtb_OldHargaBarang;

  protected Label label_butuhApproval;
  protected Textbox txtb_ReasonApproval;
  protected Button btnApprovePiutang;
  public Textbox txtb_ApprovedBy;
  public Textbox txtb_ApprovedRemark;

  protected Button btnCetak;
  protected Button btnCetakKwitansiSekarang;
  protected Listbox lbox_Printer;

  protected Listbox listBoxPenjualanDetail; // autowired
  private PenjualanDetail selectedPenjualanDetail;
  // ServiceDAOs / Domain Classes
  private PenjualanService penjualanService;

  private PiutangService piutangService;

  private BarangService barangService;
  private KaryawanService karyawanService;
  private PrintService selectedPrinter;

  private StatusService statusService;

  DecimalFormat df = new DecimalFormat("#,###");

  Piutang piutang = new Piutang();
  Piutang nextPiutang = new Piutang();

  /**
   * default constructor.<br>
   */
  public PenerimaanPembayaranMainCtrl() {
    super();
  }

  private void calculateNextCicilan() {
    if (!txtb_KodeBarang.getValue().isEmpty()) {
      int interval = piutang.getPenjualan().getIntervalKredit();
      int sisaInterval = interval - piutang.getPembayaranKe();
      BigDecimal totalPembayaranPenjualan =
          piutang.getPenjualan().getTotal().subtract(piutang.getPenjualan().getPiutang());
      txtb_TotalPembayaran.setValue(totalPembayaranPenjualan);

      BigDecimal hargaBarang = BigDecimal.ZERO;
      BigDecimal pembayaran = BigDecimal.ZERO;
      BigDecimal diskon = BigDecimal.ZERO;
      if (txtb_HargaBarang.getValue() != null) {
        hargaBarang = txtb_HargaBarang.getValue();
      }
      if (txtb_Pembayaran.getValue() != null) {
        pembayaran = txtb_Pembayaran.getValue();
      }
      if (txtb_Diskon.getValue() != null) {
        diskon = txtb_Diskon.getValue();
      }
      BigDecimal sisaPiutangTukarBarang =
          hargaBarang.subtract(totalPembayaranPenjualan).subtract(pembayaran).add(diskon);
      BigDecimal nextCicilan =
          sisaPiutangTukarBarang.divide(new BigDecimal(sisaInterval), 0, RoundingMode.HALF_UP);
      txtb_NextCicilan.setValue(nextCicilan);
    }
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
    // System.out.println("Status final : " + cmb_StatusFinal.getValue());
    String message = "";
    if (cmb_StatusFinal.getValue() != null) {
      if (cmb_StatusFinal.getValue().equals("DISKON")) {
        message += "- Kwitansi akan di Finalkan dengan status DISKON \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(5)); // DISKON
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue().equals("TARIK BARANG")) {
        message += "- Kwitansi akan di Finalkan dengan status TARIK BARANG \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(6)); // TARIK BARANG
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue().equals("MASALAH")) {
        message += "- Kwitansi akan di Finalkan dengan status MASALAH \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(7)); // MASALAH
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue().equals("FINAL")) {
        message += "- Kwitansi akan di Finalkan dengan status FINAL \n";
        Status statusFinal = getStatusService().getStatusByID(new Long(8)); // FINAL
        piutang.setStatusFinal(statusFinal);
      } else if (cmb_StatusFinal.getValue().equals("UNDUR")) {
        Status statusFinal = getStatusService().getStatusByID(new Long(9)); // UNDUR
        piutang.setStatusFinal(statusFinal);
        piutang.setStatus(statusFinal);
      } else {
        message = "";
        piutang.setStatusFinal(null);
        Status statusProses = getStatusService().getStatusByID(new Long(3)); // PROSES
        piutang.setStatus(statusProses);
        piutang.setNeedApproval(false);
        piutang.setReasonApproval("");
        txtb_ReasonApproval.setValue("");
        label_butuhApproval.setValue("Tidak");
      }
    }


    if (StringUtils.isNotEmpty(message)) {
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
        Status status = getStatusService().getStatusByID(new Long(4)); // KURANG_BAYAR
        piutang.setStatus(status);

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


    boolean tukarBarang = false;
    Listitem oldItemBarang = lbox_OldBarang.getSelectedItem();
    if (oldItemBarang != null) {
      ListModelList lmlBarang = (ListModelList) lbox_OldBarang.getListModel();
      Barang barang = (Barang) lmlBarang.get(oldItemBarang.getIndex());
      piutang.setTukarOldBarang(barang);
      if (txtb_OldHargaBarang.getValue() != null) {
        piutang.setTukarOldHarga(txtb_OldHargaBarang.getValue());
      } else {
        piutang.setTukarOldHarga(null);
      }
    }

    Listitem itemBarang = lbox_Barang.getSelectedItem();
    if (itemBarang != null) {
      ListModelList lmlBarang = (ListModelList) lbox_Barang.getListModel();
      Barang barang = (Barang) lmlBarang.get(itemBarang.getIndex());
      piutang.setTukarBarang(barang);
      tukarBarang = true;
      calculateNextCicilan();
      if (txtb_HargaBarang.getValue() != null) {
        piutang.setTukarHarga(txtb_HargaBarang.getValue());
      } else {
        piutang.setTukarHarga(null);
      }
    }

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
    } else if (txtb_NilaiTagihan.getValue().compareTo(pembayaran) == 0) {
      piutang.setFullPayment(true);
    }

    boolean alamatBerubah = false;
    if (!txtb_NamaPelangganPiutang.getValue().equals(piutang.getNamaPelanggan())
        || !txtb_Telepon.getValue().equals(piutang.getTelepon())
        || !txtb_Alamat.getValue().equals(piutang.getAlamat())
        || !txtb_Alamat2.getValue().equals(piutang.getAlamat2())
        || !txtb_Alamat3.getValue().equals(piutang.getAlamat3())) {
      alamatBerubah = true;
    }

    piutang.setNamaPelanggan(txtb_NamaPelangganPiutang.getValue());
    piutang.setTelepon(txtb_Telepon.getValue());
    piutang.setAlamat(txtb_Alamat.getValue());
    piutang.setAlamat2(txtb_Alamat2.getValue());
    piutang.setAlamat3(txtb_Alamat3.getValue());

    String userName =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsername();
    piutang.setLastUpdate(new Date());
    piutang.setUpdatedBy(userName);

    BigDecimal kekuranganBayar = BigDecimal.ZERO;
    if (!tukarBarang) {
      kekuranganBayar =
          piutang.getNilaiTagihan().add(piutang.getKekuranganBayar())
              .subtract(piutang.getPembayaran()).subtract(diskon);
    }

    if (!piutang.isNeedApproval())
      piutang.setAktif(false);

    // save it to database
    getPiutangService().saveOrUpdate(piutang);
    nextPiutang = piutangService.getNextPiutang(piutang);
    if (nextPiutang != null) {
      if (txtb_tglJatuhTempoBerikut.getValue() != null) {
        nextPiutang.setTglJatuhTempo(txtb_tglJatuhTempoBerikut.getValue());
        // update ke semua piutang interval sebulan
        getPiutangService().updateTglJatuhTempo(nextPiutang);
        // getPiutangService().saveOrUpdate(nextPiutang);
      }
    }

    if (alamatBerubah) {
      getPiutangService().updateAlamat(piutang);
    }


    if (tukarBarang) {
      if (nextPiutang != null) {
        if (txtb_NextCicilan.getValue() != null
            || !BigDecimal.ZERO.equals(txtb_NextCicilan.getValue())) {
          try { // update ke semua piutang cicilan nilai tagihan selanjutnya
            getPiutangService().updateNextNilaiTagihan(piutang, txtb_NextCicilan.getValue());
          } catch (Exception e) {
            logger.info("gagal update nilai tagihan selanjutnya");
          }
        }
      }
    }

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
    // penjualan.getTotal() harus di calculate lagi karena tukar barang
    BigDecimal selisihTukarBarang = BigDecimal.ZERO;
    if (tukarBarang) {
      selisihTukarBarang = piutang.getTukarHarga().subtract(piutang.getTukarOldHarga());
    }
    penjualan.setPiutang(penjualan.getTotal().add(selisihTukarBarang).subtract(totalPaid));

    penjualan.setTukarBarang(tukarBarang);
    getPenjualanService().saveOrUpdate(penjualan);

    if (penjualan.getPiutang().compareTo(BigDecimal.ZERO) == 0) {
      penjualan.setStatus(statusLunas);
      getPenjualanService().saveOrUpdate(penjualan);
      getPiutangService().deleteNextPiutang(piutang);
      ZksampleMessageUtils
          .showErrorMessage("No Faktur " + piutang.getNoFaktur() + " sudah LUNAS!!");


    } else if (!piutang.isNeedApproval()) {
      printNextKuitansi(kekuranganBayar, statusProses);
    }

    emptyAllValue();
    panelResult.setVisible(false);
    panelApproval.setVisible(false);
    piutang = null;
    nextPiutang = null;
    txtb_SearchNoKwitansi.setValue("");
    txtb_SearchNoKwitansi.focus();
  }

  public void doSearchNoFaktur() throws Exception {
    if (txtb_SearchNoKwitansi.getValue() != null) {
      piutang = piutangService.getPiutangByNoFaktur(txtb_SearchNoKwitansi.getValue().toUpperCase());

      if (piutang != null) {

        emptyAllValue();
        txtb_NoFaktur.setValue(piutang.getPenjualan().getNoFaktur());
        txtb_NoKuitansi.setValue(piutang.getNoKuitansi());
        txtb_PembayaranKe.setValue(String.valueOf(piutang.getPembayaranKe()));
        txtb_TglJatuhTempo.setValue(piutang.getTglJatuhTempo());
        txtb_NilaiTagihan.setValue(piutang.getNilaiTagihan());
        txb_KekuranganBayar.setValue(piutang.getKekuranganBayar());
        txtb_NamaPelanggan.setValue(piutang.getPenjualan().getNamaPelanggan());
        List<Status> listStatus = getStatusService().getAllStatuss();
        lbox_Status.setModel(new ListModelList(listStatus));
        lbox_Status.setItemRenderer(new StatusListModelItemRenderer());
        if (piutang.getStatus() != null) {
          ListModelList lml = (ListModelList) lbox_Status.getModel();
          Status status = getStatusService().getStatusByID(piutang.getStatus().getId());
          lbox_Status.setSelectedIndex(lml.indexOf(status));
        }

        List<Karyawan> listKaryawan = getKaryawanService().getKaryawansByJobTypeId(new Long(6));// Kolektor
        List<Karyawan> listDivisi = getKaryawanService().getKaryawansByJobTypeId(new Long(2));// Divisi
        listKaryawan.addAll(listDivisi);

        lbox_Kolektor.setModel(new ListModelList(listKaryawan));
        lbox_Kolektor.setItemRenderer(new KaryawanListModelItemRenderer());
        if (piutang.getKolektor() != null) {
          ListModelList lml = (ListModelList) lbox_Kolektor.getModel();
          Karyawan karyawan = getKaryawanService().getKaryawanByID(piutang.getKolektor().getId());
          lbox_Kolektor.setSelectedIndex(lml.indexOf(karyawan));
          txtb_KodeKolektor.setValue(karyawan.getKodeKaryawan());
          panelResult.setVisible(true);
        }
        txtb_tglBawaKolektor.setValue(piutang.getTglBawaKolektor());

        if (piutang.getTglPembayaran() != null) {
          txtb_tglPembayaran.setValue(piutang.getTglPembayaran());
        } else {
          txtb_tglPembayaran.setValue(new Date());
        }


        // txtb_Pembayaran.setValue(piutang.getPembayaran());
        // txtb_Diskon.setValue(piutang.getDiskon());
        if (piutang.getDiskon() == null) {
          txtb_Diskon.setValue(BigDecimal.ZERO);
        }
        // txtb_Keterangan.setValue(piutang.getKeterangan());
        panelApproval.setVisible(piutang.isNeedApproval());

        nextPiutang = piutangService.getNextPiutang(piutang);
        if (nextPiutang != null) {
          txtb_tglJatuhTempoBerikut.setValue(nextPiutang.getTglJatuhTempo());
        }


        if (StringUtils.isBlank(piutang.getNamaPelanggan())) {
          txtb_NamaPelangganPiutang.setValue(piutang.getPenjualan().getNamaPelanggan());
          piutang.setNamaPelanggan(piutang.getPenjualan().getNamaPelanggan());
        } else {
          txtb_NamaPelangganPiutang.setValue(piutang.getNamaPelanggan());
        }

        if (StringUtils.isBlank(piutang.getTelepon())) {
          txtb_Telepon.setValue(piutang.getPenjualan().getTelepon());
          piutang.setTelepon(piutang.getPenjualan().getTelepon());
        } else {
          txtb_Telepon.setValue(piutang.getTelepon());
        }

        if (StringUtils.isBlank(piutang.getAlamat())) {
          txtb_Alamat.setValue(piutang.getPenjualan().getAlamat());
          piutang.setAlamat(piutang.getPenjualan().getAlamat());
        } else {
          txtb_Alamat.setValue(piutang.getAlamat());
        }

        if (StringUtils.isBlank(piutang.getAlamat2())) {
          txtb_Alamat2.setValue(piutang.getPenjualan().getAlamat2());
          piutang.setAlamat2(piutang.getPenjualan().getAlamat2());
        } else {
          txtb_Alamat2.setValue(piutang.getAlamat2());
        }

        if (StringUtils.isBlank(piutang.getAlamat3())) {
          txtb_Alamat3.setValue(piutang.getPenjualan().getAlamat3());
          piutang.setAlamat3(piutang.getPenjualan().getAlamat3());
        } else {
          txtb_Alamat3.setValue(piutang.getAlamat3());
        }

        if (piutang.getStatusFinal() != null) {
          // piutang.getStatusFinal().equals(l)
          // cmb_StatusFinal
        }


        List<PenjualanDetail> listPenjualanDetail = new ArrayList<PenjualanDetail>();
        listPenjualanDetail =
            getPenjualanService().getPenjualanDetailsTukarBarangByPenjualan(piutang.getPenjualan());
        if (listPenjualanDetail.size() > 0) {
          getListBoxPenjualanDetail().setModel(new ListModelList(listPenjualanDetail));
          getListBoxPenjualanDetail().setItemRenderer(new PenjualanDetailListModelItemRenderer());
        }

        List<Barang> listBarang =
            getBarangService().getAllBarangsByWilayah(piutang.getPenjualan().getWilayah());
        lbox_OldBarang.setModel(new ListModelList(listBarang));
        lbox_OldBarang.setItemRenderer(new BarangListModelItemRenderer());
        lbox_Barang.setModel(new ListModelList(listBarang));
        lbox_Barang.setItemRenderer(new BarangListModelItemRenderer());
        if (piutang.getTukarBarang() != null) {
          ListModelList lml = (ListModelList) lbox_Barang.getModel();
          Barang barang = getBarangService().getBarangByID(piutang.getTukarBarang().getId());
          lbox_Barang.setSelectedIndex(lml.indexOf(barang));
          txtb_KodeBarang.setValue(barang.getKodeBarang());
          txtb_HargaBarang.setValue(piutang.getTukarHarga());
        }


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

  public void emptyAllValue() {
    try {


      txtb_Pembayaran.setValue(BigDecimal.ZERO);
      txtb_Diskon.setValue(BigDecimal.ZERO);
      txtb_Keterangan.setValue("");
      txtb_NamaPelangganPiutang.setValue("");
      txtb_Telepon.setValue("");
      txtb_Alamat.setValue("");
      txtb_Alamat2.setValue("");
      txtb_Alamat3.setValue("");
      txtb_OldKodeBarang.setValue("");
      txtb_OldHargaBarang.setValue(BigDecimal.ZERO);
      txtb_KodeBarang.setValue("");
      txtb_HargaBarang.setValue(BigDecimal.ZERO);
      txtb_TotalPembayaran.setValue(BigDecimal.ZERO);
      txtb_NextCicilan.setValue(BigDecimal.ZERO);

    } catch (Exception e) {
      logger.info("error empty value" + e.getMessage());
    }
  }

  public BarangService getBarangService() {
    return barangService;
  }

  public KaryawanService getKaryawanService() {
    return this.karyawanService;
  }

  public Listbox getListBoxPenjualanDetail() {
    return listBoxPenjualanDetail;
  }

  public PenjualanService getPenjualanService() {
    return penjualanService;
  }

  /* SERVICES */
  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public PenjualanDetail getSelectedPenjualanDetail() {
    return selectedPenjualanDetail;
  }

  public Piutang getSelectedPiutang() {
    // STORED IN THE module's MainController
    return this.piutang;
  }

  public StatusService getStatusService() {
    return statusService;
  }

  public void onBlur$txtb_Diskon(Event event) throws InterruptedException {
    calculateNextCicilan();
  }

  public void onBlur$txtb_HargaBarang(Event event) throws InterruptedException {
    calculateNextCicilan();
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

  public void onBlur$txtb_Pembayaran(Event event) throws InterruptedException {
    calculateNextCicilan();
  }

  public void onChange$txtb_KodeBarang(Event event) throws InterruptedException {
    if (txtb_KodeBarang.getValue() != null) {
      Barang barang = null;
      if (piutang.getPenjualan().getWilayah() == null) {
        barang = getBarangService().getBarangByKodeBarang(txtb_KodeBarang.getValue().trim());
      } else {
        barang =
            getBarangService().getBarangByKodeBarangAndWilayah(txtb_KodeBarang.getValue().trim(),
                piutang.getPenjualan().getWilayah());
      }
      if (barang != null) {
        ListModelList lml = (ListModelList) lbox_Barang.getModel();
        lbox_Barang.setSelectedIndex(lml.indexOf(barang));
        int interval = piutang.getPenjualan().getIntervalKredit();
        txtb_HargaBarang.setValue(barangService.getHargaBarangByIntervalKredit(barang, interval));
        calculateNextCicilan();
      } else {
        lbox_Barang.setSelectedIndex(-1);
        txtb_HargaBarang.setValue(BigDecimal.ZERO);
        txtb_NextCicilan.setValue(BigDecimal.ZERO);
        txtb_TotalPembayaran.setValue(BigDecimal.ZERO);
        MultiLineMessageBox.doSetTemplate();
        MultiLineMessageBox.show("Kode Barang tidak ditemukan", "Information",
            MultiLineMessageBox.OK, "Information", true);
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

  public void onChange$txtb_tglJatuhTempoBerikut(Event event) throws InterruptedException {
    if (txtb_tglJatuhTempoBerikut.getValue() != null && txtb_TglJatuhTempo.getValue() != null) {
      if (txtb_tglJatuhTempoBerikut.getValue().equals(txtb_TglJatuhTempo.getValue())) {
        MultiLineMessageBox.show(
            "Tanggal Jatuh Tempo Kuitansi berikutnya tidak bisa sama dengan Kuitansi saat ini",
            "Information", MultiLineMessageBox.OK, "Information", true);
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


  public void onClick$btnSave(Event event) throws Exception {
    String message = "";

    message += doCheckApproval();
    message += doCheckPembayaran();
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

  public void onDoubleClickedPenjualanDetailItem(Event event) {
    Listitem item = this.listBoxPenjualanDetail.getSelectedItem();
    if (item != null) {
      setSelectedPenjualanDetail((PenjualanDetail) item.getAttribute("data"));
      // select item
      // Show a confirm box
      final String msg =
          Labels.getLabel("message.Question.Are_you_sure_to_tukar_barang") + "\n\n --> "
              + getSelectedPenjualanDetail().getBarang().getNamaBarang();
      final String title = Labels.getLabel("message.Tukar.Barang");

      MultiLineMessageBox.doSetTemplate();
      try {
        if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO,
            Messagebox.QUESTION, true, new EventListener() {


              @Override
              public void onEvent(Event evt) {
                switch (((Integer) evt.getData()).intValue()) {
                  case MultiLineMessageBox.YES:

                    ListModelList lml = (ListModelList) lbox_OldBarang.getModel();
                    lbox_OldBarang.setSelectedIndex(lml.indexOf(getSelectedPenjualanDetail()
                        .getBarang()));
                    txtb_OldKodeBarang.setValue(getSelectedPenjualanDetail().getBarang()
                        .getKodeBarang());
                    txtb_OldHargaBarang.setValue(getSelectedPenjualanDetail().getTotal());


                    break; //
                  case MultiLineMessageBox.NO:
                    break; //
                }
              }
            }

        ) == MultiLineMessageBox.YES) {
        }
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }


    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

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

              if ((cmb_StatusFinal.getValue().equals("DISKON")
                  || cmb_StatusFinal.getValue().equals("TARIK BARANG")
                  || cmb_StatusFinal.getValue().equals("MASALAH")
                  || cmb_StatusFinal.getValue().equals("FINAL") || (cmb_StatusFinal.getValue()
                  .equals("UNDUR")) && piutang.getPembayaran().equals(BigDecimal.ZERO))) {
                // no need to print

              } else {
                final Window win = (Window) Path.getComponent("/outerIndexWindow");
                List<Piutang> listPiutang = new ArrayList<Piutang>();
                listPiutang.add(nextPiutang);
                new CetakKuitansiTextPrinter(win, listPiutang, selectedPrinter);
              }
            }
          }
        } catch (Exception e) {
          ZksampleMessageUtils.showErrorMessage(e.getMessage().toString());
          e.printStackTrace();
        }
      } else {
        ZksampleMessageUtils.showErrorMessage("Silakan pilih printer");

      }
    }
  }

  public void setBarangService(BarangService barangService) {
    this.barangService = barangService;
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  public void setListBoxPenjualanDetail(Listbox listBoxPenjualanDetail) {
    this.listBoxPenjualanDetail = listBoxPenjualanDetail;
  }

  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }


  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /* Master BEANS */


  public void setSelectedPenjualanDetail(PenjualanDetail selectedPenjualanDetail) {
    this.selectedPenjualanDetail = selectedPenjualanDetail;
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
