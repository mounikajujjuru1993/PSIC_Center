package com.coursework.psic;

import java.util.Map;

public class ConsultingWeeks {

	private String weekName;
	private Map<String,Shift> shift;
	private Integer numberofDaysInWeek;
	private String dayNames;
	public String getWeekName() {
		return weekName;
	}
	public void setWeekName(String weekName) {
		this.weekName = weekName;
	}
	public Map<String,Shift> getShift() {
		return shift;
	}
	public void setShift(Map<String,Shift> shift) {
		this.shift = shift;
	}
	public Integer getNumberofDaysInWeek() {
		return numberofDaysInWeek;
	}
	public void setNumberofDaysInWeek(Integer numberofDaysInWeek) {
		this.numberofDaysInWeek = numberofDaysInWeek;
	}
	public String getDayNames() {
		return dayNames;
	}
	public void setDayNames(String dayNames) {
		this.dayNames = dayNames;
	}
	
	
	
}
