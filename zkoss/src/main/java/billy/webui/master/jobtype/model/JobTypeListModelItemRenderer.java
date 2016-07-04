package billy.webui.master.jobtype.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import billy.backend.model.JobType;


public class JobTypeListModelItemRenderer implements ListitemRenderer, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(JobTypeListModelItemRenderer.class);

  @Override
  public void render(Listitem item, Object data) throws Exception {

    final JobType entity = (JobType) data;

    final Listcell lc = new Listcell(entity.getNamaJobType());
    lc.setParent(item);

    item.setAttribute("data", data);

  }

}
