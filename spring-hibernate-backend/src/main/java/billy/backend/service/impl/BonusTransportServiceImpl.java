package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.BonusTransportDAO;
import billy.backend.model.BonusTransport;
import billy.backend.service.BonusTransportService;

public class BonusTransportServiceImpl implements BonusTransportService {

  private BonusTransportDAO bonusTransportDAO;

  @Override
  public void delete(BonusTransport entity) {
    getBonusTransportDAO().delete(entity);
  }

  @Override
  public List<BonusTransport> getAllBonusTransports() {
    return getBonusTransportDAO().getAllBonusTransports();
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
  public BonusTransport getNewBonusTransport() {
    return getBonusTransportDAO().getNewBonusTransport();
  }

  @Override
  public void saveOrUpdate(BonusTransport entity) {
    getBonusTransportDAO().saveOrUpdate(entity);
  }

  public void setBonusTransportDAO(BonusTransportDAO bonusTransportDAO) {
    this.bonusTransportDAO = bonusTransportDAO;
  }

}
