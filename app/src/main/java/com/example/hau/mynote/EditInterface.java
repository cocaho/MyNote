package com.example.hau.mynote;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Hau on 2017/4/27.
 */

public class EditInterface extends AppCompatActivity{

    Toolbar mToolbar;
    boolean isImportant = false;
    int isStar=0;
    EditText editText;
    Note note;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        initToolbar();
        editText = (EditText)findViewById(R.id.et_note_content);
        note = (Note)getIntent().getSerializableExtra("note");
        if(note!=null) {
            editText.setText(note.getContent());
            editText.setSelection(editText.getText().length());
            isImportant = note.getInportant()==1?true:false;
        }

    }


    private void initToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar_note_content);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(note!=null) {
            if (note.getInportant() == 1) {
                //getResource.getDrawable()过时
                menu.findItem(R.id.star).setIcon(ContextCompat.getDrawable(this, R.mipmap.star_light));
                isImportant = true;
                isStar = 1;
            } else {
                menu.findItem(R.id.star).setIcon(ContextCompat.getDrawable(this, R.mipmap.star));
                isImportant = false;
                isStar = 0;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.save:
                if(note!=null){
                    String content = editText.getText().toString();
                    if(!TextUtils.isEmpty(editText.getText().toString())){
                    Note newNote = new Note(note.getId(),content,isStar,DateUtil.formatDateTime());
                    int result = MainActivity.noteDbAdapter.updateNote(newNote);}
                    else{
                        MainActivity.noteDbAdapter.deleteNoteById(note.getId());
                    }
                    finish();
                    break;
                }
                else{
                    if(TextUtils.isEmpty(editText.getText().toString())){
                        finish();
                        break;
                    }else {
                        String content = editText.getText().toString();
                        long result = MainActivity.noteDbAdapter.createNote(content,isImportant, DateUtil.formatDateTime());
                        finish();
                        break;
                    }
                }
            case R.id.star:
               if(!isImportant){
                   //getResource.getDrawable()过时
                   item.setIcon(ContextCompat.getDrawable(this,R.mipmap.star_light));
                   isImportant = true;
                   isStar=1;
               }else {
                   item.setIcon(ContextCompat.getDrawable(this,R.mipmap.star));
                   isImportant = false;
                   isStar=0;
               }
                Toast.makeText(this,"Star",Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
