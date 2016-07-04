package billy.backend.service;

import java.util.List;

import billy.backend.model.KategoriBarang;

public interface KategoriBarangService {

  public void delete(KategoriBarang entity);

  public List<KategoriBarang> getAllKategoriBarangs();

  public int getCountAllKategoriBarangs();

  public KategoriBarang getKategoriBarangByID(Long id);

  public List<KategoriBarang> getKategoriBarangsLikeDeskripsiKategoriBarang(String string);

  public List<KategoriBarang> getKategoriBarangsLikeKodeKategoriBarang(String string);

  public KategoriBarang getNewKategoriBarang();

  public void saveOrUpdate(KategoriBarang entity);

}
