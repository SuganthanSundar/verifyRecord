package com.quantrium.verifydoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Takepicture extends AppCompatActivity {
    Button done_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);
        done_button=findViewById(R.id.done_button);
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Takepicture.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
