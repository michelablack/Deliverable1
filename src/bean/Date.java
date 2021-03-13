package bean;

import java.util.Arrays;
import java.util.List;

public class Date {
	
	public Date() {
		super();
	}
	public Date(String month, String year) {
		super();
		this.month = month;
		this.year = year;
	}
	private String month;
	private String year;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * Function to obtain the next month for a given one. 
	 * @return the String of the next month for the one in input.
	 */
	public String nextMonth(String month) {
		List<String> monthList= Arrays.asList("Jan","Feb","Mar","Apr","May","Jun",
				   "Jul","Aug","Sep","Oct","Nov","Dec");
		int index = monthList.indexOf(month);
		return monthList.get((index + 1)%(monthList.size()));
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (obj== null || getClass() != obj.getClass()) {
            return false;
        }
        Date date = (Date) obj; 
		return month.equals(date.month) && year.equals(date.year);
	}
	
	
	
}
