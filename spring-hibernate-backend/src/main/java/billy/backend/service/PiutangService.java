package billy.backend.service;

import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.model.Status;

public interface PiutangService {

  public void delete(Piutang piutang);

  public void generatePiutangByIntervalKredit(Penjualan penjualan, int intervalKredit, Status status);

  public int getCountPiutangsByPenjualan(Penjualan penjualan);

  public Piutang getNewPiutang();

  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan);

  public void saveOrUpdate(Piutang piutang);


}
