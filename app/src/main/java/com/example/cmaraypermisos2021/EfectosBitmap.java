package com.example.cmaraypermisos2021;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.Random;

public class EfectosBitmap {
    public static Bitmap efectoSepia(Bitmap original) {
        //Devolvemos el bitmap en escala de grises.
        Bitmap sepia = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(sepia);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);

        // Convert to grayscale, then apply brown color
        colorMatrix.postConcat(colorScale);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(original, 0, 0, paint);
        return sepia;
    }

    public static Bitmap efectoMirror(Bitmap original) {
        //Devolvemos el bitmap con un flip horizontal.
        Bitmap mirror;
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        mirror = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

        return mirror;
    }

    public static Bitmap efectoBlackAndWhiteLento(Bitmap bitmap) {
        Bitmap bwBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        float[] hsv = new float[3];
        for (int col = 0; col < bitmap.getWidth(); col++) {
            for (int row = 0; row < bitmap.getHeight(); row++) {
                Color.colorToHSV(bitmap.getPixel(col, row), hsv);
                if (hsv[2] > 0.5f) {
                    bwBitmap.setPixel(col, row, 0xffffffff);
                } else {
                    bwBitmap.setPixel(col, row, 0xff000000);
                }
            }
        }
        return bwBitmap;
    }

    public static Bitmap efectoBlackAndWhite(Bitmap original) {
        //Devolvemos el bitmap en escala de grises.
        Bitmap bmpMonochrome = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        float m = 255f;
        float t = -255 * 128f;
        ColorMatrix threshold = new ColorMatrix(new float[]{
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });

        // Convert to grayscale, then scale and clamp
        colorMatrix.postConcat(threshold);
        ;
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(original, 0, 0, paint);
        return bmpMonochrome;
    }

    public static Bitmap efectoThermalBasico(Bitmap original) {
        Bitmap image2 = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        float[][] val = new float[original.getWidth()][original.getHeight()];
        int pixel;
        int red;
        int blue;
        int green;
        float[] hsbvals = new float[3];
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                pixel = original.getPixel(x, y);


                red = (pixel & 0x00ff0000) >> 16;
                blue = (pixel & 0x0000ff00) >> 8;
                green = (pixel & 0x000000ff);

                Color.colorToHSV(pixel, hsbvals);
                //RGBtoHSB(red, green, blue, hsbvals);
                if (hsbvals[2] > 0.7) {
                    image2.setPixel(x, y, Color.RED);
                } else if (hsbvals[2] >= 0.5 && hsbvals[2] < 0.7) {
                    image2.setPixel(x, y, Color.GREEN);
                } else if (hsbvals[2] >= 0.2 && hsbvals[2] < 0.5) {
                    image2.setPixel(x, y, Color.BLUE);
                }
            }
        }

        //Devolvemos el bitmap
        return image2;
    }

    public static Bitmap vignette(Bitmap image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        float radius = (float) (width / 1.2);
        int[] colors = new int[]{0, 0x55000000, 0xff000000};
        float[] positions = new float[]{0.0f, 0.5f, 1.0f};

        RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, colors, positions, Shader.TileMode.CLAMP);

        //RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

        Canvas canvas = new Canvas(image);
        canvas.drawARGB(1, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setShader(gradient);

        final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        final RectF rectf = new RectF(rect);

        canvas.drawRect(rectf, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(image, rect, rect, paint);

        return image;
    }

    public static Bitmap grayscale(Bitmap src) {
        //Array to generate Gray-Scale image
        float[] GrayArray = {
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        };

        ColorMatrix colorMatrixGray = new ColorMatrix(GrayArray);

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult = Bitmap
                .createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrixGray);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        src.recycle();
        src = null;

        return bitmapResult;
    }

    public static Bitmap noise(Bitmap source) {
        final int COLOR_MAX = 0xFF;

        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

        source.recycle();
        source = null;

        return bmOut;
    }
    public static Bitmap dibujaGafas(Bitmap bitmapOriginal, Bitmap gafas, Context ctx){

        boolean depuracion = true;
        Bitmap conGafas = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(conGafas);
        canvas.drawBitmap(bitmapOriginal, 0, 0, null);
        float xOjoD=0, xOjoI=0, yOjoD=0, yOjoI=0, distanciaOjos;

        Paint paint = new Paint();
        if (depuracion) {

            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
        }


        FaceDetector faceDetector = new FaceDetector.Builder(ctx)
                .setProminentFaceOnly(false).setProminentFaceOnly(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmapOriginal).build();
        SparseArray<Face> caras = faceDetector.detect(frame);

        for (int i = 0; i < caras.size(); i++) { //Actúo con cada cara
            Face thisFace = caras.valueAt(i);
            System.out.println("Euler: " + thisFace.getEulerY() + " " + thisFace.getEulerZ());

            for (Landmark landmark : thisFace.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);
                if (depuracion) {

                    canvas.drawCircle(cx, cy, 10, paint);
                }
                /*
                if (landmark.getType() == Landmark.RIGHT_MOUTH) {
                    xLabioD = cx;
                    yLabioD = cy;
                }
                if (landmark.getType() == Landmark.LEFT_MOUTH) {
                    xLabioI = cx;
                    yLabioI = cy;
                }
                if (landmark.getType() == Landmark.BOTTOM_MOUTH) {
                    yMenton = cy;
                    xMenton = cx;
                }
                if (landmark.getType() == Landmark.NOSE_BASE) {
                    xNariz = cx;
                    yNariz = cy;
                }
                */

                if (landmark.getType() == Landmark.RIGHT_EYE) {
                    xOjoD = cx;
                    yOjoD = cy;
                }
                if (landmark.getType() == Landmark.LEFT_EYE) {
                    xOjoI = cx;
                    yOjoI = cy;
                }
            }

            if (xOjoI > xOjoD) {
                float aux = xOjoI;
                xOjoI = xOjoD;
                xOjoD = aux;

                aux = yOjoI;
                yOjoI = yOjoD;
                yOjoD = aux;
            }


            distanciaOjos = Math.abs(xOjoD - xOjoI);
            float escalaGafas = 1.0f;
            float factor = 2f;
            int correccion = (int) ((int) ((float) distanciaOjos * escalaGafas) - distanciaOjos) / 2;

            Matrix mat = new Matrix();
            mat.setRotate(-thisFace.getEulerZ(), 0, gafas.getHeight() / 2);
            Bitmap gafasRotadas = Bitmap.createBitmap(gafas, 0, 0, gafas.getWidth(), gafas.getHeight(), mat, true);

            //tempCanvas.drawBitmap(sombrero, x1 , y1 , null);
            float proporcionGafa = (float) gafasRotadas.getWidth() / (float) gafasRotadas.getHeight();

            int anchoGafa = (int) (distanciaOjos * factor);
            int altoGafa = (int) (anchoGafa / proporcionGafa);

            Rect rectangualoFinal = new Rect((int) xOjoI - (int) ((anchoGafa - distanciaOjos) / 2) - correccion, (int) yOjoI - (altoGafa / 2) - correccion, (int) xOjoD + (int) ((anchoGafa - distanciaOjos) / 2) + correccion, (int) (int) yOjoD + (altoGafa / 2) + correccion);

            Rect rectanguloCara = new Rect((int) thisFace.getPosition().x, (int) thisFace.getPosition().y, (int) (thisFace.getPosition().x + thisFace.getWidth()), (int) (thisFace.getPosition().y + thisFace.getHeight()));

            if (depuracion) canvas.drawRect(rectangualoFinal, paint);


            canvas.drawBitmap(gafasRotadas, new Rect(0, 0, gafasRotadas.getWidth(), gafasRotadas.getHeight()), rectangualoFinal, null);


        }

        return conGafas;
    }

}
