package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.StatusDAO;
import billy.backend.model.Status;
import billy.backend.service.StatusService;

public class StatusServiceImpl implements StatusService {

  private StatusDAO statusDAO;

  @Override
  public void delete(Status entity) {
    getStatusDAO().delete(entity);
  }

  @Override
  public List<Status> getAllStatuss() {
    return getStatusDAO().getAllStatuss();
  }

  @Override
  public int getCountAllStatuss() {
    return getStatusDAO().getCountAllStatuss();
  }

  @Override
  public Status getNewStatus() {
    return getStatusDAO().getNewStatus();
  }

  @Override
  public Status getStatusByDeskripsiStatus(String string) {
    return getStatusDAO().getStatusByDeskripsiStatus(string);
  }

  @Override
  public Status getStatusByID(Long id) {
    return getStatusDAO().getStatusById(id);
  }

  public StatusDAO getStatusDAO() {
    return statusDAO;
  }

  @Override
  public List<Status> getStatussLikeDeskripsiStatus(String string) {
    return getStatusDAO().getStatussLikeDeskripsiStatus(string);
  }

  @Override
  public void saveOrUpdate(Status entity) {
    getStatusDAO().saveOrUpdate(entity);
  }

  public void setStatusDAO(StatusDAO statusDAO) {
    this.statusDAO = statusDAO;
  }

}
