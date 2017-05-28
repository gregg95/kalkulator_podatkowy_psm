package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by grzeg on 23.05.2017.
 */

public class WybierzGrupe extends AppCompatActivity {

    Button btnGrupaI;
    Button btnGrupaII;
    Button btnGrupaIII;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wybierz_grupe);

        btnGrupaI = (Button) findViewById(R.id.btnGrupaI);
        btnGrupaII = (Button) findViewById(R.id.btnGrupaII);
        btnGrupaIII = (Button) findViewById(R.id.btnGrupaIII);

        btnGrupaI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WybierzGrupe.this, ObliczPodatekOdSpadkuDarowizny.class);
                intent.putExtra("grupa", "I Grupa");
                startActivity(intent);

            }
        });

        btnGrupaII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WybierzGrupe.this, ObliczPodatekOdSpadkuDarowizny.class);
                intent.putExtra("grupa", "II Grupa");
                startActivity(intent);
            }
        });

        btnGrupaIII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WybierzGrupe.this, ObliczPodatekOdSpadkuDarowizny.class);
                intent.putExtra("grupa", "III Grupa");
                startActivity(intent);
            }
        });
    }
}
