package billy.backend.dao;

import java.util.List;

import billy.backend.model.Wilayah;

public interface WilayahDAO {

	public Wilayah getNewWilayah();

	public Wilayah getWilayahById(Long id);

	public Wilayah getWilayahByKodeWilayah(String string);

	public List<Wilayah> getAllWilayahs();

	public int getCountAllWilayahs();

	public List<Wilayah> getWilayahsLikeKodeWilayah(String string);

	public List<Wilayah> getWilayahsLikeNamaWilayah(String string);	

	public void deleteWilayahById(long id);

	public void saveOrUpdate(Wilayah entity);

	public void delete(Wilayah entity);

	public void save(Wilayah entity);

}
