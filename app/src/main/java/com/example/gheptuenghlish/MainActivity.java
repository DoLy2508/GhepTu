package com.example.gheptuenghlish;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Database.SQLiteConnect;
import com.example.Model.TuVungGhepTu;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTimer;
    private GridLayout gridLayout;
    private HashMap<String, String> pairs;
    private List<String> allWords;
    private List<String> listEN;
    private List<String> listVI;
    private int totalPairsOnScreen;
    List<TuVungGhepTu> listPairs = new ArrayList<>();
    private ImageButton imbtnQuanLiTu;
    SQLiteConnect sqLiteConnect;


    private MaterialCardView firstCard = null;
    private String firstValue = "";
    private int timeElapsed = 0;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int matchedPairs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteConnect = new SQLiteConnect(MainActivity.this, getString(R.string.db_name),
                null,
                1
        );
        imbtnQuanLiTu = findViewById(R.id.imbtnQuanLiTu);

        gridLayout = findViewById(R.id.gridLayout);
        tvTimer = findViewById(R.id.tvTimer);
        imbtnQuanLiTu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGame = new Intent(MainActivity.this, QuanLiTuActivity.class);
                startActivity(intentGame);
            }
        });
        setupDuLieu();
        setupKhung();

        batDauThoiGian();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // dừng timer khi thoát
    }


    @Override
    protected void onResume() {
        super.onResume();
        batDauThoiGian(); // chạy lại timer

    }



    private void setupDuLieu() {
        pairs = new HashMap<>();
        listEN = new ArrayList<>();
        listVI = new ArrayList<>();
        allWords = new ArrayList<>();
        matchedPairs = 0;
        firstCard = null;
        firstValue = "";

        listPairs.clear();


        Cursor cursor = sqLiteConnect.getData("SELECT * FROM tuvungGT"); // load tất cả bản ghi
        while (cursor.moveToNext() && listPairs.size() < 6) {
            int key = cursor.getInt(0);
            String maTu = cursor.getString(1);
            String en = cursor.getString(2);
            String vi = cursor.getString(3);

            listPairs.add(new TuVungGhepTu(key, maTu, en, vi));
            pairs.put(en, vi);
        }
        cursor.close();

        totalPairsOnScreen = listPairs.size();
        Collections.shuffle(listPairs);


        for (TuVungGhepTu item : listPairs) {
            if (Math.random() < 0.5) {
                allWords.add(item.getTiengAnh());
                allWords.add(item.getTiengViet());
            } else {
                allWords.add(item.getTiengViet());
                allWords.add(item.getTiengAnh());
            }
        }
    }








    private void setupKhung() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3); // 3 cột

        for (String word : allWords) {
            MaterialCardView card = (MaterialCardView)
                    getLayoutInflater().inflate(R.layout.item_card, gridLayout, false);

            TextView txtWord = card.findViewById(R.id.txtWord);
            txtWord.setText(word);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            card.setLayoutParams(params);

            // Click listener
            card.setOnClickListener(v -> handleCardClick(card, word));

            gridLayout.addView(card);
        }
    }



    private void handleCardClick(MaterialCardView card, String word) {
        if (firstCard == null) {
            firstCard = card;
            firstValue = word;
            setVienThe(card, Color.parseColor("#FF9800"));
            return;
        }

        if (card == firstCard) {
            clearVienThe(card);
            firstCard = null;
            return;
        }

        setVienThe(card, Color.parseColor("#FF9800"));

        boolean ok = checkMatch(firstValue, word);

        if (ok) {
            setVienThe(firstCard, Color.GREEN);
            setVienThe(card, Color.GREEN);

            matchedPairs++;
            if (matchedPairs == totalPairsOnScreen) {
                startActivity(new Intent(MainActivity.this, VictoryActivity.class));
                finish();
            }

            handler.postDelayed(() -> {
                firstCard.setVisibility(View.INVISIBLE);
                card.setVisibility(View.INVISIBLE);
                firstCard = null;
            }, 350);

        } else {
            setVienThe(firstCard, Color.RED);
            setVienThe(card, Color.RED);

            handler.postDelayed(() -> {
                clearVienThe(firstCard);
                clearVienThe(card);
                firstCard = null;
            }, 600);
        }
    }


    private boolean checkMatch(String w1, String w2) {
        return (pairs.containsKey(w1) && pairs.get(w1).equals(w2))
                || (pairs.containsKey(w2) && pairs.get(w2).equals(w1));
    }

    private void setVienThe(MaterialCardView card, int color) {
        card.setCardBackgroundColor(Color.WHITE);
        card.setCardElevation(8f);
        card.setStrokeColor(color);
        card.setStrokeWidth(6);
    }

    private void clearVienThe(MaterialCardView card) {
        card.setStrokeColor(Color.TRANSPARENT);
        card.setStrokeWidth(0);
    }
    private void batDauThoiGian() {
        timeElapsed = 0; // bắt đầu từ 0
        int totalTime = 300;
        runnable = new Runnable() {
            @Override
            public void run() {
                // Định dạng thời gian kiểu mm:ss
                int minutes = timeElapsed / 60;
                int seconds = timeElapsed % 60;
                // Hiển thị dạng mm:ss (vd: 00:05, 01:30, 04:59)
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

                timeElapsed++;

                if (timeElapsed < totalTime) {
                    handler.postDelayed(this, 1000);
                } else {
                    tvTimer.setText("05:00");
                    Toast.makeText(MainActivity.this, "Hết giờ!", Toast.LENGTH_LONG).show();

                    // Chuyển sang VictoryActivity
                    startActivity(new Intent(MainActivity.this, VictoryActivity.class));
                    finish();
                }

            }
        };
        handler.post(runnable); // bắt đầu
    }
}
