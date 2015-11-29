package billy.backend.dao;

import java.util.List;

import billy.backend.model.KategoriBarang;

public interface KategoriBarangDAO {

	public KategoriBarang getNewKategoriBarang();

	public KategoriBarang getKategoriBarangById(Long id);

	public KategoriBarang getKategoriBarangByKodeKategoriBarang(String string);

	public List<KategoriBarang> getAllKategoriBarangs();

	public int getCountAllKategoriBarangs();

	public List<KategoriBarang> getKategoriBarangsLikeKodeKategoriBarang(String string);

	public List<KategoriBarang> getKategoriBarangsLikeDeskripsiKategoriBarang(String string);	

	public void deleteKategoriBarangById(long id);

	public void saveOrUpdate(KategoriBarang entity);

	public void delete(KategoriBarang entity);

	public void save(KategoriBarang entity);

}
