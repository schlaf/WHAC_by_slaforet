package com.schlaf.steam.activities.selectlist.selected;

import com.schlaf.steam.R;

public class SelectedSection implements SelectedItem {

	public static int orderingOffsetCaster = -10010;
	public static int orderingOffsetUnit = -5010;
	public static int orderingOffsetSolo = -1010;
	public static int orderingOffsetBE = -510;
	public static int orderingOffsetObjective = -410;
	
	public static final int TYPE_CASTER = 1;
	public static final int TYPE_UNIT = 2;
	public static final int TYPE_SOLO = 3;
	public static final int TYPE_BE = 3;
	public static final int TYPE_OBJO = 4;
	
	private SectionTypeEnum type;
	
	public enum SectionTypeEnum {
		CASTER(TYPE_CASTER, orderingOffsetCaster, R.string.casters),
		UNIT(TYPE_UNIT, orderingOffsetUnit, R.string.units),
		SOLO(TYPE_SOLO, orderingOffsetSolo, R.string.solos),
		BATTLE_ENGINE(TYPE_BE, orderingOffsetBE, R.string.battle_engines ),
		OBJECTIVE(TYPE_OBJO, orderingOffsetObjective, R.string.objective);
		
		private int type;
		private int labelId;
		private int offset;
		
		private SectionTypeEnum(int type, int offset, int labelId) {
			this.type = type;
			this.offset = offset;
			this.labelId = labelId;
		}

		public int getType() {
			return type;
		}

		public int getLabelId() {
			return labelId;
		}

		public int getOffset() {
			return offset;
		}
	}
	
	public SelectedSection(SectionTypeEnum type) {
		super();
		this.type = type;
	}

	
	@Override
	public boolean isSection() {
		return true;
	}

	public int getLabelId() {
		return type.getLabelId();
	}

	@Override
	public int compareTo(SelectedItem another) {
		return getOrderingOffset() - another.getOrderingOffset();
	}

	@Override
	public int getOrderingOffset() {
		return type.getOffset();
	}


	@Override
	public String getLabel() {
		return type.name();
	}

}
