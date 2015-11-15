package billy.webui.utility.parameter;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import billy.backend.model.Parameter;
import billy.webui.utility.parameter.model.ParameterListModelItemRenderer;

import com.googlecode.genericdao.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class ParameterListCtrl  extends GFCBaseListCtrl<Parameter>  implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private static final Logger logger = Logger.getLogger(ParameterListCtrl.class);

	protected Window parameterListWindow; // autowired
	protected Panel panel_ParameterList; // autowired

	// filter components
	protected Checkbox checkbox_ParameterList_ShowAll; // autowired
	protected Textbox tb_Parameter_ParameterName; // autowired

	// Listbox Parameter
	protected Borderlayout borderLayout_parametersList; // autowired
	protected Listbox listBoxParameters; // aurowired
	protected Paging paging_ParameterList; // aurowired
	protected Listheader listheader_ParameterList_parameterName; // aurowired
	protected Listheader listheader_ParameterList_parameterValue; // aurowired
	protected Listheader listheader_ParameterList_parameterDescription; // aurowired

	// row count for listbox
	private int countRows;

	/**
	 * default constructor.<br>
	 */
	public ParameterListCtrl() {
		super();
	}

	public void onCreate$parameterListWindow(Event event) throws Exception {
		
		int panelHeight = 50;
		// TODO put the logic for working with panel in the ApplicationWorkspace
		final boolean withPanel = false;
		if (withPanel == false) {
			panel_ParameterList.setVisible(false);
		} else {
			panel_ParameterList.setVisible(true);
			panelHeight = 0;
		}

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		height = height + panelHeight;
		final int maxListBoxHeight = height - 175;
		setCountRows(Math.round(maxListBoxHeight / 22));
		
		borderLayout_parametersList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_ParameterList_ShowAll.setChecked(true);

		listheader_ParameterList_parameterName.setSortAscending(new FieldComparator("parameterName", true));
		listheader_ParameterList_parameterName.setSortDescending(new FieldComparator("parameterName", false));

		
		
		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Parameter> soParameter = new HibernateSearchObject<Parameter>(Parameter.class, getCountRows());
		soParameter.addSort("parameterName", false);

		// set the paging params
		paging_ParameterList.setPageSize(getCountRows());
		paging_ParameterList.setDetailed(true);

		// Set the ListModel.
		getPagedListWrapper().init(soParameter, listBoxParameters, paging_ParameterList);
		// set the itemRenderer
		listBoxParameters.setItemRenderer(new ParameterListModelItemRenderer());

	}

	public void onParameterItemDoubleClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxParameters.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Parameter aParam = (Parameter) item.getAttribute("data");

			showDetailView(aParam);
		}
	}

	private void showDetailView(Parameter aParam) throws Exception {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("param", aParam);
		map.put("listBoxParameters", listBoxParameters);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/utility/parameter/parameterDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {

		Events.postEvent("onCreate", parameterListWindow, event);
		parameterListWindow.invalidate();
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_ParameterList_ShowAll(Event event) {

		// empty the text search boxes
		tb_Parameter_ParameterName.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Parameter> soParameter = new HibernateSearchObject<Parameter>(Parameter.class, getCountRows());
		soParameter.addSort("paramName", false);

		// Set the ListModel.
		getPagedListWrapper().init(soParameter, listBoxParameters, paging_ParameterList);

	}

	

	public void onClick$button_ParameterList_paramName(Event event) throws Exception {

		// if not empty
		if (!tb_Parameter_ParameterName.getValue().isEmpty()) {
			checkbox_ParameterList_ShowAll.setChecked(false); // unCheck

			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Parameter> soParameter = new HibernateSearchObject<Parameter>(Parameter.class, getCountRows());
			soParameter.addSort("paramName", false);

			soParameter.addFilter(new Filter("paramName", "%" + tb_Parameter_ParameterName.getValue() + "%", Filter.OP_ILIKE));

			// Set the ListModel.
			getPagedListWrapper().init(soParameter, listBoxParameters, paging_ParameterList);
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
