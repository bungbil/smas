package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.PenjualanDetailDAO;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;


@Repository
public class PenjualanDetailDAOImpl extends BillyBasisDAO<PenjualanDetail> implements
    PenjualanDetailDAO {

  @Override
  public void deletePenjualanDetailsByPenjualan(Penjualan entity) {
    List<PenjualanDetail> penjualanDetail = getPenjualanDetailsByPenjualan(entity);
    if (penjualanDetail != null) {
      deleteAll(penjualanDetail);
    }
  }

  @Override
  public int getCountAllPenjualanDetails() {
    return DataAccessUtils.intResult(getHibernateTemplate().find(
        "select count(*) from PenjualanDetail"));
  }

  @Override
  public int getCountPenjualanDetailsByPenjualan(Penjualan entity) {
    DetachedCriteria criteria = DetachedCriteria.forClass(PenjualanDetail.class);
    criteria.add(Restrictions.eq("penjualan", entity));
    criteria.setProjection(Projections.rowCount());
    return DataAccessUtils.intResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @Override
  public PenjualanDetail getNewPenjualanDetail() {
    return new PenjualanDetail();
  }

  @Override
  public PenjualanDetail getPenjualanDetailById(long id) {
    return get(PenjualanDetail.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan entity) {
    List<PenjualanDetail> result;

    DetachedCriteria criteria = DetachedCriteria.forClass(PenjualanDetail.class);
    criteria.add(Restrictions.eq("penjualan", entity));

    result = getHibernateTemplate().findByCriteria(criteria);

    return result;

  }

}
