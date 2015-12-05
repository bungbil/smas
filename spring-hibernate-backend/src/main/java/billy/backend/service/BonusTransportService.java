package billy.backend.service;

import java.util.List;

import billy.backend.model.BonusTransport;
public interface BonusTransportService {

	public BonusTransport getNewBonusTransport();

	public int getCountAllBonusTransports();

	public BonusTransport getBonusTransportByID(Long id);

	public List<BonusTransport> getAllBonusTransports();

	public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string);
	
	public void saveOrUpdate(BonusTransport entity);

	public void delete(BonusTransport entity);

}
