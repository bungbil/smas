package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;

import billy.backend.dao.BarangDAO;
import billy.backend.model.Barang;
import billy.backend.model.Karyawan;

@Repository
public class BarangDAOImpl extends BillyBasisDAO<Barang> implements BarangDAO {

	@Override
	public Barang getNewBarang() {
		return new Barang();
	}

	@Override
	public Barang getBarangById(Long id) {
		return get(Barang.class, id);
	}

	@SuppressWarnings("unchecked")
	public Barang getBarangByKodeBarang(String string) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Barang.class);
		criteria.add(Restrictions.eq("kodeBarang", string));

		return (Barang) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<Barang> getAllBarangs() {
		return getHibernateTemplate().loadAll(Barang.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Barang> getAllBarangsByWilayahId(Long id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Barang.class);
		criteria.add(Restrictions.eq("wilayah.id", id));

		return getHibernateTemplate().findByCriteria(criteria);		
	}
	
	@Override
	public void deleteBarangById(long id) {
		Barang barang = getBarangById(id);
		if (barang != null) {
			delete(barang);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Barang> getBarangsLikeKodeBarang(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Barang.class);
		criteria.add(Restrictions.ilike("kodeBarang", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Barang> getBarangsLikeNamaBarang(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Barang.class);
		criteria.add(Restrictions.ilike("namaBarang", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public int getCountAllBarangs() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Barang"));
	}
	 
	@Override
	public void initialize(Barang barang) {
		super.initialize(barang);
	}

}
