package billy.webui.master.satuanbarang;

import java.io.Serializable;

import org.apache.log4j.Logger;
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

import billy.backend.model.SatuanBarang;
import billy.backend.service.SatuanBarangService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class SatuanBarangListCtrl extends GFCBaseListCtrl<SatuanBarang> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(SatuanBarangListCtrl.class);

  protected Window windowSatuanBarangList; // autowired
  protected Panel panelSatuanBarangList; // autowired

  protected Borderlayout borderLayout_satuanBarangList; // autowired
  protected Paging paging_SatuanBarangList; // autowired
  protected Listbox listBoxSatuanBarang; // autowired
  protected Listheader listheader_SatuanBarangList_Kode; // autowired
  protected Listheader listheader_SatuanBarangList_Deskripsi; // autowired
  protected Listheader listheader_SatuanBarangList_LastUpdate;
  protected Listheader listheader_SatuanBarangList_UpdatedBy;

  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<SatuanBarang> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private SatuanBarangMainCtrl satuanBarangMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient SatuanBarangService satuanBarangService;

  /**
   * default constructor.<br>
   */
  public SatuanBarangListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setSatuanBarangMainCtrl((SatuanBarangMainCtrl) arg.get("ModuleMainController"));
      getSatuanBarangMainCtrl().setSatuanBarangListCtrl(this);

      if (getSatuanBarangMainCtrl().getSelectedSatuanBarang() != null) {
        setSelectedSatuanBarang(getSatuanBarangMainCtrl().getSelectedSatuanBarang());
      } else
        setSelectedSatuanBarang(null);
    } else {
      setSelectedSatuanBarang(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_SatuanBarangList.setPageSize(getCountRows());
    paging_SatuanBarangList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")
    listheader_SatuanBarangList_Kode
        .setSortAscending(new FieldComparator("kodeSatuanBarang", true));
    listheader_SatuanBarangList_Kode.setSortDescending(new FieldComparator("kodeSatuanBarang",
        false));

    listheader_SatuanBarangList_Deskripsi.setSortAscending(new FieldComparator(
        "deskripsiSatuanBarang", true));
    listheader_SatuanBarangList_Deskripsi.setSortDescending(new FieldComparator(
        "deskripsiSatuanBarang", false));

    listheader_SatuanBarangList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_SatuanBarangList_UpdatedBy
        .setSortDescending(new FieldComparator("updatedBy", false));

    listheader_SatuanBarangList_LastUpdate
        .setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_SatuanBarangList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate",
        false));

    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<SatuanBarang>(SatuanBarang.class, getCountRows());
    searchObj.addSort("kodeSatuanBarang", false);
    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxSatuanBarang(), paging_SatuanBarangList);
    BindingListModelList lml = (BindingListModelList) getListBoxSatuanBarang().getModel();
    setSatuanBarangs(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedSatuanBarang() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxSatuanBarang().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedSatuanBarang((SatuanBarang) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events
            .sendEvent(new Event("onSelect", getListBoxSatuanBarang(), getSelectedSatuanBarang()));
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
    borderLayout_satuanBarangList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowSatuanBarangList.invalidate();
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

  public Listbox getListBoxSatuanBarang() {
    return this.listBoxSatuanBarang;
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
  public SatuanBarang getSatuanBarang() {
    // STORED IN THE module's MainController
    return getSatuanBarangMainCtrl().getSelectedSatuanBarang();
  }

  public SatuanBarangMainCtrl getSatuanBarangMainCtrl() {
    return this.satuanBarangMainCtrl;
  }

  public BindingListModelList getSatuanBarangs() {
    // STORED IN THE module's MainController
    return getSatuanBarangMainCtrl().getSatuanBarangs();
  }

  public SatuanBarangService getSatuanBarangService() {
    return this.satuanBarangService;
  }

  public HibernateSearchObject<SatuanBarang> getSearchObj() {
    return this.searchObj;
  }

  public SatuanBarang getSelectedSatuanBarang() {
    // STORED IN THE module's MainController
    return getSatuanBarangMainCtrl().getSelectedSatuanBarang();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowSatuanBarangList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedSatuanBarangItem(Event event) {
    // logger.debug(event.toString());

    SatuanBarang anSatuanBarang = getSelectedSatuanBarang();

    if (anSatuanBarang != null) {
      setSelectedSatuanBarang(anSatuanBarang);
      setSatuanBarang(anSatuanBarang);

      // check first, if the tabs are created
      if (getSatuanBarangMainCtrl().getSatuanBarangDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect", getSatuanBarangMainCtrl().tabSatuanBarangDetail,
            null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getSatuanBarangMainCtrl().getSatuanBarangDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect", getSatuanBarangMainCtrl().tabSatuanBarangDetail,
            null));
      }

      Events.sendEvent(new Event("onSelect", getSatuanBarangMainCtrl().tabSatuanBarangDetail,
          anSatuanBarang));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxSatuanBarang(Event event) {
    // logger.debug(event.toString());

    // selectedSatuanBarang is filled by annotated databinding mechanism
    SatuanBarang anSatuanBarang = getSelectedSatuanBarang();

    if (anSatuanBarang == null) {
      return;
    }

    // check first, if the tabs are created
    if (getSatuanBarangMainCtrl().getSatuanBarangDetailCtrl() == null) {
      Events
          .sendEvent(new Event("onSelect", getSatuanBarangMainCtrl().tabSatuanBarangDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getSatuanBarangMainCtrl().getSatuanBarangDetailCtrl().getBinder() == null) {
      Events
          .sendEvent(new Event("onSelect", getSatuanBarangMainCtrl().tabSatuanBarangDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getSatuanBarangMainCtrl().getSatuanBarangDetailCtrl().setSelectedSatuanBarang(anSatuanBarang);
    getSatuanBarangMainCtrl().getSatuanBarangDetailCtrl().setSatuanBarang(anSatuanBarang);

    // store the selected bean values as current
    getSatuanBarangMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str =
        Labels.getLabel("common.SatuanBarang") + ": " + anSatuanBarang.getKodeSatuanBarang();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setListBoxSatuanBarang(Listbox listBoxSatuanBarang) {
    this.listBoxSatuanBarang = listBoxSatuanBarang;
  }

  public void setSatuanBarang(SatuanBarang anSatuanBarang) {
    // STORED IN THE module's MainController
    getSatuanBarangMainCtrl().setSelectedSatuanBarang(anSatuanBarang);
  }

  /* CONTROLLERS */
  public void setSatuanBarangMainCtrl(SatuanBarangMainCtrl satuanBarangMainCtrl) {
    this.satuanBarangMainCtrl = satuanBarangMainCtrl;
  }

  public void setSatuanBarangs(BindingListModelList satuanBarangs) {
    // STORED IN THE module's MainController
    getSatuanBarangMainCtrl().setSatuanBarangs(satuanBarangs);
  }

  /* SERVICES */
  public void setSatuanBarangService(SatuanBarangService satuanBarangService) {
    this.satuanBarangService = satuanBarangService;
  }

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<SatuanBarang> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedSatuanBarang(SatuanBarang selectedSatuanBarang) {
    // STORED IN THE module's MainController
    getSatuanBarangMainCtrl().setSelectedSatuanBarang(selectedSatuanBarang);
  }

}
