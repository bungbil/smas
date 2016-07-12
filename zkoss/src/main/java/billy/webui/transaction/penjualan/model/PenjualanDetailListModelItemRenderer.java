package billy.webui.transaction.penjualan.model;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.PenjualanDetail;

public class PenjualanDetailListModelItemRenderer implements ListitemRenderer, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PenjualanDetailListModelItemRenderer.class);

  @Override
  public void render(Listitem item, Object data) throws Exception {

    final PenjualanDetail entity = (PenjualanDetail) data;
    DecimalFormat df = new DecimalFormat("#,###");
    Listcell lc = new Listcell(entity.getBarang().getKodeBarang());
    lc.setParent(item);
    lc = new Listcell(entity.getBarang().getNamaBarang());
    lc.setParent(item);
    lc = new Listcell(String.valueOf(entity.getQty()));
    lc.setParent(item);
    lc = new Listcell(String.valueOf(df.format(entity.getHarga())));
    lc.setParent(item);
    lc = new Listcell(String.valueOf(df.format(entity.getTotal())));
    lc.setParent(item);
    item.setAttribute("data", data);
    ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedPenjualanDetailItem");
  }

}
