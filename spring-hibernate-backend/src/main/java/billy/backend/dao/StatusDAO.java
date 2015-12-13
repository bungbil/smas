package billy.backend.dao;

import java.util.List;

import billy.backend.model.Status;

public interface StatusDAO {

	public Status getNewStatus();

	public Status getStatusById(Long id);

	public Status getStatusByDeskripsiStatus(String string);

	public List<Status> getAllStatuss();

	public int getCountAllStatuss();

	public List<Status> getStatussLikeDeskripsiStatus(String string);	

	public void deleteStatusById(long id);

	public void saveOrUpdate(Status entity);

	public void delete(Status entity);

	public void save(Status entity);

}
