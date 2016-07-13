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

  public List<Piutang> getAllPiutangsByDivisiAndRangeDate(Karyawan obj, Date startDate, Date endDate);

  public int getCountPiutangsByPenjualan(Penjualan penjualan);

  public Piutang getNewPiutang();

  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan);

  public void saveOrUpdate(Piutang piutang);


}
