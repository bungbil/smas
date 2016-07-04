package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.StatusDAO;
import billy.backend.model.Status;

@Repository
public class StatusDAOImpl extends BillyBasisDAO<Status> implements StatusDAO {

  @Override
  public void deleteStatusById(long id) {
    Status Status = getStatusById(id);
    if (Status != null) {
      delete(Status);
    }
  }

  @Override
  public List<Status> getAllStatuss() {
    return getHibernateTemplate().loadAll(Status.class);
  }

  @Override
  public int getCountAllStatuss() {
    return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Status"));
  }

  @Override
  public Status getNewStatus() {
    return new Status();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Status getStatusByDeskripsiStatus(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Status.class);
    criteria.add(Restrictions.eq("deskripsiStatus", string));

    return (Status) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @Override
  public Status getStatusById(Long id) {
    return get(Status.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Status> getStatussLikeDeskripsiStatus(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Status.class);
    criteria.add(Restrictions.ilike("deskripsiStatus", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

}
