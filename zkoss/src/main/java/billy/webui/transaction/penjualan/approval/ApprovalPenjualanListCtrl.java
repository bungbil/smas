package billy.webui.transaction.penjualan.approval;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.service.PenjualanService;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class ApprovalPenjualanListCtrl extends GFCBaseListCtrl<Penjualan> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(ApprovalPenjualanListCtrl.class);

  protected Window windowApprovalPenjualanList; // autowired
  protected Panel panelApprovalPenjualanList; // autowired

  protected Borderlayout borderLayout_approvalPenjualanList; // autowired
  protected Paging paging_ApprovalPenjualanList; // autowired
  protected Listbox listBoxApprovalPenjualan; // autowired
  protected Listheader listheader_PenjualanList_NoFaktur; // autowired
  protected Listheader listheader_PenjualanList_TglPenjualan; // autowired
  protected Listheader listheader_PenjualanList_MetodePembayaran; // autowired
  protected Listheader listheader_PenjualanList_Total; // autowired
  protected Listheader listheader_PenjualanList_Piutang; // autowired
  protected Listheader listheader_PenjualanList_Status; // autowired
  protected Listheader listheader_PenjualanList_NamaPelanggan; // autowired
  protected Listheader listheader_PenjualanList_Telepon; // autowired
  protected Listheader listheader_PenjualanList_Sales1; // autowired
  protected Listheader listheader_PenjualanList_Sales2; // autowired
  protected Listheader listheader_PenjualanList_LastUpdate; // autowired
  protected Listheader listheader_PenjualanList_UpdatedBy; // autowired

  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<Penjualan> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private ApprovalPenjualanMainCtrl approvalPenjualanMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient PenjualanService penjualanService;

  /**
   * default constructor.<br>
   */
  public ApprovalPenjualanListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setApprovalPenjualanMainCtrl((ApprovalPenjualanMainCtrl) arg.get("ModuleMainController"));
      getApprovalPenjualanMainCtrl().setApprovalPenjualanListCtrl(this);

      if (getApprovalPenjualanMainCtrl().getSelectedPenjualan() != null) {
        setSelectedPenjualan(getApprovalPenjualanMainCtrl().getSelectedPenjualan());
      } else
        setSelectedPenjualan(null);
    } else {
      setSelectedPenjualan(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_ApprovalPenjualanList.setPageSize(getCountRows());
    paging_ApprovalPenjualanList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")
    listheader_PenjualanList_NoFaktur.setSortAscending(new FieldComparator("noFaktur", true));
    listheader_PenjualanList_NoFaktur.setSortDescending(new FieldComparator("noFaktur", false));

    listheader_PenjualanList_TglPenjualan
        .setSortAscending(new FieldComparator("tglPenjualan", true));
    listheader_PenjualanList_TglPenjualan.setSortDescending(new FieldComparator("tglPenjualan",
        false));

    listheader_PenjualanList_MetodePembayaran.setSortAscending(new FieldComparator(
        "metodePembayaran", true));
    listheader_PenjualanList_MetodePembayaran.setSortDescending(new FieldComparator(
        "metodePembayaran", false));

    listheader_PenjualanList_Total.setSortAscending(new FieldComparator("grandTotal", true));
    listheader_PenjualanList_Total.setSortDescending(new FieldComparator("grandTotal", false));

    listheader_PenjualanList_Piutang.setSortAscending(new FieldComparator("piutang", true));
    listheader_PenjualanList_Piutang.setSortDescending(new FieldComparator("piutang", false));

    listheader_PenjualanList_Status.setSortAscending(new FieldComparator("status.deskripsiStatus",
        true));
    listheader_PenjualanList_Status.setSortDescending(new FieldComparator("status.deskripsiStatus",
        false));

    listheader_PenjualanList_NamaPelanggan.setSortAscending(new FieldComparator("namaPelanggan",
        true));
    listheader_PenjualanList_NamaPelanggan.setSortDescending(new FieldComparator("namaPelanggan",
        false));

    listheader_PenjualanList_Telepon.setSortAscending(new FieldComparator("telepon", true));
    listheader_PenjualanList_Telepon.setSortDescending(new FieldComparator("telepon", false));

    listheader_PenjualanList_Sales1.setSortAscending(new FieldComparator("sales1.namaKtp", true));
    listheader_PenjualanList_Sales1.setSortDescending(new FieldComparator("sales1.namaKtp", false));

    listheader_PenjualanList_Sales2.setSortAscending(new FieldComparator("sales2.namaKtp", true));
    listheader_PenjualanList_Sales2.setSortDescending(new FieldComparator("sales2.namaKtp", false));

    listheader_PenjualanList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_PenjualanList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));

    listheader_PenjualanList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_PenjualanList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));


    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<Penjualan>(Penjualan.class, getCountRows());
    searchObj.addSort("noFaktur", false);
    searchObj.addFilter(new Filter("needApproval", true, Filter.OP_EQUAL));
    SecUser secUser =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    if (secUser.getKaryawan() != null) {
      Karyawan karyawan = secUser.getKaryawan();
      if (karyawan.getSupervisorDivisi() != null) {
        Karyawan supervisor = karyawan.getSupervisorDivisi();
        searchObj.addFilter(new Filter("divisi.supervisorDivisi.id", supervisor.getId(),
            Filter.OP_EQUAL));
      }
    }

    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxApprovalPenjualan(),
        paging_ApprovalPenjualanList);
    BindingListModelList lml = (BindingListModelList) getListBoxApprovalPenjualan().getModel();
    setPenjualans(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedPenjualan() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxApprovalPenjualan().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedPenjualan((Penjualan) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events.sendEvent(new Event("onSelect", getListBoxApprovalPenjualan(),
            getSelectedPenjualan()));
      }
    }

  }

  /**
   * Recalculates the container size for this controller and resize them. Calculate how many rows
   * have been place in the listbox. Get the currentDesktopHeight from a hidden Intbox from the
   * index.zul that are filled by onClientInfo() in the indexCtroller.
   */
  public void doFitSize() {

    // normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
    final int specialSize = 5;

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - specialSize - 148;
    setCountRows((int) Math.round(maxListBoxHeight / 17.7));
    borderLayout_approvalPenjualanList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowApprovalPenjualanList.invalidate();
  }

  public ApprovalPenjualanMainCtrl getApprovalPenjualanMainCtrl() {
    return this.approvalPenjualanMainCtrl;
  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public int getCountRows() {
    return this.countRows;
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
  // ++++++++++++++++++ getter / setter +++++++++++++++++++//
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

  public Listbox getListBoxApprovalPenjualan() {
    return this.listBoxApprovalPenjualan;
  }

  /**
   * Best Pratice Hint:<br>
   * The setters/getters for the local annotated data binded Beans/Sets are administered in the
   * module's mainController. Working in this way you have clean line to share this beans/sets with
   * other controllers.
   */
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

  public HibernateSearchObject<Penjualan> getSearchObj() {
    return this.searchObj;
  }

  public Penjualan getSelectedPenjualan() {
    // STORED IN THE module's MainController
    return getApprovalPenjualanMainCtrl().getSelectedPenjualan();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowApprovalPenjualanList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedPenjualanItem(Event event) {
    // logger.debug(event.toString());

    Penjualan anPenjualan = getSelectedPenjualan();

    if (anPenjualan != null) {
      setSelectedPenjualan(anPenjualan);
      setPenjualan(anPenjualan);

      // check first, if the tabs are created
      if (getApprovalPenjualanMainCtrl().getApprovalPenjualanDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect",
            getApprovalPenjualanMainCtrl().tabApprovalPenjualanDetail, null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getApprovalPenjualanMainCtrl().getApprovalPenjualanDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect",
            getApprovalPenjualanMainCtrl().tabApprovalPenjualanDetail, null));
      }

      Events.sendEvent(new Event("onSelect",
          getApprovalPenjualanMainCtrl().tabApprovalPenjualanDetail, anPenjualan));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxApprovalPenjualan(Event event) {
    // logger.debug(event.toString());

    // selectedPenjualan is filled by annotated databinding mechanism
    Penjualan anPenjualan = getSelectedPenjualan();

    if (anPenjualan == null) {
      return;
    }

    // check first, if the tabs are created
    if (getApprovalPenjualanMainCtrl().getApprovalPenjualanDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect",
          getApprovalPenjualanMainCtrl().tabApprovalPenjualanDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getApprovalPenjualanMainCtrl().getApprovalPenjualanDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect",
          getApprovalPenjualanMainCtrl().tabApprovalPenjualanDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getApprovalPenjualanMainCtrl().getApprovalPenjualanDetailCtrl().setSelectedPenjualan(
        anPenjualan);
    getApprovalPenjualanMainCtrl().getApprovalPenjualanDetailCtrl().setPenjualan(anPenjualan);

    // store the selected bean values as current
    getApprovalPenjualanMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str = Labels.getLabel("common.Penjualan") + ": " + anPenjualan.getNoFaktur();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  /* CONTROLLERS */
  public void setApprovalPenjualanMainCtrl(ApprovalPenjualanMainCtrl approvalPenjualanMainCtrl) {
    this.approvalPenjualanMainCtrl = approvalPenjualanMainCtrl;
  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setListBoxApprovalPenjualan(Listbox listBoxApprovalPenjualan) {
    this.listBoxApprovalPenjualan = listBoxApprovalPenjualan;
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

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<Penjualan> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedPenjualan(Penjualan selectedPenjualan) {
    // STORED IN THE module's MainController
    getApprovalPenjualanMainCtrl().setSelectedPenjualan(selectedPenjualan);
  }

}
