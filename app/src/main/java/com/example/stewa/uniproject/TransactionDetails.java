package com.example.stewa.uniproject;

public class TransactionDetails {

    private String senderWalletAddress;
    private String senderPrivateKey;
    private String receiverWalletAddress;
    private String productName;
    private double productWeightTotal;
    private double transactionValueTotal;


    //this class makes it easier to move data from the order confirmation page and use it to create
    //a transaction on the blockchain
    public TransactionDetails(String senderWalletAddress,
                              String senderPrivateKey,
                              String receiverWalletAddress,
                              String productName,
                              double productWeightTotal,
                              double transactionValueTotal){

        this.senderWalletAddress = senderWalletAddress;
        this.senderPrivateKey = senderPrivateKey;
        this.receiverWalletAddress = receiverWalletAddress;
        this.productName = productName;
        this.productWeightTotal = productWeightTotal;
        this.transactionValueTotal = transactionValueTotal;

    }

    //getters and setters
    public String getSenderWalletAddress() {
        return senderWalletAddress;
    }

    public void setSenderWalletAddress(String senderWalletAddress) {
        this.senderWalletAddress = senderWalletAddress;
    }

    public String getSenderPrivateKey() {
        return senderPrivateKey;
    }

    public void setSenderPrivateKey(String senderPrivateKey) {
        this.senderPrivateKey = senderPrivateKey;
    }

    public String getReceiverWalletAddress() {
        return receiverWalletAddress;
    }

    public void setReceiverWalletAddress(String receiverWalletAddress) {
        this.receiverWalletAddress = receiverWalletAddress;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductWeightTotal() {
        return productWeightTotal;
    }

    public void setProductWeightTotal(double productWeightTotal) {
        this.productWeightTotal = productWeightTotal;
    }

    public double getTransactionValueTotal() {
        return transactionValueTotal;
    }

    public void setTransactionValueTotal(double transactionValueTotal) {
        this.transactionValueTotal = transactionValueTotal;
    }
}
