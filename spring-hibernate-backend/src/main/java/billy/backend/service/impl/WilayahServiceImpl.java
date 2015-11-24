package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.WilayahDAO;
import billy.backend.model.Wilayah;
import billy.backend.service.WilayahService;

public class WilayahServiceImpl implements WilayahService {

	private WilayahDAO WilayahDAO;

	public WilayahDAO getWilayahDAO() {
		return WilayahDAO;
	}

	public void setWilayahDAO(WilayahDAO WilayahDAO) {
		this.WilayahDAO = WilayahDAO;
	}

	@Override
	public Wilayah getNewWilayah() {
		return getWilayahDAO().getNewWilayah();
	}

	@Override
	public Wilayah getWilayahByID(Long id) {
		return getWilayahDAO().getWilayahById(id);
	}

	@Override
	public List<Wilayah> getAllWilayahs() {
		return getWilayahDAO().getAllWilayahs();
	}

	@Override
	public void saveOrUpdate(Wilayah Wilayah) {
		getWilayahDAO().saveOrUpdate(Wilayah);
	}

	@Override
	public void delete(Wilayah Wilayah) {
		getWilayahDAO().delete(Wilayah);
	}

	@Override
	public List<Wilayah> getWilayahsLikeKodeWilayah(String string) {
		return getWilayahDAO().getWilayahsLikeKodeWilayah(string);
	}

	@Override
	public List<Wilayah> getWilayahsLikeNamaWilayah(String string) {
		return getWilayahDAO().getWilayahsLikeNamaWilayah(string);
	}

	@Override
	public int getCountAllWilayahs() {
		return getWilayahDAO().getCountAllWilayahs();
	}

}
