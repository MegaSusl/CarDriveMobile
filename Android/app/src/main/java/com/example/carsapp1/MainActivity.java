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

public class MainActivity extends AppCompatActivity {

    private EditText editTextLogin, editTextPassword;
    private Button button_login;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (getIntent().getBooleanExtra("EXIT", false)) {
//            finish();
//        }
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            if(SharedPrefManager.getInstance(this).getRole()==1){
                finish();
                startActivity(new Intent(this,AdminActivity.class));
                return;
            }
            else if(SharedPrefManager.getInstance(this).getRole()==2){
                finish();
                startActivity(new Intent(this,MenuActivity.class));
                return;
            }

        }

        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);

        button_login = findViewById(R.id.button_login);

        progressDialog = new ProgressDialog(this);

    }
    private void checklog(){
        final String login = editTextLogin.getText().toString().trim();
        final String pass = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Пожалуйста подождите...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("login"),
                                        jsonObject.getInt("role")
                                );
                                if(SharedPrefManager.getInstance(getApplicationContext()).getRole() == 2) {
                                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if(SharedPrefManager.getInstance(getApplicationContext()).getRole() == 1){
                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                                progressDialog.dismiss();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("login", login);
                params.put("pass", pass);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void onLoginClick (View view){
        checklog();
    }

    public void onRegClick (View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}