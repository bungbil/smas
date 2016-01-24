package billy.backend.dao;

import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;

public interface PenjualanDetailDAO {

	public PenjualanDetail getNewPenjualanDetail();

	public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan obj);

	public int getCountPenjualanDetailsByPenjualan(Penjualan obj);

	public int getCountAllPenjualanDetails();

	public PenjualanDetail getPenjualanDetailById(long id);

	public void deletePenjualanDetailsByPenjualan(Penjualan obj);

	public void saveOrUpdate(PenjualanDetail entity);

	public void delete(PenjualanDetail entity);

	public void save(PenjualanDetail entity);

}
