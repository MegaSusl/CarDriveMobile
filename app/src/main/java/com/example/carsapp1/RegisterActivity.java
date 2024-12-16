package com.example.carsapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText loginInputRegister,phoneInputRegistration, passInputRegister, passInputRegisterCheck;
    private Button submitRegistration;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        loginInputRegister = findViewById(R.id.loginInputRegister);
        phoneInputRegistration = findViewById(R.id.phoneInputRegistration);
        passInputRegister = findViewById(R.id.passInputRegister);
        passInputRegisterCheck = findViewById(R.id.passInputRegisterCheck);

        submitRegistration = findViewById(R.id.submitRegistration);

        progressDialog = new ProgressDialog(this);
    }
    private void registerAcc(){
        final String login = loginInputRegister.getText().toString().trim();
        final String phone = phoneInputRegistration.getText().toString().trim();
        final String password = passInputRegister.getText().toString().trim();
        final String passwordCheck = passInputRegisterCheck.getText().toString().trim();

        progressDialog.setMessage("Проверка данных");
        progressDialog.show();
        if (password.equals(passwordCheck)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("login", login);
                    params.put("phone", phone);
                    params.put("password", password);
                    params.put("passwordCheck", passwordCheck);
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            //Volley.newRequestQueue(this).add(stringRequest);
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, "Введёные пароли не совпадают", Toast.LENGTH_LONG).show();
        }
    }
    public void onRegBtnClick (View view){
        registerAcc();
//        finish();
//        startActivity(new Intent(this,MainActivity.class));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
