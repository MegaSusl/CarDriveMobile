package com.example.carsapp1;

import static com.example.carsapp1.Constants.URL_GET_CARS_IMAGES;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private final ArrayList<ImageView> imgs;
    // Создаем ExecutorService для потоков
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public ImageLoader(ArrayList<ImageView> imgs) {
        this.imgs = imgs;
    }

    public void loadImages() {
        executorService.execute(() -> {
            try {
                Log.d("ImageLoader", "Загрузка JSON началась");

                // Загружаем JSON-ответ
                String jsonResponse = downloadJson(URL_GET_CARS_IMAGES);
                Log.d("ImageLoader", "JSON получен: " + jsonResponse);

                // Парсим JSON-объект
                JSONObject jsonObject = new JSONObject(jsonResponse);

                // Проверяем поле "success"
                boolean success = jsonObject.getBoolean("success");
                if (!success) {
                    Log.e("ImageLoader", "Ошибка: success = false");
                    return;
                }

                // Получаем массив из поля "list"
                JSONArray jsonArray = jsonObject.getJSONArray("list");

                // Обрабатываем массив
                for (int i = 0; i < jsonArray.length() && i < imgs.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String imageString = obj.getString("image");

                    Log.d("ImageLoader", "Извлечён image: " + imageString.substring(0, Math.min(20, imageString.length())));

                    // Преобразуем строку в Bitmap
                    Bitmap bitmap = StringToBitmap(imageString);
                    if (bitmap != null) {
                        Log.d("ImageLoader", "Bitmap создан успешно");
                    } else {
                        Log.e("ImageLoader", "Bitmap == null");
                        continue;
                    }

                    // Устанавливаем изображение в ImageView на UI-потоке
                    int index = i;
                    uiHandler.post(() -> {
                        imgs.get(index).setImageBitmap(bitmap);
                        Log.d("ImageLoader", "Изображение установлено для ImageView с индексом " + index);
                    });
                }
            } catch (Exception e) {
                Log.e("ImageLoader", "Ошибка: ", e);
            }
        });

    }

    private String downloadJson(String urlString) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try (InputStream inputStream = connection.getInputStream()) {
            int data;
            while ((data = inputStream.read()) != -1) {
                result.append((char) data);
            }
        }
        return result.toString();
    }

    // Твой метод конвертации строки в Bitmap
    static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
