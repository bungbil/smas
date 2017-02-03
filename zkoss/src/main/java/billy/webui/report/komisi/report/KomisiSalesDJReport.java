package billy.webui.report.komisi.report;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import billy.backend.model.CompanyProfile;
import billy.backend.model.Karyawan;
import billy.backend.model.Penjualan;
import billy.backend.model.PenjualanDetail;
import billy.backend.service.BonusTransportService;
import billy.backend.service.CompanyProfileService;
import billy.backend.service.PenjualanService;
import billy.webui.report.komisi.model.KomisiSales;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class KomisiSalesDJReport extends Window implements Serializable {

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
  private BigDecimal totalTabungan = BigDecimal.ZERO;
  private BigDecimal totalKomisi = BigDecimal.ZERO;
  private BigDecimal totalJumlah = BigDecimal.ZERO;
  private Double totalQty = 0.0;
  private AMedia amedia;
  private static final Logger logger = Logger.getLogger(KomisiSalesDJReport.class);
  private static final String title = "LAPORAN KOMISI SALES";
  private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("ddMMyyyy");
  DecimalFormat df = new DecimalFormat("#,###");

  public KomisiSalesDJReport(Component parent, Karyawan karyawan, Date startDate, Date endDate,
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

    setTitle(this.title);
    setId("ReportWindowKomisiSales");
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

  public void doPrint(Karyawan karyawan, Date startDate, Date endDate, List<Penjualan> listPenjualan)
      throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

    List<KomisiSales> resultList = generateData(karyawan, listPenjualan);
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
    footerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
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
    drb.setTitle(this.title);
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
    AutoText divisi =
        new AutoText("Sales : " + karyawan.getKodeKaryawan() + " - " + karyawan.getNamaPanggilan(),
            AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    divisi.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    divisi.setWidth(new Integer(700));
    AutoText tanggal =
        new AutoText("Tanggal Penjualan : "
            + ZksampleDateFormat.getDateFormater().format(startDate) + " - "
            + ZksampleDateFormat.getDateFormater().format(endDate), AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    tanggal.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    tanggal.setWidth(new Integer(700));
    AutoText emptyLine = new AutoText("", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    emptyLine.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    drb.addAutoText(atCompanyHeader).addAutoText(divisi).addAutoText(tanggal);
    //
    BonusTransportService bonusService =
        (BonusTransportService) SpringUtil.getBean("bonusTransportService");
    BigDecimal bonusSales = bonusService.getBonusSales(karyawan, totalQty);
    BigDecimal transportSales = bonusService.getTransportSales(karyawan, totalQty);
    BigDecimal totalPendapatan = totalJumlah.add(bonusSales).add(transportSales);
    BigDecimal total = totalPendapatan.subtract(totalTabungan);
    AutoText footerTextBonus =
        new AutoText("Bonus     : " + df.format(bonusSales), AutoText.POSITION_FOOTER,
            HorizontalBandAlignment.RIGHT);
    footerTextBonus.setWidth(new Integer(200));

    AutoText footerTextTransport =
        new AutoText("Transport : " + df.format(transportSales), AutoText.POSITION_FOOTER,
            HorizontalBandAlignment.RIGHT);
    footerTextTransport.setWidth(new Integer(200));

    AutoText footerTextTotalPendapatan =
        new AutoText("Total Pendapatan    : " + df.format(totalPendapatan),
            AutoText.POSITION_FOOTER, HorizontalBandAlignment.RIGHT);
    footerTextTotalPendapatan.setWidth(new Integer(200));
    footerTextTotalPendapatan.setStyle(footerStyle);


    AutoText footerTextTabungan =
        new AutoText("Potongan Tabungan  : " + df.format(totalTabungan), AutoText.POSITION_FOOTER,
            HorizontalBandAlignment.RIGHT);
    footerTextTabungan.setWidth(new Integer(200));


    AutoText footerTextTotal =
        new AutoText("Total    : " + df.format(total), AutoText.POSITION_FOOTER,
            HorizontalBandAlignment.RIGHT);
    footerTextTotal.setWidth(new Integer(200));
    footerTextTotal.setStyle(footerStyle);

    AutoText footerEmptyLine =
        new AutoText("", AutoText.POSITION_FOOTER, HorizontalBandAlignment.RIGHT);

    drb.addAutoText(footerTextBonus).addAutoText(footerTextTransport)
        .addAutoText(footerTextTotalPendapatan).addAutoText(footerTextTabungan)
        .addAutoText(footerTextTotal).addAutoText(footerEmptyLine);

    /**
     * Columns Definitions. A new ColumnBuilder instance for each column.
     */

    AbstractColumn colBarang =
        ColumnBuilder.getNew().setColumnProperty("namaBarang", String.class.getName()).build();
    colBarang.setTitle("Nama Barang");
    colBarang.setWidth(100);
    colBarang.setHeaderStyle(columnHeaderStyleText);
    colBarang.setStyle(columnDetailStyleText);

    AbstractColumn colIntervalKredit =
        ColumnBuilder.getNew().setColumnProperty("intervalKredit", Integer.class.getName()).build();
    colIntervalKredit.setTitle("Kredit");
    colIntervalKredit.setWidth(40);
    colIntervalKredit.setHeaderStyle(columnHeaderStyleNumber);
    colIntervalKredit.setStyle(columnDetailStyleNumbers);

    AbstractColumn colQuantity =
        ColumnBuilder.getNew().setColumnProperty("qty", Double.class.getName()).build();
    colQuantity.setTitle("Qty");
    colQuantity.setWidth(40);
    colQuantity.setHeaderStyle(columnHeaderStyleNumber);
    colQuantity.setStyle(columnDetailStyleNumbers);

    AbstractColumn colKomisi =
        ColumnBuilder.getNew().setColumnProperty("komisiSales", BigDecimal.class.getName()).build();
    colKomisi.setTitle("Komisi");
    colKomisi.setWidth(40);
    colKomisi.setPattern("#,##0");
    colKomisi.setHeaderStyle(columnHeaderStyleNumber);
    colKomisi.setStyle(columnDetailStyleNumbers);

    AbstractColumn colTabungan =
        ColumnBuilder.getNew().setColumnProperty("tabunganSales", BigDecimal.class.getName())
            .build();
    colTabungan.setTitle("Tabungan");
    colTabungan.setWidth(40);
    colTabungan.setPattern("#,##0");
    colTabungan.setHeaderStyle(columnHeaderStyleNumber);
    colTabungan.setStyle(columnDetailStyleNumbers);

    AbstractColumn colJumlah =
        ColumnBuilder.getNew().setColumnProperty("jumlah", BigDecimal.class.getName()).build();
    colJumlah.setTitle("Jumlah");
    colJumlah.setWidth(40);
    colJumlah.setPattern("#,##0");
    colJumlah.setHeaderStyle(columnHeaderStyleNumber);
    colJumlah.setStyle(columnDetailStyleNumbers);

    drb.addColumn(colBarang);
    drb.addColumn(colIntervalKredit);
    drb.addColumn(colQuantity);
    drb.addColumn(colKomisi);
    drb.addColumn(colTabungan);
    drb.addColumn(colJumlah);

    /**
     * Add a global total sum for the lineSum field.
     */
    drb.addGlobalFooterVariable(colQuantity, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.addGlobalFooterVariable(colJumlah, DJCalculation.SUM, footerStyleTotalSumValue);
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

  private List<KomisiSales> generateData(Karyawan karyawan, List<Penjualan> listPenjualan) {
    Map<String, KomisiSales> mapBarang = new HashMap<String, KomisiSales>();
    PenjualanService penjualanService = (PenjualanService) SpringUtil.getBean("penjualanService");
    for (Penjualan penjualan : listPenjualan) {
      List<PenjualanDetail> penjualanDetails =
          penjualanService.getPenjualanDetailsByPenjualan(penjualan);
      for (PenjualanDetail penjualanDetail : penjualanDetails) {
        if (!penjualanDetail.getBarang().isBonus()) {
          String kodeBarang =
              penjualanDetail.getBarang().getKodeBarang() + "-" + penjualan.getIntervalKredit();
          KomisiSales data = mapBarang.get(kodeBarang);
          if (data == null) {
            data = new KomisiSales();
            data.setNamaBarang(penjualanDetail.getBarang().getNamaBarang());
            data.setIntervalKredit(penjualan.getIntervalKredit());
            data.setKomisiSales(penjualanDetail.getKomisiSales());
            data.setTabunganSales(penjualanDetail.getTabunganSales());

            Double qty = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));
            BigDecimal komisi = BigDecimal.ZERO;
            BigDecimal tabungan = BigDecimal.ZERO;
            tabungan =
                penjualanDetail.getTabunganSales().multiply(
                    new BigDecimal(penjualanDetail.getQty()));
            komisi =
                penjualanDetail.getKomisiSales().multiply(new BigDecimal(penjualanDetail.getQty()));

            if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales2() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            } else if (penjualan.getSales2() != null
                && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales1() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            }
            totalKomisi = totalKomisi.add(komisi);
            totalTabungan = totalTabungan.add(tabungan);
            totalQty = totalQty + qty;

            data.setQty(qty);
            data.setJumlah(komisi.add(tabungan));
            mapBarang.put(kodeBarang, data);
          } else {

            Double qty = Double.parseDouble(String.valueOf(penjualanDetail.getQty()));
            BigDecimal komisi = BigDecimal.ZERO;
            BigDecimal tabungan = BigDecimal.ZERO;
            tabungan =
                penjualanDetail.getTabunganSales().multiply(
                    new BigDecimal(penjualanDetail.getQty()));
            komisi =
                penjualanDetail.getKomisiSales().multiply(new BigDecimal(penjualanDetail.getQty()));

            if (penjualan.getSales1().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales2() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            } else if (penjualan.getSales2() != null
                && penjualan.getSales2().getKodeKaryawan().equals(karyawan.getKodeKaryawan())
                && penjualan.getSales1() != null) {
              qty = qty / 2;
              komisi = komisi.divide(new BigDecimal(2));
              tabungan = tabungan.divide(new BigDecimal(2));
            }
            totalKomisi = totalKomisi.add(komisi);
            totalTabungan = totalTabungan.add(tabungan);
            totalQty = totalQty + qty;

            data.setQty(data.getQty() + qty);
            data.setJumlah(data.getJumlah().add(komisi).add(tabungan));
          }
        }
      }
    }

    List<KomisiSales> komisiSalesList = new ArrayList<KomisiSales>();
    for (Map.Entry<String, KomisiSales> kodeBarangMap : mapBarang.entrySet()) {
      KomisiSales data = kodeBarangMap.getValue();
      komisiSalesList.add(data);
      totalJumlah = totalJumlah.add(data.getJumlah());
    }

    return komisiSalesList;
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
