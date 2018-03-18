package com.mailon2.reproductormusica;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer reproductor;
    private AudioManager audioManager;
    private CountDownTimer countDownTimer=null;
    private CountDownTimer countDownTimerLetra=null;

    private ListView listaCanciones;
    private Button anterior,play,siguiente;
    private ArrayList<String> canciones;
    private ArrayAdapter<String> adaptador;
    private SeekBar volumenSeekBar;
    private SeekBar duracionSeekBar;
    private TextView textoCancion;

    private  boolean accionReproduccion = false;
    private int cancionSelecionada = 0;
    private int cancionContador = 0;


    public void botonPlay(View view){
        reproducirCancion();
        if(!accionReproduccion){
            playCancion();
            accionReproduccion=true;
        }else{
            pausarCancion();
            accionReproduccion=false;
        }
        /*Toast.makeText(this, Integer.toString(R.raw.bruno_mars_liquor_store_blue) , Toast.LENGTH_LONG).show();*/


    }

    public void siguienteCancion(View view){
        int cantidad = canciones.size();
        cancionContador+=1;
        if(cancionContador<cantidad){
            detenerCancion();
            cancionSelecionada=cancionContador;
            reproducirCancion();
            playCancion();
        }else{
            Toast.makeText(this, "No existe canción." , Toast.LENGTH_LONG).show();
            cancionContador-=1;
        }
    }

    public void anteriorCancion(View view){
        int cantidad = canciones.size();
        cancionContador-=1;
        if(cancionContador>-1){
            detenerCancion();
            cancionSelecionada=cancionContador;
            reproducirCancion();
            playCancion();
        }else{
            Toast.makeText(this, "No existe canción." , Toast.LENGTH_LONG).show();
            cancionContador+=1;
        }
    }

    public void reproducirCancion(){
        int numCancion = -1;
        /*Toast.makeText(this, Integer.toString(cancionSelecionada) , Toast.LENGTH_LONG).show();*/
        switch (cancionSelecionada){
            case 0:
                numCancion=R.raw.camila_todo_cambio;
                cancionContador=0;
                break;
            case 1:
                numCancion=R.raw.bruno_mars_liquor_store_blue;
                cancionContador=1;
                break;
            case 2:
                numCancion=R.raw.dread_mar_i_tu_sin_mi;
                cancionContador=2;
                break;
        }
        if(numCancion!=-1 && cancionSelecionada!=-1){
            reproductor = MediaPlayer.create(this,numCancion);
            int e = reproductor.getDuration();
            int a= (e/1000);
            desplazamientoSeekBar(a);
            desplazamientoLetra(cancionSelecionada);
            //Toast.makeText(this, Integer.toString(a)+":"+Integer.toString(b) , Toast.LENGTH_LONG).show();
            cancionSelecionada=-1;
        }
    }

    public void playCancion(){
        reproductor.start();
        accionReproduccion=true;
        play.setBackground(getResources().getDrawable(R.raw.pause));
    }

    public void pausarCancion(){
        reproductor.pause();
        accionReproduccion=false;
        play.setBackground(getResources().getDrawable(R.raw.play));
    }

    public void detenerCancion(){
        reproductor.stop();
        accionReproduccion=false;
        play.setBackground(getResources().getDrawable(R.raw.play));
    }

    private void controladorSonido(){

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumenSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumenSeekBar.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));

        volumenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listaCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cancionSelecionada=i;
                cancionContador=i;
                detenerCancion();
                countDownTimer.cancel();
                countDownTimerLetra.cancel();
            }
        });

        duracionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                if(b){
                    reproductor.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void desplazamientoSeekBar(int entrada){
        duracionSeekBar.setMax(entrada);

        long durac = reproductor.getDuration();

        countDownTimer = new CountDownTimer(durac,1000){
            public void onTick(long millisecond){
                duracionSeekBar.setPressed(false);
                int progreso = reproductor.getCurrentPosition()/1000;

                duracionSeekBar.setProgress(progreso);
            }
            public void onFinish(){
                siguienteCancion(getCurrentFocus());
            }
        };

        countDownTimer.start();
    }


    public void desplazamientoLetra(int entrada){
        long durac = reproductor.getDuration();

        String cancion = canciones.get(entrada);
        textoCancion.setText("Canción: "+cancion);

        countDownTimerLetra = new CountDownTimer(durac,10000){
            public void onTick(long millisecond){
                textoCancion.setTranslationX(1070f);
                textoCancion.setTranslationY(100f);
                textoCancion.animate().translationXBy(-2000f).setDuration(10000);
            }
            public void onFinish(){

            }
        };

        countDownTimerLetra.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anterior = findViewById(R.id.buttonAnterior);
        play = findViewById(R.id.buttonPlay);
        siguiente = findViewById(R.id.buttonSiguiente);
        listaCanciones = findViewById(R.id.listViewCanciones);
        textoCancion = findViewById(R.id.textViewLetra);


        play.setBackgroundDrawable(getResources().getDrawable(R.raw.play));

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        reproductor = MediaPlayer.create(this,R.raw.camila_todo_cambio);

        volumenSeekBar = findViewById(R.id.seekBarVolumen);
        duracionSeekBar = findViewById(R.id.seekBarDuracion);

        canciones =new ArrayList<String>();
        canciones.add("camila_todo_cambio.mp3");
        canciones.add("bruno_mars_liquor_store_blue.mp3");
        canciones.add("dread_mar_i_tu_sin_mi.mp3");
        adaptador=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,canciones);
        listaCanciones.setAdapter(adaptador);
        controladorSonido();

    }
}
