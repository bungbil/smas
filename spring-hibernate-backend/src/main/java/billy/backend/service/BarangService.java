package billy.backend.service;

import java.util.List;

import de.forsthaus.backend.model.Order;

import billy.backend.model.Barang;
import billy.backend.model.HargaBarang;
public interface BarangService {

	public Barang getNewBarang();

	public int getCountAllBarangs();

	public Barang getBarangByID(Long id);

	public List<Barang> getAllBarangs();

	public List<Barang> getBarangsLikeKodeBarang(String string);
	
	public List<Barang> getBarangsLikeNamaBarang(String string);

	public void saveOrUpdate(Barang entity);

	public void delete(Barang entity);
	public void initialize(Barang entity);
	
	public HargaBarang getNewHargaBarang();
	
	public List<HargaBarang> getHargaBarangsByBarang(Barang barang);
	
	public int getCountHargaBarangsByBarang(Barang barang);

	public int getCountAllHargaBarangs();
	
	public void saveOrUpdate(HargaBarang entity);

	public void delete(HargaBarang entity);

	public void save(HargaBarang entity);

}
