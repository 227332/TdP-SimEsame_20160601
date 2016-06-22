package it.polito.tdp.flight.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.flight.model.Airline;
import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.Route;

public class FlightDAO {

	public List<Airport> getAllAirports() {
		
		String sql = "SELECT * FROM airport" ;
		
		List<Airport> list = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add( new Airport(
						res.getInt("Airport_ID"),
						res.getString("name"),
						res.getString("city"),
						res.getString("country"),
						res.getString("IATA_FAA"),
						res.getString("ICAO"),
						res.getDouble("Latitude"),
						res.getDouble("Longitude"),
						res.getFloat("timezone"),
						res.getString("dst"),
						res.getString("tz"))) ;
			}
			
			conn.close();
			
			return list ;
		} catch (SQLException e) {

			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Airline> getAllAirlines() {
		
		String sql = "SELECT * FROM airline ORDER BY name" ;
		
		List<Airline> list = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add( new Airline(
						res.getInt("Airline_ID"),
						res.getString("name"),
						res.getString("alias"),
						res.getString("iata"),
						res.getString("icao"),
						res.getString("callsign"),
						res.getString("country"),
						res.getString("active"))) ;
			}
			
			conn.close();
			
			return list ;
		} catch (SQLException e) {

			e.printStackTrace();
			return null ;
		}
	}

	
	public static void main(String args[]) {
		FlightDAO dao = new FlightDAO() ;
		
		List<Airport> arps = dao.getAllAirports() ;
		System.out.println(arps);
		
		List<Airline> arls = dao.getAllAirlines() ;
		System.out.println(arls);

	}

	public List<Integer> getReachedAirportsID(Airline myAirline) {
		/*
		 * come query ho usato "select distinct AirportId from ports" con ports una tabella materializzata.
		 * Volendo potevo anche fare "select AirportId from Airports where AirportId IN(...) OR AirportId IN(...)
		 */
		String sql = "select distinct AirportId from ( " + 
				"select distinct r1.Source_airport_ID as AirportId " + 
				"from route r1 " + 
				"where r1.Airline_ID=? " + 
				"union " + 
				"select distinct r2.Destination_airport_ID as AirportId " + 
				"from route r2 " + 
				"where r2.Airline_ID=? " + 
				") as ports" ;
		
		List<Integer> list = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, myAirline.getAirlineId());
			st.setInt(2, myAirline.getAirlineId());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add( res.getInt("AirportID")) ;
			}
			
			conn.close();
			
			return list ;
		} catch (SQLException e) {

			e.printStackTrace();
			return null ;
		}
	}

	public List<Route> getRoutesByAirline(Airline airline) {
		String sql ="select * from route " + 
				"where Airline_ID=?" ;

		List<Route> list = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, airline.getAirlineId());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add( new Route(
						res.getString("Airline"),
						res.getInt("Airline_ID"),
						res.getString("source_airport"),
						res.getInt("source_airport_id"),
						res.getString("destination_airport"),
						res.getInt("destination_airport_id"),
						res.getString("codeshare"),
						res.getInt("stops"),
						res.getString("equipment"))) ;
			}
			
			conn.close();
			
			return list ;
		} catch (SQLException e) {

			e.printStackTrace();
			return null ;
		}

	
	}
	
}
