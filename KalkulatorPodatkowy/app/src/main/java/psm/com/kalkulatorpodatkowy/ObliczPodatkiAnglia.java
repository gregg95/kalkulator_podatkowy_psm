package psm.com.kalkulatorpodatkowy;

import android.content.Context;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Created by grzeg on 25.05.2017.
 */

public class ObliczPodatkiAnglia extends AppCompatActivity {

    Button btnOblicz;
    TextView txtWynik;
    EditText etWprowadzKwote;

    DatabaseReference ref;

    Spinner rodzajKwoty;
    String umowa;
    String lata;
    double wt, po, stopa, netto, spol, ul;

    String[] items;

    void setSpol(double s) {

        this.spol = s;
    }
    void setNetto(double nt) {
        this.netto = nt;

    }
    void setStopa(double st) {
        this.stopa = st;
    }

    void setWT(double wt) {
        this.wt = wt;
    }

    void setPO(double po) {
        this.po = po;
    }

    void setUL(double ul) {
        this.ul = ul;
    }

    double getUL() {
        return ul;
    }
    double getStopa() {
        return stopa;
    }
    double getWT() {
        return wt;
    }

    double getPO() {
        return po;
    }
    double getNetto() {
        return netto;
    }
    double getSpol() {
        return spol;
    }

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oblicz_podatki);

        btnOblicz = (Button) findViewById(R.id.btnOblicz);
        txtWynik = (TextView) findViewById(R.id.txtWynik);
        etWprowadzKwote = (EditText) findViewById(R.id.etWprowadzKwote);
        rodzajKwoty = (Spinner) findViewById(R.id.spinnerRodzajeKwot);
        btnOblicz.setEnabled(false);
        progressBar = (ProgressBar)findViewById(R.id.progressBarPl);
        progressBar.setVisibility(View.GONE);

        items = new String[]{"Miesięcznie", "Tygodniowo", "Rocznie"};

        ref = FirebaseDatabase.getInstance().getReference();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        rodzajKwoty.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            umowa = bundle.getString("umowa");
            lata = bundle.getString("lata");

        }

        rodzajKwoty.setEnabled(false);
        if (umowa.equals("Umowa o pracę")){
            rodzajKwoty.setEnabled(true);
        }

        rodzajKwoty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                double wartoscMinimalna = 0;


                if (etWprowadzKwote.getText().length() > 0) {
                    try {
                        if (umowa.equals("Umowa o pracę")) {
                            if (rodzajKwoty.getSelectedItem().toString().equals("Tygodniowo")) {
                                wartoscMinimalna = 1299/4.33;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Rocznie")){
                                wartoscMinimalna = 1299*12;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Miesięcznie")){
                                wartoscMinimalna = 1299;
                            }
                        } else {
                            wartoscMinimalna = 1;
                        }
                        if (Double.parseDouble(etWprowadzKwote.getText().toString()) >= wartoscMinimalna) {
                            btnOblicz.setEnabled(true);
                            txtWynik.setText("Kwota jest poprawna.");
                        } else {
                            txtWynik.setText("Ta kwota nie przekracza minimalnej krajowej, która wynosi "+zaokraglij(wartoscMinimalna)+" funtów brutto.");
                            btnOblicz.setEnabled(false);
                        }
                    } catch (NumberFormatException e) {
                        txtWynik.setText("Podana wartość nie jest liczbą");
                        btnOblicz.setEnabled(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        etWprowadzKwote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                double wartoscMinimalna = 0;


                if (s.length() > 0) {
                    try {
                        if (umowa.equals("Umowa o pracę")) {
                            if (rodzajKwoty.getSelectedItem().toString().equals("Tygodniowo")) {
                                wartoscMinimalna = 1299/4.33;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Rocznie")){
                                wartoscMinimalna = 1299*12;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Miesięcznie")){
                                wartoscMinimalna = 1299;
                            }
                        } else {
                            wartoscMinimalna = 1;
                        }
                        if (Double.parseDouble(s.toString()) >= wartoscMinimalna) {
                            btnOblicz.setEnabled(true);
                            txtWynik.setText("Kwota jest poprawna.");
                        } else {
                            txtWynik.setText("Ta kwota nie przekracza minimalnej krajowej, która wynosi "+zaokraglij(wartoscMinimalna)+" funtów brutto.");
                            btnOblicz.setEnabled(false);
                        }
                    } catch (NumberFormatException e) {
                        txtWynik.setText("Podana wartość nie jest liczbą");
                        btnOblicz.setEnabled(false);
                    }
                } else {
                    etWprowadzKwote.setText(" ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final double[] w = new double[1];

        btnOblicz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                txtWynik.setText("");
                w[0] = Double.parseDouble(etWprowadzKwote.getText().toString());

                progressBar.setVisibility(View.VISIBLE);
                switch (umowa) {
                    case "Umowa o pracę":
                        ref.child("Kraj").child("Anglia").child("Umowa o pracę").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String text = rodzajKwoty.getSelectedItem().toString();

                                        if (text.equals("Tygodniowo")) {
                                            w[0] = w[0] *4.33;
                                        } else if (text.equals("Rocznie")){
                                            w[0] = w[0]/12;
                                        }



                                        try {
                                            setWT(oblicz(dataSnapshot.child("Wynagrodzenie tygodniowe brutto").getValue(String.class), w[0]));
                                            setPO(oblicz(dataSnapshot.child("Podstawa opodatkowania").getValue(String.class), w[0]));
                                            if (w[0] * 12 <= 31785) {
                                                setStopa(oblicz(dataSnapshot.child("Dochodowy 1").getValue(String.class), 0));
                                            } else if (w[0] * 12 > 31785 && w[0] * 12 <= 150000) {
                                                setStopa(oblicz(dataSnapshot.child("Dochodowy 2").getValue(String.class), 0));
                                            } else {
                                                setStopa(oblicz(dataSnapshot.child("Dochodowy 3").getValue(String.class), 0));
                                            }

                                            if (getWT() > 155.00 && getWT() <= 827.00) {
                                                setSpol(oblicz(dataSnapshot.child("Społeczne 1").getValue(String.class), 0));
                                            } else if (getWT() > 827.00) {
                                                setSpol(oblicz(dataSnapshot.child("Społeczne 2").getValue(String.class), 0));
                                            }

                                            setNetto(oblicz(dataSnapshot.child("Netto").getValue(String.class), w[0]));

                                        } catch (EvalError evalError) {
                                            evalError.printStackTrace();
                                        }

                                        progressBar.setVisibility(View.GONE);

                                        if (text.equals("Tygodniowo")) {
                                            wyswietlTygodniowe();
                                            wyswietlMiesieczne();
                                            wyswietlRoczne();
                                        } else if (text.equals("Rocznie")){
                                            wyswietlRoczne();
                                            wyswietlMiesieczne();
                                            wyswietlTygodniowe();
                                        } else if (text.equals("Miesięcznie")){
                                            wyswietlMiesieczne();
                                            wyswietlRoczne();
                                            wyswietlTygodniowe();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        break;
                    case "Spadek":
                        ref.child("Kraj").child("Anglia").child(umowa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                setUL(dataSnapshot.child(lata).getValue(Double.class));

                                if (w[0] < 325000) {
                                    progressBar.setVisibility(View.GONE);
                                    txtWynik.setText("Ta kwota jest wolna od odprowadzania podatku");
                                } else {
                                    try {
                                        double p = oblicz(dataSnapshot.child("Podatek od spadku").getValue(String.class), w[0]);

                                        progressBar.setVisibility(View.GONE);
                                        txtWynik.setText("Podatek od tej kwoty wynosi: " + p);

                                    } catch (EvalError evalError) {
                                        evalError.printStackTrace();
                                    }
                                }


                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }

            private void wyswietlRoczne(){
                txtWynik.setText(txtWynik.getText() +"\nBrutto rocznie: " + zaokraglij(w[0] *12) + "\n");
                txtWynik.setText(txtWynik.getText() +"Podstawa opodatkowania: " + zaokraglij(getPO()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Podatek dochodowy: " + zaokraglij(getStopa()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie społeczne: " + zaokraglij((getSpol() * 4.33)*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Netto rocznnie: " + zaokraglij(getNetto()*12) + "\n");
            }

            private void wyswietlMiesieczne(){
                txtWynik.setText(txtWynik.getText() +"\nNetto miesiesięcznie: " + zaokraglij(w[0]) + "\n");
                txtWynik.setText(txtWynik.getText() +"Podstawa opodatkowania: " + zaokraglij(getPO()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Podatek dochodowy: " + zaokraglij(getStopa()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie społeczne: " + zaokraglij(getSpol() * 4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Netto mieszięcznie: " + zaokraglij(getNetto()) + "\n");
            }

            private void wyswietlTygodniowe(){
                txtWynik.setText(txtWynik.getText() +"\nBrutto tygodniowo: " + zaokraglij(getWT()) + "\n");
                txtWynik.setText(txtWynik.getText() +"Podstawa opodatkowania: " + zaokraglij(getPO()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Podatek dochodowy: " + zaokraglij(getStopa()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie społeczne: " + zaokraglij(getSpol()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Netto tygodniowo: " + zaokraglij(getNetto()/4.33) + "\n");
            }

            private double oblicz(String wzor, double w) throws EvalError {

                Interpreter interpreter = new Interpreter();
                interpreter.set("W", w);
                interpreter.set("WT", getWT());
                interpreter.set("PO", getPO());
                interpreter.set("Stopa", getStopa());
                interpreter.set("Społeczne", getSpol());
                interpreter.set("UL", getUL());
                interpreter.eval("result = " + wzor);

                return (Double) interpreter.get("result");
            }
        });
    }

    public static String zaokraglij(double value) {
        return String.format("%.2f", value);
    }
}