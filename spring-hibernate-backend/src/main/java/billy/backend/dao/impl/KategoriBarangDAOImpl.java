package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;

import billy.backend.dao.KategoriBarangDAO;
import billy.backend.model.KategoriBarang;

@Repository
public class KategoriBarangDAOImpl extends BillyBasisDAO<KategoriBarang> implements KategoriBarangDAO {

	@Override
	public KategoriBarang getNewKategoriBarang() {
		return new KategoriBarang();
	}

	@Override
	public KategoriBarang getKategoriBarangById(Long id) {
		return get(KategoriBarang.class, id);
	}

	@SuppressWarnings("unchecked")
	public KategoriBarang getKategoriBarangByKodeKategoriBarang(String string) {
		DetachedCriteria criteria = DetachedCriteria.forClass(KategoriBarang.class);
		criteria.add(Restrictions.eq("kodeKategoriBarang", string));

		return (KategoriBarang) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<KategoriBarang> getAllKategoriBarangs() {
		return getHibernateTemplate().loadAll(KategoriBarang.class);
	}

	@Override
	public void deleteKategoriBarangById(long id) {
		KategoriBarang KategoriBarang = getKategoriBarangById(id);
		if (KategoriBarang != null) {
			delete(KategoriBarang);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KategoriBarang> getKategoriBarangsLikeKodeKategoriBarang(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(KategoriBarang.class);
		criteria.add(Restrictions.ilike("kodeKategoriBarang", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KategoriBarang> getKategoriBarangsLikeDeskripsiKategoriBarang(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(KategoriBarang.class);
		criteria.add(Restrictions.ilike("deskripsiKategoriBarang", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public int getCountAllKategoriBarangs() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from KategoriBarang"));
	}

}
