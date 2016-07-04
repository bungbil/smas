package billy.backend.service;

import java.util.List;

import billy.backend.model.BonusTransport;

public interface BonusTransportService {

  public void delete(BonusTransport entity);

  public List<BonusTransport> getAllBonusTransports();

  public BonusTransport getBonusTransportByDeskripsiBonusTransport(String string);

  public BonusTransport getBonusTransportByID(Long id);

  public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string);

  public int getCountAllBonusTransports();

  public BonusTransport getNewBonusTransport();

  public void saveOrUpdate(BonusTransport entity);

}
