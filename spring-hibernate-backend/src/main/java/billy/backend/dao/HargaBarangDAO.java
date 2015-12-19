package billy.backend.dao;

import java.util.List;

import de.forsthaus.backend.model.Order;


import billy.backend.model.Barang;
import billy.backend.model.HargaBarang;

public interface HargaBarangDAO {

	public HargaBarang getNewHargaBarang();
	
	public List<HargaBarang> getHargaBarangsByBarang(Barang barang);
	public int getCountHargaBarangsByBarang(Barang barang);

	public int getCountAllHargaBarangs();

	public HargaBarang getHargaBarangById(long id);
	
	public void deleteHargaBarangsByBarang(Barang barang);
	public void deleteHargaBarangById(long id);

	public void saveOrUpdate(HargaBarang entity);

	public void delete(HargaBarang entity);

	public void save(HargaBarang entity);

}
