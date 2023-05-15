package com.example.stewa.uniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.SQLException;

import javax.inject.Inject;

import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class AddProductTest {

    @Rule
    public final ActivityTestRule<ProductAddActivity> mRule = new ActivityTestRule<ProductAddActivity>(ProductAddActivity.class,false,false);

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final Class mDbHelperClass = DatabaseHelper.class;
    private static final String productTableName = "PRODUCTS";
    private static final String productID = "ID";
    private static final String productName = "Product_Name";
    private static final String productDescription = "Description";
    private static final String productWeight = "Product_Weight";
    private static final String productItemCost = "Item_Cost";
    private static final String productStock = "Stock_Remaining";
    private static final String productProducerName = "Producer_Name";

    @Test
    public void insertTestProduct() {
        try {
            SQLiteOpenHelper dbHelper =
                    (SQLiteOpenHelper) mDbHelperClass
                            .getConstructor(Context.class)
                            .newInstance(mContext);

            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues testValues = new ContentValues();
            testValues.put(productName, "Broccoli");
            testValues.put(productDescription, "green");
            testValues.put(productWeight, 10.0);
            testValues.put(productItemCost, 0.05);
            testValues.put(productStock, 20);
            testValues.put(productProducerName, "Stewart");

            long insertedRowId = database.insert(
                    "PRODUCTS",
                    null,
                    testValues);

            assertNotEquals("Unable to insert into the database", -1, insertedRowId);
            dbHelper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
