package com.schlaf.steam.activities.selectlist;

import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.ModelTypeEnum;

public class SelectionGroup implements Comparable<SelectionGroup> {

	private ModelTypeEnum type;
	private FactionNamesEnum faction;
	
	public SelectionGroup(ModelTypeEnum type, FactionNamesEnum faction ) {
		this.type = type;
		this.faction = faction;
	}

	public ModelTypeEnum getType() {
		return type;
	}

	public FactionNamesEnum getFaction() {
		return faction;
	}

	@Override
	public int compareTo(SelectionGroup another) {
		
		if (faction.equals(another.getFaction())) {
			return type.compareTo(another.getType());
		} else {
			return faction.compareTo(another.getFaction());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((faction == null) ? 0 : faction.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectionGroup other = (SelectionGroup) obj;
		if (faction != other.faction)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
