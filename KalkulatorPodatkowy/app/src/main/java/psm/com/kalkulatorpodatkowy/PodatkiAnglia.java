package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by grzeg on 25.05.2017.
 */

public class PodatkiAnglia extends AppCompatActivity {

    Button btnSpadek;
    Button btnUmowaOPrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anglia);

        btnSpadek = (Button)findViewById(R.id.btnSpadekAnglia);
        btnUmowaOPrace = (Button) findViewById(R.id.btnUmowaOPraceAnglia);

        btnSpadek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PodatkiAnglia.this, AngliaLata.class);
                startActivity(intent1);
            }
        });

        btnUmowaOPrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(PodatkiAnglia.this, ObliczPodatkiAnglia.class);
                intent2.putExtra("umowa", "Umowa o pracę");
                startActivity(intent2);
            }
        });


    }
}
