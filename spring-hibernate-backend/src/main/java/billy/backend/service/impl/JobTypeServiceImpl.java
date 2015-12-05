package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.JobTypeDAO;
import billy.backend.model.JobType;
import billy.backend.service.JobTypeService;

public class JobTypeServiceImpl implements JobTypeService {

	private JobTypeDAO jobTypeDAO;

	public JobTypeDAO getJobTypeDAO() {
		return jobTypeDAO;
	}

	public void setJobTypeDAO(JobTypeDAO jobTypeDAO) {
		this.jobTypeDAO = jobTypeDAO;
	}

	@Override
	public JobType getNewJobType() {
		return getJobTypeDAO().getNewJobType();
	}

	@Override
	public JobType getJobTypeByID(Long id) {
		return getJobTypeDAO().getJobTypeById(id);
	}

	@Override
	public List<JobType> getAllJobTypes() {
		return getJobTypeDAO().getAllJobTypes();
	}

	@Override
	public void saveOrUpdate(JobType entity) {
		getJobTypeDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(JobType entity) {
		getJobTypeDAO().delete(entity);
	}

	@Override
	public List<JobType> getJobTypesLikeNamaJobType(String string) {
		return getJobTypeDAO().getJobTypesLikeNamaJobType(string);
	}

	@Override
	public int getCountAllJobTypes() {
		return getJobTypeDAO().getCountAllJobTypes();
	}

}
