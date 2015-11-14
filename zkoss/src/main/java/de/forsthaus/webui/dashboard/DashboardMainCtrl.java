package de.forsthaus.webui.dashboard;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import de.forsthaus.webui.dashboard.module.DashboardApplicationInformationCtrl;
import de.forsthaus.webui.dashboard.module.DashboardApplicationLoginInformationCtrl;
import de.forsthaus.webui.dashboard.module.DashboardApplicationNewsListCtrl;
import de.forsthaus.webui.dashboard.module.DashboardGermanSoccerStatsCtrl;
import de.forsthaus.webui.dashboard.module.DashboardGoogleNewsCtrl;
import de.forsthaus.webui.dashboard.module.DashboardTableRecordsCounterCtrl;
import de.forsthaus.webui.dashboard.module.DashboardYoutubeVideoCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class DashboardMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient static final Logger logger = Logger.getLogger(DashboardMainCtrl.class);

	protected Window windowDashboard; // autowired
	protected Div divDashboardCenter; // autowired
	protected Div divDashboardEast; // autowired

	/**
	 * default constructor.<br>
	 */
	public DashboardMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * Set an 'alias' for this composer name in the zul file for access.<br>
		 * Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		self.setAttribute("controller", this, false);

		// Set the Dashboard unClosable
		try {
			Component cmp = (Component) windowDashboard.getParent().getParent().getParent();
			Tab menuTab = (Tab) cmp.getFellowIfAny("tab_menu_Item_Home", false);
			menuTab.setClosable(false);
		} catch (Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
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
	public void onCreate$windowDashboard(Event event) throws Exception {

		/**
		 * CENTER area
		 */
		//divDashboardCenter.appendChild(DashboardApplicationNewsListCtrl.show(250, true, 600000));

		Hbox hbox = new Hbox();
		hbox.setSclass("FDCenterNoBorder");
		hbox.setAlign("stretch");
		hbox.setPack("stretch");
		hbox.setWidth("100%");

		Cell cellLeft = new Cell();
		cellLeft.setId("cellLeft");
		// cellLeft.setWidth("100%");
		cellLeft.setStyle("padding: 0px;");
		cellLeft.setParent(hbox);

		Cell cellRight = new Cell();
		cellRight.setId("cellRight");
		cellRight.setWidth("280px");
		cellRight.setStyle("padding: 0px;");
		cellRight.setParent(hbox);

		//cellLeft.appendChild(DashboardTableRecordsCounterCtrl.show(200, true, 600000));
		//cellRight.appendChild(DashboardYoutubeVideoCtrl.show(198));

		
		divDashboardCenter.appendChild(hbox);
		//divDashboardCenter.appendChild(DashboardApplicationInformationCtrl.show(200));
		//divDashboardCenter.appendChild(DashboardApplicationLoginInformationCtrl.show(200));

		/**
		 * EAST area
		 */
		// divDashboardEast.appendChild(DashboardCalendarCtrl.show(146));
		// divDashboardEast.appendChild(DashboardGoogleTranslateCtrl.show(65));
		// divDashboardEast.appendChild(DashboardYoutubeVideoCtrl.show(198));
		// divDashboardEast.appendChild(DashboardBBCNewsCtrl.show(480, "no"));
		//divDashboardEast.appendChild(DashboardGoogleNewsCtrl.show(400, "yes"));
	//	divDashboardEast.appendChild(DashboardStockNewsCtrl.show(390, "no"));
		//divDashboardEast.appendChild(DashboardGermanSoccerStatsCtrl.show(460, "no"));

	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	
//	<div style="float: left; display: inline; margin-bottom: 5px; margin-top: 5px; background-color: #ffffff;"><iframe src="http://www.nachrichten.de/widget.html?sources=1158&amount=10&teasers=true&thumbnails=true&width=365&height=560" frameBorder="0" style="border: none; width: 365px; height: 560px;"></iframe>
//	<div id="nachrichtenWidgetFooter" style="border: none; height: 32px; padding: 0px; margin: 0px; font-size: 11px; width: 365px;"><a href=""><img src="http://www.nachrichten.de/images/widget/nde_widget_logo_130.png" style="margin-left: 5px; margin-top: 2px; border: none; float: left; display: inline;" alt="nachrichten.de Widget" title="nachrichten.de Widget" /></a><div style="padding-top: 5px; width: 98%; text-align: right;"><a href="http://www.nachrichten.de/widget/" style="text-decoration: none; color: #666666; font-family: Arial;">Eigenes Widget erstellen</a></div></div></div>
}
