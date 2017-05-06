package billy.webui.transaction.penjualan.approval;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Barang;
import billy.backend.model.Karyawan;
import billy.backend.model.Mandiri;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Status;
import billy.backend.model.Wilayah;
import billy.backend.service.BarangService;
import billy.backend.service.KaryawanService;
import billy.backend.service.MandiriService;
import billy.backend.service.PenjualanService;
import billy.backend.service.StatusService;
import billy.backend.service.WilayahService;
import billy.webui.master.barang.model.BarangListModelItemRenderer;
import billy.webui.master.karyawan.model.KaryawanListModelItemRenderer;
import billy.webui.master.mandiri.model.MandiriListModelItemRenderer;
import billy.webui.master.status.model.StatusListModelItemRenderer;
import billy.webui.master.wilayah.model.WilayahListModelItemRenderer;
import billy.webui.transaction.penjualan.model.PenjualanDetailListModelItemRenderer;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;


public class ApprovalPenjualanDetailCtrl extends GFCBaseCtrl implements Serializable {

  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(ApprovalPenjualanDetailCtrl.class);

  protected Window windowApprovalPenjualanDetail; // autowired

  protected Borderlayout borderlayout_ApprovalPenjualanDetail; // autowired

  protected Textbox txtb_NoFaktur; // autowired
  // protected Textbox txtb_NoOrderSheet; // autowired
  protected Textbox txtb_Mandiri; // autowired
  protected Datebox txtb_TglPenjualan; // autowired
  protected Datebox txtb_RencanaKirim; // autowired
  protected Listbox lbox_Wilayah;
  protected Listbox lbox_Sales1;
  protected Listbox lbox_Sales2;
  protected Listbox lbox_Mandiri;
  // protected Listbox lbox_Pengirim;
  protected Textbox txtb_NamaPelanggan; // autowired
  protected Textbox txtb_Telepon; // autowired
  protected Textbox txtb_Alamat; // autowired
  protected Textbox txtb_Alamat2; // autowired
  protected Textbox txtb_Alamat3; // autowired
  protected Textbox txtb_Remark; // autowired
  protected Radio radioStatusCash;
  protected Radio radioStatusKredit;
  protected Datebox txtb_TglAngsuran2;
  protected Combobox cmb_IntervalKredit;
  protected Decimalbox label_KreditPerBulan;
  protected Decimalbox label_Total;
  // protected Decimalbox txtb_Diskon; // autowired
  protected Decimalbox txtb_DownPayment; // autowired
  protected Listbox lbox_Status;
  // protected Decimalbox label_DiskonDP;
  protected Decimalbox label_GrandTotal;
  protected Label label_butuhApproval;
  protected Textbox txtb_ReasonApproval;
  protected Button btnApprovePenjualan;
  public Textbox txtb_ApprovedBy;
  public Textbox txtb_ApprovedRemark;

  protected Listbox lbox_Barang;
  protected Textbox txtb_NamaBarang;
  protected Intbox txtb_JumlahBarang;
  protected Decimalbox txtb_HargaBarang;
  protected Button btnNewBarang;

  public List<PenjualanDetail> listApprovalPenjualanDetail = new ArrayList<PenjualanDetail>();
  // Databinding
  private PenjualanDetail selectedPenjualanDetail;

  protected Listbox listBoxApprovalPenjualanDetail; // autowired
  // Databinding
  protected transient AnnotateDataBinder binder;
  private ApprovalPenjualanMainCtrl approvalPenjualanMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient PenjualanService penjualanService;
  private transient WilayahService wilayahService;
  private transient KaryawanService karyawanService;
  private transient BarangService barangService;
  private transient StatusService statusService;
  private transient MandiriService mandiriService;

  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public ApprovalPenjualanDetailCtrl() {
    super();
  }

  public void calculateGrandTotal() {
    BigDecimal grandTotal = BigDecimal.ZERO;
    BigDecimal diskonTotal = BigDecimal.ZERO;
    if (getSelectedPenjualan().getDiskon() != null) {
      diskonTotal = diskonTotal.add(getSelectedPenjualan().getDiskon());
    }
    if (getSelectedPenjualan().getDownPayment() != null) {
      diskonTotal = diskonTotal.add(getSelectedPenjualan().getDownPayment());
    }
    if (getSelectedPenjualan().getTotal() != null) {
      grandTotal = grandTotal.add(getSelectedPenjualan().getTotal());
    }
    grandTotal = grandTotal.subtract(diskonTotal);

    // label_DiskonDP.setValue(diskonTotal);

    getSelectedPenjualan().setGrandTotal(grandTotal);
    label_GrandTotal.setValue(grandTotal);

  }

  public BigDecimal calculateSubTotal(int itemQuantity, BigDecimal itemPrice) {
    BigDecimal itemCost = BigDecimal.ZERO;
    BigDecimal totalCost = BigDecimal.ZERO;

    itemCost = itemPrice.multiply(new BigDecimal(itemQuantity));
    totalCost = totalCost.add(itemCost);
    return totalCost;
  }

  public void calculateTotal() {
    BigDecimal total = BigDecimal.ZERO;
    for (PenjualanDetail pd : listApprovalPenjualanDetail) {
      total = total.add(pd.getTotal());
    }
    getSelectedPenjualan().setTotal(total);
    label_Total.setValue(total);
  }


  public void clearBarang() {
    lbox_Barang.setSelectedIndex(-1);
    txtb_NamaBarang.setValue("");
    txtb_JumlahBarang.setValue(null);
    txtb_HargaBarang.setValue(BigDecimal.ZERO);
    btnNewBarang.setDisabled(true);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);

    if (arg.containsKey("ModuleMainController")) {
      setApprovalPenjualanMainCtrl((ApprovalPenjualanMainCtrl) arg.get("ModuleMainController"));

      // SET THIS CONTROLLER TO THE module's Parent/MainController
      getApprovalPenjualanMainCtrl().setApprovalPenjualanDetailCtrl(this);

      // Get the selected object.
      // Check if this Controller if created on first time. If so,
      // than the selectedXXXBean should be null
      if (getApprovalPenjualanMainCtrl().getSelectedPenjualan() != null) {
        setSelectedPenjualan(getApprovalPenjualanMainCtrl().getSelectedPenjualan());
        doRefresh();
        txtb_ApprovedRemark.focus();
      } else
        setSelectedPenjualan(null);
    } else {
      setSelectedPenjualan(null);
    }

  }

  public void doApprovalMode() {
    // doReadOnlyMode(true);
    // txtb_ApprovedBy.setReadonly(false);
    txtb_ApprovedRemark.setReadonly(false);
    btnApprovePenjualan.setVisible(true);
  }

  public void doFillListboxPenjualanDetail() {

    getListBoxApprovalPenjualanDetail().setModel(new ListModelList(listApprovalPenjualanDetail));
    getListBoxApprovalPenjualanDetail().setItemRenderer(new PenjualanDetailListModelItemRenderer());

  }

  public void doFitSize(Event event) {

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - 152;
    borderlayout_ApprovalPenjualanDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowApprovalPenjualanDetail.invalidate();
  }

  public void doReadOnlyMode(boolean b) {
    // txtb_NoFaktur.setReadonly(b);
    // txtb_NoOrderSheet.setReadonly(b);
    // txtb_Mandiri.setReadonly(b);
    lbox_Wilayah.setDisabled(b);
    txtb_TglPenjualan.setDisabled(b);
    txtb_RencanaKirim.setDisabled(b);
    lbox_Sales1.setDisabled(b);
    lbox_Sales2.setDisabled(b);
    // lbox_Pengirim.setDisabled(b);
    txtb_NamaPelanggan.setReadonly(b);
    txtb_Telepon.setReadonly(b);
    txtb_Alamat.setReadonly(b);
    txtb_Alamat2.setReadonly(b);
    txtb_Alamat3.setReadonly(b);
    txtb_Remark.setReadonly(b);
    lbox_Mandiri.setDisabled(b);
    radioStatusCash.setDisabled(b);
    radioStatusKredit.setDisabled(b);
    // txtb_TglAngsuran2.setDisabled(b);
    cmb_IntervalKredit.setDisabled(b);
    // txtb_Diskon.setReadonly(b);

    txtb_DownPayment.setReadonly(b);
    // lbox_Status.setDisabled(b);

    lbox_Barang.setDisabled(b);
    txtb_NamaBarang.setReadonly(b);
    txtb_JumlahBarang.setReadonly(b);
    txtb_HargaBarang.setReadonly(b);
    btnNewBarang.setDisabled(b);


  }

  public void doRefresh() {
    if (getSelectedPenjualan() != null) {
      List<Wilayah> listWilayah = getWilayahService().getAllWilayahs();
      lbox_Wilayah.setModel(new ListModelList(listWilayah));
      lbox_Wilayah.setItemRenderer(new WilayahListModelItemRenderer());
      if (getSelectedPenjualan().getWilayah() != null) {
        ListModelList lml = (ListModelList) lbox_Wilayah.getModel();
        Wilayah wilayah =
            getWilayahService().getWilayahByID(getSelectedPenjualan().getWilayah().getId());
        lbox_Wilayah.setSelectedIndex(lml.indexOf(wilayah));
      }

      List<Mandiri> listMandiri = getMandiriService().getAllMandiris();
      lbox_Mandiri.setModel(new ListModelList(listMandiri));
      lbox_Mandiri.setItemRenderer(new MandiriListModelItemRenderer());
      if (getSelectedPenjualan().getMandiriId() != null) {
        ListModelList lml = (ListModelList) lbox_Mandiri.getModel();
        Mandiri mandiri =
            getMandiriService().getMandiriByID(getSelectedPenjualan().getMandiriId().getId());
        lbox_Mandiri.setSelectedIndex(lml.indexOf(mandiri));
      }

      List<Status> listStatus = getStatusService().getAllStatuss();
      lbox_Status.setModel(new ListModelList(listStatus));
      lbox_Status.setItemRenderer(new StatusListModelItemRenderer());
      if (getSelectedPenjualan().getStatus() != null) {
        ListModelList lml = (ListModelList) lbox_Status.getModel();
        Status status =
            getStatusService().getStatusByID(getSelectedPenjualan().getStatus().getId());
        lbox_Status.setSelectedIndex(lml.indexOf(status));
      }
      SecUser userLogin =
          ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
              .getSecUser();
      List<Karyawan> listKaryawan = getKaryawanService().getAllSalesKaryawansByUserLogin(userLogin);

      lbox_Sales1.setModel(new ListModelList(listKaryawan));
      lbox_Sales1.setItemRenderer(new KaryawanListModelItemRenderer());
      if (getSelectedPenjualan().getSales1() != null) {
        ListModelList lml = (ListModelList) lbox_Sales1.getModel();
        Karyawan karyawan =
            getKaryawanService().getKaryawanByID(getSelectedPenjualan().getSales1().getId());
        lbox_Sales1.setSelectedIndex(lml.indexOf(karyawan));
      }

      lbox_Sales2.setModel(new ListModelList(listKaryawan));
      lbox_Sales2.setItemRenderer(new KaryawanListModelItemRenderer());
      if (getSelectedPenjualan().getSales2() != null) {
        ListModelList lml = (ListModelList) lbox_Sales2.getModel();
        Karyawan karyawan =
            getKaryawanService().getKaryawanByID(getSelectedPenjualan().getSales2().getId());
        lbox_Sales2.setSelectedIndex(lml.indexOf(karyawan));
      }

      // lbox_Pengirim.setModel(new ListModelList(listKaryawan));
      // lbox_Pengirim.setItemRenderer(new KaryawanListModelItemRenderer());
      // if (getSelectedPenjualan().getPengirim() != null) {
      // ListModelList lml = (ListModelList) lbox_Pengirim.getModel();
      // Karyawan karyawan =
      // getKaryawanService().getKaryawanByID(getSelectedPenjualan().getPengirim().getId());
      // lbox_Pengirim.setSelectedIndex(lml.indexOf(karyawan));
      // }

      if (getSelectedPenjualan().getIntervalKredit() != 0) {
        cmb_IntervalKredit.setValue(String.valueOf(getSelectedPenjualan().getIntervalKredit()));
      } else {
        cmb_IntervalKredit.setValue("1");
      }

      if (getSelectedPenjualan().getMetodePembayaran() != null) {
        if (getSelectedPenjualan().getMetodePembayaran().equals(radioStatusCash.getLabel())) {
          radioStatusCash.setSelected(true);
          txtb_TglAngsuran2.setDisabled(true);
        }
        if (getSelectedPenjualan().getMetodePembayaran().equals(radioStatusKredit.getLabel())) {
          radioStatusKredit.setSelected(true);
        }
      } else {
        radioStatusCash.setSelected(true);
        txtb_TglAngsuran2.setDisabled(true);
      }

      List<Barang> listBarang =
          getBarangService().getAllBarangsByWilayah(getSelectedPenjualan().getWilayah());
      lbox_Barang.setModel(new ListModelList(listBarang));
      lbox_Barang.setItemRenderer(new BarangListModelItemRenderer());
      clearBarang();

      if (getSelectedPenjualan().getNoFaktur() != null) {
        listApprovalPenjualanDetail =
            getPenjualanService().getPenjualanDetailsByPenjualan(getSelectedPenjualan());
      } else {
        listApprovalPenjualanDetail = new ArrayList<PenjualanDetail>();
      }
      doFillListboxPenjualanDetail();

      if (getSelectedPenjualan().getDiskon() != null) {
        BigDecimal diskonTotal = BigDecimal.ZERO;
        diskonTotal = diskonTotal.add(getSelectedPenjualan().getDiskon());
        diskonTotal = diskonTotal.add(getSelectedPenjualan().getDownPayment());
        // label_DiskonDP.setValue(diskonTotal);
      } else {
        // label_DiskonDP.setValue(BigDecimal.ZERO);
      }

      if (getSelectedPenjualan().isNeedApproval()) {
        label_butuhApproval.setValue("Ya");
      } else {
        label_butuhApproval.setValue("Tidak");
      }
    }
  }

  public void emptyAllValue() {
    txtb_NoFaktur.setValue(null);
    // txtb_NoOrderSheet.setValue(null);
    txtb_Mandiri.setValue(null);
    txtb_TglPenjualan.setValue(null);
    txtb_RencanaKirim.setValue(null);
    txtb_NamaPelanggan.setValue(null);
    txtb_Telepon.setValue(null);
    txtb_Alamat.setValue(null);
    txtb_Remark.setValue(null);
    radioStatusKredit.setSelected(true);
    txtb_TglAngsuran2.setValue(null);
    cmb_IntervalKredit.setSelectedIndex(-1);
    lbox_Status.setSelectedIndex(-1);
    lbox_Wilayah.setSelectedIndex(-1);
    cmb_IntervalKredit.setSelectedIndex(-1);
    txtb_NamaBarang.setValue(null);
    txtb_JumlahBarang.setValue(null);
    // txtb_Diskon.setValue(BigDecimal.ZERO);
    txtb_DownPayment.setValue(BigDecimal.ZERO);
    txtb_HargaBarang.setValue(BigDecimal.ZERO);
    label_Total.setValue(BigDecimal.ZERO);
    // label_DiskonDP.setValue(BigDecimal.ZERO);
    label_GrandTotal.setValue(BigDecimal.ZERO);
    label_KreditPerBulan.setValue(BigDecimal.ZERO);
    lbox_Mandiri.setSelectedIndex(-1);
    txtb_Alamat2.setValue(null);
    txtb_Alamat3.setValue(null);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  public void generateNewNoFaktur() {
    if (getSelectedPenjualan().getNoFaktur() == null) {
      StringBuilder noFaktur = new StringBuilder();
      Karyawan divisi = null;
      Listitem itemSales1 = lbox_Sales1.getSelectedItem();
      if (itemSales1 != null) {
        ListModelList lml1 = (ListModelList) lbox_Sales1.getListModel();
        Karyawan karyawan = (Karyawan) lml1.get(itemSales1.getIndex());
        divisi = getKaryawanService().getKaryawanByID(karyawan.getSupervisorDivisi().getId());
      }

      if (txtb_TglPenjualan.getValue() != null && divisi != null) {
        Date tglPenjualan = txtb_TglPenjualan.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(tglPenjualan);
        int bulanPenjualan = cal.get(Calendar.MONTH) + 1;
        int tahunPenjualan = cal.get(Calendar.YEAR);
        String temp = String.valueOf(bulanPenjualan);
        if (temp.length() == 1) {
          temp = "0" + temp;
        }
        noFaktur.append(temp);

        int countPenjualanDivisi =
            getPenjualanService().getCountAllPenjualansByDivisi(divisi, tglPenjualan) + 1;
        temp = String.valueOf(countPenjualanDivisi);
        if (temp.length() == 1) {
          temp = "000" + temp;
        } else if (temp.length() == 2) {
          temp = "00" + temp;
        } else if (temp.length() == 3) {
          temp = "0" + temp;
        }
        noFaktur.append(temp);
        noFaktur.append(divisi.getInisialDivisi());

        temp = String.valueOf(tahunPenjualan);
        String substringYear = temp.length() > 2 ? temp.substring(temp.length() - 2) : temp;
        noFaktur.append(substringYear);

        getSelectedPenjualan().setNoFaktur(noFaktur.toString());
        txtb_NoFaktur.setValue(noFaktur.toString());
      }
    }
  }

  public ApprovalPenjualanMainCtrl getApprovalPenjualanMainCtrl() {
    return this.approvalPenjualanMainCtrl;
  }

  public BarangService getBarangService() {
    return barangService;
  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public KaryawanService getKaryawanService() {
    return karyawanService;
  }

  public List<PenjualanDetail> getListApprovalPenjualanDetail() {
    return listApprovalPenjualanDetail;
  }

  public Listbox getListBoxApprovalPenjualanDetail() {
    return listBoxApprovalPenjualanDetail;
  }

  public MandiriService getMandiriService() {
    return mandiriService;
  }

  /* Master BEANS */
  public Penjualan getPenjualan() {
    // STORED IN THE module's MainController
    return getApprovalPenjualanMainCtrl().getSelectedPenjualan();
  }

  public BindingListModelList getPenjualans() {
    // STORED IN THE module's MainController
    return getApprovalPenjualanMainCtrl().getPenjualans();
  }

  public PenjualanService getPenjualanService() {
    return this.penjualanService;
  }

  public Penjualan getSelectedPenjualan() {
    // STORED IN THE module's MainController
    return getApprovalPenjualanMainCtrl().getSelectedPenjualan();
  }

  public PenjualanDetail getSelectedPenjualanDetail() {
    return selectedPenjualanDetail;
  }

  public StatusService getStatusService() {
    return statusService;
  }

  public WilayahService getWilayahService() {
    return wilayahService;
  }

  public void onBlur$txtb_Diskon(Event event) throws InterruptedException {
    calculateGrandTotal();
  }

  public void onBlur$txtb_DownPayment(Event event) throws InterruptedException {
    calculateGrandTotal();
  }

  public void onChange$cmb_IntervalKredit(Event event) throws InterruptedException {
    BigDecimal kreditPerBulan = BigDecimal.ZERO;

    int interval = Integer.parseInt(cmb_IntervalKredit.getValue());
    if (interval > 1) {
      for (PenjualanDetail ld : listApprovalPenjualanDetail) {
        ld.setHarga(getBarangService().getHargaBarangByIntervalKredit(ld.getBarang(), interval));
        BigDecimal subTotal = calculateSubTotal(ld.getQty(), ld.getHarga());
        ld.setTotal(subTotal);
        /*
         * BigDecimal cicilanPerBulan = calculateSubTotal(ld.getQty(),
         * getBarangService().getCicilanPerBulanByIntervalKredit(ld.getBarang(), interval));
         * kreditPerBulan = kreditPerBulan.add(cicilanPerBulan);
         */
      }
    } else {
      radioStatusCash.setSelected(true);
    }

    doFillListboxPenjualanDetail();
    calculateTotal();
    calculateGrandTotal();

    if (interval > 1) {
      int temp = interval - 1;
      kreditPerBulan =
          getSelectedPenjualan().getTotal().divide(new BigDecimal(temp), 0, RoundingMode.HALF_UP);
      label_KreditPerBulan.setValue(kreditPerBulan);
      getSelectedPenjualan().setKreditPerBulan(kreditPerBulan);
    }

  }

  public void onChange$txtb_TglPenjualan(Event event) throws InterruptedException {
    generateNewNoFaktur();
  }

  public void onCheck$radioStatusCash(Event event) throws InterruptedException {
    cmb_IntervalKredit.setSelectedIndex(0);
    txtb_TglAngsuran2.setDisabled(true);
    txtb_TglAngsuran2.setValue(null);
    label_KreditPerBulan.setValue(BigDecimal.ZERO);
    getSelectedPenjualan().setKreditPerBulan(BigDecimal.ZERO);
  }

  public void onCheck$radioStatusKredit(Event event) throws InterruptedException {
    txtb_TglAngsuran2.setDisabled(false);
  }

  public void onClick$btnApprovePenjualan(Event event) throws InterruptedException {
    final Penjualan anPenjualan = getSelectedPenjualan();
    if (anPenjualan != null) {

      // Show a confirm box
      String msg = "Apakah anda yakin mengapprove penjualan ini? ";
      final String title = Labels.getLabel("message.Information");

      MultiLineMessageBox.doSetTemplate();
      if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
          true, new EventListener() {
            @Override
            public void onEvent(Event evt) {
              switch (((Integer) evt.getData()).intValue()) {
                case MultiLineMessageBox.YES:
                  try {
                    getApprovalPenjualanMainCtrl().doSave(evt);
                  } catch (InterruptedException e) {
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
    }


  }

  public void onClick$btnNewBarang(Event event) throws InterruptedException {
    Listitem itemBarang = lbox_Barang.getSelectedItem();
    if (itemBarang != null) {
      ListModelList lml1 = (ListModelList) lbox_Barang.getListModel();
      Barang barang = (Barang) lml1.get(itemBarang.getIndex());

      PenjualanDetail item = penjualanService.getNewPenjualanDetail();
      item.setBarang(barang);
      item.setHarga(txtb_HargaBarang.getValue());
      item.setQty(txtb_JumlahBarang.getValue());
      BigDecimal total =
          calculateSubTotal(txtb_JumlahBarang.getValue(), txtb_HargaBarang.getValue());
      item.setTotal(total);
      item.setPenjualan(getSelectedPenjualan());
      listApprovalPenjualanDetail.add(item);
      doFillListboxPenjualanDetail();
      clearBarang();
      calculateTotal();
      calculateGrandTotal();
    }

  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowApprovalPenjualanDetail(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void onDoubleClickedPenjualanDetailItem(Event event) {
    /*
     * Listitem item = this.listBoxApprovalPenjualanDetail.getSelectedItem(); if (item != null) {
     * setSelectedPenjualanDetail((PenjualanDetail) item.getAttribute("data")); //delete item //
     * Show a confirm box final String msg =
     * Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
     * getSelectedPenjualanDetail().getBarang().getNamaBarang(); final String title =
     * Labels.getLabel("message.Deleting.Record"); MultiLineMessageBox.doSetTemplate(); try { if
     * (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
     * true, new EventListener() {
     * @Override public void onEvent(Event evt) { switch (((Integer) evt.getData()).intValue()) {
     * case MultiLineMessageBox.YES: // try { deleteBean(); Iterator<PenjualanDetail> itr =
     * listApprovalPenjualanDetail.iterator(); while (itr.hasNext()) { PenjualanDetail pd =
     * itr.next(); if (pd.getBarang().getId() == getSelectedPenjualanDetail().getBarang().getId() &&
     * pd.getQty() == getSelectedPenjualanDetail().getQty() && pd.getHarga() ==
     * getSelectedPenjualanDetail().getHarga() && pd.getTotal() ==
     * getSelectedPenjualanDetail().getTotal() ) { itr.remove(); } } } catch (InterruptedException
     * e) { // TODO Auto-generated catch block e.printStackTrace(); } break; // case
     * MultiLineMessageBox.NO: break; // } } private void deleteBean() throws InterruptedException {
     * try { getPenjualanService().delete(getSelectedPenjualanDetail()); } catch
     * (DataAccessException e) {
     * ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString()); } } } ) ==
     * MultiLineMessageBox.YES) { } } catch (InterruptedException e) { // TODO Auto-generated catch
     * block e.printStackTrace(); } doFillListboxPenjualanDetail(); calculateTotal();
     * calculateGrandTotal(); }
     */
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  public void onSelect$lbox_Barang(Event event) throws InterruptedException {
    Listitem itemBarang = lbox_Barang.getSelectedItem();
    if (itemBarang != null) {
      ListModelList lml1 = (ListModelList) lbox_Barang.getListModel();
      Barang barang = (Barang) lml1.get(itemBarang.getIndex());
      btnNewBarang.setDisabled(false);
      txtb_NamaBarang.setValue(barang.getNamaBarang());
      txtb_JumlahBarang.setValue(1);
      if (cmb_IntervalKredit.getValue() != null) {
        txtb_HargaBarang.setValue(barangService.getHargaBarangByIntervalKredit(barang,
            Integer.parseInt(cmb_IntervalKredit.getValue())));
      }

    } else {
      clearBarang();
    }

  }

  public void onSelect$lbox_Sales1(Event event) throws InterruptedException {
    generateNewNoFaktur();
  }

  public void onSelect$lbox_Wilayah(Event event) throws InterruptedException {
    Listitem item = lbox_Wilayah.getSelectedItem();
    if (item != null) {
      ListModelList lml1 = (ListModelList) lbox_Wilayah.getListModel();
      Wilayah wilayah = (Wilayah) lml1.get(item.getIndex());
      List<Barang> listBarang = getBarangService().getAllBarangsByWilayah(wilayah);
      lbox_Barang.setModel(new ListModelList(listBarang));
      lbox_Barang.setItemRenderer(new BarangListModelItemRenderer());
      clearBarang();
    }
  }

  /* CONTROLLERS */
  public void setApprovalPenjualanMainCtrl(ApprovalPenjualanMainCtrl penjualanMainCtrl) {
    this.approvalPenjualanMainCtrl = penjualanMainCtrl;
  }

  public void setBarangService(BarangService barangService) {
    this.barangService = barangService;
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }

  public void setListBoxApprovalPenjualanDetail(Listbox listBoxApprovalPenjualanDetail) {
    this.listBoxApprovalPenjualanDetail = listBoxApprovalPenjualanDetail;
  }

  public void setListPenjualanDetail(List<PenjualanDetail> listApprovalPenjualanDetail) {
    this.listApprovalPenjualanDetail = listApprovalPenjualanDetail;
  }

  public void setMandiriService(MandiriService mandiriService) {
    this.mandiriService = mandiriService;
  }

  public void setPenjualan(Penjualan anPenjualan) {
    // STORED IN THE module's MainController
    getApprovalPenjualanMainCtrl().setSelectedPenjualan(anPenjualan);
  }

  public void setPenjualans(BindingListModelList penjualans) {
    // STORED IN THE module's MainController
    getApprovalPenjualanMainCtrl().setPenjualans(penjualans);
  }

  /* SERVICES */
  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }

  public void setSelectedPenjualan(Penjualan selectedPenjualan) {
    // STORED IN THE module's MainController
    getApprovalPenjualanMainCtrl().setSelectedPenjualan(selectedPenjualan);
  }

  public void setSelectedPenjualanDetail(PenjualanDetail selectedPenjualanDetail) {
    this.selectedPenjualanDetail = selectedPenjualanDetail;
  }

  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  public void setWilayahService(WilayahService wilayahService) {
    this.wilayahService = wilayahService;
  }

  /*
   * public Barang getSelectedBarang() { return selectedBarang; } public void
   * setSelectedBarang(Barang selectedBarang) { this.selectedBarang = selectedBarang; }
   */

  /* COMPONENTS and OTHERS */


}
