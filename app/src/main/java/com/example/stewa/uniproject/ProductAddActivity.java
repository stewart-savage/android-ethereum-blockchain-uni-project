package com.example.stewa.uniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProductAddActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        //opens up navigation menu by clicking on white menu icon

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ImageButton navBarMenuButton = (ImageButton) findViewById(R.id.btn_add_product_drawer_menu);
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
        final TextView emptyFieldsError = findViewById(R.id.tv_product_add_empty_fields_error);
        final EditText etProductName = (EditText) findViewById(R.id.et_product_name);
        final EditText etProductDescription = (EditText) findViewById(R.id.et_product_description);
        final EditText etProductItemCost = (EditText) findViewById(R.id.et_product_item_cost);
        final EditText etProductStock = (EditText) findViewById(R.id.et_product_stock);
        final EditText etProductWeight = (EditText) findViewById(R.id.et_product_weight);

        Button addProductButton = (Button) findViewById(R.id.btn_product_add);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks is any text fields are empty
                if (isFieldEmpty(etProductName.getText()) || isFieldEmpty(etProductDescription.getText()) || isFieldEmpty(etProductItemCost.getText())
                        || isFieldEmpty(etProductWeight.getText()) || isFieldEmpty(etProductStock.getText())) {

                    emptyFieldsError.setVisibility(View.VISIBLE);
                } else {
                    emptyFieldsError.setVisibility(View.INVISIBLE);
                    String productName = etProductName.getText().toString();
                    String productDescription = etProductDescription.getText().toString();
                    double productItemCost = Double.parseDouble(etProductItemCost.getText().toString());
                    int productStock = Integer.parseInt(etProductStock.getText().toString());
                    double productWeight = Double.parseDouble(etProductWeight.getText().toString());
                    String producer = getProducerNameFromSharedPrefs(currentUserCredentials);
                    Product newProduct = new Product(productName, productDescription, productWeight, productItemCost, productStock, producer);
                    DatabaseHelper dbhelper = new DatabaseHelper(ProductAddActivity.this);

                    if(dbhelper.addProductToDatabase(newProduct)){
                        emptyForm((ViewGroup) findViewById(R.id.constraintLayout));
                        Toast.makeText(ProductAddActivity.this, "Successfully added product", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    //nav bar buttons
    //home nav bar button
    public void navBarHome(MenuItem menuItem){
        Intent goToHomeScreen = new Intent(ProductAddActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(ProductAddActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToViewOrdersScreen = new Intent(ProductAddActivity.this,OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(ProductAddActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(ProductAddActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(ProductAddActivity.this,SalesHistoryActivity.class);
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

        Intent goToLoginScreen = new Intent(ProductAddActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }

    //looks through shared preferences to get username (producer name for users adding products)
    private String getProducerNameFromSharedPrefs(String currentUserDetails) {

        System.out.println(currentUserDetails);
        int indexValue = currentUserDetails.indexOf("\n");
        if (indexValue == -1) {
            System.out.println("oops, error getting producer name from shared preferences.");
            return "";
        } else {
            String producerName = currentUserDetails.substring(0, indexValue);
            System.out.println("producer name: "+producerName);
            return producerName;
        }
    }

    //clears all edittext fields in the form so a new product can be added
    private void emptyForm(ViewGroup group) {
        for (int i = 0, currentChildCount = group.getChildCount(); i < currentChildCount; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).getText().clear();
            }
        }
    }

    private boolean isFieldEmpty(Editable textField) {
        if (TextUtils.isEmpty(textField)) {
            return true;
        } else return false;

    }
}
