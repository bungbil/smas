package billy.backend.dao;

import java.util.List;

import billy.backend.model.SatuanBarang;

public interface SatuanBarangDAO {

	public SatuanBarang getNewSatuanBarang();

	public SatuanBarang getSatuanBarangById(Long id);

	public SatuanBarang getSatuanBarangByKodeSatuanBarang(String string);

	public List<SatuanBarang> getAllSatuanBarangs();

	public int getCountAllSatuanBarangs();

	public List<SatuanBarang> getSatuanBarangsLikeKodeSatuanBarang(String string);

	public List<SatuanBarang> getSatuanBarangsLikeDeskripsiSatuanBarang(String string);	

	public void deleteSatuanBarangById(long id);

	public void saveOrUpdate(SatuanBarang entity);

	public void delete(SatuanBarang entity);

	public void save(SatuanBarang entity);

}
