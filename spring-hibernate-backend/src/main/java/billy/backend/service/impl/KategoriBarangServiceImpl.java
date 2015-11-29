package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.KategoriBarangDAO;
import billy.backend.model.KategoriBarang;
import billy.backend.service.KategoriBarangService;

public class KategoriBarangServiceImpl implements KategoriBarangService {

	private KategoriBarangDAO KategoriBarangDAO;

	public KategoriBarangDAO getKategoriBarangDAO() {
		return KategoriBarangDAO;
	}

	public void setKategoriBarangDAO(KategoriBarangDAO KategoriBarangDAO) {
		this.KategoriBarangDAO = KategoriBarangDAO;
	}

	@Override
	public KategoriBarang getNewKategoriBarang() {
		return getKategoriBarangDAO().getNewKategoriBarang();
	}

	@Override
	public KategoriBarang getKategoriBarangByID(Long id) {
		return getKategoriBarangDAO().getKategoriBarangById(id);
	}

	@Override
	public List<KategoriBarang> getAllKategoriBarangs() {
		return getKategoriBarangDAO().getAllKategoriBarangs();
	}

	@Override
	public void saveOrUpdate(KategoriBarang KategoriBarang) {
		getKategoriBarangDAO().saveOrUpdate(KategoriBarang);
	}

	@Override
	public void delete(KategoriBarang KategoriBarang) {
		getKategoriBarangDAO().delete(KategoriBarang);
	}

	@Override
	public List<KategoriBarang> getKategoriBarangsLikeKodeKategoriBarang(String string) {
		return getKategoriBarangDAO().getKategoriBarangsLikeKodeKategoriBarang(string);
	}

	@Override
	public List<KategoriBarang> getKategoriBarangsLikeDeskripsiKategoriBarang(String string) {
		return getKategoriBarangDAO().getKategoriBarangsLikeDeskripsiKategoriBarang(string);
	}

	@Override
	public int getCountAllKategoriBarangs() {
		return getKategoriBarangDAO().getCountAllKategoriBarangs();
	}

}
