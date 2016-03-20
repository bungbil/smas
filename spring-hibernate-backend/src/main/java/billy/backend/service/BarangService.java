package billy.backend.service;

import java.math.BigDecimal;
import java.util.List;

import billy.backend.model.Barang;
import billy.backend.model.Wilayah;

public interface BarangService {

	public Barang getNewBarang();

	public int getCountAllBarangs();

	public Barang getBarangByID(Long id);
	
	public Barang getBarangByKodeBarang(String string);
	
	public List<Barang> getAllBarangs();
	public List<Barang> getAllBarangsByWilayah(Wilayah obj);

	public List<Barang> getBarangsLikeKodeBarang(String string);
	
	public List<Barang> getBarangsLikeNamaBarang(String string);

	public void saveOrUpdate(Barang entity);

	public void delete(Barang entity);
	
	public BigDecimal getHargaBarangByIntervalKredit(Barang obj,int interval);
	public BigDecimal getCicilanPerBulanByIntervalKredit(Barang obj,int interval);
	public BigDecimal getKomisiSalesByIntervalKredit(Barang obj,int interval);
	public BigDecimal getTabunganSalesByIntervalKredit(Barang obj,int interval);
}
