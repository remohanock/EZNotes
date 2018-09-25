package com.remo.material.easynotes.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.remo.material.easynotes.R;
import com.remo.material.easynotes.adapters.NotesAdapter;
import com.remo.material.easynotes.interfaces.ItemSelection;
import com.remo.material.easynotes.model.NotesBuilder;

import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rv_notes;
    LinearLayout ll_empty;
    private SearchView searchView;
    NotesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rv_notes = findViewById(R.id.rv_notes);
        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        rv_notes.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
        rv_notes.setItemAnimator(new DefaultItemAnimator());
        ll_empty = findViewById(R.id.ll_empty);
        try {
            NotesBuilder.executeQuery("CREATE TABLE NOTES_BUILDER(NOTEID INT PRIMARY KEY, TITLE TEXT, CONTENT TEXT)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                if (!searchView.isIconified()){
                    searchView.setIconified(true);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getNotes();

    }

    private void getNotes() {
        List<NotesBuilder> notes;
        try {
            notes = NotesBuilder.listAll(NotesBuilder.class);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("Sorry! We have encountered a problem! We will fix this soon")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Oops!!")
                    .show();
            notes = null;
        }
        if (notes != null) {
            for (NotesBuilder notesBuilder : notes) {
                Log.d("NOTES", "getNotes: " + notesBuilder.getTitle() + " ID:" + notesBuilder.getId());
            }
            if (notes.size() == 0) {
                ll_empty.setVisibility(View.VISIBLE);
            } else {
                ll_empty.setVisibility(View.GONE);
            }
            try {
                Collections.reverse(notes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new NotesAdapter(HomeActivity.this, notes, new ItemSelection() {
                @Override
                public void selectedMe(NotesBuilder note) {
                    if (!searchView.isIconified()){
                        searchView.setIconified(true);
                    }
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra(getString(R.string.note),note);
                    startActivity(intent);
                }

                @Override
                public void deleteMe(NotesBuilder note) {
                    NotesBuilder.executeQuery("DELETE FROM NOTES_BUILDER WHERE NOTEID = "+note.getNoteid());
                    try {
                        if (NotesBuilder.listAll(NotesBuilder.class).size() == 0) {
                            ll_empty.setVisibility(View.VISIBLE);
                        } else {
                            ll_empty.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ll_empty.setVisibility(View.VISIBLE);
                    }
                }
            });
            rv_notes.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
