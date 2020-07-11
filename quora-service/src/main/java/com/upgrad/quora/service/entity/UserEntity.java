package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "users", schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid"),
                @NamedQuery(name = "userById", query = "select u from UserEntity u where u.id = :id"),
                @NamedQuery(name = "userByEmail", query = "select u from UserEntity u where u.email =:email"),
                @NamedQuery(name = "userByUsername", query = "select u from UserEntity u where u.username =:username"),
                @NamedQuery(name = "deleteUserByUuid", query = "delete from UserEntity u where u.uuid = :uuid")
        }
)

public class UserEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 64)
    private String uuid;

    @Column(name = "FIRSTNAME")
    @NotNull
    @Size(max = 200)
    private String firstName;

    @Column(name = "LASTNAME")
    @NotNull
    @Size(max = 200)
    private String lastName;

    @Column(name = "USERNAME")
    @NotNull
    @Size(max = 200)
    private String username;

    @Column(name = "EMAIL")
    @NotNull
    @Size(max = 200)
    private String email;

    //@ToStringExclude
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SALT")
    @NotNull
    @Size(max = 200)
    //@ToStringExclude
    private String salt;

    @Column(name = "COUNTRY")
    @NotNull
    @Size(max = 50)
    private String country;

    @Column(name = "ABOUTME")
    @NotNull
    @Size(max = 200)
    private String aboutme;

    @Column(name = "DOB")
    @NotNull
    @Size(max = 200)
    private String dob;

    @Column(name = "ROLE")
    @Size(max = 50)
    private String role;

    @Column(name = "CONTACTNUMBER")
    @NotNull
    @Size(max = 50)
    private String contactNumber;

    /*
        @Version
        @Column(name="VERSION" , length=19 , nullable = false)
        private Long version;

        @Column(name="CREATED_BY")
        @NotNull
        private String createdBy;

        @Column(name="CREATED_AT")
        @NotNull
        private ZonedDateTime createdAt;

        @Column(name="MODIFIED_BY")
        private String modifiedBy;

        @Column(name="MODIFIED_AT")
        private ZonedDateTime modifiedAt;

        @Column(name = "FAILED_LOGIN_COUNT")
        @Min(0)
        @Max(5)
        private int failedLoginCount;

        @Column(name = "LAST_PASSWORD_CHANGE_AT")
        private ZonedDateTime lastPasswordChangeAt;

        @Column(name = "LAST_LOGIN_AT")
        private ZonedDateTime lastLoginAt;

        @Column(name = "STATUS")
        @NotNull
        private int status;
    */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    /*
        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public ZonedDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public String getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public ZonedDateTime getModifiedAt() {
            return modifiedAt;
        }

        public void setModifiedAt(ZonedDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
        }

        public int getFailedLoginCount() {
            return failedLoginCount;
        }

        public void setFailedLoginCount(int failedLoginCount) {
            this.failedLoginCount = failedLoginCount;
        }

        public ZonedDateTime getLastPasswordChangeAt() {
            return lastPasswordChangeAt;
        }

        public void setLastPasswordChangeAt(ZonedDateTime lastPasswordChangeAt) {
            this.lastPasswordChangeAt = lastPasswordChangeAt;
        }

        public ZonedDateTime getLastLoginAt() {
            return lastLoginAt;
        }

        public void setLastLoginAt(ZonedDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    */
    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }


}
