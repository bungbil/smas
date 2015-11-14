package de.forsthaus.webui.util;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Window;

public class StatusBarCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(StatusBarCtrl.class);

	protected Window winStatusBar; // autowired

	// Used Columns
	private Column statusBarAppVersion;

	// Localized labels for the columns
	private final String _labelAppVersion = "";

	/**
	 * Default constructor.
	 */
	public StatusBarCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		// Listener for applicationVersion
		EventQueues.lookup("appVersionEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				StatusBarCtrl.this.statusBarAppVersion.setLabel(StatusBarCtrl.this._labelAppVersion + msg);
			}
		});
	}

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 */
	public void onCreate$winStatusBar(Event event) {

		final Grid grid = new Grid();
		grid.setHeight("22px");
		grid.setStyle("padding: 0px;");
		grid.setParent(this.winStatusBar);

		final Columns columns = new Columns();
		columns.setSizable(false);
		columns.setParent(grid);

		this.statusBarAppVersion = new Column();
		this.statusBarAppVersion.setHeight("22px");
		this.statusBarAppVersion.setLabel(this._labelAppVersion);
		this.statusBarAppVersion.setWidth("50%");
		this.statusBarAppVersion.setStyle("background-color: #D6DCDE; color: #FF0000;");
		this.statusBarAppVersion.setParent(columns);

	}
}
