package com.example.adminparcialpics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private Button addMaterias;
    private CardView cardmateria,cardParcial;
    private EditText etCodigo,ingresaricono;
    private TextView cerrarsubirfoto,cerrarmateria;
    private String materia, codigo,codigoicono;
    private int iddrawable;
    private AlertDialog.Builder dialogBuilder,dialogBuilder2;
    private AlertDialog dialog,dialog2;
    private Spinner spinnermaterias,spinnersemestres,spinnertipoparcial;

    //subir img
    private static final int File = 1 ;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.uploadImageView)
    ImageView mUploadImageView;
    private ProgressDialog progressDialogParcial;
    public static String carpeta = "";
    public static String subcarpeta = "";
    public static String subsubcarpeta = "1 Parcial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardmateria = findViewById(R.id.cardMateria);
        cardParcial = findViewById(R.id.cardParcial);
        cardmateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearNuevaMateria();
            }
        });
        cardParcial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearNuevoParcial();
            }
        });
    }

    public void crearNuevaMateria(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.layout_bottom_anadir_materias,null);
//        contactPopupView.setBackground(getDrawable(R.drawable.bottom_sheet_background));
        contactPopupView.setPadding(5,0,0,40);
        ingresaricono = contactPopupView.findViewById(R.id.etxIngresarNombreIcono);
        etCodigo = contactPopupView.findViewById(R.id.etxIngresarCodMateria);
        addMaterias = contactPopupView.findViewById(R.id.buttonShare12);
        cerrarmateria = contactPopupView.findViewById(R.id.cerrarparcial);
        spinnermaterias = contactPopupView.findViewById(R.id.spinnerMateriasma);

        String []materias={
                "Base De Datos",
                "Inteligencia Artificial",
                "Calculo Integral",
                "Calculo Diferencial",
                "Analisis Numerico",
                "Algebra Lineal",
                "Circuitos Logicos",
                "Electronica General",
                "Emprendimiento",
                "Ecuaciones Diferenciales",
                "Ondas y Particulas"};

        ArrayAdapter<String> adapterMaterias = new ArrayAdapter<String>(this,R.layout.text_spinner_semestre, materias);
        spinnermaterias.setAdapter(adapterMaterias);

        spinnermaterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                materia = spinnermaterias.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                
            }
        });


        addMaterias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codigo = etCodigo.getText().toString();
                codigoicono=ingresaricono.getText().toString();
                iddrawable = getResources().getIdentifier(codigoicono, "drawable", getPackageName());

                if (TextUtils.isEmpty(codigoicono)){
                    ingresaricono.setError("Ingrese un icono");
                    ingresaricono.requestFocus();
                }else if (TextUtils.isEmpty(codigo)){
                    etCodigo.setError("Ingrese un codigo");
                    etCodigo.requestFocus();
                }else {
                    MateriasHome materiasHome = new MateriasHome();
                    materiasHome.setFoto(iddrawable);
                    materiasHome.setNombre(materia);
                    materiasHome.setCodigo(codigo);
                    //instancia de la bd en firebase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    //para el id el reference push y se envia a la bd en fire...
                    DatabaseReference reference = database.getReference("Asignaturas");
                    reference.push().setValue(materiasHome);

                    etCodigo.setText("");
                    ingresaricono.setText("");
                    Toast.makeText(MainActivity.this, "Materia Registrada", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cerrarmateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    public void crearNuevoParcial(){
        dialogBuilder2 = new AlertDialog.Builder(this);
        final View contactPopupView2 = getLayoutInflater().inflate(R.layout.layou_bottom_anadir_parcial,null);
        mUploadImageView = contactPopupView2.findViewById(R.id.uploadImageView);
        spinnersemestres = contactPopupView2.findViewById(R.id.spinnerSemestre);
        spinnertipoparcial = contactPopupView2.findViewById(R.id.spinnerTipoParcial);
        spinnermaterias = contactPopupView2.findViewById(R.id.spinnerMaterias);
        cerrarsubirfoto = contactPopupView2.findViewById(R.id.cerrar);

        String []materias={
                "Base De Datos",
                "Inteligencia Artificial",
                "Calculo Integral",
                "Calculo Diferencial",
                "Analisis Numerico",
                "Algebra Lineal",
                "Circuitos Logicos",
                "Electronica General",
                "Emprendimiento",
                "Ecuaciones Diferenciales",
                "Ondas y Particulas"};

        String []semestres={
                "Semestre 2020 - 1",
                "Semestre 2020 - 2",
                "Semestre 2021 - 1",
                "Semestre 2021 - 2",
                "Semestre 2022 - 1",
                "Semestre 2022 - 2"};

        String []tipoParciales={
                "1 Parcial",
                "2 Parcial",
                "Examen Final"};

        ArrayAdapter<String> adapterMaterias = new ArrayAdapter<String>(this,R.layout.text_spinner_semestre, materias);
        spinnermaterias.setAdapter(adapterMaterias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.text_spinner_semestre, semestres);
        spinnersemestres.setAdapter(adapter);
        ArrayAdapter<String> adapterTipoParcial = new ArrayAdapter<String>(this,R.layout.text_spinner_semestre, tipoParciales);
        spinnertipoparcial.setAdapter(adapterTipoParcial);

        spinnermaterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carpeta = spinnermaterias.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnersemestres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subcarpeta = spinnersemestres.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnertipoparcial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subsubcarpeta = spinnertipoparcial.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUploadImageView.setOnClickListener(v -> fileUpload());
            }
        });

        cerrarsubirfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
            }
        });

        dialogBuilder2.setView(contactPopupView2);
        dialog2 = dialogBuilder2.create();
        dialog2.show();
    }

    public void fileUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,File);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialogParcial = new ProgressDialog(this);
        Bitmap originBitmap = null;
        InputStream imageStream;

        if(requestCode == File){
            if(resultCode == RESULT_OK){
                progressDialogParcial.setTitle("Subiendo Imagen");
                progressDialogParcial.setMessage("Por Favor Espere un Momento");
                progressDialogParcial.setCancelable(false);
                progressDialogParcial.show();

                Uri FileUri = data.getData();
                StorageReference Folder = FirebaseStorage.getInstance().getReference().child(carpeta);
                StorageReference Folder2 = Folder.child(subcarpeta);
                StorageReference Folder3 = Folder2.child(subsubcarpeta);
                final StorageReference file_name = Folder3.child("file"+FileUri.getLastPathSegment());

                try {
                    imageStream = getContentResolver().openInputStream(FileUri);
                    originBitmap = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (originBitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    originBitmap.compress(Bitmap.CompressFormat.JPEG, 35, byteArrayOutputStream);
                    UploadTask uploadTask = file_name.putBytes(byteArrayOutputStream.toByteArray());

                    uploadTask.continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            progressDialogParcial.dismiss();
                            Toast.makeText(MainActivity.this, "Imagen Subida Correctamente", Toast.LENGTH_LONG).show();
                            Log.d("Mensaje", "Se subió correctamente");
                            throw task.getException();

                        }
                        return null;
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                        }
                    });
                }
            }
            /** codigo para subir imagenes sin comprimir  **/

//                file_name.putFile(FileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
//
//                    //codigo para realtime
////                    HashMap<String,String> hashMap = new HashMap<>();
////                    hashMap.put("link", String.valueOf(uri));
//
////                    //mostrar la imagen que se acaba de subir
//////                    Glide.with(SubirParciales.this)
//////                            .load(uri)
//////                            .centerCrop()
//////                            .into(imageView);
////                    myRef.setValue(hashMap);
//                    progressDialogParcial.dismiss();
//                    Toast.makeText(MainActivity.this, "Imagen Subida Correctamente", Toast.LENGTH_LONG).show();
//                    Log.d("Mensaje", "Se subió correctamente");
//
//                }));

            }

        }

    }

