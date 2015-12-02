package billy.backend.service;

import java.util.List;

import billy.backend.model.JobType;
public interface JobTypeService {

	public JobType getNewJobType();

	public int getCountAllJobTypes();

	public JobType getJobTypeByID(Long id);

	public List<JobType> getAllJobTypes();
	
	public List<JobType> getJobTypesLikeNamaJobType(String string);

	public void saveOrUpdate(JobType ofice);

	public void delete(JobType JobType);

}
