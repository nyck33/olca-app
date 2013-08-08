/*
 * DISCLAIMER
 * 
 * Code is provided on an "as is" basis, without warranty of any kind, 
 * either expressed or implied, including, without limitation, warranties 
 * that the covered code is free of defects, merchantable, fit for a 
 * particular purpose or non-infringing. The entire risk as to the quality 
 * and performance of the covered code is with you. Should any covered 
 * code prove defective in any respect, you (not the initial developer or 
 * any other contributor) assume the cost of any necessary servicing, 
 * repair or correction. No use of any covered code is authorized hereunder 
 * except under this disclaimer.
 * 
 * The initial developer of the original code is GreenDeltaTC GmbH, 
 * http://www.greendeltatc.com.
 * 
 * Portions created by the initial developer are copyright 
 * (c) 2010 GreenDeltaTC GmbH and Evea Evaluation et accompagnement, 
 * http://www.evea-conseil.com. All rights reserved.
 * 
 * Contributor(s):
 * Michael Srocka <srocka@greendeltatc.com> (original author)
 */

package org.openlca.core.editors.charts;

import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.swt.graphics.RGB;
import org.openlca.core.application.FaviColor;

/**
 * Defines methods for pre-defined colors.
 * 
 * @author Michael Srocka
 * 
 */
public class ChartFaviColor {

	public static ColorDefinition getGray() {
		return getColor(128, 128, 128);
	}

	public static ColorDefinition getColor(String hexStr) {
		if (hexStr == null || hexStr.length() != 6)
			return getColor(0, 0, 0);
		String def = hexStr.toLowerCase();
		String rDef = def.substring(0, 2);
		String gDef = def.substring(2, 4);
		String bDef = def.substring(4, 6);
		int red = getInt(rDef);
		int green = getInt(gDef);
		int blue = getInt(bDef);
		return getColor(red, green, blue);
	}

	private static int getInt(String hex) {
		int base = 0;
		int rest = 0;
		try {
			base = getInt(hex.charAt(0));
			rest = getInt(hex.charAt(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int c = base * 16 + rest;
		return c;
	}

	private static int getInt(char c) {
		int i = 0;
		switch (c) {
		case '0':
			i = 0;
			break;
		case '1':
			i = 1;
			break;
		case '2':
			i = 2;
			break;
		case '3':
			i = 3;
			break;
		case '4':
			i = 4;
			break;
		case '5':
			i = 5;
			break;
		case '6':
			i = 6;
			break;
		case '7':
			i = 7;
			break;
		case '8':
			i = 8;
			break;
		case '9':
			i = 9;
			break;
		case 'a':
			i = 10;
			break;
		case 'b':
			i = 11;
			break;
		case 'c':
			i = 12;
			break;
		case 'd':
			i = 13;
			break;
		case 'e':
			i = 14;
			break;
		case 'f':
			i = 15;
			break;
		default:
			break;
		}
		return i;
	}

	public static ColorDefinition getColor(int red, int green, int blue) {
		ColorDefinition color = ColorDefinitionImpl.create(red, green, blue);
		color.setTransparency(220);
		return color;
	}

	public static ColorDefinition getColor(int idx) {
		RGB rgb = FaviColor.getRgbForChart(idx);
		return getColor(rgb.red, rgb.green, rgb.blue);
	}

}
