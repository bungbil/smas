package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;

import billy.backend.dao.BonusTransportDAO;
import billy.backend.model.BonusTransport;

@Repository
public class BonusTransportDAOImpl extends BillyBasisDAO<BonusTransport> implements BonusTransportDAO {

	@Override
	public BonusTransport getNewBonusTransport() {
		return new BonusTransport();
	}

	@Override
	public BonusTransport getBonusTransportById(Long id) {
		return get(BonusTransport.class, id);
	}

	@SuppressWarnings("unchecked")
	public BonusTransport getBonusTransportByDeskripsiBonusTransport(String string) {
		DetachedCriteria criteria = DetachedCriteria.forClass(BonusTransport.class);
		criteria.add(Restrictions.eq("deskripsiBonusTransport", string));

		return (BonusTransport) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<BonusTransport> getAllBonusTransports() {
		return getHibernateTemplate().loadAll(BonusTransport.class);
	}

	@Override
	public void deleteBonusTransportById(long id) {
		BonusTransport BonusTransport = getBonusTransportById(id);
		if (BonusTransport != null) {
			delete(BonusTransport);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(BonusTransport.class);
		criteria.add(Restrictions.ilike("deskripsiBonusTransport", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public int getCountAllBonusTransports() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from BonusTransport"));
	}

}