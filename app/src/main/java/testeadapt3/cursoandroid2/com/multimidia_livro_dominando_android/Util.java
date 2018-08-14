package testeadapt3.cursoandroid2.com.multimidia_livro_dominando_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by laianeoliveira on 13/08/18.
 */

public abstract class Util {


    public static final int MIDIA_FOTO = 0;
    public static final int MIDIA_VIDEO = 1;
    public static final int MIDIA_AUDIO = 2;

    public static final int REQUESTCODE_FOTO = 1;
    public static final int REQUESTCOD_VIDEO = 2;
    public static final int REQUESTCOD_AUDIO = 3;

    private static final String ULTIMA_FOTO = "ultima_foto";
    private static final String ULTIMO_VIDEO = "ultimo_video";
    private static final String ULTIMO_AUDIO = "ultimo_audio";

    private static final String PREFERENCIA_MIDIA = "midia_prefs";
    private static final String PASTA_MIDIA = "Dominando_Android";

    private static final String[] EXTENSOES =
            new String[]{".jpg", ".mp4", ".3gp"};

    private static final String[] CHAVES_PREF =
            new String[]{ULTIMA_FOTO, ULTIMO_VIDEO, ULTIMO_AUDIO};

    public static File novaMidia(int tipo) {
        String nomeMidia = android.text.format.DateFormat.format(
                "yyyy-MM-dd_hhmmss", new Date()).toString();
        File dirMidia = new File(
                Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM ), PASTA_MIDIA );
        if (dirMidia.exists()) {
            dirMidia.mkdirs();
        }
        return new File( dirMidia, nomeMidia + EXTENSOES[tipo] );
    }

    public static void salvarUltimaMidia(Context ctx, int tipo, String midia) {

        //armazena o caminho da ultma midia salva,(dependendo do parâetro TIPO)
        SharedPreferences sharedPreferences =
                ctx.getSharedPreferences( PREFERENCIA_MIDIA, Context.MODE_PRIVATE );
        sharedPreferences.edit()
                .putString( CHAVES_PREF[tipo], midia )
                .commit();
        //android scaneia o sistema de arquivo e adiciona essa nova midia a galeria de midias
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
        Uri contentUri = Uri.parse( midia );
        mediaScanIntent.setData( contentUri );
        ctx.sendBroadcast( mediaScanIntent );
    }

    //metodo responsavel por retornar o caminho da ultima midia salva no sharedPreference do metodo salvarUltimaMidia
    public static String getUltimaMidia(Context ctx, int tipo) {
        return ctx.getSharedPreferences( PREFERENCIA_MIDIA, Context.MODE_PRIVATE )
                .getString( CHAVES_PREF[tipo], null );
    }

    //metodo para carregar a imagen redimencionada para area que queremos exibi-la ...ja que as fotos da camera sao amiores que a tela do celular.
    public static Bitmap carregarImagens(File imagem, int largura, int altura) {
        if (largura == 0 || altura == 0) return null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true; //indica que querler apenas o tamanho da imagem sem carrega-la em memoria
        BitmapFactory.decodeFile( imagem.getAbsolutePath(), bmOptions );//armazana em bmOption o tamanho real da imagem

        int larguraFoto = bmOptions.outWidth;
        int alturaFoto = bmOptions.outHeight;
        int escala = Math.min( larguraFoto / largura, alturaFoto / altura ); //aqui se calcula a escala que devemos aplicar na imagem

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = escala; // passado a escala aplicada para este atributo
        bmOptions.inPurgeable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;//definindo menos memoria nesta aplicacao

        Bitmap bitmap = BitmapFactory.decodeFile( imagem.getAbsolutePath(), bmOptions );
        bitmap = rotacionar( bitmap, imagem.getAbsolutePath() );
        return bitmap;

    }

    private static Bitmap rotacionar(Bitmap bitmap, String path) {

        try {
            ExifInterface ei = new ExifInterface( path );// saber a orientacao da imagem...
            int orientation = ei.getAttributeInt( ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotacionar( bitmap, 90 );
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotacionar( bitmap, 180 );
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotacionar( bitmap, 210 );
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap rotacionar(Bitmap sourse, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate( angle ); // somente aqui a imagem érotacionada
        Bitmap bitmap = Bitmap.createBitmap(
                sourse, 0, 0,
                sourse.getWidth(), sourse.getHeight(),
                matrix, true );
        return bitmap;
    }
}
