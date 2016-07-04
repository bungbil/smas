package billy.backend.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import billy.backend.dao.JobTypeDAO;
import billy.backend.model.JobType;
import billy.backend.service.JobTypeService;

public class JobTypeServiceImpl implements JobTypeService {

  private JobTypeDAO jobTypeDAO;

  @Override
  public void delete(JobType entity) {
    getJobTypeDAO().delete(entity);
  }

  @Override
  public List<JobType> getAllJobTypes() {
    List<JobType> list = getJobTypeDAO().getAllJobTypes();
    Collections.sort(list, new Comparator<JobType>() {
      @Override
      public int compare(JobType obj1, JobType obj2) {
        return obj1.getNamaJobType().compareTo(obj2.getNamaJobType());
      }
    });
    return list;
  }

  @Override
  public int getCountAllJobTypes() {
    return getJobTypeDAO().getCountAllJobTypes();
  }

  @Override
  public JobType getJobTypeByID(Long id) {
    return getJobTypeDAO().getJobTypeById(id);
  }

  public JobTypeDAO getJobTypeDAO() {
    return jobTypeDAO;
  }

  @Override
  public List<JobType> getJobTypesLikeNamaJobType(String string) {
    return getJobTypeDAO().getJobTypesLikeNamaJobType(string);
  }

  @Override
  public JobType getNewJobType() {
    return getJobTypeDAO().getNewJobType();
  }

  @Override
  public void saveOrUpdate(JobType entity) {
    getJobTypeDAO().saveOrUpdate(entity);
  }

  public void setJobTypeDAO(JobTypeDAO jobTypeDAO) {
    this.jobTypeDAO = jobTypeDAO;
  }

}
