package com.schlaf.steam.activities.card;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.damages.DamageSpiralView;
import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.activities.damages.MyrmidonDamageGridView;
import com.schlaf.steam.activities.damages.WarjackDamageGridView;
import com.schlaf.steam.activities.selectlist.selected.SpellCaster;
import com.schlaf.steam.data.ArmyCommander;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.Capacity;
import com.schlaf.steam.data.Colossal;
import com.schlaf.steam.data.ColossalDamageGrid;
import com.schlaf.steam.data.MeleeWeapon;
import com.schlaf.steam.data.MountWeapon;
import com.schlaf.steam.data.MyrmidonDamageGrid;
import com.schlaf.steam.data.RangedWeapon;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.Spell;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.WarbeastDamageSpiral;
import com.schlaf.steam.data.WarbeastPack;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.Warlock;
import com.schlaf.steam.data.Weapon;

public class ViewCardFragment extends Fragment {

	public static final String ID = "ViewCardFragment";
	
	private static final String TAG = "ViewCardFragment";
	private final boolean D = false;
	
	public interface ViewCardActivityInterface {
		public ArmyElement getArmyElement();
		
		public void removeViewCardFragment(View v);
		
		/**
		 * card must be shown full screen
		 * @return
		 */
		public boolean isCardfullScreen();
		
		/**
		 * card has two panels (for landscape view)
		 * @return
		 */
		public boolean isCardDoublePane();
		
		/**
		 * card should be opened in new window
		 * @param v
		 */
		public void viewModelDetailInNewActivity(View v);
		
		/**
		 * card should be opened in fragment or window, depending on current screen disposition
		 * @param v
		 */
		public void viewModelDetail(View v);
		
		/**
		 * card is opened with a single click (else : use long click)
		 * @return
		 */
		public boolean useSingleClick();
		
		/**
		 * if true, you can use the card library selection screen to add a card to battle
		 * @return
		 */
		public boolean canAddCardToBattle();
		
		/**
		 * click on "+" button to add this card to battle entries of player1
		 * @param v
		 */
		public void addModelToBattle(View v);
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof ViewCardActivityInterface) {
			Log.d("ViewCardFragment", "onAttach received " + activity.getClass().getName());
		} else {
			throw new UnsupportedOperationException("ViewCardFragment requires a ViewCardActivityInterface as parent activity");
		}
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("ViewCardFragment", "ViewCardFragment.onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		fillCard( ((ViewCardActivityInterface) getActivity()).getArmyElement());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		Log.d("ViewCardFragment", "ViewCardFragment.onCreateView");
		
		View view = inflater.inflate(R.layout.card_fragment,
				container, false);

		return view;		
	}
	
	/**
	 * notify this fragment that a new model has been selected, and to redraw the view
	 */
	public void notifyNewModelToView() {
		Log.d("ViewCardFragment", "notifyNewModelToView");
		fillCard( ((ViewCardActivityInterface) getActivity()).getArmyElement());
		getView().invalidate();
	}

	
	public void updateContent() {
		fillCard( ((ViewCardActivityInterface) getActivity()).getArmyElement());
	}
	
	/**
	 * add a drawable icon to the end of the linear layout "title"
	 * 
	 * @param textResource
	 * @param drawableResource
	 */
	private void addCapacityImage(LinearLayout modelTitleLayout,
			final int drawableResource, final int textResource, final int descResource) {
		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(drawableResource);
		LayoutParams imageViewLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(imageViewLayoutParams);
		imageView.setPadding(0, 0, 2, 0);
		modelTitleLayout.addView(imageView);

		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder weaponDialog = new AlertDialog.Builder(getActivity());
				weaponDialog.setIcon(drawableResource);
				weaponDialog.setTitle(textResource);
				weaponDialog.setMessage(descResource);
				weaponDialog.show();
			}
		});

		
	}

	/**
	 * add a drawable icon to the end of the linear layout "title"
	 * 
	 * @param weapon1TitleLayout
	 * @param drawableResource
	 */
	private void addWeaponImage(LinearLayout weapon1TitleLayout,
			final int drawableResource, final int textResource, final int descResource) {
		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(drawableResource);
	
		LayoutParams imageViewLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(imageViewLayoutParams);
		imageView.setPadding(0, 0, 2, 0);
		weapon1TitleLayout.addView(imageView);
		
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder weaponDialog = new AlertDialog.Builder(getActivity());
				weaponDialog.setIcon(drawableResource);
				weaponDialog.setTitle(textResource);
				weaponDialog.setMessage(descResource);
				weaponDialog.show();
			}
		});
	}

	private String convertCaracToString(int carac) {
		if (carac > 0) {
			return Integer.toString(carac);
		} else {
			return "-";
		}
	}

	/**
	 * fill the complete card layout with data from the ArmyElement
	 * <br>
	 * handles special cases : casters, warjacks with damage grid, spells, feat, attached models, ...
	 * @param element
	 */
	private void fillCard(ArmyElement element) {

        if (element == null) {
            return;
        }

		if (D) Log.d(TAG, "-- start fillCard");
	
		TextView title = (TextView) getView().findViewById(R.id.card_title);
		title.setText(element.getFullName());

		TextView version = (TextView) getView().findViewById(R.id.card_version);
		version.setText(element.getVersion());


		TextView subtitle = (TextView) getView().findViewById(R.id.card_subtitle);
		subtitle.setText(element.getQualification());
		
		TextView fa = (TextView) getView().findViewById(R.id.card_fa);
		TextView cost = (TextView) getView().findViewById(R.id.card_cost);
		TextView unitSize = (TextView) getView().findViewById(R.id.card_unit_size);
		
		StringBuffer faString = new StringBuffer();
		faString.append("FA:");
		if (element.isUniqueCharacter()) {
			faString.append("C");
		} else if (element.isUnlimitedFA()) {
			faString.append("U");
		} else {
			faString.append(element.getFA());
		}
		fa.setText(faString.toString());
		
		StringBuffer costString = new StringBuffer(12);
		if (element instanceof Warcaster) {
			costString.append("WJ:+").append(Math.abs(element.getBaseCost()));
		} else if (element instanceof Warlock) {
			costString.append("WB:+").append(Math.abs(element.getBaseCost()));
		} else {
			costString.append("PC:").append(element.getBaseCost());
		}
		cost.setText(costString.toString());
		
		if (element instanceof Unit) {
			if ( ((Unit) element).isVariableSize() ) {
				Unit unit = (Unit) element;
				unitSize.setText( unit.getBaseNumberOfModels() + "/" + unit.getFullNumberOfModels() + getResources().getString(R.string._models));
				cost.setText( "PC:" + unit.getBaseCost() + "/" + unit.getFullCost() );
			} else {
				unitSize.setText(((Unit) element).getBaseNumberOfModels() + getResources().getString(R.string._models));	
			}
		} else if (element instanceof WarbeastPack) {
			unitSize.setText(((WarbeastPack) element).getNbModels() + getResources().getString(R.string._models));
		} else {
			unitSize.setVisibility(View.GONE);
		}
		
	
//		int logoId = FactionNamesEnum.getFaction(element.getFaction().getId())
//				.getLogoResource();
//		ImageView logo = (ImageView) getView().findViewById(R.id.factionLogo);
//		logo.setImageResource(logoId);
	
		if (D) Log.d(TAG, "--- handle manchette visibility : Start");
		if (((ViewCardActivityInterface) getActivity()).isCardfullScreen() ){
			// remove title;
			LinearLayout cardManchette = (LinearLayout) getView().findViewById(R.id.card_manchette);
			cardManchette.setVisibility(View.GONE);
			// ((LinearLayout) cardManchette.getParent()).removeView(cardManchette);
		} else {
			TextView shortTitle = (TextView) getView().findViewById(R.id.card_shorttitle);
			shortTitle.setText(element.getFullName());
		}
		if (D) Log.d(TAG, "--- handle manchette visibility : end");


		LinearLayout uniqueCardContainer = (LinearLayout)  getView().findViewById(R.id.uniqueCardContainer);
		ScrollView secondCardScrollView = (ScrollView)  getView().findViewById(R.id.secondCardScrollView);
		LinearLayout secondCardBaseLayoutTwoRows = (LinearLayout)  getView().findViewById(R.id.secondCardBaseLayoutTwoRows);

		if (D) Log.d(TAG, "--- handle doublepane : Start");
		if (((ViewCardActivityInterface) getActivity()).isCardDoublePane() ){
			// cards side to side,
			// delete feat, spells, ... from first card zone
			uniqueCardContainer.removeView(uniqueCardContainer.findViewById(R.id.featView));
			uniqueCardContainer.removeView(uniqueCardContainer.findViewById(R.id.spellView));
			uniqueCardContainer.removeView(uniqueCardContainer.findViewById(R.id.otherView));
		} else {
			// cards in on list from top to bottom
			secondCardBaseLayoutTwoRows.removeView(secondCardBaseLayoutTwoRows.findViewById(R.id.featView));
			secondCardBaseLayoutTwoRows.removeView(uniqueCardContainer.findViewById(R.id.spellView));
			secondCardBaseLayoutTwoRows.removeView(secondCardBaseLayoutTwoRows.findViewById(R.id.otherView));
			secondCardScrollView.setVisibility(View.GONE);
		}
		if (D) Log.d(TAG, "--- handle doublepane : end");
		
		StringBuffer fullText = new StringBuffer(512);
		
		if (element.getModels() != null && element.getModels().size() > 0) {
			int elementCount = 0;
			for (SingleModel model : element.getModels()) {
				if (D) Log.d(TAG, "--- handle model grid : Start");
				View modelView = null;
				if (elementCount == 0) {
					// reuse basic model view
					modelView = (View) getView().findViewById(R.id.modelgrid);
				} else {
					// create new model view
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					modelView = inflater.inflate(R.layout.model_grid, null, true);
					// and add to list
					int childCount = uniqueCardContainer.getChildCount();
					int featViewIndex = 0;
					for ( int i = 0; i < childCount; i++) {
						if (uniqueCardContainer.getChildAt(i).getId() == R.id.featView) {
							featViewIndex = i;
							break;
						}
					}
					if (featViewIndex == 0) {
						uniqueCardContainer.addView(modelView); // append at end of card
					} else {
						uniqueCardContainer.addView(modelView, featViewIndex); // insert just before feat	
					}
					
				}
				fillModel(model, modelView, element, elementCount==0);
				
				// fill model capacities
				if (! model.getCapacities().isEmpty()) {
					
					if (elementCount > 0 && fullText.length() > 0) {
						fullText.append("<BR>");
					}
					
					fullText.append("<U><B>").append(model.getName()).append("</B></U><BR>");
					for (Capacity capacity : model.getCapacities()) {
						fullText.append("<B>").append(capacity.getTitle()).append("</B>");
						if (capacity.getType() !=null && capacity.getType().trim().length() > 0) {
							fullText.append("[").append(capacity.getType().replace('*', '\u2605')).append("]");
						}
						fullText.append(" - ").append(capacity.getLabel().replace("\n", "<BR>")).append("<BR><BR>");
					}
				}
				if (D) Log.d(TAG, "--- handle model grid : end");
				elementCount++;
			}
		}
		
		element.setCardFullText(fullText.toString());
		
		boolean secondCardHasData = false;
		
		if (element instanceof ArmyCommander ) {
			View featView = (View) getView().findViewById(R.id.featView);
			
			TextView featTitle = (TextView) featView.findViewById(R.id.featTitle);
			featTitle.setText(((ArmyCommander) element).getFeatTitle());
			
			TextView featText = (TextView) featView.findViewById(R.id.featTextView);
			featText.setText(((ArmyCommander) element).getFeatContent());
	
			featText.setTag(R.id.featTitle, ((ArmyCommander) element).getFeatTitle());
			featText.setTag(R.id.featTextView, ((ArmyCommander) element).getFeatContent());
			featText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
					alert.setTitle( (String) v.getTag(R.id.featTitle));
					alert.setMessage( (String) v.getTag(R.id.featTextView));
					alert.show();
				}
			});
			
			secondCardHasData = true;
		} else {
			View featView = (View) getView().findViewById(R.id.featView);
			featView.setVisibility(View.GONE);
		}
		
		if (D) Log.d(TAG, "--- handle spells : Start");
		if (element instanceof SpellCaster && ((SpellCaster) element).hasSpells()) {
			View spellView = (View) getView().findViewById(R.id.spellView);
			
			TableLayout spellTable = (TableLayout) spellView.findViewById(R.id.spellTable);
			
			spellTable.removeView(spellTable.findViewById(R.id.toRemove1));
			spellTable.removeView(spellTable.findViewById(R.id.toRemove2));

            if (element.getModels() != null && element.getModels().size() > 0) {

                boolean moreThan1ModelWithSpells = false;
                int modelsWithSpells = 0;
                for (SingleModel model : element.getModels()) {
                    if (model.getSpells() != null && ! model.getSpells().isEmpty()) {
                        modelsWithSpells ++;
                    }
                }
                if (modelsWithSpells > 1) {
                    moreThan1ModelWithSpells = true;
                }
                // more than 1 model with spells --> write down the model name


                for (SingleModel model : element.getModels()) {
                    if (moreThan1ModelWithSpells && model.getSpells() != null && ! model.getSpells().isEmpty()) {
                        View spellLineModelNameView = getLayoutInflater(null).inflate(R.layout.spell_line_model_name, null, false);
                        ((TextView) spellLineModelNameView.findViewById(R.id.modelNameTv)).setText(model.getName());
                        spellTable.addView(spellLineModelNameView);
                    }


                    for (Spell spell : model.getSpells()) {
                        View spellLineCaracView = getLayoutInflater(null).inflate(R.layout.spell_line_carac, null, false);
                        ((TextView) spellLineCaracView.findViewById(R.id.spellTitle)).setText(spell.getTitle());
                        ((TextView) spellLineCaracView.findViewById(R.id.spellCost)).setText(spell.getCost());
                        ((TextView) spellLineCaracView.findViewById(R.id.spellRange)).setText(spell.getRange());
                        ((TextView) spellLineCaracView.findViewById(R.id.spellAOE)).setText(spell.getAoe());
                        ((TextView) spellLineCaracView.findViewById(R.id.spellPOW)).setText(spell.getPow());
                        ((TextView) spellLineCaracView.findViewById(R.id.spellUP)).setText(spell.getDuration());
                        ((TextView) spellLineCaracView.findViewById(R.id.spellOFF)).setText(spell.getOffensive());
                        spellTable.addView(spellLineCaracView);

                        View spellLineTextView = getLayoutInflater(null).inflate(R.layout.spell_line_text, null, false);
                        ((TextView) spellLineTextView.findViewById(R.id.spellDescription)).setText(Html.fromHtml(spell.getFullText()));
                        spellTable.addView(spellLineTextView);


                        spellLineCaracView.setTag(spell);
                        spellLineTextView.setTag(spell);
                        spellLineCaracView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createSpellPopup(v);
                            }
                        });
                        spellLineTextView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createSpellPopup(v);
                            }
                        });

                    }
                }


            }
			secondCardHasData = true;
		}  else {
			View spellView = (View) getView().findViewById(R.id.spellView);
			spellView.setVisibility(View.GONE);
		}
		if (D) Log.d(TAG, "--- handle spells : end");
	
		if (element.getCardFullText() != null && element.getCardFullText().trim().length() > 0) {
			View otherView = (View) getView().findViewById(R.id.otherView);
			TextView otherText = (TextView) otherView
					.findViewById(R.id.detailText);
			otherText.setText(Html.fromHtml(element.getCardFullText()));
			
			otherView.setTag(element.getCardFullText());
			otherView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String fullText = (String) v.getTag();
					AlertDialog.Builder fullTextDialog = new AlertDialog.Builder(getActivity());
					fullTextDialog.setTitle("Capacities");
					fullTextDialog.setMessage(Html.fromHtml(fullText));
					fullTextDialog.show();
				}
			});
			
			
			secondCardHasData = true;
		} else {
			View otherView = (View) getView().findViewById(R.id.otherView);
			otherView.setVisibility(View.GONE);
			// secondCardLayout.removeView(otherView);
		}
		
		if (!secondCardHasData) {
			secondCardScrollView.setVisibility(View.GONE);
		}
		
		if (D) Log.d(TAG, "-- end fillCard");
		
	}

	private void fillModel(SingleModel element, View container, ArmyElement parent, boolean firstModel) {
	
		View baseStatsView = (View) container.findViewById(R.id.basestats);
		generateBaseStatView(baseStatsView, element);
	
		// generate focus/fury
		ImageView image = (ImageView) container.findViewById(R.id.imageView1);
		ImageView imageThr = (ImageView) container.findViewById(R.id.imageViewThreshold);
		Log.d("ViewCardFragment", "army element class = " + parent.getClass().getName());
		boolean noFocusOrFury = true;
		if (element.getFocus() != null && element.getFocus().length() > 0) {
			noFocusOrFury = false;
			String focus = element.getFocus().trim();
			Log.d("ViewCardFragment", "warcaster : focus = " + focus);
			switch (focus) {
				case "3":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_3_icon));
					break;
				case "4":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_4_icon));
					break;
				case "5":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_5_icon));
					break;
				case "6":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_6_icon));
					break;
				case "7":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_7_icon));
					break;
				case "8":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_8_icon));
					break;
				case "9":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_9_icon));
					break;
				case "10":
					image.setImageDrawable(getResources().getDrawable(R.drawable.focus_10_icon));
					break;
				default:
					image.setImageDrawable(getResources().getDrawable(R.drawable.empty));
			}
		} else {
			image.setImageDrawable(getResources().getDrawable(R.drawable.empty));
		}
		if (element.getFury() != null && element.getFury().length() > 0) {
			noFocusOrFury = false;
			String fury = element.getFury().trim();

			switch (fury) {
				case "2":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_2_icon));
					break;
				case "3":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_3_icon));
					break;
				case "4":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_4_icon));
					break;
				case "5":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_5_icon));
					break;
				case "6":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_6_icon));
					break;
				case "7":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_7_icon));
					break;
				case "8":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_8_icon));
					break;
				case "*":
					image.setImageDrawable(getResources().getDrawable(R.drawable.fury_star_icon));
				default:
					image.setImageDrawable(getResources().getDrawable(R.drawable.empty));
			}
		}
		if (element.getThreshold() > 0) {
			noFocusOrFury = false;
			switch (element.getThreshold()) {
				case 3:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_3_icon));
					break;
				case 4:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_4_icon));
					break;
				case 5:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_5_icon));
					break;
				case 6:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_6_icon));
					break;
				case 7:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_7_icon));
					break;
				case 8:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_8_icon));
					break;
				case 9:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_9_icon));
					break;
				case 10:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_10_icon));
					break;
				case 11:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.thr_11_icon));
					break;
				default:
					imageThr.setImageDrawable(getResources().getDrawable(R.drawable.empty));
			}
		} else {
			imageThr.setImageDrawable(getResources().getDrawable(R.drawable.empty));
		}

		
		RelativeLayout weaponsLayout = (RelativeLayout) container.findViewById(R.id.focusAndWeaponsLayout);
		
		if (element.getWeapons().isEmpty() && noFocusOrFury ) {
			weaponsLayout.setVisibility(View.GONE);
		} else if (element.getWeapons().isEmpty() &&  ! noFocusOrFury) {
			// if no weapons, align focus/fury & threshold
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imageThr.setLayoutParams(params);
			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params2.addRule(RelativeLayout.LEFT_OF, R.id.imageView1);
			image.setLayoutParams(params2);
		}
		
		boolean firstWeapon = true;
		int weaponViewId = 10235;
		for (Weapon weapon : element.getWeapons()) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			if (noFocusOrFury) {
				// add a padding
				params.setMargins(48, 0, 0, 0);
			}
			if (firstWeapon) {
				params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView1);
				firstWeapon = false;
			} else {
				params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView1);
				params.addRule(RelativeLayout.BELOW, weaponViewId);
			}
			if (weapon instanceof MeleeWeapon) {
				View meleeWeaponView = generateMeleeWeaponView((MeleeWeapon) weapon);
				meleeWeaponView.setId(++weaponViewId);
				weaponsLayout.addView(meleeWeaponView, params);
			} else if (weapon instanceof RangedWeapon) {
				View rangedWeaponView = generateRangedWeaponView((RangedWeapon) weapon);
				rangedWeaponView.setId(++weaponViewId);
				weaponsLayout.addView(rangedWeaponView, params);
			} else if (weapon instanceof MountWeapon) {
				View mountWeaponView = generateMountWeaponView((MountWeapon) weapon);
				mountWeaponView.setId(++weaponViewId);
				weaponsLayout.addView(mountWeaponView, params);
			} 
		}
	
		// handle damage grid
        View layoutGrid = container;
		// if (parent instanceof Warjack || parent instanceof Warcaster) {
		FrameLayout gridHolder = (FrameLayout) layoutGrid.findViewById(R.id.damageGridHolder);
		
			if (element.getHitpoints() instanceof ModelDamageLine) {
				TextView tvDamages = (TextView) gridHolder.findViewById(R.id.textViewDamages);
				tvDamages.setText("Damages: " + element.getHitpoints().getTotalHits());
//				DamageLineView lineView = new DamageLineView(getActivity());
//				lineView.setDamageLine( (ModelDamageLine) element.getHitpoints());
//				lineView.setEdit(false);
//				gridHolder.addView(lineView);
			} else {
				gridHolder.removeAllViews();
			}
		
			if (element.getHitpoints() instanceof WarjackDamageGrid ) {
				WarjackDamageGridView gridView = new WarjackDamageGridView(getActivity());
				gridView.setGrid((WarjackDamageGrid)element.getHitpoints());
				gridView.setEdit(false);
				gridHolder.addView(gridView);
			} 
			
			if (element.getHitpoints() instanceof MyrmidonDamageGrid ) {
				MyrmidonDamageGridView gridView = new MyrmidonDamageGridView(getActivity());
				gridView.setGrid((MyrmidonDamageGrid)element.getHitpoints());
				gridView.setEdit(false);
				gridHolder.addView(gridView);
			} 

			if (element.getHitpoints() instanceof ColossalDamageGrid) {
				LinearLayout gridView = new LinearLayout(getActivity());
				gridHolder.addView(gridView);
				gridView.setOrientation(LinearLayout.HORIZONTAL);
				
				if ( ((ColossalDamageGrid) element.getHitpoints()).getForceFieldGrid() != null) {
					WarjackDamageGridView leftGrid = new WarjackDamageGridView(getActivity());
					MyrmidonDamageGridView rightGrid = new MyrmidonDamageGridView(getActivity());
					LinearLayout.LayoutParams paramsLeft = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3);
					LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 4);
					gridView.addView(leftGrid, paramsLeft);
					gridView.addView(rightGrid, paramsRight);
					leftGrid.setGrid( ((ColossalDamageGrid) element.getHitpoints()).getLeftGrid());
					leftGrid.setEdit(false);
					MyrmidonDamageGrid rGrid = new MyrmidonDamageGrid(element);
					rGrid.fromString( ((Colossal) parent).getForceField() + "-" + ((Colossal) parent).getRightGrid());
					rightGrid.setGrid( rGrid);
					rightGrid.setEdit(false);
				} else {
					
					WarjackDamageGridView leftGrid = new WarjackDamageGridView(getActivity());
					WarjackDamageGridView rightGrid = new WarjackDamageGridView(getActivity());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
					gridView.addView(leftGrid, params);
					gridView.addView(rightGrid, params);
					leftGrid.setGrid( ((ColossalDamageGrid) element.getHitpoints()).getLeftGrid());
					leftGrid.setEdit(false);
					rightGrid.setGrid( ((ColossalDamageGrid) element.getHitpoints()).getRightGrid());
					rightGrid.setEdit(false);
				}
			} 
			
			if (element.getHitpoints() instanceof WarbeastDamageSpiral ) {
				DamageSpiralView spiralView = new DamageSpiralView(getActivity());
				spiralView.setSpiral((WarbeastDamageSpiral)element.getHitpoints());
				spiralView.setEdit(false);
				gridHolder.addView(spiralView);
			}

			
		
		// force redraw?
		container.invalidate();
		
	}

	/**
	 * feeds the baseStats grid with values from army element
	 * 
	 * @param baseStatsView
	 * @param element
	 */
	private void generateBaseStatView(View baseStatsView, SingleModel element) {
		TextView name = (TextView) baseStatsView.findViewById(R.id.carac_name);
		TextView spd = (TextView) baseStatsView
				.findViewById(R.id.carac_spd_value);
		TextView str = (TextView) baseStatsView
				.findViewById(R.id.carac_str_value);
		TextView mat = (TextView) baseStatsView
				.findViewById(R.id.carac_mat_value);
		TextView rat = (TextView) baseStatsView
				.findViewById(R.id.carac_rat_value);
		TextView def = (TextView) baseStatsView
				.findViewById(R.id.carac_def_value);
		TextView arm = (TextView) baseStatsView
				.findViewById(R.id.carac_arm_value);
		TextView cmd = (TextView) baseStatsView
				.findViewById(R.id.carac_cmd_value);
	
		name.setText(element.getName());
		spd.setText(convertCaracToString(element.getSPD()));
		str.setText(convertCaracToString(element.getSTR()));
		mat.setText(convertCaracToString(element.getMAT()));
		rat.setText(convertCaracToString(element.getRAT()));
		def.setText(convertCaracToString(element.getDEF()));
		arm.setText(convertCaracToString(element.getARM()));
		cmd.setText(convertCaracToString(element.getCMD()));
	
		// generate special capacities icons
		LinearLayout titleLinearLayout = (LinearLayout) baseStatsView
				.findViewById(R.id.model_title_linear_layout);
		titleLinearLayout.removeAllViews();

		if (element.isAmphibious()) {
			addCapacityImage(titleLinearLayout, R.drawable.amphibious_icon, R.string.amphibious, R.string.desc_amphibious);
		}
		if (element.isAdvanceDeployment()) {
			addCapacityImage(titleLinearLayout, R.drawable.advance_deployment_icon, R.string.advance_deployment, R.string.desc_advance_deploy);
		}
		if (element.isArcNode()) {
			addCapacityImage(titleLinearLayout, R.drawable.arcnode_icon, R.string.arc_node, R.string.desc_arc_node);
		}
		if (element.isAssault()) {
			addCapacityImage(titleLinearLayout, R.drawable.assult_icon, R.string.assault, R.string.desc_assault);
		}
		if (element.isCavalry()) {
			addCapacityImage(titleLinearLayout, R.drawable.cavalry_icon, R.string.cavalry, R.string.desc_cavalry);
		}
		if (element.isCra()) {
			addCapacityImage(titleLinearLayout, R.drawable.cra_icon, R.string.cra,  R.string.desc_cra);
		}
		if (element.isCma()) {
			addCapacityImage(titleLinearLayout, R.drawable.cma_icon, R.string.cma,  R.string.desc_cma);
		}
		if (element.isConstruct()) {
			addCapacityImage(titleLinearLayout, R.drawable.construct_icon, R.string.construct, R.string.desc_construct);
		}
		if (element.isEyelessSight()) {
			addCapacityImage(titleLinearLayout, R.drawable.eyeless_sight, R.string.eyeless, R.string.desc_eyeless);
		}
		if (element.isFlight()) {
			addCapacityImage(titleLinearLayout, R.drawable.flight_icon, R.string.flight, R.string.desc_flight);
		}
		if (element.isGunfighter()) {
			addCapacityImage(titleLinearLayout, R.drawable.gunfighter, R.string.gunfighter, R.string.desc_gunfighter);
		}
		if (element.isIncorporeal()) {
			addCapacityImage(titleLinearLayout, R.drawable.incorporeal_icon, R.string.incorporeal, R.string.desc_incorporeal);
		}
		if (element.isJackMarshal()) {
			addCapacityImage(titleLinearLayout, R.drawable.jack_marshal_icon, R.string.jack_marshal, R.string.desc_jack_marshal);
		}
		if (element.isOfficer()) {
			addCapacityImage(titleLinearLayout, R.drawable.officer_icon, R.string.officer, R.string.desc_officer);
		}
		if (element.isParry()) {
			addCapacityImage(titleLinearLayout, R.drawable.parry_icon, R.string.parry, R.string.desc_parry);
		}
		if (element.isPathfinder()) {
			addCapacityImage(titleLinearLayout, R.drawable.pathfinder_icon, R.string.pathfinder, R.string.desc_pathfinder);
		}
		if (element.isSoulless()) {
			addCapacityImage(titleLinearLayout, R.drawable.soulless_icon, R.string.soulless, R.string.desc_soulless);
		}
		if (element.isStandardBearer()) {
			addCapacityImage(titleLinearLayout, R.drawable.standard_bearer_icon, R.string.standard_bearer, R.string.standard_bearer);
		}
		if (element.isStealth()) {
			addCapacityImage(titleLinearLayout, R.drawable.stealth_icon, R.string.stealth, R.string.desc_stealth);
		}
		if (element.isTough()) {
			addCapacityImage(titleLinearLayout, R.drawable.tough_icon, R.string.tough, R.string.desc_tough);
		}
		if (element.isUndead()) {
			addCapacityImage(titleLinearLayout, R.drawable.undead_icon, R.string.undead, R.string.desc_undead);
		}
		if (element.isImmunityFire()) {
			addCapacityImage(titleLinearLayout, R.drawable.immunity_fire_icon, R.string.immunity_fire, R.string.desc_immune_fire);
		}
		if (element.isImmunityCorrosion()) {
			addCapacityImage(titleLinearLayout, R.drawable.immunity_corrosion_icon, R.string.immunity_corrosion, R.string.desc_immune_corrosion);
		}
		if (element.isImmunityElectricity()) {
			addCapacityImage(titleLinearLayout, R.drawable.immunity_electricity_icon, R.string.immunity_electricity, R.string.desc_immune_electricity);
		}
		if (element.isImmunityFrost()) {
			addCapacityImage(titleLinearLayout, R.drawable.immunity_frost_icon, R.string.immunity_frost, R.string.desc_immune_cold);
		}
		if (element.getWeapons().isEmpty()) {
			addCapacityImage(titleLinearLayout, R.drawable.no_melee_icon, R.string.no_weapon, R.string.no_weapon);
		}
		
	}

	/**
	 * feeds the melee weapon grid with weapon caracs and return new View <br>
	 * caller must add the view to the underlying layout
	 * 
	 * @param weapon
	 * @return View
	 */
	private View generateMeleeWeaponView(MeleeWeapon weapon) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		if (D) Log.d(TAG, "--- inflating melee weapon : Start");
		View meleeWeaponView = inflater.inflate(R.layout.melee_weapon, null,
				true);
		if (D) Log.d(TAG, "--- inflating melee weapon : end");
	
		generateWeaponIcons(weapon, meleeWeaponView);
	
		// weapon POW and P+S
		TextView weaponTitle = (TextView) meleeWeaponView
				.findViewById(R.id.text_weapon_name);
		TextView weaponRng = (TextView) meleeWeaponView
				.findViewById(R.id.carac_rng_value);
		TextView weaponPow = (TextView) meleeWeaponView
				.findViewById(R.id.carac_pow_value);
		TextView weaponP_plus_s = (TextView) meleeWeaponView
				.findViewById(R.id.carac_p_plus_s_value);
		TextView weaponCapacities = (TextView) meleeWeaponView
				.findViewById(R.id.weaponCapacitiesText);
	
		generateWeaponName(weapon, weaponTitle);
		
		generationWeaponLocationCount(weapon, meleeWeaponView);

	
		generateWeaponCapacities(weapon, weaponCapacities);

		if ("*".equals(weapon.getRange())) {
			weaponRng.setText("*");
		} else {
			weaponRng.setText("0.5".equals(weapon.getRange())?"½\"": weapon.getRange()+"\"");
		}

		weaponPow.setText(weapon.getPow());
		weaponP_plus_s.setText(weapon.getP_plus_s());
	
		if (D) Log.d(TAG, "--- melee weapon generated");
		return meleeWeaponView;
	}

	private void generationWeaponLocationCount(Weapon weapon,
			View meleeWeaponView) {
		TextView weaponLocation = (TextView) meleeWeaponView
				.findViewById(R.id.tvLocation);
		TextView weaponCount = (TextView) meleeWeaponView
				.findViewById(R.id.tvCount);

		if (weapon.hasLocation()) {
			weaponLocation.setText(weapon.getLocation());
		} else {
			weaponLocation.setVisibility(View.INVISIBLE);
		}
		
		if (weapon.getCount() > 1) {
			weaponCount.setText('\u00D7' + String.valueOf(weapon.getCount()));
		} else {
			weaponCount.setVisibility(View.INVISIBLE);
		}
	}

	private void generateWeaponCapacities(Weapon weapon,
			TextView weaponCapacities) {
		StringBuffer weaponCapacitiesBf = new StringBuffer(256);
		if (! weapon.getCapacities().isEmpty() ) {
			for (Capacity capacity : weapon.getCapacities()) {
				weaponCapacitiesBf.append("<B>").append(capacity.getTitle()).append("</B>");
				if (capacity.getType() !=null && capacity.getType().length() > 0) {
					weaponCapacitiesBf.append("[").append(capacity.getType().replace('*', '\u2605')).append("]");
				}
				String capacityFormated = capacity.getLabel().replace("\n", "<BR>");

				weaponCapacitiesBf.append(" - ").append(capacityFormated).append("<BR>");
			}
			weaponCapacities.setText(Html.fromHtml(weaponCapacitiesBf.toString()));
			
			weaponCapacities.setTag(weapon);
			weaponCapacities.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Weapon weapon = (Weapon) v.getTag();
					AlertDialog.Builder weaponDialog = new AlertDialog.Builder(getActivity());
					weaponDialog.setTitle(weapon.getName());
					
					StringBuffer weaponCapacitiesBf = new StringBuffer(256);
					for (Capacity capacity : weapon.getCapacities()) {
						weaponCapacitiesBf.append("<B>").append(capacity.getTitle()).append("</B>");
						if (capacity.getType() !=null) {
							weaponCapacitiesBf.append("[").append(capacity.getType().replace('*', '\u2605')).append("]");
						}
						String capacityFormated = capacity.getLabel().replace("\n", "<BR>");
						weaponCapacitiesBf.append(" - ").append(capacityFormated).append("<BR>");
					}
					
					weaponDialog.setMessage(Html.fromHtml(weaponCapacitiesBf.toString()));
					weaponDialog.show();
				}
			});
			
			
		} else {
			weaponCapacities.setVisibility(View.GONE);	
		}
	}

	/**
	 * feeds the mount weapon grid with weapon caracs and return new View <br>
	 * caller must add the view to the underlying layout
	 * 
	 * @param weapon
	 * @return View
	 */
	
	private View generateMountWeaponView(MountWeapon weapon) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		View mountWeaponView = inflater.inflate(R.layout.mount_weapon, null,
				true);
		TextView weaponTitle = (TextView) mountWeaponView
				.findViewById(R.id.text_weapon_name);
		TextView weaponRng = (TextView) mountWeaponView
				.findViewById(R.id.carac_rng_value);
		TextView weaponPow = (TextView) mountWeaponView
				.findViewById(R.id.carac_pow_value);
		TextView weaponCapacities = (TextView) mountWeaponView
				.findViewById(R.id.weaponCapacitiesText);
		
		
		generateWeaponName(weapon, weaponTitle);
		generateWeaponCapacities(weapon, weaponCapacities);

		weaponRng.setText(weapon.getRange());
		weaponPow.setText(weapon.getPow());
	
		generateWeaponIcons(weapon, mountWeaponView);
	
		return mountWeaponView;
	}

	/**
	 * feeds the ranged weapon grid with weapon caracs and return new View <br>
	 * caller must add the view to the underlying layout
	 * 
	 * @param weapon
	 * @return View
	 */
	
	private View generateRangedWeaponView(RangedWeapon weapon) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		View rangedWeaponView = inflater.inflate(R.layout.ranged_weapon, null,
				true);
		TextView weaponTitle = (TextView) rangedWeaponView
				.findViewById(R.id.text_weapon_name);
		TextView weaponRng = (TextView) rangedWeaponView
				.findViewById(R.id.carac_rng_value);
		TextView weaponRof = (TextView) rangedWeaponView
				.findViewById(R.id.carac_rof_value);
		TextView weaponAoe = (TextView) rangedWeaponView
				.findViewById(R.id.carac_aoe_value);
		TextView weaponPow = (TextView) rangedWeaponView
				.findViewById(R.id.carac_pow_value);
		TextView weaponCapacities = (TextView) rangedWeaponView
				.findViewById(R.id.weaponCapacitiesText);
		
		generateWeaponName(weapon, weaponTitle);
	
		generateWeaponCapacities(weapon, weaponCapacities);
		
    	weaponRng.setText(weapon.getRange());
		weaponRof.setText(weapon.getRof());
		weaponAoe.setText(weapon.getAoe());
		weaponPow.setText(weapon.getPow());
	
		generationWeaponLocationCount(weapon, rangedWeaponView);
		
		generateWeaponIcons(weapon, rangedWeaponView);
	
		return rangedWeaponView;
	}

	/**
	 * generate weapon icons in the weapon view.
	 * 
	 * @param weapon
	 * @param weaponView
	 */
	private void generateWeaponIcons(Weapon weapon, View weaponView) {
		// remove every weapon icons from linear layout
		LinearLayout weapon1TitleLayout = (LinearLayout) weaponView
				.findViewById(R.id.weapon_title_linear_layout);
		weapon1TitleLayout.removeAllViews();
	
		// add weapon icons to linear layout
		if (weapon.isMagical()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.magical_weapon_icon, R.string.magical, R.string.desc_weapon_magical);
		}
		if (weapon instanceof MeleeWeapon) {
			if (((MeleeWeapon) weapon).isOpenFist()) {
				addWeaponImage(weapon1TitleLayout, R.drawable.open_fist_icon, R.string.open_fist, R.string.desc_weapon_open_fist);
			}
			if (((MeleeWeapon) weapon).isChain()) {
				addWeaponImage(weapon1TitleLayout, R.drawable.chain_weapon, R.string.chain_weapon, R.string.desc_weapon_chain);
			}
		}
		if (weapon.isShield()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.shield_icon, R.string.shield, R.string.desc_weapon_shield);
		}
		if (weapon.isBlessed()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.blessed_icon, R.string.blessed, R.string.desc_weapon_blessed);
		}
		if (weapon.isBuckler()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.buckler_icon, R.string.buckler, R.string.desc_weapon_buckler);
		}
		if (weapon.isFire()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.fire_icon, R.string.fire, R.string.desc_weapon_fire);
		}
		if (weapon.isContinuousFire()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.continuous_fire_icon, R.string.continuous_fire, R.string.desc_weapon_cont_fire);
		}
		if (weapon.isCorrosion()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.corrosion_icon, R.string.corrosion, R.string.desc_weapon_corrosion);
		}
		if (weapon.isContinuousCorrosion()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.continuous_corrosion_icon, R.string.continuous_corrosion, R.string.desc_weapon_cont_corrosion);
		}
		if (weapon.isDisrupt()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.disrupt_icon, R.string.disrupt, R.string.desc_weapon_disruption);
		}
		if (weapon.isCriticalDisrupt()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.critical_disrupt_icon, R.string.critical_disruption, R.string.des_weapon_crit_disruption);
		}
		if (weapon.isWeaponMaster()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.weapon_master_icon, R.string.weapon_master, R.string.desc_weapon_weapon_master);
		}
		if (weapon.isCriticalFire()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.critical_fire_icon, R.string.critical_fire, R.string.desc_weapon_crit_fire);
		}
		if (weapon.isCriticalCorrosion()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.critical_corrosion_icon, R.string.critical_corrosion, R.string.desc_weapon_crit_corrosion);
		}
		if (weapon.isFrost()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.frost, R.string.frost, R.string.desc_weapon_cold);
		}
		if (weapon.isElectricity()) {
			addWeaponImage(weapon1TitleLayout, R.drawable.electricity_icon, R.string.electricity, R.string.desc_weapon_electricity);
		}
	}

	private void generateWeaponName(Weapon weapon, TextView weaponTitle) {
		StringBuffer weaponName = new StringBuffer();
//		if (weapon.hasLocation()) {
//			weaponName.append("[").append(weapon.getLocation()).append("] ");
//		}
		weaponName.append(weapon.getName());
//		if (weapon.getCount() > 1) {
//			weaponName.append("[x").append(weapon.getCount()).append("] ");
//		}
		weaponTitle.setText(weaponName.toString());
	}

	private void createSpellPopup(View v) {
		Spell spell = (Spell) v.getTag();
		AlertDialog.Builder spellDialog = new AlertDialog.Builder(getActivity());
		spellDialog.setTitle(spell.getTitle());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View spellView = (View) inflater.inflate(R.layout.spells, null);
		TableLayout spellTable = (TableLayout) spellView.findViewById(R.id.spellTable);
		spellTable.removeView(spellTable.findViewById(R.id.toRemove1));
		spellTable.removeView(spellTable.findViewById(R.id.toRemove2));
		
		View spellLineCaracView = getLayoutInflater(null).inflate(R.layout.spell_line_carac_dialog, null, false);	
		((TextView) spellLineCaracView.findViewById(R.id.spellTitle)).setText(spell.getTitle());
		((TextView) spellLineCaracView.findViewById(R.id.spellCost)).setText(spell.getCost()); 
		((TextView) spellLineCaracView.findViewById(R.id.spellRange)).setText(spell.getRange());
		((TextView) spellLineCaracView.findViewById(R.id.spellAOE)).setText(spell.getAoe());
		((TextView) spellLineCaracView.findViewById(R.id.spellPOW)).setText(spell.getPow());
		((TextView) spellLineCaracView.findViewById(R.id.spellUP)).setText(spell.getDuration());
		((TextView) spellLineCaracView.findViewById(R.id.spellOFF)).setText(spell.getOffensive());
		spellTable.addView(spellLineCaracView);
		
		View spellLineTextView = getLayoutInflater(null).inflate(R.layout.spell_line_text_dialog, null, false);
		((TextView) spellLineTextView.findViewById(R.id.spellDescription)).setText(Html.fromHtml(spell.getFullText()));
		spellTable.addView(spellLineTextView);
		
		
		spellDialog.setView(spellView);
		spellDialog.setTitle(spell.getTitle());
		spellDialog.setIcon(R.drawable.magical_weapon_icon);
		spellDialog.show();
	}

}
