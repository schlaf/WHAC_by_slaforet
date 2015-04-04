package com.schlaf.steam.activities.importation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by seb on 10/03/15.
 */
public class ImportVersionsStatus {

    private String lastUpdate;

    private List<FileVersion> versions = new ArrayList<FileVersion>();

    public ImportVersionsStatus() {
        super();
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<FileVersion> getVersions() {
        return versions;
    }


}
