package dev.edu.poly.Model;

public class User {
    private String idKey;
    private String name;
    private String email;
    private String password;
    private String role;

    public User() {
    }

    public User(String idKey, String name, String email, String password) {
        this.idKey = idKey;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "user";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
