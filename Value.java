import java.util.*;
import java.io.*;

// Class to handle any physical measurements
public class Value {
	// The actual numerical value of the measurement
	public double value;
	// The units that the measurement is in
	public String units;
	// The number of significant figures (sig figs) in the numerical value
	public int sigFigs;
	
	
	// Value constructor
	public Value(double value, String units, int sigFigs) {
		this.value = value;
		this.units = units; // .replace('u', 'μ');
		this.sigFigs = sigFigs;
	}
	
	// Parses a string into a Value (separates the numerical value from the units, and counts sig figs)
	static Value getValue(String stringToParse, boolean checkValid) {
		String numericalValue = "";
		String unitsValue = "";
		for (int i = 0; i < stringToParse.length(); i++) {
			char current = stringToParse.charAt(i);
			if (Character.isLetter(current) || current == '*' || current == '/') {
				unitsValue += current;
			}
			else if (Character.isDigit(current) || current == '.') {
				numericalValue += current;
			}
		}
		if (numericalValue == "") {
			return null;
		}
		int sigFigs = getSigFigs(numericalValue);
		Value returnValue = new Value(Double.parseDouble(numericalValue), unitsValue, sigFigs);
		if (!checkValid) {
			return returnValue;
		}
		if (returnValue.isValid()) {
			return returnValue;
		} else {
			return null;
		}
	}
	
	// prints a Value as a string
	public void printValue(boolean withSigFigs) {
		String printValue;
		if (withSigFigs) {
			printValue = this.formatWithSigFigs();
		} else {
			printValue = Double.toString(this.value);
		}
		System.out.print(printValue.replace('u', 'μ') + this.units); //.replace(':', '*') + this.units);
	}
	
	// Checks whether this Value is valid
	public boolean isValid() {
		return isValidUnit(this.units);
	}
	
	// Check whether the inputted Value has a valid (currently supported) unit
	public static boolean isValidUnit(String tryUnit) {
		
		return !(getUnitDefinition(tryUnit, 1) == null);
	}
	
	public Value getUnitDefinition() {
		return getUnitDefinition(this.units, this.sigFigs);
	}
	
	// returns the SI definition of a given unit (eg. how many meters in a mile, how many grams in a pound)
	public static Value getUnitDefinition(String units, int origSigFigs) {
		if (units == null) {
			return null;
		}
		units.replace('u', 'μ');
		if (!(units.contains("/") || units.contains("*"))) {
			return getSingleUnitDefinition(units, origSigFigs);
		}
		Value definition = new Value(1, "", origSigFigs);
		String[] unitsDivision = units.split("/");
		String[][] splitUnits = new String[unitsDivision.length][];
		for (int i = 0; i < unitsDivision.length; i++) {
			splitUnits[i] = unitsDivision[i].split("\\*");
		}
		String tempBottomUnits = "";
		for (String currentUnit : splitUnits[0]) {
			Value singleUnitValue = getSingleUnitDefinition(currentUnit, origSigFigs);
			if (singleUnitValue == null) {
				return null;
			}
			if (singleUnitValue.units.contains("/")) {
				String[] complexUnit = singleUnitValue.units.split("/", 2);
				singleUnitValue.units = complexUnit[0];
				tempBottomUnits += complexUnit[1] + "*";
			}
			definition.value *= singleUnitValue.value;
			definition.units += singleUnitValue.units + "*";
			definition.sigFigs = Math.min(definition.sigFigs, singleUnitValue.sigFigs);
		}
		definition.units = definition.units.substring(0, definition.units.length() - 1);
		definition.units += "/";
		if (tempBottomUnits.length() > 0) {
			definition.units += tempBottomUnits;
			tempBottomUnits = "";
		}
		for (int dividedUnits = 1; dividedUnits < splitUnits.length; dividedUnits++) {
			if (dividedUnits != 1) {
				definition.units += "*";
			}
			for (String currentUnit : splitUnits[dividedUnits]) {
				Value singleUnitValue = getSingleUnitDefinition(currentUnit,  origSigFigs);
				if (singleUnitValue == null) {
					return null;
				}
				if (singleUnitValue.units.contains("/")) {
					String[] complexUnit = singleUnitValue.units.split("/", 2);
					singleUnitValue.units = complexUnit[0];
					tempBottomUnits += complexUnit[1] + "*";
				}
				definition.value /= singleUnitValue.value;
				definition.units += singleUnitValue.units + "*";
				definition.sigFigs = Math.min(definition.sigFigs, singleUnitValue.sigFigs);
			}
			definition.units = definition.units.substring(0, definition.units.length() - 1);
		}
		if (definition.units.charAt(definition.units.length() - 1) == '*') {
			definition.units = definition.units.substring(0, definition.units.length() - 1);
		}
		if (definition.units.charAt(definition.units.length() - 1) == '/') {
			definition.units = definition.units.substring(0, definition.units.length() - 1);
		}
		if (tempBottomUnits.length() > 0) {
			String[] lastSplitUnits = definition.units.split("/", 2);
			if (lastSplitUnits.length > 1) {
				lastSplitUnits[0] += (lastSplitUnits[0].length() > 0 ? "*" : "") + tempBottomUnits;
				lastSplitUnits[0] = lastSplitUnits[0].substring(0, lastSplitUnits[0].length() - 1);
				definition.units = lastSplitUnits[0] + "/" + lastSplitUnits[1];
			} else {
				definition.units += (definition.units.length() > 0 ? "*" : "") + tempBottomUnits;
			}
		}
		return definition;
	}
		
	public static Value getSingleUnitDefinition(String units, int origSigFigs) {
		String line;
		int power = 0;
		line = getLine(units);
		if (line == null) {
			if (units.length() <= 1) {
				return null;
			}
			power = getUnitPrefix(units);
			if (power == 0) {
				return null;
			}
			units = units.substring(1);
			line = getLine(units);
			if (line == null) {
				return null;
			}
		}
		String[] def = line.split(" ");
		Value returnValue = getValue(def[2], false);
		if (!(def[2].contains("."))) {
			returnValue.sigFigs = origSigFigs;
		}
		returnValue.value *= Math.pow(10, power);
		return returnValue;
	}
	
	// determines what prefix the unit has and returns it as the power of ten that it represents (eg. km -> 3, ns -> -9)
	public static int getUnitPrefix(String units) {
		String prefix = units.substring(0, 1);
		try {
			File file = new File("PrefixData.txt");
			Scanner inputFile = new Scanner(file);
			while (inputFile.hasNext()) {
				String line = inputFile.nextLine();
				if (line.startsWith(prefix)) {
					inputFile.close();
					int power = Integer.parseInt(line.split(" ")[2]);
					return power;
				}
			}
			inputFile.close();
		} catch (Exception e) {}
		return 1;
	}
	
	// finds the line in the UnitData file that matches the given units, no match returns null
	public static String getLine(String units) {
		try {
			File file = new File("UnitData.txt");
			Scanner inputFile = new Scanner(file);
			while (inputFile.hasNext()) {
				String line = inputFile.nextLine();
				if (line.startsWith(units)) {
					inputFile.close();
					return line;
				}
			}
			inputFile.close();
		} catch (Exception e) {}
		return null;
	}
	
	// calculates the number of significant figures
	public static int getSigFigs(String value) {
		boolean startZeros = true;
		boolean hasDecimalPoint = value.contains(".");
		int sigFigs = 0;
		int numberOfZeros = 0;
		for (int i = 0; i < value.length(); i++) {
			char current = value.charAt(i);
			if (Character.isDigit(current)) {
				if (current == '0' && !hasDecimalPoint) {
					numberOfZeros++;
				} else {
					numberOfZeros = 0;
				}
				if (!startZeros || current != '0') {
					startZeros = false;
					sigFigs++;
				}
			}
		}
		return sigFigs - numberOfZeros;
	}
	
	public String formatWithSigFigs() {
		return formatWithSigFigs(this.value, this.sigFigs);
	}
	
	// returns this Value as a string with the correct number of sig figs (rounds to number of sig figs or adds trailing 0s)
	public static String formatWithSigFigs(double value, int sigFigs) {
		if (sigFigs < 1) {
			return null;
		}
		if (sigFigs == Integer.MAX_VALUE) {
			
		}
		String inputString = Double.toString(value);
		String formattedString = "";
		boolean decimalPoint = false;
		int decimalDigits = 0;
		int i = 0;
		char current = inputString.charAt(0);
		while (current == '0' || current == '.') {
			formattedString += current;
			if (current == '.') {
				decimalPoint = true;
			} else if (decimalPoint) {
				decimalDigits++;
			}
			i++;
			if (i < inputString.length()) {
				current = inputString.charAt(i);
			}
		}
		int j = i;
		while (sigFigs > 0) {
			if (j > inputString.length() - 1) {
				formattedString += "0";
			} else {
				formattedString += inputString.charAt(j);
				if (inputString.charAt(j) == '.') {
					sigFigs++;
					decimalPoint = true;
					decimalDigits = -1;
				}
			}
			if (decimalPoint) {
				decimalDigits++;
			}
			j++;
			sigFigs--;
		}
		j--;
		if (decimalDigits == 0) {
			for (int decimals = j + 1; decimals < inputString.length() - 1; decimals++) {
				if (inputString.charAt(decimals) == '.') {
					break;
				}
				decimalDigits--;
			}
		}
		if (j < inputString.length() - 1) {
			if (decimalDigits < 1) {
				formattedString = Long.toString(Math.round(value / (Math.pow(10, -decimalDigits))) * (long) Math.pow(10, -decimalDigits));
			} else {
				formattedString = Double.toString(Math.round(value * (Math.pow(10, decimalDigits))) / Math.pow(10, decimalDigits));
			}
		}
		return formattedString;
	}
}