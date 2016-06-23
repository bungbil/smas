package billy.backend.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;

public interface PenjualanDAO {

	public Penjualan getNewPenjualan();

	public int getCountAllPenjualans();
	public int getCountAllPenjualansByDivisi(Karyawan obj, Date startDate, Date endDate);
	
	public Penjualan getPenjualanById(long id);

	public Penjualan getPenjualanByNoFaktur(String string);
	
	public List<Penjualan> getAllPenjualans();
	public List<Penjualan> getListNeedApprovalPenjualansByListNoFaktur(List<String> listNoFaktur);
	public List<Penjualan> getAllPenjualansByListNoFaktur(List<String> listNoFaktur);
	public BigDecimal getPenjualanSum(Penjualan obj);

	public void refresh(Penjualan obj);

	public void initialize(Penjualan obj);

	public void saveOrUpdate(Penjualan entity);

	public void delete(Penjualan entity);

	public void save(Penjualan entity);

	public List<Penjualan> getAllPenjualansByDivisiAndRangeDate(Karyawan obj,
			Date startDate, Date endDate);

	public List<Penjualan> getAllPenjualansByKaryawanAndRangeDate(Karyawan obj,
			Date startDate, Date endDate);

}
