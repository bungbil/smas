package billy.backend.service;

import java.util.List;

import billy.backend.model.Karyawan;
public interface KaryawanService {

	public Karyawan getNewKaryawan();

	public int getCountAllKaryawans();

	public Karyawan getKaryawanByID(Long id);

	public List<Karyawan> getAllKaryawans();

	public List<Karyawan> getKaryawansLikeKodeKaryawan(String string);
	
	public List<Karyawan> getKaryawansLikeNamaKaryawan(String string);
	
	public List<Karyawan> getKaryawansByJobTypeId(Long id);
	
	public List<Karyawan> getKaryawansByNamaJobType(String string);

	public void saveOrUpdate(Karyawan entity);

	public void delete(Karyawan entity);

}
