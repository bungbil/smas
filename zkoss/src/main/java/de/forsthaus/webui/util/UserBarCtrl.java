package de.forsthaus.webui.util;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

public class UserBarCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(UserBarCtrl.class);
	
	protected Window winUserBar; // autowired

	// Used Labels
	private Label userLabel;
	
	// Localized labels for the columns
	private final String _UserLabel = Labels.getLabel("common.User") + ": ";
	
	// Used Labels
	private Label userLabelText;
	
	private String _UserText = "";
	
	/**
	 * Default constructor.
	 */
	public UserBarCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		// Listener for user
		EventQueues.lookup("userNameEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				set_UserText(msg);
				doShowLabel();
			}
		});

		}

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 */
	public void onCreate$winUserBar(Event event) {

		Space space;

		winUserBar.setBorder("none");

		Hbox hbox = new Hbox();
		hbox.setParent(winUserBar);

		userLabel = new Label();
		userLabel.setStyle("text-align: right; font-size: 10px;");
		userLabel.setParent(hbox);
		userLabelText = new Label();
		userLabelText.setStyle("padding-left: 2px; text-align: right; color: blue; font-size: 10px;");
		userLabelText.setParent(hbox);

		space = new Space();
		space.setWidth("5px");
		space.setParent(hbox);
	}

	/**
	 * Shows the labels with values.<br>
	 */
	private void doShowLabel() {

		this.userLabel.setValue(this._UserLabel);
		this.userLabelText.setValue(get_UserText());

	}

	public void set_UserText(String _UserText) {
		this._UserText = _UserText;
	}

	public String get_UserText() {
		return this._UserText;
	}

}
