package billy.backend.dao;

import java.util.List;

import billy.backend.model.SatuanBarang;

public interface SatuanBarangDAO {

  public void delete(SatuanBarang entity);

  public void deleteSatuanBarangById(long id);

  public List<SatuanBarang> getAllSatuanBarangs();

  public int getCountAllSatuanBarangs();

  public SatuanBarang getNewSatuanBarang();

  public SatuanBarang getSatuanBarangById(Long id);

  public SatuanBarang getSatuanBarangByKodeSatuanBarang(String string);

  public List<SatuanBarang> getSatuanBarangsLikeDeskripsiSatuanBarang(String string);

  public List<SatuanBarang> getSatuanBarangsLikeKodeSatuanBarang(String string);

  public void save(SatuanBarang entity);

  public void saveOrUpdate(SatuanBarang entity);

}
