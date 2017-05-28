package psm.com.kalkulatorpodatkowy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Created by grzeg on 23.05.2017.
 */

public class ObliczPodatekOdSpadkuDarowizny extends AppCompatActivity {

    EditText etWprowadzDarowizne;
    Button btnObliczPodatek;
    TextView txtWysokoscPodatku;

    DatabaseReference ref;
    String grupa;
    String przedzial;
    double w;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spadek_darowizna);

        ref = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar)findViewById(R.id.progressBarSDPl);
        progressBar.setVisibility(View.GONE);
        txtWysokoscPodatku = (TextView) findViewById(R.id.txtWysokoscPodatku);
        etWprowadzDarowizne = (EditText) findViewById(R.id.etWprowadzDarowizne);
        btnObliczPodatek = (Button) findViewById(R.id.btnObliczPodatek);
        btnObliczPodatek.setEnabled(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            grupa = bundle.getString("grupa");
        }


        etWprowadzDarowizne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int wartoscMinimalna = 1;

                if(s.length()>0) {
                    try {

                        if (Double.parseDouble(s.toString()) >= wartoscMinimalna) {
                            btnObliczPodatek.setEnabled(true);
                            txtWysokoscPodatku.setText("Kwota jest poprawna.");
                        }
                    }catch(NumberFormatException e){
                        txtWysokoscPodatku.setText("Podana wartość nie jest poprawna");
                        btnObliczPodatek.setEnabled(false);
                    }
                } else {
                    txtWysokoscPodatku.setText("Podaj wysokość darowizny i spadku");
                    btnObliczPodatek.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnObliczPodatek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                w = Double.parseDouble(etWprowadzDarowizne.getText().toString());
                txtWysokoscPodatku.setText("");

                switch (grupa){
                    case "I Grupa":
                        sprawdzPrzedzialGrI(w);

                        break;
                    case "II Grupa":
                        sprawdzPrzedzialGrII(w);
                        break;
                    case "III Grupa":
                        sprawdzPrzedzialGrIII(w);
                        break;

                }

            }

            private void obliczPodatek(String grupa, String przedzial, final double w){

                progressBar.setVisibility(View.VISIBLE);
                ref.child("Kraj").child("Polska").child("Spadek i darowizna").child(grupa).child(przedzial).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String wzor = dataSnapshot.getValue(String.class);
                        Toast.makeText(ObliczPodatekOdSpadkuDarowizny.this, wzor,
                                Toast.LENGTH_LONG).show();
                        try {
                            Interpreter interpreter = new Interpreter();
                            interpreter.set("W", w);
                            interpreter.eval("result = " + wzor);
                            String result = String.format("%.2f", interpreter.get("result"));
                            txtWysokoscPodatku.setText("Wysokość podatku do odprowadzenia: " + result);
                            progressBar.setVisibility(View.GONE);
                        } catch (EvalError evalError) {
                            evalError.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            private void sprawdzPrzedzialGrI(double w){
                if (w < 9637){
                    txtWysokoscPodatku.setText("Od tej kwoty nie trzeba odprowadzać podatku");
                } else if (w>=9637 && w < 10278){
                    obliczPodatek("I Grupa", "Od 9 637 zł do 10 278 zł", w);
                } else if (w>=10278 && w < 20556){
                    obliczPodatek("I Grupa", "Od 10 278 zł do 20 556 zł", w);
                } else if (w >= 20556){
                    obliczPodatek("I Grupa", "Od 20 556 zł", w);
                }
            }

            private void sprawdzPrzedzialGrII(double w){
                if (w < 7276){
                    txtWysokoscPodatku.setText("Od tej kwoty nie trzeba odprowadzać podatku");
                } else if (w>=7276 && w < 10278){
                    obliczPodatek("II Grupa", "Od 7 276 zł do 10 278 zł", w);
                } else if (w>=10278 && w < 20556){
                    obliczPodatek("II Grupa", "Od 10 278 zł do 20 556 zł", w);
                } else if (w >= 20556){
                    obliczPodatek("II Grupa", "Od 20 556 zł", w);
                }
            }

            private void sprawdzPrzedzialGrIII(double w){
                if (w < 4902){
                    txtWysokoscPodatku.setText("Od tej kwoty nie trzeba odprowadzać podatku");
                } else if (w>=4902 && w < 10278){
                    obliczPodatek("III Grupa", "Od 4 902 zł do 10 278 zł", w);
                } else if (w>=10278 && w < 20556){
                    obliczPodatek("III Grupa", "Od 10 278 zł do 20 556 zł", w);
                } else if (w >= 20556){
                    obliczPodatek("III Grupa", "Od 20 556 zł", w);
                }
            }
        });

    }
}
