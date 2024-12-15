package com.example.carsapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class addCarActivity extends AppCompatActivity {
    int SELECT_PHOTO = 1;
    Uri uri;
    ImageView imageView;
    private final String userId = String.valueOf(SharedPrefManager.getInstance(this).getUserID());
    private String imageString;
    private EditText CarName, CarPrice, CarEngine;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        Button Choose = findViewById(R.id.PickImage);
        imageView = findViewById(R.id.PickedImage);
        CarName = findViewById(R.id.CarName);
        CarPrice = findViewById(R.id.CarPrice);
        CarEngine = findViewById(R.id.CarEngine);
        progressDialog = new ProgressDialog(this);
        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);

                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] imageBytes = baos.toByteArray();
                long imgSize = imageBytes.length;
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void addCar() {
        final String name = CarName.getText().toString();
        final String price = CarPrice.getText().toString();
        final String engine = CarEngine.getText().toString();
        final String info = name + ";" + price + ";" + engine + ";";

        Log.d("AddCar", "Начало функции addCar");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Log.d("AddCar", "Собрали данные: " + info);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_ADD_CAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("AddCar", "Ответ от сервера: " + response);
                            progressDialog.setMessage(response);

                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("AddCar", "JSON распарсен успешно");
                            if (jsonObject.getBoolean("success")) {
                                Log.d("AddCar", "Успешный запрос. Сообщение: " + jsonObject.getString("message"));
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(addCarActivity.this, MenuActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w("AddCar", "Сервер вернул ошибку: " + jsonObject.getString("message"));
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            Log.e("AddCar", "Ошибка парсинга JSON", e);
                            progressDialog.dismiss();
                            Toast.makeText(addCarActivity.this, "Ошибка обработки ответа от сервера", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                error -> {
                    Log.e("AddCar", "Ошибка сети или сервера!", error);
                    progressDialog.dismiss();
                    Toast.makeText(addCarActivity.this, "Ошибка сети! " + error.getMessage(), Toast.LENGTH_LONG).show();
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d("AddCar", "Добавление параметров в запрос...");
                Map<String, String> params = new HashMap<>();
                if (userId != null) {
                    params.put("owner_id", userId);
                    Log.d("AddCar", "Добавлен параметр owner_id: " + userId);
                }
                if (imageString != null) {
                    params.put("image", imageString);
                    Log.d("AddCar", "Добавлен параметр image");
                }
                if (info != null) {
                    params.put("info", info);
                    Log.d("AddCar", "Добавлен параметр info: " + info);
                }
                return params;
            }
        };

        Log.d("AddCar", "Запрос добавлен в очередь");
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void onAddCarClick (View view){
        addCar();
    }
}
