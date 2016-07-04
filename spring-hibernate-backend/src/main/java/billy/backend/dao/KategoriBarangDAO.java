package billy.backend.dao;

import java.util.List;

import billy.backend.model.KategoriBarang;

public interface KategoriBarangDAO {

  public void delete(KategoriBarang entity);

  public void deleteKategoriBarangById(long id);

  public List<KategoriBarang> getAllKategoriBarangs();

  public int getCountAllKategoriBarangs();

  public KategoriBarang getKategoriBarangById(Long id);

  public KategoriBarang getKategoriBarangByKodeKategoriBarang(String string);

  public List<KategoriBarang> getKategoriBarangsLikeDeskripsiKategoriBarang(String string);

  public List<KategoriBarang> getKategoriBarangsLikeKodeKategoriBarang(String string);

  public KategoriBarang getNewKategoriBarang();

  public void save(KategoriBarang entity);

  public void saveOrUpdate(KategoriBarang entity);

}
