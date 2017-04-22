package billy.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import billy.backend.dao.PenjualanDAO;
import billy.backend.dao.PenjualanDetailDAO;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.PenjualanService;


public class PenjualanServiceImpl implements PenjualanService {

  private PenjualanDAO penjualanDAO;
  private PenjualanDetailDAO penjualanDetailDAO;

  /**
   * default Constructor
   */
  public PenjualanServiceImpl() {}

  @Override
  public void delete(Penjualan penjualan) {
    getPenjualanDAO().delete(penjualan);
  }

  @Override
  public void delete(PenjualanDetail penjualanDetail) {
    getPenjualanDetailDAO().delete(penjualanDetail);
  }

  @Override
  public void deletePenjualanDetailsByPenjualan(Penjualan penjualan) {
    getPenjualanDetailDAO().deletePenjualanDetailsByPenjualan(penjualan);
  }

  @Override
  public List<Penjualan> getAllPenjualans() {
    return getPenjualanDAO().getAllPenjualans();
  }

  @Override
  public List<Penjualan> getAllPenjualansByDivisiAndRangeDate(Karyawan obj, Date startDate,
      Date endDate) {
    List<Penjualan> allPenjualan = new ArrayList<Penjualan>();
    List<Penjualan> allPenjualanSalesUnderDivisi =
        getPenjualanDAO().getAllPenjualansByDivisiAndRangeDate(obj, startDate, endDate);
    // List<Penjualan> allPenjualanKaryawanDivisi =
    // getPenjualanDAO().getAllPenjualansByKaryawanAndRangeDate(obj, startDate, endDate);
    allPenjualan.addAll(allPenjualanSalesUnderDivisi);
    // allPenjualan.addAll(allPenjualanKaryawanDivisi);

    return allPenjualan;
  }

  @Override
  public List<Penjualan> getAllPenjualansByListNoFaktur(List<String> listNoFaktur) {
    List<Penjualan> allPenjualan = getPenjualanDAO().getAllPenjualansByListNoFaktur(listNoFaktur);

    Collections.sort(allPenjualan, new Comparator<Penjualan>() {
      @Override
      public int compare(Penjualan obj1, Penjualan obj2) {
        return obj1.getNoFaktur().compareTo(obj2.getNoFaktur());
      }
    });

    return allPenjualan;
  }

  @Override
  public List<Penjualan> getAllPenjualansBySalesAndRangeDate(Karyawan obj, Date startDate,
      Date endDate) {
    List<Penjualan> allPenjualan = new ArrayList<Penjualan>();
    List<Penjualan> allPenjualanKaryawan =
        getPenjualanDAO().getAllPenjualansByKaryawanAndRangeDate(obj, startDate, endDate);
    allPenjualan.addAll(allPenjualanKaryawan);

    return allPenjualan;
  }

  @Override
  public int getCountAllPenjualanDetails() {
    return getPenjualanDetailDAO().getCountAllPenjualanDetails();
  }

  @Override
  public int getCountAllPenjualans() {
    return getPenjualanDAO().getCountAllPenjualans();
  }

  @Override
  public int getCountAllPenjualansByDivisi(Karyawan obj, Date date) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    Date startDate = calendar.getTime();
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    Date endDate = calendar.getTime();

    return getPenjualanDAO().getCountAllPenjualansByDivisi(obj, startDate, endDate);
  }


  @Override
  public int getCountPenjualanDetailsByPenjualan(Penjualan penjualan) {
    int result = getPenjualanDetailDAO().getCountPenjualanDetailsByPenjualan(penjualan);
    return result;
  }

  @Override
  public List<Penjualan> getListNeedApprovalPenjualansByListNoFaktur(List<String> listNoFaktur) {
    return getPenjualanDAO().getListNeedApprovalPenjualansByListNoFaktur(listNoFaktur);
  }

  @Override
  public Penjualan getNewPenjualan() {
    return getPenjualanDAO().getNewPenjualan();
  }

  @Override
  public PenjualanDetail getNewPenjualanDetail() {
    return getPenjualanDetailDAO().getNewPenjualanDetail();
  }

  @Override
  public Penjualan getPenjualanById(long id) {
    return getPenjualanDAO().getPenjualanById(id);
  }

  @Override
  public Penjualan getPenjualanByNoFaktur(String noFaktur) {
    return getPenjualanDAO().getPenjualanByNoFaktur(noFaktur);
  }

  public PenjualanDAO getPenjualanDAO() {
    return penjualanDAO;
  }

  public PenjualanDetailDAO getPenjualanDetailDAO() {
    return penjualanDetailDAO;
  }

  @Override
  public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan penjualan) {

    getPenjualanDAO().refresh(penjualan);
    getPenjualanDAO().initialize(penjualan);

    List<PenjualanDetail> result =
        getPenjualanDetailDAO().getPenjualanDetailsByPenjualan(penjualan);

    return result;
  }

  @Override
  public BigDecimal getPenjualanSum(Penjualan penjualan) {
    return getPenjualanDAO().getPenjualanSum(penjualan);
  }

  @Override
  public void saveOrUpdate(Penjualan penjualan) {
    getPenjualanDAO().saveOrUpdate(penjualan);
  }


  @Override
  public void saveOrUpdate(PenjualanDetail penjualanDetail) {
    getPenjualanDetailDAO().saveOrUpdate(penjualanDetail);
  }

  public void setPenjualanDAO(PenjualanDAO penjualanDAO) {
    this.penjualanDAO = penjualanDAO;
  }

  public void setPenjualanDetailDAO(PenjualanDetailDAO penjualanDetailDAO) {
    this.penjualanDetailDAO = penjualanDetailDAO;
  }

}
