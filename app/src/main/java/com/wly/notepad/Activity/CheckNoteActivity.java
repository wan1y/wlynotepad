package com.wly.notepad.Activity;



//本java用来查看笔记



import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.com.wly.notepad.R;
import com.wly.notepad.Manager.NotesDB;

import com.wly.notepad.Util.DateTime;
import com.wly.notepad.Util.FilePathUtils;
import com.wly.notepad.Util.MapUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CheckNoteActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    //调用系统相机和相册回调参数
    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 10;
    private final int TAKE_PHOTO = 11;
    private final int CROP_PHOTO = 12;
    private final int REQUST_VIDEO = 13;
    private Bitmap bmp;
    private int bmpflag=0;



    //标签内容，媒体路径
    final String items[] = {"未标签","生活","个人","旅游","工作"};
    final String picItems[] = {"拍照","从相册选择"};
    private String tag = "未标签";
    private String picPath = "";
    private String audioPath = "";


    private int id;
    //字体
    private Paint paint;
    final String gitems[] = {"13磅","15磅", "18磅", "20磅", "23磅", "26磅", "30磅"};
    private String gtag = "1磅";
    //笔记内容,时间，图片路径
    private String content = "";
    private String dateNow;
    private String timeNow;
    private String timePast;
    private Uri ImageUri;

    //各种控件
    private ImageButton btnSave;
    private Button addTag;
    private EditText editText;
    private TextView timeTv;
    private NotesDB notesDB;
    private ImageView ivContent;
    private SQLiteDatabase dbWriter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        initView();
    }

    public void initView(){
        //初始化导航栏Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        toolbar.setTitle("查看笔记");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化底部导航栏
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.addnotes_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //初始化Button及图片控件
        btnSave = (ImageButton)findViewById(R.id.btn_ok);
        addTag = (Button)findViewById(R.id.tag);
        editText = (EditText)findViewById(R.id.edit_note);
        timeTv = (TextView)findViewById(R.id.showtime);
        ivContent = (ImageView)findViewById(R.id.imageContent1);



        //创建数据库对象
        dbWriter = getDataBase();

        //获取系统时间
        dateNow = DateTime.getTime();
        timeNow = dateNow.substring(12);

        //根据传入的数据初始化图片界面
        initSrcollView();

        //给按钮添加绑定事件
        btnSave.setOnClickListener(this);
        addTag.setOnClickListener(this);
        editText.setOnClickListener(this);

    }

    public void initSrcollView(){

        //获取从主页面传递过来的数据
        Intent intent = getIntent();
        id = intent.getIntExtra(NotesDB.NOTES_ID, 0);
        content = intent.getStringExtra(NotesDB.NOTES_CONTENT);
        tag = intent.getStringExtra(NotesDB.NOTES_TAG);
        timePast = intent.getStringExtra(NotesDB.NOTES_TIME).substring(12);
        picPath = intent.getStringExtra(NotesDB.NOTES_PIC);

        if(!picPath.equals("")) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(picPath);
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setImageBitmap(bitmap2);
        }




        //设置初始界面,时间和显示内容
        addTag.setText(tag);
        addTag.setCompoundDrawablesWithIntrinsicBounds(MapUtils.imageMap.get(tag),0,0,0);
        timeTv.setText(timePast);
        editText.setText(content);
    }
    ///点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                ModifyNotes();
                finish();
                break;
            case R.id.tag:
                setTag();
                break;
        }
    }

    //字体
    private void font(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        //设置标题
        builder.setTitle("选择粗细");
        //设置图标
        builder.setIcon(R.mipmap.font);
        //设置单选按钮
        builder.setSingleChoiceItems(gitems, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取出选择的条目
                String item = gitems[which];
                gtag = item;
                switch (gtag) {
                    case "13磅":
                        editText.setTextSize(13);
                        break;
                    case "15磅":
                        editText.setTextSize(15);
                        break;
                    case "18磅":
                        editText.setTextSize(18);
                        break;
                    case "20磅":
                        editText.setTextSize(20);
                        break;
                    case "23磅":
                        editText.setTextSize(23);
                        break;
                    case "26磅":
                        editText.setTextSize(26);
                        break;
                    case "30磅":
                        editText.setTextSize(30);
                        break;
                }
                //关闭对话框
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    //得到便笺的分类
    public void setTag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置标题
        builder.setTitle("选择标签");
        //设置图标
        builder.setIcon(R.mipmap.login6);
        //设置单选按钮
        builder.setSingleChoiceItems(items,0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取出选择的条目
                String item = items[which];
                tag = item;
                addTag.setText(tag);
                //根据不同的标签来设置ImageView
                addTag.setCompoundDrawablesWithIntrinsicBounds(MapUtils.imageMap.get(tag),0,0,0);
                //关闭对话框
                dialog.dismiss();
            }
        });
        builder.create().show();

    }
    //获取数据库对象
    public SQLiteDatabase getDataBase() {
        notesDB = new NotesDB(this,"notes.db",null,1);
        return notesDB.getWritableDatabase();
    }

    //设置Toolbar上面的返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            dbWriter.close();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //添加底部导航栏点击事件的响应
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.picture:
                choosePic();
                break;
            case R.id.font:
                font();
                break;
            case R.id.audio:
                initSpeech(this);
                break;
        }
        return false;
    }

    //选择图片的方式
    public void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置标题
        builder.setTitle("选择图片");
        //设置图标
        builder.setIcon(R.mipmap.pic);
        //设置单选按钮
        builder.setSingleChoiceItems(picItems,0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取出选择的条目
                String item = picItems[which];
                //根据不同的选择来获取图片
                switch(item)
                {
                    case "拍照":
                        takePhoto();
                        break;
                    case "从相册选择":
                        callGallery();
                        break;
                }
                //关闭对话框
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    //从图库添加图片
    public void callGallery() {
        ivContent.setVisibility(View.VISIBLE);	//设置图片可见
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, IMAGE_CODE);
    }

    //调用相机获取图片
    public void takePhoto() {
        ivContent.setVisibility(View.VISIBLE);    //设置图片可见
        //设置图片的路径
        picPath = System.currentTimeMillis()+".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(),picPath);
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            //兼容android7.0 使用共享文件的形式
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, outputImage.getAbsolutePath());
            //检查是否有存储权限，以免崩溃
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                Toast.makeText(this,"请开启存储权限",Toast.LENGTH_SHORT).show();
                return;
            }
            ImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        }else {
            ImageUri=Uri.fromFile(outputImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        }
        // 开启一个带有返回值的Activity，请求码为TAKE_PHOTO
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //获取图片后回调函数
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){// the onActivityResult() begin
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==TAKE_PHOTO) {//获取相机照片
            if(resultCode==RESULT_OK) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(ImageUri, "image/*");
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
                startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
            }
        }
        if (requestCode == IMAGE_CODE) {//获取相册照片
            ContentResolver resolver = getContentResolver();
            try {
                if (data == null){  //未选中图片
                    ivContent.setVisibility(View.INVISIBLE);
                    return;
                }
                else {
                    //获取图片存储的路径
                    Uri originalUri = data.getData();
                    picPath = FilePathUtils.getRealPathFromUri(this, data.getData());
                    bmp = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    ivContent.setImageBitmap(bmp);
                    bmpflag = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == CROP_PHOTO)
        {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(ImageUri));
                    ivContent.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }//the onActivityResult() end



    //语音识别模块
    //语音识别当前的文字
    /**
     * 初始化语音识别
     */
    public void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    //返回的result为识别后的汉字,直接赋值到TextView上即可
                    String result = parseVoice(recognizerResult.getResultString());
                    content = editText.getText().toString()+" "+result;
                    editText.setText(content);
                }
            }
            @Override
            public void onError(SpeechError speechError) {
            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }
    public void startRecod(final Context context)
    {

    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        AddNotesActivity.Voice voiceBean = gson.fromJson(resultString, AddNotesActivity.Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<AddNotesActivity.Voice.WSBean> ws = voiceBean.ws;
        for (AddNotesActivity.Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    /**
     * 语音对象封装
     */
    public class Voice {
        public ArrayList<AddNotesActivity.Voice.WSBean> ws;
        public class WSBean {
            public ArrayList<AddNotesActivity.Voice.CWBean> cw;
        }
        public class CWBean {
            public String w;
        }
    }


    //更新该便笺的信息
    private void ModifyNotes() {
        String ID = String.valueOf(id);

        ContentValues cv = new ContentValues();
        content = editText.getText().toString();
//        cv.put(NotesDB.USER_ID,MainUser.user.getId());
        cv.put(NotesDB.NOTES_TAG,tag);
        cv.put(NotesDB.NOTES_TIME,dateNow);
        cv.put(NotesDB.NOTES_CONTENT,content);
        cv.put(NotesDB.NOTES_PIC,picPath);
        cv.put(NotesDB.NOTES_AUDIO,audioPath);
        cv.put(NotesDB.NOTES_STATUS,"0");
        dbWriter.update(NotesDB.TABLE_NOTE,cv,"notes_id=?",new String[]{ID});
        Toast.makeText(getApplicationContext(),"更新便笺信息成功!",Toast.LENGTH_LONG).show();
        finish();
    }



}
