package billy.webui.report.penerimaanpembayaran.report;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
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
import billy.backend.model.Piutang;
import billy.backend.service.CompanyProfileService;
import billy.webui.report.penerimaanpembayaran.model.PenerimaanPembayaran;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

public class PenerimaanPembayaranDJReport extends Window implements Serializable {

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


  private AMedia amedia;
  private static final Logger logger = Logger.getLogger(PenerimaanPembayaranDJReport.class);
  private static final String title = "LAPORAN PENERIMAAN PEMBAYARAN";
  private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("ddMMyyyy");

  public PenerimaanPembayaranDJReport(Component parent, Karyawan karyawan, Date startDate,
      Date endDate, List<Piutang> listPiutang) throws InterruptedException {
    super();
    this.setParent(parent);

    try {
      doPrint(karyawan, startDate, endDate, listPiutang);
    } catch (final Exception e) {
      ZksampleMessageUtils.showErrorMessage(e.toString());
    }
  }

  private void callReportWindow(AMedia aMedia, String format) {
    boolean modal = true;

    setTitle(this.title);
    setId("ReportWindowPenerimaanPembayaran");
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

  public void doPrint(Karyawan karyawan, Date startDate, Date endDate, List<Piutang> listPiutang)
      throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {
    List<PenerimaanPembayaran> resultList = generateData(karyawan, listPiutang);
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
        new AutoText("Kolektor : " + karyawan.getKodeKaryawan() + " - "
            + karyawan.getNamaPanggilan(), AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    divisi.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    divisi.setWidth(new Integer(700));
    AutoText tanggal =
        new AutoText("Tanggal Pembayaran : "
            + ZksampleDateFormat.getDateFormater().format(startDate) + " - "
            + ZksampleDateFormat.getDateFormater().format(endDate), AutoText.POSITION_HEADER,
            HorizontalBandAlignment.LEFT);
    tanggal.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    tanggal.setWidth(new Integer(700));
    AutoText emptyLine = new AutoText("", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
    emptyLine.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
    drb.addAutoText(atCompanyHeader).addAutoText(divisi).addAutoText(tanggal);
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

    AbstractColumn colNo =
        ColumnBuilder.getNew().setColumnProperty("no", String.class.getName()).build();
    colNo.setTitle("No.");
    colNo.setWidth(15);
    colNo.setHeaderStyle(columnHeaderStyleText);
    colNo.setStyle(columnDetailStyleText);

    AbstractColumn colBillingDate =
        ColumnBuilder.getNew().setColumnProperty("billingDate", Date.class.getName()).build();
    colBillingDate.setTitle("Billing Date");
    colBillingDate.setWidth(35);
    colBillingDate.setPattern("dd-MM-yyyy");
    colBillingDate.setHeaderStyle(columnHeaderStyleNumber);
    colBillingDate.setStyle(columnDetailStyleNumbers);

    AbstractColumn colPaymentDate =
        ColumnBuilder.getNew().setColumnProperty("paymentDate", Date.class.getName()).build();
    colPaymentDate.setTitle("Payment Date");
    colPaymentDate.setWidth(40);
    colPaymentDate.setPattern("dd-MM-yyyy");
    colPaymentDate.setHeaderStyle(columnHeaderStyleNumber);
    colPaymentDate.setStyle(columnDetailStyleNumbers);

    AbstractColumn colInvoiceNumber =
        ColumnBuilder.getNew().setColumnProperty("invoiceNumber", String.class.getName()).build();
    colInvoiceNumber.setTitle("Invoice Number");
    colInvoiceNumber.setWidth(50);
    colInvoiceNumber.setHeaderStyle(columnHeaderStyleText);
    colInvoiceNumber.setStyle(columnDetailStyleText);

    AbstractColumn colCustomerName =
        ColumnBuilder.getNew().setColumnProperty("customerName", String.class.getName()).build();
    colCustomerName.setTitle("Customer Name");
    colCustomerName.setWidth(50);
    colCustomerName.setHeaderStyle(columnHeaderStyleText);
    colCustomerName.setStyle(columnDetailStyleText);


    AbstractColumn colTotalInvoice =
        ColumnBuilder.getNew().setColumnProperty("totalInvoice", BigDecimal.class.getName())
            .build();
    colTotalInvoice.setTitle("Total Invoice");
    colTotalInvoice.setWidth(40);
    colTotalInvoice.setPattern("#,##0");
    colTotalInvoice.setHeaderStyle(columnHeaderStyleNumber);
    colTotalInvoice.setStyle(columnDetailStyleNumbers);

    AbstractColumn colAmountPaid =
        ColumnBuilder.getNew().setColumnProperty("amountPaid", BigDecimal.class.getName()).build();
    colAmountPaid.setTitle("Amount Paid");
    colAmountPaid.setWidth(40);
    colAmountPaid.setPattern("#,##0");
    colAmountPaid.setHeaderStyle(columnHeaderStyleNumber);
    colAmountPaid.setStyle(columnDetailStyleNumbers);

    AbstractColumn colBalance =
        ColumnBuilder.getNew().setColumnProperty("balance", BigDecimal.class.getName()).build();
    colBalance.setTitle("Balance");
    colBalance.setWidth(40);
    colBalance.setPattern("#,##0");
    colBalance.setHeaderStyle(columnHeaderStyleNumber);
    colBalance.setStyle(columnDetailStyleNumbers);

    AbstractColumn colCollectorName =
        ColumnBuilder.getNew().setColumnProperty("collectorName", String.class.getName()).build();
    colCollectorName.setTitle("Collector Name");
    colCollectorName.setWidth(40);
    colCollectorName.setHeaderStyle(columnHeaderStyleNumber);
    colCollectorName.setStyle(columnDetailStyleNumbers);

    drb.addColumn(colNo);
    drb.addColumn(colBillingDate);
    drb.addColumn(colPaymentDate);
    drb.addColumn(colInvoiceNumber);
    drb.addColumn(colCustomerName);
    drb.addColumn(colTotalInvoice);
    drb.addColumn(colAmountPaid);
    drb.addColumn(colBalance);
    // drb.addColumn(colCollectorName);

    /**
     * Add a global total sum for the lineSum field.
     */
    drb.addGlobalFooterVariable(colTotalInvoice, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.addGlobalFooterVariable(colAmountPaid, DJCalculation.SUM, footerStyleTotalSumValue);
    drb.addGlobalFooterVariable(colBalance, DJCalculation.SUM, footerStyleTotalSumValue);
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

  private List<PenerimaanPembayaran> generateData(Karyawan karyawan, List<Piutang> listPiutang) {
    List<PenerimaanPembayaran> penerimaanPembayaranList = new ArrayList<PenerimaanPembayaran>();
    int i = 1;
    for (Piutang piutang : listPiutang) {
      PenerimaanPembayaran data = new PenerimaanPembayaran();
      data.setNo(i + ".");
      data.setBillingDate(piutang.getTglJatuhTempo());
      data.setPaymentDate(piutang.getTglPembayaran());
      data.setInvoiceNumber(piutang.getNoKuitansi());
      data.setCustomerName(piutang.getPenjualan().getNamaPelanggan());
      BigDecimal totalInvoice = piutang.getPenjualan().getPiutang().add(piutang.getPembayaran());
      data.setTotalInvoice(totalInvoice);
      data.setAmountPaid(piutang.getPembayaran());
      data.setBalance(piutang.getPenjualan().getPiutang());
      data.setCollectorName(piutang.getKolektor().getNamaPanggilan());

      penerimaanPembayaranList.add(data);
      i++;
    }


    Collections.sort(penerimaanPembayaranList, new Comparator<PenerimaanPembayaran>() {
      @Override
      public int compare(PenerimaanPembayaran obj1, PenerimaanPembayaran obj2) {
        return obj1.getInvoiceNumber().compareTo(obj2.getInvoiceNumber());
      }
    });
    return penerimaanPembayaranList;
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
