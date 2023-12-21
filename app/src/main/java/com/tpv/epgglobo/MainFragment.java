package com.tpv.epgglobo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.tpv.epgglobo.adapter.ProgramAdapter;
import com.tpv.epgglobo.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;

public class MainFragment extends Fragment implements
        ProgramAdapter.OnProgramSelectedListener {

    private static final int LIMIT = 50;

    private ProgramAdapter mAdapter;
    private Query mQuery;
    private ActivityMainBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = ActivityMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} restaurants
        mQuery = mFirestore
                .collection("guides")
                .document("2023-12-18")
                .collection("programs");

        // RecyclerView
        mAdapter = new ProgramAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerProgram.setVisibility(View.GONE);
//                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerProgram.setVisibility(View.VISIBLE);
//                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.recyclerProgram.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.recyclerProgram.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
    @Override
    public void onProgramSelected(DocumentSnapshot program) { }
}
