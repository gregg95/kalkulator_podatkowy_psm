package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by grzeg on 27.05.2017.
 */

public class StatusIrlandia extends AppCompatActivity {

    Button btnSingiel;
    Button btnZwiazek1;
    Button btnZwiazek2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_irlandia);

        btnSingiel = (Button) findViewById(R.id.btnSingleIrl);
        btnZwiazek1 = (Button) findViewById(R.id.btnZwiazek1);
        btnZwiazek2 = (Button) findViewById(R.id.btnZwiazek2);

        btnSingiel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusIrlandia.this, ObliczPodatkiIrlandia.class);
                intent.putExtra("umowa", "Umowa o prace");
                intent.putExtra("status", "Pojedyńczy");
                startActivity(intent);
            }
        });

        btnZwiazek1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusIrlandia.this, ObliczPodatkiIrlandia.class);
                intent.putExtra("umowa", "Umowa o prace");
                intent.putExtra("status", "Żonaty I");
                startActivity(intent);

            }
        });

        btnZwiazek2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusIrlandia.this, ObliczPodatkiIrlandia.class);
                intent.putExtra("umowa", "Umowa o prace");
                intent.putExtra("status", "Żonaty II");
                startActivity(intent);

            }
        });

    }
}
