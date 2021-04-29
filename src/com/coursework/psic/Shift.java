package com.coursework.psic;

public class Shift {

	private String ShiftDay;
	private ShiftType shiftType;
	private String startTime;
	private String endTime;
	private boolean isWeekend;
	
	
	
	public String getShiftDay() {
		return ShiftDay;
	}
	public void setShiftDay(String shiftDay) {
		ShiftDay = shiftDay;
	}
	public ShiftType getShiftType() {
		return shiftType;
	}
	public void setShiftType(ShiftType shiftType) {
		this.shiftType = shiftType;
	}
	
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean isWeekend() {
		return isWeekend;
	}
	public void setWeekend(boolean isWeekend) {
		this.isWeekend = isWeekend;
	}
	
	
	
}
