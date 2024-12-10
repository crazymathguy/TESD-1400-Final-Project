import java.util.*;
import java.sql.*;

public class ScientificCalculator {
	public static class Value {
		public double value;
		public String units;
	}
	
	
	public static void main(String[] args) {
		// 1 mile = 1609 m
		String[] input = getUserInput();
		for (int i = 0; i < input.length; i++) {
			Value value = getValue(input[i]);
			System.out.println(value.value + " " + value.units);
		}
	}
	
	static String[] getUserInput() {
		Scanner input = new Scanner(System.in);
		String line = input.nextLine();
		return line.split(" ");
	}
	
	static Value getValue(String stringToParse) {
		Value returnValue = new Value();
		String numericalValue = "";
		for (int i = 0; i < stringToParse.length(); i++) {
			char current = stringToParse.charAt(i);
			if (Character.isLetter(current)) {
				returnValue.units += current;
			}
			else if (Character.isDigit(current) || current == '.') {
				numericalValue += current;
			}
		}
		returnValue.value = Integer.parseInt(numericalValue);
		return returnValue;
	}
}