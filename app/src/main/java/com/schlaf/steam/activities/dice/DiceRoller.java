package com.schlaf.steam.activities.dice;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DiceRoller {

	private int nbCases = 0;
	private int nbCasesSuccessfull = 0;
	private int nbCasesSuccessfullWithCritical = 0;
	
	private int[] dicesRolled;
	private int[] dicesRolledAfterRemoving;
	private int diceNumber;
	
	private int threshold = 8;
	
	public enum RemoveStrategy {
		REMOVE_NONE,
		REMOVE_LOWEST,
		REMOVE_BIGGEST;
	}

	public void rollDices(int nbDices, int nbDicesToRemove, RemoveStrategy removeStrategy) {
		nbCases = 0;
		nbCasesSuccessfull = 0;
		nbCasesSuccessfullWithCritical = 0;
		diceNumber = nbDices;
		rollDices(nbDices, nbDicesToRemove, 0, removeStrategy);
	}
	
	private void rollDices(int nbDices, int nbDicesToRemove, int currentDiceNumber, RemoveStrategy removeStrategy) {
		if (currentDiceNumber == 0) {
			dicesRolled = new int[nbDices];
		}
		
		for (int diceValue = 1; diceValue <=6; diceValue ++) {
			dicesRolled[currentDiceNumber] = diceValue;
			if (currentDiceNumber == nbDices - 1) {
				// exit
				removeDices(nbDicesToRemove, removeStrategy);
			} else {
				rollDices(nbDices, nbDicesToRemove, currentDiceNumber+1, removeStrategy);	
			}
			
		}
		
	}
	
	public double getPercentageHit() {
		return ( (double) nbCasesSuccessfull * 100) / nbCases;
	}
	
	public double getPercentageCritical() {
		return ( (double) nbCasesSuccessfullWithCritical * 100) / nbCases;
	}
	
	private void recap() {
		System.out.println("nb cases : " + nbCases);
		System.out.println("dont succés : " + nbCasesSuccessfull + " soit " + nbCasesSuccessfull * 100 / nbCases + "%");
		System.out.println("dont critique : " + nbCasesSuccessfullWithCritical+ " soit " + nbCasesSuccessfullWithCritical * 100 / nbCases + "%");

	}
	
	private void removeDices(int nbDicesToRemove, RemoveStrategy removeStrategy) {
		
		if (nbDicesToRemove > 0 && removeStrategy != RemoveStrategy.REMOVE_NONE) {
			
			ArrayList<Integer> dices = new ArrayList<Integer>(diceNumber);
			for (int i = 0; i< dicesRolled.length; i++) {
				dices.add(dicesRolled[i]);
			}
			

			
			switch (removeStrategy) {
			case REMOVE_LOWEST:
				Collections.sort(dices);
				for (int i = 0; i < nbDicesToRemove; i++) {
					dices.remove(0);
				}
				break;
			case REMOVE_BIGGEST:
				Collections.sort(dices);
				Collections.reverse(dices);
				for (int i = 0; i < nbDicesToRemove; i++) {
					dices.remove(0);
				}
				break;
			default:
				break;
			}
			dicesRolledAfterRemoving = new int[diceNumber-nbDicesToRemove];
			int i = 0;
			for (Integer die : dices) {
				dicesRolledAfterRemoving[i++] = die.intValue();
			}
		} else {
			dicesRolledAfterRemoving = dicesRolled;
		}
		
		
		
		finalizeDiceRoll();
	}
	
	private void finalizeDiceRoll() {
		nbCases++;
		
		// register result;
		int sum = 0;
		for (int i = 0; i< dicesRolledAfterRemoving.length; i++) {
			sum += dicesRolledAfterRemoving[i];
		}
		if (sum >= threshold) {
			nbCasesSuccessfull++;
			
			List<Integer> distinctDices = new ArrayList<Integer>(); 
					
			boolean allOne = true;
			boolean atLeastTwoDiceIdentical = false;
			for (int i = 0; i< dicesRolledAfterRemoving.length; i++) {
				int diceValue = dicesRolledAfterRemoving[i];
				
				if (diceValue != 1) {
					allOne = false;
				}
				if (distinctDices.contains(Integer.valueOf(diceValue))) {
					atLeastTwoDiceIdentical = true;
				} else {
					distinctDices.add(Integer.valueOf(diceValue));
				}
				
			}
			// System.out.println("result OK : " + displayRoll(dicesRolled));
			if (atLeastTwoDiceIdentical && !allOne) {
				nbCasesSuccessfullWithCritical ++;
				//System.out.println("withCritical");
			}
			
		}
	}
	
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		DiceRoller roller = new DiceRoller();
		roller.threshold = 8;
		roller.diceNumber = 2;
		roller.rollDices(2, 0, 0, RemoveStrategy.REMOVE_BIGGEST);
		roller.recap();
		long endTime = System.currentTimeMillis();
		long elapsed = endTime - startTime;
		System.out.println("elapsed : " + elapsed);
		
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

}
