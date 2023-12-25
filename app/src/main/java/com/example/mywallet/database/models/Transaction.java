package com.example.mywallet.database.models;

public class Transaction {
    private long id;
    private double amount;
    private String dateCreated;
    private String dateUpdated;
    private long categoryId;
    private long userId;

    public Transaction(
            double amount,
            String dateCreated,
            String dateUpdated,
            long categoryId,
            long userId
    ) {
        setAmount(amount);
        setDateCreated(dateCreated);
        setDateUpdated(dateUpdated);
        setCategoryId(categoryId);
        setUserId(userId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
