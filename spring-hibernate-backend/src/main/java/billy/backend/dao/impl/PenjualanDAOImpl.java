package billy.backend.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import de.forsthaus.backend.model.MyCalendarEvent;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.PenjualanDAO;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
@Repository
public class PenjualanDAOImpl extends BillyBasisDAO<Penjualan> implements PenjualanDAO {

	@Override
	public Penjualan getNewPenjualan() {
		return new Penjualan();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Penjualan getPenjualanByNoFaktur(String string) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Penjualan.class);
		criteria.add(Restrictions.eq("noFaktur", string));

		return (Penjualan) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<Penjualan> getAllPenjualans() {
		return getHibernateTemplate().loadAll(Penjualan.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal getPenjualanSum(Penjualan obj) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PenjualanDetail.class);
		criteria.add(Restrictions.eq("obj", obj));
		criteria.setProjection(Projections.sum("total"));

		BigDecimal sumResult = (BigDecimal) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));

		return sumResult;
	}

	@Override
	public int getCountAllPenjualans() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Penjualan"));
	}
	
	public int getCountAllPenjualansByDivisi(Karyawan obj,Date startDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Penjualan.class);
		criteria.add(Restrictions.ge("tglPenjualan", startDate));
		criteria.add(Restrictions.le("tglPenjualan", endDate));
		criteria.add(Restrictions.eq("divisi.id", obj.getId()));
		return getHibernateTemplate().findByCriteria(criteria).size();
	}
	

	@Override
	public void initialize(Penjualan obj) {
		super.initialize(obj);
	}

	@Override
	public Penjualan getPenjualanById(long id) {
		return get(Penjualan.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Penjualan> getListNeedApprovalPenjualansByListNoFaktur(
			List<String> listNoFaktur) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Penjualan.class);
		criteria.add(Restrictions.in("noFaktur", listNoFaktur.toArray()));
		criteria.add(Restrictions.eq("needApproval", true));
		return getHibernateTemplate().findByCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Penjualan> getAllPenjualansByListNoFaktur(
			List<String> listNoFaktur) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Penjualan.class);
		criteria.add(Restrictions.in("noFaktur", listNoFaktur.toArray()));
		return getHibernateTemplate().findByCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Penjualan> getAllPenjualansByDivisiAndRangeDate(
			Karyawan obj,Date startDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Penjualan.class);
		criteria.add(Restrictions.ge("tglPenjualan", startDate));
		criteria.add(Restrictions.le("tglPenjualan", endDate));
		criteria.add(Restrictions.eq("divisi.id", obj.getId()));		
		return getHibernateTemplate().findByCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Penjualan> getAllPenjualansByKaryawanAndRangeDate(
			Karyawan obj,Date startDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Penjualan.class);
		criteria.add(Restrictions.ge("tglPenjualan", startDate));
		criteria.add(Restrictions.le("tglPenjualan", endDate));		
		criteria.add(Restrictions.eq("id", obj.getId()));
		return getHibernateTemplate().findByCriteria(criteria);
	}
}
