package com.pdmanager.views.patient.cognition.cognitive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pdmanager.R;

/**
 * Main menu to launch the different cognitive tests
 *
 * @authors Quentin DELEPIERRE, Thibaud PACQUETET, Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class CognitiveMenu extends Activity implements View.OnClickListener {
    Button butPALPRM, butPAL, butPRM, butSWM, butSSP, butSST, butAST, butVAS, butVAS2, butWins, butLond;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cognitive_main_menu);

        butPAL = (Button) findViewById(R.id.buttonPairedAssociatesLearning);
        butPRM = (Button) findViewById(R.id.buttonPatternRecognitionMemory);
        butPALPRM = (Button) findViewById(R.id.buttonPALPRM);
        butSWM = (Button) findViewById(R.id.buttonSpatialWorkingMemory);
        butSSP = (Button) findViewById(R.id.buttonSpatialSpan);
        butSST = (Button) findViewById(R.id.buttonStopSignalTask);
        butAST = (Button) findViewById(R.id.buttonAttentionSwitchingTask);
        butVAS = (Button) findViewById(R.id.buttonVisualAnalogueScale);
        butVAS2 = (Button) findViewById(R.id.buttonVisualAnalogueScale2);
        butWins = (Button) findViewById(R.id.buttonWisconsin);
        butLond = (Button) findViewById(R.id.buttonLondon);

        butPAL.setOnClickListener(this);
        butPRM.setOnClickListener(this);
        butPALPRM.setOnClickListener(this);
        butSWM.setOnClickListener(this);
        butSSP.setOnClickListener(this);
        butSST.setOnClickListener(this);
        butAST.setOnClickListener(this);
        butVAS.setOnClickListener(this);
        butVAS2.setOnClickListener(this);
        butWins.setOnClickListener(this);
        butLond.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonPALPRM:
                Intent menuPALPRMIntent =
                        new Intent(CognitiveMenu.this, PALPRM.class);
                startActivity(menuPALPRMIntent);
                finish();
                break;

            case R.id.buttonPairedAssociatesLearning:
                Intent menuPALIntent =
                        new Intent(CognitiveMenu.this, PairedAssociatesLearningTest.class);
                startActivity(menuPALIntent);
                finish();
                break;

            case R.id.buttonPatternRecognitionMemory:
                Intent menuPRMIntent =
                        new Intent(CognitiveMenu.this, PatternRecognitionMemoryTest.class);
                startActivity(menuPRMIntent);
                finish();
                break;

            case R.id.buttonSpatialWorkingMemory:
                Intent menuSWMIntent =
                        new Intent(CognitiveMenu.this, SpatialWorkingMemoryTest.class);
                startActivity(menuSWMIntent);
                finish();
                break;

            case R.id.buttonSpatialSpan:
                Intent menuSSIntent =
                        new Intent(CognitiveMenu.this, SpatialSpanTest.class);
                startActivity(menuSSIntent);
                finish();
                break;

            case R.id.buttonStopSignalTask:
                Intent menuSSTIntent =
                        new Intent(CognitiveMenu.this, StopSignalTaskTest.class);
                startActivity(menuSSTIntent);
                finish();
                break;

            case R.id.buttonAttentionSwitchingTask:
                Intent menuASTIntent =
                        new Intent(CognitiveMenu.this, AttentionSwitchingTaskTest.class);
                startActivity(menuASTIntent);
                finish();
                break;

            case R.id.buttonVisualAnalogueScale:
                Intent menuVASIntent =
                        new Intent(CognitiveMenu.this, VisualAnalogueScaleTest.class);
                startActivity(menuVASIntent);
                finish();
                break;

            case R.id.buttonVisualAnalogueScale2:
                Intent menuVASIntent2 =
                        new Intent(CognitiveMenu.this, VisualAnalogueScaleTest2.class);
                startActivity(menuVASIntent2);
                finish();
                break;

            case R.id.buttonWisconsin:
                Intent intent = new Intent(getApplicationContext(), WisconsinCardSorting.class);
                startActivity(intent);
                finish();
                break;

            case R.id.buttonLondon:
                Intent lndn = new Intent(getApplicationContext(), LondonTowersTest.class);
                startActivity(lndn);
                finish();
                break;

        }

    }
}



