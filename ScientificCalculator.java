import java.util.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		// 1 mile = 1609 m
		String[] input = getUserInput("Enter a value and a unit to convert it to\n(separated by a space: ex. 4.5mi m)");
		Value value = getValue(input[0]);
		value.printValue();
		System.out.print(" = " + input[1]);
		
		/* for (int i = 0; i < input.length; i++) {
			Value value = getValue(input[i]);
			System.out.println(value.value + " " + value.units);
		} */
	}
	
	static String[] getUserInput(String message) {
		Scanner input = new Scanner(System.in);
		System.out.println(message);
		String line = input.nextLine();
		return line.split(" ");
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
		return returnValue;
	}
}