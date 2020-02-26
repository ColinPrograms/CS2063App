package com.mobile.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class TagSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagselect);

        final Button mbutton = findViewById(R.id.button);
        final TextView radiusText = findViewById(R.id.radiusText);
        final TextView locationText = findViewById(R.id.location);
        //AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TagSelectActivity.this, MapsActivity.class);
                intent.putExtra("Radius", radiusText.getText().toString());
                Log.v("EditText", radiusText.getText().toString());
                intent.putExtra("Latitude", 45.966425);
                intent.putExtra("Longitude", -66.645813);

                startActivity(intent);
            }
        });



        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mbutton.setEnabled(true);
                } else {
                    mbutton.setEnabled(false);
                }
            }
        });



    }


}
