package com.example.carsapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.carsapp1.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail);
        Toolbar toolbar = findViewById(R.id.main_menu);
        setSupportActionBar(toolbar);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        // Получаем данные из Intent
        Intent intent = getIntent();
        String carInfo = intent.getStringExtra("car_info");
        String carDetails = intent.getStringExtra("car_details");
        int carId = intent.getIntExtra("car_id", 0);
        String phone = intent.getStringExtra("phone_number");
        loadImageForCar(carId, findViewById(R.id.card_detail_image));
        // Привязываем данные к элементам интерфейса
        TextView carInfoTextView = findViewById(R.id.card_detail_title);
        TextView carDetailsTextView = findViewById(R.id.card_detail_description);

        carInfoTextView.setText(carInfo != null ? carInfo : "Информация отсутствует");
        carDetailsTextView.setText(carDetails != null ? carDetails : "Детали отсутствуют");

        Button callButton = findViewById(R.id.call_btn);

        // Устанавливаем обработчик нажатия
        callButton.setOnClickListener(v -> {
            if (phone != null && !phone.isEmpty()) {
                // Создаем Intent для набора номера
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } else {
                // Обработка случая, если номер не передан
                Toast.makeText(this, "Номер телефона отсутствует!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageForCar(int carId, ImageView imageView) {
        // URL для получения JSON с изображением
        String url = Constants.URL_GET_CAR_IMAGE_BY_ID + "?id=" + carId;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Устанавливаем соединение
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Читаем ответ
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Парсим JSON-ответ
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    String base64Image = jsonResponse.getString("image");

                    // Используем метод StringToBitmap из ImageLoader
                    Bitmap bitmap = ImageLoader.StringToBitmap(base64Image);

                    // Устанавливаем картинку в ImageView на главном потоке
                    handler.post(() -> imageView.setImageBitmap(bitmap));
                } else {
                    // Обработка ошибки на случай, если success = false
                    handler.post(() -> {
                        Toast.makeText(DetailActivity.this, "Ошибка: изображение не найдено", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Обработка исключений
                handler.post(() -> {
                    Toast.makeText(DetailActivity.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
