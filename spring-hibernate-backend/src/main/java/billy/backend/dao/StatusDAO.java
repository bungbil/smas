package billy.backend.dao;

import java.util.List;

import billy.backend.model.Status;

public interface StatusDAO {

  public void delete(Status entity);

  public void deleteStatusById(long id);

  public List<Status> getAllStatuss();

  public int getCountAllStatuss();

  public Status getNewStatus();

  public Status getStatusByDeskripsiStatus(String string);

  public Status getStatusById(Long id);

  public List<Status> getStatussLikeDeskripsiStatus(String string);

  public void save(Status entity);

  public void saveOrUpdate(Status entity);

}
