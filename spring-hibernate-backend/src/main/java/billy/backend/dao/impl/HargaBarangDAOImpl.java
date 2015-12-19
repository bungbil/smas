package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;
import billy.backend.dao.HargaBarangDAO;
import billy.backend.model.Barang;
import billy.backend.model.HargaBarang;


@Repository
public class HargaBarangDAOImpl extends BillyBasisDAO<HargaBarang> implements HargaBarangDAO {

	@Override
	public HargaBarang getNewHargaBarang() {
		return new HargaBarang();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HargaBarang> getHargaBarangsByBarang(Barang barang) {
		
		List<HargaBarang> result;

		DetachedCriteria criteria = DetachedCriteria.forClass(HargaBarang.class);
		criteria.add(Restrictions.eq("barang", barang));

		result = getHibernateTemplate().findByCriteria(criteria);

		return result;

	}

	@Override
	public int getCountHargaBarangsByBarang(Barang barang) {
		DetachedCriteria criteria = DetachedCriteria.forClass(HargaBarang.class);
		criteria.add(Restrictions.eq("barang", barang));
		criteria.setProjection(Projections.rowCount());
		return DataAccessUtils.intResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public HargaBarang getHargaBarangById(long id) {
		return get(HargaBarang.class, id);
	}

	@Override
	public void deleteHargaBarangsByBarang(Barang barang) {
		List<HargaBarang> hargaBarang = getHargaBarangsByBarang(barang);
		if (hargaBarang != null) {
			deleteAll(hargaBarang);
		}
	}

	@Override
	public int getCountAllHargaBarangs() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from HargaBarang"));
	}

	@Override
	public void deleteHargaBarangById(long id) {
		HargaBarang hargaBarang = getHargaBarangById(id);
		if (hargaBarang != null) {
			delete(hargaBarang);
		}
	}

}
