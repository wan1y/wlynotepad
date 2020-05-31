package com.wly.notepad.Activity;


import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.com.wly.notepad.R;
import com.wly.notepad.Manager.NotesDB;

import com.wly.notepad.Manager.UserManage;
import com.wly.notepad.Model.MainUser;


public class UpdateInformationActivity extends AppCompatActivity {

    //private Button headChangeBtn;
    private ImageButton inforChangeBtn;
    private Button inforChangeBtn_back;
    private EditText nameChange;
    private  EditText passwordOld;
    private EditText passwordChange1;
    private EditText passwordChange2;
    private String headPhotoURL;
    private UserManage userManage;
    private NotesDB notesDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_information);
        //headChangeBtn = (Button) findViewById(R.id.headChangeButton);

        nameChange = (EditText)findViewById(R.id.nameChangeEdit);
        nameChange.setHint(MainUser.user.getName());
        passwordOld = (EditText)findViewById(R.id.passwordOldEdit);
        passwordChange1 = findViewById(R.id.passwordNew1Edit);
        passwordChange2 = (EditText)findViewById(R.id.passwordNew2Edit);

        //初始化导航栏Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.modifyme_toolbar);
        toolbar.setTitle("修改个人信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userManage = new UserManage();
        notesDB = new NotesDB(this,"data.db",null,1);


        //确定修改个人信息
        inforChangeBtn = (ImageButton)findViewById(R.id.updateInforBtn);
        inforChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameNew = nameChange.getText().toString();
                String passOld = passwordOld.getText().toString();
                String passNew1 = passwordChange1.getText().toString();
                String passNew2 = passwordChange2.getText().toString();
                if(passOld.equals(MainUser.user.getPassword())){

                    if(nameNew.length()==0){
                        Toast.makeText(UpdateInformationActivity.this, "修改个人资料失败，用户名为空", Toast.LENGTH_SHORT).show();
                    }
                    else if((!passNew1.equals(passNew2))||(passNew1.length()==0)){
                        Toast.makeText(UpdateInformationActivity.this, "修改个人资料失败，新密码格式错误或者两次密码不同", Toast.LENGTH_SHORT).show();
                    }
                    else{


                       userManage.updateUser(notesDB.getWritableDatabase(),MainUser.user.getId(),passNew1,nameNew);
                       MainUser.user=userManage.getuser(notesDB.getReadableDatabase(),MainUser.user.getId(),passNew1);
                        Toast.makeText(UpdateInformationActivity.this, "修改个人资料成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else{
                    Toast.makeText(UpdateInformationActivity.this, "修改个人资料失败，原密码错误", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    //设置Toolbar上面的返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
