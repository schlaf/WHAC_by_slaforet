package com.schlaf.steam.tier;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.AvailableModels;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.Tier;
import com.schlaf.steam.data.TierCostAlteration;
import com.schlaf.steam.data.TierEntry;
import com.schlaf.steam.data.TierEntryGroup;
import com.schlaf.steam.data.TierFAAlteration;
import com.schlaf.steam.data.TierFACostBenefit;
import com.schlaf.steam.data.TierFreeModel;
import com.schlaf.steam.data.TierLevel;
import com.schlaf.steam.data.TierMarshalAlteration;

public class DisplayTierFragment extends DialogFragment implements OnClickListener {

	Tier tier;
	
	TextView tierRequirements;
	TextView tierBenefit;
	
	ToggleButton button1;
	ToggleButton button2;
	ToggleButton button3;
	ToggleButton button4;
	
	private static final String ARMY_INCLUDES_ONLY = "The army includes only the following models.";
	private static final String ARMY_INCLUDES = "The army includes %d or more (%s). ";
	private static final String ARMY_INCLUDES_THIS_MODEL = "The army includes (%s). ";
	private static final String ARMY_INCLUDES_IN_BATTLEGROUP = "%s's battlegroup includes %d or more (%s). ";
	private static final String ARMY_INCLUDES_IN_MARSHAL = "The army includes %d or more marshaled (%s). ";
	private static final String COST_REDUCTION = "Reduce the cost of (%s) models by %d.";
	private static final String COST_REDUCTION_RESTRICTED = "Reduce the cost of (%s) models attached to %s by %d. ";
	private static final String MARSHALL_AUGMENTATION = "(%s) models can marshal %d more warjack each. ";
	private static final String FA_AUGMENTATION = "Increase the FA of (%s) by %d.";
	private static final String FA_U = "(%s) models become FA:U. ";
	private static final String FREE_MODEL = "Add a (%s) free of cost. This entry does not count toward FA restrictions. ";
	private static final String FREE_MODEL_FOR_EACH = "Add a (%s) free of cost for each (%s). This entry does not count toward FA restrictions. ";
	private static final String FA_AUGMENTATION_FOR_EACH = "Increase the FA of (%s) by %d for each (%s). ";
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("DiceRollFragment", "onCreateDialog");

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		
		
		tier = SelectionModelSingleton.getInstance().getCurrentTiers();
		ArrayList<TierLevel> levels = tier.getLevels();
		
		builder.setTitle(tier.getTitle());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.tier_display_fragment, null);

		
		ArrayList<String> warjacks = new ArrayList<String>();
		ArrayList<String> warbeasts = new ArrayList<String>();
		ArrayList<String> units = new ArrayList<String>();
		ArrayList<String> solos = new ArrayList<String>();
		ArrayList<String> battleEngines = new ArrayList<String>();
		
		
		LinearLayout llT_warjacks = (LinearLayout) view.findViewById(R.id.llT_warjacks);
		LinearLayout llT_warbeasts= (LinearLayout) view.findViewById(R.id.llT_warbeasts);
		LinearLayout llT_units = (LinearLayout) view.findViewById(R.id.llT_units);
		LinearLayout llT_solos = (LinearLayout) view.findViewById(R.id.llT_solos);
		LinearLayout llT_BE = (LinearLayout) view.findViewById(R.id.llT_BE);

		boolean hasWarjacks = false;
		boolean hasWarbeasts = false;
		boolean hasUnits = false;
		boolean hasSolos = false;
		boolean hasBE = false;
		
		if( ! tier.getAvailableModels().isEmpty()) {
			for (AvailableModels models : tier.getAvailableModels()) {
				switch (models.getType()) {
				case WARJACKS :
					fillRestrictionZone(llT_warjacks, models.getModels());
					hasWarjacks = true;
					break;
				case BATTLE_ENGINES:
					fillRestrictionZone(llT_BE, models.getModels());
					hasBE = true;
					break;
				case SOLOS:
					fillRestrictionZone(llT_solos, models.getModels());
					hasSolos = true;
					break;
				case UNITS:
					fillRestrictionZone(llT_units, models.getModels());
					hasUnits = true;
					break;
				case WARBEASTS:
					fillRestrictionZone(llT_warbeasts, models.getModels());
					hasWarbeasts = true;
					break;
				default:
					break;
				}
			}
			
			if (!hasWarjacks) {
				llT_warjacks.setVisibility(View.GONE);
			} else {
				llT_warjacks.setVisibility(View.VISIBLE);
			}
			if (!hasWarbeasts) {
				llT_warbeasts.setVisibility(View.GONE);
			} else {
				llT_warbeasts.setVisibility(View.VISIBLE);
			}
			if (!hasUnits) {
				llT_units.setVisibility(View.GONE);
			} else {
				llT_units.setVisibility(View.VISIBLE);
			}
			if (!hasSolos) {
				llT_solos.setVisibility(View.GONE);
			} else {
				llT_solos.setVisibility(View.VISIBLE);
			}
			if (!hasBE) {
				llT_BE.setVisibility(View.GONE);
			} else {
				llT_BE.setVisibility(View.VISIBLE);
			}
			

		} else {
			for (TierEntry entry :  levels.get(0).getOnlyModels()) {
				ArmyElement model = ArmySingleton.getInstance().getArmyElement(entry.getId());
				if (model.getModelType() == ModelTypeEnum.WARJACK || model.getModelType() == ModelTypeEnum.COLOSSAL) {
					warjacks.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.WARBEAST || model.getModelType() == ModelTypeEnum.GARGANTUAN) {
					warbeasts.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.UNIT ) {
					units.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.SOLO ) {
					solos.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.BATTLE_ENGINE ) {
					battleEngines.add(model.getFullName());
				}
			}
			Collections.sort(warjacks);
			Collections.sort(warbeasts);
			Collections.sort(units);
			Collections.sort(solos);
			Collections.sort(battleEngines);
			
			fillRestrictionZone(llT_warjacks, warjacks);
			fillRestrictionZone(llT_warbeasts, warbeasts);
			fillRestrictionZone(llT_units, units);
			fillRestrictionZone(llT_solos, solos);
			fillRestrictionZone(llT_BE, battleEngines);
		}
		
		tierRequirements = (TextView) view.findViewById(R.id.tvRequirements);
		tierBenefit	 = (TextView) view.findViewById(R.id.tvBenefit);
		button1 = (ToggleButton) view.findViewById(R.id.button1);
		button2 = (ToggleButton) view.findViewById(R.id.button2);
		button3 = (ToggleButton) view.findViewById(R.id.button3);
		button4 = (ToggleButton) view.findViewById(R.id.button4);
		
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		
		showTier(0);
		
		builder.setView(view);
		// Create the AlertDialog object and return it
		return builder.create();
	}

	private void fillRestrictionZone(LinearLayout view, String models) {
		TextView tvRestriction = (TextView) view.findViewById(R.id.tvRestriction);
		tvRestriction.setText(models);
	}

	
	private void fillRestrictionZone(LinearLayout view, ArrayList<String> models) {
		
		if (models.isEmpty()) {
			view.setVisibility(View.GONE);
		} else {
			TextView tvJacks = (TextView) view.findViewById(R.id.tvRestriction);
			completeRestrictionZone(models, tvJacks);
		}
	}

	private void completeRestrictionZone(ArrayList<String> models,
			TextView textView) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (String model : models) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(model);
			first = false;
		}
		textView.setText(sb.toString());
	}
	
	private String getRequirementsString(TierLevel level) {
		
		StringBuffer result = new StringBuffer();
		boolean explicitRestriction = false;
		
		if (! level.isInheritOnlyModelsFromPreviousLevel() && level.getLevel() > 1) {
		
			ArrayList<String> warjacks = new ArrayList<String>();
			ArrayList<String> warbeasts = new ArrayList<String>();
			ArrayList<String> units = new ArrayList<String>();
			ArrayList<String> solos = new ArrayList<String>();
			ArrayList<String> battleEngines = new ArrayList<String>();

			for (TierEntry entry :  level.getOnlyModels()) {
				ArmyElement model = ArmySingleton.getInstance().getArmyElement(entry.getId());
				if (model.getModelType() == ModelTypeEnum.WARJACK || model.getModelType() == ModelTypeEnum.COLOSSAL) {
					warjacks.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.WARBEAST || model.getModelType() == ModelTypeEnum.GARGANTUAN) {
					warbeasts.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.UNIT ) {
					units.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.SOLO ) {
					solos.add(model.getFullName());
				}
				if (model.getModelType() == ModelTypeEnum.BATTLE_ENGINE ) {
					battleEngines.add(model.getFullName());
				}
			}
			Collections.sort(warjacks);
			Collections.sort(warbeasts);
			Collections.sort(units);
			Collections.sort(solos);
			Collections.sort(battleEngines);			
			
			result.append(ARMY_INCLUDES_ONLY).append("\n");
			result.append(generateOnlyString("WARJACKS", warjacks));
			result.append(generateOnlyString("WARBEASTS", warbeasts));
			result.append(generateOnlyString("UNITS", units));
			result.append(generateOnlyString("SOLOS", solos));
			result.append(generateOnlyString("BATTLE ENGINES", battleEngines));
			
			
		}
		
		
		if (! level.getMustHaveModels().isEmpty()) {
			explicitRestriction = true;
			for (TierEntryGroup entryGroup :  level.getMustHaveModels()) {
				boolean characterModel = false;
				StringBuffer modelList = new StringBuffer();
				boolean first = true;
				for (TierEntry entry :  entryGroup.getEntries()) {
					if (!first) {
						modelList.append(", ");
					}
					first = false;
					ArmyElement element = ArmySingleton.getInstance().getArmyElement(entry.getId());
					if (element.isUniqueCharacter()) {
						characterModel = true;
					}
					modelList.append(element.getFullName());
				}
				
				if (entryGroup.getEntries().size() > 1) {
					characterModel = false; // maybe one character, but in a group...
				}

				if (characterModel) {
					result.append(String.format(ARMY_INCLUDES_THIS_MODEL, modelList.toString() ));	
				} else {
					result.append(String.format(ARMY_INCLUDES, entryGroup.getMinCount(), modelList.toString() ));	
				}
					
				
				
				
			}
		}
		
		if (! level.getMustHaveModelsInBG().isEmpty()) {
			explicitRestriction = true;
			String casterName = ArmySingleton.getInstance().getArmyElement(tier.getCasterId()).getFullName();
			
			for (TierEntryGroup entryGroup :  level.getMustHaveModelsInBG()) {
                StringBuffer modelList = new StringBuffer();
				boolean first = true;
				for (TierEntry entry :  entryGroup.getEntries()) {
					if (!first) {
						modelList.append(", ");
					}
					first = false;
					modelList.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
				}
				
				result.append(String.format(ARMY_INCLUDES_IN_BATTLEGROUP, casterName, entryGroup.getMinCount(), modelList.toString() ));
			}
		}
		
		if (! level.getMustHaveModelsInMarshal().isEmpty()) {
			explicitRestriction = true;
			StringBuffer modelList = new StringBuffer();
			for (TierEntryGroup entryGroup :  level.getMustHaveModelsInMarshal()) {
				boolean first = true;
				for (TierEntry entry :  entryGroup.getEntries()) {
					if (!first) {
						modelList.append(", ");
					}
					first = false;
					modelList.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
				}
				
				result.append(String.format(ARMY_INCLUDES_IN_MARSHAL, entryGroup.getMinCount(), modelList.toString() ));
			}
		}
		
		if (!explicitRestriction) {
			result.append("This army can include only the models listed above.");
		}
		
		return result.toString();
	}
	
	private String generateOnlyString(String category, ArrayList<String> models) {
		if (models.isEmpty()) {
			return "";
		}
		StringBuffer modelList = new StringBuffer();
		modelList.append(category).append(" : ");
		boolean first = true;
		for (String entry :  models) {
			if (!first) {
				modelList.append(", ");
			}
			first = false;
			modelList.append(entry);
		}		
		modelList.append("\n");
		
		return modelList.toString();
	}

	private String getBenefitsString(TierLevel level) {
		StringBuffer sb = new StringBuffer(512);
		boolean first = true;
		for (TierFACostBenefit alteration : level.getBenefit().getAlterations()) {
			if (!first) {
				sb.append("\n");
				first = false;
			}
			
			ArmyElement entry = ArmySingleton.getInstance().getArmyElement( alteration.getEntry().getId());
			if (alteration instanceof TierCostAlteration) {
				if (alteration.isRestricted()) {
					ArmyElement owner = ArmySingleton.getInstance().getArmyElement( ((TierCostAlteration) alteration).getRestrictedToId());
					sb.append(String.format(COST_REDUCTION_RESTRICTED, entry.getFullName(), owner.getFullName(), ((TierCostAlteration) alteration).getCostAlteration()));
				} else {
					sb.append(String.format(COST_REDUCTION, entry.getFullName(), ((TierCostAlteration) alteration).getCostAlteration()));	
				}
				
			}
			
			if (alteration instanceof TierMarshalAlteration) {
					sb.append(String.format(MARSHALL_AUGMENTATION, entry.getFullName(), ((TierMarshalAlteration) alteration).getMarshallNbJacksAlteration()));
			}
			if (alteration instanceof TierFAAlteration) {
				TierFAAlteration faAlteration = (TierFAAlteration) alteration;
				if ( faAlteration.getFaAlteration() == ArmyElement.MAX_FA) {
					sb.append(String.format(FA_U, entry.getFullName()));
				} else {
					if (faAlteration.getForEach() != null && ! faAlteration.getForEach().isEmpty()) {
						StringBuffer sbForEach = new StringBuffer();
						boolean firstA = true;
						for (TierEntry entryForEach : faAlteration.getForEach()) {
							if (!firstA) {
								sbForEach.append(", ");
							}
							firstA = false;
							sbForEach.append(ArmySingleton.getInstance().getArmyElement(entryForEach.getId()).getFullName());
						}
						sb.append(String.format(FA_AUGMENTATION_FOR_EACH, entry.getFullName(), faAlteration.getFaAlteration(), sbForEach));
					} else {
						sb.append(String.format(FA_AUGMENTATION, entry.getFullName(), faAlteration.getFaAlteration()));	
					}
				}
			}
		}
		for (TierFreeModel freeModel : level.getBenefit().getFreebies()) {
			
			if (!first) {
				sb.append("\n");
				first = false;
			}

			StringBuffer sbModels = new StringBuffer();
			boolean firstModel = true;
			for (TierEntry entry : freeModel.getFreeModels()) {
				if (!firstModel) {
					sbModels.append(" or ");
				}
				firstModel = false;
				sbModels.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
			}

			boolean useForEach = false;
			StringBuffer sbForEach = new StringBuffer();
			if (freeModel.getForEach() != null && freeModel.getForEach().size() > 0) {
				useForEach = true;
				boolean firstA = true;
				for (TierEntry entry : freeModel.getForEach()) {
					if (!firstA) {
						sbForEach.append(", ");
					}
					firstA = false;
					sbForEach.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
				}
			}

			if (useForEach) {
				sb.append(String.format(FREE_MODEL_FOR_EACH, sbModels.toString(), sbForEach.toString() ));	 
			} else {
				sb.append(String.format(FREE_MODEL, sbModels.toString()));	
			}

		}
		if (level.getBenefit().getInGameEffect() != null && level.getBenefit().getInGameEffect().trim().length() > 0) {
			if (!first) {
				sb.append("\n");
				first = false;
			}
			sb.append(level.getBenefit().getInGameEffect());
		}
		return sb.toString();
	}
	
	
	private void showTier(int tierLevel) {
		
		button1.setChecked(false);
		button2.setChecked(false);
		button3.setChecked(false);
		button4.setChecked(false);
		
		switch (tierLevel) {
		case 0:
			button1.setChecked(true);
			break;
		case 1:
			button2.setChecked(true);
			break;
		case 2:
			button3.setChecked(true);
			break;
		case 3:
			button4.setChecked(true);
			break;
		default:
			break;
		}
		
		Tier tier = SelectionModelSingleton.getInstance().getCurrentTiers();
		TierLevel level = tier.getLevels().get(tierLevel);
		
		tierRequirements.setText(getRequirementsString(level));
		tierBenefit.setSingleLine(false);
		tierBenefit.setText(getBenefitsString(level));
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (button1.getId() == v.getId()) {
			showTier(0);
		}
		if (button2.getId() == v.getId()) {
			showTier(1);
		}
		if (button3.getId() == v.getId()) {
			showTier(2);
		}
		if (button4.getId() == v.getId()) {
			showTier(3);
		}
	}

}
