package de.unikoblenz.west.reveal.roles;

import java.util.Collections;
import java.util.List;

public class Boundaries {
	
	private double low_mid = 0;
	private double mid_high = 0;
	
	public Boundaries() {
		
	}
	
	public Boundaries(List<Double> values) {
		this.train(values);
	}
	
	public void train(List<Double> values) {
		Collections.sort(values);
		int total = values.size();
		int section1 = total/3;
		int section2 = (total*2)/3;
		this.low_mid = (values.get(section1) + values.get(section1+1))/2;
		this.mid_high = (values.get(section2) + values.get(section2+1))/2;
	}
	
	public String getLevel(double value) {
		if (value <= this.low_mid) {
			return UserWithRole.LEVEL_LOW;
		} else if (value <= this.mid_high) {
			return UserWithRole.LEVEL_MED;
		} else {
			return UserWithRole.LEVEL_HIGH;
		}
	}
	

}
