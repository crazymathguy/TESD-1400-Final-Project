public class Value {
	public double value;
	public String units;
	
	public Value(double value, String units) {
		this.value = value;
		this.units = units;
	}
	
	public void printValue() {
		System.out.print(this.value + this.units);
	}
}