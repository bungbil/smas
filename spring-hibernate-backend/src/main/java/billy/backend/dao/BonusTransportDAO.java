package billy.backend.dao;

import java.util.List;

import billy.backend.model.BonusTransport;

public interface BonusTransportDAO {

	public BonusTransport getNewBonusTransport();

	public BonusTransport getBonusTransportById(Long id);

	public BonusTransport getBonusTransportByDeskripsiBonusTransport(String string);

	public List<BonusTransport> getAllBonusTransports();

	public int getCountAllBonusTransports();

	public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string);

	public void deleteBonusTransportById(long id);

	public void saveOrUpdate(BonusTransport entity);

	public void delete(BonusTransport entity);

	public void save(BonusTransport entity);

}
