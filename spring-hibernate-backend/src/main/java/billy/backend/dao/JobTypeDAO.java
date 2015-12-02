package billy.backend.dao;

import java.util.List;

import billy.backend.model.JobType;

public interface JobTypeDAO {

	public JobType getNewJobType();

	public JobType getJobTypeById(Long id);

	public JobType getJobTypeByNamaJobType(String string);

	public List<JobType> getAllJobTypes();

	public int getCountAllJobTypes();

	public List<JobType> getJobTypesLikeNamaJobType(String string);	

	public void deleteJobTypeById(long id);

	public void saveOrUpdate(JobType entity);

	public void delete(JobType entity);

	public void save(JobType entity);

}
