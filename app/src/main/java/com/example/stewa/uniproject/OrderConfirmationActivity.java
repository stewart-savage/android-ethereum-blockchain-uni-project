package com.example.stewa.uniproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class OrderConfirmationActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private DatabaseHelper productDBHelper;
    private Double transactionTotal;
    private Double weightTotal;
    private String transactionHash;
    private String timestamp;
    private TransactionDetails transactionDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        //opens up navigation menu by clicking on white menu icon

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ImageButton navBarMenuButton = (ImageButton) findViewById(R.id.btn_order_confirm_product_drawer_menu);
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

        //gets product selected in previous page from its passed on productID
        Bundle bundle = getIntent().getExtras();
        String productID = bundle.getString("productID");
        productDBHelper = new DatabaseHelper(OrderConfirmationActivity.this);
        final Product selectedProduct = productDBHelper.getProductByID(productID);

        final TextView tvEmptyQuantityFieldError = (TextView) findViewById(R.id.tv_product_order_confirm_empty_quantity_error);
        final TextView tvTotalTransactionCost = (TextView) findViewById(R.id.tv_product_order_total_cost);
        final TextView tvProductName = (TextView) findViewById(R.id.tv_order_product_name);
        final TextView tvProductDescription = (TextView) findViewById(R.id.tv_order_product_description);
        final TextView tvProductWeight = (TextView) findViewById(R.id.tv_order_product_weight);
        final TextView tvProductCost = (TextView) findViewById(R.id.tv_order_product_cost);
        final TextView tvProductStock = (TextView) findViewById(R.id.tv_order_product_current_stock);
        final TextView tvProductProducerName = (TextView) findViewById(R.id.tv_order_product_producer_name);
        final EditText etQuantity = (EditText) findViewById(R.id.et_order_product_quantity);
        final Button btnOrderProduct = (Button) findViewById(R.id.btn_product_order_confirm);

        //populates textviews with fields of previously selected productID
        tvProductName.setText("Name: " + selectedProduct.getName());
        tvProductDescription.setText("Description: " + selectedProduct.getDescription());
        tvProductWeight.setText("Weight (in KG): " + Double.toString(selectedProduct.getWeight()));
        tvProductCost.setText("Cost per Item (in Ether): " + Double.toString(selectedProduct.getItemCost()));
        tvProductStock.setText("Stock Remaining: " + Integer.toString(selectedProduct.getStock()));
        tvProductProducerName.setText("Sold by: " + selectedProduct.getProducer());

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUserDetails = sharedPreferences.getString("credentials","error getting credentials");
        String[] currentUserDetailsArray = currentUserDetails.split("\n");
        final String buyer = currentUserDetailsArray[0];
        final String buyerWalletAddress = currentUserDetailsArray[1];
        final String buyerWalletPrivateKey = currentUserDetailsArray[2];

        final String seller = selectedProduct.getProducer();
        final String sellerWalletAddress = sharedPreferences.getString(seller+"walletAddress","error getting seller wallet address");

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvTotalTransactionCost.setVisibility(View.INVISIBLE);
            }

            //sets transaction total to be the cost per item multiplied by quantity selected
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTotalTransactionCost.setVisibility(View.VISIBLE);
                if (!etQuantity.getText().toString().equals("")) {
                    transactionTotal = Double.parseDouble(etQuantity.getText().toString()) * selectedProduct.getItemCost();
                    weightTotal = Double.parseDouble(etQuantity.getText().toString()) * selectedProduct.getWeight();
                    String transactionTotalString = "Total: " + transactionTotal.toString();
                    tvTotalTransactionCost.setText(transactionTotalString);
                } else {
                    String emptyQuantityTotal = "Please Enter a Quantity";
                    tvTotalTransactionCost.setText(emptyQuantityTotal);
                }
            }

            //sets transaction total to be the cost per item multiplied by quantity selected
            @Override
            public void afterTextChanged(Editable s) {
                tvTotalTransactionCost.setVisibility(View.VISIBLE);
                if (!etQuantity.getText().toString().equals("")) {
                    transactionTotal = Integer.parseInt(etQuantity.getText().toString()) * selectedProduct.getItemCost();
                    weightTotal = Double.parseDouble(etQuantity.getText().toString()) * selectedProduct.getWeight();
                    String transactionTotalString = "Total: " + transactionTotal.toString()+" Ether";
                    tvTotalTransactionCost.setText(transactionTotalString);
                } else {
                    String emptyQuantityTotal = "Please Enter a Quantity";
                    tvTotalTransactionCost.setText(emptyQuantityTotal);
                }
            }
        });

        //creates a transaction on the blockchain with the details on the order
        btnOrderProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 AsyncTask asyncTask = new AsyncTask() {
                                        @SuppressLint("WrongThread")
                                        @Override
                    protected Object doInBackground(Object[] objects) {
                        transactionDetails = new TransactionDetails(buyerWalletAddress,
                        buyerWalletPrivateKey,sellerWalletAddress,selectedProduct.getName(),weightTotal,transactionTotal);
                        EthereumTransactionMaker ethereumTransactionMaker = new EthereumTransactionMaker(transactionDetails);
                        if(ethereumTransactionMaker.successfulTransaction()){
                            transactionHash = ethereumTransactionMaker.getTransactionHash();
                            timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o){
                        OrderDatabaseHelper orderDBHelper = new OrderDatabaseHelper(OrderConfirmationActivity.this);
                        if(orderDBHelper.addOrderToDatabase(transactionDetails,timestamp,transactionHash,buyer,seller)){
                            Toast.makeText(OrderConfirmationActivity.this, "Successfully ordered product", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(OrderConfirmationActivity.this, "Error ordering product", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
    }

    //nav bar buttons
    //home nav bar button
    public void navBarHome(MenuItem menuItem){
        Intent goToHomeScreen = new Intent(OrderConfirmationActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(OrderConfirmationActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToViewOrdersScreen = new Intent(OrderConfirmationActivity.this,OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(OrderConfirmationActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(OrderConfirmationActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(OrderConfirmationActivity.this,SalesHistoryActivity.class);
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

        Intent goToLoginScreen = new Intent(OrderConfirmationActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }
}
