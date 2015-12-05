package billy.backend.service;

import java.util.List;

import billy.backend.model.KategoriBarang;
public interface KategoriBarangService {

	public KategoriBarang getNewKategoriBarang();

	public int getCountAllKategoriBarangs();

	public KategoriBarang getKategoriBarangByID(Long id);

	public List<KategoriBarang> getAllKategoriBarangs();

	public List<KategoriBarang> getKategoriBarangsLikeKodeKategoriBarang(String string);
	
	public List<KategoriBarang> getKategoriBarangsLikeDeskripsiKategoriBarang(String string);

	public void saveOrUpdate(KategoriBarang entity);

	public void delete(KategoriBarang entity);

}
