package vn.iotstar.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Entity
@Table(name = "`User`")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(255)")
    private String email;

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
    private String userName;

    @Column(name = "fullname", nullable = false, columnDefinition = "nvarchar(255)")
    private String fullName;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(255)")
    private String passWord;

    @Column(name = "avatar", columnDefinition = "nvarchar(255)")
    private String avatar;

    @Column(name = "roleid", nullable = false)
    private int roleid;

    @Column(name = "phone", nullable = false, columnDefinition = "nvarchar(30)")
    private String phone;

    @Column(name = "createddate", nullable = false)
    private Date createdDate;

    @Column(name = "active", nullable = false, columnDefinition = "bit not null default 0")
    private boolean active;

    @Column(name = "otp_code", columnDefinition = "varchar(10)")
    private String otpCode;

    @Column(name = "otp_expiry")
    private Timestamp otpExpiry;

    @Column(name = "otp_type", columnDefinition = "varchar(30)")
    private String otpType;

    public User() {}

    public User(String email, String userName, String fullName, String passWord,
                String avatar, int roleid, String phone, Date createdDate) {
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.createdDate = createdDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) { this.passWord = passWord; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public int getRoleid() { return roleid; }
    public void setRoleid(int roleid) { this.roleid = roleid; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public Timestamp getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(Timestamp otpExpiry) { this.otpExpiry = otpExpiry; }
    public String getOtpType() { return otpType; }
    public void setOtpType(String otpType) { this.otpType = otpType; }
}
