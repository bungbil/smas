package billy.backend.dao;

import java.util.Date;
import java.util.List;

import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.Piutang;

public interface PiutangDAO {

  public void delete(Piutang entity);

  public void deletePiutangsByPenjualan(Penjualan obj);

  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByDivisiAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKaryawanAndRangeDateTglJatuhTempo(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKaryawanAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawa(Karyawan obj, Date startDate,
      Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglBawaBelumBayar(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaran(Karyawan obj,
      Date startDate, Date endDate);

  public List<Piutang> getAllPiutangsByKolektorAndRangeDateTglPembayaranAndDiskon(Karyawan obj,
      Date startDate, Date endDate);

  public int getCountAllPiutangs();

  public int getCountPiutangsByPenjualan(Penjualan obj);

  public Piutang getNewPiutang();

  public Piutang getPiutangById(long id);

  public Piutang getPiutangByNoFaktur(String data);

  public Piutang getPiutangByNoFakturAndPembayaranKe(String data, int data2);

  public Piutang getPiutangByNoKuitansi(String data);

  public List<Piutang> getPiutangsByPenjualan(Penjualan obj);

  public List<Piutang> getPiutangsTukarBarangByPenjualan(Penjualan entity);

  public void save(Piutang entity);

  public void saveOrUpdate(Piutang entity);

}
