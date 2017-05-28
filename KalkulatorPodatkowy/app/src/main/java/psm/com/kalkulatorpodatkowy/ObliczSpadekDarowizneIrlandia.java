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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Created by grzeg on 27.05.2017.
 */

public class ObliczSpadekDarowizneIrlandia extends AppCompatActivity {

    EditText etWprowadzDarowizne;
    Button btnObliczPodatek;
    TextView txtWysokoscPodatku;

    DatabaseReference ref;
    String grupa;
    Double w = 0.0;

    ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spadek_darowizna);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            grupa = bundle.getString("grupa");
        }


        progressBar = (ProgressBar) findViewById(R.id.progressBarSDPl);
        progressBar.setVisibility(View.GONE);
        txtWysokoscPodatku = (TextView) findViewById(R.id.txtWysokoscPodatku);
        etWprowadzDarowizne = (EditText) findViewById(R.id.etWprowadzDarowizne);
        btnObliczPodatek = (Button) findViewById(R.id.btnObliczPodatek);
        btnObliczPodatek.setEnabled(false);
        ref = FirebaseDatabase.getInstance().getReference();

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
                    txtWysokoscPodatku.setText("Podaj wysokośc darowizny i spadku");
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
                progressBar.setVisibility(View.VISIBLE);
                switch (grupa) {
                    case "Grupa A":
                        ref.child("Kraj").child("Irlandia").child("Spadek i darowizna").child(grupa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(Double.parseDouble(etWprowadzDarowizne.getText().toString()) > dataSnapshot.child("Próg").getValue(Integer.class)){
                                    try {
                                        progressBar.setVisibility(View.GONE);
                                        txtWysokoscPodatku.setText("Wysokość podatku do zapłacenia: " +
                                                zaokraglij(oblicz(dataSnapshot.child("Podatek").getValue(String.class), w)));
                                    } catch (EvalError evalError) {
                                        evalError.printStackTrace();
                                    }
                                } else {
                                    txtWysokoscPodatku.setText("Od tej kwoty nie trzeba odprowadzać podatku");
                                    progressBar.setVisibility(View.GONE);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;
                    case "Grupa B":
                        ref.child("Kraj").child("Irlandia").child("Spadek i darowizna").child(grupa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(Double.parseDouble(etWprowadzDarowizne.getText().toString()) > dataSnapshot.child("Próg").getValue(Integer.class)){
                                    try {
                                        progressBar.setVisibility(View.GONE);
                                        txtWysokoscPodatku.setText("Wysokość podatku do zapłacenia: " +
                                                zaokraglij(oblicz(dataSnapshot.child("Podatek").getValue(String.class), w)));
                                    } catch (EvalError evalError) {
                                        evalError.printStackTrace();
                                    }
                                } else {
                                    txtWysokoscPodatku.setText("Od tej kwoty nie trzeba odprowadzać podatku");
                                    progressBar.setVisibility(View.GONE);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;
                    case "Grupa C":
                        ref.child("Kraj").child("Irlandia").child("Spadek i darowizna").child(grupa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(Double.parseDouble(etWprowadzDarowizne.getText().toString()) > dataSnapshot.child("Próg").getValue(Integer.class)){
                                    try {
                                        progressBar.setVisibility(View.GONE);
                                        txtWysokoscPodatku.setText("Wysokość podatku do zapłacenia: " +
                                                zaokraglij(oblicz(dataSnapshot.child("Podatek").getValue(String.class), w)));
                                    } catch (EvalError evalError) {
                                        evalError.printStackTrace();
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    txtWysokoscPodatku.setText("Od tej kwoty nie trzeba odprowadzać podatku");
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;
                }

            }
            private double oblicz(String wzor, double w) throws EvalError {

                Interpreter interpreter = new Interpreter();
                interpreter.set("SPAD",w);
                interpreter.eval("result = " + wzor);

                //  txtWynik.setText(txtWynik.getText()+"\n>" + w + " -- " + wzor);
                return (Double) interpreter.get("result");
            }
        });
    }

    public static String zaokraglij(double value) {
        return String.format("%.2f", value);
    }
}
