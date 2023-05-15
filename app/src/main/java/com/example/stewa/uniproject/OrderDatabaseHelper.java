package com.example.stewa.uniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class OrderDatabaseHelper extends SQLiteOpenHelper {

    private static final String orderHistoryTableName = "ORDER_HISTORY";
    private static final String orderTransactionHash = "Transaction_Hash";
    private static final String orderProductName = "Product_Name";
    private static final String orderWeightTotal = "Total_Weight";
    private static final String orderValueTotal = "Total_Value";
    private static final String orderTimestamp = "Timestamp";
    private static final String orderBuyerName = "Buyer_Name";
    private static final String orderBuyerAddress = "Buyer_Address";
    private static final String orderSellerName = "Seller_Name";
    private static final String orderSellerAddress = "Seller_Address";

    public OrderDatabaseHelper(Context context) {
        super(context, orderHistoryTableName, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + orderHistoryTableName);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + orderHistoryTableName + " (Transaction_Hash String PRIMARY KEY, " +
                orderProductName + " TEXT," +
                orderWeightTotal + " REAL," +
                orderValueTotal + " REAL," +
                orderTimestamp + " TEXT," +
                orderBuyerName + " TEXT," +
                orderBuyerAddress + " TEXT," +
                orderSellerName + " TEXT," +
                orderSellerAddress + " TEXT)";
        db.execSQL(createTableStatement);
    }

    //gets db and adds product details to content values before inserting it as one statement
    public boolean addOrderToDatabase(TransactionDetails transactionDetails, String timestamp, String transactionHash, String buyerName, String sellerName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues orderContentValues = new ContentValues();
        orderContentValues.put(orderTransactionHash, transactionHash);
        orderContentValues.put(orderProductName, transactionDetails.getProductName());
        orderContentValues.put(orderWeightTotal, transactionDetails.getProductWeightTotal());
        orderContentValues.put(orderValueTotal, transactionDetails.getTransactionValueTotal());
        orderContentValues.put(orderTimestamp, timestamp);
        orderContentValues.put(orderBuyerName,buyerName);
        orderContentValues.put(orderBuyerAddress,transactionDetails.getReceiverWalletAddress());
        orderContentValues.put(orderSellerName,sellerName);
        orderContentValues.put(orderSellerAddress,transactionDetails.getSenderWalletAddress());

        Log.d(TAG, "addOrderToDatabase: adding " + transactionHash + " to " + orderHistoryTableName + " table.");

        long outcomeOfInsert = db.insert(orderHistoryTableName, null, orderContentValues);

        //will return false if insert failed
        if (outcomeOfInsert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getOrderedProductsForCurrentUser(String currentUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        String productQuery = "SELECT " + orderTransactionHash + "," + orderProductName + "," + orderWeightTotal + "," + orderValueTotal +
                "," + orderTimestamp + "," + orderBuyerName + "," + orderBuyerAddress + "," + orderSellerName + "," + orderSellerAddress + " FROM " + orderHistoryTableName +
                " WHERE " + orderBuyerName + " = '" + currentUser + "'";
        Cursor result = db.rawQuery(productQuery, null);
        return result;
    }

    public Cursor getSoldProductsForCurrentUser(String currentUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        String productQuery = "SELECT " + orderTransactionHash + "," + orderProductName + "," + orderWeightTotal + "," + orderValueTotal +
                "," + orderTimestamp + "," + orderBuyerName + "," + orderBuyerAddress + "," + orderSellerName + "," + orderSellerAddress + " FROM " + orderHistoryTableName +
                " WHERE " + orderSellerName + " = '" + currentUser + "'";
        Cursor result = db.rawQuery(productQuery, null);
        return result;
    }
//
//    //returns a product object based on the productID selected in the database
//    public Product getProductByID(String selectedOrderTransactionHash){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String productQuery = "SELECT * FROM " + orderHistoryTableName + " WHERE "+ orderTransactionHash+ " = "+Integer.parseInt(selectedOrderTransactionHash);
//        Cursor result = db.rawQuery(productQuery,null);
//        result.moveToFirst();
//        Product product = new Product(result.getString(1),result.getString(2),
//                Double.parseDouble(result.getString(3)),
//                Double.parseDouble(result.getString(4)),
//                Integer.parseInt(result.getString(5)),
//                result.getString(6));
//        return product;
//    }
//
//    //deletes product from database by its productID
//    public boolean deleteProductFromDatabase(String orderTransactionHashString) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Log.d(TAG, "deleteProductFromDatabase: deleting product ID " + orderTransactionHashString + " from " + orderHistoryTableName + " table.");
//
//        long outcomeOfDelete = db.delete(orderHistoryTableName, orderTransactionHash + "=?", new String[]{orderTransactionHashString});
//
//        //will return false if delete failed
//        if (outcomeOfDelete == -1) {
//            return false;
//        } else {
//            return true;
//        }
//    }

}
