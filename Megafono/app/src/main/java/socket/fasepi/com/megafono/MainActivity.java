package socket.fasepi.com.megafono;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Megafono";
    private EditText ipTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipTexto = (EditText) findViewById(R.id.editText);

        if (weHavePermissionToReadContacts() == false) {
            Log.i("Record", ": ---------------------ENTRA A REVISAR PERMISOS : ");
            ChequearPermiso();
            CreaCarpeta();
        }else{
            CreaCarpeta();
        }

    }



    public void Recibir(View v){

        Intent activity = new Intent();
        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.setClass(getApplicationContext(), ServerActivity.class);
        getApplicationContext().startActivity(activity);
        finish();

    }


    public void Enviar(View v){

        String ipSend = ipTexto.getText().toString();

        if(ipTexto.getText().toString().length() < 10){

            Toast.makeText(getApplicationContext(), "Debe ingresar la IP del servidor.", Toast.LENGTH_LONG).show();

        }else{


            Intent activity = new Intent();
            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.putExtra("ip", ipSend);
            activity.setClass(getApplicationContext(), ClientActivity.class);
            getApplicationContext().startActivity(activity);
            finish();




        }


    }


    public void CreaCarpeta(){


        File f = new File(Environment.getExternalStorageDirectory() + "/SonidosAlarmas");
        // Comprobamos si la carpeta está ya creada

        // Si la carpeta no está creada, la creamos.

        if(!f.isDirectory()) {
            String newFolder = "/SonidosAlarmas"; //cualquierCarpeta es el nombre de la Carpeta que vamos a crear
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File myNewFolder = new File(extStorageDirectory + newFolder);
            myNewFolder.mkdir(); //creamos la carpeta
        }else{
            Log.d(TAG,"La carpeta ya estaba creada");
        }
    }

    /******************************************************************************************************************************/
    /*************************************      PARA REVISAR LOS PERMISOS      ****************************************************/
    /******************************************************************************************************************************/
    private boolean weHavePermissionToReadContacts() {
        boolean permisos;
        boolean write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;



        if (write && audio) {
            permisos = true;
        } else {
            permisos = false;
        }
        //return ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED;
        return permisos;
    }

    public void ChequearPermiso() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
            Log.i("Record", "NO TIENE HABILITADOS PERMISOS DE LAS WRITE");
        } else {
            Log.i("Record", "YA TIENE HABILITADOS PERMISOS DE LAS WRITE");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            Log.i("Record", "NO TIENE HABILITADOS PERMISOS DE AUDIO");
        } else {
            Log.i("Record", "YA TIENE HABILITADOS PERMISOS DE AUDIO");
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("Record", " -----------------------onRequestPermissionsResult:   " + requestCode);
        switch (requestCode) {
            case 1:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    Log.i("Record", "NO TIENE HABILITADOS PERMISOS DE UBICACION");
                } else {
                    Log.i("Record", "YA TIENE HABILITADOS PERMISOS DE UBICACION");
                }
                break;
            case 2:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 4);
                    Log.i("Record", "NO TIENE HABILITADOS PERMISOS DE LEER TELEFONO");
                } else {
                    Log.i("Record", "YA TIENE HABILITADOS PERMISOS DE LEER TELEFONO");
                }
                break;

        }
    }

}
