package dev.edu.poly.Model;

public class Cart {
    private String idKey;
    private String idUser;
    private String idProduct;
    private int quantity;

    public Cart() {
    }

    public Cart(String idKey, String idUser, String idProduct, int quantity) {
        this.idKey = idKey;
        this.idUser = idUser;
        this.idProduct = idProduct;
        this.quantity = quantity;
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

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
