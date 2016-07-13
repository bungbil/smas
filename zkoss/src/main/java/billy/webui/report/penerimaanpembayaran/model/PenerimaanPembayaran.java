package billy.webui.report.penerimaanpembayaran.model;

import java.math.BigDecimal;
import java.util.Date;

public class PenerimaanPembayaran {
  private String no;
  private String collectorName;
  private Date billingDate;
  private Date paymentDate;
  private String invoiceNumber;
  private String customerName;
  private BigDecimal totalInvoice;
  private BigDecimal amountPaid;
  private BigDecimal balance;


  public PenerimaanPembayaran() {

  }


  public PenerimaanPembayaran(String collectorName, Date billingDate, Date paymentDate,
      String invoiceNumber, String customerName, BigDecimal totalInvoice, BigDecimal amountPaid,
      BigDecimal balance) {
    super();
    this.collectorName = collectorName;
    this.billingDate = billingDate;
    this.paymentDate = paymentDate;
    this.invoiceNumber = invoiceNumber;
    this.customerName = customerName;
    this.totalInvoice = totalInvoice;
    this.amountPaid = amountPaid;
    this.balance = balance;
  }


  public BigDecimal getAmountPaid() {
    return amountPaid;
  }


  public BigDecimal getBalance() {
    return balance;
  }


  public Date getBillingDate() {
    return billingDate;
  }


  public String getCollectorName() {
    return collectorName;
  }


  public String getCustomerName() {
    return customerName;
  }


  public String getInvoiceNumber() {
    return invoiceNumber;
  }


  public String getNo() {
    return no;
  }


  public Date getPaymentDate() {
    return paymentDate;
  }


  public BigDecimal getTotalInvoice() {
    return totalInvoice;
  }


  public void setAmountPaid(BigDecimal amountPaid) {
    this.amountPaid = amountPaid;
  }


  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }


  public void setBillingDate(Date billingDate) {
    this.billingDate = billingDate;
  }


  public void setCollectorName(String collectorName) {
    this.collectorName = collectorName;
  }


  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }


  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }


  public void setNo(String no) {
    this.no = no;
  }


  public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }


  public void setTotalInvoice(BigDecimal totalInvoice) {
    this.totalInvoice = totalInvoice;
  }


  @Override
  public String toString() {
    return String
        .format(
            "PenerimaanPembayaran [collectorName=%s, billingDate=%s, paymentDate=%s, invoiceNumber=%s, customerName=%s, totalInvoice=%s, amountPaid=%s, balance=%s]",
            collectorName, billingDate, paymentDate, invoiceNumber, customerName, totalInvoice,
            amountPaid, balance);
  }


}
