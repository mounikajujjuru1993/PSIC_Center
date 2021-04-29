package com.coursework.psic.exes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.coursework.psic.Appointment;
import com.coursework.psic.ConsultingWeeks;
import com.coursework.psic.Database;
import com.coursework.psic.Expertise;
import com.coursework.psic.Patient;
import com.coursework.psic.Physician;
import com.coursework.psic.Shift;
import com.coursework.psic.ShiftType;
import com.coursework.psic.Slot;
import com.coursework.psic.Visitor;

public class Exicuter {

	public static Database database = new Database();
	public static List<Slot> slotList = new ArrayList<Slot>();

	public static void main(String[] args) {
		constructObjects();
		createAppointmnetObjects();
		chooseFunctions();
	}
	
	public static void chooseFunctions() {
		
		System.out.println("Welcome to Mounica's booking system for physiotheraphy and Sports center");
		System.out.println("Please select following options");
		System.out.println("key in 1 to register a patient.");
		System.out.println("key in 2 to book a treatment appointment");
		System.out.println("key in 3 to change an appointment");
		System.out.println("key in 4 to attend a treatment appointment");
		System.out.println("key in 5 to book an appointment for a visitor.");
		System.out.println("key in 6 to print a report1");
		System.out.println("key in 7 to print a report2");
		Scanner in = new Scanner(System.in);
		
		String input = in.nextLine();
		
		switch (input) {
		case "1":RegisterPatients();
		case "2": startAppointmentProcess(database.getTables().get("physician"), new HashMap<String, String>());
		case "3": changeAppointments();	
		case "4": attendAppointment();
		case "5": bookVisitorAppointment();
			
			break;

		default:
			System.out.println("Please try again");
			chooseFunctions();
			
		}
		
	}

	/// visitor appointments function
	
	private static void bookVisitorAppointment() {
		
		Visitor visitor = new Visitor();
		System.out.println("please enter visitor name");
		Scanner in = new Scanner(System.in);
		
		String visitorName = in.nextLine();
		if(visitorName.matches("^[0-9]*$")) {
			System.out.println("Entered Visitor name consists numbers,Please try again...");
			bookVisitorAppointment();
		}
		visitor.setName(visitorName);
		HashMap<String, String>  physicianListByExpertise = new HashMap<String, String>();
		HashMap<String, Object> physicianList = database.getTables().get("physician");
		Set<String>    physicianKeys = database.getTables().get("physician").keySet();
		for (String Physici : physicianKeys) {

			Physician physician = (Physician) database.getTables().get("physician").get(Physici);

			System.out.println("services offered by " + Physici + " are..");
			for (Expertise exp : physician.getExpertiseIn()) {
				if (physicianListByExpertise.containsKey(exp.getExpertiseName())) {

					physicianListByExpertise.put(exp.getExpertiseName(),
							physicianListByExpertise.get(exp.getExpertiseName()) + "-" + physician.getFullName());

				} else {
					physicianListByExpertise.put(exp.getExpertiseName(), physician.getFullName());
				}
				System.out.println(exp.getExpertiseName());

			}
		}

		System.out.println("Please enter physician Name or service name to book an oppointment");
		String physiorservicename = in.nextLine();

		String enteredAppointment="";
		if(physicianKeys.contains(physiorservicename)||physicianListByExpertise.containsKey(physiorservicename)) {
		   enteredAppointment = chooseActionForUserInput(physiorservicename.toLowerCase(), physicianKeys,
				physicianListByExpertise, in);
		}

		if (physicianKeys.contains(physiorservicename) && !enteredAppointment.split("-")[0].isEmpty()
				&& enteredAppointment.split("-")[0].matches("^[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {
			bookAppointmentByPhysicianName(enteredAppointment,enteredAppointment.split("-")[1], physiorservicename, visitor, physicianListByExpertise
					,in);
		} else if (!enteredAppointment.isEmpty()
				&& enteredAppointment.matches("^[a-zA-Z]+-[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {

			bookAppointmentByService(enteredAppointment, physiorservicename, visitor, physicianListByExpertise, in);
		} else {
			System.out.println("something went wrong please enter start to start booking appointments from begining or enter exit to go back to menu");
			String enteredString = in.nextLine();
			if (enteredString!=null && !enteredString.isEmpty() && enteredString.equalsIgnoreCase("start")) {
				bookVisitorAppointment(); 
			}
			else{
				chooseFunctions();
			} 
			
			
			
		}
		
		
		
	}
	
	//book appointments for visitor by service name

	private static void bookAppointmentByService(String enteredAppointment, String physiorservicename, Visitor visitor,
			HashMap<String, String> physicianListByExpertise, Scanner in) {
      
		String appontmentString = enteredAppointment.split("-")[1];
		String selectedWeek = appontmentString.substring(0, 2);
		String selectedDay = appontmentString.substring(2, 5);
		String selectedTime = appontmentString.substring(5);
		String selectedPhisician = enteredAppointment.split("-")[0];

		HashMap<String, Object> phisicianMap = database.getTables().get("physician");
		//HashMap<String, Object> roomMap = database.getTables().get("room");
		HashMap<String, Object> appointmentMap = new HashMap<String, Object>();

		Physician physician = (Physician) phisicianMap.get(selectedPhisician);

		//Room room = (Room) roomMap.get(physician.getRoomName());

		Appointment appointment = new Appointment();

		appointment.setPhysician(physician);
		appointment.setRoomName(physician.getRoomName());
		appointment.setUniqueAppointmentID(generateUniqueIDForAppointments());
		appointment.setVisitor(visitor);
		appointment.setService(physiorservicename);

		System.out.println("Please key in Cancel to cancel appointment or key in start to book another appointment");
		String enteredString = in.nextLine();

		if (enteredString.equalsIgnoreCase("start")) {
			bookVisitorAppointment(); 
		} else if (enteredString.equalsIgnoreCase("cancel")) {

			 changeAppointments();	
		} else {
			bookVisitorAppointment(); 

		}

	
		
	}

	
	///book appointments for visitor by phisician name
	
	private static void bookAppointmentByPhysicianName(String enteredAppointment, String serviceName, String physiorservicename,
			 Visitor visitor, HashMap<String, String> physicianListByExpertise, Scanner in) {
		
		HashMap<String, Object> phisicianMap = database.getTables().get("physician");
		//HashMap<String, Object> roomMap = database.getTables().get("room");
		HashMap<String, Object> appointmentMap = new HashMap<String, Object>();

		Physician physician = (Physician) phisicianMap.get(physiorservicename);

		//Room room = (Room) roomMap.get(physician.getRoomName());

		Appointment appointment = new Appointment();
		//appointment.setPatient(patient);
		appointment.setPhysician(physician);
		appointment.setRoomName(physician.getRoomName());
		appointment.setUniqueAppointmentID(generateUniqueIDForAppointments());
		appointment.setVisitor(visitor);
		appointment.setService(serviceName);
		String selectedWeek = enteredAppointment.substring(0, 2);
		String selectedDay = enteredAppointment.substring(2, 5);
		String selectedTime = enteredAppointment.substring(5);

		for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {

			if (consweek.getWeekName().substring(4).equals(selectedWeek.substring(1))) {

					/*Integer startTime = Integer.valueOf(consweek.getShift()
							.get(consweek.getWeekName() + selectedDay + " Day").getStartTime().substring(0, 2));

					Integer endTime = Integer.valueOf(consweek.getShift()
							.get(consweek.getWeekName() + selectedDay + " Day").getEndTime().substring(0, 2));*/

					
				Slot slot = physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime);
				String slotStart = slot.getStartTime().substring(0, 2);

					if (slot.getIsAvailable() && slotStart.contains(selectedTime)) {

						physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime).setIsAvailable(false);
						appointment.setTimeSlot(slot.getStartTime() + "-"
								+ slot.getEndTime());
						appointment.setWeek(consweek.getWeekName());
						appointment.setDay(selectedDay + "Day");
						//appointment.setStatus("Booked");
						appointment.setStatus("Attended");

					}

				
			/*	if (appointment.getStatus().equals("Booked")) {*/
					if (appointment.getStatus().equals(("Attended"))) {
					
					appointmentMap.put(appointment.getUniqueAppointmentID(), appointment);
					
                     if(database.getTables().containsKey("Appointment")) {
						
						database.getTables().get("Appointment").putAll(appointmentMap);;
						System.out.println("Appointment booked Succuessfully for Visitor :"+visitor.getName());
					    break;
					}
					else {
						
						database.getTables().put("Appointment", appointmentMap);
						System.out.println("Appointment booked Succuessfully for Visitor :"+visitor.getName());
					    break;
					}
					
				}

			}

		}

		System.out.println("Please key in Cancel to cancel appointment or key in start to book another appointment or exit to go back to menu");
		String enteredString = in.nextLine();

		if (enteredString.equalsIgnoreCase("start")) {
			bookVisitorAppointment(); 
		} else if (enteredString.equalsIgnoreCase("cancel")) {

			changeAppointments();
		} else if(enteredString.equalsIgnoreCase("exit")) {
			chooseFunctions();
		} 
	    else {
			bookVisitorAppointment(); 

		}

	}

	private static void attendAppointment() {
		Boolean ifEnteredOnce=false;
		System.out.println("Change appointments");
		Scanner in = new Scanner(System.in);
		HashMap<String, Object> appointmentMap = database.getTables().get("Appointment");
		for(String key :appointmentMap.keySet()){
			Appointment appointment = (Appointment) appointmentMap.get(key);

			if(appointment.getPatient()!=null && !appointment.getStatus().equals("Attended") && !appointment.getStatus().equals("Cancelled")) {
				ifEnteredOnce=true;
				System.out.println("--------Appointment Details----------");
				System.out.println("Appointment ID:"+appointment.getUniqueAppointmentID());
				System.out.println("Room:"+appointment.getPhysician().getRoomName());
				System.out.println("Appointment Time:"+appointment.getWeek()+","+appointment.getDay()+","+appointment.getTimeSlot());
				System.out.println("Patient:"+appointment.getPatient().getFullName());
				System.out.println("Physician:"+appointment.getPhysician().getFullName());
				System.out.println("Appointment Status:"+appointment.getStatus());
			}
               /*else if(appointment.getVisitor()!=null) {
				
				System.out.println("--------Appointment Details----------");
				System.out.println("Appointment ID:"+appointment.getUniqueAppointmentID());
				System.out.println("Appointment Time:"+appointment.getWeek()+","+appointment.getDay()+","+appointment.getTimeSlot());
				System.out.println("Visitor:"+appointment.getVisitor().getName());
			}*/
			if(!ifEnteredOnce) {
				System.out.println("There are no Appointments to Display");
			}
			
		}
		if(ifEnteredOnce) {
		System.out.println("please enter unique appointment Id to cancel appointment");
		
		
		String enteredString = in.nextLine();
		
		if(appointmentMap.keySet().contains(enteredString.toUpperCase())) {
			
			Appointment appointment = (Appointment) appointmentMap.get(enteredString.toUpperCase());
			appointment.setStatus("Attended");
		}
		
		System.out.println("Appointment Attended Sucessfully");
		}
		System.out.println("Please enter start to create new appointment or enter exit to go back to menu");
		String entered = in.nextLine();
		if(entered.equalsIgnoreCase("start")) {
		startAppointmentProcess(database.getTables().get("physician"), new HashMap<String, String>());
		}else {
		chooseFunctions();
		}
		
		
	}

	private static void changeAppointments() {
		Boolean ifEnteredOnce=false;
		Scanner in = new Scanner(System.in);
		System.out.println("Change appointments");
		HashMap<String, Object> appointmentMap = database.getTables().get("Appointment");
		for(String key :appointmentMap.keySet()){
			Appointment appointment = (Appointment) appointmentMap.get(key);
			
			if(appointment.getPatient()!=null && !appointment.getStatus().equals("Attended") && !appointment.getStatus().equals("Cancelled") ) {
				 ifEnteredOnce=true;
				System.out.println("--------Appointment Details----------");
				System.out.println("Appointment ID:"+appointment.getUniqueAppointmentID());
				System.out.println("Room:"+appointment.getPhysician().getRoomName());
				System.out.println("Appointment Time:"+appointment.getWeek()+","+appointment.getDay()+","+appointment.getTimeSlot());
				System.out.println("Patient:"+appointment.getPatient().getFullName());
				System.out.println("Physician:"+appointment.getPhysician().getFullName());
				System.out.println("Treatment:"+appointment.getService());
				System.out.println("Appointment Status:"+appointment.getStatus());

				
			}
			/*else if(appointment.getVisitor()!=null) {
				
				System.out.println("--------Appointment Details----------");
				System.out.println("Appointment ID:"+appointment.getUniqueAppointmentID());
				System.out.println("Appointment Time:"+appointment.getWeek()+","+appointment.getDay()+","+appointment.getTimeSlot());
				System.out.println("Visitor:"+appointment.getVisitor().getName());
			}*/
			if(!ifEnteredOnce) {
				System.out.println("There are no Appointments to Display");
			}
			
		
			
		}
		if(ifEnteredOnce) {
		System.out.println("please enter unique appointment Id to cancel appointment or enter exit to go back to menu");
	
		String enteredString = in.nextLine();
		

		if(enteredString!=null && !enteredString.isEmpty() && appointmentMap.keySet().contains(enteredString.toUpperCase())) {
			
			Appointment appointment = (Appointment) appointmentMap.get(enteredString.toUpperCase());
			appointment.setStatus("Cancelled");
			
		}
		else if(enteredString!=null && !enteredString.isEmpty() && enteredString.toLowerCase().equals("exit")) {
			chooseFunctions();
		}
		makeSlotAvailbleAfterAppointmentCancel((Appointment)appointmentMap.get(enteredString.toUpperCase()));
		
		System.out.println("Appointment Cancelled Sucessfully");
		}
		System.out.println("Please enter start to create new appointment or enter exit to go back to menu");
		String entered = in.nextLine();
		if(entered!=null && !entered.isEmpty() && entered.equalsIgnoreCase("start")) {
		startAppointmentProcess(database.getTables().get("physician"), new HashMap<String, String>());
		}else {
		chooseFunctions();
		}
	}

	
	private static void makeSlotAvailbleAfterAppointmentCancel(Appointment appointment) {
		Physician physician = appointment.getPhysician();
		String slotStart = appointment.getTimeSlot().substring(0,2);
		physician.getSlotsMap().get(appointment.getWeek()+appointment.getDay()+slotStart).setIsAvailable(true);
	}

	//register new patients
	private static void RegisterPatients() {
		
		Boolean controlPatinetCreate=true;
		 HashMap<String, Object> patientData = new HashMap<String, Object>();
		 Boolean exit = false;
		 
		while(controlPatinetCreate) {
		System.out.println("Enter Patient Details");
		controlPatinetCreate=false;
       
		Patient Patient = new Patient();
		Patient.setUniqueID(generateUniqueIDForPatient());
		
		System.out.println("Please Enter Name:");

		Scanner in = new Scanner(System.in);
		String name = in.nextLine();
		Patient.setFullName(name);

		System.out.println("Please enter age:");

		// Scanner in = new Scanner(System.in);
		String age = in.nextLine();
		Patient.setAge(Integer.valueOf(age));
		System.out.println("Please enter sex:");

		// Scanner in = new Scanner(System.in);
		String sex = in.nextLine();
		Patient.setSex(sex);
		
		System.out.println("Please enter Address:");

		// Scanner in = new Scanner(System.in);
		String address = in.nextLine();
		
		Patient.setAddress(address);
		
		patientData.put(Patient.getFullName(), Patient);
		System.out.println("patient added");
		System.out.println("Please enter continue to add one more patient or exit to go back to functions menu");
		
		String enteredText = in.nextLine();
		if(enteredText!=null && !enteredText.isEmpty() && enteredText.toLowerCase().equals("continue")) {
			
			controlPatinetCreate = true;
		}
		else {
			exit = true;
			break;
		}
		}
		if(database.getTables().get("Patient")==null) {
		database.getTables().put("Patient", patientData);
		}
		else {
			patientData.putAll(database.getTables().get("Patient"));
			database.getTables().put("Patient", patientData);
			
		}
		
		if(exit) {
			chooseFunctions();
		}
		
	}

	
	
	// prepare inital object setup
	private static void constructObjects() {

		ArrayList<ConsultingWeeks> listOfConsultationWeeks = new ArrayList<ConsultingWeeks>();

		Map<String, HashMap<String, Object>> dbtables = new HashMap<String, HashMap<String, Object>>();
		LinkedHashMap<String, Slot> slotMap = new LinkedHashMap<String, Slot>();
       for(int week=1;week<=4;week++) {
		String[] dayNames =  new String[] {"monday","tuesday","wednesday","thursday","friday"};
		for (int i = 0; i < dayNames.length; i++) {
			
			//.Map<String, Slot> slotMap = new HashMap<String, Slot>();

			for (int j = 0; j <= 24; j++) {
				Slot slot = new Slot();
				if (j < 10) {
					slot.setStartTime("0" + j + ":00");
					slot.setEndTime("0" + j + ":" + 55);
					slot.setIsAvailable(true);
				} else {
					slot.setStartTime(j + ":00");
					slot.setEndTime(j + ":" + 55);
					slot.setIsAvailable(true);
				}
				if(week==1 && i==0) {
					slotList.add(slot);
				}
				slotMap.put("week"+week+dayNames[i]+slot.getStartTime().substring(0, 2), slot);
			}
			//Room.setSlots(slotMap);
			/*roomsMap.put(Room.getRoomName(), Room);
			dbtables.put("room", roomsMap);*/
			//dbtables.put("slot", slotMap);
		}
     }

		HashMap<String, Object> physicianList = new HashMap<String, Object>();

		Physician Physician = new Physician();
		Physician.setUniqueID(generateUniqueIDForDoctor());
		Physician.setAddress("London");
		Physician.setTelephoneNumber("079292929292");
		Physician.setFullName("chinchang");
		Physician.setRoomName("Room1");
		Physician.setSlotsMap(slotMap);
		
		
		ConsultingWeeks ConsultingWeek = new ConsultingWeeks();
		ConsultingWeek.setWeekName("week1");
		ConsultingWeek.setNumberofDaysInWeek(1);
		ConsultingWeek.setDayNames("mon");
		
		Map<String, Shift> shiftMaps = new HashMap<String, Shift>();
		for (String day : ConsultingWeek.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps.put(ConsultingWeek.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek.setShift(shiftMaps);
		
		listOfConsultationWeeks.add(ConsultingWeek);
		
		
		ConsultingWeeks ConsultingWeek1 = new ConsultingWeeks();
		ConsultingWeek1.setWeekName("week2");
		ConsultingWeek1.setNumberofDaysInWeek(1);
		ConsultingWeek1.setDayNames("tues");
		
		Map<String, Shift> shiftMaps1 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek1.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps1.put(ConsultingWeek1.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek1.setShift(shiftMaps1);
		
		listOfConsultationWeeks.add(ConsultingWeek1);
		
		
		ConsultingWeeks ConsultingWeek2 = new ConsultingWeeks();
		ConsultingWeek2.setWeekName("week3");
		ConsultingWeek2.setNumberofDaysInWeek(1);
		ConsultingWeek2.setDayNames("wednes");
		Map<String, Shift> shiftMaps2 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek2.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			;
			shiftMaps2.put(ConsultingWeek2.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek2.setShift(shiftMaps2);
		listOfConsultationWeeks.add(ConsultingWeek2);
		
		
		ConsultingWeeks ConsultingWeek3 = new ConsultingWeeks();
		ConsultingWeek3.setWeekName("week4");
		ConsultingWeek3.setNumberofDaysInWeek(1);
		ConsultingWeek3.setDayNames("mon");
		
		Map<String, Shift> shiftMaps3 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek3.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			;
			shiftMaps3.put(ConsultingWeek3.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek3.setShift(shiftMaps3);
		listOfConsultationWeeks.add(ConsultingWeek3);
		
		List<Expertise> expertiseList = new ArrayList<Expertise>();
		Expertise expertise = new Expertise();
		expertise.setExpertiseName("oncology");
		expertiseList.add(expertise);
		Expertise expertise1 = new Expertise();
		expertise1.setExpertiseName("sports");
		expertiseList.add(expertise1);
		Expertise expertise2 = new Expertise();
		expertise2.setExpertiseName("orthopedics");
		expertiseList.add(expertise2);
		Physician.setExpertiseIn(expertiseList);
		Physician.setConsultingWeeks(listOfConsultationWeeks);

		ArrayList<ConsultingWeeks> listOfConsultationWeeks1 = new ArrayList<ConsultingWeeks>();
		
		Physician Physician2 = new Physician();
		Physician2.setUniqueID(generateUniqueIDForDoctor());
		Physician2.setAddress("London");
		Physician2.setTelephoneNumber("079292929292");
		Physician2.setFullName("sarath");
		Physician2.setRoomName("Room2");
		
		ConsultingWeeks ConsultingWeek21 = new ConsultingWeeks();
		ConsultingWeek21.setWeekName("week1");
		ConsultingWeek21.setNumberofDaysInWeek(1);
		ConsultingWeek21.setDayNames("tues");
		
		Map<String, Shift> shiftMaps21 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek21.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps21.put(ConsultingWeek21.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek21.setShift(shiftMaps21);
		listOfConsultationWeeks1.add(ConsultingWeek21);
		
		ConsultingWeeks ConsultingWeek22 = new ConsultingWeeks();
		ConsultingWeek22.setWeekName("week2");
		ConsultingWeek22.setNumberofDaysInWeek(3);
		ConsultingWeek22.setDayNames("wednes");
		Map<String, Shift> shiftMaps22 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek22.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps22.put(ConsultingWeek22.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek22.setShift(shiftMaps22);
		listOfConsultationWeeks1.add(ConsultingWeek22);
		
		ConsultingWeeks ConsultingWeek23 = new ConsultingWeeks();
		ConsultingWeek23.setWeekName("week3");
		ConsultingWeek23.setNumberofDaysInWeek(3);
		ConsultingWeek23.setDayNames("thurs");
		Map<String, Shift> shiftMaps23 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek23.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps23.put(ConsultingWeek23.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek23.setShift(shiftMaps23);
		listOfConsultationWeeks1.add(ConsultingWeek23);
		
		ConsultingWeeks ConsultingWeek24 = new ConsultingWeeks();
		ConsultingWeek24.setWeekName("week4");
		ConsultingWeek24.setNumberofDaysInWeek(3);
		ConsultingWeek24.setDayNames("tues");
		Map<String, Shift> shiftMaps24 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek24.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps24.put(ConsultingWeek24.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek24.setShift(shiftMaps24);
		listOfConsultationWeeks1.add(ConsultingWeek24);
		
		Physician2.setConsultingWeeks(listOfConsultationWeeks1);
		List<Expertise> expertiseList1 = new ArrayList<Expertise>();
		Expertise expertise4 = new Expertise();
		expertise4.setExpertiseName("oncology");
		expertiseList.add(expertise4);
		Expertise expertise5 = new Expertise();
		expertise5.setExpertiseName("sports");
		expertiseList1.add(expertise5);
		Physician2.setExpertiseIn(expertiseList1);
		Physician2.setSlotsMap(slotMap);
		
		
		
   ArrayList<ConsultingWeeks> listOfConsultationWeeks2 = new ArrayList<ConsultingWeeks>();
		
		Physician Physician3 = new Physician();
		Physician3.setUniqueID(generateUniqueIDForDoctor());
		Physician3.setAddress("London");
		Physician3.setTelephoneNumber("079292929292");
		Physician3.setFullName("mickel");
		Physician3.setRoomName("Room3");
		
		ConsultingWeeks ConsultingWeek31 = new ConsultingWeeks();
		ConsultingWeek31.setWeekName("week1");
		ConsultingWeek31.setNumberofDaysInWeek(1);
		ConsultingWeek31.setDayNames("wednes");
		
		Map<String, Shift> shiftMaps31 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek31.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps31.put(ConsultingWeek31.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek31.setShift(shiftMaps31);
		listOfConsultationWeeks2.add(ConsultingWeek31);
		
		ConsultingWeeks ConsultingWeek32 = new ConsultingWeeks();
		ConsultingWeek32.setWeekName("week2");
		ConsultingWeek32.setNumberofDaysInWeek(3);
		ConsultingWeek32.setDayNames("thurs");
		
		Map<String, Shift> shiftMaps32 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek32.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps32.put(ConsultingWeek32.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek32.setShift(shiftMaps32);
		listOfConsultationWeeks2.add(ConsultingWeek32);
		
		ConsultingWeeks ConsultingWeek33 = new ConsultingWeeks();
		ConsultingWeek33.setWeekName("week3");
		ConsultingWeek33.setNumberofDaysInWeek(3);
		ConsultingWeek33.setDayNames("fri");
		Map<String, Shift> shiftMaps33 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek33.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps33.put(ConsultingWeek33.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek33.setShift(shiftMaps33);
		listOfConsultationWeeks2.add(ConsultingWeek33);
		
		ConsultingWeeks ConsultingWeek34 = new ConsultingWeeks();
		ConsultingWeek34.setWeekName("week4");
		ConsultingWeek34.setNumberofDaysInWeek(3);
		ConsultingWeek34.setDayNames("wednes");
		Map<String, Shift> shiftMaps34 = new HashMap<String, Shift>();
		for (String day : ConsultingWeek34.getDayNames().split("-")) {
			Shift shift = new Shift();
			shift.setShiftType(ShiftType.Morning);
			shift.setStartTime("09:00");
			shift.setEndTime("17:00");
			shift.setShiftDay(day + " Day");
			shiftMaps34.put(ConsultingWeek34.getWeekName() + shift.getShiftDay(), shift);
		}

		ConsultingWeek34.setShift(shiftMaps34);
		listOfConsultationWeeks2.add(ConsultingWeek34);
		
		Physician3.setConsultingWeeks(listOfConsultationWeeks2);
		List<Expertise> expertiseList3 = new ArrayList<Expertise>();
		Expertise expertise31 = new Expertise();
		expertise31.setExpertiseName("oncology");
		expertiseList3.add(expertise31);
		Expertise expertise32 = new Expertise();
		expertise32.setExpertiseName("sports");
		expertiseList3.add(expertise32);
		Physician3.setExpertiseIn(expertiseList1);
		Physician3.setSlotsMap(slotMap);
		
		
		
		
		
		   ArrayList<ConsultingWeeks> listOfConsultationWeeks3 = new ArrayList<ConsultingWeeks>();
			
			Physician Physician4 = new Physician();
			Physician4.setUniqueID(generateUniqueIDForDoctor());
			Physician4.setAddress("London");
			Physician4.setTelephoneNumber("079292929292");
			Physician4.setFullName("natasha");
			Physician4.setRoomName("Room3");
			/// be aware of dayname from here
			ConsultingWeeks ConsultingWeek41 = new ConsultingWeeks();
			ConsultingWeek41.setWeekName("week1");
			ConsultingWeek41.setNumberofDaysInWeek(1);
			ConsultingWeek41.setDayNames("wednes");
			
			Map<String, Shift> shiftMaps41 = new HashMap<String, Shift>();
			for (String day : ConsultingWeek41.getDayNames().split("-")) {
				Shift shift = new Shift();
				shift.setShiftType(ShiftType.Morning);
				shift.setStartTime("09:00");
				shift.setEndTime("17:00");
				shift.setShiftDay(day + " Day");
				shiftMaps41.put(ConsultingWeek41.getWeekName() + shift.getShiftDay(), shift);
			}

			ConsultingWeek41.setShift(shiftMaps41);
			listOfConsultationWeeks3.add(ConsultingWeek41);
			
			ConsultingWeeks ConsultingWeek42 = new ConsultingWeeks();
			ConsultingWeek42.setWeekName("week2");
			ConsultingWeek42.setNumberofDaysInWeek(3);
			ConsultingWeek42.setDayNames("thurs");
			
			Map<String, Shift> shiftMaps42 = new HashMap<String, Shift>();
			for (String day : ConsultingWeek42.getDayNames().split("-")) {
				Shift shift = new Shift();
				shift.setShiftType(ShiftType.Morning);
				shift.setStartTime("09:00");
				shift.setEndTime("17:00");
				shift.setShiftDay(day + " Day");
				shiftMaps42.put(ConsultingWeek42.getWeekName() + shift.getShiftDay(), shift);
			}

			ConsultingWeek42.setShift(shiftMaps42);
			listOfConsultationWeeks3.add(ConsultingWeek42);
			
			ConsultingWeeks ConsultingWeek43 = new ConsultingWeeks();
			ConsultingWeek43.setWeekName("week3");
			ConsultingWeek43.setNumberofDaysInWeek(3);
			ConsultingWeek43.setDayNames("fri");
			Map<String, Shift> shiftMaps43 = new HashMap<String, Shift>();
			for (String day : ConsultingWeek43.getDayNames().split("-")) {
				Shift shift = new Shift();
				shift.setShiftType(ShiftType.Morning);
				shift.setStartTime("09:00");
				shift.setEndTime("17:00");
				shift.setShiftDay(day + " Day");
				shiftMaps43.put(ConsultingWeek43.getWeekName() + shift.getShiftDay(), shift);
			}

			ConsultingWeek43.setShift(shiftMaps43);
			listOfConsultationWeeks2.add(ConsultingWeek43);
			
			ConsultingWeeks ConsultingWeek44 = new ConsultingWeeks();
			ConsultingWeek44.setWeekName("week4");
			ConsultingWeek44.setNumberofDaysInWeek(3);
			ConsultingWeek44.setDayNames("wednes");
			Map<String, Shift> shiftMaps44 = new HashMap<String, Shift>();
			for (String day : ConsultingWeek44.getDayNames().split("-")) {
				Shift shift = new Shift();
				shift.setShiftType(ShiftType.Morning);
				shift.setStartTime("09:00");
				shift.setEndTime("17:00");
				shift.setShiftDay(day + " Day");
				shiftMaps44.put(ConsultingWeek44.getWeekName() + shift.getShiftDay(), shift);
			}

			ConsultingWeek44.setShift(shiftMaps44);
			listOfConsultationWeeks3.add(ConsultingWeek44);
			
			Physician4.setConsultingWeeks(listOfConsultationWeeks3);
			List<Expertise> expertiseList4 = new ArrayList<Expertise>();
			Expertise expertise41 = new Expertise();
			expertise41.setExpertiseName("oncology");
			expertiseList4.add(expertise41);
			Expertise expertise42 = new Expertise();
			expertise42.setExpertiseName("sports");
			expertiseList4.add(expertise42);
			Physician4.setExpertiseIn(expertiseList4);
			Physician4.setSlotsMap(slotMap);
		
		
			
			
			
			 ArrayList<ConsultingWeeks> listOfConsultationWeeks4 = new ArrayList<ConsultingWeeks>();
				
				Physician Physician5 = new Physician();
				Physician5.setUniqueID(generateUniqueIDForDoctor());
				Physician5.setAddress("London");
				Physician5.setTelephoneNumber("079292929292");
				Physician5.setFullName("natasha");
				Physician5.setRoomName("Room3");
				/// be aware of dayname from here
				ConsultingWeeks ConsultingWeek51 = new ConsultingWeeks();
				ConsultingWeek51.setWeekName("week1");
				ConsultingWeek51.setNumberofDaysInWeek(1);
				ConsultingWeek51.setDayNames("wednes");
				
				Map<String, Shift> shiftMaps51 = new HashMap<String, Shift>();
				for (String day : ConsultingWeek51.getDayNames().split("-")) {
					Shift shift = new Shift();
					shift.setShiftType(ShiftType.Morning);
					shift.setStartTime("09:00");
					shift.setEndTime("17:00");
					shift.setShiftDay(day + " Day");
					shiftMaps51.put(ConsultingWeek51.getWeekName() + shift.getShiftDay(), shift);
				}

				ConsultingWeek51.setShift(shiftMaps51);
				listOfConsultationWeeks4.add(ConsultingWeek51);
				
				ConsultingWeeks ConsultingWeek52 = new ConsultingWeeks();
				ConsultingWeek52.setWeekName("week2");
				ConsultingWeek52.setNumberofDaysInWeek(3);
				ConsultingWeek52.setDayNames("thurs");
				
				Map<String, Shift> shiftMaps52 = new HashMap<String, Shift>();
				for (String day : ConsultingWeek52.getDayNames().split("-")) {
					Shift shift = new Shift();
					shift.setShiftType(ShiftType.Morning);
					shift.setStartTime("09:00");
					shift.setEndTime("17:00");
					shift.setShiftDay(day + " Day");
					shiftMaps52.put(ConsultingWeek52.getWeekName() + shift.getShiftDay(), shift);
				}

				ConsultingWeek52.setShift(shiftMaps52);
				listOfConsultationWeeks4.add(ConsultingWeek52);
				
				ConsultingWeeks ConsultingWeek53 = new ConsultingWeeks();
				ConsultingWeek53.setWeekName("week3");
				ConsultingWeek53.setNumberofDaysInWeek(3);
				ConsultingWeek53.setDayNames("fri");
				Map<String, Shift> shiftMaps53 = new HashMap<String, Shift>();
				for (String day : ConsultingWeek43.getDayNames().split("-")) {
					Shift shift = new Shift();
					shift.setShiftType(ShiftType.Morning);
					shift.setStartTime("09:00");
					shift.setEndTime("17:00");
					shift.setShiftDay(day + " Day");
					shiftMaps53.put(ConsultingWeek53.getWeekName() + shift.getShiftDay(), shift);
				}

				ConsultingWeek53.setShift(shiftMaps53);
				listOfConsultationWeeks4.add(ConsultingWeek53);
				
				ConsultingWeeks ConsultingWeek54 = new ConsultingWeeks();
				ConsultingWeek54.setWeekName("week4");
				ConsultingWeek54.setNumberofDaysInWeek(3);
				ConsultingWeek54.setDayNames("wednes");
				Map<String, Shift> shiftMaps54 = new HashMap<String, Shift>();
				for (String day : ConsultingWeek54.getDayNames().split("-")) {
					Shift shift = new Shift();
					shift.setShiftType(ShiftType.Morning);
					shift.setStartTime("09:00");
					shift.setEndTime("17:00");
					shift.setShiftDay(day + " Day");
					shiftMaps54.put(ConsultingWeek54.getWeekName() + shift.getShiftDay(), shift);
				}

				ConsultingWeek54.setShift(shiftMaps54);
				listOfConsultationWeeks4.add(ConsultingWeek54);
				
				Physician5.setConsultingWeeks(listOfConsultationWeeks4);
				List<Expertise> expertiseList5 = new ArrayList<Expertise>();
				Expertise expertise51 = new Expertise();
				expertise51.setExpertiseName("oncology");
				expertiseList5.add(expertise51);
				Expertise expertise52 = new Expertise();
				expertise52.setExpertiseName("sports");
				expertiseList5.add(expertise52);
				Physician5.setExpertiseIn(expertiseList5);
				Physician5.setSlotsMap(slotMap);
			
			
		
		
		
		physicianList.put(Physician.getFullName(), Physician);
		physicianList.put(Physician2.getFullName(), Physician2);
		physicianList.put(Physician3.getFullName(), Physician3);
		physicianList.put(Physician4.getFullName(), Physician4);
		physicianList.put(Physician5.getFullName(), Physician5);

		dbtables.put("physician", physicianList);
		database.setTables(dbtables);
	}
	
	
	private static void createAppointmnetObjects() {
		List<Patient> patientList = new ArrayList<Patient>();
		Patient p1 = new Patient();
		p1.setFullName("mark");
		p1.setAge(26);
		p1.setAddress("hatfield");
		p1.setSex("male");
		patientList.add(p1);
		
		Patient p2 = new Patient();
		p2.setFullName("priyanka");
		p2.setAge(26);
		p2.setAddress("hatfield");
		p2.setSex("female");
		patientList.add(p2);
		
		Patient p3 = new Patient();
		p3.setFullName("akshara");
		p3.setAge(26);
		p3.setAddress("hatfield");
		p3.setSex("female");
		patientList.add(p3);
		
		Patient p4 = new Patient();
		p4.setFullName("meega");
		p4.setAge(26);
		p4.setAddress("hatfield");
		p4.setSex("female");
		patientList.add(p4);
		
		Patient p5 = new Patient();
		p5.setFullName("sita");
		p5.setAge(26);
		p5.setAddress("hatfield");
		p5.setSex("female");
		patientList.add(p5);
		
		Patient p6 = new Patient();
		p6.setFullName("suchi");
		p6.setAge(26);
		p6.setAddress("hatfield");
		p6.setSex("female");
		patientList.add(p6);
		
		Patient p7 = new Patient();
		p7.setFullName("katy");
		p7.setAge(26);
		p7.setAddress("hatfield");
		p7.setSex("female");
		patientList.add(p7);
		
		Patient p8 = new Patient();
		p8.setFullName("manny");
		p8.setAge(26);
		p8.setAddress("hatfield");
		p8.setSex("female");
		patientList.add(p8);
		
		Patient p9 = new Patient();
		p9.setFullName("lucy");
		p9.setAge(26);
		p9.setAddress("hatfield");
		p9.setSex("female");
		patientList.add(p9);
		
		Patient p10 = new Patient();
		p10.setFullName("lara");
		p10.setAge(24);
		p10.setAddress("hatfield");
		p10.setSex("female");
		patientList.add(p10);
		
		HashMap<String, Object> phisicianMap = database.getTables().get("physician");
		HashSet<String> uniqueSet = new HashSet<String>();
		
	
		for(String phisicianName:phisicianMap.keySet()) {
			    Physician physician = (Physician) phisicianMap.get(phisicianName);
			  
			for(ConsultingWeeks consWeek : physician.getConsultingWeeks()) {
				
				for(String day:consWeek.getDayNames().split("-")) {
					String enteredText = consWeek.getWeekName()+day+"day"+"14:00";
			
					//Slot slot = physician.getSlotsMap().get(enteredText);
					if(!uniqueSet.contains(enteredText)) {
				    uniqueSet.add(enteredText);
					bookAppointmentsOnLoad(enteredText,p8,physician);
					}
				}
			  break;
			}
		
		
		}
		
		
			
			for(String phisicianName:phisicianMap.keySet()) {
				    Physician physician = (Physician) phisicianMap.get(phisicianName);
				  
				for(ConsultingWeeks consWeek : physician.getConsultingWeeks()) {
					
					for(String day:consWeek.getDayNames().split("-")) {
						String enteredText = consWeek.getWeekName()+day+"day"+"10:00";
				
						//Slot slot = physician.getSlotsMap().get(enteredText);
						if(!uniqueSet.contains(enteredText)) {
					    uniqueSet.add(enteredText);
						bookAppointmentsOnLoad(enteredText,p10,physician);
						}
					}
				  break;
				}
		 
			}
			
			
		
	
			
			for(String phisicianName:phisicianMap.keySet()) {
				    Physician physician = (Physician) phisicianMap.get(phisicianName);
				  
				for(ConsultingWeeks consWeek : physician.getConsultingWeeks()) {
					
					for(String day:consWeek.getDayNames().split("-")) {
						String enteredText = consWeek.getWeekName()+day+"day"+"15:00";
				
						//Slot slot = physician.getSlotsMap().get(enteredText);
						if(!uniqueSet.contains(enteredText)) {
					    uniqueSet.add(enteredText);
						bookAppointmentsOnLoad(enteredText,p9,physician);
						}
					}
				  break;
				}
		 
			}
		
	}

	

	private static void startAppointmentProcess(HashMap<String, Object> physicianList,HashMap<String, String> physicianListByExpertise) {

		Patient Patient = new Patient();
		Set<String> physicianKeys = physicianList.keySet();
		if(database.getTables().get("Patient")!=null && !database.getTables().get("Patient").keySet().isEmpty()) {
			System.out.println("Enter Y to create appointments for registered Patients");
			Scanner in = new Scanner(System.in);
			String yesorNo = in.nextLine();
			if(yesorNo!=null && !yesorNo.isEmpty() && yesorNo.equalsIgnoreCase("y")) {
				System.out.println("please Enter any patient name to create appointment for..");
				
				for(String pName :database.getTables().get("Patient").keySet()) {
				
				System.out.println(pName);
				}
				//Scanner in = new Scanner(System.in);
				String patientName= in.nextLine();
				if(patientName!=null && patientName!="" && database.getTables().get("Patient").keySet().contains(patientName)) {
					Patient = (com.coursework.psic.Patient) database.getTables().get("Patient").get(patientName);
				}
				
			}
			
		}
		else {
			
		System.out.println("Please Enter Patient Details");

		System.out.println("Please enter Name:");

		Scanner in = new Scanner(System.in);
		String name = in.nextLine();
        Patient.setFullName(name);

		System.out.println("Please enter age:");

		// Scanner in = new Scanner(System.in);
		String age = in.nextLine();
		Patient.setAge(Integer.valueOf(age));
		System.out.println("Please enter sex:");

		// Scanner in = new Scanner(System.in);
		String sex = in.nextLine();
		Patient.setSex(sex);
		
		
		System.out.println("Please enter location:");

		// Scanner in = new Scanner(System.in);
		String location = in.nextLine();;
		Patient.setAddress(location);
		
		}
		for (String Physici : physicianKeys) {

			Physician physician = (Physician) physicianList.get(Physici);

			System.out.println("services offered by " + Physici + " are..");
			for (Expertise exp : physician.getExpertiseIn()) {
				if (physicianListByExpertise.containsKey(exp.getExpertiseName())) {

					physicianListByExpertise.put(exp.getExpertiseName(),
							physicianListByExpertise.get(exp.getExpertiseName()) + "-" + physician.getFullName());

				} else {
					physicianListByExpertise.put(exp.getExpertiseName(), physician.getFullName());
				}
				System.out.println(exp.getExpertiseName());

			}
		}

		System.out.println("Please enter physician Name or service name to book an oppointment");
		Scanner in = new Scanner(System.in);
		String physiorservicename = in.nextLine();

		String enteredAppointment = chooseActionForUserInput(physiorservicename.toLowerCase(), physicianKeys,
				physicianListByExpertise, in);

		if (physicianKeys.contains(physiorservicename) && !enteredAppointment.split("-")[0].isEmpty()
				&& enteredAppointment.split("-")[0].matches("^[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {
			
			bookAppointmentByPhysicianName(enteredAppointment,enteredAppointment.split("-")[1], physiorservicename, Patient, physicianListByExpertise,
					in);
		} else if (!enteredAppointment.isEmpty()
				&& enteredAppointment.matches("^[a-zA-Z]+-[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {

			bookAppointmentByService(enteredAppointment, physiorservicename, Patient, physicianListByExpertise, in);
			
			System.out.println("please enter start to start booking appointments from begining or enter exit to go back to menu");
			String enteredString = in.nextLine();
			if (enteredString!=null && !enteredString.isEmpty() && enteredString.equalsIgnoreCase("start")) {
				startAppointmentProcess(physicianList, physicianListByExpertise);
			}
			else{
				chooseFunctions();
			} 
			
		} else {
			
			System.out.println("something went wrong please enter start to start booking appointments from begining or enter exit to go back to menu");
				String enteredString = in.nextLine();
				if (enteredString!=null && !enteredString.isEmpty() && enteredString.equalsIgnoreCase("start")) {
					startAppointmentProcess(physicianList, physicianListByExpertise);
				}
				else{
					chooseFunctions();
				} 
			
		}
	}

	
	
	////; book appointments by service
	
	
	private static void bookAppointmentByService(String enteredAppointment, String physiorservicename, Patient patient,
			HashMap<String, String> physicianListByExpertise, Scanner in) {

		String appontmentString = enteredAppointment.split("-")[1];
		String selectedWeek = appontmentString.substring(0, 2);
		String selectedDay = appontmentString.substring(2, 5);
		String selectedTime = appontmentString.substring(5);
		String selectedPhisician = enteredAppointment.split("-")[0];

		HashMap<String, Object> phisicianMap = database.getTables().get("physician");
		HashMap<String, Object> roomMap = database.getTables().get("room");
		HashMap<String, Object> appointmentMap = new HashMap<String, Object>();

		Physician physician = null;
		 if(phisicianMap.containsKey(selectedPhisician)) {
			 physician  = (Physician) phisicianMap.get(selectedPhisician);
		 }else {
			 System.out.println("Entered physician name is wrong");
			 return;
			 
		 }
		//Room room = (Room) roomMap.get(physician.getRoomName());

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setPhysician(physician);
		appointment.setRoomName(physician.getRoomName());
		appointment.setUniqueAppointmentID(generateUniqueIDForAppointments());
        appointment.setService(physiorservicename);
		for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {

			if (consweek.getWeekName().substring(4).equals(selectedWeek.substring(1))) {

				

					/*Integer startTime = Integer.valueOf(consweek.getShift()
							.get(consweek.getWeekName() + selectedDay + " Day").getStartTime().substring(0, 2));

					Integer endTime = Integer.valueOf(consweek.getShift()
							.get(consweek.getWeekName() + selectedDay + " Day").getEndTime().substring(0, 2));*/

			    if(physician.getSlotsMap().containsKey(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime))
			    {
				Slot slot = physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime);
				String slotStart = slot.getStartTime().substring(0, 2);

					if (slot.getIsAvailable() && slotStart.contains(selectedTime)) {

						physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime).setIsAvailable(false);
						appointment.setTimeSlot(slot.getStartTime() + "-"
								+ slot.getEndTime());
						appointment.setWeek(consweek.getWeekName());
						appointment.setDay(selectedDay + "day");
						appointment.setStatus("Booked");
					}

				
				if (appointment.getStatus().equals("Booked")) {
					appointmentMap.put(appointment.getUniqueAppointmentID(), appointment);
					if(database.getTables().containsKey("Appointment")) {
						
						database.getTables().get("Appointment").putAll(appointmentMap);;
						System.out.println("Appointment booked Succuessfully");
					    break;
					}
					else {
						
						database.getTables().put("Appointment", appointmentMap);
						System.out.println("Appointment booked Succuessfully");
					    break;
					}
				}
			}
			    else {
			    	System.out.println("Entered Appointment details are Wrong");
			    	return;
			    }
			}

		}


		System.out.println("Please key in Cancel to cancel appointment or key in start to book another appointment or exit to go back to menu");
		String enteredString = in.nextLine();

		if (enteredString.equalsIgnoreCase("start")) {
			startAppointmentProcess(phisicianMap, physicianListByExpertise);
		} else if (enteredString.equalsIgnoreCase("cancel")) {
			changeAppointments();
		} 
		else if(enteredString.equalsIgnoreCase("exit")) {
			chooseFunctions();
		} 
		else {
			startAppointmentProcess(phisicianMap, physicianListByExpertise);
		}

	}

	
	
	///choose action....
	
	private static String chooseActionForUserInput(String physiorservicename, Set<String> physicianKeys,
			HashMap<String, String> physicianListByExpertise, Scanner in) {

		if (physicianKeys.contains(physiorservicename)) {

			return showAppointmentsByPhisicianName(physiorservicename, database.getTables().get("physician"),physicianListByExpertise,
					database.getTables().get("room"), in);

		}

		else if (physicianListByExpertise.containsKey(physiorservicename)) {

			return showAppointmentsByService(physiorservicename, database.getTables().get("physician"),
					database.getTables().get("room"), physicianListByExpertise, in);

		}

		else {
			System.out.println("Entered Details are incorrect Please try again..");

			startAppointmentProcess(database.getTables().get("physician"), physicianListByExpertise);

		}
		return "";

	}

	
	/// show appointments by phisician Name
	private static String showAppointmentsByPhisicianName(String physiorservicename,
			HashMap<String, Object> phisicianhashMap, HashMap<String, String> physicianListByExpertise, HashMap<String, Object> roomhashMap, Scanner in) {

		System.out.println("the available appointments for " + physiorservicename + " are..");
		Physician physician = (Physician) phisicianhashMap.get(physiorservicename);

		System.out.println("the service offered by "+physician.getFullName()+" are..");
		
		for(Expertise exp:physician.getExpertiseIn()) {
			
			System.out.println(exp.getExpertiseName());
			
		}
		
		Boolean tryAgain= true;
		
		String treatmentName = "";
		while(tryAgain) {
		
			System.out.println("Please enter treatment or Service name to book an appointment.");
			
			treatmentName = in.nextLine();
			
		if(!physicianListByExpertise.containsKey(treatmentName)) {
			
			System.out.println("entered treatment or Service name is not valid");
		}
		else {
		    tryAgain=false;
		}
		}
		
		
		
		
		for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {
			Test(consweek, roomhashMap, physiorservicename, in);

			System.out.println(
					"please enter  start time of any oppointment like 'w1mon09' for week1 monday 09:00 slot or enter 'Next' to see next week appointments");

			String enteredNext = in.nextLine();

			if (enteredNext.equalsIgnoreCase("next")) {

				continue;
			} else if (enteredNext != null && enteredNext.length() == 7
					&& enteredNext.matches("^[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {

				return enteredNext+"-"+treatmentName;
			} else {

				break;

			}

		}
		return "";

	}

	
	
	///show appointments
	
	private static String showAppointmentsByService(String physiorservicename, HashMap<String, Object> phisicianhashMap,
			HashMap<String, Object> roomhashMap, HashMap<String, String> physicianListByExpertise, Scanner in) {

		Set<String> expertiseSet = physicianListByExpertise.keySet();

		//System.out.println("Available appointments this week for " + physiorservicename + " are..");

		for (String expName : expertiseSet) {

			String physicianName = physicianListByExpertise.get(expName);
			if (physicianName.contains("-")) {
				for (String phyName : physicianName.split("-")) {
					System.out.println("Available appointments this week for " + physiorservicename + " are..");
					Physician physician = (Physician) phisicianhashMap.get(phyName);

					for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {

						Test(consweek, roomhashMap, physician.getFullName(), in);

						System.out.println(
								"please enter doctor full name-start time of any oppointment like 'Doctorname-w1mon09' for mark-week1 monday 09:00 slot or enter 'Next' to see next week appointments");

						String enteredNext = in.nextLine();

						if (enteredNext.equalsIgnoreCase("next")) {

							continue;
						} else if (enteredNext != null 
								&& enteredNext.matches("^[a-zA-Z]+-[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {

							return enteredNext;
						} else {

							break;

						}

					}
					

				}
			}

			else {

				Physician physician = (Physician) phisicianhashMap.get(physicianName);

				for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {

					Test(consweek, roomhashMap, physician.getFullName(), in);

					System.out.println(
							"please select Appointment like 'w1mon09' for week1 monday 09:00 slot or enter 'Next' to see next week appointments");

					String enteredNext = in.nextLine();

					if (enteredNext.equalsIgnoreCase("next")) {

						continue;
					} else if (enteredNext != null && enteredNext.length() == 7
							&& enteredNext.matches("^[a-zA-Z][0-9]+[a-zA-Z]+[0-9]+$")) {

						return enteredNext;
					} else {

						break;

					}

				}
				return "";

			}
		}
		return "";

	}

	
	///Patient appointment booking by phisician Name
	
	private static void bookAppointmentByPhysicianName(String enteredNext, String physiorservicename, String servicename, Patient patient,
			HashMap<String, String> physicianListByExpertise, Scanner in) {

		HashMap<String, Object> phisicianMap = database.getTables().get("physician");
		//HashMap<String, Object> roomMap = database.getTables().get("room");
		HashMap<String, Object> appointmentMap = new HashMap<String, Object>();

		Physician physician = (Physician) phisicianMap.get(physiorservicename);

		//Room room = (Room) roomMap.get(physician.getRoomName());

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setPhysician(physician);
		appointment.setRoomName(physician.getRoomName());
		appointment.setUniqueAppointmentID(generateUniqueIDForAppointments());

		String selectedWeek = enteredNext.substring(0, 2);
		String selectedDay = enteredNext.substring(2, 5);
		String selectedTime = enteredNext.substring(5);

		for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {
			

				if (consweek.getWeekName().substring(4).equals(selectedWeek.substring(1))) {

						
					Slot slot = physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime);
					String slotStart = slot.getStartTime().substring(0, 2);

						if (slot.getIsAvailable() && slotStart.contains(selectedTime)) {

							physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime).setIsAvailable(false);
							appointment.setTimeSlot(slot.getStartTime() + "-"
									+ slot.getEndTime());
							appointment.setWeek(consweek.getWeekName());
							appointment.setDay(selectedDay + "day");
							appointment.setStatus("Booked");
							
						

						}

					
						if (appointment.getStatus().equals("Booked")) {
							appointmentMap.put(appointment.getUniqueAppointmentID(), appointment);
							if(database.getTables().containsKey("Appointment")) {
								
								database.getTables().get("Appointment").putAll(appointmentMap);;
								System.out.println("Appointment booked Succuessfully");
							    break;
							}
							else {
								
								database.getTables().put("Appointment", appointmentMap);
								System.out.println("Appointment booked Succuessfully");
							    break;
							}
						}

				}

			}

		

		System.out.println("Please key in Cancel to cancel appointment or key in start to book another appointment or enter exit to go back to menu");
		String enteredString = in.nextLine();

		if (enteredString.equalsIgnoreCase("start")) {
			startAppointmentProcess(phisicianMap, physicianListByExpertise);
		} else if (enteredString.equalsIgnoreCase("cancel")) {
			changeAppointments();
		} 
		else if(enteredString.equalsIgnoreCase("exit")) {
			chooseFunctions();
		} 
		else {
			startAppointmentProcess(phisicianMap, physicianListByExpertise);

		}
	}

	 private static void bookAppointmentsOnLoad(String enteredNext, Patient patient, Physician physician) {

		//HashMap<String, Object> phisicianMap = database.getTables().get("physician");
		HashMap<String, Object> appointmentMap = new HashMap<String, Object>();


		//Room room = (Room) roomMap.get(physician.getRoomName());

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setPhysician(physician);
		appointment.setRoomName(physician.getRoomName());
		appointment.setUniqueAppointmentID(generateUniqueIDForAppointments());
		appointment.setService(physician.getExpertiseIn().get(0).getExpertiseName());
		
		String selectedWeek = enteredNext.substring(0,5);
		String selectedDay = enteredNext.substring(5,enteredNext.indexOf("day"));
		String selectedTime = enteredNext.substring(enteredNext.indexOf("y")+1);

		for (ConsultingWeeks consweek : physician.getConsultingWeeks()) {

				if (consweek.getWeekName().equals(selectedWeek)) {
						
					Slot slot = physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime.substring(0, 2));
					//String slotStart = slot.getStartTime().substring(0, 2);

							physician.getSlotsMap().get(consweek.getWeekName()+selectedDay.toLowerCase()+"day"+selectedTime.substring(0, 2)).setIsAvailable(false);
							appointment.setTimeSlot(slot.getStartTime() + "-"
									+ slot.getEndTime());
							appointment.setWeek(consweek.getWeekName());
							appointment.setDay(selectedDay + "day");
							appointment.setStatus("Booked");

					
						if (appointment.getStatus().equals("Booked")) {
							appointmentMap.put(appointment.getUniqueAppointmentID(), appointment);
							if(database.getTables().containsKey("Appointment")) {
								
								database.getTables().get("Appointment").putAll(appointmentMap);;
								//System.out.println("Appointment booked Succuessfully");
							    break;
							}
							else {
								
								database.getTables().put("Appointment", appointmentMap);
								//System.out.println("Appointment booked Succuessfully");
							    break;
							}
						}

				}

			}

	}	
	
	
	///test method
	

	public static void Test(ConsultingWeeks consweek, Map<String, Object> roomsMap, String phisicianName, Scanner in) {

		Boolean breakLoop = false;
		Physician Physician = (com.coursework.psic.Physician) database.getTables().get("physician").get(phisicianName);
		//Room room = (Room) roomsMap.get(Physician.getRoomName());

		for (String day : consweek.getDayNames().split("-")) {

			System.out.println(consweek.getWeekName());
			if (breakLoop) {
				break;
			}

			System.out.println(day + "day");
			StringBuffer daySlot = new StringBuffer("Doctor Name:"+Physician.getFullName());
			LinkedHashMap<String, Slot> slotMap = Physician.getSlotsMap();
			for (Slot slot : slotList) {
	

				Integer startTime = Integer.valueOf(
						consweek.getShift().get(consweek.getWeekName() + day + " Day").getStartTime().substring(0, 2));
				Integer endTime = Integer.valueOf(
						consweek.getShift().get(consweek.getWeekName() + day + " Day").getEndTime().substring(0, 2));
				if(slotMap.keySet().contains(consweek.getWeekName() + day +"day"+slot.getStartTime().substring(0, 2))) {
				Slot slotComp = slotMap.get(consweek.getWeekName() + day +"day"+slot.getStartTime().substring(0, 2));
				Integer slotStart = Integer.valueOf(slotComp.getStartTime().substring(0, 2));

				if (slotComp.getIsAvailable() && slotStart >= startTime
						&& slotStart <= endTime) {
					if (daySlot.length() < 2) {

						daySlot.append(slotComp.getStartTime() + "-"
								+ slotComp.getEndTime());
					} else {

						daySlot.append("  " + slotComp.getStartTime() + "-"
								+ slotComp.getEndTime());
					}
				}

		}
			}
			System.out.println(daySlot);

		}
	}

	
	
	
	
	
	
	
	
	
	/// unique ID Code
	
	
	
	
	
	public static String generateUniqueIDForDoctor() {

		Random random = new Random();

		return "DOC" + random.nextInt();

	}

	public static String generateUniqueIDForPatient() {

		Random random = new Random();

		return "PAT" + random.nextInt();

	}

	public static String generateUniqueIDForAppointments() {

		Random random = new Random();

		return "APP" + random.nextInt();

	}

}
