package billy.backend.service;

import java.util.List;

import billy.backend.model.Mandiri;

public interface MandiriService {

  public void delete(Mandiri entity);

  public List<Mandiri> getAllMandiris();

  public int getCountAllMandiris();

  public Mandiri getMandiriByID(Long id);

  public List<Mandiri> getMandirisLikeDeskripsiMandiri(String string);

  public List<Mandiri> getMandirisLikeKodeMandiri(String string);

  public Mandiri getNewMandiri();

  public void saveOrUpdate(Mandiri entity);

}
