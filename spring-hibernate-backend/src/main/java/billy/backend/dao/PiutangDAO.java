package billy.backend.dao;

import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;

public interface PiutangDAO {

	public Piutang getNewPiutang();

	public List<Piutang> getPiutangsByPenjualan(Penjualan obj);

	public int getCountPiutangsByPenjualan(Penjualan obj);

	public int getCountAllPiutangs();

	public Piutang getPiutangById(long id);

	public void deletePiutangsByPenjualan(Penjualan obj);

	public void saveOrUpdate(Piutang entity);

	public void delete(Piutang entity);

	public void save(Piutang entity);

}
