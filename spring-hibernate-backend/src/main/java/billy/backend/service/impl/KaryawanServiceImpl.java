package billy.backend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import billy.backend.dao.KaryawanDAO;
import billy.backend.model.Karyawan;
import billy.backend.service.KaryawanService;
import de.forsthaus.backend.model.SecUser;

public class KaryawanServiceImpl implements KaryawanService {

  private static final Logger logger = Logger.getLogger(KaryawanServiceImpl.class);
  private KaryawanDAO karyawanDAO;

  @Override
  public void delete(Karyawan entity) {
    getKaryawanDAO().delete(entity);
  }

  @Override
  public List<Karyawan> getAllDivisiKaryawansByUserLogin(SecUser user) {
    List<Karyawan> listDivisi = new ArrayList<Karyawan>();
    if (user.getKaryawan() != null) {
      // under supervisor
      if (user.getKaryawan().getSupervisorDivisi() != null) {
        List<Karyawan> list =
            getKaryawanDAO().getKaryawansBySupervisorId(
                user.getKaryawan().getSupervisorDivisi().getId());
        // under divisi
        for (Karyawan karyawan : list) {
          if (karyawan.getJobType().getId() == new Long(2)) {// divisi
            listDivisi.add(karyawan);
          }
        }
      }
    } else {
      listDivisi = getKaryawansByJobTypeId(new Long(2));
    }
    Collections.sort(listDivisi, new Comparator<Karyawan>() {
      @Override
      public int compare(Karyawan obj1, Karyawan obj2) {
        return obj1.getNamaPanggilan().compareTo(obj2.getNamaPanggilan());
      }
    });
    return listDivisi;
  }

  @Override
  public List<Karyawan> getAllKaryawans() {
    return getKaryawanDAO().getAllKaryawans();
  }

  @Override
  public List<Karyawan> getAllSalesKaryawansByUserLogin(SecUser user) {
    List<Karyawan> listSales = new ArrayList<Karyawan>();
    if (user.getKaryawan() != null) {
      // under supervisor
      if (user.getKaryawan().getSupervisorDivisi() != null) {
        List<Karyawan> list =
            getKaryawanDAO().getKaryawansBySupervisorId(
                user.getKaryawan().getSupervisorDivisi().getId());
        // under divisi
        for (Karyawan karyawan : list) {
          if (karyawan.getJobType().getId() == new Long(2)) {// divisi
            List<Karyawan> listDivisi =
                getKaryawanDAO().getKaryawansBySupervisorId(karyawan.getId());
            for (Karyawan karyawanDivisi : listDivisi) {
              if (karyawanDivisi.getJobType().getId() == new Long(4)) {// sales
                listSales.add(karyawanDivisi);
              }
            }
            listSales.add(karyawan); // divisi = sales
          }
        }

      } else {
        listSales = getKaryawansByJobTypeId(new Long(4));
        List<Karyawan> listDivisi = getKaryawansByJobTypeId(new Long(2));// all divisi
        listSales.addAll(listDivisi);
      }
    }
    Collections.sort(listSales, new Comparator<Karyawan>() {
      @Override
      public int compare(Karyawan obj1, Karyawan obj2) {
        return obj1.getNamaPanggilan().compareTo(obj2.getNamaPanggilan());
      }
    });
    return listSales;
  }

  @Override
  public int getCountAllKaryawans() {
    return getKaryawanDAO().getCountAllKaryawans();
  }

  @Override
  public Karyawan getKaryawanByID(Long id) {
    return getKaryawanDAO().getKaryawanById(id);
  }

  @Override
  public Karyawan getKaryawanByKodeKaryawan(String string) {
    return getKaryawanDAO().getKaryawanByKodeKaryawan(string);
  }

  @Override
  public Karyawan getKaryawanByKtp(String string) {
    return getKaryawanDAO().getKaryawanByKtp(string);
  }

  public KaryawanDAO getKaryawanDAO() {
    return karyawanDAO;
  }

  @Override
  public List<Karyawan> getKaryawansByJobTypeId(Long id) {
    List<Karyawan> list = getKaryawanDAO().getKaryawansByJobTypeId(id);
    Collections.sort(list, new Comparator<Karyawan>() {
      @Override
      public int compare(Karyawan obj1, Karyawan obj2) {
        return obj1.getNamaPanggilan().compareTo(obj2.getNamaPanggilan());
      }
    });
    return list;
  }

  @Override
  public List<Karyawan> getKaryawansByNamaJobType(String string) {
    return getKaryawanDAO().getKaryawansByNamaJobType(string);
  }

  @Override
  public List<Karyawan> getKaryawansLikeKodeKaryawan(String string) {
    return getKaryawanDAO().getKaryawansLikeKodeKaryawan(string);
  }

  @Override
  public List<Karyawan> getKaryawansLikeKtp(String string) {
    return getKaryawanDAO().getKaryawansLikeKtp(string);
  }

  @Override
  public List<Karyawan> getKaryawansLikeNamaKtp(String string) {
    return getKaryawanDAO().getKaryawansLikeNamaKtp(string);
  }

  @Override
  public List<Karyawan> getKaryawansLikeNamaPanggilan(String string) {
    return getKaryawanDAO().getKaryawansLikeNamaPanggilan(string);
  }

  @Override
  public Karyawan getNewKaryawan() {
    return getKaryawanDAO().getNewKaryawan();
  }

  @Override
  public void saveOrUpdate(Karyawan entity) {
    getKaryawanDAO().saveOrUpdate(entity);
  }

  public void setKaryawanDAO(KaryawanDAO karyawanDAO) {
    this.karyawanDAO = karyawanDAO;
  }

}
