import java.util.*;
import java.io.*;

public class Value {
	public double value;
	public String units;
	public int sigFigs;
	
	public Value(double value, String units, int sigFigs) {
		this.value = value;
		this.units = units;
		this.sigFigs = sigFigs;
	}
	
	static Value getValue(String stringToParse, boolean checkTrue) {
		String numericalValue = "";
		String unitsValue = "";
		for (int i = 0; i < stringToParse.length(); i++) {
			char current = stringToParse.charAt(i);
			if (Character.isLetter(current)) {
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
		if (!checkTrue) {
			return returnValue;
		}
		if (returnValue.isValid()) {
			return returnValue;
		} else {
			return null;
		}
	}
	
	public void printValue(boolean withSigFigs) {
		String printValue = this.formatWithSigFigs();
		System.out.print(printValue + this.units);
	}
	
	public boolean isValid() {
		return isValidUnit(this.units);
	}
	
	public static boolean isValidUnit(String tryUnit) {
		return !(getUnitDefinition(tryUnit) == null);
	}
	
	public Value getUnitDefinition() {
		return getUnitDefinition(this.units);
	}
	
	public static Value getUnitDefinition(String units) {
		int power = 0;
		String line = getLine(units);
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
		returnValue.value *= Math.pow(10, power);
		return returnValue;
	}
	
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
	
	public static String getLine(String units) {
		try {
			File file = new File("Data.txt");
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
	
	public static String formatWithSigFigs(double value, int sigFigs) {
		String inputString = Double.toString(value);
		String formattedString = "";
		int i = 0;
		char current = inputString.charAt(0);
		while (current == '0' || current == '.') {
			formattedString += current;
			i++;
			current = inputString.charAt(i);
		}
		int j = i;
		while (j < sigFigs + i) {
			if (j > inputString.length()) {
				formattedString += "0";
			}  else {
				formattedString += inputString.charAt(j);
			}
			j++;
		}
		if (j < inputString.length()) {
			if (inputString.charAt(j + 1) > '4') {
				formattedString = formattedString.substring(0, j - 1);
			}
		}
	}
}