package it.polito.tdp.flight.model;
/*
 * Java Bean creato per comodità in modo da avere una classe contenente tutte le info che dovevo restituire
 * con il metodo getDestinations() del model, il quale doveva restituirmi l' elenco di Aeroporti raggiungibili
 * da un dato aeroporto di partenza assegnato insieme con le relative distanze in base alle tratte possibili (e
 * non le distanze in linea d'aria)
 */
public class AirportDistance {
	
	private Airport airport ;
	private double distance ;
	private int tratte ;
	
	public AirportDistance(Airport airport, double distance, int tratte) {
		super();
		this.airport = airport;
		this.distance = distance;
		this.setTratte(tratte) ;
	}
	public Airport getAirport() {
		return airport;
	}
	public void setAirport(Airport airport) {
		this.airport = airport;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int getTratte() {
		return tratte;
	}
	public void setTratte(int tratte) {
		this.tratte = tratte;
	}
	
	

}
