package billy.webui.report.piutangsales.report;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ExpressionHelper;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import billy.backend.model.CompanyProfile;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.model.Piutang;
import billy.backend.model.Status;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.backend.service.PiutangService;
import billy.backend.service.StatusService;
import billy.webui.report.piutangsales.model.PiutangSales;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PiutangSalesDJReport extends Window implements Serializable {

  /**
   * EventListener for closing the Report Window.<br>
   * 
   * @author sge
   */
  public final class OnCloseReportEventListener implements EventListener {
    @Override
    public void onEvent(Event event) throws Exception {
      closeReportWindow();
    }
  }

  private static final long serialVersionUID = 1L;
  private Iframe iFrame;
  private ByteArrayOutputStream output;
  private InputStream mediais;
  private Double totalQty = 0.0;
  private AMedia amedia;
  private static final Logger logger = Logger.getLogger(PiutangSalesDJReport.class);
  DecimalFormat df = new DecimalFormat("#,###");
  private static final String title = "LAPORAN PIUTANG SALES";
  private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("ddMMyyyy");

  public PiutangSalesDJReport(Component parent, Karyawan karyawan, Date startDate, Date endDate,
      List<Penjualan> listPenjualan) throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(karyawan, startDate, endDate, listPenjualan);
    } catch (final Exception e) {
      ZksampleMessageUtils.showErrorMessage(e.toString());
    }
  }

  private void callReportWindow(AMedia aMedia, String format) {
    boolean modal = true;

    setTitle(title);
    setId("ReportWindowPiutangSales");
    setVisible(true);
    setMaximizable(true);
    setMinimizable(true);
    setSizable(true);
    setClosable(true);
    setHeight("100%");
    setWidth("80%");
    addEventListener("onClose", new OnCloseReportEventListener());

    iFrame = new Iframe();
    iFrame.setId("jasperReportId");
    iFrame.setWidth("100%");
    iFrame.setHeight("100%");
    iFrame.setContent(aMedia);
    iFrame.setParent(this);

    if (modal == true) {
      try {
        doModal();
      } catch (final SuspendNotAllowedException e) {
        throw new RuntimeException(e);
      } catch (final InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private boolean checkAngsuran2Lunas(Status statusLunas, PiutangService piutangService,
      Penjualan penjualan) {
    // TODO Auto-generated method stub
    try {
      if (penjualan.getMetodePembayaran().equals("Cash")) {
        return true;
      } else {
        List<Piutang> piutanglist = piutangService.getPiutangsByPenjualan(penjualan);
        BigDecimal totalAngsuran = BigDecimal.ZERO;
        for (Piutang data : piutanglist) {
          if (data.getPembayaran() != null) {
            totalAngsuran = totalAngsuran.add(data.getPembayaran());
          }
        }
        if (totalAngsuran.compareTo(penjualan.getDownPayment()) == 1) {
          return true;
        } else if (totalAngsuran.compareTo(penjualan.getDownPayment()) == 0) {
          return true;
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.info("catch checkAngsurang2Lunas");
    }
    return false;
  }

  private boolean checkPiutangFinalStatusTarikBarang(Status statusTarikBarang,
      PiutangService piutangService, Penjualan penjualan) {
    // TODO Auto-generated method stub
    try {
      List<Piutang> piutanglist = piutangService.getPiutangsByPenjualan(penjualan);
      for (Piutang data : piutanglist) {
        if (data.getStatusFinal() != null && statusTarikBarang.equals(data.getStatusFinal())) {
          return true;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.info("catch checkPiutangFinalStatusTarikBarang");
    }
    return false;
  }

  /**
   * We must clear something to prevent errors or problems <br>
   * by opening the report a few times. <br>
   * 
   * @throws IOException
   */
  private void closeReportWindow() throws IOException {

    // TODO check this
    try {
      amedia.getStreamData().close();
      output.close();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }

    onClose();

  }

  public void doPrint(Karyawan karyawan, Date startDate, Date endDate, List<Penjualan> listPenjualan)
      throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

    List<PiutangSales> resultList = generateData(karyawan, listPenjualan);
    /**
     * STYLES
     */
    // Styles: Title
    Style titleStyle = new Style();
    titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
    Font titleFont = Font.VERDANA_BIG_BOLD;
    titleFont.setUnderline(true);
    titleStyle.setFont(titleFont);

    // Styles: Subtitle
    Style subtitleStyle = new Style();
    subtitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
    subtitleStyle.setFont(Font.VERDANA_MEDIUM_BOLD);

    // ColumnHeader Style Text (left-align)
    final Style columnHeaderStyleText = new Style();
    columnHeaderStyleText.setFont(Font.VERDANA_SMALL_BOLD);
    columnHeaderStyleText.setHorizontalAlign(HorizontalAlign.LEFT);
    columnHeaderStyleText.setBorderBottom(Border.PEN_1_POINT());

    // ColumnHeader Style Text (right-align)
    Style columnHeaderStyleNumber = new Style();
    columnHeaderStyleNumber.setFont(Font.VERDANA_SMALL_BOLD);
    columnHeaderStyleNumber.setHorizontalAlign(HorizontalAlign.RIGHT);
    columnHeaderStyleNumber.setBorderBottom(Border.PEN_1_POINT());

    // Footer Style (center-align)
    Style footerStyle = new Style();
    // footerStyle.setFont(Font.VERDANA_SMALL);
    // footerStyle.getFont().setFontSize(8);
    footerStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
    footerStyle.setBorderTop(Border.PEN_1_POINT());

    // Rows content Style (left-align)
    final Style columnDetailStyleText = new Style();
    columnDetailStyleText.setFont(Font.VERDANA_SMALL);
    columnDetailStyleText.setHorizontalAlign(HorizontalAlign.LEFT);

    // Rows content Style (right-align)
    Style columnDetailStyleNumbers = new Style();
    columnDetailStyleNumbers.setFont(Font.VERDANA_SMALL);
    columnDetailStyleNumbers.setHorizontalAlign(HorizontalAlign.RIGHT);

    // TotalSum (left-right)
    Style footerStyleTotalSumValue = new Style();
    footerStyleTotalSumValue.setFont(Font.VERDANA_SMALL_BOLD);
    footerStyleTotalSumValue.setHorizontalAlign(HorizontalAlign.RIGHT);
    footerStyleTotalSumValue.setBorderTop(Border.PEN_1_POINT());

    DynamicReportBuilder drb = new DynamicReportBuilder();
    DynamicReport dr;

    // Sets the Report Columns, header, Title, Groups, Etc Formats
    // DynamicJasper documentation
    drb.setTitle(title);
    // drb.setSubtitle("Tanggal Penjualan : "+ZksampleDateFormat.getDateFormater().format(startDate)+" - "+ZksampleDateFormat.getDateFormater().format(endDate));
    // drb.setSubtitleStyle(subtitleStyle);

    drb.setHeaderHeight(20);
    drb.setDetailHeight(15);
    drb.setFooterVariablesHeight(10);
    drb.setMargins(20, 20, 30, 15);

    drb.setDefaultStyles(titleStyle, subtitleStyle, columnHeaderStyleText, columnDetailStyleText);
    drb.setPrintBackgroundOnOddRows(false);

    /**
     * Adding many autotexts in the same position (header/footer and aligment) makes them to be one
     * on top of the other
     */
    Style atStyle = new StyleBuilder(true).setFont(Font.COMIC_SANS_SMALL).build();

    AutoText created =
        new AutoText(Labels.getLabel("common.Created") + ": "
            + ZksampleDateFormat.getDateTimeFormater().format(new Date()),
            AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
    created.setWidth(new Integer(120));
    created.setStyle(atStyle);
    drb.addAutoText(created);

    AutoText autoText =
        new AutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_HEADER,
            HorizontalBandAlignment.RIGHT);
    autoText.setWidth(new Integer(20));
    autoText.setStyle(atStyle);
    drb.addAutoText(autoText);

    CompanyProfileService as = (CompanyProfileService) SpringUtil.getBean("companyProfileService");
    List<CompanyProfile> company = as.getAllCompanyProfiles();


    AutoText atCompanyHeader =
        new AutoText(company.get(0).getCompanyName(), AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    atCompanyHeader.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    atCompanyHeader.setWidth(new Integer(700));
    // AutoText address =
    // new AutoText(company.get(0).getAddress(), AutoText.POSITION_HEADER,
    // HorizontalBandAlignment.LEFT);
    // address.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    // address.setWidth(new Integer(700));
    AutoText sales =
        new AutoText("Sales : " + karyawan.getKodeKaryawan() + "-" + karyawan.getNamaPanggilan()
            + "(" + karyawan.getSupervisorDivisi().getInisialDivisi() + ")",
            AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    sales.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    sales.setWidth(new Integer(700));
    AutoText tanggal =
        new AutoText("Tanggal Penjualan : "
            + ZksampleDateFormat.getDateFormater().format(startDate) + " - "
            + ZksampleDateFormat.getDateFormater().format(endDate), AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    tanggal.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    tanggal.setWidth(new Integer(700));
    AutoText emptyLine = new AutoText("", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    emptyLine.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    drb.addAutoText(atCompanyHeader).addAutoText(sales).addAutoText(tanggal);


    /**
     * Columns Definitions. A new ColumnBuilder instance for each column.
     */

    AbstractColumn colNomorFaktur =
        ColumnBuilder.getNew().setColumnProperty("nomorFaktur", String.class.getName()).build();
    colNomorFaktur.setTitle("Nomor Faktur");
    colNomorFaktur.setWidth(60);
    colNomorFaktur.setHeaderStyle(columnHeaderStyleText);
    colNomorFaktur.setStyle(columnDetailStyleText);

    AbstractColumn colPelanggan =
        ColumnBuilder.getNew().setColumnProperty("namaPelanggan", String.class.getName()).build();
    colPelanggan.setTitle("Nama Pelanggan");
    colPelanggan.setWidth(70);
    colPelanggan.setHeaderStyle(columnHeaderStyleText);
    colPelanggan.setStyle(columnDetailStyleText);

    AbstractColumn colKodePartner =
        ColumnBuilder.getNew().setColumnProperty("kodePartner", String.class.getName()).build();
    colKodePartner.setTitle("Partner");
    colKodePartner.setWidth(40);
    colKodePartner.setHeaderStyle(columnHeaderStyleText);
    colKodePartner.setStyle(columnDetailStyleText);

    AbstractColumn colIntervalKredit =
        ColumnBuilder.getNew().setColumnProperty("intervalKredit", String.class.getName()).build();
    colIntervalKredit.setTitle("Interval Kredit");
    colIntervalKredit.setWidth(40);
    colIntervalKredit.setHeaderStyle(columnHeaderStyleText);
    colIntervalKredit.setStyle(columnDetailStyleText);

    AbstractColumn colBarang =
        ColumnBuilder.getNew().setColumnProperty("namaBarang", String.class.getName()).build();
    colBarang.setTitle("Nama Barang");
    colBarang.setWidth(70);
    colBarang.setHeaderStyle(columnHeaderStyleText);
    colBarang.setStyle(columnDetailStyleText);

    AbstractColumn colQuantity =
        ColumnBuilder.getNew().setColumnProperty("qtyKirim", Double.class.getName()).build();
    colQuantity.setTitle("Qty");
    colQuantity.setWidth(20);
    colQuantity.setHeaderStyle(columnHeaderStyleNumber);
    colQuantity.setStyle(columnDetailStyleNumbers);

    AbstractColumn colPenjualanBarang =
        ColumnBuilder.getNew().setColumnProperty("penjualanBarang", BigDecimal.class.getName())
            .build();
    colPenjualanBarang.setTitle("Nilai Jual");
    colPenjualanBarang.setWidth(50);
    colPenjualanBarang.setPattern("#,##0");
    colPenjualanBarang.setHeaderStyle(columnHeaderStyleNumber);
    colPenjualanBarang.setStyle(columnDetailStyleNumbers);

    AbstractColumn colPenerimaanPenjualan =
        ColumnBuilder.getNew().setColumnProperty("penerimaanPenjualan", BigDecimal.class.getName())
            .build();
    colPenerimaanPenjualan.setTitle("Pembayaran");
    colPenerimaanPenjualan.setWidth(50);
    colPenerimaanPenjualan.setPattern("#,##0");
    colPenerimaanPenjualan.setHeaderStyle(columnHeaderStyleNumber);
    colPenerimaanPenjualan.setStyle(columnDetailStyleNumbers);

    AbstractColumn colSisaPiutang =
        ColumnBuilder.getNew().setColumnProperty("sisaPiutang", BigDecimal.class.getName()).build();
    colSisaPiutang.setTitle("Sisa Piutang");
    colSisaPiutang.setWidth(50);
    colSisaPiutang.setPattern("#,##0");
    colSisaPiutang.setHeaderStyle(columnHeaderStyleNumber);
    colSisaPiutang.setStyle(columnDetailStyleNumbers);

    drb.addColumn(colNomorFaktur);
    drb.addColumn(colPelanggan);
    drb.addColumn(colKodePartner);
    drb.addColumn(colIntervalKredit);
    drb.addColumn(colBarang);
    drb.addColumn(colQuantity);
    drb.addColumn(colPenjualanBarang);
    drb.addColumn(colPenerimaanPenjualan);
    drb.addColumn(colSisaPiutang);

    /**
     * Add a global total sum for the lineSum field.
     */
    drb.addGlobalFooterVariable(colQuantity, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.addGlobalFooterVariable(colPenjualanBarang, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.addGlobalFooterVariable(colPenerimaanPenjualan, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.addGlobalFooterVariable(colSisaPiutang, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.setGlobalFooterVariableHeight(new Integer(20));
    drb.setGrandTotalLegend("Total");

    // ADD ALL USED FIELDS to the report.

    drb.setUseFullPageWidth(true); // use full width of the page
    dr = drb.build(); // build the report


    // Generate the Jasper Print Object
    JRDataSource ds = new JRBeanCollectionDataSource(resultList);
    JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);

    String outputFormat = "PDF";

    output = new ByteArrayOutputStream();

    if (outputFormat.equalsIgnoreCase("PDF")) {
      JasperExportManager.exportReportToPdfStream(jp, output);
      mediais = new ByteArrayInputStream(output.toByteArray());
      amedia =
          new AMedia(generateFileName(".pdf", karyawan, startDate, endDate), "pdf",
              "application/pdf", mediais);

      callReportWindow(this.amedia, "PDF");
    } else if (outputFormat.equalsIgnoreCase("XLS")) {
      JExcelApiExporter exporterXLS = new JExcelApiExporter();
      exporterXLS.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jp);
      exporterXLS.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, output);
      exporterXLS.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
      exporterXLS.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
      exporterXLS.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
          Boolean.TRUE);
      exporterXLS.exportReport();
      mediais = new ByteArrayInputStream(output.toByteArray());
      amedia = new AMedia("FileFormatExcel", "xls", "application/vnd.ms-excel", mediais);

      callReportWindow(this.amedia, "XLS");
    } else if (outputFormat.equalsIgnoreCase("RTF") || outputFormat.equalsIgnoreCase("DOC")) {
      JRRtfExporter exporterRTF = new JRRtfExporter();
      exporterRTF.setParameter(JRExporterParameter.JASPER_PRINT, jp);
      exporterRTF.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
      exporterRTF.exportReport();
      mediais = new ByteArrayInputStream(output.toByteArray());
      amedia = new AMedia("FileFormatRTF", "rtf", "application/rtf", mediais);

      callReportWindow(this.amedia, "RTF-DOC");
    }
  }

  private List<PiutangSales> generateData(Karyawan karyawan, List<Penjualan> listPenjualan) {
    List<PiutangSales> komisiPenjualanList = new ArrayList<PiutangSales>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    StatusService statusService = (StatusService) SpringUtil.getBean("statusService");
    PiutangService piutangService = (PiutangService) SpringUtil.getBean("piutangService");

    Status statusLunas = statusService.getStatusByID(new Long(2)); // LUNAS
    Status statusTarikBarang = statusService.getStatusByID(new Long(6)); // Tarik Barang

    for (Penjualan penjualan : listPenjualan) {
      try {
        if (checkAngsuran2Lunas(statusLunas, piutangService, penjualan)) {
          List<PenjualanDetail> penjualanDetails =
              penjualanService.getPenjualanDetailsByPenjualan(penjualan);
          for (PenjualanDetail penjualanDetail : penjualanDetails) {
            // valid to show yg angsuran kedua lunas
            if (!penjualanDetail.getBarang().isBonus()) {

              PiutangSales data = new PiutangSales();
              data.setNomorFaktur(penjualan.getNoFaktur());
              data.setNamaPelanggan(penjualan.getNamaPelanggan());
              data.setIntervalKredit(penjualan.getIntervalKredit() + " Bulan");
              String kodePartner = "0000";
              Double qtyKirim = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));

              if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                  && penjualan.getSales2() != null) {
                kodePartner = penjualan.getSales2().getKodeKaryawan();
                qtyKirim = qtyKirim / 2;
              } else if (penjualan.getSales2() != null
                  && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                  && penjualan.getSales1() != null) {
                kodePartner = penjualan.getSales1().getKodeKaryawan();
                qtyKirim = qtyKirim / 2;
              }

              if (checkPiutangFinalStatusTarikBarang(statusTarikBarang, piutangService, penjualan)) {
                qtyKirim = 0.0;
              }
              data.setKodePartner(kodePartner);
              data.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
              data.setQtyKirim(qtyKirim);
              data.setPenjualanBarang(penjualanDetail.getTotal());
              data.setPenerimaanPenjualan(penjualan.getTotal().subtract(penjualan.getPiutang()));
              data.setSisaPiutang(penjualan.getPiutang());
              totalQty = totalQty + qtyKirim;
              komisiPenjualanList.add(data);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        logger.info("error piutangSales penjualan : " + penjualan.getNoFaktur());
      }
    }

    Collections.sort(komisiPenjualanList, new Comparator<PiutangSales>() {
      @Override
      public int compare(PiutangSales obj1, PiutangSales obj2) {
        return obj1.getNomorFaktur().compareTo(obj2.getNomorFaktur());
      }
    });

    return komisiPenjualanList;
  }

  private String generateFileName(String fileType, Karyawan karyawan, Date startDate, Date endDate) {
    StringBuffer fileName = new StringBuffer();
    fileName.append(title);
    fileName.append("_" + karyawan.getNamaPanggilan());
    fileName.append("_" + DATE_FORMATER.format(startDate));
    fileName.append("_" + DATE_FORMATER.format(endDate));
    fileName.append(fileType);
    return fileName.toString();
  }

}
