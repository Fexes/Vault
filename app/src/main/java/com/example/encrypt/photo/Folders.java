package com.example.encrypt.photo;


import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.example.encrypt.R;
import com.example.encrypt.activity.BaseActivity;

import java.util.List;



public class Folders extends BaseActivity {

    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    private FolderGirdViewAdapter folderAdapter;
    private GridView gridView;
    TextView file_count;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        addAppActivity(Folders.this);
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        file_count = findViewById(R.id.file_count);
        gridView = findViewById(R.id.fileGridView);
        folderAdapter = new FolderGirdViewAdapter(this);
         findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(Folders.this, MainActivity.class));
                finish();
            }
        });
    }

    protected void onResume() {
        super.onResume();
         contentList = helper.getImagesBucketList(true);
         if(contentList.size()==1){
            file_count.setText(contentList.size()+" Album");
        }else {
            file_count.setText(contentList.size()+" Albums");
        }
        folderAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(contentList.size()==1){
                    file_count.setText(contentList.size()+" Album");
                }else {
                    file_count.setText(contentList.size()+" Albums");
                }
            }
        });
        gridView.setAdapter(folderAdapter);
    }
    public void onBackPressed() {
        super.onBackPressed();
       // startActivity(new Intent(Folders.this, MainActivity.class));
        finish();
    }
}
