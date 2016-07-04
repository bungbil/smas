package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.SatuanBarangDAO;
import billy.backend.model.SatuanBarang;

@Repository
public class SatuanBarangDAOImpl extends BillyBasisDAO<SatuanBarang> implements SatuanBarangDAO {

  @Override
  public void deleteSatuanBarangById(long id) {
    SatuanBarang SatuanBarang = getSatuanBarangById(id);
    if (SatuanBarang != null) {
      delete(SatuanBarang);
    }
  }

  @Override
  public List<SatuanBarang> getAllSatuanBarangs() {
    return getHibernateTemplate().loadAll(SatuanBarang.class);
  }

  @Override
  public int getCountAllSatuanBarangs() {
    return DataAccessUtils.intResult(getHibernateTemplate().find(
        "select count(*) from SatuanBarang"));
  }

  @Override
  public SatuanBarang getNewSatuanBarang() {
    return new SatuanBarang();
  }

  @Override
  public SatuanBarang getSatuanBarangById(Long id) {
    return get(SatuanBarang.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public SatuanBarang getSatuanBarangByKodeSatuanBarang(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(SatuanBarang.class);
    criteria.add(Restrictions.eq("kodeSatuanBarang", string));

    return (SatuanBarang) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(
        criteria));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<SatuanBarang> getSatuanBarangsLikeDeskripsiSatuanBarang(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(SatuanBarang.class);
    criteria.add(Restrictions.ilike("deskripsiSatuanBarang", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<SatuanBarang> getSatuanBarangsLikeKodeSatuanBarang(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(SatuanBarang.class);
    criteria.add(Restrictions.ilike("kodeSatuanBarang", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

}
