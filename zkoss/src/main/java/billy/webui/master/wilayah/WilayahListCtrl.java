package billy.webui.master.wilayah;

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

import billy.backend.model.Wilayah;
import billy.backend.service.WilayahService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class WilayahListCtrl extends GFCBaseListCtrl<Wilayah> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(WilayahListCtrl.class);

  protected Window windowWilayahList; // autowired
  protected Panel panelWilayahList; // autowired

  protected Borderlayout borderLayout_wilayahList; // autowired
  protected Paging paging_WilayahList; // autowired
  protected Listbox listBoxWilayah; // autowired
  protected Listheader listheader_WilayahList_Kode; // autowired
  protected Listheader listheader_WilayahList_Nama; // autowired
  protected Listheader listheader_WilayahList_Status; // autowired
  protected Listheader listheader_WilayahList_LastUpdate;
  protected Listheader listheader_WilayahList_UpdatedBy;

  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<Wilayah> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private WilayahMainCtrl wilayahMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient WilayahService wilayahService;

  /**
   * default constructor.<br>
   */
  public WilayahListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setWilayahMainCtrl((WilayahMainCtrl) arg.get("ModuleMainController"));
      getWilayahMainCtrl().setWilayahListCtrl(this);

      if (getWilayahMainCtrl().getSelectedWilayah() != null) {
        setSelectedWilayah(getWilayahMainCtrl().getSelectedWilayah());
      } else
        setSelectedWilayah(null);
    } else {
      setSelectedWilayah(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_WilayahList.setPageSize(getCountRows());
    paging_WilayahList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")
    listheader_WilayahList_Kode.setSortAscending(new FieldComparator("kodeWilayah", true));
    listheader_WilayahList_Kode.setSortDescending(new FieldComparator("kodeWilayah", false));

    listheader_WilayahList_Nama.setSortAscending(new FieldComparator("namaWilayah", true));
    listheader_WilayahList_Nama.setSortDescending(new FieldComparator("namaWilayah", false));

    listheader_WilayahList_Status.setSortAscending(new FieldComparator("status", true));
    listheader_WilayahList_Status.setSortDescending(new FieldComparator("status", false));

    listheader_WilayahList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_WilayahList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));

    listheader_WilayahList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_WilayahList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));


    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<Wilayah>(Wilayah.class, getCountRows());
    searchObj.addSort("kodeWilayah", false);
    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxWilayah(), paging_WilayahList);
    BindingListModelList lml = (BindingListModelList) getListBoxWilayah().getModel();
    setWilayahs(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedWilayah() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxWilayah().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedWilayah((Wilayah) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events.sendEvent(new Event("onSelect", getListBoxWilayah(), getSelectedWilayah()));
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
    borderLayout_wilayahList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowWilayahList.invalidate();
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

  public Listbox getListBoxWilayah() {
    return this.listBoxWilayah;
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
  // ++++++++++++++++++ getter / setter +++++++++++++++++++//
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

  public HibernateSearchObject<Wilayah> getSearchObj() {
    return this.searchObj;
  }

  public Wilayah getSelectedWilayah() {
    // STORED IN THE module's MainController
    return getWilayahMainCtrl().getSelectedWilayah();
  }

  /**
   * Best Pratice Hint:<br>
   * The setters/getters for the local annotated data binded Beans/Sets are administered in the
   * module's mainController. Working in this way you have clean line to share this beans/sets with
   * other controllers.
   */
  /* Master BEANS */
  public Wilayah getWilayah() {
    // STORED IN THE module's MainController
    return getWilayahMainCtrl().getSelectedWilayah();
  }

  public WilayahMainCtrl getWilayahMainCtrl() {
    return this.wilayahMainCtrl;
  }

  public BindingListModelList getWilayahs() {
    // STORED IN THE module's MainController
    return getWilayahMainCtrl().getWilayahs();
  }

  public WilayahService getWilayahService() {
    return this.wilayahService;
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowWilayahList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedWilayahItem(Event event) {
    // logger.debug(event.toString());

    Wilayah anWilayah = getSelectedWilayah();

    if (anWilayah != null) {
      setSelectedWilayah(anWilayah);
      setWilayah(anWilayah);

      // check first, if the tabs are created
      if (getWilayahMainCtrl().getWilayahDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect", getWilayahMainCtrl().tabWilayahDetail, null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getWilayahMainCtrl().getWilayahDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect", getWilayahMainCtrl().tabWilayahDetail, null));
      }

      Events.sendEvent(new Event("onSelect", getWilayahMainCtrl().tabWilayahDetail, anWilayah));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxWilayah(Event event) {
    // logger.debug(event.toString());

    // selectedWilayah is filled by annotated databinding mechanism
    Wilayah anWilayah = getSelectedWilayah();

    if (anWilayah == null) {
      return;
    }

    // check first, if the tabs are created
    if (getWilayahMainCtrl().getWilayahDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", getWilayahMainCtrl().tabWilayahDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getWilayahMainCtrl().getWilayahDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", getWilayahMainCtrl().tabWilayahDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getWilayahMainCtrl().getWilayahDetailCtrl().setSelectedWilayah(anWilayah);
    getWilayahMainCtrl().getWilayahDetailCtrl().setWilayah(anWilayah);

    // store the selected bean values as current
    getWilayahMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str = Labels.getLabel("common.Wilayah") + ": " + anWilayah.getKodeWilayah();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setListBoxWilayah(Listbox listBoxWilayah) {
    this.listBoxWilayah = listBoxWilayah;
  }

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<Wilayah> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedWilayah(Wilayah selectedWilayah) {
    // STORED IN THE module's MainController
    getWilayahMainCtrl().setSelectedWilayah(selectedWilayah);
  }

  public void setWilayah(Wilayah anWilayah) {
    // STORED IN THE module's MainController
    getWilayahMainCtrl().setSelectedWilayah(anWilayah);
  }

  /* CONTROLLERS */
  public void setWilayahMainCtrl(WilayahMainCtrl wilayahMainCtrl) {
    this.wilayahMainCtrl = wilayahMainCtrl;
  }

  public void setWilayahs(BindingListModelList wilayahs) {
    // STORED IN THE module's MainController
    getWilayahMainCtrl().setWilayahs(wilayahs);
  }

  /* SERVICES */
  public void setWilayahService(WilayahService wilayahService) {
    this.wilayahService = wilayahService;
  }

}
