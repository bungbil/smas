package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.SatuanBarangDAO;
import billy.backend.model.SatuanBarang;
import billy.backend.service.SatuanBarangService;

public class SatuanBarangServiceImpl implements SatuanBarangService {

  private SatuanBarangDAO satuanBarangDAO;

  @Override
  public void delete(SatuanBarang entity) {
    getSatuanBarangDAO().delete(entity);
  }

  @Override
  public List<SatuanBarang> getAllSatuanBarangs() {
    return getSatuanBarangDAO().getAllSatuanBarangs();
  }

  @Override
  public int getCountAllSatuanBarangs() {
    return getSatuanBarangDAO().getCountAllSatuanBarangs();
  }

  @Override
  public SatuanBarang getNewSatuanBarang() {
    return getSatuanBarangDAO().getNewSatuanBarang();
  }

  @Override
  public SatuanBarang getSatuanBarangByID(Long id) {
    return getSatuanBarangDAO().getSatuanBarangById(id);
  }

  public SatuanBarangDAO getSatuanBarangDAO() {
    return satuanBarangDAO;
  }

  @Override
  public List<SatuanBarang> getSatuanBarangsLikeDeskripsiSatuanBarang(String string) {
    return getSatuanBarangDAO().getSatuanBarangsLikeDeskripsiSatuanBarang(string);
  }

  @Override
  public List<SatuanBarang> getSatuanBarangsLikeKodeSatuanBarang(String string) {
    return getSatuanBarangDAO().getSatuanBarangsLikeKodeSatuanBarang(string);
  }

  @Override
  public void saveOrUpdate(SatuanBarang entity) {
    getSatuanBarangDAO().saveOrUpdate(entity);
  }

  public void setSatuanBarangDAO(SatuanBarangDAO satuanBarangDAO) {
    this.satuanBarangDAO = satuanBarangDAO;
  }

}
