package com.coursework.psic;

import java.util.Map;

public class Room {

	private String roomName;
	private Integer numberofSlotsAvailable=24;
	//private Map<String,Slot> slots;
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public Integer getNumberofSlotsAvailable() {
		return numberofSlotsAvailable;
	}
	public void setNumberofSlotsAvailable(Integer numberofSlotsAvailable) {
		this.numberofSlotsAvailable = numberofSlotsAvailable;
	}
	/*public Map<String,Slot> getSlots() {
		return slots;
	}
	public void setSlots(Map<String,Slot> slots) {
		this.slots = slots;
	}*/
	
	
	
	
	
}
