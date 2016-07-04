package billy.webui.master.wilayah.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Wilayah;


public class WilayahListModelItemRenderer implements ListitemRenderer, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(WilayahListModelItemRenderer.class);

  @Override
  public void render(Listitem item, Object data) throws Exception {

    final Wilayah entity = (Wilayah) data;

    final Listcell lc = new Listcell(entity.getNamaWilayah());
    lc.setParent(item);

    item.setAttribute("data", data);

  }

}
