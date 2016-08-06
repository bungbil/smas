package billy.backend.dao;

import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.KaryawanImages;

public interface KaryawanImagesDAO {

  public void delete(KaryawanImages entity);

  public void deleteKaryawanImagesById(long id);

  public List<KaryawanImages> getAllKaryawanImages();

  public int getCountAllKaryawanImages();

  public KaryawanImages getKaryawanImagesById(Long id);

  public KaryawanImages getKaryawanImagesByKaryawan(Karyawan karyawan);

  public KaryawanImages getNewKaryawanImages();

  public void save(KaryawanImages entity);

  public void saveOrUpdate(KaryawanImages entity);

}
