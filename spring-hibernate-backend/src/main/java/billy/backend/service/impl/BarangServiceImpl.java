package billy.backend.service.impl;

import java.util.List;

import de.forsthaus.backend.dao.OrderpositionDAO;
import de.forsthaus.backend.model.Order;
import de.forsthaus.backend.model.Orderposition;

import billy.backend.dao.BarangDAO;
import billy.backend.dao.HargaBarangDAO;
import billy.backend.model.Barang;
import billy.backend.model.HargaBarang;
import billy.backend.service.BarangService;

public class BarangServiceImpl implements BarangService {

	private BarangDAO barangDAO;

	private HargaBarangDAO hargaBarangDAO;

	public HargaBarangDAO getHargaBarangDAO() {
		return hargaBarangDAO;
	}

	public void setHargaBarangDAO(HargaBarangDAO hargaBarangDAO) {
		this.hargaBarangDAO = hargaBarangDAO;
	}

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
	@Override
	public void initialize(Barang proxy) {
		getBarangDAO().initialize(proxy);

	}
	@Override
	public HargaBarang getNewHargaBarang() {
		return getHargaBarangDAO().getNewHargaBarang();
	}

	@Override
	public List<HargaBarang> getHargaBarangsByBarang(Barang barang) {
		getBarangDAO().refresh(barang);
		getBarangDAO().initialize(barang);
		
		List<HargaBarang> result = getHargaBarangDAO().getHargaBarangsByBarang(barang);

		return result;
	}

	@Override
	public int getCountHargaBarangsByBarang(Barang barang) {
		int result = getHargaBarangDAO().getCountHargaBarangsByBarang(barang);
		return result;
	}

	@Override
	public int getCountAllHargaBarangs() {
		// TODO Auto-generated method stub
		return getHargaBarangDAO().getCountAllHargaBarangs();
	}

	@Override
	public void saveOrUpdate(HargaBarang entity) {
		// TODO Auto-generated method stub
		getHargaBarangDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(HargaBarang entity) {
		// TODO Auto-generated method stub
		getHargaBarangDAO().delete(entity);
	}

	@Override
	public void save(HargaBarang entity) {
		// TODO Auto-generated method stub
		getHargaBarangDAO().save(entity);
	}

}
