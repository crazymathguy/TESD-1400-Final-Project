import java.util.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		// String[] input = {};
		String[] input = getUserInput("Enter a value and a unit to convert it to\n(separated by a space: ex. 4.5mi m)");
		
		// Sig figs test
		/* System.out.println(Value.formatWithSigFigs(8725.8207, 10));
		System.out.println(Value.formatWithSigFigs(500., 1));
		System.out.println(Value.formatWithSigFigs(500., 3));
		System.out.println(Value.formatWithSigFigs(500., 5));
		System.out.println(Value.formatWithSigFigs(.032, 1));
		System.out.println(Value.formatWithSigFigs(.032, 2));
		System.out.println(Value.formatWithSigFigs(.032, 4));
		System.out.println(Value.formatWithSigFigs(8725.8207, 4));
		// end test */
		
		if (input.length == 2) {
			Value inputValue = Value.getValue(input[0], true);
			if (inputValue == null) {
				System.out.print("Invalid known value");
			} else {
				Value unitDef = inputValue.getUnitDefinition();
				Value siValue = new Value(inputValue.value * unitDef.value, unitDef.units, Math.min(inputValue.sigFigs, unitDef.sigFigs));
				Value unitToConvert = Value.getUnitDefinition(input[1]);
				if (unitToConvert == null) {
					System.out.print("Invalid unknown value");
				} else {
					if (siValue.units.equals(unitToConvert.units)) {
						Value convertedValue = new Value(siValue.value / unitToConvert.value, input[1], Math.min(siValue.sigFigs, unitToConvert.sigFigs));
						if (convertedValue.isValid()) {
							printEquation(inputValue, convertedValue, true);
						} else {
							System.out.print("Something went wrong, please try again.");
						}
					} else {
						System.out.print("Incompatible units");
					}
				}
			}
		} else {
			// System.out.print("Invalid equation");
		}
		
		/* for (int i = 0; i < input.length; i++) {
			Value value = getValue(input[i]);
			System.out.println(value.value + " " + value.units);
		} */
	}
	
	// Obtains user input
	static String[] getUserInput(String message) {
		Scanner input = new Scanner(System.in);
		System.out.println(message);
		String line = input.nextLine();
		return line.split(" ");
	}
	
	// Prints output of unit conversion calculations
	static void printEquation(Value known, Value unknown, boolean withSigFigs) {
		known.printValue(withSigFigs);
		System.out.print(" = ");
		unknown.printValue(withSigFigs);
	}
	
	//static void getSigFigs(String)
}