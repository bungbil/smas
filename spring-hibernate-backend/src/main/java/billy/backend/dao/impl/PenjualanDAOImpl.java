package billy.backend.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.PenjualanDAO;
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

	@Override
	public void initialize(Penjualan obj) {
		super.initialize(obj);
	}

	@Override
	public Penjualan getPenjualanById(long id) {
		return get(Penjualan.class, id);
	}
}
