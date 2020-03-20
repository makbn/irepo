package io.github.makbn.irepo.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name = "app_ipa")
public class IPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column
    private String title;

    @Column(length = 1500)
    private String description;

    @Column
    private String uuid;

    @Column
    private String developerName;

    @Column
    private String developerEmail;

    @Column
    private String ssoId;

    @Column
    @CreationTimestamp
    private Date created;

    @Column
    @UpdateTimestamp
    private Date updated;

    @OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @Builder.Default
    private IPAInfo ipaInfo = new IPAInfo();

    @Column
    private String iconName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public IPAInfo getIpaInfo() {
        return ipaInfo;
    }

    public void setIpaInfo(IPAInfo ipaInfo) {
        this.ipaInfo = ipaInfo;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

}
