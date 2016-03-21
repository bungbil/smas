package billy.webui.transaction.penjualan.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Barang;

public class BarangListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BarangListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final Barang entity = (Barang) data;

		final Listcell lc = new Listcell(entity.getNamaBarang());		
		lc.setParent(item);

		item.setAttribute("data", data);

	}

}