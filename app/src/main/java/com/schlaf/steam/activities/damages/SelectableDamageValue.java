package com.schlaf.steam.activities.damages;

public class SelectableDamageValue {

	private int damageValue;
	private boolean checked;
	
	public SelectableDamageValue(int value) {
		damageValue = value;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public String getDamageString() {
		return String.valueOf(damageValue);
	}
}
