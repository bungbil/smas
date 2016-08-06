package billy.backend.model;

import java.util.Arrays;


public class KaryawanImages implements java.io.Serializable, Entity {

  private static final long serialVersionUID = 1L;

  private long id = Long.MIN_VALUE + 1;
  private int version;
  private Karyawan karyawan;

  private byte[] profileImage;
  private byte[] ktpImage;

  public KaryawanImages() {}


  public boolean equals(KaryawanImages karyawan) {
    return getId() == karyawan.getId();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof KaryawanImages) {
      KaryawanImages karyawan = (KaryawanImages) obj;
      return equals(karyawan);
    }

    return false;
  }


  @Override
  public long getId() {
    return id;
  }


  public Karyawan getKaryawan() {
    return karyawan;
  }


  public byte[] getKtpImage() {
    return ktpImage;
  }


  public byte[] getProfileImage() {
    return profileImage;
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

  @Override
  public void setId(long id) {
    this.id = id;
  }


  public void setKaryawan(Karyawan karyawan) {
    this.karyawan = karyawan;
  }


  public void setKtpImage(byte[] ktpImage) {
    this.ktpImage = ktpImage;
  }


  public void setProfileImage(byte[] profileImage) {
    this.profileImage = profileImage;
  }


  public void setVersion(int version) {
    this.version = version;
  }


  @Override
  public String toString() {
    return String.format(
        "KaryawanImages [id=%s, version=%s, karyawan=%s, profileImage=%s, ktpImage=%s]", id,
        version, karyawan, Arrays.toString(profileImage), Arrays.toString(ktpImage));
  }


}
