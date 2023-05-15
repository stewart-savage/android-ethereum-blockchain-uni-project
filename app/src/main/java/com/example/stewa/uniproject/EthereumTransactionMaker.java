package com.example.stewa.uniproject;

/** web3j android framework source available from <a href="https://github.com/web3j/web3j/tree/android"></a>*/
import android.os.AsyncTask;


import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

//makes synchronous requests
public class EthereumTransactionMaker {

    TransactionDetails transactionDetails;

    public EthereumTransactionMaker(TransactionDetails transactionDetails){
        this.transactionDetails = transactionDetails;
    }

    public EthereumTransactionMaker(){

    }

    //address of the ganache test server, hosted on the wifi-address of mobile hotspot
    private final static String GANACHE_ADDRESS = "http://192.168.43.231:8545";

    //from prototype demo
    //these are the addresses and private keys of the first 2 pre-created addresses on my Ganache
    //test network, and cannot be changed
    private final static String ADDRESS_1 = "0x0088b08aBab996eF368FF7F4c6c1834B5F348C72";
    private final static String ADDRESS_1_PRIVATE_KEY = "23de68a8c5e9759e65dc5f1f4ca75a9a3e76c94eb8c0cb276fa1dc14b9936d9d";
    private final static String ADDRESS_2 = "0xC408093B7EbEe303d3f9D64be6DAC134bdfC2B7b";
    private final static String ADDRESS_2_PRIVATE_KEY = "fe95d8bee9ecc51ba648f9bfcceecbce03d47f15ffab25922328e9348cfc99a7";
    private final static String ADDRESS_3 = "0x4Ff3847eD1511F69ce20e3b09c626871D0FEaEa1";
    private final static String ADDRESS_3_PRIVATE_KEY = "37a70f9c305dc666c4f9a77eaa97da6803a9fe1912e32b3570c4480b5508f131";

    private String transactionHash = null;


    //gas is the cost to execute a transaction on the Ethereum blockchain network, GAS_PRICE is the price per unit of gas
    // and GAS_LIMIT is the maximum cost in gas of any 1 transaction
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(2000000000L);

    //establishes an instance of web3j that can be used to interact with the test Ethereum network
    private Web3j web3jInstance = Web3jFactory.build(new HttpService(GANACHE_ADDRESS));

    //gets the version of Ethereum being used by the server (e.g. test server uses EthereumJS TestRPC/v2.1.0/ethereum-js)
    public String getClientVersion() {

        Web3ClientVersion web3ClientVersion = null;
        try {
           web3ClientVersion = web3jInstance.web3ClientVersion().send();
           String clientVersionString = web3ClientVersion.getWeb3ClientVersion();
            System.out.println("Client version is: "+clientVersionString);
            return clientVersionString;
        }catch(IOException ioException) {
            ioException.printStackTrace();
            return "Error getting client version";
        }
    }

    public String getTransactionHash(){
        return transactionHash;
    }


    public double getWalletBalance(String walletAddress){

        try {
            //converts wallet balance from wei to ether
            EthGetBalance ethGetBalance = web3jInstance.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger weiInt = ethGetBalance.getBalance();
            BigDecimal weiDecimal = new BigDecimal(weiInt);
            BigDecimal walletBalanceRaw = Convert.fromWei(weiDecimal, Convert.Unit.ETHER);
            double walletBalance = walletBalanceRaw.setScale(3, RoundingMode.HALF_UP).doubleValue();

            return walletBalance;
        }catch (Exception ie){
            ie.printStackTrace();
        }
        return 0;
    }

    //returns true is successful transaction is completed
    public boolean successfulTransaction(){
        transactionHash = executeTransactionBetweenBuyerAndSeller();
        if(!transactionHash.equals("Error completing transaction")){
            return true;
        }else return false;
    }

    //sends the transactionValue quantity of Ether from sender's wallet to receiver's wallet
    //and returns the transaction hash as a string
    public String executeTransactionBetweenBuyerAndSeller(){
    try {
        TransactionManager senderTransactionManager = prepareTransaction(transactionDetails.getSenderPrivateKey());
        TransactionReceipt transactionReceipt = executeTransaction(senderTransactionManager,transactionDetails.getReceiverWalletAddress(),transactionDetails.getTransactionValueTotal());
        String transactionHash = transactionReceipt.getTransactionHash();
        System.out.println("Transaction Hash = "+ transactionHash);
        return  transactionHash;
    }catch (Exception e){
        e.printStackTrace();
    }return "Error completing transaction";
    }


//TRANSACTION METHODS
    //Creates a transaction and returns its receipt sends Ether from the private key address in the
    // TransactionManager to the recipient address
    private TransactionReceipt executeTransaction(TransactionManager transactionManager, String recipientAddress, double transactionValue){

        BigDecimal bigDecimalTransactionValue = BigDecimal.valueOf(transactionValue);
        Transfer transfer = new Transfer(web3jInstance,transactionManager);
        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = transfer.sendFunds(
                    recipientAddress,
                    bigDecimalTransactionValue,
                    Convert.Unit.ETHER,
                    GAS_PRICE,
                    GAS_LIMIT)
                    .sendAsync()
                    .get();
            return transactionReceipt;
        }catch (Exception e){
            e.printStackTrace();
        }return transactionReceipt;
    }

    //this method will only be used in the public Ethereum network, not the test server
    private Credentials getWalletCredentials() throws IOException, CipherException {

        Credentials credentials = WalletUtils.loadCredentials(
                "password",
                "walletPathString"
        );
        return credentials;
    }

    //creates credentials using private key of an address on the network, for the test server
    private Credentials getPrivateKeyCredentials(String addressPrivateKey){

        return Credentials.create(addressPrivateKey);
    }

    //sets up TransactionManager which includes sender's credentials
    private TransactionManager prepareTransaction(String addressPrivateKey){

        TransactionManager transactionManager = new RawTransactionManager(web3jInstance, getPrivateKeyCredentials(addressPrivateKey));
        return transactionManager;
    }}

