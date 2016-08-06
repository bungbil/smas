package billy.backend.service;

import billy.backend.model.Karyawan;
import billy.backend.model.KaryawanImages;

public interface KaryawanImagesService {

  public void delete(KaryawanImages entity);

  public KaryawanImages getKaryawanImagesByID(Long id);

  public KaryawanImages getKaryawanImagesByKaryawan(Karyawan entity);

  public KaryawanImages getNewKaryawanImages();

  public void saveOrUpdate(KaryawanImages entity);

}
