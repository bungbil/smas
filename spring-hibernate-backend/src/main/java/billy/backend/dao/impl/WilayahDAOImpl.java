package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.WilayahDAO;
import billy.backend.model.Wilayah;

@Repository
public class WilayahDAOImpl extends BillyBasisDAO<Wilayah> implements WilayahDAO {

  @Override
  public void deleteWilayahById(long id) {
    Wilayah Wilayah = getWilayahById(id);
    if (Wilayah != null) {
      delete(Wilayah);
    }
  }

  @Override
  public List<Wilayah> getAllWilayahs() {
    return getHibernateTemplate().loadAll(Wilayah.class);
  }

  @Override
  public int getCountAllWilayahs() {
    return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Wilayah"));
  }

  @Override
  public Wilayah getNewWilayah() {
    return new Wilayah();
  }

  @Override
  public Wilayah getWilayahById(Long id) {
    return get(Wilayah.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Wilayah getWilayahByKodeWilayah(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Wilayah.class);
    criteria.add(Restrictions.eq("kodeWilayah", string));

    return (Wilayah) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Wilayah> getWilayahsLikeKodeWilayah(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Wilayah.class);
    criteria.add(Restrictions.ilike("kodeWilayah", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Wilayah> getWilayahsLikeNamaWilayah(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Wilayah.class);
    criteria.add(Restrictions.ilike("namaWilayah", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

}
