package billy.backend.service;

import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;

public interface PiutangService {

  public void delete(Piutang piutang);

  public int getCountPiutangsByPenjualan(Penjualan penjualan);

  public Piutang getNewPiutang();

  public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan);

  public void saveOrUpdate(Piutang piutang);

}
