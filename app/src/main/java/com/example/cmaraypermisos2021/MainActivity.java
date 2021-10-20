package com.example.cmaraypermisos2021;

import static android.provider.CalendarContract.CalendarCache.URI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int VENGO_DE_CAMARA = 1;
    private static final int PEDI_PERMISO_ESCRITURA = 1;
    private static final int VENGO_DE_CAMARA_CON_CALIDAD = 2;
    Button buttonHacerFoto, buttonHacerFotoCalidad;
    ImageView imageView;
    private File fichero;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PEDI_PERMISO_ESCRITURA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hacerLaFotoConCalidad();
            } else {
                Toast.makeText(this, "Sin permiso de escritura no hay foto de calidad", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void hacerLaFotoConCalidad() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            fichero = crearFicheroFoto();
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,"com.example.cmaraypermisos2021.fileprovider", fichero));

        if (intent.resolveActivity(getPackageManager()) != null) { //Debo permitir la consulta en el android manifest
            startActivityForResult(intent, VENGO_DE_CAMARA_CON_CALIDAD);
        } else {
            Toast.makeText(MainActivity.this, "Necesitas instalar o tener una cámara.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VENGO_DE_CAMARA && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == VENGO_DE_CAMARA_CON_CALIDAD) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));
        }
    }

    private File crearFicheroFoto() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_").format(new Date());
        String nombreFichero = "fotos_" + fechaYHora;
        File carpetaFotos = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        carpetaFotos.mkdirs();
        File imagenAltaResolucion = File.createTempFile(nombreFichero, ".jpg", carpetaFotos);
        return imagenAltaResolucion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Doy permisos para que la cámara pueda ver el fichero que yo he creado:
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //Fin de dar permisos

        buttonHacerFoto = findViewById(R.id.buttonHacerFoto);
        imageView = findViewById(R.id.imageView);
        buttonHacerFotoCalidad = findViewById(R.id.buttonHacerFotoCalidad);

        buttonHacerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) { //Debo permitir la consulta en el android manifest
                    startActivityForResult(intent, VENGO_DE_CAMARA);
                } else {
                    Toast.makeText(MainActivity.this, "Necesitas instalar o tener una cámara.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonHacerFotoCalidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirPermisoParaFoto();
            }
        });
    }

    private void pedirPermisoParaFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //No tengo permiso
            //Pedir permiso:
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PEDI_PERMISO_ESCRITURA);
            }

        } else {
            hacerLaFotoConCalidad();
        }

    }
}