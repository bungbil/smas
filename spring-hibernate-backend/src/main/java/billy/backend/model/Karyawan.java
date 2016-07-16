package billy.backend.model;

import java.util.Arrays;
import java.util.Date;

public class Karyawan implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private String kodeKaryawan;
  private String namaPanggilan;
  private String namaKtp;
  private String ktp;
  private String jenisKelamin;
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
  private Date tanggalMulaiKerja;
  private Date tanggalBerhentiKerja;

  public Karyawan() {}

  public Karyawan(long id, String kodeKaryawan) {
    this.setId(id);
    this.kodeKaryawan = kodeKaryawan;
  }

  public Karyawan(long id, String kodeKaryawan, String namaPanggilan) {
    this.setId(id);
    this.kodeKaryawan = kodeKaryawan;
    this.namaPanggilan = namaPanggilan;
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

  public String getAlamat() {
    return alamat;
  }

  public String getCatatan() {
    return catatan;
  }

  public String getEmail() {
    return email;
  }

  public String getHandphone() {
    return handphone;
  }

  @Override
  public long getId() {
    return id;
  }

  public String getInisialDivisi() {
    return inisialDivisi;
  }

  public String getJenisKelamin() {
    return jenisKelamin;
  }


  public JobType getJobType() {
    return jobType;
  }

  public String getKodeKaryawan() {
    return kodeKaryawan;
  }

  public String getKtp() {
    return ktp;
  }

  public byte[] getKtpImage() {
    return ktpImage;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public String getNamaKtp() {
    return namaKtp;
  }

  public String getNamaPanggilan() {
    return namaPanggilan;
  }

  public byte[] getProfileImage() {
    return profileImage;
  }

  public String getStatusDivisi() {
    return statusDivisi;
  }

  public Karyawan getSupervisorDivisi() {
    return supervisorDivisi;
  }

  public Date getTanggalBerhentiKerja() {
    return tanggalBerhentiKerja;
  }

  public Date getTanggalLahir() {
    return tanggalLahir;
  }

  public Date getTanggalMulaiKerja() {
    return tanggalMulaiKerja;
  }

  public String getTelepon() {
    return telepon;
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

  @Override
  public boolean isNew() {
    return (getId() == Long.MIN_VALUE + 1);
  }

  public void setAlamat(String alamat) {
    this.alamat = alamat;
  }

  public void setCatatan(String catatan) {
    this.catatan = catatan;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setHandphone(String handphone) {
    this.handphone = handphone;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public void setInisialDivisi(String inisialDivisi) {
    this.inisialDivisi = inisialDivisi;
  }

  public void setJenisKelamin(String jenisKelamin) {
    this.jenisKelamin = jenisKelamin;
  }

  public void setJobType(JobType jobType) {
    this.jobType = jobType;
  }

  public void setKodeKaryawan(String kodeKaryawan) {
    this.kodeKaryawan = kodeKaryawan;
  }

  public void setKtp(String ktp) {
    this.ktp = ktp;
  }

  public void setKtpImage(byte[] ktpImage) {
    this.ktpImage = ktpImage;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setNamaKtp(String namaKtp) {
    this.namaKtp = namaKtp;
  }

  public void setNamaPanggilan(String namaPanggilan) {
    this.namaPanggilan = namaPanggilan;
  }

  public void setProfileImage(byte[] profileImage) {
    this.profileImage = profileImage;
  }

  public void setStatusDivisi(String statusDivisi) {
    this.statusDivisi = statusDivisi;
  }

  public void setSupervisorDivisi(Karyawan supervisorDivisi) {
    this.supervisorDivisi = supervisorDivisi;
  }

  public void setTanggalBerhentiKerja(Date tanggalBerhentiKerja) {
    this.tanggalBerhentiKerja = tanggalBerhentiKerja;
  }

  public void setTanggalLahir(Date tanggalLahir) {
    this.tanggalLahir = tanggalLahir;
  }

  public void setTanggalMulaiKerja(Date tanggalMulaiKerja) {
    this.tanggalMulaiKerja = tanggalMulaiKerja;
  }

  public void setTelepon(String telepon) {
    this.telepon = telepon;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return String
        .format(
            "Karyawan [id=%s, version=%s, kodeKaryawan=%s, namaPanggilan=%s, namaKtp=%s, ktp=%s, jenisKelamin=%s, tanggalLahir=%s, telepon=%s, handphone=%s, email=%s, alamat=%s, jobType=%s, inisialDivisi=%s, statusDivisi=%s, supervisorDivisi=%s, lastUpdate=%s, updatedBy=%s, catatan=%s, profileImage=%s, ktpImage=%s, tanggalMulaiKerja=%s, tanggalBerhentiKerja=%s]",
            id, version, kodeKaryawan, namaPanggilan, namaKtp, ktp, jenisKelamin, tanggalLahir,
            telepon, handphone, email, alamat, jobType, inisialDivisi, statusDivisi,
            supervisorDivisi, lastUpdate, updatedBy, catatan, Arrays.toString(profileImage),
            Arrays.toString(ktpImage), tanggalMulaiKerja, tanggalBerhentiKerja);
  }

}
