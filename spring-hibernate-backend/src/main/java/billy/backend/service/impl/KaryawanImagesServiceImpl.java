package billy.backend.service.impl;

import org.apache.log4j.Logger;

import billy.backend.dao.KaryawanImagesDAO;
import billy.backend.model.Karyawan;
import billy.backend.model.KaryawanImages;
import billy.backend.service.KaryawanImagesService;

public class KaryawanImagesServiceImpl implements KaryawanImagesService {

  private static final Logger logger = Logger.getLogger(KaryawanImagesServiceImpl.class);
  private KaryawanImagesDAO karyawanImagesDAO;

  @Override
  public void delete(KaryawanImages entity) {
    getKaryawanImagesDAO().delete(entity);
  }


  @Override
  public KaryawanImages getKaryawanImagesByID(Long id) {
    return getKaryawanImagesDAO().getKaryawanImagesById(id);
  }

  @Override
  public KaryawanImages getKaryawanImagesByKaryawan(Karyawan entity) {
    return getKaryawanImagesDAO().getKaryawanImagesByKaryawan(entity);
  }

  public KaryawanImagesDAO getKaryawanImagesDAO() {
    return karyawanImagesDAO;
  }

  @Override
  public KaryawanImages getNewKaryawanImages() {
    return getKaryawanImagesDAO().getNewKaryawanImages();
  }

  @Override
  public void saveOrUpdate(KaryawanImages entity) {
    getKaryawanImagesDAO().saveOrUpdate(entity);
  }

  public void setKaryawanImagesDAO(KaryawanImagesDAO karyawanImagesDAO) {
    this.karyawanImagesDAO = karyawanImagesDAO;
  }

}
