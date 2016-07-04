package billy.backend.dao;

import java.util.List;

import billy.backend.model.BonusTransport;

public interface BonusTransportDAO {

  public void delete(BonusTransport entity);

  public void deleteBonusTransportById(long id);

  public List<BonusTransport> getAllBonusTransports();

  public BonusTransport getBonusTransportByDeskripsiBonusTransport(String string);

  public BonusTransport getBonusTransportById(Long id);

  public List<BonusTransport> getBonusTransportByJobTypeIdAndUnit(long id, Double totalQty);

  public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string);

  public int getCountAllBonusTransports();

  public BonusTransport getNewBonusTransport();

  public void save(BonusTransport entity);

  public void saveOrUpdate(BonusTransport entity);

}
