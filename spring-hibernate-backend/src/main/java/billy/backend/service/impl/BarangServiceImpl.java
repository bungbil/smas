package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.BarangDAO;
import billy.backend.model.Barang;
import billy.backend.service.BarangService;

public class BarangServiceImpl implements BarangService {

	private BarangDAO barangDAO;

	public BarangDAO getBarangDAO() {
		return barangDAO;
	}

	public void setBarangDAO(BarangDAO barangDAO) {
		this.barangDAO = barangDAO;
	}

	@Override
	public Barang getNewBarang() {
		return getBarangDAO().getNewBarang();
	}

	@Override
	public Barang getBarangByID(Long id) {
		return getBarangDAO().getBarangById(id);
	}
	@Override
	public Barang getBarangByKodeBarang(String string) {
		return getBarangDAO().getBarangByKodeBarang(string);
	}

	@Override
	public List<Barang> getAllBarangs() {
		return getBarangDAO().getAllBarangs();
	}

	@Override
	public void saveOrUpdate(Barang entity) {
		getBarangDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(Barang entity) {
		getBarangDAO().delete(entity);
	}

	@Override
	public List<Barang> getBarangsLikeKodeBarang(String string) {
		return getBarangDAO().getBarangsLikeKodeBarang(string);
	}

	@Override
	public List<Barang> getBarangsLikeNamaBarang(String string) {
		return getBarangDAO().getBarangsLikeNamaBarang(string);
	}

	@Override
	public int getCountAllBarangs() {
		return getBarangDAO().getCountAllBarangs();
	}
}
