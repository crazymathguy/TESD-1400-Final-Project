public class Value {
	public double value;
	public String units;
	
	public Value(double value, String units) {
		this.value = value;
		this.units = units;
	}
	
	static Value getValue(String stringToParse) {
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
		Value returnValue = new Value(Double.parseDouble(numericalValue), unitsValue);
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
		switch (tryUnit) {
			case "m":
			case "mi":
				return true;
			default:
				return false;
		}
	}
	
	public Value getUnitDefinition() {
		return getUnitDefinition(this.units);
	}
	
	public static Value getUnitDefinition(String units) {
		if (!isValidUnit(units))
			return null;
		switch (units) {
			case "m":
				return new Value(1f, "m");
			case "mi":
				return new Value(1609f, "m");
			default:
				return null;
		}
	}
}