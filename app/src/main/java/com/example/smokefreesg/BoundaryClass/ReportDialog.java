package com.example.smokefreesg.BoundaryClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smokefreesg.ControllerClass.MapsController;
import com.example.smokefreesg.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_dialog,
                container, false);
        TextView instructions = v.findViewById(R.id.instructions);
        final EditText reportLodged = v.findViewById(R.id.text_input);
        Button submit = v.findViewById(R.id.submit);
        Button back = v.findViewById(R.id.back);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String report = reportLodged.getText().toString();
                String id = getArguments().getString("id");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                MapsController.lodgeReport(db, id, report);
                Toast.makeText(getContext() , "Report lodged", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }
}

