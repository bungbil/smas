package billy.backend.service;

import java.math.BigDecimal;
import java.util.List;

import billy.backend.model.Barang;
import billy.backend.model.Wilayah;

public interface BarangService {

  public void delete(Barang entity);

  public List<Barang> getAllBarangs();

  public List<Barang> getAllBarangsByWilayah(Wilayah obj);

  public Barang getBarangByID(Long id);

  public Barang getBarangByKodeBarang(String string);

  public Barang getBarangByKodeBarangAndWilayah(String string, Wilayah obj);

  public List<Barang> getBarangsLikeKodeBarang(String string);

  public List<Barang> getBarangsLikeNamaBarang(String string);

  public BigDecimal getCicilanPerBulanByIntervalKredit(Barang obj, int interval);

  public int getCountAllBarangs();

  public BigDecimal getHargaBarangByIntervalKredit(Barang obj, int interval);

  public BigDecimal getKomisiSalesByIntervalKredit(Barang obj, int interval);

  public Barang getNewBarang();

  public BigDecimal getTabunganSalesByIntervalKredit(Barang obj, int interval);

  public void saveOrUpdate(Barang entity);
}
