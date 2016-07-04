package billy.backend.dao;

import java.util.List;

import billy.backend.model.JobType;

public interface JobTypeDAO {

  public void delete(JobType entity);

  public void deleteJobTypeById(long id);

  public List<JobType> getAllJobTypes();

  public int getCountAllJobTypes();

  public JobType getJobTypeById(Long id);

  public JobType getJobTypeByNamaJobType(String string);

  public List<JobType> getJobTypesLikeNamaJobType(String string);

  public JobType getNewJobType();

  public void save(JobType entity);

  public void saveOrUpdate(JobType entity);

}
