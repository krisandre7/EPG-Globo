package com.tpv.epgglobo.adapter;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.tpv.epgglobo.AlarmScheduler;
import com.tpv.epgglobo.databinding.ItemProgramBinding;
import com.tpv.epgglobo.model.Program;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.Calendar;

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

        private final ItemProgramBinding binding;

        public ViewHolder(ItemProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnProgramSelectedListener listener) {
            Program program = snapshot.toObject(Program.class);

            assert program != null;
            binding.programItemName.setText(program.getName());
            binding.programItemStartTime.setText(program.getStartTimeStr());

            AlarmScheduler scheduler = new AlarmScheduler(this.itemView.getContext());

            binding.programItemAlertBtn.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Date currentDate = new Date();
                    if (currentDate.getTime() <= program.getStartTime().getTime()) {
                            scheduler.schedule(program);
                            Toast.makeText(this.itemView.getContext(), "Notificação Agendada!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this.itemView.getContext(), "Nao foi possivel agendar!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onProgramSelected(snapshot);
                }
            });
        }
    }
}