package billy.backend.dao;

import java.util.List;

import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;

public interface PenjualanDetailDAO {

  public void delete(PenjualanDetail entity);

  public void deletePenjualanDetailsByPenjualan(Penjualan obj);

  public int getCountAllPenjualanDetails();

  public int getCountPenjualanDetailsByPenjualan(Penjualan obj);

  public PenjualanDetail getNewPenjualanDetail();

  public PenjualanDetail getPenjualanDetailById(long id);

  public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan obj);

  public void save(PenjualanDetail entity);

  public void saveOrUpdate(PenjualanDetail entity);

}
