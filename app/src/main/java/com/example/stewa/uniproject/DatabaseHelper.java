package com.example.stewa.uniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String productTableName = "PRODUCTS";
    private static final String productID = "ID";
    private static final String productName = "Product_Name";
    private static final String productDescription = "Description";
    private static final String productWeight = "Product_Weight";
    private static final String productItemCost = "Item_Cost";
    private static final String productStock = "Stock_Remaining";
    private static final String productProducerName = "Producer_Name";

    public DatabaseHelper(Context context) {
        super(context, productTableName, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + productTableName);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + productTableName + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                productName + " TEXT," +
                productDescription + " TEXT," +
                productWeight + " REAL," +
                productItemCost + " REAL," +
                productStock + " INTEGER," +
                productProducerName + " TEXT)";
        db.execSQL(createTableStatement);
    }

    //gets db and adds product details to content values before inserting it as one statement
    public boolean addProductToDatabase(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues productContentValues = new ContentValues();
        productContentValues.put(productName, product.getName());
        productContentValues.put(productDescription, product.getDescription());
        productContentValues.put(productWeight, product.getWeight());
        productContentValues.put(productItemCost, product.getItemCost());
        productContentValues.put(productStock, product.getStock());
        productContentValues.put(productProducerName, product.getProducer());

        Log.d(TAG, "addProductToDatabase: adding " + product.getName() + " to " + productTableName + " table.");

        long outcomeOfInsert = db.insert(productTableName, null, productContentValues);

        //will return false if insert failed
        if (outcomeOfInsert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getProductsForCurrentUser(String currentUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        String productQuery = "SELECT " + productID + "," + productName + "," + productDescription + "," + productWeight +
                "," + productItemCost + "," + productStock + " FROM " + productTableName +
                " WHERE " + productProducerName + " = '" + currentUser + "'";
        Cursor result = db.rawQuery(productQuery, null);
        return result;
    }

    public Cursor getAllUnownedProducts(String currentUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        String productQuery = "SELECT * FROM " + productTableName +
                " WHERE "+productProducerName + " != '" + currentUser + "'";

        Cursor result = db.rawQuery(productQuery, null);
        return result;
    }

    //returns a product object based on the productID selected in the database
    public Product getProductByID(String selectedProductID){
        SQLiteDatabase db = this.getWritableDatabase();
        String productQuery = "SELECT * FROM " + productTableName + " WHERE "+ productID+ " = "+Integer.parseInt(selectedProductID);
        Cursor result = db.rawQuery(productQuery,null);
        result.moveToFirst();
        Product product = new Product(result.getString(1),result.getString(2),
                Double.parseDouble(result.getString(3)),
                Double.parseDouble(result.getString(4)),
                Integer.parseInt(result.getString(5)),
                result.getString(6));
        return product;
    }

    //deletes product from database by its productID
    public boolean deleteProductFromDatabase(String productIDString) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "deleteProductFromDatabase: deleting product ID " + productIDString + " from " + productTableName + " table.");

        long outcomeOfDelete = db.delete(productTableName, productID + "=?", new String[]{productIDString});

        //will return false if delete failed
        if (outcomeOfDelete == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateProductInDatabase(Product updatedProduct,String productIDString) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updatedProductContentValues = new ContentValues();
        updatedProductContentValues.put(productName, updatedProduct.getName());
        updatedProductContentValues.put(productDescription, updatedProduct.getDescription());
        updatedProductContentValues.put(productWeight, updatedProduct.getWeight());
        updatedProductContentValues.put(productItemCost, updatedProduct.getItemCost());
        updatedProductContentValues.put(productStock, updatedProduct.getStock());

        Log.d(TAG, "updateProductInDatabase: updating " + updatedProduct.getName() + " in " + productTableName + " table.");

        long outcomeOfInsert = db.update(productTableName, updatedProductContentValues, productID + "=?", new String[]{productIDString});

        //will return false if update failed
        if (outcomeOfInsert == -1) {
            return false;
        } else {
            return true;
        }
    }

}



