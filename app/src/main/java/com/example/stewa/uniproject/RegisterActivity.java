package com.example.stewa.uniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText newName = (EditText) findViewById(R.id.et_new_name);
        final EditText walletAddress = (EditText) findViewById(R.id.et_wallet_address);
        final EditText walletPrivateKey = (EditText) findViewById(R.id.et_wallet_private_key);
        final EditText newPassword = (EditText) findViewById(R.id.et_new_password);
        final EditText confirmedPassword = (EditText) findViewById(R.id.et_confirm_password);
        Button confirmRegistrationButton = (Button) findViewById(R.id.btn_register_create);

        confirmRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String newUsername = newName.getText().toString();
                String walletAddressString = walletAddress.getText().toString();
                String walletPrivateKeyString = walletAddress.getText().toString();
                String newPasswordString = newPassword.getText().toString();
                String confirmedPasswordString = confirmedPassword.getText().toString();

                //checks is any text fields are empty
                if (isFieldEmpty(newName.getText())||isFieldEmpty(walletAddress.getText())||isFieldEmpty(newPassword.getText())
                ||isFieldEmpty(walletPrivateKey.getText())){
                    TextView emptyFieldsError = findViewById(R.id.tv_empty_register_fields_error);
                    emptyFieldsError.setVisibility(View.VISIBLE);
                }else{

                    if (passwordsMatch(newPasswordString,confirmedPasswordString)){
                        editor.putString(newUsername+newPasswordString,newUsername+"\n"+walletAddressString+"\n"+walletPrivateKeyString);
                        editor.commit();

                        Intent goToLogin = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(goToLogin);
                        finish();
                    }else{
                       TextView passwordMatchError = findViewById(R.id.tv_passwords_match_error);
                       passwordMatchError.setVisibility(View.VISIBLE);
                    }
                 }
            }
        });
    }
        //checks if the new password matches the confirmed password
        private boolean passwordsMatch(String newPassword,String confirmedPassword){
        boolean passwordMatches = false;

        if(newPassword.equals(confirmedPassword)){
            passwordMatches=true;
        }
        return passwordMatches;
    }

        private boolean isFieldEmpty(Editable textField){
        if(TextUtils.isEmpty(textField)){
            return true;
        }else return false;
}
}

