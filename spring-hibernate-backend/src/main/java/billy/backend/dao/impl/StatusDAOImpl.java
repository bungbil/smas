package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;

import billy.backend.dao.StatusDAO;
import billy.backend.model.Status;

@Repository
public class StatusDAOImpl extends BillyBasisDAO<Status> implements StatusDAO {

	@Override
	public Status getNewStatus() {
		return new Status();
	}

	@Override
	public Status getStatusById(Long id) {
		return get(Status.class, id);
	}

	@SuppressWarnings("unchecked")
	public Status getStatusByKodeStatus(String string) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Status.class);
		criteria.add(Restrictions.eq("kodeStatus", string));

		return (Status) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<Status> getAllStatuss() {
		return getHibernateTemplate().loadAll(Status.class);
	}

	@Override
	public void deleteStatusById(long id) {
		Status Status = getStatusById(id);
		if (Status != null) {
			delete(Status);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Status> getStatussLikeKodeStatus(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Status.class);
		criteria.add(Restrictions.ilike("kodeStatus", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Status> getStatussLikeDeskripsiStatus(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Status.class);
		criteria.add(Restrictions.ilike("deskripsiStatus", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public int getCountAllStatuss() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Status"));
	}

}