package billy.backend.dao;

import java.util.List;

import billy.backend.model.Karyawan;

public interface KaryawanDAO {

  public void delete(Karyawan entity);

  public void deleteKaryawanById(long id);

  public List<Karyawan> getAllKaryawans();

  public int getCountAllKaryawans();

  public Karyawan getKaryawanById(Long id);

  public Karyawan getKaryawanByKodeKaryawan(String string);

  public Karyawan getKaryawanByKtp(String string);

  public List<Karyawan> getKaryawansByJobTypeId(Long id);

  public List<Karyawan> getKaryawansByNamaJobType(String string);

  public List<Karyawan> getKaryawansBySupervisorId(Long id);

  public List<Karyawan> getKaryawansLikeKodeKaryawan(String string);

  public List<Karyawan> getKaryawansLikeKtp(String string);

  public List<Karyawan> getKaryawansLikeNamaKtp(String string);

  public List<Karyawan> getKaryawansLikeNamaPanggilan(String string);

  public Karyawan getNewKaryawan();

  public void save(Karyawan entity);

  public void saveOrUpdate(Karyawan entity);

}
