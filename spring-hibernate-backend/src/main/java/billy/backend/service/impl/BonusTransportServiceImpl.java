package billy.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import billy.backend.dao.BonusTransportDAO;
import billy.backend.model.BonusTransport;
import billy.backend.model.Karyawan;
import billy.backend.service.BonusTransportService;

public class BonusTransportServiceImpl implements BonusTransportService {

  private BonusTransportDAO bonusTransportDAO;
  private static final Logger logger = Logger.getLogger(BonusTransportServiceImpl.class);

  @Override
  public void delete(BonusTransport entity) {
    getBonusTransportDAO().delete(entity);
  }

  @Override
  public List<BonusTransport> getAllBonusTransports() {
    return getBonusTransportDAO().getAllBonusTransports();
  }

  @Override
  public BigDecimal getBonusSales(Karyawan karyawan, Double totalQty) {
    List<BonusTransport> result =
        getBonusTransportDAO().getBonusTransportByJobTypeIdAndUnit(karyawan.getJobType().getId(),
            totalQty);
    BigDecimal bonusSales = BigDecimal.ZERO;
    for (BonusTransport data : result) {
      if (data.getBonus() != null)
        bonusSales = bonusSales.add(data.getBonus());
    }
    return bonusSales;
  }

  @Override
  public BonusTransport getBonusTransportByDeskripsiBonusTransport(String string) {
    return getBonusTransportDAO().getBonusTransportByDeskripsiBonusTransport(string);
  }

  @Override
  public BonusTransport getBonusTransportByID(Long id) {
    return getBonusTransportDAO().getBonusTransportById(id);
  }

  public BonusTransportDAO getBonusTransportDAO() {
    return bonusTransportDAO;
  }

  @Override
  public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string) {
    return getBonusTransportDAO().getBonusTransportsLikeDeskripsiBonusTransport(string);
  }

  @Override
  public int getCountAllBonusTransports() {
    return getBonusTransportDAO().getCountAllBonusTransports();
  }

  @Override
  public BigDecimal getHonorDivisi(Karyawan karyawan, Double totalQty) {
    List<BonusTransport> result =
        getBonusTransportDAO().getBonusTransportByJobTypeIdAndUnit(karyawan.getJobType().getId(),
            totalQty);
    BigDecimal honorDivisi = BigDecimal.ZERO;
    for (BonusTransport data : result) {
      if (data.getHonor() != null)
        honorDivisi = honorDivisi.add(data.getHonor());
    }
    return honorDivisi;
  }

  @Override
  public BonusTransport getNewBonusTransport() {
    return getBonusTransportDAO().getNewBonusTransport();
  }

  @Override
  public BigDecimal getTransportSales(Karyawan karyawan, Double totalQty) {
    List<BonusTransport> result =
        getBonusTransportDAO().getBonusTransportByJobTypeIdAndUnit(karyawan.getJobType().getId(),
            totalQty);
    BigDecimal transportSales = BigDecimal.ZERO;
    for (BonusTransport data : result) {
      if (data.getTransport() != null)
        transportSales = transportSales.add(data.getTransport());
    }

    return transportSales;
  }

  @Override
  public void saveOrUpdate(BonusTransport entity) {
    getBonusTransportDAO().saveOrUpdate(entity);
  }

  public void setBonusTransportDAO(BonusTransportDAO bonusTransportDAO) {
    this.bonusTransportDAO = bonusTransportDAO;
  }

}
