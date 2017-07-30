package com.zjgsu.ai.calibrationtest;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;

/**
 * Created by Double on 2017/4/25.
 */

public class CaliPreActivity extends AppCompatActivity{
    private ImageView imageView;
    private EditText editText;
    private Button button1;
    private Button button2;

    private String path;
    private String category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cali_pre);
        path = null;

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        editText = (EditText) findViewById(R.id.edit_text);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = editText.getText().toString();
                if (path != null && category != null) {
                    CalibrationLab.Companion.get(getApplication()).addCalibration(new Calibration(category, path));
//                    Intent data = new Intent();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("category", category);
//                    bundle.putString("path", path);
//                    data.putExtra("bundle1", bundle);
//                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaliPreActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver resolver = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));
                imageView.setImageBitmap(bitmap);

                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                if(cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = new String(cursor.getString(column_index));

                }
                cursor.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
