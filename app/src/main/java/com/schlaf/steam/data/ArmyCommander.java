package com.schlaf.steam.data;

import java.util.ArrayList;


public abstract class ArmyCommander extends ArmyElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 488803665245182481L;

	public enum Generation {
		P("prime"),
		E("epic"),
		EE("legendary");
		
		private String label;
		
		private Generation(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}
	}
	
	protected Generation generation;
	protected String generationId; // unique id shared beetween all incarnations of same model
	protected String featTitle;
	protected String featContent;
	
	private ArrayList<Spell> spells = new ArrayList<Spell>(10);
	
	public ArmyCommander() {
		super();
		setFA(C_FA);
		setUniqueCharacter(true);
	}
	
	public Generation getGeneration() {
		return generation;
	}

	public void setGeneration(Generation generation) {
		this.generation = generation;
	}
	
	public String getFeatTitle() {
		return featTitle;
	}

	public void setFeatTitle(String featTitle) {
		this.featTitle = featTitle;
	}

	public String getFeatContent() {
		return featContent;
	}

	public void setFeatContent(String featContent) {
		this.featContent = featContent;
	}

	public ArrayList<Spell> getSpells() {
        if (getModels()!=null && getModels().size() > 0) {
            return getModels().get(0).getSpells();
        }
        return new ArrayList<Spell>();
	}

	public void setSpells(ArrayList<Spell> spells) {
        throw new UnsupportedOperationException();
	}

	public String getGenerationId() {
		return generationId;
	}

	public void setGenerationId(String generationId) {
		this.generationId = generationId;
	}


}
