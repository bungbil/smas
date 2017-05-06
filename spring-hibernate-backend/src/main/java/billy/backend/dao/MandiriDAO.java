package billy.backend.dao;

import java.util.List;

import billy.backend.model.Mandiri;

public interface MandiriDAO {

  public void delete(Mandiri entity);

  public void deleteMandiriById(long id);

  public List<Mandiri> getAllMandiris();

  public int getCountAllMandiris();

  public Mandiri getMandiriById(Long id);

  public Mandiri getMandiriByKodeMandiri(String string);

  public List<Mandiri> getMandirisLikeDeskripsiMandiri(String string);

  public List<Mandiri> getMandirisLikeKodeMandiri(String string);

  public Mandiri getNewMandiri();

  public void save(Mandiri entity);

  public void saveOrUpdate(Mandiri entity);

}
