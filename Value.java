import java.util.*;
import java.io.*;

public class Value {
	public double value;
	public String units;
	
	public Value(double value, String units) {
		this.value = value;
		this.units = units;
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
		Value returnValue = new Value(Double.parseDouble(numericalValue), unitsValue);
		if (!checkTrue) {
			return returnValue;
		}
		if (returnValue.isValid()) {
			return returnValue;
		} else {
			return null;
		}
	}
	
	public void printValue() {
		System.out.print(this.value + this.units);
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
}