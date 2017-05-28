package psm.com.kalkulatorpodatkowy;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
 * Created by grzeg on 27.05.2017.
 */

public class ObliczPodatkiIrlandia extends AppCompatActivity {

    Spinner rodzajKwoty;
    Spinner procent;
    Button btnOblicz;
    EditText etWprowadzKwote;
    TextView txtWynik;
    String[] items;
    DatabaseReference ref;
    String umowa;
    String status;
    String[] itemsProc;
    ProgressBar progressBar;

    double pope = 0;
    double pod = 0;
    double prsi = 0;
    double usc = 0;
    double po = 0;
    double pe = 0;
    double netto = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oblicz_irlandia);

        btnOblicz = (Button) findViewById(R.id.btnObliczIrl);
        txtWynik = (TextView) findViewById(R.id.txtWynikIrl);
        etWprowadzKwote = (EditText) findViewById(R.id.etWysokoscWyplatyIrl);
        rodzajKwoty = (Spinner) findViewById(R.id.spinner_irlandia);
        procent =(Spinner)findViewById(R.id.spinner_procent);
        btnOblicz.setEnabled(false);
        progressBar = (ProgressBar)findViewById(R.id.progressBarIRL);
        progressBar.setVisibility(View.GONE);
        items = new String[]{"Miesięcznie", "Tygodniowo", "Rocznie"};

        ref = FirebaseDatabase.getInstance().getReference();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        rodzajKwoty.setAdapter(adapter);

        itemsProc = new String[]{"% wypłaty na ubezpieczenie rentowne", "1%","2%",
                "3%","4%","5%","6%","7%","8%","9%","10%","11%","12%","13%","14%","15%"};
        ArrayAdapter<String> adapterp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsProc);
        procent.setAdapter(adapterp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            umowa = bundle.getString("umowa");
            status = bundle.getString("status");
        }

        rodzajKwoty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                double wartoscMinimalna = 0;


                if (etWprowadzKwote.getText().length() > 0) {
                    try {
                        if (umowa.equals("Umowa o prace")) {
                            if (rodzajKwoty.getSelectedItem().toString().equals("Tygodniowo")) {
                                wartoscMinimalna = 1035/4.33;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Rocznie")){
                                wartoscMinimalna = 1035*12;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Miesięcznie")){
                                wartoscMinimalna = 1035;
                            }
                        } else {
                            wartoscMinimalna = 1;
                        }
                        if (Double.parseDouble(etWprowadzKwote.getText().toString()) >= wartoscMinimalna) {
                            btnOblicz.setEnabled(true);
                            txtWynik.setText("Kwota jest poprawna.");
                        } else {
                            txtWynik.setText("Ta kwota nie przekracza minimalnej krajowej, która wynosi "+zaokraglij(wartoscMinimalna)+" euro brutto.");
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
                        if (umowa.equals("Umowa o prace")) {
                            if (rodzajKwoty.getSelectedItem().toString().equals("Tygodniowo")) {
                                wartoscMinimalna = 1035/4.33;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Rocznie")){
                                wartoscMinimalna = 1035*12;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Miesięcznie")){
                                wartoscMinimalna = 1035;
                            }
                        } else {
                            wartoscMinimalna = 1;
                        }
                        if (Double.parseDouble(s.toString()) >= wartoscMinimalna) {
                            btnOblicz.setEnabled(true);
                            txtWynik.setText("Kwota jest poprawna.");
                        } else {
                            txtWynik.setText("Ta kwota nie przekracza minimalnej krajowej, która wynosi "+zaokraglij(wartoscMinimalna)+" euro brutto.");
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

                progressBar.setVisibility(View.VISIBLE);
                final String text = rodzajKwoty.getSelectedItem().toString();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                txtWynik.setText("");
                w[0] = Double.parseDouble(etWprowadzKwote.getText().toString());

                if (text.equals("Tygodniowo")) {
                    w[0] = w[0] *4.33;
                } else if (text.equals("Rocznie")){
                    w[0] = w[0] /12;
                }

                switch (umowa) {
                    case "Umowa o prace":
                        switch (status){
                            case "Pojedyńczy":
                                ref.child("Kraj").child("Irlandia").child(umowa).child(status).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        obliczPodatki(dataSnapshot, w[0]);
                                        progressBar.setVisibility(View.GONE);

                                        if (text.equals("Tygodniowo")) {
                                            wyswietlTygodniowo();
                                            wyswietlMiesiecznie();
                                            wyswietlRocznie();
                                        } else if (text.equals("Rocznie")){
                                            wyswietlRocznie();
                                            wyswietlMiesiecznie();
                                            wyswietlTygodniowo();
                                        } else if (text.equals("Miesięcznie")){
                                            wyswietlMiesiecznie();
                                            wyswietlRocznie();
                                            wyswietlTygodniowo();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case "Żonaty I":

                                ref.child("Kraj").child("Irlandia").child(umowa).child(status).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        obliczPodatki(dataSnapshot, w[0]);

                                        progressBar.setVisibility(View.GONE);
                                        if (text.equals("Tygodniowo")) {
                                            wyswietlTygodniowo();
                                            wyswietlMiesiecznie();
                                            wyswietlRocznie();
                                        } else if (text.equals("Rocznie")){
                                            wyswietlRocznie();
                                            wyswietlMiesiecznie();
                                            wyswietlTygodniowo();
                                        } else if (text.equals("Miesięcznie")){
                                            wyswietlMiesiecznie();
                                            wyswietlRocznie();
                                            wyswietlTygodniowo();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case "Żonaty II":
                                ref.child("Kraj").child("Irlandia").child(umowa).child(status).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        obliczPodatki(dataSnapshot, w[0]);

                                        progressBar.setVisibility(View.GONE);
                                        if (text.equals("Tygodniowo")) {
                                            wyswietlTygodniowo();
                                            wyswietlMiesiecznie();
                                            wyswietlRocznie();
                                        } else if (text.equals("Rocznie")){
                                            wyswietlRocznie();
                                            wyswietlMiesiecznie();
                                            wyswietlTygodniowo();
                                        } else if (text.equals("Miesięcznie")){
                                            wyswietlMiesiecznie();
                                            wyswietlRocznie();
                                            wyswietlTygodniowo();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                break;
                        }
                        break;
                }
            }


            private void wyswietlMiesiecznie(){
                txtWynik.setText(txtWynik.getText() + "\nBrutto miesięcznie: " + zaokraglij(w[0]));
                txtWynik.setText(txtWynik.getText() + "\nUbezpiecznie rentowne: " + zaokraglij(pope));
                txtWynik.setText(txtWynik.getText() + "\nPodstawa opodatkowania: " + zaokraglij(po));
                txtWynik.setText(txtWynik.getText() + "\nPodatek PRSI: " + zaokraglij(prsi));
                txtWynik.setText(txtWynik.getText() + "\nPodatek dochodowy: " + zaokraglij(pod));
                txtWynik.setText(txtWynik.getText() + "\nPodatek USC: " + zaokraglij(usc));
                txtWynik.setText(txtWynik.getText() + "\nNetto miesięcznie: " + zaokraglij(netto) +"\n");

            }

            private void wyswietlRocznie(){
                txtWynik.setText(txtWynik.getText() + "\nBrutto rocznie: " + zaokraglij(w[0]*12));
                txtWynik.setText(txtWynik.getText() + "\nUbezpiecznie rentowne: " + zaokraglij(pope*12));
                txtWynik.setText(txtWynik.getText() + "\nPodstawa opodatkowania: " + zaokraglij(po*12));
                txtWynik.setText(txtWynik.getText() + "\nPodatek PRSI: " + zaokraglij(prsi*12));
                txtWynik.setText(txtWynik.getText() + "\nPodatek dochodowy: " + zaokraglij(pod*12));
                txtWynik.setText(txtWynik.getText() + "\nPodatek USC: " + zaokraglij(usc*12));
                txtWynik.setText(txtWynik.getText() + "\nNetto rocznie: " + zaokraglij(netto*12)+"\n");

            }

            private void wyswietlTygodniowo(){
                txtWynik.setText(txtWynik.getText() + "\nBrutto tygodniowo: " + zaokraglij(w[0]/4.33));
                txtWynik.setText(txtWynik.getText() + "\nUbezpiecznie rentowne: " + zaokraglij(pope/4.33));
                txtWynik.setText(txtWynik.getText() + "\nPodstawa opodatkowania: " + zaokraglij(po/4.33));
                txtWynik.setText(txtWynik.getText() + "\nPodatek PRSI: " + zaokraglij(prsi/4.33));
                txtWynik.setText(txtWynik.getText() + "\nPodatek dochodowy: " + zaokraglij(pod/4.33));
                txtWynik.setText(txtWynik.getText() + "\nPodatek USC: " + zaokraglij(usc/4.33));
                txtWynik.setText(txtWynik.getText() + "\nNetto tygodniowo: " + zaokraglij(netto/4.33)+"\n");

            }

            private void obliczPodatki(DataSnapshot dataSnapshot, double w){
                try {
                    if (procent.getSelectedItem().toString().equals("% wypłaty na ubezpieczenie rentowne")){
                        pe = 0;
                    } else {
                        pe = Double.parseDouble(procent.getSelectedItem().toString().replaceAll("%", ""));
                    }

                    pope = oblicz(dataSnapshot.child("Podatek obliczony przez użytkownika").getValue(String.class), w);
                    po = oblicz(dataSnapshot.child("Podstawa opodatkowania").getValue(String.class), w);

                    if (w*12 > dataSnapshot.child("Próg PRSI").getValue(Integer.class)){
                        prsi = oblicz(dataSnapshot.child("PRSI").getValue(String.class), w);
                    } else {
                        prsi = 0;
                    }

                    if (w*12 <= dataSnapshot.child("Próg podatkowy").getValue(Integer.class)){
                        pod = oblicz(dataSnapshot.child("Podatek próg I").getValue(String.class), 0);
                    } else {
                        pod = oblicz(dataSnapshot.child("Podatek próg II").getValue(String.class), 0);
                    }

                    if (w*12<12013){
                        usc = 0;
                    } else if(w*12>=12013 && w*12<18773){
                        usc = (oblicz(dataSnapshot.child("USC I").getValue(String.class), w*12))/12;
                    }else if (w*12>=18773 && w*12<70005){
                        usc = (oblicz(dataSnapshot.child("USC II").getValue(String.class), w*12))/12;
                    } else {
                        usc = (oblicz(dataSnapshot.child("USC III").getValue(String.class), w*12))/12;
                    }

                    netto = oblicz(dataSnapshot.child("Netto").getValue(String.class), w);

                } catch (EvalError evalError) {
                    evalError.printStackTrace();
                }
            }
            private double oblicz(String wzor, double w) throws EvalError {

                Interpreter interpreter = new Interpreter();
                interpreter.set("W", w);
                interpreter.set("PE", pe);
                interpreter.set("POPE", pope);
                interpreter.set("PRSI", prsi);
                interpreter.set("PO", po);
                interpreter.set("POD", pod);
                interpreter.set("USC", usc);
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
