package billy.backend.service;

import java.util.List;

import billy.backend.model.Wilayah;

public interface WilayahService {

  public void delete(Wilayah entity);

  public List<Wilayah> getAllWilayahs();

  public int getCountAllWilayahs();

  public Wilayah getNewWilayah();

  public Wilayah getWilayahByID(Long id);

  public Wilayah getWilayahByKodeWilayah(String string);

  public List<Wilayah> getWilayahsLikeKodeWilayah(String string);

  public List<Wilayah> getWilayahsLikeNamaWilayah(String string);

  public void saveOrUpdate(Wilayah entity);

}
