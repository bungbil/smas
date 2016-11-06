package billy.webui.transaction.piutang.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import billy.backend.model.Piutang;
import billy.backend.service.CompanyProfileService;
import billy.webui.transaction.piutang.model.PiutangList;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PiutangListDJReport extends Window implements Serializable {

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


  private AMedia amedia;
  private static final Logger logger = Logger.getLogger(PiutangListDJReport.class);
  private static final String title = "LAPORAN KWITANSI";
  private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("ddMMyyyy");

  public PiutangListDJReport(Component parent, String kodeKolektor, Date startDate, Date endDate,
      List<Piutang> listPiutang) throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(kodeKolektor, startDate, endDate, listPiutang);
    } catch (final Exception e) {
      ZksampleMessageUtils.showErrorMessage(e.toString());
    }
  }

  private void callReportWindow(AMedia aMedia, String format) {
    boolean modal = true;

    setTitle(this.title);
    setId("ReportWindow");
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

  public void doPrint(String kodeKolektor, Date startDate, Date endDate, List<Piutang> listPiutang)
      throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

    List<PiutangList> resultList = generateData(listPiutang);
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
    // columnHeaderStyleText.setBorderBottom(Border.PEN_1_POINT());

    // ColumnHeader Style Text (right-align)
    Style columnHeaderStyleNumber = new Style();
    columnHeaderStyleNumber.setFont(Font.VERDANA_SMALL_BOLD);
    columnHeaderStyleNumber.setHorizontalAlign(HorizontalAlign.RIGHT);
    columnHeaderStyleNumber.setBorderBottom(Border.PEN_1_POINT());

    // Footer Style (center-align)
    Style footerStyle = new Style();
    footerStyle.setFont(Font.VERDANA_SMALL);
    footerStyle.getFont().setFontSize(8);
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
    drb.setPrintBackgroundOnOddRows(true);

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
    AutoText address =
        new AutoText(company.get(0).getAddress(), AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    address.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    address.setWidth(new Integer(700));
    String kodeKolektorStr = "";
    if (kodeKolektor != null)
      kodeKolektorStr = kodeKolektor;
    AutoText divisi =
        new AutoText("Kolektor : " + kodeKolektorStr, AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    divisi.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    divisi.setWidth(new Integer(700));

    String startDateStr = "";
    if (startDate != null)
      startDateStr = ZksampleDateFormat.getDateFormater().format(startDate);
    String endDateStr = "";
    if (endDate != null)
      endDateStr = ZksampleDateFormat.getDateFormater().format(endDate);
    AutoText tanggal =
        new AutoText("Tanggal Jatuh Tempo : " + startDateStr + " - " + endDateStr,
            AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    tanggal.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    tanggal.setWidth(new Integer(700));

    AutoText emptyLine = new AutoText("", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    emptyLine.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    drb.addAutoText(atCompanyHeader).addAutoText(address).addAutoText(emptyLine)
        .addAutoText(divisi).addAutoText(tanggal);
    //
    // // Footer
    // AutoText footerText = new
    // AutoText("Help to prevent the global warming by writing cool software.",
    // AutoText.POSITION_FOOTER, HorizontalBandAlignment.CENTER);
    // footerText.setStyle(footerStyle);
    // drb.addAutoText(footerText);

    /**
     * Columns Definitions. A new ColumnBuilder instance for each column.
     */

    AbstractColumn colFaktur =
        ColumnBuilder.getNew().setColumnProperty("noFaktur", String.class.getName()).build();
    colFaktur.setTitle("No Faktur");
    colFaktur.setWidth(80);
    colFaktur.setHeaderStyle(columnHeaderStyleText);
    colFaktur.setStyle(columnDetailStyleText);

    AbstractColumn colKuitansi =
        ColumnBuilder.getNew().setColumnProperty("noKwitansi", String.class.getName()).build();
    colKuitansi.setTitle("No Kwitansi");
    colKuitansi.setWidth(80);
    colKuitansi.setHeaderStyle(columnHeaderStyleText);
    colKuitansi.setStyle(columnDetailStyleText);

    AbstractColumn colPembayaranKe =
        ColumnBuilder.getNew().setColumnProperty("pembayaranKe", Integer.class.getName()).build();
    colPembayaranKe.setTitle("Pembayaran Ke-");
    colPembayaranKe.setWidth(80);
    colPembayaranKe.setHeaderStyle(columnHeaderStyleText);
    colPembayaranKe.setStyle(columnDetailStyleText);

    AbstractColumn colTglJatuhTempo =
        ColumnBuilder.getNew().setColumnProperty("tglJatuhTempo", Date.class.getName()).build();
    colTglJatuhTempo.setTitle("Tgl Jatuh Tempo");
    colTglJatuhTempo.setWidth(80);
    colTglJatuhTempo.setPattern("dd-MM-yyyy");
    colTglJatuhTempo.setHeaderStyle(columnHeaderStyleNumber);
    colTglJatuhTempo.setStyle(columnDetailStyleNumbers);

    AbstractColumn colNilaiTagihan =
        ColumnBuilder.getNew().setColumnProperty("nilaiTagihan", BigDecimal.class.getName())
            .build();
    colNilaiTagihan.setTitle("Nilai Tagihan");
    colNilaiTagihan.setWidth(80);
    colNilaiTagihan.setPattern("#,##0");
    colNilaiTagihan.setHeaderStyle(columnHeaderStyleNumber);
    colNilaiTagihan.setStyle(columnDetailStyleNumbers);


    AbstractColumn colKolektor =
        ColumnBuilder.getNew().setColumnProperty("namaKolektor", String.class.getName()).build();
    colKolektor.setTitle("Kolektor");
    colKolektor.setWidth(80);
    colKolektor.setHeaderStyle(columnHeaderStyleText);
    colKolektor.setStyle(columnDetailStyleText);

    drb.addColumn(colFaktur);
    drb.addColumn(colKuitansi);
    drb.addColumn(colPembayaranKe);
    drb.addColumn(colNilaiTagihan);
    drb.addColumn(colTglJatuhTempo);
    drb.addColumn(colKolektor);

    /**
     * Add a global total sum for the lineSum field.
     */
    drb.addGlobalFooterVariable(colNilaiTagihan, DJCalculation.SUM, footerStyleTotalSumValue);
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
          new AMedia(generateFileName(".pdf", kodeKolektor, startDate, endDate), "pdf",
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

  private List<PiutangList> generateData(List<Piutang> listPiutang) {
    List<PiutangList> resultList = new ArrayList<PiutangList>();

    for (Piutang piutang : listPiutang) {
      PiutangList data = new PiutangList();
      data.setNoFaktur(piutang.getPenjualan().getNoFaktur());
      data.setNoKwitansi(piutang.getNoKuitansi());
      data.setPembayaranKe(piutang.getPembayaranKe());
      data.setNilaiTagihan(piutang.getNilaiTagihan());
      data.setTglJatuhTempo(piutang.getTglJatuhTempo());

      if (piutang.getKolektor() != null) {
        data.setNamaKolektor(piutang.getKolektor().getNamaPanggilan());
      } else {
        data.setNamaKolektor("");
      }
      resultList.add(data);
    }

    return resultList;
  }

  private String generateFileName(String fileType, String kodeKolektor, Date startDate, Date endDate) {
    StringBuffer fileName = new StringBuffer();
    fileName.append(title);
    if (kodeKolektor != null)
      fileName.append("_" + kodeKolektor);
    if (startDate != null)
      fileName.append("_" + DATE_FORMATER.format(startDate));
    if (endDate != null)
      fileName.append("_" + DATE_FORMATER.format(endDate));
    fileName.append(fileType);
    return fileName.toString();
  }
}
