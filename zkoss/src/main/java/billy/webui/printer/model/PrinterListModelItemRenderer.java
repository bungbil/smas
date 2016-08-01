package billy.webui.printer.model;

import java.io.Serializable;

import javax.print.PrintService;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class PrinterListModelItemRenderer implements ListitemRenderer, Serializable {


  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PrinterListModelItemRenderer.class);

  @Override
  public void render(Listitem item, Object data) throws Exception {

    final PrintService entity = (PrintService) data;

    final Listcell lc = new Listcell(entity.getName());
    lc.setParent(item);

    item.setAttribute("data", data);

  }

}
