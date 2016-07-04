package billy.webui.master.karyawan.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.Karyawan;



public class KaryawanListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(KaryawanListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final Karyawan entity = (Karyawan) data;

		final Listcell lc = new Listcell(entity.getNamaPanggilan()+"("+entity.getId()+")");		
		lc.setParent(item);

		item.setAttribute("data", data);

	}

}
