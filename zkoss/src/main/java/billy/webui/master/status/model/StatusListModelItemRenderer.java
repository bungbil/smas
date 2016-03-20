package billy.webui.master.status.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Status;



public class StatusListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StatusListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final Status entity = (Status) data;

		final Listcell lc = new Listcell(entity.getDeskripsiStatus());		
		lc.setParent(item);

		item.setAttribute("data", data);

	}

}
