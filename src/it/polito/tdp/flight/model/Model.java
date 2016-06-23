package it.polito.tdp.flight.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {
	private List<Airline> airlines=null;
	private List<Airport> airports=null;
	private WeightedGraph<Airport,DefaultWeightedEdge> grafo;
	private Airline airline=null;
	private List<Route> routesByAirline;
	private List<Airport> reachedAirports=null;
	
	
	public Model(){
		airlines = getAllAirlines();
		airports = getAllAirports();
	}
	

	public List<Airline> getAllAirlines() {
		if(airlines==null){
			FlightDAO dao= new FlightDAO();
			airlines = dao.getAllAirlines();
		}
		return airlines;
	}
	
	public List<Airport> getAllAirports() {
		if(airports==null){
			FlightDAO dao= new FlightDAO();
			airports = dao.getAllAirports();
		}
		return airports;
	}
	
	public List<Route> getRoutesByAirline(Airline a){
		if(routesByAirline==null){
			FlightDAO dao= new FlightDAO();
			routesByAirline = dao.getRoutesByAirline(a);
		}
		return routesByAirline;
	}
	

	public void creaGrafo(Airline a) {
		//se ho selezionato una airline diversa da prima
		if(airline==null || !a.equals(airline)){
			
			airline=a;
			
			grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
			Graphs.addAllVertices(grafo, airports);
			
			for(Route r: getRoutesByAirline(a)){
				/*
				 * RICORDA DI FARE TUTTI I CHECK POSSIBILI ALL'ESAME, PERCHE IL DB PUO' ESSERE INCONSISTENTE!!!
				 * ATT: Qui occorreva notare che nel DB, quando non si conoscevano gli AirportId,
				 *  veniva messo 0 alla loro voce! Perciò bisogna mettere il check:
				 *  if(r.getSourceAirportId()!=0 && r.getDestinationAirportId()!=0 ){..}
				 *  
				 *  o, cmq ,si può mettere anche questo check che è più generale:
				 */
				Airport atemp1 = new Airport(r.getSourceAirportId());
				Airport atemp2 = new Airport( r.getDestinationAirportId());
				if(airports.contains(atemp1) && airports.contains(atemp2)){
					Airport a1= airports.get(airports.indexOf(atemp1));
					Airport a2= airports.get(airports.indexOf(atemp2));

					//ATT: ricorda di gestire SEMPRE il caso in cui hai null dopo una get! All'esame
					//il caso in cui uno dei due è null lo gestiamo ignorandolo, ossia così:
					if (a1 != null && a2 != null) {						
						//LatLng è una classe che devo importare inserendo l'archivio simplelatlng.jar
						//nel progetto
						LatLng c1 = new LatLng(a1.getLatitude(), a1.getLongitude());
						LatLng c2 = new LatLng(a2.getLatitude(), a2.getLongitude());

						double distance = LatLngTool.distance(c1, c2, LengthUnit.KILOMETER);

						Graphs.addEdge(grafo, a1, a2, distance);
					}
				}	
			}		
		}
	}
	
	public List<Airport> getReachedAirports(Airline a){
		FlightDAO dao = new FlightDAO();
		reachedAirports = dao.getReachedAirports(a,airports);
		return reachedAirports;	
	}


	public List<AirportDistance> getReachableAirports(Airport airp) {

		List<AirportDistance> list = new LinkedList<>();
		for(Airport a: reachedAirports ){
			DijkstraShortestPath<Airport,DefaultWeightedEdge> cammino = new DijkstraShortestPath<>(grafo,airp,a);
			if(cammino.getPath() != null){
				//a è raggiungibile da airp
				list.add(new AirportDistance(a,cammino.getPathLength()));
			}
		}
		Collections.sort(list);
		return list;
	}
	

}
