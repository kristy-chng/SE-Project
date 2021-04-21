package com.example.smokefreesg.BoundaryClass;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smokefreesg.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetModal extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_modal,
                container, false);
        final String loc_info = getArguments().getString("loc_info");
        TextView coordinates_text = (TextView) v.findViewById(R.id.display_xy);
        coordinates_text.setText(loc_info);
        Button route_button = v.findViewById(R.id.route_button);
        final Button report_button = v.findViewById(R.id.report_button);

        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double[] coordinates = getArguments().getDoubleArray("coordinates");
                Uri navigationUri = Uri.parse("google.navigation:q=" + String.valueOf(coordinates[0]) + "," + String.valueOf(coordinates[1]) + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                dismiss();
            }
        });

        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportDialog reportDialog = new ReportDialog();
                Bundle args = new Bundle();
                args.putString("id", loc_info);
                reportDialog.setArguments(args);
                reportDialog.show(getChildFragmentManager(), "ReportDialog");
            }
        });

        return v;
    }
}
