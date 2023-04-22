package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;
	@Autowired
	CabRepository cabRepository;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
       customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
        List<Driver> drivers=new ArrayList<>();
		drivers=driverRepository2.findAll();
		Driver driver=new Driver();
		int id = Integer.MAX_VALUE;
		try {

			for (Driver d : drivers) {
				if (d.getDriverId() < id && d.getCab().isAvailable()) {
					driver.setMobileNumber(d.getMobileNumber());
					driver.setPassword(d.getPassword());
					driver.setCab(d.getCab());
					driver.setTripBookingList(d.getTripBookingList());
					id = d.getDriverId();
					driver.getCab().setAvailable(false);
				}
			}
		}catch (Exception e) {
			if (id == Integer.MAX_VALUE) {
				throw new Exception("No cab available!");
			}
		}
		Customer customer=new Customer();
		customer=customerRepository2.findById(customerId).get();

		TripBooking tripBooking=new TripBooking();
		tripBooking.setTripStatus(TripStatus.CONFIRMED);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBookingRepository2.save(tripBooking);
		List<TripBooking> list=customer.getTripBookingList();
		list.add(tripBooking);
		customer.setTripBookingList(list);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);
	}
}
