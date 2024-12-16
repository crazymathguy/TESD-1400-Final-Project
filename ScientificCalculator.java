import java.util.*;
import java.awt.print.*;

public class ScientificCalculator {
	public static void main(String[] args) {
		// 1 mile = 1609 m
		String[] input = getUserInput("Enter a value and a unit to convert it to\n(separated by a space: ex. 4.5mi m)");
		if (input.length == 2) {
			Value value = Value.getValue(input[0]);
			if (value == null) {
				System.out.print("Invalid known value");
			} else {
				Value unitDef = value.getUnitDefinition();
				Value siValue = new Value(value.value * unitDef.value, unitDef.units);
				Value unitToConvert = Value.getUnitDefinition(input[1]);
				if (unitToConvert == null) {
					System.out.print("Invalid unknown value");
				} else {
					Value convertedValue = new Value(siValue.value / unitToConvert.value, unitToConvert.units);
					printEquation(value, convertedValue);
				}
			}
		} else {
			System.out.print("Invalid equation");
		}
		
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
	
	static void printEquation(Value known, Value unknown) {
		known.printValue();
		System.out.print(" = ");
		unknown.printValue();
	}
}