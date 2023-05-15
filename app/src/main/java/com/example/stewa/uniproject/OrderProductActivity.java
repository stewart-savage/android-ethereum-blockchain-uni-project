package com.example.stewa.uniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OrderProductActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView orderProductListView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product);

        //opens up navigation menu by clicking on white menu icon

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ImageButton navBarMenuButton = (ImageButton) findViewById(R.id.btn_order_product_drawer_menu);
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

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        final String currentUserCredentials = sharedPreferences.getString("credentials", null);
        String currentUser = getCurrentUserNameFromSharedPrefs(currentUserCredentials);

        orderProductListView = (ListView) findViewById(R.id.order_product_list_view);
        ArrayList<String> orderProductList = new ArrayList<>();
        dbHelper = new DatabaseHelper(OrderProductActivity.this);
        Cursor orderProductResults = dbHelper.getAllUnownedProducts(currentUser);

        if (orderProductResults.getCount() == 0) {
            displayNoProductsFoundError("There are no products available, or a database " +
                    "error has occurred.", "No Products Added");
        } else {
            while (orderProductResults.moveToNext()) {
                orderProductList.add(orderProductResults.getString(0) + "\n" +
                        "Product Name: "+orderProductResults.getString(1) + "\n" +
                        "Cost(ETH): "+orderProductResults.getString(4) + "\n");
                ListAdapter productListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderProductList);
                orderProductListView.setAdapter(productListAdapter);
            }
        }

        //opens the order confirmation page for the selected product by passing its product ID
        //to be used in a SQL select statement in the OrderConfirmationActivity class
        orderProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productListItemValue = (String) orderProductListView.getItemAtPosition(position);
                String[] productListItemValueArray = productListItemValue.split("\n");
                String productListItemValueID = productListItemValueArray[0];
                goToOrderConfirmation(productListItemValueID);
            }
        });



    }

    //looks through shared preferences to get username of current user, so that only products not
    //added by the current user are displayed
    private String getCurrentUserNameFromSharedPrefs(String currentUserDetails) {

        System.out.println(currentUserDetails);
        int indexValue = currentUserDetails.indexOf("\n");
        if (indexValue == -1) {
            System.out.println("oops, error getting current user name from shared preferences.");
            return "";
        } else {
            String producerName = currentUserDetails.substring(0, indexValue);
            System.out.println("current user name: "+producerName);
            return producerName;
        }
    }

    //nav bar buttons
    //home nav bar button
    public void navBarHome(MenuItem menuItem){
        Intent goToHomeScreen = new Intent(OrderProductActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(OrderProductActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToViewOrdersScreen = new Intent(OrderProductActivity.this,OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(OrderProductActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(OrderProductActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(OrderProductActivity.this,SalesHistoryActivity.class);
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

        Intent goToLoginScreen = new Intent(OrderProductActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }

    private void goToOrderConfirmation(String productListItemValueID){
        Intent goToOrderConfirmationScreen = new Intent(OrderProductActivity.this, OrderConfirmationActivity.class);
        String productID = productListItemValueID;
        Bundle bundle = new Bundle();
        bundle.putString("productID", productID);
        goToOrderConfirmationScreen.putExtras(bundle);
        startActivity(goToOrderConfirmationScreen);
    }


    //this should be displayed if there are no products added by the currently logged in user
    private void displayNoProductsFoundError(String errorMessage, String errorName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(errorMessage);
        alertDialogBuilder.setTitle(errorName);
        alertDialogBuilder.setCancelable(true);
    }

}
