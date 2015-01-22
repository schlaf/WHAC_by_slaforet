package com.schlaf.steam.activities.importation;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class ImportTab  implements TabContentFactory{

	private Context mContext;
	 
    public ImportTab(Context context){
        mContext = context;
    }
	@Override
    public View createTabContent(String tag) {
        View v = new View(mContext);
        return v;
    }
	
}
