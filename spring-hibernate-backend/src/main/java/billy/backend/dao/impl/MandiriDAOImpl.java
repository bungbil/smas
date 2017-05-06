package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.MandiriDAO;
import billy.backend.model.Mandiri;

@Repository
public class MandiriDAOImpl extends BillyBasisDAO<Mandiri> implements MandiriDAO {

  @Override
  public void deleteMandiriById(long id) {
    Mandiri Mandiri = getMandiriById(id);
    if (Mandiri != null) {
      delete(Mandiri);
    }
  }

  @Override
  public List<Mandiri> getAllMandiris() {
    return getHibernateTemplate().loadAll(Mandiri.class);
  }

  @Override
  public int getCountAllMandiris() {
    return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Mandiri"));
  }

  @Override
  public Mandiri getMandiriById(Long id) {
    return get(Mandiri.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Mandiri getMandiriByKodeMandiri(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Mandiri.class);
    criteria.add(Restrictions.eq("kodeMandiri", string));

    return (Mandiri) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Mandiri> getMandirisLikeDeskripsiMandiri(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Mandiri.class);
    criteria.add(Restrictions.ilike("deskripsiMandiri", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Mandiri> getMandirisLikeKodeMandiri(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Mandiri.class);
    criteria.add(Restrictions.ilike("kodeMandiri", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @Override
  public Mandiri getNewMandiri() {
    return new Mandiri();
  }

}
