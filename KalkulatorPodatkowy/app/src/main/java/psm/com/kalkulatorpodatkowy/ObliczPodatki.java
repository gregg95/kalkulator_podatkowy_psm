package psm.com.kalkulatorpodatkowy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;


public class ObliczPodatki extends AppCompatActivity {

    String umowa;
    Button btnOblicz;
    TextView txtWynik;
    EditText etWprowadzKwote;
    Spinner rodzajKwoty;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    String[] items;

    double uz, uc, ur, ue, szdo, po, zpit, netto;

    void setuz(double uz){
        this.uz = uz;
    }

    void setue(double ue){
        this.ue = ue;
    }

    void setuc(double uc){
        this.uc = uc;
    }

    void setur(double ur){
        this.ur = ur;
    }

    void setpo(double po) {
        this.po = po;
    }

    void setszdo(double szdo) {
        this.szdo = szdo;
    }

    void setpit(double zpit){
        this.zpit = zpit;
    }

    void setnetto(double netto){
        this.netto = netto;
    }

    double getzpit(){
        return zpit;
    }

    double getpo(){
        return po;
    }

    double getnetto(){
        return netto;
    }

    double getuz(){
        return uz;
    }

    double getue(){
        return ue;
    }

    double getuc(){
        return uc;
    }

    double getur(){
        return ur;
    }

    double getszdo() {
        return szdo;
    }


    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oblicz_podatki);

        ref = FirebaseDatabase.getInstance().getReference();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            umowa = bundle.getString("umowaO");
        }



        btnOblicz = (Button) findViewById(R.id.btnOblicz);
        btnOblicz.setEnabled(false);
        items = new String[]{"Miesięcznie", "Tygodniowo", "Rocznie"};
        txtWynik = (TextView) findViewById(R.id.txtWynik);
        etWprowadzKwote = (EditText) findViewById(R.id.etWprowadzKwote);
        rodzajKwoty = (Spinner) findViewById(R.id.spinnerRodzajeKwot);
        progressBar = (ProgressBar)findViewById(R.id.progressBarPl);
        progressBar.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        rodzajKwoty.setAdapter(adapter);


        rodzajKwoty.setEnabled(false);
        if (umowa.equals("Prace")){
            rodzajKwoty.setEnabled(true);
        }

        rodzajKwoty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                double wartoscMinimalna = 0;

                if(etWprowadzKwote.getText().length()>0) {
                    try {
                        if (umowa.equals("Prace")){
                            if (rodzajKwoty.getSelectedItem().toString().equals("Tygodniowo")) {
                                wartoscMinimalna = 2000/4.33;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Rocznie")){
                                wartoscMinimalna = 2000*12;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Miesięcznie")){
                                wartoscMinimalna = 2000;
                            }
                        } else {
                            wartoscMinimalna=1;
                        }
                        if (Double.parseDouble(etWprowadzKwote.getText().toString()) >= wartoscMinimalna) {
                            btnOblicz.setEnabled(true);
                            txtWynik.setText("Kwota jest poprawna.");
                        } else {
                            txtWynik.setText("Ta kwota nie przekracza minimalnej krajowej, która wynosi "+zaokraglij(wartoscMinimalna)+"zł");
                            btnOblicz.setEnabled(false);
                        }
                    }catch(NumberFormatException e){
                        txtWynik.setText("Podana wartość nie jest liczbą");
                        btnOblicz.setEnabled(false);
                    }
                } else {
                    etWprowadzKwote.setText("");
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

                if(s.length()>0) {
                    try {
                        if (umowa.equals("Prace")){
                            if (rodzajKwoty.getSelectedItem().toString().equals("Tygodniowo")) {
                                wartoscMinimalna = 2000/4.33;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Rocznie")){
                                wartoscMinimalna = 2000*12;
                            } else if (rodzajKwoty.getSelectedItem().toString().equals("Miesięcznie")){
                                wartoscMinimalna = 2000;
                            }
                        } else {
                            wartoscMinimalna=1;
                        }
                        if (Double.parseDouble(s.toString()) >= wartoscMinimalna) {
                            btnOblicz.setEnabled(true);
                            txtWynik.setText("Kwota jest poprawna.");
                        } else {
                            txtWynik.setText("Ta kwota nie przekracza minimalnej krajowej, która wynosi "+zaokraglij(wartoscMinimalna)+"zł");
                            btnOblicz.setEnabled(false);
                        }
                    }catch(NumberFormatException e){
                        txtWynik.setText("Podana wartość nie jest liczbą");
                        btnOblicz.setEnabled(false);
                    }
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

               /* progressDialog=new ProgressDialog(ObliczPodatki.this);
                progressDialog.setMessage("Obliczam...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
*/
               progressBar.setVisibility(View.VISIBLE);
                w[0] =Double.parseDouble(etWprowadzKwote.getText().toString());
                txtWynik.setText("");
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                switch (umowa){

                    case "Prace":

                        final String text = rodzajKwoty.getSelectedItem().toString();
                        if (text.equals("Tygodniowo")) {
                            w[0] = w[0] *4.33;
                        } else if (text.equals("Rocznie")){
                            w[0] = w[0] /12;
                        }


                        if (w[0] * 12 > 85528) {
                            ref.child("Kraj").child("Polska").child("Umowa o ").child(umowa).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        setue(oblicz(dataSnapshot.child("Ubezpieczenie emerytalne").getValue(String.class), w[0]));
                                        setur(oblicz(dataSnapshot.child("Ubezpieczenie rentowe").getValue(String.class), w[0]));
                                        setuz(oblicz(dataSnapshot.child("Ubezpieczenie zdrowotne").getValue(String.class), w[0]));
                                        setuc(oblicz(dataSnapshot.child("Ubezpieczenie chorobowe").getValue(String.class), w[0]));
                                        setszdo(oblicz(dataSnapshot.child("Składka zdrowotna do odliczenia").getValue(String.class), w[0]));
                                        setpo(oblicz(dataSnapshot.child("Podstawa opodatkowania(do pełnych złotych)").getValue(String.class), w[0]));
                                        setpit(oblicz(dataSnapshot.child("Zaliczka na pit (Do pełnych złotych) od 85 528 zł").getValue(String.class), w[0]));
                                        setnetto(oblicz(dataSnapshot.child("Netto").getValue(String.class), w[0]));

                                    } catch (EvalError evalError) {
                                        evalError.printStackTrace();
                                    }

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

                        } else {
                            ref.child("Kraj").child("Polska").child("Umowa o ").child(umowa).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        setue(oblicz(dataSnapshot.child("Ubezpieczenie emerytalne").getValue(String.class), w[0]));
                                        setur(oblicz(dataSnapshot.child("Ubezpieczenie rentowe").getValue(String.class), w[0]));
                                        setuz(oblicz(dataSnapshot.child("Ubezpieczenie zdrowotne").getValue(String.class), w[0]));
                                        setuc(oblicz(dataSnapshot.child("Ubezpieczenie chorobowe").getValue(String.class), w[0]));
                                        setszdo(oblicz(dataSnapshot.child("Składka zdrowotna do odliczenia").getValue(String.class), w[0]));
                                        setpo(oblicz(dataSnapshot.child("Podstawa opodatkowania(do pełnych złotych)").getValue(String.class), w[0]));
                                        setpit(oblicz(dataSnapshot.child("Zaliczka na pit (Do pełnych złotych) do 85 528 zł").getValue(String.class), w[0]));
                                        setnetto(oblicz(dataSnapshot.child("Netto").getValue(String.class), w[0]));
                                    } catch (EvalError evalError) {
                                        evalError.printStackTrace();
                                    }


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

                        }

                        break;
                    case "Dzieło":
                        ref.child("Kraj").child("Polska").child("Umowa o ").child(umowa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    setpit(oblicz(dataSnapshot.child("Zaliczka na PIT").getValue(String.class), w[0]));
                                    setnetto(oblicz(dataSnapshot.child("Netto").getValue(String.class), w[0]));
                                } catch (EvalError evalError) {
                                    evalError.printStackTrace();
                                }

                                progressBar.setVisibility(View.GONE);

                                txtWynik.setText("Brutto: " + zaokraglij(w[0]) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Zaliczka na PIT: " + zaokraglij(getzpit()) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Netto: " + zaokraglij(getnetto()));


                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;
                    case "Zlecenie z dorosłym":
                        ref.child("Kraj").child("Polska").child("Umowa o ").child(umowa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    setue(oblicz(dataSnapshot.child("Ubezpieczenie emerytalne").getValue(String.class), w[0]));
                                    setur(oblicz(dataSnapshot.child("Ubezpieczenie rentowe").getValue(String.class), w[0]));
                                    setuz(oblicz(dataSnapshot.child("Ubezpieczenie zdrowotne").getValue(String.class), w[0]));
                                    setpit(oblicz(dataSnapshot.child("Zaliczka na PIT").getValue(String.class), w[0]));
                                    setnetto(oblicz(dataSnapshot.child("Netto").getValue(String.class), w[0]));
                                } catch (EvalError evalError) {
                                    evalError.printStackTrace();
                                }

                                progressBar.setVisibility(View.GONE);

                                txtWynik.setText("Brutto: " + zaokraglij(w[0]) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie emerytalne: " + zaokraglij(getue()) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie rentowe: " + zaokraglij(getur()) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie zdrowotne: " + zaokraglij(getuz()) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Zaliczka na PIT: " + zaokraglij(getzpit()) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Netto: " + zaokraglij(getnetto()));

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        break;
                    case "Zlecenie ze studentem":
                        ref.child("Kraj").child("Polska").child("Umowa o ").child(umowa).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    setpit(oblicz(dataSnapshot.child("Zaliczka na PIT").getValue(String.class), w[0]));
                                    setnetto(oblicz(dataSnapshot.child("Netto").getValue(String.class), w[0]));
                                } catch (EvalError evalError) {
                                    evalError.printStackTrace();
                                }

                                progressBar.setVisibility(View.GONE);

                                txtWynik.setText("Brutto: " + zaokraglij(w[0]) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Zaliczka na PIT: " + zaokraglij(getzpit()) + "\n");
                                txtWynik.setText(txtWynik.getText() + "Netto: " + zaokraglij(getnetto()));

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        break;

                }
            }

            private void wyswietlTygodniowo(){
                txtWynik.setText(txtWynik.getText() + "\nBrutto tygodniowo: " + zaokraglij(w[0]/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie emerytalne: " + zaokraglij(getue()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie rentowe: " + zaokraglij(getur()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie zdrowotne: " + zaokraglij(getuz()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie chorobowe: " + zaokraglij(getuc()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Składka zdrowotna: " + zaokraglij(getszdo()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Podstawa opodatkowania: " + zaokraglij(getpo()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Zaliczka na PIT: " + zaokraglij(getzpit()/4.33) + "\n");
                txtWynik.setText(txtWynik.getText() + "Netto tygodniowo: " + zaokraglij(getnetto()/4.33) + "\n");
            }

            private void wyswietlMiesiecznie(){
                txtWynik.setText(txtWynik.getText() + "\nBrutto miesięcznie: " + zaokraglij(w[0]) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie emerytalne: " + zaokraglij(getue()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie rentowe: " + zaokraglij(getur()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie zdrowotne: " + zaokraglij(getuz()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie chorobowe: " + zaokraglij(getuc()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Składka zdrowotna: " + zaokraglij(getszdo()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Podstawa opodatkowania: " + zaokraglij(getpo()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Zaliczka na PIT: " + zaokraglij(getzpit()) + "\n");
                txtWynik.setText(txtWynik.getText() + "Netto miesięcznie: " + zaokraglij(getnetto()) + "\n");
            }

            private void wyswietlRocznie(){
                txtWynik.setText(txtWynik.getText() + "\nBrutto rocznie: " + zaokraglij(w[0]*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie emerytalne: " + zaokraglij(getue()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie rentowe: " + zaokraglij(getur()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie zdrowotne: " + zaokraglij(getuz()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Ubezpieczenie chorobowe: " + zaokraglij(getuc()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Składka zdrowotna: " + zaokraglij(getszdo()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Podstawa opodatkowania: " + zaokraglij(getpo()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Zaliczka na PIT: " + zaokraglij(getzpit()*12) + "\n");
                txtWynik.setText(txtWynik.getText() + "Netto rocznie: " + zaokraglij(getnetto()*12) + "\n");

            }


            private double oblicz(String wzor, double w) throws EvalError {

                Interpreter interpreter = new Interpreter();
                interpreter.set("W", w);
                interpreter.set("UE", getue());
                interpreter.set("UR", getur());
                interpreter.set("UC", getuc());
                interpreter.set("UZ", getuz());
                interpreter.set("PO", getpo());
                interpreter.set("SZDO", getszdo());
                interpreter.set("ZPIT", getzpit());
                interpreter.eval("result = " + wzor);

                return (Double) interpreter.get("result");
            }


        });


    }

    public static String zaokraglij(double value) {
        return String.format("%.2f", value);
    }

}
