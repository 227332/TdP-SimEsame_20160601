package it.polito.tdp.flight.model;

public class AirportDistance implements Comparable<AirportDistance>{
	private Airport airport;
	private double dist;
	
	public AirportDistance(Airport a, double d){
		airport=a;
		dist=d;
	}
	

	public Airport getAirport() {
		return airport;
	}

	public void setAirport(Airport airport) {
		this.airport = airport;
	}

	public double getDist() {
		return dist;
	}


	public void setDist(double dist) {
		this.dist = dist;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((airport == null) ? 0 : airport.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AirportDistance other = (AirportDistance) obj;
		if (airport == null) {
			if (other.airport != null)
				return false;
		} else if (!airport.equals(other.airport))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ("Aeroporto = "+airport.getName() + ", distanza = " + dist);
	}


	@Override
	public int compareTo(AirportDistance a) {
		return Double.compare(dist, a.getDist());
	}

}
