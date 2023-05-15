package com.example.stewa.uniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText loginName = (EditText) findViewById(R.id.etLoginName);
        final EditText loginPassword = (EditText) findViewById(R.id.etLoginPassword);
        Button loginButton = (Button) findViewById(R.id.btnLoginButton);
        Button registerButton = (Button) findViewById(R.id.btnLoginRegister);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                String usernameString = loginName.getText().toString();
                String passwordString = loginPassword.getText().toString();
                TextView emptyLoginFieldsError = findViewById(R.id.tv_empty_login_fields_error);

                //checks if both login fields are empty
                if (usernameString.equals("") || passwordString.equals("")) {
                    emptyLoginFieldsError.setVisibility(View.VISIBLE);
                } else {
                    //gets the data value for the key of entered username+password, if there is no
                    //data value for this key, it is set to "Invalid Login"
                    emptyLoginFieldsError.setVisibility(View.INVISIBLE);
                    String userDetails = sharedPreferences.getString(usernameString + passwordString, "Invalid Login");
                    //if Invalid Login is the value stored for this key, it presents error text
                    if(userDetails.equals("Invalid Login")){
                        TextView invalidLoginError = findViewById(R.id.tv_invalid_login_error);
                        invalidLoginError.setVisibility(View.VISIBLE);
                    }else{
                    //if the user has non-default values for their username/password key combination,
                    //therefore they have existing data values and therefore are taken to the home page and the
                    //credentials are set to this.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("credentials", userDetails);
                    editor.putString(usernameString+"walletAddress",getWalletAddressFromUserDetails(userDetails));
                    editor.commit();

                    Intent goToHomeScreen = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(goToHomeScreen);
                    finish();
                    }
                }
            }});

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegistration = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(goToRegistration);
            }
        });
    }

    private String getWalletAddressFromUserDetails(String userDetails){
        String[] userDetailsStringArray = userDetails.split("\n");
        String walletAddress = userDetailsStringArray[1];
        return walletAddress;
    }
}
