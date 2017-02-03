package billy.webui.transaction.piutang;

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
import billy.backend.model.Piutang;
import billy.backend.service.PiutangService;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class PiutangListCtrl extends GFCBaseListCtrl<Piutang> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(PiutangListCtrl.class);

  protected Window windowPiutangList; // autowired
  protected Panel panelPiutangList; // autowired

  protected Borderlayout borderLayout_piutangList; // autowired
  protected Paging paging_PiutangList; // autowired
  protected Listbox listBoxPiutang; // autowired
  protected Listheader listheader_PiutangList_Warning; // autowired
  protected Listheader listheader_PiutangList_NoFaktur; // autowired
  protected Listheader listheader_PiutangList_NoKuitansi; // autowired
  protected Listheader listheader_PiutangList_PembayaranKe; // autowired
  protected Listheader listheader_PiutangList_TglPembayaran; // autowired
  protected Listheader listheader_PiutangList_Status; // autowired
  protected Listheader listheader_PiutangList_NilaiTagihan; // autowired
  protected Listheader listheader_PiutangList_Pembayaran; // autowired
  protected Listheader listheader_PiutangList_TglJatuhTempo; // autowired
  protected Listheader listheader_PiutangList_Kolektor; // autowired
  protected Listheader listheader_PiutangList_LastUpdate; // autowired
  protected Listheader listheader_PiutangList_UpdatedBy; // autowired

  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<Piutang> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private PiutangMainCtrl piutangMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient PiutangService piutangService;

  /**
   * default constructor.<br>
   */
  public PiutangListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setPiutangMainCtrl((PiutangMainCtrl) arg.get("ModuleMainController"));
      getPiutangMainCtrl().setPiutangListCtrl(this);

      if (getPiutangMainCtrl().getSelectedPiutang() != null) {
        setSelectedPiutang(getPiutangMainCtrl().getSelectedPiutang());
      } else
        setSelectedPiutang(null);
    } else {
      setSelectedPiutang(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_PiutangList.setPageSize(getCountRows());
    paging_PiutangList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")
    // listheader_PiutangList_Warning.setSortAscending(new FieldComparator("warning", true));
    // listheader_PiutangList_Warning.setSortDescending(new FieldComparator("warning", false));

    listheader_PiutangList_NoFaktur
        .setSortAscending(new FieldComparator("penjualan.noFaktur", true));
    listheader_PiutangList_NoFaktur.setSortDescending(new FieldComparator("penjualan.noFaktur",
        false));

    // listheader_PiutangList_NoKuitansi.setSortAscending(new FieldComparator("noKuitansi", true));
    // listheader_PiutangList_NoKuitansi.setSortDescending(new FieldComparator("noKuitansi",
    // false));

    listheader_PiutangList_PembayaranKe.setSortAscending(new FieldComparator("pembayaranKe", true));
    listheader_PiutangList_PembayaranKe
        .setSortDescending(new FieldComparator("pembayaranKe", false));

    listheader_PiutangList_TglPembayaran
        .setSortAscending(new FieldComparator("tglPembayaran", true));
    listheader_PiutangList_TglPembayaran.setSortDescending(new FieldComparator("tglPembayaran",
        false));

    listheader_PiutangList_Status.setSortAscending(new FieldComparator("status.deskripsiStatus",
        true));
    listheader_PiutangList_Status.setSortDescending(new FieldComparator("status.deskripsiStatus",
        false));

    listheader_PiutangList_NilaiTagihan.setSortAscending(new FieldComparator("nilaiTagihan", true));
    listheader_PiutangList_NilaiTagihan
        .setSortDescending(new FieldComparator("nilaiTagihan", false));

    listheader_PiutangList_Pembayaran.setSortAscending(new FieldComparator("pembayaran", true));
    listheader_PiutangList_Pembayaran.setSortDescending(new FieldComparator("pembayaran", false));

    listheader_PiutangList_TglJatuhTempo
        .setSortAscending(new FieldComparator("tglJatuhTempo", true));
    listheader_PiutangList_TglJatuhTempo.setSortDescending(new FieldComparator("tglJatuhTempo",
        false));

    listheader_PiutangList_Kolektor.setSortAscending(new FieldComparator("kolektor.namaPanggilan",
        true));
    listheader_PiutangList_Kolektor.setSortDescending(new FieldComparator("kolektor.namaPanggilan",
        false));

    listheader_PiutangList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_PiutangList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));

    listheader_PiutangList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_PiutangList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));


    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<Piutang>(Piutang.class, getCountRows());
    searchObj.addSort("tglJatuhTempo", false);
    searchObj.addFilter(new Filter("aktif", true));
    SecUser secUser =
        ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getSecUser();
    if (secUser.getKaryawan() != null) {
      Karyawan karyawan = secUser.getKaryawan();
      if (karyawan.getSupervisorDivisi() != null) {
        Karyawan supervisor = karyawan.getSupervisorDivisi();
        searchObj.addFilter(new Filter("penjualan.divisi.supervisorDivisi.id", supervisor.getId(),
            Filter.OP_EQUAL));

      }
    }

    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxPiutang(), paging_PiutangList);
    BindingListModelList lml = (BindingListModelList) getListBoxPiutang().getModel();
    setPiutangs(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedPiutang() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxPiutang().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedPiutang((Piutang) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events.sendEvent(new Event("onSelect", getListBoxPiutang(), getSelectedPiutang()));
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
    borderLayout_piutangList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowPiutangList.invalidate();
  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public int getCountRows() {
    return this.countRows;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++++ Business Logic ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public Listbox getListBoxPiutang() {
    return this.listBoxPiutang;
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
  // ++++++++++++++++++ getter / setter +++++++++++++++++++//
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

  /**
   * Best Pratice Hint:<br>
   * The setters/getters for the local annotated data binded Beans/Sets are administered in the
   * module's mainController. Working in this way you have clean line to share this beans/sets with
   * other controllers.
   */
  /* Master BEANS */
  public Piutang getPiutang() {
    // STORED IN THE module's MainController
    return getPiutangMainCtrl().getSelectedPiutang();
  }

  public PiutangMainCtrl getPiutangMainCtrl() {
    return this.piutangMainCtrl;
  }

  public BindingListModelList getPiutangs() {
    // STORED IN THE module's MainController
    return getPiutangMainCtrl().getPiutangs();
  }

  public PiutangService getPiutangService() {
    return this.piutangService;
  }

  public HibernateSearchObject<Piutang> getSearchObj() {
    return this.searchObj;
  }

  public Piutang getSelectedPiutang() {
    // STORED IN THE module's MainController
    return getPiutangMainCtrl().getSelectedPiutang();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowPiutangList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedPiutangItem(Event event) {
    // logger.debug(event.toString());

    Piutang anPiutang = getSelectedPiutang();

    if (anPiutang != null) {
      setSelectedPiutang(anPiutang);
      setPiutang(anPiutang);

      // check first, if the tabs are created
      if (getPiutangMainCtrl().getPiutangDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect", getPiutangMainCtrl().tabPiutangDetail, null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getPiutangMainCtrl().getPiutangDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect", getPiutangMainCtrl().tabPiutangDetail, null));
      }

      Events.sendEvent(new Event("onSelect", getPiutangMainCtrl().tabPiutangDetail, anPiutang));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxPiutang(Event event) {
    // logger.debug(event.toString());

    // selectedPiutang is filled by annotated databinding mechanism
    Piutang anPiutang = getSelectedPiutang();

    if (anPiutang == null) {
      return;
    }

    // check first, if the tabs are created
    if (getPiutangMainCtrl().getPiutangDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", getPiutangMainCtrl().tabPiutangDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getPiutangMainCtrl().getPiutangDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", getPiutangMainCtrl().tabPiutangDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getPiutangMainCtrl().getPiutangDetailCtrl().setSelectedPiutang(anPiutang);
    getPiutangMainCtrl().getPiutangDetailCtrl().setPiutang(anPiutang);

    // store the selected bean values as current
    getPiutangMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str = Labels.getLabel("common.Piutang") + ": " + anPiutang.getNoKuitansi();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setListBoxPiutang(Listbox listBoxPiutang) {
    this.listBoxPiutang = listBoxPiutang;
  }

  public void setPiutang(Piutang anPiutang) {
    // STORED IN THE module's MainController
    getPiutangMainCtrl().setSelectedPiutang(anPiutang);
  }

  /* CONTROLLERS */
  public void setPiutangMainCtrl(PiutangMainCtrl piutangMainCtrl) {
    this.piutangMainCtrl = piutangMainCtrl;
  }

  public void setPiutangs(BindingListModelList piutangs) {
    // STORED IN THE module's MainController
    getPiutangMainCtrl().setPiutangs(piutangs);
  }

  /* SERVICES */
  public void setPiutangService(PiutangService piutangService) {
    this.piutangService = piutangService;
  }

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<Piutang> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedPiutang(Piutang selectedPiutang) {
    // STORED IN THE module's MainController
    getPiutangMainCtrl().setSelectedPiutang(selectedPiutang);
  }

}
