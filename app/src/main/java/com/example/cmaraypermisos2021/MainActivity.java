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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
    private static final int VENGO_DE_GALERIA = 3;
    Button buttonHacerFoto, buttonHacerFotoCalidad, buttonDeGaleria, buttonByN, buttonDibujar, buttonGafas;
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

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.example.cmaraypermisos2021.fileprovider", fichero));

        if (intent.resolveActivity(getPackageManager()) != null) { //Debo permitir la consulta en el android manifest
            startActivityForResult(intent, VENGO_DE_CAMARA_CON_CALIDAD);
        } else {
            Toast.makeText(MainActivity.this, "Necesitas instalar o tener una c??mara.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VENGO_DE_CAMARA && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == VENGO_DE_CAMARA_CON_CALIDAD) {
            if (resultCode == RESULT_OK) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));

            } else {
                fichero.delete();

            }
        } else if (requestCode == VENGO_DE_GALERIA) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
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
        //Doy permisos para que la c??mara pueda ver el fichero que yo he creado:
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //Fin de dar permisos

        buttonHacerFoto = findViewById(R.id.buttonHacerFoto);
        imageView = findViewById(R.id.imageView);
        buttonHacerFotoCalidad = findViewById(R.id.buttonHacerFotoCalidad);
        buttonDeGaleria = findViewById(R.id.buttonGaler??a);
        buttonByN = findViewById(R.id.buttonBlancoYNegro);
        buttonDibujar = findViewById(R.id.buttonDibujar);
        buttonGafas = findViewById(R.id.buttonGafas);

        buttonGafas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                imageView.setImageBitmap(EfectosBitmap.dibujaGafas(b,((BitmapDrawable)getDrawable(R.drawable.ojos)).getBitmap(), getApplicationContext()));
            }
        });
        buttonByN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                imageView.setImageBitmap(EfectosBitmap.grayscale(b));
            }
        });

        buttonDibujar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                // Canvas canvas = new Canvas(b); //Esto falla por ser inmutable Necesito una copia
//Uno nuevo                  Bitmap copy = Bitmap.createBitmap (bitmap.getWidth (), bitmap.getHeight (), Bitmap.Config.ARGB_8888); // importante
                Bitmap copia = b.copy(b.getConfig(), true);
                Canvas canvas = new Canvas(copia);
                Paint pincel = new Paint();
                pincel.setColor(Color.BLUE);
                pincel.setStrokeWidth(8);
                pincel.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(100, 100, 100, pincel);
                imageView.setImageBitmap(copia);
            }
        });



        buttonDeGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galer??a = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(galer??a, "Selecciona galer??a"), VENGO_DE_GALERIA);
            }
        });

        buttonHacerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) { //Debo permitir la consulta en el android manifest
                    startActivityForResult(intent, VENGO_DE_CAMARA);
                } else {
                    Toast.makeText(MainActivity.this, "Necesitas instalar o tener una c??mara.", Toast.LENGTH_SHORT).show();
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