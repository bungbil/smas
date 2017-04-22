package billy.backend.service.impl;

import java.math.BigDecimal;
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
  public void deleteNextPiutang(Piutang data) {

    int totalPiutang = data.getPenjualan().getIntervalKredit();
    int startDelete = data.getPembayaranKe() + 1;
    for (int i = startDelete; i < totalPiutang; i++) {
      Piutang piutang = getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(data.getNoFaktur(), i);
      getPiutangDAO().delete(piutang);
    }
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
      piutang.setNoFaktur(penjualan.getNoFaktur());
      piutang.setStatus(status);
      piutang.setKekuranganBayar(BigDecimal.ZERO);
      piutang.setDiskon(BigDecimal.ZERO);
      piutang.setPembayaran(BigDecimal.ZERO);
      piutang.setLastUpdate(new Date());
      piutang.setUpdatedBy("SYSTEM");

      piutang.setPembayaran(null);
      piutang.setTglPembayaran(null);
      piutang.setKolektor(null);
      piutang.setKeterangan(null);

      saveOrUpdate(piutang);
    }

  }

  private Piutang getAdditionalPiutang(Piutang currentData, Status statusProses) {
    Calendar cal = Calendar.getInstance();
    Piutang piutang = getNewPiutang();
    piutang.setAktif(false);

    Date tglJatuhTempo = currentData.getTglJatuhTempo();
    cal.setTime(tglJatuhTempo);

    cal.add(Calendar.MONTH, 1);
    tglJatuhTempo = cal.getTime();


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
    piutang.setNoKuitansi(dateString + "." + monthString + "."
        + currentData.getPenjualan().getNoFaktur());

    piutang.setNilaiTagihan(BigDecimal.ZERO);
    piutang.setPembayaranKe(currentData.getPembayaranKe() + 1);
    piutang.setPenjualan(currentData.getPenjualan());
    piutang.setNoFaktur(currentData.getPenjualan().getNoFaktur());


    piutang.setStatus(statusProses);
    piutang.setKekuranganBayar(BigDecimal.ZERO);
    piutang.setDiskon(BigDecimal.ZERO);
    piutang.setPembayaran(BigDecimal.ZERO);
    piutang.setLastUpdate(new Date());
    piutang.setUpdatedBy("SYSTEM");

    piutang.setPembayaran(null);
    piutang.setTglPembayaran(null);
    piutang.setKolektor(null);
    piutang.setKeterangan(null);

    saveOrUpdate(piutang);

    return piutang;
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
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawa(Karyawan obj, Date startDate,
      Date endDate) {
    return getPiutangDAO().getAllPiutangsByKolektorAndRangeDateTglBawa(obj, startDate, endDate);
  }

  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawaBelumBayar(Karyawan obj,
      Date startDate, Date endDate) {
    return getPiutangDAO().getAllPiutangsByKolektorAndRangeDateTglBawaBelumBayar(obj, startDate,
        endDate);
  }

  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate) {
    return getPiutangDAO().getAllPiutangsByKolektorAndRangeDateTglPembayaran(obj, startDate,
        endDate);
  }

  @Override
  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaranAndDiskon(Karyawan obj,
      Date startDate, Date endDate) {
    return getPiutangDAO().getAllPiutangsByKolektorAndRangeDateTglPembayaranAndDiskon(obj,
        startDate, endDate);
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
  public Piutang getNextPiutang(Piutang data, Status statusProses) {

    if (data.getPenjualan().getIntervalKredit() <= data.getPembayaranKe()) {
      return getAdditionalPiutang(data, statusProses);
    } else {

      return getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(data.getNoFaktur(),
          data.getPembayaranKe() + 1);
    }
  }

  @Override
  public Piutang getPiutangByNoFaktur(String data) {
    return getPiutangDAO().getPiutangByNoFaktur(data);
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
