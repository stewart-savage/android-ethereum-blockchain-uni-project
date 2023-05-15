package com.example.stewa.uniproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private double walletBalance=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //opens up navigation menu by clicking on white menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ImageButton navBarMenuButton = (ImageButton) findViewById(R.id.btn_drawer_menu);
        navBarMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //show balance of currently logged in user's wallet
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUserDetails = sharedPreferences.getString("credentials","error getting credentials");
        String[] currentUserDetailsArray = currentUserDetails.split("\n");
        final String userWalletAddress = currentUserDetailsArray[1];
        final TextView walletBalanceTextView = findViewById(R.id.tvWalletBalance);

        AsyncTask asyncTask = new AsyncTask() {
            @SuppressLint("WrongThread")
            @Override
            protected Object doInBackground(Object[] objects) {
                EthereumTransactionMaker ethereumTransactionMaker = new EthereumTransactionMaker();
                walletBalance = ethereumTransactionMaker.getWalletBalance(userWalletAddress);
                return null;
            }


            @Override
            protected void onPostExecute(Object o){
                walletBalanceTextView.setText("Balance: "+walletBalance+" Ether");
            }
        }.execute();

        //buttons on home screen
        Button logoutButton = (Button) findViewById(R.id.btn_home_log_out);
        Button addProductButton = (Button) findViewById(R.id.btn_home_add_product);
        Button editOrRemoveProductButton = (Button) findViewById(R.id.btn_home_edit_remove_product);
        Button orderProductButton = (Button) findViewById(R.id.btn_home_order_produce);
        Button orderHistoryButton = (Button) findViewById(R.id.btn_home_view_orders);
        Button salesHistoryButton = (Button) findViewById(R.id.btn_home_view_sales);
        //empties credentials and returns to login screen
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              logOut();

            }
        });

        //goes to add product screen
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddProductScreen();

            }
        });

        //goes to edit or remove product screen
        editOrRemoveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditOrRemoveProductScreen();

            }
        });

        //goes to order product screen
        orderProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrderProductScreen();
            }
        });

        //goes to order history screen
        orderHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrderHistoryScreen();
            }
        });

        //goes to order history screen
        salesHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewSalesScreen();
            }
        });
    }

    //nav bar buttons
    //home nav bar button
    public void navBarHome(MenuItem menuItem){
        Intent goToHomeScreen = new Intent(HomeActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(HomeActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToOrderHistoryScreen = new Intent(HomeActivity.this,OrderHistoryActivity.class);
        startActivity(goToOrderHistoryScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(HomeActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(HomeActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(HomeActivity.this,SalesHistoryActivity.class);
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

        Intent goToLoginScreen = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }



    //Home screen button functionality for navigation
    private void goToOrderProductScreen(){
        Intent goToOrderProductScreen = new Intent(HomeActivity.this, OrderProductActivity.class);
        startActivity(goToOrderProductScreen);
    }

    private void goToOrderHistoryScreen(){
        Intent goToViewOrdersScreen = new Intent(HomeActivity.this, OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
    }

    private void goToAddProductScreen(){

        Intent goToAddProductScreen = new Intent(HomeActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    private void goToEditOrRemoveProductScreen(){

        Intent goToEditOrRemoveProductScreen = new Intent(HomeActivity.this, EditOrRemoveProductActivity.class);
        startActivity(goToEditOrRemoveProductScreen);
    }

    private void goToViewSalesScreen(){
        Intent goToViewSalesScreen = new Intent(HomeActivity.this, SalesHistoryActivity.class);
        startActivity(goToViewSalesScreen);
    }

    private void logOut(){
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        //sets credentials to be empty when user logs out
        editor.putString("credentials", "");
        editor.commit();

        Intent goToLoginScreen = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }

}
