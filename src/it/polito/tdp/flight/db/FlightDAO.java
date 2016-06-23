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
		String sql = "SELECT * FROM airline order by name" ;//RICORDA order by
		
		List<Airline> list = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add( new Airline(
						res.getInt("Airline_ID"),
						res.getString("Name"),
						res.getString("Alias"),
						res.getString("IATA"),
						res.getString("ICAO"),
						res.getString("Callsign"),
						res.getString("Country"),
						res.getString("Active")));
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
	
	public List<Airport> getReachedAirports(Airline myAirline,List<Airport> airports) {

		String sql = "select distinct Airport_ID,name from airport where Airport_ID IN( " + 
				"select distinct Source_airport_ID as AirportId " + 
				"from route " + 
				"where Airline_ID=? )" + 
				"OR Airport_ID IN " + 
				"(select distinct Destination_airport_ID as AirportId " + 
				"from route " + 
				"where Airline_ID=? " + 
				") order by name asc" ;
		
		List<Airport> list = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, myAirline.getAirlineId());
			st.setInt(2, myAirline.getAirlineId());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Airport a = new Airport(res.getInt("Airport_ID"));
				/*
				 * OSS: sfrutto il fatto che ho detto che 2 oggetti Airport sono uguali
				 * se hanno lo stesso id per trovare il corrispettivo oggetto nella mia 
				 * lista airports...
				 */
				list.add( airports.get(airports.indexOf(a))) ;
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
		//System.out.println("\n arps= "+arps);
		
		List<Airline> arl = dao.getAllAirlines();
		//System.out.println("\n arl= "+arl);
		
		List<Airport> a = dao.getReachedAirports(arl.get(10), arps);
		System.out.println("\n a= "+a);
	}
	
}
