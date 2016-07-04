package billy.backend.service;

import java.util.List;

import billy.backend.model.JobType;

public interface JobTypeService {

  public void delete(JobType entity);

  public List<JobType> getAllJobTypes();

  public int getCountAllJobTypes();

  public JobType getJobTypeByID(Long id);

  public List<JobType> getJobTypesLikeNamaJobType(String string);

  public JobType getNewJobType();

  public void saveOrUpdate(JobType entity);

}
