package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.StatusDAO;
import billy.backend.model.Status;
import billy.backend.service.StatusService;

public class StatusServiceImpl implements StatusService {

	private StatusDAO statusDAO;

	public StatusDAO getStatusDAO() {
		return statusDAO;
	}

	public void setStatusDAO(StatusDAO statusDAO) {
		this.statusDAO = statusDAO;
	}

	@Override
	public Status getNewStatus() {
		return getStatusDAO().getNewStatus();
	}

	@Override
	public Status getStatusByID(Long id) {
		return getStatusDAO().getStatusById(id);
	}

	@Override
	public List<Status> getAllStatuss() {
		return getStatusDAO().getAllStatuss();
	}

	@Override
	public void saveOrUpdate(Status entity) {
		getStatusDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(Status entity) {
		getStatusDAO().delete(entity);
	}

	@Override
	public List<Status> getStatussLikeKodeStatus(String string) {
		return getStatusDAO().getStatussLikeKodeStatus(string);
	}

	@Override
	public List<Status> getStatussLikeDeskripsiStatus(String string) {
		return getStatusDAO().getStatussLikeDeskripsiStatus(string);
	}

	@Override
	public int getCountAllStatuss() {
		return getStatusDAO().getCountAllStatuss();
	}

}
