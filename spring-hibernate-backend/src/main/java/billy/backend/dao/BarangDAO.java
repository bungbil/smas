package billy.backend.dao;

import java.util.List;

import billy.backend.model.Barang;

public interface BarangDAO {

  public void delete(Barang entity);

  public void deleteBarangById(long id);

  public List<Barang> getAllBarangs();

  public List<Barang> getAllBarangsByWilayahId(Long id);

  public Barang getBarangById(Long id);

  public Barang getBarangByKodeBarang(String string);

  public Barang getBarangByKodeBarangAndWilayah(String string, Long id);

  public List<Barang> getBarangsLikeKodeBarang(String string);

  public List<Barang> getBarangsLikeNamaBarang(String string);

  public int getCountAllBarangs();

  public Barang getNewBarang();

  public void initialize(Barang entity);

  public void refresh(Barang entity);

  public void save(Barang entity);

  public void saveOrUpdate(Barang entity);

}
