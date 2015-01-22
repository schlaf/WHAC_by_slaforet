package com.schlaf.steam.tier;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.schlaf.steam.R;

public class DisplayTierInfo extends TabActivity {

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tier_display_tab);
 
        // On récupére notre intent et la valeur nommée valeur
        String valeur = getIntent().getStringExtra("level");
 
        // On affiche cette chaéne dans le textview
        TextView textView = (TextView) findViewById(R.id.textView2);
        
        textView.setText("valeur = " + valeur);
 
        textView.setText(valeur);
 
    }
}
