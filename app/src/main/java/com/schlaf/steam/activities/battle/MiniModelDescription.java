package com.schlaf.steam.activities.battle;

import java.io.Serializable;

import com.schlaf.steam.data.SingleModel;

public class MiniModelDescription implements Serializable {

	/** serial  */
	private static final long serialVersionUID = 4563580883489090697L;
	
	private String name;
	private int spd;
	private int str;
	private int mat;
	private int rat;
	private int def;
	private int arm;
	private int cmd;
	
	public MiniModelDescription(String name, int spd, int str, int mat, int rat, int def, int arm, int cmd) {
		this.name = name;
		this.spd = spd;
		this.str = str;
		this.mat = mat;
		this.rat = rat;
		this.def = def;
		this.arm = arm;
		this.cmd = cmd;
	}
	
	public MiniModelDescription(SingleModel model) {
        spd = model.getSPD();
        str = model.getSTR();
        mat = model.getMAT();
        rat = model.getRAT();
		def = model.getDEF();
		arm = model.getARM();
        cmd = model.getCMD();
		name = model.getName();
	}

    public int getCmd() {
        return cmd;
    }

    public int getMat() {
        return mat;
    }

    public int getRat() {
        return rat;
    }

    public int getSpd() {
        return spd;
    }

    public int getStr() {
        return str;
    }

    public int getDef() {
		return def;

	}

	public int getArm() {
		return arm;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDefArmLabel() {

        StringBuffer sb = new StringBuffer(20);
        if (spd>0) {
            sb.append("<font color=\"grey\">");
            sb.append("SPD");
            sb.append("</font>");
            sb.append(spd);
        }
        if (str> 0) {
            sb.append("<font color=\"grey\">");
            sb.append(" STR");
            sb.append("</font>");
            sb.append(str);
        }
        if (mat > 0) {
            sb.append("<font color=\"grey\">");
            sb.append(" MAT");
            sb.append("</font>");
            sb.append(mat);
        }
        if (rat > 0) {
            sb.append("<font color=\"grey\">");
            sb.append(" RAT");
            sb.append("</font>");
            sb.append(rat);
        }
        sb.append("<font color=\"grey\">");
        sb.append(" DEF");
        sb.append("</font>");
        sb.append(def);
        sb.append("<font color=\"grey\">");
        sb.append(" ARM");
        sb.append("</font>");
        sb.append(arm);
        if (cmd > 0) {
            sb.append("<font color=\"grey\">");
            sb.append(" CMD");
            sb.append("</font>");
            sb.append(cmd);
        }

		// sb.append("DEF ").append(def).append(" - ARM ").append(arm); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

}
