package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by grzeg on 27.05.2017.
 */

public class PodatkiIrlandia extends AppCompatActivity {

    Button btnUmowaOPrace;
    Button btnSpadekDarowizna;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podatki_irlandia);

        btnUmowaOPrace = (Button) findViewById(R.id.btnUmowaOPraceIrl);

        btnUmowaOPrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PodatkiIrlandia.this, StatusIrlandia.class);
                startActivity(intent);
            }
        });

        btnSpadekDarowizna = (Button) findViewById(R.id.btnSpadekIrlandia);

        btnSpadekDarowizna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PodatkiIrlandia.this, WybierzGrupeIrlandia.class);
                startActivity(intent);

            }
        });
    }
}
