package antifraud.model.user;

public enum Role {
    ADMINISTRATOR,
    MERCHANT,
    SUPPORT;

    public String withPrefix() {
        return "ROLE_" + this.name();
    }
}
