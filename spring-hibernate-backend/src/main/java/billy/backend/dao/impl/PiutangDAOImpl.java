package billy.backend.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.PiutangDAO;
import billy.backend.model.Karyawan;
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

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglJatuhTempo", startDate));
    criteria.add(Restrictions.le("tglJatuhTempo", endDate));
    criteria.add(Restrictions.eq("penjualan.divisi.id", obj.getId()));
    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglPembayaran", startDate));
    criteria.add(Restrictions.le("tglPembayaran", endDate));
    criteria.add(Restrictions.eq("penjualan.divisi.id", obj.getId()));
    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByKaryawanAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglJatuhTempo", startDate));
    criteria.add(Restrictions.le("tglJatuhTempo", endDate));
    // criteria.add(Restrictions.eq("sales1.id", obj.getId()));
    Criterion sales1 = Restrictions.eq("penjualan.sales1.id", obj.getId());
    Criterion sales2 = Restrictions.eq("penjualan.sales2.id", obj.getId());
    criteria.add(Restrictions.or(sales1, sales2));
    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByKaryawanAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglPembayaran", startDate));
    criteria.add(Restrictions.le("tglPembayaran", endDate));
    // criteria.add(Restrictions.eq("sales1.id", obj.getId()));
    Criterion sales1 = Restrictions.eq("penjualan.sales1.id", obj.getId());
    Criterion sales2 = Restrictions.eq("penjualan.sales2.id", obj.getId());
    criteria.add(Restrictions.or(sales1, sales2));
    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawa(Karyawan obj, Date startDate,
      Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglBawaKolektor", startDate));
    criteria.add(Restrictions.le("tglBawaKolektor", endDate));
    criteria.add(Restrictions.eq("kolektor.id", obj.getId()));
    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawaBelumBayar(Karyawan obj,
      Date startDate, Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglBawaKolektor", startDate));
    criteria.add(Restrictions.le("tglBawaKolektor", endDate));
    criteria.add(Restrictions.isNull("tglPembayaran"));
    criteria.add(Restrictions.eq("kolektor.id", obj.getId()));
    return getHibernateTemplate().findByCriteria(criteria);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.ge("tglPembayaran", startDate));
    criteria.add(Restrictions.le("tglPembayaran", endDate));
    criteria.add(Restrictions.eq("kolektor.id", obj.getId()));
    return getHibernateTemplate().findByCriteria(criteria);
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
  public Piutang getPiutangByNoFaktur(String data) {
    List<Piutang> result;

    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.eq("noFaktur", data));
    criteria.add(Restrictions.eq("aktif", true));

    result = getHibernateTemplate().findByCriteria(criteria);
    if (result.size() > 0) {
      Piutang piutang = result.get(0);
      return piutang;
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Piutang getPiutangByNoKuitansi(String data) {
    List<Piutang> result;

    DetachedCriteria criteria = DetachedCriteria.forClass(Piutang.class);
    criteria.add(Restrictions.eq("noKuitansi", data));

    result = getHibernateTemplate().findByCriteria(criteria);
    if (result.size() > 0) {
      Piutang piutang = result.get(0);
      return piutang;
    }

    return null;
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
