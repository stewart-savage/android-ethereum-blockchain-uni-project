package com.example.stewa.uniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProductEditActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        //opens up navigation menu by clicking on white menu icon
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

        Bundle bundle = getIntent().getExtras();
        String productDetails = bundle.getString("productDetails");
        String[] productDetailsArray = productDetails.split("\n");
        final String productID = productDetailsArray[0];
        String productName = productDetailsArray[1];
        String productDescription = productDetailsArray[2];
        double productWeight = Double.parseDouble(productDetailsArray[3]);
        double productCost = Double.parseDouble(productDetailsArray[4]);
        int stock = Integer.parseInt(productDetailsArray[5]);

        final TextView emptyFieldsError = findViewById(R.id.tv_product_edit_empty_fields_error);
        final EditText etProductName = (EditText) findViewById(R.id.et_product_edit_name);
        final EditText etProductDescription = (EditText) findViewById(R.id.et_product_edit_description);
        final EditText etProductWeight = (EditText) findViewById(R.id.et_product_edit_weight);
        final EditText etProductItemCost = (EditText) findViewById(R.id.et_product_edit_item_cost);
        final EditText etProductStock = (EditText) findViewById(R.id.et_product_edit_stock);

        etProductName.setText(productName);
        etProductDescription.setText(productDescription);
        etProductWeight.setText(Double.toString(productWeight));
        etProductItemCost.setText(Double.toString(productCost));
        etProductStock.setText(Integer.toString(stock));

        final Button updateProductButton = (Button) findViewById(R.id.btn_product_update);
        final Button deleteProductButton = (Button) findViewById(R.id.btn_product_delete);

        final DatabaseHelper dbHelper = new DatabaseHelper(ProductEditActivity.this);

        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checks is any text fields are empty
                if (isFieldEmpty(etProductName.getText()) ||
                        isFieldEmpty(etProductDescription.getText()) ||
                        isFieldEmpty(etProductItemCost.getText()) ||
                        isFieldEmpty(etProductWeight.getText()) ||
                        isFieldEmpty(etProductStock.getText())) {

                    emptyFieldsError.setVisibility(View.VISIBLE);
                } else {
                    //updates product with new details
                    emptyFieldsError.setVisibility(View.INVISIBLE);
                    Product updatedProduct = new Product(etProductName.getText().toString(),
                            etProductDescription.getText().toString(),
                            Double.parseDouble(etProductWeight.getText().toString()),
                            Double.parseDouble(etProductItemCost.getText().toString()),
                            Integer.parseInt(etProductStock.getText().toString()),
                             null);

                    if (dbHelper.updateProductInDatabase(updatedProduct, productID)) {
                        Toast.makeText(ProductEditActivity.this, "Successfully updated product", Toast.LENGTH_SHORT).show();
                        Intent goToHomeScreen = new Intent(ProductEditActivity.this, HomeActivity.class);
                        startActivity(goToHomeScreen);
                        finish();
                    }

                }
            }
        });

        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.deleteProductFromDatabase(productID)) {
                    Toast.makeText(ProductEditActivity.this, "Successfully deleted product", Toast.LENGTH_SHORT).show();
                    Intent goToHomeScreen = new Intent(ProductEditActivity.this, HomeActivity.class);
                    startActivity(goToHomeScreen);
                    finish();
                }
            }
        });
    }

    //nav bar buttons
    //home nav bar button
    public void navBarHome(MenuItem menuItem){
        Intent goToHomeScreen = new Intent(ProductEditActivity.this,HomeActivity.class);
        startActivity(goToHomeScreen);
        finish();
    }

    //order produce nav bar button
    public void navBarOrderProduce(MenuItem menuItem){
        Intent goToOrderProduceScreen = new Intent(ProductEditActivity.this,OrderProductActivity.class);
        startActivity(goToOrderProduceScreen);
        finish();
    }

    //view orders nav bar button
    public void navBarViewOrders(MenuItem menuItem){
        Intent goToViewOrdersScreen = new Intent(ProductEditActivity.this,OrderHistoryActivity.class);
        startActivity(goToViewOrdersScreen);
        finish();
    }

    //add product nav bar button
    public void navBarAddProduct(MenuItem menuItem){
        Intent goToAddProductScreen = new Intent(ProductEditActivity.this, ProductAddActivity.class);
        startActivity(goToAddProductScreen);
    }

    //edit or remove product nav bar button
    public void navBarEditRemoveProduct(MenuItem menuItem){
        Intent goToEditRemoveProductScreen = new Intent(ProductEditActivity.this,EditOrRemoveProductActivity.class);
        startActivity(goToEditRemoveProductScreen);
        finish();
    }

    //view sales nav bar button
    public void navBarViewSales(MenuItem menuItem){
        Intent goToViewSalesScreen = new Intent(ProductEditActivity.this,SalesHistoryActivity.class);
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

        Intent goToLoginScreen = new Intent(ProductEditActivity.this, LoginActivity.class);
        startActivity(goToLoginScreen);
        finish();
    }

    private boolean isFieldEmpty(Editable textField) {
        if (TextUtils.isEmpty(textField)) {
            return true;
        } else return false;
    }
}
