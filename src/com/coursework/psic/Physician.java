package com.coursework.psic;

import java.util.LinkedHashMap;
import java.util.List;

public class Physician {
	 
	private String uniqueID;
	private String fullName;
	private String address;
	private String telephoneNumber;
	private List<Expertise> expertiseIn;
    private List<Appointment> appointments;
    private List<ConsultingWeeks> consultingWeeks;
    private String roomName;
	private LinkedHashMap<String, Slot> slotsMap;
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	public List<Expertise> getExpertiseIn() {
		return expertiseIn;
	}
	public void setExpertiseIn(List<Expertise> expertiseIn) {
		this.expertiseIn = expertiseIn;
	}
	public List<Appointment> getAppointments() {
		return appointments;
	}
	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}
	public List<ConsultingWeeks> getConsultingWeeks() {
		return consultingWeeks;
	}
	public void setConsultingWeeks(List<ConsultingWeeks> consultingWeeks) {
		this.consultingWeeks = consultingWeeks;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public LinkedHashMap<String, Slot> getSlotsMap() {
		return slotsMap;
	}
	public void setSlotsMap(LinkedHashMap<String, Slot> slotsMap) {
		this.slotsMap = slotsMap;
	}
	
	
	
	
	
}
