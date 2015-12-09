package billy.backend.dao;

import java.util.List;

import billy.backend.model.Karyawan;

public interface KaryawanDAO {

	public Karyawan getNewKaryawan();

	public Karyawan getKaryawanById(Long id);

	public Karyawan getKaryawanByKodeKaryawan(String string);

	public List<Karyawan> getAllKaryawans();

	public int getCountAllKaryawans();

	public List<Karyawan> getKaryawansLikeKodeKaryawan(String string);

	public List<Karyawan> getKaryawansLikeNamaKaryawan(String string);
	
	public List<Karyawan> getKaryawansByJobTypeId(Long id);
	
	public List<Karyawan> getKaryawansByNamaJobType(String string);

	public void deleteKaryawanById(long id);

	public void saveOrUpdate(Karyawan entity);

	public void delete(Karyawan entity);

	public void save(Karyawan entity);

}
