/**
 *
 */
package com.schlaf.steam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleEntry;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.activities.battle.ChooseSpecialistsDialog;
import com.schlaf.steam.activities.battleresult.BattleResultsActivity;
import com.schlaf.steam.activities.selectlist.ArmySelectionChangeListener;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.data.FactionNamesEnum;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * @author S0085289
 *
 */
public class SpecialistRowAdapter extends ArrayAdapter<BattleEntry> {

    private final Context context;
    private final ChooseSpecialistsDialog parentDialog;
    private final List<BattleEntry> results;

    private final List<SpecialistEntry> specialists;

    public class SpecialistEntry {

        String name;
        public int cost;
        public boolean specialist;
        public SpecialistEntry(BattleEntry entry) {
            name = entry.getLabel();
            cost = entry.getCost();
            specialist = entry.isSpecialist();
        }

    }


    public List<SpecialistEntry> getSpecialists() {
        return specialists;
    }


    public SpecialistRowAdapter(Context context, ChooseSpecialistsDialog parent, List<BattleEntry> results) {
        super(context, R.layout.row_specialist, results);
        this.parentDialog = parent;
        this.context = context;
        this.results = results;

        specialists = new ArrayList<SpecialistEntry>(results.size());

        for (BattleEntry entry : results) {
            specialists.add(new SpecialistEntry(entry));
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_specialist, parent, false);
        }

        SpecialistEntry result = specialists.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView detail = (TextView) convertView.findViewById(R.id.detail);

        title.setText(result.name);
        detail.setText(String.valueOf(result.cost));

        ToggleButton specialistButton = (ToggleButton) convertView.findViewById(R.id.specialistToggleButton);
        specialistButton.setTag(result);
        specialistButton.setFocusable(false);
        specialistButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SpecialistEntry entry = (SpecialistEntry) v.getTag();
                entry.specialist = (((ToggleButton) v).isChecked()  );
                parentDialog.notifySpecialistChange();
            }
        });


        if (result.specialist) {
            specialistButton.setChecked(true);
        } else {
            specialistButton.setChecked(false);
        }


        convertView.setFocusable(false);

        return convertView;
    }


}
