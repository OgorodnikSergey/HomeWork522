package ru.ogorodnik.homework522;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String FILE_LOGIN = "login.txt";
    private static final String FILE_PASSWORD = "password.txt";
    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView exampleText = (TextView) findViewById(R.id.textView2);
        final EditText login = (EditText) findViewById(R.id.login);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);
        final Button registrationBtn = (Button) findViewById(R.id.registrationBtn);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        checkBoxUpload();
        requestPermission();
        // Сохраняем настройки CheckBox
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxSave();
                Toast.makeText(MainActivity.this, getText(R.string.checkBoxTextSave), Toast.LENGTH_LONG).show();
            }
        });

        // Нажимаем на кнопку ЛОГИН - загружаем данные логина и пароля из файла и сравниваем с введенными
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoginAndPasswordValid()) {
                     String strLog = "";
                     String strPassword = "";

                    if (!checkBoxSettings()) {
                        //------------------------------------Загружаем логин
                        strLog = readLogin();
                        //------------------------------------Загружаем пароль
                        strPassword = readPassword();
                    } else {
                        //------------------------------------Загружаем логин
                        strLog = readLoginExternal();
                        //------------------------------------Загружаем пароль
                        strPassword = readPasswordExternal();
                    }

                    if (password.getText().toString().equals(strPassword) & login.getText().toString().equals(strLog)) {
                        exampleText.setText(getText(R.string.messageOkLogin));
                        Toast.makeText(MainActivity.this, getText(R.string.messageOkToastLogin), Toast.LENGTH_LONG).show();
                    } else {

                        exampleText.setText(getText(R.string.messageErrorLogin));
                        Toast.makeText(MainActivity.this, getText(R.string.messageErrorToastLogin), Toast.LENGTH_LONG).show();
                    }
                } else {
                    exampleText.setText(getText(R.string.messageErrorLoginEmpty));
                    Toast.makeText(MainActivity.this, getText(R.string.messageErrorToastLoginEmpty), Toast.LENGTH_LONG).show();
                }
            }
        });
//-------------------------------------------------------------------------------------------------------------------------------
        // Нажимаем на кнопку РЕГИСТРАЦИЯ - записываем данные логина и пароля в файл
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginAndPasswordValid()) {
                    final String loginText = login.getText().toString();
                    final String passwordText = password.getText().toString();
                    if (!checkBoxSettings()) {
                        // --------------------------------- сохраняем логин
                        saveData(FILE_LOGIN, loginText);
                        // --------------------------------- сохраняем пароль
                        saveData(FILE_PASSWORD, passwordText);
                    } else {
                        // --------------------------------- сохраняем логин на внешнем носителе
                        saveDataExternal(FILE_LOGIN, loginText);
                        // --------------------------------- сохраняем пароль на внешнем носителе
                        saveDataExternal(FILE_PASSWORD, passwordText);
                    }
                    //-----------------------------------
                    login.getText().clear();
                    password.getText().clear();
                    exampleText.setText(getText(R.string.messageOkRegistration));
                    Toast.makeText(MainActivity.this, getText(R.string.messageOkRegistration), Toast.LENGTH_LONG).show();
                } else {
                    exampleText.setText(getText(R.string.messageErrorRegistr));
                    Toast.makeText(MainActivity.this, getText(R.string.messageErrorToastRegistr), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //------------------------------------------------------------
    private String readLogin() {
        return readLineFromFile(FILE_LOGIN);
    }

    //------------------------------------------------------------
    private String readPassword() {
        return readLineFromFile(FILE_PASSWORD);
    }

    //------------------------------------------------------------
    private String readLoginExternal() {
        return readLineFromFileExternal(FILE_LOGIN);
    }

    //------------------------------------------------------------
    private String readPasswordExternal() {
        return readLineFromFileExternal(FILE_PASSWORD);
    }

    //------------------------READ FILE---------------------------
    private String readLineFromFile(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(openFileInput(fileName)));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //------------------------------------------------------------
    private void saveData(String fileName, String text) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //------------------------------------------------------------
    private boolean isLoginAndPasswordValid() {
        final EditText login = (EditText) findViewById(R.id.login);
        final EditText password = (EditText) findViewById(R.id.password);
        return !login.getText().toString().equals("") && !password.getText().toString().equals("");
    }

    //------------------------------------------------------------
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

//----------------------------------------------------------------------------------
// Сохраняем ЧекБокс
    public void checkBoxSave() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        SharedPreferences.Editor editor = mSettings.edit();
        if (checkBox.isChecked()) {
            editor.putBoolean(APP_PREFERENCES, true);
            editor.apply();
        } else {
            editor.putBoolean(APP_PREFERENCES, false);
            editor.apply();
        }
    }
//----------------------------------------------------------------------------------
// Загружаем ЧекБокс
    public void checkBoxUpload() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(mSettings.getBoolean(APP_PREFERENCES, false));
    }

    //----------------------------------------------------------------------------------
// Проверяем состояние ЧекБокс
    private boolean checkBoxSettings() {
        return (mSettings.getBoolean(APP_PREFERENCES, false));
    }

    //----------------------------------------------------------------------------------
// Читаем с внешнего носителя
    private String readLineFromFileExternal(String fileName) {
        if (isExternalStorageReadable()) {
            FileInputStream fis = null;
            try {
                File textFile = new File(Environment.getExternalStorageDirectory(), fileName);
                fis = new FileInputStream(textFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader buff = new BufferedReader(isr);
                return buff.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Toast.makeText(this, "Чтение с внешнего носителя запрещено", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private boolean isExternalStorageReadable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.i("Статус", "Можно читать с внешнего носителя");
            return true;
        } else {
            Log.i("Статус", "Нельзя читать с внешнего носителя");
            return false;
        }
    }
    private boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.i("Статус","Можно записывать на внешний носитель");
            return true;
        }else{
            Log.i("Статус","Нельзя записывать на внешний носитель");
            return false;
        }
    }

    private void saveDataExternal(String fileName, String text) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FileOutputStream fos = null;
            try {
                File textFile = new File(Environment.getExternalStorageDirectory(), fileName);
                fos = new FileOutputStream(textFile);
                fos.write(text.getBytes());

                Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Toast.makeText(this, "Нельзя записать на внешний носитель", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}