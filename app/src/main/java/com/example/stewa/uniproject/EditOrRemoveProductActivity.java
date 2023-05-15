package com.example.stewa.uniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EditOrRemoveProductActivity extends AppCompatActivity {
    
    private DrawerLayout drawerLayout;
    private ListView productListView;
    private DatabaseHelper dbHelper;
    private ArrayList<String> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_remove_product);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ImageButton navBarMenuButton = (ImageButton) findViewById(R.id.btn_edit_product_drawer_menu);
        navBarMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        final TextView tvNoProductsAdded = (TextView) findViewById(R.id.tv_no_products_added);
        final Button btnNoProductsAdded = (Button) findViewById(R.id.btn_no_products_added);
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        final String currentUserCredentials = sharedPreferences.getString("credentials", null);
        final String currentUser = getCurrentUserNameFromSharedPrefs(currentUserCredentials);

        productListView = (ListView) findViewById(R.id.product_list_view);
        ArrayList<String> productViewList = new ArrayList<>();
        dbHelper = new DatabaseHelper(EditOrRemoveProductActivity.this);
        Cursor productResults = dbHelper.getProductsForCurrentUser(currentUser);

        //if no products are added, show a message and button that takes the user to
        //the product add page
        if (productResults.getCount() == 0) {
            tvNoProductsAdded.setVisibility(View.VISIBLE);
            btnNoProductsAdded.setVisibility(View.VISIBLE);
            btnNoProductsAdded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToAddProductScreen = new Intent(EditOrRemoveProductActivity.this, ProductAddActivity.class);
                    startActivity(goToAddProductScreen);
                    finish();
                }
            });
            //else show a listview of all the products added by the current user and hide the
            //button and message that ask the user to add a new product
        } else {
            tvNoProductsAdded.setVisibility(View.INVISIBLE);
            btnNoProductsAdded.setVisibility(View.INVISIBLE);
            while (productResults.moveToNext()) {
                productViewList.add("ID: "+productResults.getString(0) + "\n" +
                       "Product Name: "+ productResults.getString(1) + "\n" +
                        "Description: "+productResults.getString(2) + "\n" +
                        "Weight (kg): "+productResults.getString(3) + "\n" +
                        "Cost per item (ether): "+productResults.getString(4) + "\n" +
                       "Stock: "+ productResults.getString(5));
                productList.add(productResults.getString(0) + "\n" +
                        productResults.getString(1) + "\n" +
                        productResults.getString(2) + "\n" +
                        productResults.getString(3) + "\n" +
                        productResults.getString(4) + "\n" +
                        productResults.getString(5));
                ListAdapter productListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productViewList);
                productListView.setAdapter(productListAdapter);
            }
        }

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productListItemValue = productList.get(position).toString();
                goToProductEdit(productListItemValue);
            }
        });
    }

    //nav bar buttons
    //home nav bar button
    public void navBarHome(MenuItem menuItem){
        Intent goToHomeScreen = new Intent(EditOrRemoveProductActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(EditOrRemoveProductActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToViewOrdersScreen = new Intent(EditOrRemoveProductActivity.this,OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(EditOrRemoveProductActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(EditOrRemoveProductActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(EditOrRemoveProductActivity.this,SalesHistoryActivity.class);
        startActivity(goToViewSalesScreen);
        finish();
    }

    //logout nav bar button
    public void navBarLogOut(MenuItem menuItem){
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        //sets credentials to be empty when user logs out
        editor.putString("credentials", "");
        editor.commit();

        Intent goToLoginScreen = new Intent(EditOrRemoveProductActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }

    //opens ProductEditActivity and passes the value of the product clicked on the product list
    private void goToProductEdit(String productListItemValue) {
        Intent goToProductEditScreen = new Intent(EditOrRemoveProductActivity.this, ProductEditActivity.class);
        String productDetails = productListItemValue;
        Bundle bundle = new Bundle();
        bundle.putString("productDetails", productDetails);
        goToProductEditScreen.putExtras(bundle);
        startActivity(goToProductEditScreen);
    }

    //looks through shared preferences to get username of currently logged in user
    private String getCurrentUserNameFromSharedPrefs(String currentUserDetails) {

        System.out.println(currentUserDetails);
        int indexValue = currentUserDetails.indexOf("\n");
        if (indexValue == -1) {
            System.out.println("oops, error getting current user name from shared preferences.");
            return "";
        } else {
            String currentUserName = currentUserDetails.substring(0, indexValue);
            System.out.println("producer name: " + currentUserName);
            return currentUserName;
        }
    }
}
