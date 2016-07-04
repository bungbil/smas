package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.PiutangDAO;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;


@Repository
public class PiutangDAOImpl extends BillyBasisDAO<Piutang> implements PiutangDAO {

  @Override
  public void deletePiutangsByPenjualan(Penjualan entity) {
    List<Piutang> piutang = getPiutangsByPenjualan(entity);
    if (piutang != null) {
      deleteAll(piutang);
    }
  }

  @Override
  public int getCountAllPiutangs() {
    return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Piutang"));
  }

  @Override
  public int getCountPiutangsByPenjualan(Penjualan entity) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.eq("penjualan", entity));
    criteria.setProjection(Projections.rowCount());
    return DataAccessUtils.intResult(getHibernateTemplate().findByCriteria(criteria));
  }

  @Override
  public Piutang getNewPiutang() {
    return new Piutang();
  }

  @Override
  public Piutang getPiutangById(long id) {
    return get(Piutang.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getPiutangsByPenjualan(Penjualan entity) {
    List<Piutang> result;

    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.eq("penjualan", entity));

    result = getHibernateTemplate().findByCriteria(criteria);

    return result;

  }

}
