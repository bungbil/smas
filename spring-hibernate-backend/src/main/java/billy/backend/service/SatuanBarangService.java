package billy.backend.service;

import java.util.List;

import billy.backend.model.SatuanBarang;

public interface SatuanBarangService {

  public void delete(SatuanBarang entity);

  public List<SatuanBarang> getAllSatuanBarangs();

  public int getCountAllSatuanBarangs();

  public SatuanBarang getNewSatuanBarang();

  public SatuanBarang getSatuanBarangByID(Long id);

  public List<SatuanBarang> getSatuanBarangsLikeDeskripsiSatuanBarang(String string);

  public List<SatuanBarang> getSatuanBarangsLikeKodeSatuanBarang(String string);

  public void saveOrUpdate(SatuanBarang entity);

}
