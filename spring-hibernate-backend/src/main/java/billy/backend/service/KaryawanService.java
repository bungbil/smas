package billy.backend.service;

import java.util.List;

import billy.backend.model.Karyawan;
import de.forsthaus.backend.model.SecUser;

public interface KaryawanService {

  public void delete(Karyawan entity);

  public List<Karyawan> getAllDivisiKaryawansByUserLogin(SecUser user);

  public List<Karyawan> getAllKaryawans();

  public List<Karyawan> getAllSalesKaryawansByUserLogin(SecUser user);

  public int getCountAllKaryawans();

  public Karyawan getKaryawanByID(Long id);

  public Karyawan getKaryawanByKodeKaryawan(String string);

  public Karyawan getKaryawanByKtp(String string);

  public List<Karyawan> getKaryawansByJobTypeId(Long id);

  public List<Karyawan> getKaryawansByNamaJobType(String string);

  public List<Karyawan> getKaryawansLikeKodeKaryawan(String string);

  public List<Karyawan> getKaryawansLikeKtp(String string);

  public List<Karyawan> getKaryawansLikeNamaKtp(String string);


  public List<Karyawan> getKaryawansLikeNamaPanggilan(String string);

  public Karyawan getNewKaryawan();

  public void saveOrUpdate(Karyawan entity);

}
