package billy.backend.service;

import java.util.List;

import de.forsthaus.backend.model.SecUser;

import billy.backend.model.Karyawan;
public interface KaryawanService {

	public Karyawan getNewKaryawan();

	public int getCountAllKaryawans();

	public Karyawan getKaryawanByID(Long id);
	
	public Karyawan getKaryawanByKtp(String string);
	public Karyawan getKaryawanByKodeKaryawan(String string);

	public List<Karyawan> getAllKaryawans();
	public List<Karyawan> getAllSalesKaryawansByUserLogin(SecUser user);
	public List<Karyawan> getKaryawansLikeKodeKaryawan(String string);
	
	public List<Karyawan> getKaryawansLikeNamaPanggilan(String string);
	
	public List<Karyawan> getKaryawansLikeNamaKtp(String string);
	
	public List<Karyawan> getKaryawansLikeKtp(String string);
	
	public List<Karyawan> getKaryawansByJobTypeId(Long id);
	
	public List<Karyawan> getKaryawansByNamaJobType(String string);

	public void saveOrUpdate(Karyawan entity);

	public void delete(Karyawan entity);

}
