package com.schlaf.steam.data;

import com.schlaf.steam.activities.selectlist.selected.SpellCaster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SingleModel implements SpellCaster, Serializable {

	/** serial 	 */
	private static final long serialVersionUID = -7920751069340356399L;
	/** name */
	private String name;
	/** speed */
	private int SPD;

    /** strength */
	private int STR;
	/** melee attack */
	private int MAT;
	/** ranged attack */
	private int RAT;
	/** defense */
	private int DEF;
	/** armor */
	private int ARM;
	/** command */
	private int CMD;

	/** list of weapons */
	private List<Weapon> weapons = new ArrayList<Weapon>();

    private ArrayList<Spell> spells = new ArrayList<Spell>(10);


    /** hitpoints on damage grid */
	private DamageGrid damages;
	
	private List<Capacity> capacities = new ArrayList<Capacity>();
	
	// liste des caracs spéciales
	boolean advanceDeployment;
    boolean amphibious;
	boolean arcNode;
    boolean assault;
    boolean cavalry;
	boolean cra;
	boolean cma;
	boolean construct;
	boolean eyelessSight;
    boolean flight;
	boolean gunfighter;
	boolean incorporeal;
	boolean jackMarshal;
	boolean journeyManWarcaster;
	boolean lesserWarlock;
	boolean officer;
    boolean parry;
	boolean pathfinder;
    boolean soulless;
	boolean standardBearer;
	boolean stealth;
	boolean tough;
	boolean undead;
	
	boolean immunityFire;
	boolean immunityFrost;
	boolean immunityElectricity;
	boolean immunityCorrosion;
	
	// for casters, warlocks, warbeasts
	String focus;
	String fury;
    int threshold;
	
	
	public String getHTMLResume() {
		StringBuffer sb = new StringBuffer(512);
		sb.append("[SPD=").append(SPD).append("|STR=").append(STR).append("|MAT=").append(MAT)
		.append("|RAT=").append(RAT).append("|DEF=").append(DEF).append("|ARM=").append(ARM)
		.append("|CMD=").append(CMD).append("]");
		return sb.toString();
	}
	
	
	public int getSPD() {
		return SPD;
	}

	public void setSPD(int sPD) {
		SPD = sPD;
	}

	public int getSTR() {
		return STR;
	}

	public void setSTR(int sTR) {
		STR = sTR;
	}

	public int getMAT() {
		return MAT;
	}

	public void setMAT(int mAT) {
		MAT = mAT;
	}

	public int getRAT() {
		return RAT;
	}

	public void setRAT(int rAT) {
		RAT = rAT;
	}

	public int getDEF() {
		return DEF;
	}

	public void setDEF(int dEF) {
		DEF = dEF;
	}

	public int getARM() {
		return ARM;
	}

	public void setARM(int aRM) {
		ARM = aRM;
	}

	public int getCMD() {
		return CMD;
	}

	public void setCMD(int cMD) {
		CMD = cMD;
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<Weapon> weapons) {
		this.weapons = weapons;
	}

	public boolean isAdvanceDeployment() {
		return advanceDeployment;
	}

	public void setAdvanceDeployment(boolean advanceDeployment) {
		this.advanceDeployment = advanceDeployment;
	}

	public boolean isArcNode() {
		return arcNode;
	}

	public void setArcNode(boolean arcNode) {
		this.arcNode = arcNode;
	}

	public boolean isCra() {
		return cra;
	}

	public void setCra(boolean cra) {
		this.cra = cra;
	}

	public boolean isCma() {
		return cma;
	}

	public void setCma(boolean cma) {
		this.cma = cma;
	}

	public boolean isConstruct() {
		return construct;
	}

	public void setConstruct(boolean construct) {
		this.construct = construct;
	}

	public boolean isEyelessSight() {
		return eyelessSight;
	}

	public void setEyelessSight(boolean eyelessSight) {
		this.eyelessSight = eyelessSight;
	}

	public boolean isGunfighter() {
		return gunfighter;
	}

	public void setGunfighter(boolean gunfighter) {
		this.gunfighter = gunfighter;
	}

	public boolean isIncorporeal() {
		return incorporeal;
	}

	public void setIncorporeal(boolean incorporeal) {
		this.incorporeal = incorporeal;
	}

	public boolean isJackMarshal() {
		return jackMarshal;
	}

	public void setJackMarshal(boolean jackMarshal) {
		this.jackMarshal = jackMarshal;
	}

	public boolean isOfficer() {
		return officer;
	}

	public void setOfficer(boolean officer) {
		this.officer = officer;
	}

	public boolean isPathfinder() {
		return pathfinder;
	}

	public void setPathfinder(boolean pathfinder) {
		this.pathfinder = pathfinder;
	}

	public boolean isStandardBearer() {
		return standardBearer;
	}

	public void setStandardBearer(boolean standardBearer) {
		this.standardBearer = standardBearer;
	}

	public boolean isStealth() {
		return stealth;
	}

	public void setStealth(boolean stealth) {
		this.stealth = stealth;
	}

	public boolean isTough() {
		return tough;
	}

	public void setTough(boolean tough) {
		this.tough = tough;
	}

	public boolean isUndead() {
		return undead;
	}

	public void setUndead(boolean undead) {
		this.undead = undead;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DamageGrid getHitpoints() {
		return damages;
	}

	public void setHitpoints(DamageGrid hitpoints) {
		this.damages = hitpoints;
	}

	public List<Capacity> getCapacities() {
		return capacities;
	}

	public boolean isJourneyManWarcaster() {
		return journeyManWarcaster;
	}

	public void setJourneyManWarcaster(boolean journeyManWarcaster) {
		this.journeyManWarcaster = journeyManWarcaster;
	}

	public boolean isLesserWarlock() {
		return lesserWarlock;
	}

	public void setLesserWarlock(boolean lesserWarlock) {
		this.lesserWarlock = lesserWarlock;
	}

	public boolean isImmunityFire() {
		return immunityFire;
	}

	public void setImmunityFire(boolean immunityFire) {
		this.immunityFire = immunityFire;
	}

	public boolean isImmunityFrost() {
		return immunityFrost;
	}

	public void setImmunityFrost(boolean immunityFrost) {
		this.immunityFrost = immunityFrost;
	}

	public boolean isImmunityElectricity() {
		return immunityElectricity;
	}

	public void setImmunityElectricity(boolean immunityElectricity) {
		this.immunityElectricity = immunityElectricity;
	}

	public boolean isImmunityCorrosion() {
		return immunityCorrosion;
	}

	public void setImmunityCorrosion(boolean immunityCorrosion) {
		this.immunityCorrosion = immunityCorrosion;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getFury() {
		return fury;
	}

	public void setFury(String fury) {
		this.fury = fury;
	}

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void setSpells(ArrayList<Spell> spells) {
        this.spells = spells;
    }

    public boolean isAmphibious() {
        return amphibious;
    }

    public void setAmphibious(boolean amphibious) {
        this.amphibious = amphibious;
    }

    public boolean isAssault() {
        return assault;
    }

    public void setAssault(boolean assault) {
        this.assault = assault;
    }

    public boolean isCavalry() {
        return cavalry;
    }

    public void setCavalry(boolean cavalry) {
        this.cavalry = cavalry;
    }

    public boolean isFlight() {
        return flight;
    }

    public void setFlight(boolean flight) {
        this.flight = flight;
    }

    public boolean isParry() {
        return parry;
    }

    public void setParry(boolean parry) {
        this.parry = parry;
    }

    public boolean isSoulless() {
        return soulless;
    }

    public void setSoulless(boolean soulless) {
        this.soulless = soulless;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

	@Override
	public boolean hasSpells() {
		return ! getSpells().isEmpty();
	}
}
