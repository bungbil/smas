package billy.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;

import billy.backend.dao.PenjualanDAO;
import billy.backend.dao.PenjualanDetailDAO;
import billy.backend.dao.PiutangDAO;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Piutang;
import billy.backend.service.PenjualanService;
public class PenjualanServiceImpl implements PenjualanService {

	private PenjualanDAO penjualanDAO;
	private PenjualanDetailDAO penjualanDetailDAO;
	private PiutangDAO piutangDAO;


	public PiutangDAO getPiutangDAO() {
		return piutangDAO;
	}

	public void setPiutangDAO(PiutangDAO piutangDAO) {
		this.piutangDAO = piutangDAO;
	}

	public PenjualanDAO getPenjualanDAO() {
		return penjualanDAO;
	}

	public void setPenjualanDAO(PenjualanDAO penjualanDAO) {
		this.penjualanDAO = penjualanDAO;
	}

	public PenjualanDetailDAO getPenjualanDetailDAO() {
		return penjualanDetailDAO;
	}

	public void setPenjualanDetailDAO(PenjualanDetailDAO penjualanDetailDAO) {
		this.penjualanDetailDAO = penjualanDetailDAO;
	}

	/**
	 * default Constructor
	 */
	public PenjualanServiceImpl() {
	}

	@Override
	public Penjualan getNewPenjualan() {
		return getPenjualanDAO().getNewPenjualan();
	}

	@Override
	public void saveOrUpdate(Penjualan penjualan) {
		getPenjualanDAO().saveOrUpdate(penjualan);
	}

	@Override
	public void delete(Penjualan penjualan) {
		getPenjualanDAO().delete(penjualan);
	}

	@Override
	public List<PenjualanDetail> getPenjualanDetailsByPenjualan(Penjualan penjualan) {
		
		getPenjualanDAO().refresh(penjualan);
		getPenjualanDAO().initialize(penjualan);
		
		List<PenjualanDetail> result = getPenjualanDetailDAO().getPenjualanDetailsByPenjualan(penjualan);

		return result;
	}

	@Override
	public int getCountPenjualanDetailsByPenjualan(Penjualan penjualan) {
		int result = getPenjualanDetailDAO().getCountPenjualanDetailsByPenjualan(penjualan);
		return result;
	}

	@Override
	public List<Penjualan> getAllPenjualans() {
		return getPenjualanDAO().getAllPenjualans();
	}

	@Override
	public void initialize(Penjualan proxy) {
		getPenjualanDAO().initialize(proxy);

	}

	@Override
	public Penjualan getPenjualanById(long id) {
		return getPenjualanDAO().getPenjualanById(id);
	}

	@Override
	public PenjualanDetail getNewPenjualanDetail() {
		return getPenjualanDetailDAO().getNewPenjualanDetail();
	}	

	@Override
	public void saveOrUpdate(PenjualanDetail penjualanDetail) {
		getPenjualanDetailDAO().saveOrUpdate(penjualanDetail);
	}

	@Override
	public void delete(PenjualanDetail penjualanDetail) {
		getPenjualanDetailDAO().delete(penjualanDetail);
	}

	@Override
	public BigDecimal getPenjualanSum(Penjualan penjualan) {
		return getPenjualanDAO().getPenjualanSum(penjualan);
	}

	@Override
	public int getCountAllPenjualans() {
		return getPenjualanDAO().getCountAllPenjualans();
	}

	@Override
	public int getCountAllPenjualanDetails() {
		return getPenjualanDetailDAO().getCountAllPenjualanDetails();
	}

	@Override
	public List<Piutang> getPiutangsByPenjualan(Penjualan penjualan) {
		getPenjualanDAO().refresh(penjualan);
		getPenjualanDAO().initialize(penjualan);
		
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
