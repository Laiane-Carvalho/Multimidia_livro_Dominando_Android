package testeadapt3.cursoandroid2.com.multimidia_livro_dominando_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

public class CameraFotoFragment extends Fragment
        implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    File mcaminhoFoto;
    ImageView mImageViewFoto;
    CarregarImageTask mTask;
    int mLarguraImagem;
    int mAlturaImagem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setRetainInstance( true );

        //carregar a imagem caso ja tenha carregado anteriormente...
        String caminhoFoto = Util.getUltimaMidia( getActivity(), Util.MIDIA_FOTO );
        if (caminhoFoto != null) {
            mcaminhoFoto = new File( caminhoFoto );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inicializamos o layout do fragmento ...
        View layout = inflater.inflate(
                R.layout.fragment_camera_foto, container, false );
        layout.findViewById( R.id.btnFoto ).setOnClickListener( this );
        mImageViewFoto = layout.findViewById( R.id.imgFoto );

        //onGlobal...échamado quando o layout estiver pronto com as dimensoes definidas, pois precisamos saber as dimensoes(largura,altura) para mostrar a imagem
        //so notificamos o evento uma vez, neste momento a imagem é carregada e desreguistramos a classe como listner das mudancas de layout.
        layout.getViewTreeObserver().addOnGlobalLayoutListener( this );
        return layout;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        //verificamos se o resultado que esta vindo é da requisicao feita por nossa tela
        if (resultCode == Activity.RESULT_OK && requestCode == Util.REQUESTCODE_FOTO) {
            carregarImagem();
        }
    }

    @Override
    public void onGlobalLayout() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {

            getView().getViewTreeObserver().removeOnGlobalLayoutListener( this );
        } else {

            getView().getViewTreeObserver().removeOnGlobalLayoutListener( this );
        }
        mLarguraImagem = mImageViewFoto.getWidth();
        mAlturaImagem = mImageViewFoto.getHeight();
        carregarImagem();
    }

    @Override
    public void onClick(View view) {
        //iniciamos o fluxo para tirar uma fotografia...
        if (view.getId() == R.id.btnFoto) {
            if (ActivityCompat.checkSelfPermission( getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE ) ==
                    PackageManager.PERMISSION_GRANTED) {//verificamos se possui a permissao de armazenar a imagem no cartao de memoria
                abrirCamera();
            } else {
                ActivityCompat.requestPermissions( getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0 );
            }
        }

    }

    private void abrirCamera() {
        mcaminhoFoto = Util.novaMidia( Util.MIDIA_FOTO );
        Intent it = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        it.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
        Uri photoURI = FileProvider.getUriForFile( getContext(),  "testeadapt3.cursoandroid2.com.multimidia_livro_dominando_android", mcaminhoFoto );
        it.putExtra( MediaStore.EXTRA_OUTPUT, photoURI );//utilizado para a imagem ter a resolucao e qualidade que a camera realmente tirou para ser salva
        startActivityForResult( it, Util.REQUESTCODE_FOTO );//tratar a foto tirada no medoto onActivityResult...
    }

    private void carregarImagem() {
        if (mcaminhoFoto != null && mcaminhoFoto.exists()) {
            if (mTask == null || mTask.getStatus != AsyncTask.Status.RUNNING) {//foto tirada da camera precisa de muita memoria para ser alocada, o async éusado para carrega-las
                mTask = new CarregarImageTask();
                mTask.execute();
            }
        }
    }

    //classe interna
    class CarregarImageTask extends AsyncTask <Void, Void, Bitmap> {

        public Status getStatus;

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return Util.carregarImagens(
                    mcaminhoFoto,
                    mLarguraImagem,
                    mAlturaImagem );
        }

        //seta a imagem carregada aqui, e salva o caminho da mesma
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute( bitmap );
            if (bitmap != null) {
                mImageViewFoto.setImageBitmap( bitmap );
                Util.salvarUltimaMidia( getActivity(),
                        Util.MIDIA_FOTO, mcaminhoFoto.getAbsolutePath() );
            }
        }
    }
}
