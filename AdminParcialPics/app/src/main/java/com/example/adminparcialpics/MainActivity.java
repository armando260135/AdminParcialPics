package com.example.adminparcialpics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity {
    private Button addMaterias,registrarUsuario;
    private CardView cardmateria,cardParcial,cardUsuario;
    private EditText etCodigo,ingresaricono,etEmail ,etPassword;
    private TextView cerrarsubirfoto,cerrarmateria,cerraragregarusuario;
    private String materia, codigo,codigoicono,userID;
    private int iddrawable;
    private AlertDialog.Builder dialogBuilder,dialogBuilder2,dialogBuilderUsuario;
    private AlertDialog dialog,dialog2,dialogusuario;
    private Spinner spinnermaterias,spinnersemestres,spinnertipoparcial;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //subir img
    private static final int File = 1 ;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.uploadImageView)
    ImageView mUploadImageView;
    private ProgressDialog progressDialogParcial,progressDialogRegisterUser;
    public static String carpeta = "";
    public static String subcarpeta = "";
    public static String subsubcarpeta = "1 Parcial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardmateria = findViewById(R.id.cardMateria);
        cardParcial = findViewById(R.id.cardParcial);
        cardUsuario = findViewById(R.id.cardUsuario);
        progressDialogRegisterUser = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        cardUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearNuevoUsuario();
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

        ArrayAdapter<String> adapterMaterias = new ArrayAdapter<String>(this,R.layout.text_spinner_semestre,getResources().getStringArray(R.array.listmaterias));
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
                    ingresaricono.setError(getResources().getString(R.string.input_error));
                    ingresaricono.requestFocus();
                }else if (TextUtils.isEmpty(codigo)){
                    etCodigo.setError(getResources().getString(R.string.input_error));
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
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.materia_succesful), Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> adapterMaterias = new ArrayAdapter<String>(this,R.layout.text_spinner_semestre,getResources().getStringArray(R.array.listmaterias));
        spinnermaterias.setAdapter(adapterMaterias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.text_spinner_semestre, getResources().getStringArray(R.array.listsemestres));
        spinnersemestres.setAdapter(adapter);
        ArrayAdapter<String> adapterTipoParcial = new ArrayAdapter<String>(this,R.layout.text_spinner_semestre, getResources().getStringArray(R.array.listparciales));
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

    public void crearNuevoUsuario(){
        dialogBuilderUsuario = new AlertDialog.Builder(this);
        final View contactPopupViewUsuario = getLayoutInflater().inflate(R.layout.layout_agregar_usuario,null);
        etEmail = contactPopupViewUsuario.findViewById(R.id.etxIngresarMail);
        etPassword = contactPopupViewUsuario.findViewById(R.id.etxIngresarPassword);
        registrarUsuario = contactPopupViewUsuario.findViewById(R.id.btn_registrar_usuario);
        cerraragregarusuario = contactPopupViewUsuario.findViewById(R.id.txt_cerrar);

        registrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                progressDialogRegisterUser.setTitle(getResources().getString(R.string.txt_progress_title_register));
                progressDialogRegisterUser.setMessage(getResources().getString(R.string.moment_upload));
                progressDialogRegisterUser.show();

                mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection("usersFromAdmin").document(userID);

                            Map<String,Object> user = new HashMap<>();
                            user.put("Nombre",mail);
                            user.put("Password", password);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG","onSuccess: Datos Registrados" + userID);
                                    progressDialogRegisterUser.dismiss();
                                }
                            });
                            progressDialogRegisterUser.dismiss();
                            etEmail.setText("");
                            etPassword.setText("");
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.txt_toast_usuario), Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialogRegisterUser.dismiss();
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_toast_usuario_fallido) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialogRegisterUser.dismiss();
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_toast_usuario_consulta_fallida) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cerraragregarusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogusuario.dismiss();
            }
        });

        dialogBuilderUsuario.setView(contactPopupViewUsuario);
        dialogusuario = dialogBuilderUsuario.create();
        dialogusuario.show();
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
                progressDialogParcial.setTitle(R.string.upload_image);
                progressDialogParcial.setMessage(getResources().getString(R.string.moment_upload));
                progressDialogParcial.setCancelable(false);
                progressDialogParcial.show();

                Uri FileUri = data.getData();
                StorageReference Folder = FirebaseStorage.getInstance().getReference().child(carpeta);
                StorageReference Folder2 = Folder.child(subcarpeta);
                StorageReference Folder3 = Folder2.child(subsubcarpeta);
                final StorageReference file_name = Folder3.child("file"+FileUri.getLastPathSegment());

                //codigo para comprimir imagen
                try {
                    imageStream = getContentResolver().openInputStream(FileUri);
                    originBitmap = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (originBitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    //modificar el quality para mayor calidad pero mayor peso ojo!
                    originBitmap.compress(Bitmap.CompressFormat.JPEG, 35, byteArrayOutputStream);
                    UploadTask uploadTask = file_name.putBytes(byteArrayOutputStream.toByteArray());

                    uploadTask.continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            progressDialogParcial.dismiss();
                            Toast.makeText(MainActivity.this, R.string.upload_succesful, Toast.LENGTH_LONG).show();
                            Log.d("Mensaje", getResources().getString(R.string.upload_succesful));
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

