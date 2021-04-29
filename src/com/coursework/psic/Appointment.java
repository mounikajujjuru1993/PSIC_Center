package com.coursework.psic;

public class Appointment {

	private String uniqueAppointmentID;
	private Patient patient;
	private String day;
	private String week;
	private String timeSlot;
	private String status;
	private String roomName;
	private Physician physician;
	private Visitor visitor;
	private String Service;
	
	
	public String getUniqueAppointmentID() {
		return uniqueAppointmentID;
	}
	public void setUniqueAppointmentID(String uniqueAppointmentID) {
		this.uniqueAppointmentID = uniqueAppointmentID;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Physician getPhysician() {
		return physician;
	}
	public void setPhysician(Physician physician) {
		this.physician = physician;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getTimeSlot() {
		return timeSlot;
	}
	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
	public String getService() {
		return Service;
	}
	public void setService(String service) {
		Service = service;
	}
	
	
	
}
