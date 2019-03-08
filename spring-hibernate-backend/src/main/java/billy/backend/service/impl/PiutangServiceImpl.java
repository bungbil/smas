package billy.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import billy.backend.dao.PiutangDAO;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.PiutangService;

public class PiutangServiceImpl implements PiutangService {

  private PiutangDAO piutangDAO;
  private static final Logger logger = Logger.getLogger(PiutangServiceImpl.class);

  /**
   * default Constructor
   */
  public PiutangServiceImpl() {}

  @Override
  public void delete(Piutang piutang) {
    getPiutangDAO().delete(piutang);

  }

  @Override
  public void deleteAllPiutang(Penjualan penjualan) {

    // if piutang exist must delete all
    try {
      logger.info("Start delete piutang by penjualan dengan no faktur :" + penjualan.getNoFaktur());
      List<Piutang> piutang = getPiutangsByPenjualan(penjualan);

      for (Piutang entity : piutang) {
        getPiutangDAO().delete(entity);
      }
      logger.info("End delete piutang by penjualan dengan no faktur :" + penjualan.getNoFaktur());
    } catch (Exception e) {
      logger.error("Error delete piutang by penjualan dengan no faktur :" + penjualan.getNoFaktur()
          + ", error message : " + e.getMessage());
    }

  }

  @Override
  public void deleteNextPiutang(Piutang data) {

    int totalPiutang = data.getPenjualan().getIntervalKredit();
    int startDelete = data.getPembayaranKe() + 1;
    try {
      for (int i = startDelete; i < totalPiutang; i++) {
        Piutang piutang =
            getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(data.getNoFaktur(), i);
        getPiutangDAO().delete(piutang);
      }
    } catch (Exception e) {
      logger.error("Error delete next piutang dengan no faktur :" + data.getNoFaktur()
          + ", error message : " + e.getMessage());
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

      piutang.setMasalah(false);
      piutang.setNamaPelanggan(penjualan.getNamaPelanggan());
      piutang.setTelepon(penjualan.getTelepon());
      piutang.setAlamat(penjualan.getAlamat());
      piutang.setAlamat2(penjualan.getAlamat2());
      piutang.setAlamat3(penjualan.getAlamat3());

      saveOrUpdate(piutang);
    }

  }

  private Piutang getAdditionalPiutang(Piutang currentData, Status statusProses) {
    Calendar cal = Calendar.getInstance();
    Piutang piutang = getNewPiutang();
    piutang.setAktif(true);

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

    piutang.setMasalah(false);
    piutang.setNamaPelanggan(currentData.getNamaPelanggan());
    piutang.setTelepon(currentData.getTelepon());
    piutang.setAlamat(currentData.getAlamat());
    piutang.setAlamat2(currentData.getAlamat2());
    piutang.setAlamat3(currentData.getAlamat3());

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
  public Piutang getNextPiutang(Piutang data) {

    return getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(data.getNoFaktur(),
        data.getPembayaranKe() + 1);
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
  public Piutang getPiutangM3(Penjualan penjualan) {
    Piutang piutangAktif = getPiutangDAO().getPiutangByNoFakturAndAktif(penjualan.getNoFaktur());

    if (piutangAktif != null && piutangAktif.isWarning()) {
      return piutangAktif;
    } else if (piutangAktif != null && piutangAktif.getPembayaranKe() > 5
        && piutangAktif.getStatusFinal() == null) {
      Piutang piutangBulan1 =
          getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(penjualan.getNoFaktur(),
              piutangAktif.getPembayaranKe() - 1);
      Piutang piutangBulan2 =
          getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(penjualan.getNoFaktur(),
              piutangAktif.getPembayaranKe() - 2);
      Piutang piutangBulan3 =
          getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(penjualan.getNoFaktur(),
              piutangAktif.getPembayaranKe() - 3);

      if (piutangBulan1.getPembayaran().compareTo(BigDecimal.ZERO) == 0
          && piutangBulan2.getPembayaran().compareTo(BigDecimal.ZERO) == 0
          && piutangBulan3.getPembayaran().compareTo(BigDecimal.ZERO) == 0) {

        return piutangAktif;
      }
    }
    return null;
  }


  @Override
  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan) {
    List<Piutang> result = getPiutangDAO().getPiutangsByPenjualan(penjualan);
    return result;
  }

  @Override
  public Piutang getPiutangTerakhirDibayar(Penjualan penjualan) {
    Piutang piutangAktif = getPiutangDAO().getPiutangByNoFakturAndAktif(penjualan.getNoFaktur());
    for (int i = piutangAktif.getPembayaranKe() - 4; i > 1; i--) {
      Piutang piutang =
          getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(penjualan.getNoFaktur(), i);
      if (piutang.getPembayaran().compareTo(BigDecimal.ZERO) == 1) {
        return piutang;
      }

    }

    return null;
  }

  @Override
  public boolean resetPembayaranPiutang(Piutang piutang, Status statusProses) {
    boolean result = false;

    Piutang nextPiutang =
        getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(piutang.getNoFaktur(),
            piutang.getPembayaranKe() + 1);
    if (nextPiutang != null && nextPiutang.isAktif()) {
      result = true;
      nextPiutang.setAktif(false);
      nextPiutang.setKekuranganBayar(null);
      nextPiutang.setKolektor(null);
      nextPiutang.setTglBawaKolektor(null);
      nextPiutang.setNeedApproval(false);
      nextPiutang.setReasonApproval(null);
      nextPiutang.setFullPayment(false);
      nextPiutang.setPembayaran(null);
      nextPiutang.setTglPembayaran(null);
      nextPiutang.setDiskon(null);
      nextPiutang.setStatus(statusProses);
      nextPiutang.setStatusFinal(null);
      saveOrUpdate(nextPiutang);
    }
    piutang.setAktif(true);
    piutang.setFullPayment(false);
    piutang.setPembayaran(null);
    piutang.setTglPembayaran(null);
    piutang.setDiskon(null);
    piutang.setStatus(statusProses);
    piutang.setStatusFinal(null);
    saveOrUpdate(piutang);

    return result;
  }

  @Override
  public void saveOrUpdate(Piutang piutang) {
    getPiutangDAO().saveOrUpdate(piutang);

  }

  public void setPiutangDAO(PiutangDAO piutangDAO) {
    this.piutangDAO = piutangDAO;
  }

  @Override
  public void updateAlamat(Piutang piutang) {

    for (int i = piutang.getPembayaranKe() + 1; i <= piutang.getPenjualan().getIntervalKredit(); i++) {
      Piutang data = getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(piutang.getNoFaktur(), i);
      data.setNamaPelanggan(piutang.getNamaPelanggan());
      data.setTelepon(piutang.getTelepon());
      data.setAlamat(piutang.getAlamat());
      data.setAlamat2(piutang.getAlamat2());
      data.setAlamat3(piutang.getAlamat3());

      saveOrUpdate(data);
    }
  }

  @Override
  public void updateNextNilaiTagihan(Piutang piutang, BigDecimal nilaiTagihan) {
    for (int i = piutang.getPembayaranKe() + 1; i <= piutang.getPenjualan().getIntervalKredit(); i++) {
      Piutang data = getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(piutang.getNoFaktur(), i);
      data.setNilaiTagihan(nilaiTagihan);
      saveOrUpdate(data);
    }
  }

  @Override
  public void updateTglJatuhTempo(Piutang piutang) {
    saveOrUpdate(piutang);

    Date tglJatuhTempo = piutang.getTglJatuhTempo();
    Calendar cal = Calendar.getInstance();

    for (int i = piutang.getPembayaranKe() + 1; i <= piutang.getPenjualan().getIntervalKredit(); i++) {
      Piutang data = getPiutangDAO().getPiutangByNoFakturAndPembayaranKe(piutang.getNoFaktur(), i);
      cal.setTime(tglJatuhTempo);
      cal.add(Calendar.MONTH, 1);
      tglJatuhTempo = cal.getTime();
      data.setTglJatuhTempo(tglJatuhTempo);
      saveOrUpdate(data);
    }
  }
}
