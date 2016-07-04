package billy.backend.service;

import java.util.List;

import billy.backend.model.Status;

public interface StatusService {

  public void delete(Status entity);

  public List<Status> getAllStatuss();

  public int getCountAllStatuss();

  public Status getNewStatus();

  public Status getStatusByDeskripsiStatus(String string);

  public Status getStatusByID(Long id);

  public List<Status> getStatussLikeDeskripsiStatus(String string);

  public void saveOrUpdate(Status entity);

}
