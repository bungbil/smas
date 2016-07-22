package billy.backend.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import billy.backend.dao.PiutangDAO;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.PiutangService;

public class PiutangServiceImpl implements PiutangService {

  private PiutangDAO piutangDAO;


  /**
   * default Constructor
   */
  public PiutangServiceImpl() {}

  @Override
  public void delete(Piutang piutang) {
    getPiutangDAO().delete(piutang);

  }

  @Override
  public void generatePiutangByIntervalKredit(Penjualan penjualan, int intervalKredit, Status status) {

    Calendar cal = Calendar.getInstance();
    for (int i = 2; i <= intervalKredit; i++) {
      Date tglJatuhTempo = penjualan.getTglAngsuran2();
      cal.setTime(tglJatuhTempo);
      Piutang piutang = getNewPiutang();

      if (i == 2) {
        piutang.setAktif(true);
      } else {
        piutang.setAktif(false);
      }
      if (i > 2) {
        cal.add(Calendar.MONTH, i - 2);
        tglJatuhTempo = cal.getTime();
      }

      int month = cal.get(Calendar.MONTH) + 1;
      int date = cal.get(Calendar.DATE);
      piutang.setTglJatuhTempo(tglJatuhTempo);

      String monthString = String.valueOf(month);
      String dateString = String.valueOf(date);
      if (dateString.length() == 1) {
        dateString = "0" + dateString;
      }
      if (monthString.length() == 1) {
        monthString = "0" + monthString;
      }
      piutang.setNoKuitansi(dateString + "." + monthString + "." + penjualan.getNoFaktur());

      piutang.setNilaiTagihan(penjualan.getKreditPerBulan());
      piutang.setPembayaranKe(i);
      piutang.setPenjualan(penjualan);
      piutang.setStatus(status);
      piutang.setLastUpdate(new Date());
      piutang.setUpdatedBy("SYSTEM");

      piutang.setPembayaran(null);
      piutang.setTglPembayaran(null);
      piutang.setKolektor(null);
      piutang.setKeterangan(null);

      saveOrUpdate(piutang);
    }

  }


  @Override
  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate) {
    List<Piutang> allPiutang = new ArrayList<Piutang>();
    List<Piutang> allPiutangSalesUnderDivisi =
        getPiutangDAO().getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(obj, startDate, endDate);
    List<Piutang> allPiutangKaryawanDivisi =
        getPiutangDAO().getAllPiutangsByKaryawanAndRangeDateTglJatuhTempo(obj, startDate, endDate);
    allPiutang.addAll(allPiutangSalesUnderDivisi);
    allPiutang.addAll(allPiutangKaryawanDivisi);

    return allPiutang;
  }

  @Override
  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate) {
    List<Piutang> allPiutang = new ArrayList<Piutang>();
    List<Piutang> allPiutangSalesUnderDivisi =
        getPiutangDAO().getAllPiutangsByDivisiAndRangeDateTglPembayaran(obj, startDate, endDate);
    List<Piutang> allPiutangKaryawanDivisi =
        getPiutangDAO().getAllPiutangsByKaryawanAndRangeDateTglPembayaran(obj, startDate, endDate);
    allPiutang.addAll(allPiutangSalesUnderDivisi);
    allPiutang.addAll(allPiutangKaryawanDivisi);

    return allPiutang;
  }

  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate) {
    return getPiutangDAO().getAllPiutangsByKolektorAndRangeDateTglPembayaran(obj, startDate,
        endDate);
  }

  @Override
  public int getCountPiutangsByPenjualan(Penjualan penjualan) {
    int result = getPiutangDAO().getCountPiutangsByPenjualan(penjualan);
    return result;
  }

  @Override
  public Piutang getNewPiutang() {
    return getPiutangDAO().getNewPiutang();
  }

  @Override
  public Piutang getPiutangByNoKuitansi(String data) {
    return getPiutangDAO().getPiutangByNoKuitansi(data);
  }

  public PiutangDAO getPiutangDAO() {
    return piutangDAO;
  }

  @Override
  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan) {
    List<Piutang> result = getPiutangDAO().getPiutangsByPenjualan(penjualan);
    return result;
  }

  @Override
  public void saveOrUpdate(Piutang piutang) {
    getPiutangDAO().saveOrUpdate(piutang);

  }

  public void setPiutangDAO(PiutangDAO piutangDAO) {
    this.piutangDAO = piutangDAO;
  }
}
