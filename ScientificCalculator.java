import java.util.*;
// import javax.lang.model.element.*;
// import javax.net.ssl.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		int answer;
		try {
			answer = Integer.parseInt(getUserInput("Choose a function to use:\n\t0) Exit program\n\t1) Conversion with the same type of unit\n\t2) Physics motion equations"));
		} catch (NumberFormatException e) {
			answer = -1;
		}
		System.out.println();
		switch (answer) {
			case 0:
				return;
			case 1:
				SingleConversion();
				break;
			case 2:
				Kinematics();
				break;
			case 3:
				String[] testPieces = IsolatePieces("Dv = Vf - Vi + 2a(Dt)2");
				for (String i : testPieces) {
					System.out.println(i);
				}
				break;
			default:
				System.out.println("Invalid, please try again");
				break;
		}
		System.out.println();
		String[] nullArgs = {};
		main(nullArgs);
		/* Value value = Value.getValue("10.183N*s", true);
		Value def = value.getUnitDefinition();
		value.value *= def.value;
		value.units = def.units;
		value.printValue(true); */
	}
	
	public static void SingleConversion() {
		String[] input = getUserInputLine("Enter a value and a unit to convert it to\n(separated by a space: eg. 4.5mi m)");
		if (input.length == 2) {
			Value inputValue = Value.getValue(input[0], true);
			if (inputValue == null) {
				System.out.print("Invalid known value");
			} else {
				Value unitDef = inputValue.getUnitDefinition();
				Value siValue = new Value(inputValue.value * unitDef.value, unitDef.units, Math.min(inputValue.sigFigs, unitDef.sigFigs));
				Value unitToConvert = Value.getUnitDefinition(input[1], inputValue.sigFigs);
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
		String unknown = getUserInput("Motion data: Enter your unknown variable, then your known variables (for help, press 1)");
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
			String[] equations = {"Dx = Xf - Xi", "v = (Xf - Xi)/Dt", "v = Dx/Dt", "Dv = Vf - Vi", "a = (Vf - Vi)/Dt", "a = Dv/Dt", "Xf = Xi + Vi*Dt + a*(Dt)2/2", "Dx = Vi*Dt + a*(Dt)2/2", "Vf2 = Vi2 + 2*a*Dx", "Vf2 = Vi2 + 2*a*(Xf - Xi)"};
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
	static String[] getUserInputLine(String message) {
		Scanner input = new Scanner(System.in);
		System.out.println(message);
		String line = input.nextLine();
		return line.split(" ");
	}
	
	// Obtains user input
	static String getUserInput(String message) {
		Scanner input = new Scanner(System.in);
		System.out.println(message);
		String line = input.nextLine();
		return line;
	}
	
	// Manipulates equations to isolate unknowns
	static Value AlgebraEquations(String equation, String unknown, Value[] values) {
		Value answer = new Value(1, "", 1);
		if (!equation.contains(unknown)) {
			return null;
		}
		if (unknown == null) {
			return null;
		}
		String[] pieces = IsolatePieces(equation);
		String[] equationLeft = new String[pieces.length];
		String[] equationRight = new String[pieces.length];
		int index = 0;
		boolean rightSide = false;
		boolean unknownLeft = false;
		for (String piece : pieces) {
			if (piece.equals("=")) {
				index = 0;
				rightSide = true;
				continue;
			}
			if (rightSide) {
				equationRight[index] = piece;
			} else {
				equationLeft[index] = piece;
				if (piece.contains(unknown)) {
					unknownLeft = true;
				}
			}
			index++;
		}
		if (!unknownLeft) {
			String[] temp = equationLeft;
			equationLeft = equationRight;
			equationRight = temp;
		}
		for (String switchPiece : equationLeft) {
			if (switchPiece == null) {
				continue;
			}
			if (switchPiece.contains(unknown)) {
				continue;
			}
			if (switchPiece.charAt(0) == '-') {
				switchPiece = switchPiece.substring(1, switchPiece.length());
			} else {
				switchPiece = "-" + switchPiece;
			}
		}
		System.out.print(equationLeft[0] + " =");
		for (String right : equationRight) {
			if (right == null) {
				continue;
			}
			System.out.print(" " + right);
		}
		System.out.println();
		return answer;
	}
	
	// Split an equation into algebraic pieces
	static String[] IsolatePieces(String equation) {
		String[] separateBySpace = equation.split(" ");
		String currentPiece = "";
		String[] pieces = new String[separateBySpace.length];
		int parentheses = 0;
		int piecesIndex = 0;
		boolean isNegative = false;
		for (String piece : separateBySpace) {
			parentheses += countSubstrings(piece, "(");
			parentheses -= countSubstrings(piece, ")");
			if (parentheses > 0) {
				currentPiece += piece + " ";
			} else {
				if (piece.equals("+")) {
					continue;
				}
				if (piece.equals("-")) {
					isNegative = true;
					continue;
				}
				currentPiece += piece;
				if (isNegative) {
					isNegative = false;
					currentPiece = "-" + currentPiece;
				}
				pieces[piecesIndex] = currentPiece;
				currentPiece = "";
				piecesIndex++;
			}
		}
		String[] returnString = new String[piecesIndex];
		for (int i = 0; i < piecesIndex; i++) {
			returnString[i] = pieces[i];
		}
		return returnString;
	}
	
	// Count how many times a certain substring appears
	public static int countSubstrings(String text, String substring) {
		int count = 0;
		int index = 0;
		
		if (substring.isEmpty()) {
			return 0;
		}
		
		while ((index = text.indexOf(substring, index)) != -1) {
			count++;
			index += substring.length(); // Move past the last found occurrence
		}
		return count;
	}
	
	// Prints output of unit conversion calculations
	static void printConversionEquation(Value known, Value unknown, boolean withSigFigs) {
		known.printValue(withSigFigs);
		System.out.print(" = ");
		unknown.printValue(withSigFigs);
		System.out.println();
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