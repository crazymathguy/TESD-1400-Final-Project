import java.util.*;
import java.text.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		int answer;
		try {
			answer = Integer.parseInt(getUserInput("Choose a function to use:\n\t0) Exit program\n\t1) Conversion with the same type of unit\n\t2) Physics motion equations"));
		} catch (NumberFormatException e) {
			answer = -1;
		}
		System.out.println();
		String[] variables = {"Xi", "Xf", "∆x", "Vi", "Vf", "∆v", "v", "∆t", "a"};
		Value[] values = {null, null, new Value(18.0, "m", 3), new Value(5.0, "m/s", 3), null, null, null, new Value(2.0, "s", 3), new Value(4.0, "m/s*s", 3)};
		switch (answer) {
			case 0:
				return;
			case 1:
				singleConversion();
				break;
			case 2:
				kinematics();
				break;
			case 3:
				Value test = algebraEquations("∆x = Vi*∆t a*∆t2/2", "Vi", values, variables);
				test.printValue(false);
				break;
			case 4:
				Value test2 = plugInValues("Vi*∆t a*∆t2/2", values, variables, null);
				test2.printValue(true);
				break;
			default:
				System.out.println("Invalid, please try again");
				break;
		}
		System.out.println();
		String[] nullArgs = {};
		main(nullArgs);
	}
	
	static String[] removeLast(String[] input) {
		String[] output = new String[input.length - 1];
		for (int i = 0; i < output.length; i++) {
			output[i] = input[i];
		}
		return output;
	}
	
	public static void singleConversion() {
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
	static void kinematics() {
		// String[] input = getUserInput("Motion data: Enter an unknown and as much given information as possible\n(separated by spaces: eg. Dx ");
		int variablesUsed = 0;
		int unknownVariable = 0;
		String[] variables = {"Xi", "Xf", "∆x", "Vi", "Vf", "∆v", "v", "∆t", "a"};
		Scanner input = new Scanner(System.in);
		String unknown = getUserInput("Motion data: Enter your unknown variable, then your known variables (for help, press 1)");
		// unknown.replace("D", "∆");
		if (unknown.equals("1")) {
			printHelp();
			kinematics();
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
				// field.replace('*', ':');
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
			String[] equations = {"∆x = Xf -Xi", "v = (Xf -Xi)/∆t", "v = ∆x/∆t", "∆v = Vf -Vi", "a = (Vf -Vi)/∆t", "a = ∆v/∆t", "Xf = Xi Vi*∆t a*∆t2/2", "∆x = Vi*∆t a*∆t2/2", "Vf2 = Vi2 2*a*∆x", "Vf2 = Vi2 2*a*(Xf -Xi)"};
			for (int i = 0; i < equations.length; i++) {
				// if the variablesUsed contains at least the necessary components and the equation contains the unknown variable
				if ((variablesUsed & equationVariables[i]) == equationVariables[i] && (unknownVariable & equationVariables[i]) > 0) {
					System.out.println(equations[i]);
					Value answer = algebraEquations(equations[i], unknown, values, variables);
					if (answer == null) {
						kinematics();
						return;
					}
					System.out.print(unknown + " = ");
					answer.printValue(true);
					return;
				}
			}
			System.out.println("Not enough information");
			kinematics();
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
	
	// Plugs the numerical values into the equation
	static Value plugInValues(String expression, Value[] values, String[] variables, Value currentValue) {
		Value answer = new Value(0, "", Integer.MAX_VALUE);
		boolean negative = false;
		String[] addPieces = isolatePieces(expression, " ");
		for (String add : addPieces) {
			if (add.charAt(0) == '-') {
				negative = true;
				add = add.substring(1);
			}
			boolean fractionBottom = false;
			String[] dividePieces = isolatePieces(add, "/");
			Value division = new Value(1, "", Integer.MAX_VALUE);
			for (String divide : dividePieces) {
				String[] multiplyPieces = isolatePieces(divide, "\\*");
				Value multiplication = new Value(1, "", Integer.MAX_VALUE);
				for (String multiply : multiplyPieces) {
					Value singleUnit = new Value(1, "", Integer.MAX_VALUE);
					if (multiply.charAt(0) == '(' && multiply.charAt(multiply.length() - 1) == ')') {
						multiply = multiply.substring(1, multiply.length() - 1);
						singleUnit = plugInValues(multiply, values, variables, currentValue);
					} else {
						int exponent = 1;
						try {
							singleUnit.value = Integer.parseInt(multiply);
							singleUnit.sigFigs = Integer.MAX_VALUE;
						} catch (NumberFormatException e) {
							try {
								singleUnit.value = Double.parseDouble(multiply);
								singleUnit.sigFigs = multiply.length() - 1;
							} catch (NumberFormatException f) {
								int numberIndex = containsNumber(multiply);
								if (numberIndex > 0) {
									try {
										exponent = Integer.parseInt(multiply.substring(numberIndex));
										multiply = multiply.substring(0, numberIndex);
									} catch (Exception g) {
										System.out.println("Only supports integer exponents. Sorry!");
										return null;
									}
								}
								if (multiply.equals("calc")) {
									singleUnit = currentValue;
								} else {
									int i;
									for (i = 0; i < variables.length; i++) {
										if (variables[i].equals(multiply)) {
											singleUnit = values[i];
											break;
										}
									}
									if (i == variables.length) {
										System.out.println("An error occurred. Please try again.");
										return null;
									}
								}
							}
						}
						singleUnit.value = Math.pow(singleUnit.value, exponent);
					}
					multiplication.value *= singleUnit.value;
					multiplication.sigFigs = Integer.min(multiplication.sigFigs, singleUnit.sigFigs);
				}
				if (fractionBottom) {
					division.value /= multiplication.value;
				} else {
					fractionBottom = true;
					division.value *= multiplication.value;
				}
				division.sigFigs = Integer.min(division.sigFigs, multiplication.sigFigs);
			}
			if (!negative) {
				answer.value += division.value;
			} else {
				answer.value -= division.value;
			}
			answer.sigFigs = Integer.min(answer.sigFigs, division.sigFigs);
		}
		return answer;
	}
	
	// Manipulates equations to isolate unknowns
	static Value algebraEquations(String equation, String unknown, Value[] values, String[] variables) {
		if (!equation.contains(unknown)) {
			return null;
		}
		if (unknown == null) {
			return null;
		}
		Value answer = new Value(0, "", 1);
		String[] pieces = isolatePieces(equation, " ");
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
		printAlgebraEquation(equationLeft, equationRight, false);
		
		if (!unknownLeft) {
			String[] temp = equationLeft;
			equationLeft = equationRight;
			equationRight = temp;
		}
		int right = findFirst(equationRight, null);
		for (int left = 0; left < equationLeft.length; left++) {
			String switchPiece = equationLeft[left];
			if (switchPiece == null) {
				continue;
			}
			if (switchPiece.contains(unknown)) {
				continue;
			}
			if (switchPiece.charAt(0) == '-') {
				switchPiece = switchPiece.substring(1);
			} else {
				switchPiece = '-' + switchPiece;
			}
			equationLeft[left] = null;
			equationRight[right] = switchPiece;
			right++;
		}
		equationLeft = fixEmptyString(equationLeft);
		printAlgebraEquation(equationLeft, equationRight, true);
		
		if (equationLeft.length > 1) {
			System.out.println("Cannot handle multiple unknowns yet");
			return null;
		}
		answer = plugInValues(toAlgebraString(equationRight, " "), values, variables, null);
		equationRight = new String[equationRight.length];
		equationRight[0] = "calc";
		equationRight = fixEmptyString(equationRight);
		if (equationLeft[0].equals(unknown)) {
			return answer;
		}
		
		if (equationLeft[0].charAt(0) == '-') {
			equationLeft[0] = equationLeft[0].substring(1);
			equationRight[0] = '-' + equationRight[0];
		}
		printAlgebraEquation(equationLeft, equationRight, true);
		answer = plugInValues(toAlgebraString(equationRight, " "), values, variables, answer);
		if (equationLeft[0].equals(unknown)) {
			return answer;
		}
		
		// equationLeft = isolatePieces(equationLeft[0], "/");
		// if ()
		
		return plugInValues(toAlgebraString(equationRight, " "), values, variables, answer);
	}
	
	// Print an equation
	static void printAlgebraEquation(String[] equationLeft, String[] equationRight, boolean withEmptySpace) {
		for (String writeEquation : equationLeft) {
			if (writeEquation == null && !withEmptySpace) {
				continue;
			} else {
				// writeEquation.replace("D", "∆").replace(':', '*');
				System.out.print(writeEquation + " ");
			}
		}
		System.out.print("=");
		for (String writeEquation : equationRight) {
			if (writeEquation == null) {
				continue;
			} else {
				// writeEquation.replace("D", "∆").replace(':', '*');
				System.out.print(" " + writeEquation);
			}
		}
		
		System.out.println();
	}
	
	// Split an equation into algebraic pieces
	static String[] isolatePieces(String equation, String delimiter) {
		String[] separateBySpace = equation.split(delimiter);
		String currentPiece = "";
		String[] pieces = new String[separateBySpace.length];
		int parentheses = 0;
		int piecesIndex = 0;
		for (String piece : separateBySpace) {
			parentheses += countSubstrings(piece, "(");
			parentheses -= countSubstrings(piece, ")");
			if (parentheses > 0) {
				currentPiece += piece + delimiter;
			} else {
				currentPiece += piece;
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
	
	// returns a new String[] with all null instances removed
	static String[] fixEmptyString(String[] fix) {
		int emptyIndex = 0;
		for (int currentIndex = 0; currentIndex < fix.length; currentIndex++) {
			if (fix[currentIndex] == null || fix[currentIndex] == "") {
				continue;
			} else {
				if (currentIndex > emptyIndex) {
					fix[emptyIndex] = fix[currentIndex];
					fix[currentIndex] = null;
				}
				emptyIndex++;
			}
		}
		String[] fixed = new String[emptyIndex];
		for (int i = 0; i < fixed.length; i++) {
			fixed[i] = fix[i];
		}
		return fixed;
	}
	
	// Count how many times a certain substring appears in a given string
	static int countSubstrings(String text, String substring) {
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
	
	// turns a String[] into an algebraic string
	static String toAlgebraString(String[] expression, String delimiter) {
		String returnString = expression[0];
		for (int index = 1; index < expression.length; index++) {
			if (expression[index] == null) {
				continue;
			}
			returnString += delimiter + expression[index];
		}
		return returnString;
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
		System.out.println("Motion variables:\nXi = initial position, Xf = final position\n∆x = delta x = change in position or distance traveled\nv =  constant velocity = speed + direction (usually direction is ignored)\nVi = initial velocity, Vf = final velocity\n∆v = delta v = change in velocity\n∆t = delta t = change in time (amount of time passed)\na = acceleration = change in velocity per unit time\nif you do not have data for a given field, leave it blank and press enter\nType \"D\" for ∆");
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
	
	// Finds the first instance of a string in a String[]
	static int findFirst(String[] search, String find) {
		for (int i = 0; i < search.length; i++) {
			if (search[i] == find) {
				return i;
			}
		}
		return -1;
	}
	
	// determines if the string contains a number, and returns the index of the first digit
	static int containsNumber(String search) {
		if (search == null || search.isEmpty()) {
			return -1;
		}
		
		for (int i = 0; i < search.length(); i++) {
			if (Character.isDigit(search.charAt(i))) {
				return i;
			}
		}
		return -1;
	}
}