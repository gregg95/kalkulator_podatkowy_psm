package psm.com.kalkulatorpodatkowy;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by grzeg on 22.05.2017.
 */

public class WybierzKraj extends AppCompatActivity {

    private ListView listKraje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wybierz_kraj);

        listKraje = (ListView)findViewById(R.id.listKraje);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WybierzKraj.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.kraje));

        listKraje.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = listKraje.getItemAtPosition(position).toString();

                switch (name) {
                    case "Polska":
                        Intent intent = new Intent(WybierzKraj.this, PodatkiPolska.class);
                        startActivity(intent);
                        break;
                    case "Anglia":
                        Intent intent1 = new Intent(WybierzKraj.this, PodatkiAnglia.class);
                        startActivity(intent1);
                        break;
                    case "Irlandia":
                        Intent intent2 = new Intent(WybierzKraj.this, PodatkiIrlandia.class);
                        startActivity(intent2);
                        break;
                }


            }
        });

        listKraje.setAdapter(arrayAdapter);

    }
}
