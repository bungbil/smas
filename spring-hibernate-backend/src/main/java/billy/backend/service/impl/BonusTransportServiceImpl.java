package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.BonusTransportDAO;
import billy.backend.model.BonusTransport;
import billy.backend.service.BonusTransportService;

public class BonusTransportServiceImpl implements BonusTransportService {

	private BonusTransportDAO bonusTransportDAO;

	public BonusTransportDAO getBonusTransportDAO() {
		return bonusTransportDAO;
	}

	public void setBonusTransportDAO(BonusTransportDAO bonusTransportDAO) {
		this.bonusTransportDAO = bonusTransportDAO;
	}

	@Override
	public BonusTransport getNewBonusTransport() {
		return getBonusTransportDAO().getNewBonusTransport();
	}

	@Override
	public BonusTransport getBonusTransportByID(Long id) {
		return getBonusTransportDAO().getBonusTransportById(id);
	}

	@Override
	public List<BonusTransport> getAllBonusTransports() {
		return getBonusTransportDAO().getAllBonusTransports();
	}

	@Override
	public void saveOrUpdate(BonusTransport entity) {
		getBonusTransportDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(BonusTransport entity) {
		getBonusTransportDAO().delete(entity);
	}

	@Override
	public List<BonusTransport> getBonusTransportsLikeDeskripsiBonusTransport(String string) {
		return getBonusTransportDAO().getBonusTransportsLikeDeskripsiBonusTransport(string);
	}

	@Override
	public int getCountAllBonusTransports() {
		return getBonusTransportDAO().getCountAllBonusTransports();
	}

}
