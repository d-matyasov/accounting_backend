package ru.matyasov.app.accounting.dto;

import ru.matyasov.app.accounting.models.references.system.UserRole;

public class UserDto extends BaseEntityDto {

    private String login;

    private boolean isEnabled;

    private UserRole role;

    public UserDto() {
    }

    public UserDto(Integer id, String login, String password, boolean isEnabled, UserRole role) {
        super(id);
        this.login = login;
        this.isEnabled = isEnabled;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
