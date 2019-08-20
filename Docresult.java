package com.quantrium.verifydoc;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Docresult extends AppCompatActivity implements View.OnClickListener {
    private TextView idType,userName,userDob,userIssuedDate,userFatherName,userIdNumber,textExtracted;
    private Button finalVerifiy,takePicture;
    LinearLayout linearLayout_idtype,linearLayout_extracteddata;
    private ResponseData responseData;
    private String type,name,dob,issuedDate,fatherName,idNumber;

    public Docresult() {
        super();
    }

    public Docresult(ResponseData response) {
        responseData=response;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docresult);
        finalVerifiy= findViewById(R.id.but__verify_details);
        idType=findViewById(R.id.card_type);
        userName=findViewById(R.id.edit_text_name);
        userDob=findViewById(R.id.edt_dob_user);
        userIssuedDate=findViewById(R.id.edt_issuedDate);
        userFatherName=findViewById(R.id.edt_fName);
        userIdNumber=findViewById(R.id.edt_idNumber);
        textExtracted=findViewById(R.id.text_extracted);
        linearLayout_idtype=findViewById(R.id.linear_layout_idtype);
        linearLayout_extracteddata=findViewById(R.id.linear_layout_extracted_details);
        takePicture=findViewById(R.id.but__try_Again_but);
        takePicture.setVisibility(View.INVISIBLE);
            type = responseData.getDocType();
            name = responseData.getName();
            dob = responseData.getDob();
            issuedDate = responseData.getIssueDate();
            fatherName = responseData.getFather();
            idNumber = responseData.getDocId();
            idType.setText(type);
            userName.setText(name);
            userDob.setText(dob);
            userIssuedDate.setText(issuedDate);
            userFatherName.setText(fatherName);
            userIdNumber.setText(idNumber);
            finalVerifiy.setOnClickListener(this);
       /* }else{
            linearLayout_idtype.setVisibility(View.INVISIBLE);
            linearLayout_extracteddata.setVisibility(View.INVISIBLE);
            takePicture.setVisibility(View.VISIBLE);
            textExtracted.setTextColor(Color.RED);
            textExtracted.setText("Error while getting the user data.. Please make sure that capturing picture without any blur and Please Try Again");
            takePicture.setOnClickListener(this);
        }*/
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.but__verify_details){
            Intent intent = new Intent(Docresult.this, Takepicture.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.but__try_Again_but){
            Intent intent = new Intent(Docresult.this, MainActivity.class);
            startActivity(intent);
        }
    }

}
