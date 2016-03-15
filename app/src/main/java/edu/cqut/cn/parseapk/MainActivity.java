package edu.cqut.cn.parseapk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    File downloadAPK;

    Class packageParseClass;
    Object mParse;
    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        getAPK();
        initParse();
        parseAPK();
    }

    private void parseAPK() {
        try {
            Method method = packageParseClass.getMethod("parsePackage",new Class<?>[]{File.class,int.class});
            Object result = method.invoke(mParse, downloadAPK, 0);

            addTextToTextView(((List) getFieldValue(result, "activities")).toString());
            addTextToTextView(((List) getFieldValue(result, "services")).toString());
            addTextToTextView(((List) getFieldValue(result, "providers")).toString());
            addTextToTextView(((List) getFieldValue(result, "permissions")).toString());
            addTextToTextView(((List) getFieldValue(result, "permissionGroups")).toString());
            addTextToTextView(((List) getFieldValue(result, "requestedPermissions")).toString());
            addTextToTextView(((List) getFieldValue(result, "receivers")).toString());
            addTextToTextView(((List) getFieldValue(result, "instrumentation")).toString());
            addTextToTextView(((String) getFieldValue(result, "packageName")).toString());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addTextToTextView(String s){
        String tempText = mTextView.getText().toString();
        if(!TextUtils.isEmpty(s)){
            tempText+="\n\n\n";
        }
        tempText = tempText+s;
        mTextView.setText(tempText);
    }

    private Object getFieldValue(Object result,String fieldName){
        try {
            Field f = result.getClass().getField(fieldName);
            f.setAccessible(true);
            return f.get(result);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initParse() {
        try {
            packageParseClass = Class.forName("android.content.pm.PackageParser");
            mParse = packageParseClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void getAPK() {
        downloadAPK = new File(Environment.getExternalStorageDirectory().getPath()+"/dun","base-apk.apk");
        if(downloadAPK.exists()){
            return;
        }
        String urlString = "http://gdown.baidu.com/data/wisegame/b22484c60d1cddba/REwenjianguanliqi_108.apk";
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在下载apk");
        OkHttpUtils
                .get()
                .url(urlString)
                .build()
                .execute(new FileCallBack(downloadAPK.getParentFile().getPath(),"base-apk.apk") {
                    @Override
                    public void inProgress(float progress, long total) {
                        Log.i(TAG, "inProgress: >>>"+progress);
                        pd.show();
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(File response) {
                        pd.dismiss();
                    }
                });


    }
}
