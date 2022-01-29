package com.example.adminparcialpics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    Button addParciales,addMaterias;
    CardView cardmateria,cardParcial;
    EditText etCodigo,etMateria,ingresaricono,ingresarmateriafoto, ingresarsemestrefoto ;
    private String materia, codigo,codigoicono;
    private int iddrawable;
    private AlertDialog.Builder dialogBuilder,dialogBuilder2;
    private AlertDialog dialog,dialog2;
    ImageView mUploadImageView;

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
        etMateria= contactPopupView.findViewById(R.id.etxIngresarMateria);
        addMaterias = contactPopupView.findViewById(R.id.buttonShare12);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        addMaterias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codigo = etCodigo.getText().toString();
                materia = etMateria.getText().toString();
                codigoicono=ingresaricono.getText().toString();
                iddrawable = getResources().getIdentifier(codigoicono, "drawable", getPackageName());

                if (TextUtils.isEmpty(codigoicono)){
                    ingresaricono.setError("Ingrese un icono");
                    ingresaricono.requestFocus();
                }else if (TextUtils.isEmpty(codigo)){
                    etCodigo.setError("Ingrese un codigo");
                    etCodigo.requestFocus();
                }else if (TextUtils.isEmpty(materia)) {
                    etMateria.setError("Ingrese una materia");
                    etMateria.requestFocus();
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
                    etMateria.setText("");
                    ingresaricono.setText("");
                }
            }
        });
    }

    public void crearNuevoParcial(){
        dialogBuilder2 = new AlertDialog.Builder(this);
        final View contactPopupView2 = getLayoutInflater().inflate(R.layout.layou_bottom_anadir_parcial,null);
//        contactPopupView2.setPadding(5,70,0,30);
        ingresarmateriafoto = contactPopupView2.findViewById(R.id.editTextTextImgFolder);
        ingresarsemestrefoto = contactPopupView2.findViewById(R.id.editTextTextImgSubFolder);
        mUploadImageView = contactPopupView2.findViewById(R.id.uploadImageView);
        mUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "asd", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder2.setView(contactPopupView2);
        dialog2 = dialogBuilder2.create();
        dialog2.show();
//

    }

}