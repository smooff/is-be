package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import sk.stuba.sdg.isbe.domain.enums.UserPermissionEnum;

public class User {
    @Id
    private String uid;
    private String name;
    private String mail;
    private String password;
    private UserPermissionEnum permissions;
    private Long createdAt;
    private boolean deactivated;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserPermissionEnum getPermissions() {
        return permissions;
    }

    public void setPermissions(UserPermissionEnum permissions) {
        this.permissions = permissions;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
