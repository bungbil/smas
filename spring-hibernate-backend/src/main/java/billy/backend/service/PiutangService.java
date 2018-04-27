package billy.backend.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;

public interface PiutangService {

  public void delete(Piutang piutang);

  public void deleteAllPiutang(Penjualan penjualan);

  public void deleteNextPiutang(Piutang data);

  public void generatePiutangByIntervalKredit(Penjualan penjualan, int intervalKredit, Status status);

  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawa(Karyawan obj, Date startDate,
      Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawaBelumBayar(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaranAndDiskon(Karyawan obj,
      Date startDate, Date endDate);

  public int getCountPiutangsByPenjualan(Penjualan penjualan);

  public Piutang getNewPiutang();

  public Piutang getNextPiutang(Piutang data);

  public Piutang getNextPiutang(Piutang data, Status status);

  public Piutang getPiutangByNoFaktur(String data);

  public Piutang getPiutangByNoKuitansi(String data);

  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan);

  public boolean resetPembayaranPiutang(Piutang piutang, Status statusProses);

  public void saveOrUpdate(Piutang piutang);

  public void updateAlamat(Piutang piutang);


  public void updateNextNilaiTagihan(Piutang piutang, BigDecimal nilaiTagihan);

  public void updateTglJatuhTempo(Piutang data);


}
