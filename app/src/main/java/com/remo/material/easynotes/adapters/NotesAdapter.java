package com.remo.material.easynotes.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remo.material.easynotes.R;
import com.remo.material.easynotes.interfaces.ItemSelection;
import com.remo.material.easynotes.model.NotesBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhanock on 19-7-2018.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.CustomViewHolder>{

    private ItemSelection itemSelection;
    private Context context;
    private List<NotesBuilder> notesBuilders;
    private List<NotesBuilder> notesBuildersFiltered;
    private boolean multiSelect;
    private ActionMode.Callback callback;


    public NotesAdapter(Context context, List<NotesBuilder> notesBuilders, ItemSelection itemSelection) {
        this.context = context;
        this.notesBuilders = notesBuilders;
        this.notesBuildersFiltered = notesBuilders;
        this.itemSelection = itemSelection;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_adapter, parent, false);
        return new CustomViewHolder(view);
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString();
                if (searchString.isEmpty()){
                    notesBuildersFiltered = notesBuilders;
                } else{
                    List<NotesBuilder> filteredList = new ArrayList<>();
                    for (NotesBuilder notesBuilder: notesBuilders){
                        if (notesBuilder.getTitle().toLowerCase().contains(searchString.toLowerCase())
                                || notesBuilder.getContent().toLowerCase().contains(searchString.toLowerCase())){
                            filteredList.add(notesBuilder);
                        }
                    }
                    notesBuildersFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = notesBuildersFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notesBuildersFiltered = (ArrayList<NotesBuilder>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        holder.textView.setText(notesBuildersFiltered.get(position).getTitle());
        holder.textView1.setText(notesBuildersFiltered.get(position).getContent());
    }

    public void deleteItem(int index){
        notesBuildersFiltered.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return notesBuildersFiltered.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {


        TextView textView;
        TextView textView1;
        ImageButton btn_delete;
        ConstraintLayout cl_constraint;

        CustomViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView1 = itemView.findViewById(R.id.textView1);
            cl_constraint = itemView.findViewById(R.id.cl_constraint);
            btn_delete = itemView.findViewById(R.id.btn_delete);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Note")
                            .setMessage("Are you sure you want to delete this note permanently?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    itemSelection.deleteMe(notesBuildersFiltered.get(getAdapterPosition()));
                                    deleteItem(getAdapterPosition());
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
            cl_constraint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemSelection.selectedMe(notesBuildersFiltered.get(getAdapterPosition()));

                }
            });
        }
    }


}
