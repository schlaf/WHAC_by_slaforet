package com.schlaf.steam.activities.importation;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class ImportVersionsFragment extends Fragment {

    public static final String ID = "ImportVersionsFragment";


    private ImportFileListener mListener;

    private TextView contentTV;
    private RecyclerView versionListView;
    private View headerLayout;

    private FileVersionAdapter adapter = new FileVersionAdapter();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImportVersionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImportVersionsFragment newInstance(String param1, String param2) {
        ImportVersionsFragment fragment = new ImportVersionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ImportVersionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.import_versions, container, false);

        return view;
    }

    public void checkVersions() {

        contentTV = (TextView) getView().findViewById(R.id.versions_content);
        versionListView = (RecyclerView)  getView().findViewById(R.id.versions_listview);
        new DownloadWebpageTask().execute("http://schlaf.github.io/data/current-releases.xml");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ImportFileListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ImportFileListener");
        }

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        versionListView = (RecyclerView)  getView().findViewById(R.id.versions_listview);
        versionListView.setVisibility(View.GONE);
        versionListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        versionListView.setAdapter(adapter);

        headerLayout = (View)  getView().findViewById(R.id.import_header);
        headerLayout.setVisibility(View.GONE);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, ImportVersionsStatus> {
        @Override
        protected ImportVersionsStatus doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return new ImportVersionsStatus();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ImportVersionsStatus result) {

            HashMap<String, Faction> factions =  ArmySingleton.getInstance().getFactions();

            List<FileVersion> versions = result.getVersions();

            for (FileVersion version : versions) {
                Faction faction = factions.get(version.faction.getId());
                faction.getVersion();

                version.factionName = faction.getFullName();
                version.localVersion = faction.getVersion();
            }


            headerLayout.setVisibility(View.VISIBLE);

            // contentTV.setText(result);
            adapter.getVersions().clear();
            adapter.getVersions().addAll(result.getVersions());
            versionListView.setAdapter(adapter);
            versionListView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }


        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private ImportVersionsStatus downloadUrl(String myurl) throws IOException {

            ImportVersionsStatus status = new ImportVersionsStatus();
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("FetchURL", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string


                try {

                    XmlPullParserFactory factory = XmlPullParserFactory
                            .newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(is, "UTF-8");
                    xpp.next();
                    int eventType = xpp.getEventType();



                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            Log.d("ImportVersions", xpp.toString());
                            if ("lastDate".equals(xpp.getName())) {
                                String date = xpp.getAttributeValue(null, "date");
                                status.setLastUpdate(date);
                            }
                            if ("faction".equals(xpp.getName())) {
                                String id = xpp.getAttributeValue(null, "id");
                                String version = xpp.getAttributeValue(null, "version");
                                String date = xpp.getAttributeValue(null, "date");

                                FileVersion fileVersion = new FileVersion();
                                fileVersion.lastVersion = version;
                                fileVersion.faction = FactionNamesEnum.getFaction(id);
                                fileVersion.dateUpdated = date;

                                status.getVersions().add(fileVersion);

                                status.setLastUpdate(date);
                            }

                        }
                        eventType = xpp.next();
                    }
                    is.close();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return status;
        }


    }

}
