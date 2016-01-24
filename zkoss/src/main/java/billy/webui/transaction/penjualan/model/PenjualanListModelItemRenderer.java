package billy.webui.transaction.penjualan.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Penjualan;

import de.forsthaus.backend.model.Order;

public class PenjualanListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PenjualanListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final Penjualan obj = (Penjualan) data;

		Listcell lc = new Listcell(obj.getNoFaktur());
		lc.setParent(item);
		lc = new Listcell(obj.getTglPenjualan().toLocaleString());
		lc.setParent(item);
		lc = new Listcell(obj.getMetodePembayaran());
		lc.setParent(item);
		lc = new Listcell(obj.getGrandTotal().toString());
		lc.setParent(item);
		lc = new Listcell(obj.getPiutang().toString());		
		lc.setParent(item);
		lc = new Listcell(obj.getStatus().getDeskripsiStatus());
		lc.setParent(item);
		lc = new Listcell(obj.getNamaPelanggan());
		lc.setParent(item);
		lc = new Listcell(obj.getTelepon());
		lc.setParent(item);
		lc = new Listcell(obj.getSales1().getNamaPanggilan());
		lc.setParent(item);
		lc = new Listcell(obj.getSales2().getNamaPanggilan());
		lc.setParent(item);		
		lc = new Listcell(obj.getLastUpdate().toLocaleString());
		lc.setParent(item);
		lc = new Listcell(obj.getUpdatedBy());
		lc.setParent(item);
		

		// lc = new Listcell();
		// Image img = new Image();
		// img.setSrc("/images/icons/page_detail.gif");
		// lc.appendChild(img);
		// lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedOrderItem");

	}

}
