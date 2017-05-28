package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by grzeg on 22.05.2017.
 */

public class PodatkiPolska extends AppCompatActivity {

    private String nazwaKraju;
    private Button btnUmowaOPrace;
    private Button btnUmowaODzielo;
    private Button btnUmowaZlecenie;
    Button btnSpadekDarowizna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polska);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
           nazwaKraju =  bundle.getString("nazwaKraju");
        }

        btnUmowaOPrace = (Button)findViewById(R.id.btnUmowaOPrace);
        btnUmowaODzielo = (Button) findViewById(R.id.btnUmowaODzielo);
        btnUmowaZlecenie = (Button) findViewById(R.id.btnUmowaZlecenie);
        btnSpadekDarowizna = (Button) findViewById(R.id.btnSpadekDarowizna);

        btnUmowaOPrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PodatkiPolska.this, ObliczPodatki.class);
                intent.putExtra("umowaO", "Prace");
                startActivity(intent);
            }
        });

        btnUmowaZlecenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PodatkiPolska.this, WybierzStatus.class);
                startActivity(intent);
            }
        });

        btnUmowaODzielo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PodatkiPolska.this, ObliczPodatki.class);
                intent.putExtra("umowaO", "Dzieło");
                startActivity(intent);
            }
        });

        btnSpadekDarowizna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PodatkiPolska.this, WybierzGrupe.class);
                startActivity(intent);
            }
        });

    }


}
