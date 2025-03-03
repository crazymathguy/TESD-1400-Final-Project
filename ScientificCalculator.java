import java.util.*;
import javax.lang.model.element.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		// SingleConversion();
		// Kinematics();
		Value value = Value.getValue("28.4003kg*m/s*min", true);
		Value def = value.getUnitDefinition();
		System.out.println(def.sigFigs);
		System.out.println(def.formatWithSigFigs());
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
		int variablesUsed = 0;
		int unknownVariable = 0;
		String[] variables = {"Xi", "Xf", "Dx", "Vi", "Vf", "Dv", "v", "Dt", "a"};
		Scanner input = new Scanner(System.in);
		System.out.println("Motion data: Enter your unknown variable, then your known variables (for help, enter 1)");
		String unknown = input.nextLine();
		if (unknown.equals("1")) {
			printHelp();
			Kinematics();
			return;
		} else if (validVariable(unknown, variables)) {
			Value[] values = new Value[variables.length];
			for (int i = 0; i < values.length; i++) {
				if (unknown.equals(variables[i])) {
					variablesUsed |= (1 << i);
					unknownVariable = (1 << i);
					values[i] = null;
					continue;
				}
				System.out.println(variables[i] + ": ");
				String field = input.nextLine();
				if (field.equals("")) {
					values[i] = null;
				} else {
					values[i] = Value.getValue(field, true);
					if (values[i] != null) {
						variablesUsed |= (1 << i);
					}
				}
			}
			int[] equationVariables = {0b000000111, 0b011000011, 0b011000100, 0b000111000, 0b110011000, 0b110100000, 0b110001011, 0b110001100, 0b100011100, 0b100011011};
			String[] equations = {"Dx = Xf - Xi", "v = (Xf - Xi)/Dt", "v = Dx/Dt", "Dv = Vf - Vi", "a = (Vf - Vi)/Dt", "a = Dv/Dt", "Xf = Xi + ViDt + a(Dt)2/2", "Dx = ViDt + a(Dt)2/2", "Vf2 = Vi2 + 2aDx", "Vf2 = Vi2 + 2a(Xf - Xi)"};
			for (int i = 0; i < equations.length; i++) {
				// if the variablesUsed contains at least the necessary components and the equation contains the unknown variable
				if ((variablesUsed & equationVariables[i]) == equationVariables[i] && (unknownVariable & equationVariables[i]) > 0) {
					System.out.println(equations[i]);
					Value answer = AlgebraEquations(equations[i], unknown, values);
					System.out.print(unknown + " = ");
					answer.printValue(true);
					return;
				}
			}
			System.out.println("Not enough information");
			Kinematics();
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
	static Value AlgebraEquations(String equation, String unknown, Value[] values) {
		Value answer = new Value(1, "m/s*s", 1);
		return answer;
	}
	
	// Prints output of unit conversion calculations
	static void printConversionEquation(Value known, Value unknown, boolean withSigFigs) {
		known.printValue(withSigFigs);
		System.out.print(" = ");
		unknown.printValue(withSigFigs);
	}
	
	// Prints help for kinematics
	static void printHelp() {
		System.out.println("Motion variables:");
		System.out.println("Xi = initial position, Xf = final position");
		System.out.println("Dx = delta x = change in position or distance traveled");
		System.out.println("v = velocity = speed + direction (usually direction is ignored)");
		System.out.println("Vi = initial velocity, Vf = final velocity");
		System.out.println("Dv = delta v = change in velocity");
		System.out.println("Dt = delta t = change in time (amount of time passed)");
		System.out.println("a = acceleration = change in velocity per unit time");
		System.out.println("if you do not have data for a given field, leave it blank and press enter");
	}
	
	// Determines if a variable is valid
	static boolean validVariable(String variable, String[] validVariables) {
		for (String i : validVariables) {
			if (variable.equals(i)) {
				return true;
			}
		}
		return false;
	}
}