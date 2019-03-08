package billy.webui.transaction.penjualan;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import billy.backend.model.Barang;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Wilayah;
import billy.backend.service.BarangService;
import billy.backend.service.KaryawanService;
import billy.backend.service.PenjualanService;
import billy.backend.service.StatusService;
import billy.backend.service.WilayahService;
import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;

public class PenjualanUploadCtrl extends GFCBaseCtrl implements Serializable {


  private static final long serialVersionUID = -8352659530536077973L;
  private static final Logger logger = Logger.getLogger(PenjualanUploadCtrl.class);
  private static final int NO_COLUMN = 0;
  private static final int NO_FAKTUR_COLUMN = 1;
  private static final int TANGGAL_PENJUALAN_COLUMN = 2;
  private static final int BULAN_PENJUALAN_COLUMN = 3;
  private static final int NAMA_CUSTOMER_COLUMN = 4;
  private static final int TELEPON_CUSTOMER_COLUMN = 5;
  private static final int ALAMAT_CUSTOMER_COLUMN = 6;
  private static final int KECAMATAN_COLUMN = 7;
  private static final int KABUPATEN_COLUMN = 8;
  private static final int KODE_BARANG_COLUMN = 9;
  private static final int NAMA_BARANG_COLUMN = 10;
  private static final int QTY_COLUMN = 11;
  private static final int INTERVAL_KREDIT_COLUMN = 12;
  private static final int HARGA_BARANG_COLUMN = 13;
  private static final int DOWN_PAYMENT_COLUMN = 14;
  private static final int SISA_KREDIT_COLUMN = 15;
  private static final int TANGGAL_TAGIHAN_COLUMN = 16;
  private static final int KODE_SALES1_COLUMN = 17;
  private static final int SALES1_COLUMN = 18;
  private static final int KODE_SALES2_COLUMN = 19;
  private static final int SALES2_COLUMN = 20;
  private static final int DIVISI_COLUMN = 21;
  private static final int LEADER_COLUMN = 22;
  private static final int MANDIRI_COLUMN = 23;
  private static final int KODE_WILAYAH_COLUMN = 24;
  private static final int WILAYAH_COLUMN = 25;
  private static final int REMARK_COLUMN = 26;

  protected Window windowPenjualanUpload; // autowired

  protected Borderlayout borderlayout_PenjualanUpload; // autowired

  protected Button btnDownload; // autowired
  protected Button btnUpload; // autowired
  // Databinding
  protected transient AnnotateDataBinder binder;

  // ServiceDAOs / Domain Classes
  private transient PenjualanService penjualanService;
  private transient WilayahService wilayahService;
  private transient KaryawanService karyawanService;
  private transient BarangService barangService;
  private transient StatusService statusService;

  DecimalFormat df = new DecimalFormat("#,###");

  /**
   * default constructor.<br>
   */
  public PenjualanUploadCtrl() {
    super();
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++ //
  // +++++++++++++++ Component Events ++++++++++++++++ //
  // +++++++++++++++++++++++++++++++++++++++++++++++++ //

  @Override
  public void doAfterCompose(Component window) throws Exception {
    super.doAfterCompose(window);

    this.self.setAttribute("controller", this, false);


  }

  public void doFitSize(Event event) {

    final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
    int height =
        ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
            .intValue();
    height = height - menuOffset;
    final int maxListBoxHeight = height - 152;
    borderlayout_PenjualanUpload.setHeight(String.valueOf(maxListBoxHeight) + "px");

    windowPenjualanUpload.invalidate();
  }

  public BarangService getBarangService() {
    return barangService;
  }

  public AnnotateDataBinder getBinder() {
    return this.binder;
  }

  public KaryawanService getKaryawanService() {
    return karyawanService;
  }

  /* Master BEANS */
  public PenjualanService getPenjualanService() {
    return this.penjualanService;
  }

  public StatusService getStatusService() {
    return statusService;
  }


  public WilayahService getWilayahService() {
    return wilayahService;
  }

  public void onClick$btnDownload(Event event) throws Exception {
    InputStream input = getClass().getResourceAsStream("penjualanTemplate.xls");
    Filedownload.save(input, "application/vnd.ms-excel", "penjualanTemplate.xls");
  }

  public void onClick$btnUpload(Event event) throws Exception {


  }

  /**
   * Automatically called method from zk.
   * 
   * @param event
   * @throws Exception
   */
  public void onCreate$windowPenjualanUpload(Event event) throws Exception {
    binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

    binder.loadAll();

    doFitSize(event);
  }

  public void onUpload$btnUpload(UploadEvent event) {
    System.out.println("this is upload");
    Media media = event.getMedia();
    if (media != null) {
      String filename = media.getName();
      if (filename.indexOf(".xls") == -1) {
        alert(filename + " bukan file excel .xls");
      } else {
        processReadExcel(media);
      }
    }
  }

  private void processReadExcel(Media media) {

    try {
      XSSFWorkbook workbook = new XSSFWorkbook(media.getStreamData());
      XSSFSheet hssfSheet = workbook.getSheet("Sheet1");

      int lastRowIndex = hssfSheet.getPhysicalNumberOfRows();
      for (int i = 5; i < lastRowIndex; i++) {
        XSSFRow row = hssfSheet.getRow(i);
        XSSFCell cellNo = row.getCell(NO_COLUMN);
        logger.info("cellNo : " + cellNo);

        XSSFCell cellNoFaktur = row.getCell(NO_FAKTUR_COLUMN);
        String noFaktur = cellNoFaktur.getStringCellValue().replace(" ", "");
        logger.info("cellNoFaktur : " + noFaktur);
        if (!StringUtils.isBlank(noFaktur)) {

          Penjualan penjualan = penjualanService.getPenjualanByNoFaktur(noFaktur);
          if (penjualan == null) {
            penjualan = penjualanService.getNewPenjualan();
            penjualan.setNoFaktur(noFaktur);

            XSSFCell cellTglPenjualan = row.getCell(TANGGAL_PENJUALAN_COLUMN);
            logger.info("cellTglPenjualan : " + cellTglPenjualan);
            penjualan.setTglPenjualan(cellTglPenjualan.getDateCellValue());

            XSSFCell cellBulanPenjualan = row.getCell(BULAN_PENJUALAN_COLUMN);
            logger.info("cellBulanPenjualan : " + cellBulanPenjualan);

            XSSFCell cellNamaCustomer = row.getCell(NAMA_CUSTOMER_COLUMN);
            logger.info("cellNamaCustomer : " + cellNamaCustomer);
            penjualan.setNamaPelanggan(cellNamaCustomer.toString());

            XSSFCell cellTelepon = row.getCell(TELEPON_CUSTOMER_COLUMN);
            logger.info("cellTelepon : " + cellTelepon);
            penjualan.setTelepon(cellTelepon.getStringCellValue());

            XSSFCell cellAlamatCustomer = row.getCell(ALAMAT_CUSTOMER_COLUMN);
            logger.info("cellAlamatCustomer : " + cellAlamatCustomer);
            XSSFCell cellKecamatan = row.getCell(KECAMATAN_COLUMN);
            logger.info("cellKecamatan : " + cellKecamatan);
            XSSFCell cellKabupaten = row.getCell(KABUPATEN_COLUMN);
            logger.info("cellKabupaten : " + cellKabupaten);
            penjualan.setAlamat(cellAlamatCustomer.getStringCellValue() + ", "
                + cellKecamatan.getStringCellValue() + ", " + cellKabupaten.getStringCellValue());

            XSSFCell cellIntervalKredit = row.getCell(INTERVAL_KREDIT_COLUMN);
            logger.info("cellIntervalKredit : " + cellIntervalKredit);
            penjualan.setIntervalKredit((int) cellIntervalKredit.getNumericCellValue());

            PenjualanDetail item = penjualanService.getNewPenjualanDetail();
            item.setPenjualan(penjualan);

            XSSFCell cellKodeBarang = row.getCell(KODE_BARANG_COLUMN);
            logger.info("cellKodeBarang : " + cellKodeBarang);
            XSSFCell cellBarang = row.getCell(NAMA_BARANG_COLUMN);
            logger.info("cellBarang : " + cellBarang);
            Barang barang =
                barangService.getBarangByKodeBarang(cellKodeBarang.getStringCellValue());
            if (barang != null) {
              item.setBarang(barang);
              item.setKomisiSales(getBarangService().getKomisiSalesByIntervalKredit(barang,
                  penjualan.getIntervalKredit()));
              item.setTabunganSales(getBarangService().getTabunganSalesByIntervalKredit(barang,
                  penjualan.getIntervalKredit()));
              item.setOprDivisi(barang.getDivisiOpr());
              item.setOrDivisi(barang.getDivisiOr());
              item.setBonus(barang.isBonus());
            } else {
              // kode barang tidak ditemukan
            }

            XSSFCell cellQty = row.getCell(QTY_COLUMN);
            logger.info("cellQty : " + cellQty);
            item.setQty((int) cellQty.getNumericCellValue());

            XSSFCell cellHarga = row.getCell(HARGA_BARANG_COLUMN);
            logger.info("cellHarga : " + cellHarga.getNumericCellValue());
            item.setHarga(BigDecimal.valueOf(cellHarga.getNumericCellValue()));
            XSSFCell cellDownPayment = row.getCell(DOWN_PAYMENT_COLUMN);
            logger.info("cellDownPayment : " + cellDownPayment);
            item.setDownPayment(BigDecimal.valueOf(cellDownPayment.getNumericCellValue()));
            penjualan.setDownPayment(BigDecimal.valueOf(cellDownPayment.getNumericCellValue()));

            BigDecimal total = item.getHarga().multiply(new BigDecimal(item.getQty()));
            item.setTotal(total);
            penjualan.setTotal(total);

            XSSFCell cellSisaKredit = row.getCell(SISA_KREDIT_COLUMN);
            logger.info("cellSisaKredit : " + cellSisaKredit.getNumericCellValue());
            penjualan.setPiutang(BigDecimal.valueOf(cellSisaKredit.getNumericCellValue()));

            XSSFCell cellTglAngsuran2 = row.getCell(TANGGAL_TAGIHAN_COLUMN);
            logger.info("cellTglAngsuran2 : " + cellTglAngsuran2.getDateCellValue());
            penjualan.setTglAngsuran2(cellTglAngsuran2.getDateCellValue());


            XSSFCell cellKodeSales1 = row.getCell(KODE_SALES1_COLUMN);
            logger.info("cellKodeSales1 : " + cellKodeSales1);
            XSSFCell cellSales1 = row.getCell(SALES1_COLUMN);
            logger.info("cellSales1 : " + cellSales1);

            Karyawan sales1 =
                karyawanService.getKaryawanByKodeKaryawan(cellKodeSales1.getStringCellValue());
            if (sales1 != null) {
              penjualan.setSales1(sales1);
              if (sales1.getSupervisorDivisi() != null) {
                penjualan.setDivisi(sales1.getSupervisorDivisi());
              }
            } else {
              // kode sales1 tidak ditemukan
            }

            XSSFCell cellKodeSales2 = row.getCell(KODE_SALES2_COLUMN);
            logger.info("cellKodeSales2 : " + cellKodeSales2);
            XSSFCell cellSales2 = row.getCell(SALES2_COLUMN);
            logger.info("cellSales2 : " + cellSales2);
            Karyawan sales2 =
                karyawanService.getKaryawanByKodeKaryawan(cellKodeSales2.getStringCellValue());
            if (sales2 != null) {
              penjualan.setSales2(sales2);
            } else {
              // kode sales2 tidak ditemukan
            }

            XSSFCell cellDivisi = row.getCell(DIVISI_COLUMN);
            logger.info("cellDivisi : " + cellDivisi);
            XSSFCell cellLeader = row.getCell(LEADER_COLUMN);
            logger.info("cellLeader : " + cellLeader);
            XSSFCell cellMandiri = row.getCell(MANDIRI_COLUMN);
            logger.info("cellMandiri : " + cellMandiri);
            penjualan.setMandiri(cellMandiri.getStringCellValue());

            XSSFCell cellKodeWilayah = row.getCell(KODE_WILAYAH_COLUMN);
            logger.info("cellKodeWilayah : " + cellKodeWilayah);
            XSSFCell cellWilayah = row.getCell(WILAYAH_COLUMN);
            logger.info("cellWilayah : " + cellWilayah);
            Wilayah wilayah =
                wilayahService.getWilayahByKodeWilayah(cellKodeWilayah.getStringCellValue());
            if (wilayah != null) {
              penjualan.setWilayah(wilayah);
            } else {
              // kode wilayah tidak ditemukan
            }

            XSSFCell cellRemark = row.getCell(REMARK_COLUMN);
            logger.info("cellRemark : " + cellRemark);
            penjualan.setRemark(cellRemark.getStringCellValue());
          } else {

            logger.info("update penjualan sudah ada dengan ID : " + penjualan.getId());
            // update barang

          }
        } else {
          logger.info("row tidak ada no faktur");
          // ZksampleMessageUtils.showErrorMessage("");
        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }

  public void setBarangService(BarangService barangService) {
    this.barangService = barangService;
  }


  public void setKaryawanService(KaryawanService karyawanService) {
    this.karyawanService = karyawanService;
  }


  /* SERVICES */
  public void setPenjualanService(PenjualanService penjualanService) {
    this.penjualanService = penjualanService;
  }


  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  public void setWilayahService(WilayahService wilayahService) {
    this.wilayahService = wilayahService;
  }


}
