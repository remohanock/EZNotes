package com.remo.material.easynotes.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.remo.material.easynotes.R;
import com.remo.material.easynotes.model.NotesBuilder;
import com.remo.material.easynotes.utils.Utilities;

public class MainActivity extends AppCompatActivity {

    EditText et_content;
    CoordinatorLayout container;
    Intent intent;
    boolean changed = true;
    boolean isExisting = false;
    private NotesBuilder selectedNote = new NotesBuilder();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et_content = findViewById(R.id.et_content);
        container = findViewById(R.id.container);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        if (intent.hasExtra(getString(R.string.note))){
            isExisting = true;
            selectedNote = intent.getParcelableExtra(getString(R.string.note));
            et_content.setText(selectedNote.getContent());
            getSupportActionBar().setTitle(selectedNote.getTitle());
        }else{
            isExisting = false;
            getSupportActionBar().setTitle("Untitled");
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_content.getText().toString().equals("")) {
                    if (isExisting) {
                        save(selectedNote, selectedNote.getTitle(),isExisting);
                    } else {
                        showAlert();
                    }
                }
            }
        });

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changed = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (isExisting){
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_rename).setVisible(true);
        }else{
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_rename).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if (!et_content.getText().toString().equals("")) {
                if (isExisting) {
                    save(selectedNote, selectedNote.getTitle(),isExisting);
                } else {
                    showAlert();
                }
            }
            return true;
        }
        if (id == R.id.action_theme){
            return true;
        }
        if (id == R.id.action_rename){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.filename_dialog);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final EditText et_filename = dialog.findViewById(R.id.et_filename);
            Button button = dialog.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (et_filename.getText().toString().equals("")) {
                        et_filename.setError("Enter a file name");
                    } else {
                        NotesBuilder.executeQuery("UPDATE NOTES_BUILDER SET TITLE = "+DatabaseUtils.sqlEscapeString(et_filename.getText().toString())+" WHERE NOTEID = "+selectedNote.getNoteid());
                        getSupportActionBar().setTitle(et_filename.getText().toString());
                        selectedNote.setTitle(et_filename.getText().toString());
                        Toast.makeText(MainActivity.this, "Note renamed to "+et_filename.getText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
            return true;
        }
        if (id == R.id.action_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note permanently?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NotesBuilder.executeQuery("DELETE FROM NOTES_BUILDER WHERE NOTEID = "+selectedNote.getNoteid());
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        if (id == android.R.id.home) {
            checkSavedStatus();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkSavedStatus();
    }

    private void checkSavedStatus() {
        if (isExisting) {
            if (!et_content.getText().toString().equals(selectedNote.getContent())) {
                showSaveDialog(selectedNote.getTitle());
            } else {
                finish();
            }
        } else {
            if (!et_content.getText().toString().equals("")) {
                if (changed) {
                    showSaveDialog(null);
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    private void showSaveDialog(final String fileName) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.unsaved_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button1 = dialog.findViewById(R.id.button1);
        Button button2 = dialog.findViewById(R.id.button2);
        Button button3 = dialog.findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileName != null) {
                    save(selectedNote,fileName,true);
                    finish();
                } else {
                    showAlert();
                }
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     *
     * @param note the note object to be saved
     * @param fileName name of the note to be saved
     */
    public void save(NotesBuilder note, String fileName, boolean exists) {
        long id;
        String contentToSave = DatabaseUtils.sqlEscapeString(et_content.getText().toString());
        String fileNameToSave = DatabaseUtils.sqlEscapeString(fileName);
        if (!exists){
            id = Utilities.getSharedPreferenceID(MainActivity.this);
            String values = id+","+fileNameToSave+","+contentToSave;
            Log.d("EZNOTES", "save: "+ values);
            NotesBuilder.executeQuery("INSERT INTO NOTES_BUILDER (NOTEID, TITLE, CONTENT) VALUES ("+values+")");
            Utilities.savePreferenceID(MainActivity.this,id+1);
        } else{
            id = note.getNoteid();
            NotesBuilder.executeQuery("UPDATE NOTES_BUILDER SET CONTENT = "+contentToSave+" WHERE NOTEID = "+id);
        }
        selectedNote.setId(id);
        selectedNote.setTitle(fileName);
        selectedNote.setContent(et_content.getText().toString());
        getSupportActionBar().setTitle(fileName);
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        changed = false;
        isExisting = true;
    }

    public void showAlert() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.filename_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText et_filename = dialog.findViewById(R.id.et_filename);
        Button button = dialog.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_filename.getText().toString().equals("")) {
                    et_filename.setError("Enter a file name");
                } else {
                    save(selectedNote, et_filename.getText().toString(),false);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();


    }

    /**
     * Displays the content of the selected file.
     *
     * @param serverID The index of data in db
     * @return The text content of the selected file
     */
    public String open(long serverID) {
        String content = "";
        NotesBuilder openNote = NotesBuilder.findById(NotesBuilder.class, serverID);
        content = openNote.getContent();
        return content;
    }
}
