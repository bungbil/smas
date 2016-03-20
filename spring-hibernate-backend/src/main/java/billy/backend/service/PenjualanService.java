package billy.backend.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;

public interface PenjualanService {

	public Penjualan getNewPenjualan();
	public int getCountAllPenjualans();
	public int getCountAllPenjualanDetails();
	public int getCountAllPenjualansByDivisi(Karyawan obj, Date date);
	public List<Penjualan> getAllPenjualans();
//	public void initialize(Penjualan proxy);
	public Penjualan getPenjualanById(long id);
	public BigDecimal getPenjualanSum(Penjualan penjualan);
	public void saveOrUpdate(Penjualan penjualan);
	public void delete(Penjualan penjualan);
	
	public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan penjualan);
	public int getCountPenjualanDetailsByPenjualan(Penjualan penjualan);
	public PenjualanDetail getNewPenjualanDetail();
	public void saveOrUpdate(PenjualanDetail penjualanDetail);
	public void delete(PenjualanDetail penjualanDetail);
	public void deletePenjualanDetailsByPenjualan(Penjualan penjualan);

}
