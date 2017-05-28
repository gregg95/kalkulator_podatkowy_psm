package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by grzeg on 25.05.2017.
 */

public class AngliaLata extends AppCompatActivity {

    private ListView listLata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anglia_lata);

        listLata = (ListView)findViewById(R.id.list_lata_anglia);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AngliaLata.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.lata_anglia));

        listLata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = listLata.getItemAtPosition(position).toString();

                switch (name) {
                    case "0 - 3":
                        Intent intent1 = new Intent(AngliaLata.this, ObliczPodatkiAnglia.class);
                        intent1.putExtra("lata", listLata.getItemAtPosition(position).toString());
                        intent1.putExtra("umowa", "Spadek");
                        startActivity(intent1);
                        break;
                    case "3 - 4":
                        Intent intent2 = new Intent(AngliaLata.this, ObliczPodatkiAnglia.class);
                        intent2.putExtra("lata", listLata.getItemAtPosition(position).toString());
                        intent2.putExtra("umowa", "Spadek");
                        startActivity(intent2);
                        break;
                    case "4 - 5":
                        Intent intent3 = new Intent(AngliaLata.this, ObliczPodatkiAnglia.class);
                        intent3.putExtra("lata", listLata.getItemAtPosition(position).toString());
                        intent3.putExtra("umowa", "Spadek");
                        startActivity(intent3);
                        break;
                    case "5 - 6":
                        Intent intent4 = new Intent(AngliaLata.this, ObliczPodatkiAnglia.class);
                        intent4.putExtra("lata", listLata.getItemAtPosition(position).toString());
                        intent4.putExtra("umowa", "Spadek");
                        startActivity(intent4);
                        break;
                    case "6 - 7":
                        Intent intent5 = new Intent(AngliaLata.this, ObliczPodatkiAnglia.class);
                        intent5.putExtra("lata", listLata.getItemAtPosition(position).toString());
                        intent5.putExtra("umowa", "Spadek");
                        startActivity(intent5);
                        break;

                }
            }
        });


        listLata.setAdapter(arrayAdapter);

    }
}
