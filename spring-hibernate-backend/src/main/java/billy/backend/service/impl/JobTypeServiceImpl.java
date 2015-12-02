package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.JobTypeDAO;
import billy.backend.model.JobType;
import billy.backend.service.JobTypeService;

public class JobTypeServiceImpl implements JobTypeService {

	private JobTypeDAO JobTypeDAO;

	public JobTypeDAO getJobTypeDAO() {
		return JobTypeDAO;
	}

	public void setJobTypeDAO(JobTypeDAO JobTypeDAO) {
		this.JobTypeDAO = JobTypeDAO;
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
	public void saveOrUpdate(JobType JobType) {
		getJobTypeDAO().saveOrUpdate(JobType);
	}

	@Override
	public void delete(JobType JobType) {
		getJobTypeDAO().delete(JobType);
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
