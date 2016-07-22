package billy.backend.service;

import java.util.Date;
import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;

public interface PiutangService {

  public void delete(Piutang piutang);

  public void generatePiutangByIntervalKredit(Penjualan penjualan, int intervalKredit, Status status);

  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public int getCountPiutangsByPenjualan(Penjualan penjualan);

  public Piutang getNewPiutang();

  public Piutang getPiutangByNoKuitansi(String data);

  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan);

  public void saveOrUpdate(Piutang piutang);


}
