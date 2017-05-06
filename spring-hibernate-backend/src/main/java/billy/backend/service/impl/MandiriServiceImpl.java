package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.MandiriDAO;
import billy.backend.model.Mandiri;
import billy.backend.service.MandiriService;

public class MandiriServiceImpl implements MandiriService {

  private MandiriDAO mandiriDAO;

  @Override
  public void delete(Mandiri entity) {
    getMandiriDAO().delete(entity);
  }

  @Override
  public List<Mandiri> getAllMandiris() {
    return getMandiriDAO().getAllMandiris();
  }

  @Override
  public int getCountAllMandiris() {
    return getMandiriDAO().getCountAllMandiris();
  }

  @Override
  public Mandiri getMandiriByID(Long id) {
    return getMandiriDAO().getMandiriById(id);
  }

  public MandiriDAO getMandiriDAO() {
    return mandiriDAO;
  }

  @Override
  public List<Mandiri> getMandirisLikeDeskripsiMandiri(String string) {
    return getMandiriDAO().getMandirisLikeDeskripsiMandiri(string);
  }

  @Override
  public List<Mandiri> getMandirisLikeKodeMandiri(String string) {
    return getMandiriDAO().getMandirisLikeKodeMandiri(string);
  }

  @Override
  public Mandiri getNewMandiri() {
    return getMandiriDAO().getNewMandiri();
  }

  @Override
  public void saveOrUpdate(Mandiri entity) {
    getMandiriDAO().saveOrUpdate(entity);
  }

  public void setMandiriDAO(MandiriDAO mandiriDAO) {
    this.mandiriDAO = mandiriDAO;
  }

}
