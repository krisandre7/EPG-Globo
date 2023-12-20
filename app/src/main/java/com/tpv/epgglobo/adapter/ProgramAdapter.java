package com.tpv.epgglobo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.tpv.epgglobo.databinding.ItemProgramBinding;
import com.tpv.epgglobo.model.Program;

import java.util.List;
import java.util.Map;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class ProgramAdapter extends FirestoreAdapter<ProgramAdapter.ViewHolder> {

    public interface OnProgramSelectedListener {

        void onProgramSelected(DocumentSnapshot program);

    }

    private final OnProgramSelectedListener mListener;

    public ProgramAdapter(Query query, OnProgramSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemProgramBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemProgramBinding binding;

        public ViewHolder(ItemProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnProgramSelectedListener listener) {

            printDocumentSnapshot(snapshot);

            Program program = snapshot.toObject(Program.class);

            assert program != null;
            binding.programItemName.setText(program.getName());
            binding.programItemStartTime.setText(program.getStartTime());

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onProgramSelected(snapshot);
                }
            });
        }

        // Your method where you have the DocumentSnapshot
        public void printDocumentSnapshot(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()) {
                // Get data as a Map
                Map<String, Object> data = documentSnapshot.getData();

                // Check if data is not null
                if (data != null) {
                    // Iterate over the map and print key-value pairs
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        Log.d("DocumentSnapshot", entry.getKey() + ": " + entry.getValue());
                    }
                } else {
                    Log.d("DocumentSnapshot", "Document data is null.");
                }
            } else {
                Log.d("DocumentSnapshot", "Document does not exist.");
            }
        }

    }
}