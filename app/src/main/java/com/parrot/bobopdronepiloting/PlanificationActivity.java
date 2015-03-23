package com.parrot.bobopdronepiloting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.bebopdronepiloting.R;

import java.util.ArrayList;
import java.util.List;

public class PlanificationActivity extends Activity implements AdapterView.OnItemSelectedListener
{
    private static String TAG = PilotingActivity.class.getSimpleName();
    public static String COMMAND_LIST = "planificationActivity.extra.device.command";
    public ARDiscoveryDeviceService service;

    private LinearLayout Layoutprincipal = null;
    // private TextView unite;
    private String valeurs;
    private EditText duree;

    private static String[] items={"Monter", "Descendre", "Avancer", "Reculer", "Tourner à gauche",
            "Tourner à droite"};
    private List Actions = new ArrayList();
    private List Parametres = new ArrayList();

    @Override
    public void onCreate(Bundle bnd) {
        super.onCreate(bnd);
        setContentView(R.layout.activity_planification);
        duree=(EditText)findViewById(R.id.editText2);
        Spinner spin=(Spinner)findViewById(R.id.spinner);

        spin.setFocusable(true);
        spin.setFocusableInTouchMode(true);
        spin.requestFocus();
        spin.setOnItemSelectedListener(this);
        ArrayAdapter ang=new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        ang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(ang);

        Intent intent = getIntent();
        service = intent.getParcelableExtra(PilotingActivity.EXTRA_DEVICE_SERVICE);
    }

    public void onItemSelected(AdapterView parent,View v, int position, long id) {
        valeurs=items[position];
        /*unite=(TextView)findViewById(R.id.unite);
        if ((items[position]=="Tourner à gauche")||(items[position]=="Tourner à droite"))
        {
            unite.setText("°");
        }
        else
        {
            unite.setText("m");
        }*/
    }

    public void onNothingSelected(AdapterView parent) {
    }

    public void Ajoutcomp(View v){

        Actions.add(valeurs);
        Parametres.add(String.valueOf(duree.getText()));
        affichercommandes(Actions,Parametres,v);
    }

    public void Supprcomp(View v){
        if ((Actions.size()>0)||(Parametres.size()>0)){
            Actions.remove(Actions.size()-1);
            Parametres.remove(Parametres.size()-1);
            affichercommandes(Actions,Parametres,v);
        }
    }

    public void affichercommandes(List commandes,List valeurs, View v){
        Layoutprincipal = (LinearLayout) findViewById(R.id.Page);
        Layoutprincipal.removeAllViewsInLayout();
        for (int i=0;i<(commandes.size());i++){
            LinearLayout ll = new LinearLayout(this);
            Layoutprincipal.addView(ll);
            TextView textView1 = new TextView(this); // Création d'un TextView
            textView1.setText((String)commandes.get(i)); // Attache le texte
            TextView textView2 = new TextView(this);
            textView2.setText("   ");
            TextView textView3 = new TextView(this);
            textView3.setText((String)valeurs.get(i));
            TextView textView4 = new TextView(this);
            if ((commandes.get(i)=="Tourner à gauche")||(commandes.get(i)=="Tourner à droite"))
            {
                textView4.setText("°");
            }
            else
            {
                textView4.setText("m");
            }
            ll.addView(textView1); // Attache le TextView au layout parent
            ll.addView(textView2);
            ll.addView(textView3);
            ll.addView(textView4);
        }
    }

    public void Lancement(View v){

        Intent intent = new Intent(PlanificationActivity.this, PilotingActivity.class);
        intent.putExtra(PilotingActivity.EXTRA_DEVICE_SERVICE, service);
        intent.putStringArrayListExtra(PlanificationActivity.COMMAND_LIST, (ArrayList<String>) this.Actions);

        startActivity(intent);
    }
}