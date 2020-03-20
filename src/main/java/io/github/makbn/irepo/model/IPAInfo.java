package io.github.makbn.irepo.model;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name = "app_ipa_info")
public class IPAInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column
    @Enumerated
    @Builder.Default
    private Kind kind = Kind.software;

    @Column
    private String bundleIdentifier;

    @Column
    private String bundleVersion;

    @Column
    private String bundleName;

    @Column
    private long size;

    @Column
    private String minOsVersion;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getBundleIdentifier() {
        return bundleIdentifier;
    }

    public void setBundleIdentifier(String bundleIdentifier) {
        this.bundleIdentifier = bundleIdentifier;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getMinOsVersion() {
        return minOsVersion;
    }

    public void setMinOsVersion(String minOsVersion) {
        this.minOsVersion = minOsVersion;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    public enum Kind{
        software,
    }
}
