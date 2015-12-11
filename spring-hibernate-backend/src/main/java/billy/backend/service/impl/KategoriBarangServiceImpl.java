package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.KategoriBarangDAO;
import billy.backend.model.KategoriBarang;
import billy.backend.service.KategoriBarangService;

public class KategoriBarangServiceImpl implements KategoriBarangService {

	private KategoriBarangDAO kategoriBarangDAO;

	public KategoriBarangDAO getKategoriBarangDAO() {
		return kategoriBarangDAO;
	}

	public void setKategoriBarangDAO(KategoriBarangDAO kategoriBarangDAO) {
		this.kategoriBarangDAO = kategoriBarangDAO;
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
	public void saveOrUpdate(KategoriBarang entity) {
		getKategoriBarangDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(KategoriBarang entity) {
		getKategoriBarangDAO().delete(entity);
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