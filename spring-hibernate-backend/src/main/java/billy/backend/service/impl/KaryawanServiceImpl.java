package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.KaryawanDAO;
import billy.backend.model.Karyawan;
import billy.backend.service.KaryawanService;

public class KaryawanServiceImpl implements KaryawanService {

	private KaryawanDAO karyawanDAO;

	public KaryawanDAO getKaryawanDAO() {
		return karyawanDAO;
	}

	public void setKaryawanDAO(KaryawanDAO karyawanDAO) {
		this.karyawanDAO = karyawanDAO;
	}

	@Override
	public Karyawan getNewKaryawan() {
		return getKaryawanDAO().getNewKaryawan();
	}

	@Override
	public Karyawan getKaryawanByID(Long id) {
		return getKaryawanDAO().getKaryawanById(id);
	}

	@Override
	public List<Karyawan> getAllKaryawans() {
		return getKaryawanDAO().getAllKaryawans();
	}

	@Override
	public void saveOrUpdate(Karyawan entity) {
		getKaryawanDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(Karyawan entity) {
		getKaryawanDAO().delete(entity);
	}

	@Override
	public List<Karyawan> getKaryawansLikeKodeKaryawan(String string) {
		return getKaryawanDAO().getKaryawansLikeKodeKaryawan(string);
	}

	@Override
	public List<Karyawan> getKaryawansLikeNamaKaryawan(String string) {
		return getKaryawanDAO().getKaryawansLikeNamaKaryawan(string);
	}

	@Override
	public int getCountAllKaryawans() {
		return getKaryawanDAO().getCountAllKaryawans();
	}

	@Override
	public List<Karyawan> getKaryawansByJobTypeId(Long id) {
		return getKaryawanDAO().getKaryawansByJobTypeId(id);
	}

	@Override
	public List<Karyawan> getKaryawansByNamaJobType(String string) {
		return getKaryawanDAO().getKaryawansByNamaJobType(string);
	}

}