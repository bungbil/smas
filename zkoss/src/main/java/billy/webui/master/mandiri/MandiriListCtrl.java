package billy.webui.master.mandiri;

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

import billy.backend.model.Mandiri;
import billy.backend.service.MandiriService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

public class MandiriListCtrl extends GFCBaseListCtrl<Mandiri> implements Serializable {

  private static final long serialVersionUID = -2170565288232491362L;
  private static final Logger logger = Logger.getLogger(MandiriListCtrl.class);

  protected Window windowMandiriList; // autowired
  protected Panel panelMandiriList; // autowired

  protected Borderlayout borderLayout_mandiriList; // autowired
  protected Paging paging_MandiriList; // autowired
  protected Listbox listBoxMandiri; // autowired
  protected Listheader listheader_MandiriList_Kode; // autowired
  protected Listheader listheader_MandiriList_Deskripsi; // autowired
  protected Listheader listheader_MandiriList_LastUpdate;
  protected Listheader listheader_MandiriList_UpdatedBy;

  // NEEDED for ReUse in the SearchWindow
  private HibernateSearchObject<Mandiri> searchObj;

  // row count for listbox
  private int countRows;

  // Databinding
  private AnnotateDataBinder binder;
  private MandiriMainCtrl mandiriMainCtrl;

  // ServiceDAOs / Domain Classes
  private transient MandiriService mandiriService;

  /**
   * default constructor.<br>
   */
  public MandiriListCtrl() {
    super();
  }

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);
    if (arg.containsKey("ModuleMainController")) {
      setMandiriMainCtrl((MandiriMainCtrl) arg.get("ModuleMainController"));
      getMandiriMainCtrl().setMandiriListCtrl(this);

      if (getMandiriMainCtrl().getSelectedMandiri() != null) {
        setSelectedMandiri(getMandiriMainCtrl().getSelectedMandiri());
      } else
        setSelectedMandiri(null);
    } else {
      setSelectedMandiri(null);
    }
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  public void doFillListbox() {

    doFitSize();

    // set the paging params
    paging_MandiriList.setPageSize(getCountRows());
    paging_MandiriList.setDetailed(true);

    // not used listheaders must be declared like ->
    // lh.setSortAscending(""); lh.setSortDescending("")
    listheader_MandiriList_Kode.setSortAscending(new FieldComparator("kodeMandiri", true));
    listheader_MandiriList_Kode.setSortDescending(new FieldComparator("kodeMandiri", false));

    listheader_MandiriList_Deskripsi
        .setSortAscending(new FieldComparator("deskripsiMandiri", true));
    listheader_MandiriList_Deskripsi.setSortDescending(new FieldComparator("deskripsiMandiri",
        false));

    listheader_MandiriList_UpdatedBy.setSortAscending(new FieldComparator("updatedBy", true));
    listheader_MandiriList_UpdatedBy.setSortDescending(new FieldComparator("updatedBy", false));

    listheader_MandiriList_LastUpdate.setSortAscending(new FieldComparator("lastUpdate", true));
    listheader_MandiriList_LastUpdate.setSortDescending(new FieldComparator("lastUpdate", false));

    // ++ create the searchObject and init sorting ++//
    // ++ create the searchObject and init sorting ++//
    searchObj = new HibernateSearchObject<Mandiri>(Mandiri.class, getCountRows());
    searchObj.addSort("kodeMandiri", false);
    setSearchObj(searchObj);

    // Set the BindingListModel
    getPagedBindingListWrapper().init(searchObj, getListBoxMandiri(), paging_MandiriList);
    BindingListModelList lml = (BindingListModelList) getListBoxMandiri().getModel();
    setMandiris(lml);

    // check if first time opened and init databinding for selectedBean
    if (getSelectedMandiri() == null) {
      // init the bean with the first record in the List
      if (lml.getSize() > 0) {
        final int rowIndex = 0;
        // only for correct showing after Rendering. No effect as an
        // Event
        // yet.
        getListBoxMandiri().setSelectedIndex(rowIndex);
        // get the first entry and cast them to the needed object
        setSelectedMandiri((Mandiri) lml.get(0));

        // call the onSelect Event for showing the objects data in the
        // statusBar
        Events.sendEvent(new Event("onSelect", getListBoxMandiri(), getSelectedMandiri()));
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
    borderLayout_mandiriList.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowMandiriList.invalidate();
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

  public Listbox getListBoxMandiri() {
    return this.listBoxMandiri;
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
  public Mandiri getMandiri() {
    // STORED IN THE module's MainController
    return getMandiriMainCtrl().getSelectedMandiri();
  }

  public MandiriMainCtrl getMandiriMainCtrl() {
    return this.mandiriMainCtrl;
  }

  public BindingListModelList getMandiris() {
    // STORED IN THE module's MainController
    return getMandiriMainCtrl().getMandiris();
  }

  public MandiriService getMandiriService() {
    return this.mandiriService;
  }

  public HibernateSearchObject<Mandiri> getSearchObj() {
    return this.searchObj;
  }

  public Mandiri getSelectedMandiri() {
    // STORED IN THE module's MainController
    return getMandiriMainCtrl().getSelectedMandiri();
  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */

  public void onCreate$windowMandiriList(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    doFillListbox();

    binder.loadAll();
  }

  /**
   * Selects the object in the listbox and change the tab.<br>
   * Event is forwarded in the corresponding listbox.
   */
  public void onDoubleClickedMandiriItem(Event event) {
    // logger.debug(event.toString());

    Mandiri anMandiri = getSelectedMandiri();

    if (anMandiri != null) {
      setSelectedMandiri(anMandiri);
      setMandiri(anMandiri);

      // check first, if the tabs are created
      if (getMandiriMainCtrl().getMandiriDetailCtrl() == null) {
        Events.sendEvent(new Event("onSelect", getMandiriMainCtrl().tabMandiriDetail, null));
        // if we work with spring beanCreation than we must check a
        // little bit deeper, because the Controller are preCreated ?
      } else if (getMandiriMainCtrl().getMandiriDetailCtrl().getBinder() == null) {
        Events.sendEvent(new Event("onSelect", getMandiriMainCtrl().tabMandiriDetail, null));
      }

      Events.sendEvent(new Event("onSelect", getMandiriMainCtrl().tabMandiriDetail, anMandiri));
    }
  }

  /**
   * When a listItem in the corresponding listbox is selected.<br>
   * Event is forwarded in the corresponding listbox.
   * 
   * @param event
   */
  public void onSelect$listBoxMandiri(Event event) {
    // logger.debug(event.toString());

    // selectedMandiri is filled by annotated databinding mechanism
    Mandiri anMandiri = getSelectedMandiri();

    if (anMandiri == null) {
      return;
    }

    // check first, if the tabs are created
    if (getMandiriMainCtrl().getMandiriDetailCtrl() == null) {
      Events.sendEvent(new Event("onSelect", getMandiriMainCtrl().tabMandiriDetail, null));
      // if we work with spring beanCreation than we must check a little
      // bit deeper, because the Controller are preCreated ?
    } else if (getMandiriMainCtrl().getMandiriDetailCtrl().getBinder() == null) {
      Events.sendEvent(new Event("onSelect", getMandiriMainCtrl().tabMandiriDetail, null));
    }

    // INIT ALL RELATED Queries/OBJECTS/LISTS NEW
    getMandiriMainCtrl().getMandiriDetailCtrl().setSelectedMandiri(anMandiri);
    getMandiriMainCtrl().getMandiriDetailCtrl().setMandiri(anMandiri);

    // store the selected bean values as current
    getMandiriMainCtrl().doStoreInitValues();

    // show the objects data in the statusBar
    String str = Labels.getLabel("common.Mandiri") + ": " + anMandiri.getKodeMandiri();
    EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(
        new Event("onChangeSelectedObject", null, str));

  }

  public void setBinder(AnnotateDataBinder binder) {
    this.binder = binder;
  }

  public void setCountRows(int countRows) {
    this.countRows = countRows;
  }

  public void setListBoxMandiri(Listbox listBoxMandiri) {
    this.listBoxMandiri = listBoxMandiri;
  }

  public void setMandiri(Mandiri anMandiri) {
    // STORED IN THE module's MainController
    getMandiriMainCtrl().setSelectedMandiri(anMandiri);
  }

  /* CONTROLLERS */
  public void setMandiriMainCtrl(MandiriMainCtrl mandiriMainCtrl) {
    this.mandiriMainCtrl = mandiriMainCtrl;
  }

  public void setMandiris(BindingListModelList mandiris) {
    // STORED IN THE module's MainController
    getMandiriMainCtrl().setMandiris(mandiris);
  }

  /* SERVICES */
  public void setMandiriService(MandiriService mandiriService) {
    this.mandiriService = mandiriService;
  }

  /* COMPONENTS and OTHERS */
  public void setSearchObj(HibernateSearchObject<Mandiri> searchObj) {
    this.searchObj = searchObj;
  }

  public void setSelectedMandiri(Mandiri selectedMandiri) {
    // STORED IN THE module's MainController
    getMandiriMainCtrl().setSelectedMandiri(selectedMandiri);
  }

}
