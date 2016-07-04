package billy.backend.service;

import java.math.BigDecimal;
import java.util.List;

import billy.backend.model.BonusTransport;
import billy.backend.model.Karyawan;

public interface BonusTransportService {

  public void delete(BonusTransport entity);

  public List<BonusTransport> getAllBonusTransports();

  public BigDecimal getBonusSales(Karyawan karyawan, Double totalQty);

  public BonusTransport getBonusTransportByDeskripsiBonusTransport(String string);

  public BonusTransport getBonusTransportByID(Long id);

  public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string);

  public int getCountAllBonusTransports();

  public BigDecimal getHonorDivisi(Karyawan karyawan, Double totalQty);

  public BonusTransport getNewBonusTransport();

  public BigDecimal getTransportSales(Karyawan karyawan, Double totalQty);

  public void saveOrUpdate(BonusTransport entity);

}
