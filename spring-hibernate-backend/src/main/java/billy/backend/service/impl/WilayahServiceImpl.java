package billy.backend.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import billy.backend.dao.WilayahDAO;
import billy.backend.model.Wilayah;
import billy.backend.service.WilayahService;

public class WilayahServiceImpl implements WilayahService {

  private WilayahDAO wilayahDAO;

  @Override
  public void delete(Wilayah entity) {
    getWilayahDAO().delete(entity);
  }

  @Override
  public List<Wilayah> getAllWilayahs() {
    List<Wilayah> list = getWilayahDAO().getAllWilayahs();
    Collections.sort(list, new Comparator<Wilayah>() {
      @Override
      public int compare(Wilayah obj1, Wilayah obj2) {
        return obj1.getNamaWilayah().compareTo(obj2.getNamaWilayah());
      }
    });
    return list;
  }

  @Override
  public int getCountAllWilayahs() {
    return getWilayahDAO().getCountAllWilayahs();
  }

  @Override
  public Wilayah getNewWilayah() {
    return getWilayahDAO().getNewWilayah();
  }

  @Override
  public Wilayah getWilayahByID(Long id) {
    return getWilayahDAO().getWilayahById(id);
  }

  @Override
  public Wilayah getWilayahByKodeWilayah(String string) {
    return getWilayahDAO().getWilayahByKodeWilayah(string);
  }

  public WilayahDAO getWilayahDAO() {
    return wilayahDAO;
  }

  @Override
  public List<Wilayah> getWilayahsLikeKodeWilayah(String string) {
    return getWilayahDAO().getWilayahsLikeKodeWilayah(string);
  }

  @Override
  public List<Wilayah> getWilayahsLikeNamaWilayah(String string) {
    return getWilayahDAO().getWilayahsLikeNamaWilayah(string);
  }

  @Override
  public void saveOrUpdate(Wilayah entity) {
    getWilayahDAO().saveOrUpdate(entity);
  }

  public void setWilayahDAO(WilayahDAO wilayahDAO) {
    this.wilayahDAO = wilayahDAO;
  }

}
