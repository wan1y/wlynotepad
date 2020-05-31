package com.wly.notepad.Activity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.com.wly.notepad.R;
import com.wly.notepad.Manager.NotesDB;
import com.wly.notepad.Manager.User;
import com.wly.notepad.Manager.UserManage;
//import com.example.notes.notetaking.Model.MainUser;
import com.wly.notepad.Model.MainUser;


public class LoginActivity2 extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonRegister;
    private EditText idInput;
    private EditText passwordInput;
    private NotesDB  dbHelper;
    private UserManage userManage;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonRegister = (Button)findViewById(R.id.buttonReg);
        idInput = (EditText)findViewById(R.id.idInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        userManage = new UserManage();
        dbHelper = new NotesDB(this,"data.db",null,1);

        preferences = getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        //登录按钮响应事件
        buttonLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String id = idInput.getText().toString();
                String password = passwordInput.getText().toString();
                User user=null;
                user=userManage.getuser(dbHelper.getReadableDatabase(),id,password);
                if(user==null){
                    Toast.makeText(LoginActivity2.this, "登录失败，请检查帐号密码", Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.putString("USERNAME", id);
                    editor.putString("PASSWORD", password);
                    editor.commit();
                    MainUser.user=user;
                    Toast.makeText(LoginActivity2.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity2.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
        //注册按钮响应事件
        buttonRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity2.this,RegisterActivity.class);
                startActivity(intent);

            }
        });
    }
}
