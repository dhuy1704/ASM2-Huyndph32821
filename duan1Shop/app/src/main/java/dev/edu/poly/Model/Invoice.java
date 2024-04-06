package dev.edu.poly.Model;

public class Invoice {
    private String idKey;
    private String idUser;
    private String total;
    private String address;
    private String email;
    private String date;
    private int status;

    public Invoice() {
    }

    public Invoice(String idKey, String idUser, String total, String address, String email, String date, int status) {
        this.idKey = idKey;
        this.idUser = idUser;
        this.total = total;
        this.address = address;
        this.email = email;
        this.date = date;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
