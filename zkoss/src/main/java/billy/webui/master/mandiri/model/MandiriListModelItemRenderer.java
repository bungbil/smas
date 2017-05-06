package billy.webui.master.mandiri.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Mandiri;


public class MandiriListModelItemRenderer implements ListitemRenderer, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(MandiriListModelItemRenderer.class);

  @Override
  public void render(Listitem item, Object data) throws Exception {

    final Mandiri entity = (Mandiri) data;

    final Listcell lc = new Listcell(entity.getDeskripsiMandiri());
    lc.setParent(item);

    item.setAttribute("data", data);

  }

}
