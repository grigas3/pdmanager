package com.pdmanager.views.common;

/**
 * Main menu, starting point of the application.
 * <p>
 * It shows two different buttons to launch the cognitive test or the finger tapping tests
 * <p>
 * Main menu to launch the different cognitive tests
 *
 * @authors Quentin DELEPIERRE, Thibaud PACQUETET, Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pdmanager.R;

public class TermsAndConds extends AppCompatActivity implements View.OnClickListener {

    Button buttonBack;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //speak.shutdown();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandconditions);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        TextView test1 = (TextView) findViewById(R.id.termsAndCondText);
        Spanned spanned = Html.fromHtml(getString(R.string.termsandconditionstext));
        test1.setText(spanned);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        finish();

    }


}


