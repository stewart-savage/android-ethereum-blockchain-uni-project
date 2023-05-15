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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView orderHistoryListView;
    private OrderDatabaseHelper orderdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        //opens up navigation menu by clicking on white menu icon

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ImageButton navBarMenuButton = (ImageButton) findViewById(R.id.btn_order_history_drawer_menu);
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

        final TextView tvNoOrdersMade = (TextView) findViewById(R.id.tv_no_orders_made);
        final Button btnNoOrdersMade = (Button) findViewById(R.id.btn_no_orders_made);
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        final String currentUserCredentials = sharedPreferences.getString("credentials", null);
        String currentUser = getCurrentUserNameFromSharedPrefs(currentUserCredentials);

        orderHistoryListView = (ListView) findViewById(R.id.order_history_list_view);
        ArrayList<String> orderProductList = new ArrayList<>();
        orderdbHelper = new OrderDatabaseHelper(OrderHistoryActivity.this);
        Cursor orderHistoryResults = orderdbHelper.getOrderedProductsForCurrentUser(currentUser);

        if (orderHistoryResults.getCount() == 0) {
            displayNoHistoryFoundError("You have not ordered any products, or a database " +
                    "error has occurred.", "No Products Ordered");
            tvNoOrdersMade.setVisibility(View.VISIBLE);
            btnNoOrdersMade.setVisibility(View.VISIBLE);
            btnNoOrdersMade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToAddProductScreen = new Intent(OrderHistoryActivity.this, OrderProductActivity.class);
                    startActivity(goToAddProductScreen);
                    finish();
                }
            });
        } else {
            while (orderHistoryResults.moveToNext()) {
                orderProductList.add("Transaction Hash: "+orderHistoryResults.getString(0) + "\n" +
                        "Product Name: "+orderHistoryResults.getString(1) + "\n" +
                        "Total Value: "+orderHistoryResults.getString(3)+" Ether" + "\n" +
                        "Total Weight: "+orderHistoryResults.getString(2)+" kg" + "\n" +
                        "Seller: "+orderHistoryResults.getString(7) + "\n" +
                        "Timestamp: "+orderHistoryResults.getString(4) + "\n");
                ListAdapter OrderListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderProductList);
                orderHistoryListView.setAdapter(OrderListAdapter);
            }
        }
    }

    //looks through shared preferences to get username of current user
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
        Intent goToHomeScreen = new Intent(OrderHistoryActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(OrderHistoryActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToViewOrdersScreen = new Intent(OrderHistoryActivity.this,OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(OrderHistoryActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(OrderHistoryActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(OrderHistoryActivity.this,SalesHistoryActivity.class);
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

        Intent goToLoginScreen = new Intent(OrderHistoryActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }

    //this should be displayed if no products have been ordered by the currently logged in user
    private void displayNoHistoryFoundError(String errorMessage, String errorName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(errorMessage);
        alertDialogBuilder.setTitle(errorName);
        alertDialogBuilder.setCancelable(true);
    }

}
