package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Karyawan implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeKaryawan;
	private String namaKaryawan;
	private Date tanggalLahir;
	private String telepon;
	private String handphone;
	private String email;
	private String alamat;
	private JobType jobType;
	private Karyawan supervisorDivisi;
	private Date lastUpdate;
	private String updatedBy;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Karyawan() {
	}

	public Karyawan(long id, String kodeKaryawan) {
		this.setId(id);
		this.kodeKaryawan = kodeKaryawan;
	}

	public Karyawan(long id, String kodeKaryawan, String namaKaryawan) {
		this.setId(id);
		this.kodeKaryawan = kodeKaryawan;
		this.namaKaryawan = namaKaryawan;		
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getKodeKaryawan() {
		return kodeKaryawan;
	}

	public void setKodeKaryawan(String kodeKaryawan) {
		this.kodeKaryawan = kodeKaryawan;
	}

	public String getNamaKaryawan() {
		return namaKaryawan;
	}

	public void setNamaKaryawan(String namaKaryawan) {
		this.namaKaryawan = namaKaryawan;
	}

	public Date getTanggalLahir() {
		return tanggalLahir;
	}

	public void setTanggalLahir(Date tanggalLahir) {
		this.tanggalLahir = tanggalLahir;
	}

	public String getTelepon() {
		return telepon;
	}

	public void setTelepon(String telepon) {
		this.telepon = telepon;
	}

	public String getHandphone() {
		return handphone;
	}

	public void setHandphone(String handphone) {
		this.handphone = handphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlamat() {
		return alamat;
	}

	public void setAlamat(String alamat) {
		this.alamat = alamat;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public Karyawan getSupervisorDivisi() {
		return supervisorDivisi;
	}

	public void setSupervisorDivisi(Karyawan supervisorDivisi) {
		this.supervisorDivisi = supervisorDivisi;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(Karyawan Parameter) {
		return getId() == Parameter.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Karyawan) {
			Karyawan Parameter = (Karyawan) obj;
			return equals(Parameter);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
