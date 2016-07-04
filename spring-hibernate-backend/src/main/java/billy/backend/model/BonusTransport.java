package billy.backend.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BonusTransport implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String deskripsiBonusTransport;
  private JobType jobType;
  private boolean multipleUnit;
  private Integer startRangeUnit;
  private Integer endRangeUnit;
  private BigDecimal honor;
  private BigDecimal or;
  private BigDecimal transport;
  private BigDecimal bonus;
  private boolean statusPusat;
  private boolean statusDaerah;
  private Date lastUpdate;
  private String updatedBy;


  public BonusTransport() {}

  public BonusTransport(long id, String deskripsiBonusTransport) {
    this.setId(id);
    this.deskripsiBonusTransport = deskripsiBonusTransport;
  }

  public BonusTransport(long id, String deskripsiBonusTransport, JobType jobType,
      boolean multipleUnit, Integer startRangeUnit, Integer endRangeUnit, BigDecimal honor,
      BigDecimal or, BigDecimal transport, BigDecimal bonus) {

    this.id = id;
    this.deskripsiBonusTransport = deskripsiBonusTransport;
    this.jobType = jobType;
    this.multipleUnit = multipleUnit;
    this.startRangeUnit = startRangeUnit;
    this.endRangeUnit = endRangeUnit;
    this.honor = honor;
    this.or = or;
    this.transport = transport;
    this.bonus = bonus;
  }

  public boolean equals(BonusTransport parameter) {
    return getId() == parameter.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof BonusTransport) {
      BonusTransport parameter = (BonusTransport) obj;
      return equals(parameter);
    }

    return false;
  }

  public BigDecimal getBonus() {
    return bonus;
  }

  public String getDeskripsiBonusTransport() {
    return deskripsiBonusTransport;
  }

  public Integer getEndRangeUnit() {
    return endRangeUnit;
  }


  public BigDecimal getHonor() {
    return honor;
  }

  @Override
  public long getId() {
    return id;
  }

  public JobType getJobType() {
    return jobType;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public BigDecimal getOr() {
    return or;
  }

  public Integer getStartRangeUnit() {
    return startRangeUnit;
  }

  public BigDecimal getTransport() {
    return transport;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public int getVersion() {
    return this.version;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(getId()).hashCode();
  }

  public boolean isMultipleUnit() {
    return multipleUnit;
  }

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public boolean isStatusDaerah() {
    return statusDaerah;
  }

  public boolean isStatusPusat() {
    return statusPusat;
  }


  public void setBonus(BigDecimal bonus) {
    this.bonus = bonus;
  }

  public void setDeskripsiBonusTransport(String deskripsiBonusTransport) {
    this.deskripsiBonusTransport = deskripsiBonusTransport;
  }

  public void setEndRangeUnit(Integer endRangeUnit) {
    this.endRangeUnit = endRangeUnit;
  }

  public void setHonor(BigDecimal honor) {
    this.honor = honor;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setJobType(JobType jobType) {
    this.jobType = jobType;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setMultipleUnit(boolean multipleUnit) {
    this.multipleUnit = multipleUnit;
  }

  public void setOr(BigDecimal or) {
    this.or = or;
  }

  public void setStartRangeUnit(Integer startRangeUnit) {
    this.startRangeUnit = startRangeUnit;
  }

  public void setStatusDaerah(boolean statusDaerah) {
    this.statusDaerah = statusDaerah;
  }

  public void setStatusPusat(boolean statusPusat) {
    this.statusPusat = statusPusat;
  }

  public void setTransport(BigDecimal transport) {
    this.transport = transport;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", getId()).toString();
  }

}
