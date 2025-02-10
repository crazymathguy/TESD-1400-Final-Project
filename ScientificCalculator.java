import java.util.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		SingleConversion();
	}
	
	public static void SingleConversion() {
		String[] input = getUserInput("Enter a value and a unit to convert it to\n(separated by a space: eg. 4.5mi m)");
		
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
							printConversionEquation(inputValue, convertedValue, true);
						} else {
							System.out.print("Something went wrong, please try again.");
						}
					} else {
						System.out.print("Incompatible units");
					}
				}
			}
		} else {
			System.out.print("Invalid equation");
		}
	}
	
	// Manipulates motion data
	static void Kinematics() {
		// String[] input = getUserInput("Motion data: Enter an unknown and as much given information as possible\n(separated by spaces: eg. Dx ");
		Scanner input = new Scanner(System.in);
		System.out.println("Motion data: Enter your unknown variable (for help, press 1)");
		String unknown = input.nextLine();
		if (unknown.equals("1")) {
			printHelp();
			Kinematics();
			return;
		}
	}
	
	// Obtains user input
	static String[] getUserInput(String message) {
		Scanner input = new Scanner(System.in);
		System.out.println(message);
		String line = input.nextLine();
		return line.split(" ");
	}
	
	// Manipulates equations to isolate unknowns
	static String[] AlgebraEquations() {
		String[] equation = {"m", "s"};
		return equation;
	}
	
	// Prints output of unit conversion calculations
	static void printConversionEquation(Value known, Value unknown, boolean withSigFigs) {
		known.printValue(withSigFigs);
		System.out.print(" = ");
		unknown.printValue(withSigFigs);
	}
	
	// Prints help for kinematics
	static void printHelp() {
		System.out.println("Motion variables");
		System.out.println("Dx = delta x = change in position");
		System.out.println("V = velocity = speed + direction (usually direction is ignored)");
		System.out.println("Vi = initial velocity, Vf = final velocity");
		System.out.println("Dt = delta t = change in time (amount of time passed)");
		System.out.println("a = acceleration = change in velocity");
	}
}