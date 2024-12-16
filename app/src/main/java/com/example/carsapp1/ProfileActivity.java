package com.example.carsapp1;

import static com.example.carsapp1.Constants.URL_GET_PHONE;
import static com.example.carsapp1.Constants.URL_SET_PHONE;
import static com.example.carsapp1.Constants.URL_UPDATE_PASS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText phoneNumberInput;
    private EditText editPassOld;
    private EditText editPassNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profile_menu);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        // Привязываем виджет EditText по новому ID
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        editPassOld = findViewById(R.id.editPassOld);
        editPassNew = findViewById(R.id.editPassNew);
        fetchPhoneNumber();
//        addIconButtonToToolbar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if (id == R.id.Logout){
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
    private void fetchPhoneNumber() {
        // Создаем очередь запросов
        RequestQueue queue = Volley.newRequestQueue(this);

        // Создаем запрос JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GET_PHONE + "?id=" + SharedPrefManager.getInstance(this).getUserID(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Получаем телефон из JSON-ответа
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String phone = response.getString("phone");
                                // Устанавливаем телефон в EditText
                                phoneNumberInput.setText(phone);
                            } else {
                                Toast.makeText(getApplicationContext(), "Не удалось получить номер телефона", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Добавляем запрос в очередь
        queue.add(jsonObjectRequest);
    }
    private void changePhoneNumber(){
        // Создаем очередь запросов
        RequestQueue queue = Volley.newRequestQueue(this);
        // Создаем запрос JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_SET_PHONE + "?id=" + SharedPrefManager.getInstance(this).getUserID() +"&phone=" + phoneNumberInput.getText(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Получаем телефон из JSON-ответа
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String message = response.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Не удалось установить номер телефона", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Добавляем запрос в очередь
        queue.add(jsonObjectRequest);
    }

    private void changePassword(String oldPassword, String newPassword) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_UPDATE_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            if (success) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                editPassNew.setText("");
                                editPassOld.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Отправляем данные в запросе POST
                int id = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id)); // Здесь должен быть id пользователя (например, можно передать через Intent)
                params.put("pass_old", oldPassword);
                params.put("pass_new", newPassword);
                return params;
            }
        };

        // Добавляем запрос в очередь
        queue.add(stringRequest);
    }
    public void onPhoneChangeClick (View view){
        changePhoneNumber();
    }
    public void onPasswordChangeClick (View view){
        changePassword(editPassOld.getText().toString(), editPassNew.getText().toString());
    }
}
