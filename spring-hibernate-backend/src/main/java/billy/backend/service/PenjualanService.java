package billy.backend.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;

public interface PenjualanService {


  public void delete(Penjualan penjualan);


  public void delete(PenjualanDetail penjualanDetail);

  public void deletePenjualanDetailsByPenjualan(Penjualan penjualan);

  public List<Penjualan> getAllPenjualans();

  public List<Penjualan> getAllPenjualansByDivisiAndRangeDate(Karyawan obj, Date startDate,
      Date endDate);

  public List<Penjualan> getAllPenjualansByListNoFaktur(List<String> listNoFaktur);

  public List<Penjualan> getAllPenjualansBySalesAndRangeDate(Karyawan obj, Date startDate,
      Date endDate);

  public List<Penjualan> getAllPenjualansBySalesAndRangeDateUnderDivisi(Karyawan sales,
      Date startDate, Date endDate, Karyawan divisi);

  public int getCountAllPenjualanDetails();

  public int getCountAllPenjualans();

  public int getCountAllPenjualansByDivisi(Karyawan obj, Date date);

  public int getCountPenjualanDetailsByPenjualan(Penjualan penjualan);

  public List<Penjualan> getListNeedApprovalPenjualansByListNoFaktur(List<String> listNoFaktur);

  public Penjualan getNewPenjualan();

  public PenjualanDetail getNewPenjualanDetail();

  // public void initialize(Penjualan proxy);
  public Penjualan getPenjualanById(long id);

  public Penjualan getPenjualanByNoFaktur(String noFaktur);

  public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan penjualan);

  public BigDecimal getPenjualanSum(Penjualan penjualan);

  public void saveOrUpdate(Penjualan penjualan);

  public void saveOrUpdate(PenjualanDetail penjualanDetail);


}
