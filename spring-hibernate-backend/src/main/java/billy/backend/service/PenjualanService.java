package billy.backend.service;

import java.math.BigDecimal;
import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Piutang;

public interface PenjualanService {

	public Penjualan getNewPenjualan();
	public int getCountAllPenjualans();
	public int getCountAllPenjualanDetails();
	public List<Penjualan> getAllPenjualans();
	public void initialize(Penjualan proxy);
	public Penjualan getPenjualanById(long id);
	public BigDecimal getPenjualanSum(Penjualan penjualan);
	public void saveOrUpdate(Penjualan penjualan);
	public void delete(Penjualan penjualan);
	
	public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan penjualan);
	public int getCountPenjualanDetailsByPenjualan(Penjualan penjualan);
	public PenjualanDetail getNewPenjualanDetail();
	public void saveOrUpdate(PenjualanDetail penjualanDetail);
	public void delete(PenjualanDetail penjualanDetail);
	
	public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan);
	public int getCountPiutangsByPenjualan(Penjualan penjualan);
	public Piutang getNewPiutang();
	public void saveOrUpdate(Piutang piutang);
	public void delete(Piutang piutang);

}
