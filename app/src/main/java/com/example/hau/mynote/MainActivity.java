package com.example.hau.mynote;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;

import java.util.Map;


public class MainActivity extends AppCompatActivity  implements MaterialCab.Callback {

    private  RecyclerView recyclerView;
    private  Toolbar toolbar;
    private  FloatingActionButton fab;
    private  MaterialCab mCab;
    private  DrawerLayout drawerLayout;

    public  static NoteAdapter noteAdapter;
    public  static NoteDbAdapter noteDbAdapter;

    private boolean isFirstStart;
    private int checkedPosition;
    static Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EditInterface.class));
            }
        });

        noteDbAdapter = new NoteDbAdapter(this);
        noteDbAdapter.open();

        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(MainActivity.this,NoteDbAdapter.CONFIG);
        isFirstStart = sharedPreferencesUtil.getBoolean(NoteDbAdapter.IS_FIRST_START);
        if(!isFirstStart){
            noteDbAdapter.deleteAllNotes();
            insertSample();
            sharedPreferencesUtil.putBoolean(NoteDbAdapter.IS_FIRST_START,true);

        }

    }
    //在onRsume里初始化RecycleView是为了每次跳回主界面是，数据可能得到更新
    @Override
    protected void onResume() {
        super.onResume();
        initRecycleView();

    }


    private void initRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycle_notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        cursor = noteDbAdapter.fetchAllNotes();
        noteAdapter = new NoteAdapter(this,cursor,0);

        noteAdapter.setRecyclerViewOnItemLongClickListener(new NoteAdapter.RecyclerViewOnItemLongClickListener() {
            @Override
            public void onItemLongClickListener(View view, int position) {
                checkedPosition = position;
                showListDialog();

            }
        });

        noteAdapter.setRecyclerViewOnItemClickListener(new NoteAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if(cursor == null|| cursor.isClosed()){
                    if (cursor == null) {
                        Log.d("NoteActivity", "newCursor is null");
                        Toast.makeText(MainActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
                    } else if (cursor.isClosed()){
                        Log.d("NoteActivity", "newCursor is closed");
                        Toast.makeText(MainActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    cursor.moveToPosition(position);
                    String content = cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_CONTENT));
                    int important = cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_IMPORTANT));
                    int id = cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_ID));
                    Note clickNote = new Note(id,content,important);
                    Intent intent = new Intent(MainActivity.this,EditInterface.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("note",clickNote);
                    intent.putExtras(bundle);
                    startActivity(intent);



                }
            }
        });

        recyclerView.setAdapter(noteAdapter);

    }


    private void insertSample() {
        noteDbAdapter.createNote("Buy Learn Android Studio", true,DateUtil.formatDateTime());
        noteDbAdapter.createNote("Send Dad birthday gift", false,DateUtil.formatDateTime());
    }

    private void showListDialog() {
        final String[] items = { "删除便签","置顶便签" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(MainActivity.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        if (mCab == null)
                            mCab = new MaterialCab(MainActivity.this, R.id.cab_stub).setMenu(R.menu.meun_delete).start(MainActivity.this);
                        else if (!mCab.isActive())
                            mCab.reset().setMenu(R.menu.meun_delete).start(MainActivity.this);
                        noteAdapter.setShowBox();
                        noteAdapter.setSelectItem(checkedPosition);
                        noteAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        cursor.moveToPosition(checkedPosition);
                        int id = cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_ID));
                        Note editNote = noteDbAdapter.fetchNoteById(id);
                        editNote.setDateTime(DateUtil.formatDateTime());
                        noteDbAdapter.updateNote(editNote);
                        cursor = noteDbAdapter.fetchAllNotes();
                        noteAdapter.changeCursor(cursor);
                        break;
                }
                Toast.makeText(MainActivity.this,
                        "你点击了" + items[which],
                        Toast.LENGTH_SHORT).show();
            }
        });
        listDialog.show();
    }

//设置true，显示菜单
    @Override
    public boolean onCabCreated(MaterialCab cab, Menu menu) {
        return true;
    }

    @Override
    public boolean onCabItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                int nothing = 0;//0表示没有checkbox被选择，1表示有。
                Map<Integer,Boolean> map = noteAdapter.getMap();
                cursor.moveToFirst();
                for(int i=0;i<cursor.getCount();i++){
                    if (map.get(i)&&cursor.moveToPosition(i) ){
                        int id = cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_ID));
                        noteDbAdapter.deleteNoteById(id);
                        nothing = 1;

                }
                }
                if(nothing == 1) {
                    cursor = noteDbAdapter.fetchAllNotes();
                    noteAdapter.changeCursor(cursor);
                    noteAdapter = new NoteAdapter(this, cursor, 0);
                    noteAdapter.setShowBox();
                    recyclerView.setAdapter(noteAdapter);

                    mCab.finish();
                }
                else Toast.makeText(this,"请选择内容",Toast.LENGTH_SHORT).show();

                break;

    }
        return true;
    }

    @Override
    public boolean onCabFinished(MaterialCab cab) {
        noteAdapter.setShowBox();
        noteAdapter.notifyDataSetChanged();
        return true;
    }
    @Override
    public void onBackPressed() {
        if (mCab != null && mCab.isActive()) {
            mCab.finish();
            mCab = null;

        } else {
            super.onBackPressed();
        }
    }
}
