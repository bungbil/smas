package billy.backend.service;

import java.util.List;

import billy.backend.model.Status;
public interface StatusService {

	public Status getNewStatus();

	public int getCountAllStatuss();

	public Status getStatusByID(Long id);
	public Status getStatusByDeskripsiStatus(String string);

	public List<Status> getAllStatuss();
	
	public List<Status> getStatussLikeDeskripsiStatus(String string);

	public void saveOrUpdate(Status entity);

	public void delete(Status entity);

}
