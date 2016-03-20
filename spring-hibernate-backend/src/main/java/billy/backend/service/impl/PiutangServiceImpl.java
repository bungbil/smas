package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.PiutangDAO;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;
import billy.backend.service.PiutangService;
public class PiutangServiceImpl implements PiutangService {

	private PiutangDAO piutangDAO;


	public PiutangDAO getPiutangDAO() {
		return piutangDAO;
	}

	public void setPiutangDAO(PiutangDAO piutangDAO) {
		this.piutangDAO = piutangDAO;
	}

	/**
	 * default Constructor
	 */
	public PiutangServiceImpl() {
	}

	
	@Override
	public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan) {		
		List<Piutang> result = getPiutangDAO().getPiutangsByPenjualan(penjualan);
		return result;
	}

	@Override
	public int getCountPiutangsByPenjualan(Penjualan penjualan) {
		int result = getPiutangDAO().getCountPiutangsByPenjualan(penjualan);
		return result;
	}

	@Override
	public Piutang getNewPiutang() {
		return getPiutangDAO().getNewPiutang();		
	}

	@Override
	public void saveOrUpdate(Piutang piutang) {
		getPiutangDAO().saveOrUpdate(piutang);
		
	}

	@Override
	public void delete(Piutang piutang) {
		getPiutangDAO().delete(piutang);
		
	}
}
