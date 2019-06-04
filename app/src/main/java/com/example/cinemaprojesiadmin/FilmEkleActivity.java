package com.example.cinemaprojesiadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FilmEkleActivity extends AppCompatActivity {
    Button resimSecButton, filmEkleButon;
    Bitmap bitmap;
    ImageView filmEkleImageView;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    EditText filmİsimEditText, filmYönetmenEditText, filmOyuncularEditText, filmAciklamaEditText,
            filmTurEditText, filmYilEditText;
    String imageUrl = "";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_film_ekle);
        tanimla();
    }


    public void tanimla() {
        resimSecButton = findViewById(R.id.resimSecButon);
        filmEkleImageView = findViewById(R.id.filmEkleImageView);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        filmİsimEditText = findViewById(R.id.filmİsimEditText);
        filmYönetmenEditText = findViewById(R.id.filmYönetmenEditText);
        filmOyuncularEditText = findViewById(R.id.filmOyuncularEditText);
        filmAciklamaEditText = findViewById(R.id.filmAciklamaEditText);
        filmTurEditText = findViewById(R.id.filmTurEditText);
        filmYilEditText = findViewById(R.id.filmYilEditText);

        filmEkleButon = findViewById(R.id.filmEkleButon);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        resimSecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galeriAc();
            }
        });

        filmEkleButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filmisim = "", yonetmen = "", oyuncular = "", aciklama = "", tur = "", yil = "";
                filmisim = filmİsimEditText.getText().toString();
                yonetmen = filmYönetmenEditText.getText().toString();
                oyuncular = filmOyuncularEditText.getText().toString();
                aciklama = filmAciklamaEditText.getText().toString();
                tur = filmTurEditText.getText().toString();
                yil = filmYilEditText.getText().toString();
                if (imageUrl.equals("")) {
                    Toast.makeText(getApplicationContext(), "Resim Seçin veya Yüklenmesini Bekleyin", Toast.LENGTH_LONG).show();
                } else if (filmisim.equals("") || yonetmen.equals("") || oyuncular.equals("") || aciklama.equals("") || tur.equals("") ||
                        yil.equals("")) {
                    Toast.makeText(getApplicationContext(), "Bilgileri Eksiksiz Giriniz", Toast.LENGTH_LONG).show();
                } else
                    filmEkle(filmisim, yonetmen, oyuncular, aciklama, tur, yil);

            }
        });

    }

    private void filmEkle(final String filmisim, String yonetmen, String oyuncular, String aciklama, String tur, String yil) {
        Map map = new HashMap();
        map.put("filmismi", filmisim);
        map.put("yonetmen", yonetmen);
        map.put("oyuncular", oyuncular);
        map.put("aciklama", aciklama);
        map.put("tur", tur);
        map.put("yil", yil);
        map.put("resim", imageUrl);

        reference.child("Filmler").child(filmisim).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Edittextlerin  ve resmin içini boşalttık
                    filmİsimEditText.setText("");
                    filmYönetmenEditText.setText("");
                    filmOyuncularEditText.setText("");
                    filmAciklamaEditText.setText("");
                    filmTurEditText.setText("");
                    filmYilEditText.setText("");
                    bitmap = null;//storage kısmındaki verilere ulaşmak için bu fonksiyonu kullanırız
                    filmEkleImageView.setImageBitmap(bitmap);
                    koltukEkle(filmisim);
                }
            }
        });


    }



    //galeri açmak için intent kullanıyorduk
    public void galeriAc() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 777);

    }
    //seçtiğimiz resmin datasını almak için bu fonksiyon gereklidir


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 777 && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            //bitmap üzerinden imageview i gösterelim
            final StorageReference filePath = storageReference.child("Resimler").child(ImageName.getImageName() + ".jpg");
            filePath.putFile(path).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        imageUrl = task.getResult().toString();
                        // Uri downUri = task.getResult();
                        //  Log.i("imagegiden", "onComplete: Url: "+ downUri.toString());
                    }
                }
            });

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);//storage kısmındaki verilere ulaşmak için bu fonksiyonu kullanırız
                filmEkleImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    public void koltukEkle(String ad) {
        //for döngüsü kullanmalıyız 50 koltuk var. 2.referans nesnemizi oluşturduk
        DatabaseReference reference2 = reference.child("Koltuklar").child(ad);
        for (int i = 1; i <= 50; i++) {
            Map map=new HashMap();
            map.put("biletalan","");

            reference2.child(String.valueOf(i)).setValue(map);
        }
    }

}
