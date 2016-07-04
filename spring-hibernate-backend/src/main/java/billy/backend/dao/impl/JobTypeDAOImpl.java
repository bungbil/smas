package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.JobTypeDAO;
import billy.backend.model.JobType;

@Repository
public class JobTypeDAOImpl extends BillyBasisDAO<JobType> implements JobTypeDAO {

  @Override
  public void deleteJobTypeById(long id) {
    JobType JobType = getJobTypeById(id);
    if (JobType != null) {
      delete(JobType);
    }
  }

  @Override
  public List<JobType> getAllJobTypes() {
    return getHibernateTemplate().loadAll(JobType.class);
  }

  @Override
  public int getCountAllJobTypes() {
    return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from JobType"));
  }

  @Override
  public JobType getJobTypeById(Long id) {
    return get(JobType.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public JobType getJobTypeByNamaJobType(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(JobType.class);
    criteria.add(Restrictions.eq("namaJobType", string));

    return (JobType) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
  }


  @SuppressWarnings("unchecked")
  @Override
  public List<JobType> getJobTypesLikeNamaJobType(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(JobType.class);
    criteria.add(Restrictions.ilike("namaJobType", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @Override
  public JobType getNewJobType() {
    return new JobType();
  }

}
