package billy.backend.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import billy.backend.dao.BarangDAO;
import billy.backend.model.Barang;
import billy.backend.model.Wilayah;
import billy.backend.service.BarangService;

public class BarangServiceImpl implements BarangService {

  private BarangDAO barangDAO;

  @Override
  public void delete(Barang entity) {
    getBarangDAO().delete(entity);
  }

  @Override
  public List<Barang> getAllBarangs() {
    List<Barang> list = getBarangDAO().getAllBarangs();
    Collections.sort(list, new Comparator<Barang>() {
      @Override
      public int compare(Barang obj1, Barang obj2) {
        return obj1.getNamaBarang().compareTo(obj2.getNamaBarang());
      }
    });
    return list;
  }

  @Override
  public List<Barang> getAllBarangsByWilayah(Wilayah obj) {
    List<Barang> list = null;
    if (obj != null) {
      list = getBarangDAO().getAllBarangsByWilayahId(obj.getId());

    } else {
      list = getBarangDAO().getAllBarangs();
    }

    Collections.sort(list, new Comparator<Barang>() {
      @Override
      public int compare(Barang obj1, Barang obj2) {
        return obj1.getNamaBarang().compareTo(obj2.getNamaBarang());
      }
    });
    return list;
  }

  @Override
  public Barang getBarangByID(Long id) {
    return getBarangDAO().getBarangById(id);
  }

  @Override
  public Barang getBarangByKodeBarang(String string) {
    return getBarangDAO().getBarangByKodeBarang(string);
  }

  @Override
  public Barang getBarangByKodeBarangAndWilayah(String string, Wilayah obj) {
    return getBarangDAO().getBarangByKodeBarangAndWilayah(string, obj.getId());
  }

  public BarangDAO getBarangDAO() {
    return barangDAO;
  }

  @Override
  public List<Barang> getBarangsLikeKodeBarang(String string) {
    return getBarangDAO().getBarangsLikeKodeBarang(string);
  }

  @Override
  public List<Barang> getBarangsLikeNamaBarang(String string) {
    return getBarangDAO().getBarangsLikeNamaBarang(string);
  }

  @Override
  public BigDecimal getCicilanPerBulanByIntervalKredit(Barang obj, int interval) {
    BigDecimal data = BigDecimal.ZERO;
    switch (interval) {
      case 1:
        data = obj.getCicilanPerBulan1x();
        break;
      case 2:
        data = obj.getCicilanPerBulan2x();
        break;
      case 3:
        data = obj.getCicilanPerBulan3x();
        break;
      case 4:
        data = obj.getCicilanPerBulan4x();
        break;
      case 5:
        data = obj.getCicilanPerBulan5x();
        break;
      case 6:
        data = obj.getCicilanPerBulan6x();
        break;
      case 7:
        data = obj.getCicilanPerBulan7x();
        break;
      case 8:
        data = obj.getCicilanPerBulan8x();
        break;
      case 9:
        data = obj.getCicilanPerBulan9x();
        break;
      case 10:
        data = obj.getCicilanPerBulan10x();
        break;
      default:
        data = BigDecimal.ZERO;
        break;
    }
    if (data == null) {
      data = BigDecimal.ZERO;
    }
    return data;
  }

  @Override
  public int getCountAllBarangs() {
    return getBarangDAO().getCountAllBarangs();
  }

  @Override
  public BigDecimal getHargaBarangByIntervalKredit(Barang obj, int interval) {
    BigDecimal data = BigDecimal.ZERO;
    switch (interval) {
      case 1:
        data = obj.getHargaBarang1x();
        break;
      case 2:
        data = obj.getHargaBarang2x();
        break;
      case 3:
        data = obj.getHargaBarang3x();
        break;
      case 4:
        data = obj.getHargaBarang4x();
        break;
      case 5:
        data = obj.getHargaBarang5x();
        break;
      case 6:
        data = obj.getHargaBarang6x();
        break;
      case 7:
        data = obj.getHargaBarang7x();
        break;
      case 8:
        data = obj.getHargaBarang8x();
        break;
      case 9:
        data = obj.getHargaBarang9x();
        break;
      case 10:
        data = obj.getHargaBarang10x();
        break;
      default:
        data = BigDecimal.ZERO;
        break;
    }
    if (data == null) {
      data = BigDecimal.ZERO;
    }
    return data;
  }

  @Override
  public BigDecimal getKomisiSalesByIntervalKredit(Barang obj, int interval) {
    BigDecimal data = BigDecimal.ZERO;
    switch (interval) {
      case 1:
        data = obj.getKomisiSales1x();
        break;
      case 2:
        data = obj.getKomisiSales2x();
        break;
      case 3:
        data = obj.getKomisiSales3x();
        break;
      case 4:
        data = obj.getKomisiSales4x();
        break;
      case 5:
        data = obj.getKomisiSales5x();
        break;
      case 6:
        data = obj.getKomisiSales6x();
        break;
      case 7:
        data = obj.getKomisiSales7x();
        break;
      case 8:
        data = obj.getKomisiSales8x();
        break;
      case 9:
        data = obj.getKomisiSales9x();
        break;
      case 10:
        data = obj.getKomisiSales10x();
        break;
      default:
        data = BigDecimal.ZERO;
        break;
    }
    if (data == null) {
      data = BigDecimal.ZERO;
    }
    return data;
  }

  @Override
  public Barang getNewBarang() {
    return getBarangDAO().getNewBarang();
  }

  @Override
  public BigDecimal getTabunganSalesByIntervalKredit(Barang obj, int interval) {
    BigDecimal data = BigDecimal.ZERO;
    switch (interval) {
      case 1:
        data = obj.getTabunganSales1x();
        break;
      case 2:
        data = obj.getTabunganSales2x();
        break;
      case 3:
        data = obj.getTabunganSales3x();
        break;
      case 4:
        data = obj.getTabunganSales4x();
        break;
      case 5:
        data = obj.getTabunganSales5x();
        break;
      case 6:
        data = obj.getTabunganSales6x();
        break;
      case 7:
        data = obj.getTabunganSales7x();
        break;
      case 8:
        data = obj.getTabunganSales8x();
        break;
      case 9:
        data = obj.getTabunganSales9x();
        break;
      case 10:
        data = obj.getTabunganSales10x();
        break;
      default:
        data = BigDecimal.ZERO;
        break;
    }
    if (data == null) {
      data = BigDecimal.ZERO;
    }
    return data;
  }

  @Override
  public void saveOrUpdate(Barang entity) {
    getBarangDAO().saveOrUpdate(entity);
  }

  public void setBarangDAO(BarangDAO barangDAO) {
    this.barangDAO = barangDAO;
  }
}
