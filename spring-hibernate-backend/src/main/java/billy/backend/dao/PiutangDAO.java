package billy.backend.dao;

import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;

public interface PiutangDAO {

  public void delete(Piutang entity);

  public void deletePiutangsByPenjualan(Penjualan obj);

  public int getCountAllPiutangs();

  public int getCountPiutangsByPenjualan(Penjualan obj);

  public Piutang getNewPiutang();

  public Piutang getPiutangById(long id);

  public List<Piutang> getPiutangsByPenjualan(Penjualan obj);

  public void save(Piutang entity);

  public void saveOrUpdate(Piutang entity);

}
