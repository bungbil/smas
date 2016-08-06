package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.KaryawanDAO;
import billy.backend.model.Karyawan;

@Repository
public class KaryawanDAOImpl extends BillyBasisDAO<Karyawan> implements KaryawanDAO {

  @Override
  public void deleteKaryawanById(long id) {
    Karyawan Karyawan = getKaryawanById(id);
    if (Karyawan != null) {
      delete(Karyawan);
    }
  }

  @Override
  public List<Karyawan> getAllKaryawans() {
    return getHibernateTemplate().loadAll(Karyawan.class);
  }

  @Override
  public int getCountAllKaryawans() {
    return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Karyawan"));
  }

  @Override
  public Karyawan getKaryawanById(Long id) {
    return get(Karyawan.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Karyawan getKaryawanByKodeKaryawan(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.eq("kodeKaryawan", string.toUpperCase()));

    return (Karyawan) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Karyawan getKaryawanByKtp(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.eq("ktp", string));

    return (Karyawan) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansByJobTypeId(Long id) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.eq("jobType.id", id));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansByNamaJobType(String string) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.eq("jobType.namaJobType", string));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansBySupervisorId(Long id) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.eq("supervisorDivisi.id", id));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansLikeKodeKaryawan(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.ilike("kodeKaryawan", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansLikeKtp(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.ilike("ktp", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansLikeNamaKtp(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.ilike("namaKtp", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Karyawan> getKaryawansLikeNamaPanggilan(String string) {

    DetachedCriteria criteria = DetachedCriteria.forClass(Karyawan.class);
    criteria.add(Restrictions.ilike("namaPanggilan", string, MatchMode.ANYWHERE));

    return getHibernateTemplate().findByCriteria(criteria);
  }

  @Override
  public Karyawan getNewKaryawan() {
    return new Karyawan();
  }

}
