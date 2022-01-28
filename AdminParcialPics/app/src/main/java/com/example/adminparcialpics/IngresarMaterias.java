package com.example.adminparcialpics;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IngresarMaterias extends AppCompatActivity {
    EditText etCodigo,etMateria,ingresaricono;
    private String materia, codigo,codigoicono;
    private int iddrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_materias);
        etCodigo = findViewById(R.id.etxIngresarCodMateria);
        etMateria = findViewById(R.id.etxIngresarMateria);
        ingresaricono = findViewById(R.id.etxIngresarNombreIcono);



    }
    public void agregarMateria (View v){
        codigo = etCodigo.getText().toString();
        materia = etMateria.getText().toString();
        codigoicono=ingresaricono.getText().toString();
        iddrawable = getResources().getIdentifier(codigoicono, "drawable", getPackageName());
        MateriasHome materiasHome = new MateriasHome();
        materiasHome.setFoto(iddrawable);
        materiasHome.setNombre(materia);
        materiasHome.setCodigo(codigo);
        //instancia de la bd en firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //para el id el reference push y se envia a la bd en fire...
        DatabaseReference reference = database.getReference("Asignaturas");
        reference.push().setValue(materiasHome);
    }
}