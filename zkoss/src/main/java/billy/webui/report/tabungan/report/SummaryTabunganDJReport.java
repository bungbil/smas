package billy.webui.report.tabungan.report;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import billy.backend.model.CompanyProfile;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.webui.report.tabungan.model.SummaryTabungan;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class SummaryTabunganDJReport extends Window implements Serializable {

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

  private final Double totalQty = 0.0;
  private AMedia amedia;
  private static final Logger logger = Logger.getLogger(SummaryTabunganDJReport.class);
  DecimalFormat df = new DecimalFormat("#,###");
  private static final String title = "LAPORAN TABUNGAN";
  private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("ddMMyyyy");

  public SummaryTabunganDJReport(Component parent, Date startDate, Date endDate,
      List<Penjualan> listPenjualan) throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(startDate, endDate, listPenjualan);
    } catch (final Exception e) {
      ZksampleMessageUtils.showErrorMessage(e.toString());
    }
  }

  private void addToMonthTabungan(SummaryTabungan data, Date tglPenjualan, BigDecimal tabungan) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(tglPenjualan);
    int month = cal.get(Calendar.MONTH);
    switch (month) {
      case 0: // JAN
        data.setTabungan1(data.getTabungan1().add(tabungan));
        break;
      case 1: // FEB
        data.setTabungan2(data.getTabungan2().add(tabungan));
        break;
      case 2: // MAR
        data.setTabungan3(data.getTabungan3().add(tabungan));
        break;
      case 3:
        data.setTabungan4(data.getTabungan4().add(tabungan));
        break;
      case 4:
        data.setTabungan5(data.getTabungan5().add(tabungan));
        break;
      case 5:
        data.setTabungan6(data.getTabungan6().add(tabungan));
        break;
      case 6:
        data.setTabungan7(data.getTabungan7().add(tabungan));
        break;
      case 7:
        data.setTabungan8(data.getTabungan8().add(tabungan));
        break;
      case 8:
        data.setTabungan9(data.getTabungan9().add(tabungan));
        break;
      case 9:
        data.setTabungan10(data.getTabungan10().add(tabungan));
        break;
      case 10:
        data.setTabungan11(data.getTabungan11().add(tabungan));
        break;
      case 11:
        data.setTabungan12(data.getTabungan12().add(tabungan));
        break;
    }
  }

  private void callReportWindow(AMedia aMedia, String format) {
    boolean modal = true;

    setTitle(title);
    setId("ReportWindowSummaryTabungan");
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

  public void doPrint(Date startDate, Date endDate, List<Penjualan> listPenjualan)
      throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

    List<SummaryTabungan> resultList = generateData(listPenjualan);
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
    Font subtitleFont = Font.VERDANA_MEDIUM_BOLD;
    subtitleFont.setUnderline(true);
    subtitleStyle.setFont(subtitleFont);

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

    AutoText tanggal =
        new AutoText("Tanggal Penjualan : "
            + ZksampleDateFormat.getDateFormater().format(startDate) + " - "
            + ZksampleDateFormat.getDateFormater().format(endDate), AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    tanggal.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    tanggal.setWidth(new Integer(700));
    AutoText emptyLine = new AutoText("", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    emptyLine.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    drb.addAutoText(atCompanyHeader).addAutoText(tanggal);


    /**
     * Columns Definitions. A new ColumnBuilder instance for each column.
     */
    AbstractColumn colNamaDivisi =
        ColumnBuilder.getNew().setColumnProperty("namaDivisi", String.class.getName()).build();
    colNamaDivisi.setTitle("Nama Divisi");
    colNamaDivisi.setWidth(60);
    colNamaDivisi.setHeaderStyle(columnHeaderStyleText);
    colNamaDivisi.setStyle(subtitleStyle);

    AbstractColumn colNamaSales =
        ColumnBuilder.getNew().setColumnProperty("namaSales", String.class.getName()).build();
    colNamaSales.setTitle("Sales");
    colNamaSales.setWidth(60);
    colNamaSales.setHeaderStyle(columnHeaderStyleText);
    colNamaSales.setStyle(columnDetailStyleText);

    AbstractColumn colTabungan1 =
        ColumnBuilder.getNew().setColumnProperty("tabungan1", BigDecimal.class.getName()).build();
    colTabungan1.setTitle("01");
    colTabungan1.setWidth(50);
    colTabungan1.setPattern("#,##0");
    colTabungan1.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan1.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan2 =
        ColumnBuilder.getNew().setColumnProperty("tabungan2", BigDecimal.class.getName()).build();
    colTabungan2.setTitle("02");
    colTabungan2.setWidth(50);
    colTabungan2.setPattern("#,##0");
    colTabungan2.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan2.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan3 =
        ColumnBuilder.getNew().setColumnProperty("tabungan3", BigDecimal.class.getName()).build();
    colTabungan3.setTitle("03");
    colTabungan3.setWidth(50);
    colTabungan3.setPattern("#,##0");
    colTabungan3.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan3.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan4 =
        ColumnBuilder.getNew().setColumnProperty("tabungan4", BigDecimal.class.getName()).build();
    colTabungan4.setTitle("04");
    colTabungan4.setWidth(50);
    colTabungan4.setPattern("#,##0");
    colTabungan4.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan4.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan5 =
        ColumnBuilder.getNew().setColumnProperty("tabungan5", BigDecimal.class.getName()).build();
    colTabungan5.setTitle("05");
    colTabungan5.setWidth(50);
    colTabungan5.setPattern("#,##0");
    colTabungan5.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan5.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan6 =
        ColumnBuilder.getNew().setColumnProperty("tabungan6", BigDecimal.class.getName()).build();
    colTabungan6.setTitle("06");
    colTabungan6.setWidth(50);
    colTabungan6.setPattern("#,##0");
    colTabungan6.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan6.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan7 =
        ColumnBuilder.getNew().setColumnProperty("tabungan7", BigDecimal.class.getName()).build();
    colTabungan7.setTitle("07");
    colTabungan7.setWidth(50);
    colTabungan7.setPattern("#,##0");
    colTabungan7.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan7.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan8 =
        ColumnBuilder.getNew().setColumnProperty("tabungan8", BigDecimal.class.getName()).build();
    colTabungan8.setTitle("08");
    colTabungan8.setWidth(50);
    colTabungan8.setPattern("#,##0");
    colTabungan8.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan8.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan9 =
        ColumnBuilder.getNew().setColumnProperty("tabungan9", BigDecimal.class.getName()).build();
    colTabungan9.setTitle("09");
    colTabungan9.setWidth(50);
    colTabungan9.setPattern("#,##0");
    colTabungan9.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan9.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan10 =
        ColumnBuilder.getNew().setColumnProperty("tabungan10", BigDecimal.class.getName()).build();
    colTabungan10.setTitle("10");
    colTabungan10.setWidth(50);
    colTabungan10.setPattern("#,##0");
    colTabungan10.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan10.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan11 =
        ColumnBuilder.getNew().setColumnProperty("tabungan11", BigDecimal.class.getName()).build();
    colTabungan11.setTitle("11");
    colTabungan11.setWidth(50);
    colTabungan11.setPattern("#,##0");
    colTabungan11.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan11.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan12 =
        ColumnBuilder.getNew().setColumnProperty("tabungan12", BigDecimal.class.getName()).build();
    colTabungan12.setTitle("12");
    colTabungan12.setWidth(50);
    colTabungan12.setPattern("#,##0");
    colTabungan12.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan12.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabunganTotal =
        ColumnBuilder.getNew().setColumnProperty("total", BigDecimal.class.getName()).build();
    colTabunganTotal.setTitle("Total");
    colTabunganTotal.setWidth(50);
    colTabunganTotal.setPattern("#,##0");
    colTabunganTotal.setHeaderStyle(columnHeaderStyleNumber);
    colTabunganTotal.setStyle(columnDetailStyleNumbers);

    GroupBuilder gb1 = new GroupBuilder();
    DJGroup g1 =
        gb1.setCriteriaColumn((PropertyColumn) colNamaDivisi)
            .addFooterVariable(colTabunganTotal, DJCalculation.SUM, footerStyleTotalSumValue)
            .setGroupLayout(GroupLayout.VALUE_IN_HEADER_WITH_HEADERS).build();
    drb.setPrintColumnNames(false);

    drb.addColumn(colNamaDivisi);
    drb.addColumn(colNamaSales);
    drb.addColumn(colTabungan1);
    drb.addColumn(colTabungan2);
    drb.addColumn(colTabungan3);
    drb.addColumn(colTabungan4);
    drb.addColumn(colTabungan5);
    drb.addColumn(colTabungan6);
    drb.addColumn(colTabungan7);
    drb.addColumn(colTabungan8);
    drb.addColumn(colTabungan9);
    drb.addColumn(colTabungan10);
    drb.addColumn(colTabungan11);
    drb.addColumn(colTabungan12);
    drb.addColumn(colTabunganTotal);
    drb.addGroup(g1);

    /**
     * Add a global total sum for the lineSum field.
     */
    // drb.addGlobalFooterVariable(colTabunganTotal, DJCalculation.SUM, footerStyleTotalSumValue);
    // drb.setGlobalFooterVariableHeight(new Integer(20));
    // drb.setGrandTotalLegend("Total");

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
          new AMedia(generateFileName(".pdf", startDate, endDate), "pdf", "application/pdf",
              mediais);

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

  private List<SummaryTabungan> generateData(List<Penjualan> listPenjualan) {
    Map<String, SummaryTabungan> mapTabungan = new HashMap<String, SummaryTabungan>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    BigDecimal tabungan = BigDecimal.ZERO;
    logger.info("awal generateData mapTabungan = " + mapTabungan.size());

    for (Penjualan penjualan : listPenjualan) {
      List<PenjualanDetail> penjualanDetails =
          penjualanService.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail penjualanDetail : penjualanDetails) {
        String namaDivisiSales1 =
            penjualan.getDivisi().getKodeKaryawan() + "-" + penjualan.getSales1().getKodeKaryawan();
        SummaryTabungan data = mapTabungan.get(namaDivisiSales1);
        if (data == null) {
          data = new SummaryTabungan();
          data.setNamaDivisi(penjualan.getDivisi().getNamaPanggilan());
          data.setNamaSales(penjualan.getSales1().getNamaPanggilan());
          tabungan =
              penjualanDetail.getTabunganSales().multiply(new BigDecimal(penjualanDetail.getQty()));

          if (penjualan.getSales2() != null) {
            tabungan = tabungan.divide(new BigDecimal(2));
            String namaDivisiSales2 =
                penjualan.getDivisi().getKodeKaryawan() + "-"
                    + penjualan.getSales2().getKodeKaryawan();
            SummaryTabungan data2 = mapTabungan.get(namaDivisiSales1);
            if (data2 == null) {
              data2 = new SummaryTabungan();
              data2.setNamaDivisi(penjualan.getDivisi().getNamaPanggilan());
              data2.setNamaSales(penjualan.getSales2().getNamaPanggilan());
              addToMonthTabungan(data2, penjualan.getTglPenjualan(), tabungan);
              data2.setTotal(data2.getTotal().add(tabungan));
              mapTabungan.put(namaDivisiSales2, data2);
            } else {
              addToMonthTabungan(data2, penjualan.getTglPenjualan(), tabungan);
              data2.setTotal(data2.getTotal().add(tabungan));
            }
          }
          addToMonthTabungan(data, penjualan.getTglPenjualan(), tabungan);
          data.setTotal(data.getTotal().add(tabungan));
          mapTabungan.put(namaDivisiSales1, data);
        } else {
          tabungan =
              penjualanDetail.getTabunganSales().multiply(new BigDecimal(penjualanDetail.getQty()));
          if (penjualan.getSales2() != null) {
            tabungan = tabungan.divide(new BigDecimal(2));
            String namaDivisiSales2 =
                penjualan.getDivisi().getKodeKaryawan() + "-"
                    + penjualan.getSales2().getKodeKaryawan();
            SummaryTabungan data2 = mapTabungan.get(namaDivisiSales1);
            if (data2 == null) {
              data2 = new SummaryTabungan();
              data2.setNamaDivisi(penjualan.getDivisi().getNamaPanggilan());
              data2.setNamaSales(penjualan.getSales2().getNamaPanggilan());
              addToMonthTabungan(data2, penjualan.getTglPenjualan(), tabungan);
              data2.setTotal(data2.getTotal().add(tabungan));
              mapTabungan.put(namaDivisiSales2, data2);
            } else {
              addToMonthTabungan(data2, penjualan.getTglPenjualan(), tabungan);
              data2.setTotal(data2.getTotal().add(tabungan));
            }
          }
          addToMonthTabungan(data, penjualan.getTglPenjualan(), tabungan);
          data.setTotal(data.getTotal().add(tabungan));
        }
      }
    }
    List<SummaryTabungan> tabunganList = new ArrayList<SummaryTabungan>();
    BigDecimal totalTabungan = BigDecimal.ZERO;
    for (Map.Entry<String, SummaryTabungan> kodeDivisiSalesMap : mapTabungan.entrySet()) {
      SummaryTabungan data = kodeDivisiSalesMap.getValue();
      tabunganList.add(data);
      totalTabungan = totalTabungan.add(data.getTotal());
    }

    return tabunganList;
  }

  private String generateFileName(String fileType, Date startDate, Date endDate) {
    StringBuffer fileName = new StringBuffer();
    fileName.append(title);
    fileName.append("_" + DATE_FORMATER.format(startDate));
    fileName.append("_" + DATE_FORMATER.format(endDate));
    fileName.append(fileType);
    return fileName.toString();
  }
}
