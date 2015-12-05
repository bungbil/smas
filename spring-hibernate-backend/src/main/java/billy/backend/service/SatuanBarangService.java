package billy.backend.service;

import java.util.List;

import billy.backend.model.SatuanBarang;
public interface SatuanBarangService {

	public SatuanBarang getNewSatuanBarang();

	public int getCountAllSatuanBarangs();

	public SatuanBarang getSatuanBarangByID(Long id);

	public List<SatuanBarang> getAllSatuanBarangs();

	public List<SatuanBarang> getSatuanBarangsLikeKodeSatuanBarang(String string);
	
	public List<SatuanBarang> getSatuanBarangsLikeDeskripsiSatuanBarang(String string);

	public void saveOrUpdate(SatuanBarang entity);

	public void delete(SatuanBarang entity);

}
