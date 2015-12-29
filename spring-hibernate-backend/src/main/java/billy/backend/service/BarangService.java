package billy.backend.service;

import java.util.List;

import billy.backend.model.Barang;

public interface BarangService {

	public Barang getNewBarang();

	public int getCountAllBarangs();

	public Barang getBarangByID(Long id);

	public List<Barang> getAllBarangs();

	public List<Barang> getBarangsLikeKodeBarang(String string);
	
	public List<Barang> getBarangsLikeNamaBarang(String string);

	public void saveOrUpdate(Barang entity);

	public void delete(Barang entity);
	

}
