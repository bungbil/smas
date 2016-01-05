package billy.backend.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Karyawan implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String kodeKaryawan;
	private String namaPanggilan;
	private String namaKtp;
	private String ktp;
	private Date tanggalLahir;
	private String telepon;
	private String handphone;
	private String email;
	private String alamat;
	private JobType jobType;
	private String inisialDivisi;
	private String statusDivisi;
	private Karyawan supervisorDivisi;
	private Date lastUpdate;
	private String updatedBy;
	private String catatan;
	private byte[] profileImage;
	private byte[] ktpImage;

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Karyawan() {
	}

	public Karyawan(long id, String kodeKaryawan) {
		this.setId(id);
		this.kodeKaryawan = kodeKaryawan;
	}

	public Karyawan(long id, String kodeKaryawan, String namaPanggilan) {
		this.setId(id);
		this.kodeKaryawan = kodeKaryawan;
		this.namaPanggilan = namaPanggilan;		
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


	public String getNamaPanggilan() {
		return namaPanggilan;
	}

	public void setNamaPanggilan(String namaPanggilan) {
		this.namaPanggilan = namaPanggilan;
	}

	public String getNamaKtp() {
		return namaKtp;
	}

	public void setNamaKtp(String namaKtp) {
		this.namaKtp = namaKtp;
	}

	public String getKtp() {
		return ktp;
	}

	public void setKtp(String ktp) {
		this.ktp = ktp;
	}

	public String getInisialDivisi() {
		return inisialDivisi;
	}

	public void setInisialDivisi(String inisialDivisi) {
		this.inisialDivisi = inisialDivisi;
	}

	public String getStatusDivisi() {
		return statusDivisi;
	}

	public void setStatusDivisi(String statusDivisi) {
		this.statusDivisi = statusDivisi;
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

	public String getCatatan() {
		return catatan;
	}

	public void setCatatan(String catatan) {
		this.catatan = catatan;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public byte[] getKtpImage() {
		return ktpImage;
	}

	public void setKtpImage(byte[] ktpImage) {
		this.ktpImage = ktpImage;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(Karyawan karyawan) {
		return getId() == karyawan.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Karyawan) {
			Karyawan karyawan = (Karyawan) obj;
			return equals(karyawan);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
