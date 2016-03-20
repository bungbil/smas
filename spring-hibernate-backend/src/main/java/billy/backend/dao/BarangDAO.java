package billy.backend.dao;

import java.util.List;

import billy.backend.model.Barang;

public interface BarangDAO {

	public Barang getNewBarang();

	public Barang getBarangById(Long id);

	public Barang getBarangByKodeBarang(String string);

	public List<Barang> getAllBarangs();
	public List<Barang> getAllBarangsByWilayahId(Long id);
	public int getCountAllBarangs();

	public List<Barang> getBarangsLikeKodeBarang(String string);

	public List<Barang> getBarangsLikeNamaBarang(String string);	

	public void deleteBarangById(long id);

	public void saveOrUpdate(Barang entity);

	public void delete(Barang entity);

	public void save(Barang entity);
	
	public void refresh(Barang entity);

	public void initialize(Barang entity);

}
