/**<ul>
 * <li>PremierTPInterParisMai</li>
 * <li>com.android2ee.formation.mai.mmxiii.premiertp</li>
 * <li>13 mai 2013</li>
 *
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
/**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br>
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 * Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 *
 * *****************************************************************************************************************</br>
 * Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 * Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br>
 * Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.schlaf.steam.adapters;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schlaf.steam.R;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals This class aims to the arrayAdpater for listViews that display
 *        BlueTooth device
 */
public class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

	/**
	 * Layout inflater
	 */
	LayoutInflater inflater;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the context
	 * @param objects
	 *            the list of objects to display
	 */
	public BluetoothDevicesAdapter(Context context,
			List<BluetoothDevice> objects) {
		super(context, R.layout.bluetooth_device_line, objects);
		inflater = LayoutInflater.from(getContext());
	}

	// Avoid using temp variable as method's variable
	private static BluetoothDevice device;
	private static View myview;
	private static ViewHolder viewHolder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		device = getItem(position);
		myview = convertView;
		if (null == myview) {
				myview = inflater.inflate(R.layout.bluetooth_device_line, null);
			viewHolder = new ViewHolder(myview);
			myview.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) myview.getTag();
		}
		
		String bondStatus = "";
		switch (device.getBondState()) {
			case BluetoothDevice.BOND_BONDED : 
				bondStatus = "Bonded";
				break;
			case BluetoothDevice.BOND_NONE : 
				bondStatus = "Not yet bonded";
				break;
			case BluetoothDevice.BOND_BONDING : 
				bondStatus = "Bonding...";
				break;

		}
		
		String detailText = device.getAddress() + "(" + bondStatus +")";
		
		viewHolder.getTxvDetails().setText(detailText);
		viewHolder.getTxvName().setText(device.getName());

		return myview;
	}


	/******************************************************************************************/
	/** ViewHolder **************************************************************************/
	/******************************************************************************************/

	public static class ViewHolder {
		View boundView;
		TextView txvName;
		TextView txvDetails;

		/**
		 * @param boundView
		 */
		private ViewHolder(View boundView) {
			super();
			this.boundView = boundView;
		}

		/**
		 * @return the txvName
		 */
		public final TextView getTxvName() {
			if (null == txvName) {
				txvName = (TextView) boundView.findViewById(R.id.nom);
			}
			return txvName;
		}

		/**
		 * @param txvName
		 *            the txvName to set
		 */
		public final void setTxvName(TextView txvName) {
			this.txvName = txvName;
		}

		/**
		 * @return the txvAdress
		 */
		public final TextView getTxvDetails() {
			if (null == txvDetails) {
				txvDetails = (TextView) boundView.findViewById(R.id.message);
			}
			return txvDetails;
		}

		/**
		 * @param txvAdress
		 *            the txvAdress to set
		 */
		public final void setTxvDetails(TextView txvDetails) {
			this.txvDetails = txvDetails;
		}

	}

}