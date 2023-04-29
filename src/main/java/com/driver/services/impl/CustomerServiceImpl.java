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
		List<Driver>driverList=driverRepository2.findAll();
		Customer customer=customerRepository2.findById(customerId).get();

		List<TripBooking> customerTripBookingList1=customer.getTripBookingList();
		List<TripBooking> driverTripBookingList1=new ArrayList<>();



        Map<Integer,Driver> driverMap=new TreeMap<>();
		for (Driver driver1: driverList)
		{
			driverMap.put(driver1.getDriverId(),driver1);
		}

		TripBooking tripBooking = null;

			for (int a:driverMap.keySet())
			{
				if (driverMap.get(a).getCab().getAvailable()==true)
				{
					tripBooking.setCustomer(customer);
					tripBooking.setToLocation(toLocation);
					tripBooking.setFromLocation(fromLocation);
					tripBooking.setDistanceInKm(distanceInKm);
					tripBooking.setDriver(driverMap.get(a));
					driverTripBookingList1=driverMap.get(a).getTripBookingList();
					driverRepository2.save(driverMap.get(a));
				}
			}

		if (tripBooking==null)
		{
			throw new Exception("No cab available!");
		}
		customerRepository2.save(customer);
		tripBookingRepository2.save(tripBooking);
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
