package com.example.bitirme_threshhold;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ikinci_sayfa extends AppCompatActivity implements SensorEventListener {
    int puan = 100;
    private SensorManager sensorManager;
    TextView txt;
    boolean ok=false, gir=true;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    String timeStampp,raporSt = "";
    String satir = "";
    String etiket="0";
    int c=0;
    static String test_adi ="";
    FileWriter yaz;
    BufferedWriter yazici;
    String[] rapor;
    int sayPi=0,sayNi=0, saySav=0, saySov=0,sayCukur=0,saySas=0,saySos=0;
    File dosya;
    int sayac=0,sayac2=0,sayac3=0,sayac4=0,sayac5=0,sayac6=0,sayac7=0;
    TextToSpeech ttobj;
    boolean isBasladi = true;
    float[] accDeger = new float[]{0, 0, 0};
    float[] graDeger = new float[]{0, 0, 0};
    float[] laDeger = new float[]{0, 0, 0};
    float[] gyDeger = new float[]{0, 0, 0};
    PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ikinci_sayfa);

        mVoiceInputTv =findViewById(R.id.bitir);
        sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);
        txt=findViewById(R.id.text);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status !=TextToSpeech.ERROR){
                    ttobj.setLanguage(new Locale("tr", "TR"));
                }
            }
        });
        try {
            wakeLock.acquire();
            dosya_olustur();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor=event.sensor;
        if(gir) {
            try {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accDeger[0] = event.values[0];
                    accDeger[1] = event.values[1];
                    accDeger[2] = event.values[2];
                }
                if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                    laDeger[0] = event.values[0];
                    laDeger[1] = event.values[1];
                    laDeger[2] = event.values[2];
                }
                if (sensor.getType() == Sensor.TYPE_GRAVITY) {
                    graDeger[0] = event.values[0];
                    graDeger[1] = event.values[1];
                    graDeger[2] = event.values[2];
                }
                if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    gyDeger[0] = event.values[0];
                    gyDeger[1] = event.values[1];
                    gyDeger[2] = event.values[2];
                }
                if (laDeger[2] < -0.776) {
                    etiket = "İvme";
                    sayac++;
                    if (sayac % 100 == 0) {
                        puan -= 1;
                        ttobj.speak("pozitif" + etiket, TextToSpeech.QUEUE_FLUSH, null);
                        sayac = 0;
                        raporSt += "Pozitif İvme;";
                    }
                } else if (laDeger[2] > 1.268) {
                    etiket = "İvme";
                    sayac2++;
                    if (sayac2 % 100 == 0) {
                        puan -= 1;
                        ttobj.speak("negatif" + etiket, TextToSpeech.QUEUE_FLUSH, null);
                        sayac2 = 0;
                        raporSt += "Negatif İvme;";
                    }
                } else if (gyDeger[1] > 0.201) {
                    etiket = "Viraj";
                    sayac3++;
                    if (sayac3 % 100 == 0) {
                        puan -= 1;
                        ttobj.speak("Sola Dönüldü" + etiket, TextToSpeech.QUEUE_FLUSH, null);
                        sayac3 = 0;
                        raporSt += "Sola Viraj;";
                    }
                } else if (gyDeger[1] < -0.192) {
                    etiket = "Viraj";
                    sayac4++;
                    if (sayac4 % 100 == 0) {
                        puan -= 1;
                        ttobj.speak("Sağa Dönüldü" + etiket, TextToSpeech.QUEUE_FLUSH, null);
                        sayac4 = 0;
                        raporSt += "Sağa Viraj;";
                    }
                } else if (accDeger[0] > 1.20 || accDeger[0] < -0.046) {
                    etiket = "Çukur";
                    sayac5++;
                    if (sayac5 % 100 == 0) {
                        puan -= 1;
                        ttobj.speak(etiket, TextToSpeech.QUEUE_FLUSH, null);
                        sayac5 = 0;
                        raporSt += "Çukur;";
                    }
                } else if (laDeger[0] < -0.257) {
                    etiket = "Şerit";
                    sayac6++;
                    if (sayac6 % 100 == 0) {
                        puan -= 1;
                        ttobj.speak("Sola Şerit Değiştirme", TextToSpeech.QUEUE_FLUSH, null);
                        sayac6 = 0;
                        raporSt += "Sola Şerit;";
                    }
                } else if (laDeger[0] > 0.161) {
                    etiket = "Şerit";
                    sayac7++;
                    if (sayac7 % 100 == 0) {
                        puan -= 1;
                        ttobj.speak("Sağa Şerit Değiştirme", TextToSpeech.QUEUE_FLUSH, null);
                        sayac7 = 0;
                        raporSt += "Sağa Şerit;";
                    }
                }
                if (isBasladi) {
                    timeStampp = SimpleDateFormat.getTimeInstance().format(new Date());
                    satir = String.format("%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;", accDeger[0], accDeger[1], accDeger[2], graDeger[0], graDeger[1], graDeger[2], laDeger[0], laDeger[1], laDeger[2], gyDeger[0], gyDeger[1], gyDeger[2]);
                    String birlestir = "";
                    birlestir = timeStampp + ";" + etiket;
                    satir += birlestir;
                    Log.d("Eşleşti", satir);
                    yazici.write(satir + "\n");
                    txt.setText(etiket);
                    etiket = "0";
                    satir = "";
                }
            } catch (Exception e) {
                Log.e("Hata!", e.getMessage());
            }

        }
        if (ok) {
            if (ttobj.isSpeaking()) {

            } else {
                if (c == -1)
                    sad();
            }
        }
    }

    public void dosya_olustur() throws IOException {
       try {
           timeStampp= SimpleDateFormat.getTimeInstance().format(new Date());
           String dosyaAdi = "Veri-"+timeStampp+".txt";
           File klasor = new File(Environment.getExternalStoragePublicDirectory("Veri Topla/"+test_adi), "Veriler");
           if (!klasor.exists()) {
               if (!klasor.mkdirs()) {
                   Log.e("dosya", "Dosya oluluşturulamadı");
               }
               else
               {
                   Log.e("dosya","mkdir var");
               }
           }
           else{
               Log.e("dosya","exists var");
               //Toast.makeText(getApplicationContext(),"Bu isimde bir test klasörü bulunmakta",Toast.LENGTH_LONG).show();
           }
           dosya = new File(Environment.getExternalStoragePublicDirectory("Veri Topla/"+test_adi+"/Veriler"),dosyaAdi);
           yaz = new FileWriter(dosya, true);
           yazici = new BufferedWriter(yaz);
           if (!dosya.exists()) {
               dosya.createNewFile();
           }
           yazici.write("AccX;AccY;AccZ;GraX;GraY;GraZ;LAX;LAY;LAZ;GyroX;GyroY;GyroZ;Time2;Etiket\n");
       }catch (Exception e){
           Log.e("Hataaaaa!", e.getMessage());
           e.printStackTrace();
       }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
//    public void onPause(){
//        if(ttobj !=null){
//            ttobj.stop();
//            ttobj.shutdown();
//        }
//        super.onPause();
//    }
    public void set_bitir(View v){
        ok=true;
        c=-1;
        gir = false;
        ttobj.speak("Sürüşünüz tamamlandı. İyi günler dileriz. Sürüş puanınız"+puan+" Ne yapmak istersiniz ",TextToSpeech.QUEUE_FLUSH,null);        rapor = raporSt.split(";");
        for(int i=0;i<rapor.length-2;i++){
        switch (rapor[i]){
            case "Pozitif İvme":
                sayPi++;
                break;
            case "Negatif İvme":
                sayNi++;
                break;
            case "Sola Viraj":
                saySov++;
                break;
            case "Sağa Viraj":
                saySav++;
                break;
            case "Çukur":
                sayCukur++;
                break;
            case "Sağa Şerit":
                saySas++;
                break;
            case "Sola Şerit":
                saySos++;
                break;
        }

        }
        final AlertDialog alertDialog = new AlertDialog.Builder(ikinci_sayfa.this).create();
        alertDialog.setTitle("Sürüş Raporu:");
        alertDialog.setMessage("Posizitif İvme Sayısı: "+sayPi+"\nNegatif İvme Sayısı: "+sayNi+"\nSağa Viraj Sayısı: "+saySav+"\nSola Viraj Sayısı: "+saySov
                +"\nSağa Şerit Sayısı: "+saySas+"\nSola Şerit Sayısı: "+saySos+"\nÇukur Sayısı: "+sayCukur+"\nSürüş puanınız: "+puan);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Tamam",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "İndir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String dosyaAdi = "Sürüş Raporu"+timeStampp+".txt";
                    File klasor = new File(Environment.getExternalStoragePublicDirectory("Veri Topla/"+test_adi), "Veriler");
                    if (!klasor.exists()) {
                        if (!klasor.mkdirs()) {
                            Log.e("dosya", "Dosya oluluşturulamadı");
                        }
                        else
                        {
                            Log.e("dosya","mkdir var");
                        }
                    }
                    else{
                        Log.e("dosya","exists var");
                        //Toast.makeText(getApplicationContext(),"Bu isimde bir test klasörü bulunmakta",Toast.LENGTH_LONG).show();
                    }
                    dosya = new File(Environment.getExternalStoragePublicDirectory("Veri Topla/"+test_adi+"/Veriler"),dosyaAdi);
                    yaz = new FileWriter(dosya, true);
                    yazici = new BufferedWriter(yaz);
                    if (!dosya.exists()) {
                        dosya.createNewFile();
                    }
                    yazici.write("Posizitif İvme Sayısı: "+sayPi+"\nNegatif İvme Sayısı: "+sayNi+"\nSağa Viraj Sayısı: "+saySav+"\nSola Viraj Sayısı: "+saySov
                            +"\nSağa Şerit Sayısı: "+saySas+"\nSola Şerit Sayısı: "+saySos+"\nÇukur Sayısı: "+sayCukur+"\nSürüş puanınız: "+puan);
                    yazici.flush();
                    yazici.close();
                }catch (Exception e){
                    Log.e("Hataaaaa!", e.getMessage());
                    e.printStackTrace();
                }
                dialog.dismiss();
                finish();
                System.exit(0);
            }
        });
        alertDialog.show();
    }

    public void sad(){
        c++;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //mVoiceInputTv.setText(result.get(0));
                    if(result.get(0).equals("indir") || result.get(0).equals("kaydet") || result.get(0).equals("kapat")){
                        try {
                            String dosyaAdi = "Sürüş Raporu"+timeStampp+".txt";
                            File klasor = new File(Environment.getExternalStoragePublicDirectory("Veri Topla/"+test_adi), "Veriler");
                            if (!klasor.exists()) {
                                if (!klasor.mkdirs()) {
                                    Log.e("dosya", "Dosya oluluşturulamadı");
                                }
                                else
                                {
                                    Log.e("dosya","mkdir var");
                                }
                            }
                            else{
                                Log.e("dosya","exists var");
                                //Toast.makeText(getApplicationContext(),"Bu isimde bir test klasörü bulunmakta",Toast.LENGTH_LONG).show();
                            }
                            dosya = new File(Environment.getExternalStoragePublicDirectory("Veri Topla/"+test_adi+"/Veriler"),dosyaAdi);
                            yaz = new FileWriter(dosya, true);
                            yazici = new BufferedWriter(yaz);
                            if (!dosya.exists()) {
                                dosya.createNewFile();
                            }
                            yazici.write("Posizitif İvme Sayısı: "+sayPi+"\nNegatif İvme Sayısı: "+sayNi+"\nSağa Viraj Sayısı: "+saySav+"\nSola Viraj Sayısı: "+saySov
                                    +"\nSağa Şerit Sayısı: "+saySas+"\nSola Şerit Sayısı: "+saySos+"\nÇukur Sayısı: "+sayCukur+"\nSürüş puanınız: "+puan);
                            yazici.flush();
                            yazici.close();
                        }catch (Exception e){
                            Log.e("Hataaaaa!", e.getMessage());
                            e.printStackTrace();
                        }
                        //dialog.dismiss();
                        finish();
                        System.exit(0);
                    }
                    else{
                        gir = true;
                    }
                }
                break;
            }

        }
    }
}
