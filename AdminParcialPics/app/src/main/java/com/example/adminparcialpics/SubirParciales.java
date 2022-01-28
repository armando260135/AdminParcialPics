package com.example.adminparcialpics;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubirParciales extends AppCompatActivity {

    private static final int File = 1 ;
    DatabaseReference myRef;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.uploadImageView)
    ImageView mUploadImageView;
    EditText editTextTextImgFolder,editTextTextImgSubFolder;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_parciales);

        ButterKnife.bind(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference("user1");
        editTextTextImgFolder = findViewById(R.id.editTextTextImgFolder);
        editTextTextImgSubFolder = findViewById(R.id.editTextTextImgSubFolder);
        progressDialog = new ProgressDialog(this);
        mUploadImageView.setOnClickListener(v -> fileUpload());
    }

    public void fileUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,File);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String carpeta = editTextTextImgFolder.getText().toString();
        String subcarpeta = editTextTextImgSubFolder.getText().toString();
        
        if(requestCode == File){
            if(resultCode == RESULT_OK){
                progressDialog.setTitle("Subiendo Imagen");
                progressDialog.setMessage("Por Favor Espere un Momento");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Uri FileUri = data.getData();

                StorageReference Folder = FirebaseStorage.getInstance().getReference().child(carpeta);

                StorageReference Folder2 = Folder.child(subcarpeta);

                final StorageReference file_name = Folder2.child("file"+FileUri.getLastPathSegment());


                file_name.putFile(FileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("link", String.valueOf(uri));
                    //mostrar la imagen que se acaba de subir
//                    Glide.with(SubirParciales.this)
//                            .load(uri)
//                            .centerCrop()
//                            .into(imageView);
                    myRef.setValue(hashMap);
                    progressDialog.dismiss();

                    Log.d("Mensaje", "Se subió correctamente");

                }));

            }

        }

    }
}