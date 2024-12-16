package com.example.carsapp1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.carsapp1.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class MenuActivity extends AppCompatActivity {
    private static ArrayList<ImageView> imgs = new ArrayList<ImageView>();
    ActivityMainBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imgs = new ArrayList<ImageView>();
        ArrayList<ImageView> imgs = new ArrayList<ImageView>();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.main_menu);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        else{
            getCars();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if (id == R.id.Profile){
//            Toast.makeText(this, "aaaaaaaaa", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProfileActivity.class);
            this.startActivity(intent);
        }
        if (id == R.id.addCarBtn){
            Intent intent = new Intent(MenuActivity.this, addCarActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
    private void getCars(){
        ConstraintLayout carsContainer = findViewById(R.id.carsContainer);
        Context ctx = getApplicationContext();
        ArrayList<String> imagesBln = new ArrayList<String>();
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_GET_CARS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray carsArr = jsonObject.getJSONArray("list");
                            if(jsonObject.getBoolean("success")){
                                for (int i = 0; i < carsArr.length(); i++) {
                                    JSONObject car = carsArr.getJSONObject(i);
                                    int id = car.getInt("id");
                                    String phone = car.getString("phone");
                                    String info = car.getString("info");
                                    String[] infoAr = info.split(";");
                                    imgs.add(addCarCard(carsContainer, i, infoAr, id, phone));
                                }
                                if (carsArr.length() > 0){
                                    addBottomSpacer(carsContainer);
                                }
                                progressDialog.dismiss();
                                Log.d("MenuActivity", "Размер массива imgs: " + imgs.size());
                                for (int i = 0; i < imgs.size(); i++) {
                                    Log.d("MenuActivity", "Картинка [" + i + "]: " + imgs.get(i).toString());
                                }
                                ImageLoader loader = new ImageLoader(imgs);
                                loader.loadImages();
                            }
                            else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                                progressDialog.dismiss();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    Toast.makeText(MenuActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private ImageView addCarCard(ConstraintLayout container, int index, String[] info, int car_id, String phone) {
        // Создаем CardView
        CardView cardView = new CardView(this);
        cardView.setId(View.generateViewId());
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.on_primary));
        cardView.setRadius(16f);

        // Фиксируем высоту CardView
        ConstraintLayout.LayoutParams cardLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                830 // Высота карточки
        );
        cardLayoutParams.setMargins(8, 16, 8, 0);
        cardView.setLayoutParams(cardLayoutParams);

        // Внутренний ConstraintLayout
        ConstraintLayout innerLayout = new ConstraintLayout(this);
        innerLayout.setId(View.generateViewId());
        ConstraintLayout.LayoutParams innerLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        innerLayout.setLayoutParams(innerLayoutParams);

        // Добавляем innerLayout в CardView
        cardView.addView(innerLayout);

        // Вложенный CardView
        CardView innerCard = new CardView(this);
        innerCard.setId(View.generateViewId());
        innerCard.setCardBackgroundColor(Color.parseColor("#AB6868"));
        innerCard.setRadius(16f);

        ConstraintLayout.LayoutParams innerCardParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                560
        );
        innerCardParams.setMargins(8, 8, 8, 8);
        innerCard.setLayoutParams(innerCardParams);

        // Добавляем innerCard в innerLayout
        innerLayout.addView(innerCard);

        // ImageView внутри innerCard
        ImageView imageView = new ImageView(this);
        imageView.setId(View.generateViewId());
        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        ));
//        Bitmap img = StringToBitMap(image);
//        imageView.setImageBitmap(img); // Замените на свой ресурс
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        innerCard.addView(imageView);

        // Заголовок (TextView)
        TextView title = new TextView(this);
        title.setId(View.generateViewId());
        title.setText(info[0]);
        title.setTextSize(34f);
        title.setTextColor(Color.BLACK);

        ConstraintLayout.LayoutParams titleParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(24, 8, 24, 0);
        title.setLayoutParams(titleParams);
        innerLayout.addView(title);

        // Подзаголовок (TextView)
        TextView subhead = new TextView(this);
        subhead.setId(View.generateViewId());
        subhead.setText(info[1]);
        subhead.setTextSize(24f);
        subhead.setTextColor(Color.GRAY);

        ConstraintLayout.LayoutParams subheadParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        subheadParams.setMargins(24, 4, 24, 24);
        subhead.setLayoutParams(subheadParams);
        innerLayout.addView(subhead);

        // Кнопка
        Button button = new Button(this);
        button.setId(View.generateViewId());
        button.setText("❯");
        button.setTextColor(Color.WHITE);
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.roundedbutton)); // Ваш ресурс

        ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams(180, 180);
        buttonParams.setMargins(24, 24, 24, 24);
        button.setLayoutParams(buttonParams);
        innerLayout.addView(button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("car_info", info[0]); // Передаем информацию о машине
            intent.putExtra("car_details", info[1]);
            intent.putExtra("car_id", car_id);
            intent.putExtra("phone_number", phone);
            this.startActivity(intent);
        });

        // Устанавливаем констрейны для innerLayout
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(innerLayout);

        // Констрейны для innerCard
        constraintSet.connect(innerCard.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(innerCard.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(innerCard.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

        // Констрейны для title
        constraintSet.connect(title.getId(), ConstraintSet.TOP, innerCard.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(title.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

        // Констрейны для subhead
        constraintSet.connect(subhead.getId(), ConstraintSet.TOP, title.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(subhead.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

        // Констрейны для button
//        constraintSet.connect(button.getId(), ConstraintSet.TOP, innerCard.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(button.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(button.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        constraintSet.applyTo(innerLayout);

        // Добавляем cardView в контейнер
        container.addView(cardView);

        // Устанавливаем констрейны для cardView
        ConstraintSet containerConstraints = new ConstraintSet();
        containerConstraints.clone(container);

        if (index == 0) {
            containerConstraints.connect(cardView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        } else {
            View previousCard = container.getChildAt(container.getChildCount() - 2); // Предыдущий CardView
            containerConstraints.connect(cardView.getId(), ConstraintSet.TOP, previousCard.getId(), ConstraintSet.BOTTOM, 32);
        }

        containerConstraints.connect(cardView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        containerConstraints.connect(cardView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

        containerConstraints.applyTo(container);

        return imageView;
    }
    private void addBottomSpacer(ConstraintLayout container) {
        // Создаем фиктивный элемент
        View spacer = new View(this);
        spacer.setId(View.generateViewId());

        // Настраиваем параметры для фиктивного элемента
        ConstraintLayout.LayoutParams spacerParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                900 // Высота фиктивного отступа
        );
        spacer.setLayoutParams(spacerParams);

        // Добавляем в контейнер
        container.addView(spacer);

        // Устанавливаем констрейнты
        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        // Привязываем верхнюю границу фиктивного элемента к последней карточке
        View lastCard = container.getChildAt(container.getChildCount() - 2); // Последняя карточка
        set.connect(spacer.getId(), ConstraintSet.TOP, lastCard.getId(), ConstraintSet.BOTTOM, 0);

        // Привязываем нижнюю границу фиктивного элемента к нижнему краю контейнера
        set.connect(spacer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        set.applyTo(container);
    }

}
