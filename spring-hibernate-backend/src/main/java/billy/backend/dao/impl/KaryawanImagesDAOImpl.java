package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.KaryawanImagesDAO;
import billy.backend.model.Karyawan;
import billy.backend.model.KaryawanImages;

@Repository
public class KaryawanImagesDAOImpl extends BillyBasisDAO<KaryawanImages> implements
    KaryawanImagesDAO {

  @Override
  public void deleteKaryawanImagesById(long id) {
    KaryawanImages Karyawan = getKaryawanImagesById(id);
    if (Karyawan != null) {
      delete(Karyawan);
    }
  }

  @Override
  public List<KaryawanImages> getAllKaryawanImages() {
    return getHibernateTemplate().loadAll(KaryawanImages.class);
  }

  @Override
  public int getCountAllKaryawanImages() {
    return DataAccessUtils.intResult(getHibernateTemplate().find(
        "select count(*) from KaryawanImages"));
  }

  @Override
  public KaryawanImages getKaryawanImagesById(Long id) {
    return get(KaryawanImages.class, id);
  }


  @Override
  @SuppressWarnings("unchecked")
  public KaryawanImages getKaryawanImagesByKaryawan(Karyawan entity) {
    DetachedCriteria criteria = DetachedCriteria.forClass(KaryawanImages.class);
    criteria.add(Restrictions.eq("karyawan", entity));

    return (KaryawanImages) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(
        criteria));
  }


  @Override
  public KaryawanImages getNewKaryawanImages() {
    return new KaryawanImages();
  }

}
