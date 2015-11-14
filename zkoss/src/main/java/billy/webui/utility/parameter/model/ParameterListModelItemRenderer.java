package billy.webui.utility.parameter.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Parameter;

import de.forsthaus.backend.model.SecRole;

public class ParameterListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ParameterListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final Parameter param = (Parameter) data;
		Listcell lc;

		lc = new Listcell(param.getParamName());
		lc.setStyle("padding-left: 5px");
		lc.setParent(item);

		lc = new Listcell(param.getParamValue());
		lc.setStyle("padding-left: 5px");
		lc.setParent(item);
		
		lc = new Listcell(param.getDescription());
		lc.setStyle("padding-left: 5px");
		lc.setParent(item);

		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onParameterItemDoubleClicked");

	}

}
