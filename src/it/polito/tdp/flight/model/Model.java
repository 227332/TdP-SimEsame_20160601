package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {

	private Airline myAirline;
	private List<Airport> reachedAirports;

	private List<Airport> allAirports;
	private List<Airline> allAirlines;

	//siccome farò spesso delle ricerche per Id, allora oltre ad avere i dati
	//nelle 2 liste di sopra, creo due Map, perchè così mi semplifica di molto il
	//codice, oltre ad essere più efficiente
	private Map<Integer, Airport> airportMap;
	private Map<Integer, Airline> airlineMap;

	private SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> graph;

	public Model() {
		
		/*
		 * OSS: siccome le Airlines sono sempre
		 * le stesse per tutto il tempo del programma, allora
		 * le memorizzo già tutte direttamente quando creo il
		 * modello. Inoltre, memorizzo fin dall'inizio anche tutti
		 * gli aeroporti esistenti giusto perchè penso che così è
		 * più efficiente. Invece per le rotte non l'ho fatto perchè
		 * mi sembrava eccessivo
		 */
		FlightDAO dao = new FlightDAO();

		this.allAirlines = dao.getAllAirlines();
		this.allAirports = dao.getAllAirports();

		// populate a map AirportId->Airport
		this.airportMap = new HashMap<>();
		for (Airport a : allAirports)
			airportMap.put(a.getAirportId(), a);

		// populate a map AirlineId->Airline
		this.airlineMap = new HashMap<>();
		for (Airline a : allAirlines)
			airlineMap.put(a.getAirlineId(), a);

	}

	public List<Airport> getReachedAirports(Airline airline) {
		//RICORDA di fare questi controlli per evitare, quando possibile, di dover richiamare il dao se l'airline
		//è la stessa della ricerca precedente
		if (this.myAirline == null || !this.myAirline.equals(airline)) {

			this.myAirline = airline;
			
			FlightDAO dao = new FlightDAO();

			List<Integer> airportIds = dao.getReachedAirportsID(this.myAirline);
			
			this.reachedAirports = new ArrayList<Airport>();
			for (Integer id : airportIds)
				this.reachedAirports.add(airportMap.get(id));

			//RICORDA: ricorda come si usa il sort con il comparator!
			this.reachedAirports.sort(new Comparator<Airport>() {
				@Override
				public int compare(Airport o1, Airport o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

		}

		return this.reachedAirports;
	}

	public List<Airport> getReachedAirports() {
		return reachedAirports;
	}

	public List<Airport> getAllAirports() {
		return allAirports;
	}

	public List<Airline> getAllAirlines() {
		return allAirlines;
	}

	public void buildGraph(Airline airline) {
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		Graphs.addAllVertices(graph, this.allAirports);

		FlightDAO dao = new FlightDAO();
		List<Route> routes = dao.getRoutesByAirline(airline);

		for (Route r : routes) {
			/*
			 * ATTENZIONE: devi sempre guardare com'è fatto il database, se no rischi che vengano lanciate
			 * delle exceptions e tu non riesci a capirne il perché! Per esempio in questo database vi è uno
			 * 0 se non è specificato l'aeroporto di partenza o di arrivo! In tali casi, all'esame, queste
			 * tuple vanno semplicemente ignorare, non vanno considerate
			 */
			if (r.getSourceAirportId() != 0 && r.getDestinationAirportId() != 0) {
				Airport a1 = airportMap.get(r.getSourceAirportId());
				Airport a2 = airportMap.get(r.getDestinationAirportId());

				//ATT: ricorda di gestire SEMPRE il caso in cui hai null dopo una get! All'esame
				//il caso in cui uno dei due è null lo gestiamo ignorandolo
				if (a1 != null && a2 != null) {

					//LatLng è una classe che devo importare inserendo l'archivio simplelatlng.jar
					//nel progetto
					LatLng c1 = new LatLng(a1.getLatitude(), a1.getLongitude());
					LatLng c2 = new LatLng(a2.getLatitude(), a2.getLongitude());
					/*
					 * RICORDA questa formula per calcolare le distanze date longitudine e latitudine
					 */
					double distance = LatLngTool.distance(c1, c2, LengthUnit.KILOMETER);

					Graphs.addEdge(graph, a1, a2, distance);
					

				}
			}
		}

	}

	/*
	 * OSS: se il problema chiedeva le distanze in linea d'aria, allora bisognava fare una visita
	 * in ampiezza o in profondità per trovare tutti gli aeroporti raggiungibili, poi per ogni 
	 * aeroporto trovato si calcolava la distanza dall'aeroporto di partenza usando LatLngTool.distance(),
	 * e infine si ordinavano i risultati...Ma qui si deve usare FloydWarshall o Dijkstra perchè la 
	 * distanza è intesa non in linea d'aria ma in base ai pesi degli archi del grafo costruito!
	 */
	public List<AirportDistance> getDestinations(Airline airline, Airport start) {

		List<AirportDistance> list = new ArrayList<>();

		//naturalmente voglio gli aeroporti raggiungibili ma usando sempre voli
		//della compagnia aerea selezionata (infatti il grafo ha solo quelli come nodi), 
		//ecco perchè uso reachedAirport e non allAirport
		for (Airport end : reachedAirports) {
			/*
			 * OSS: all'inizio verrebbe da usare FloydWarshall, però dopo averlo usato ci si rende
			 * conto che non va perchè ci mette troppo a causa del tempo computazionale. Allora conviene
			 * fare N Dijkstra perchè è anche, anche se ne devo fare ben N, meno costoso del FloydWarshall
			 */
			DijkstraShortestPath<Airport, DefaultWeightedEdge> dsp = new DijkstraShortestPath<>(graph, start, end);
			GraphPath<Airport, DefaultWeightedEdge> p = dsp.getPath();
			if (p != null) {
				//OSS: il numero di tratte è semplicemente il numero di archi del path che li collega,
				//ecco perchè basta usare p.getEdgeList().size()
				list.add(new AirportDistance(end, p.getWeight(), p.getEdgeList().size()));
			}
		}
		
		//ora ordino la lista in base alla distanza
		list.sort(new Comparator<AirportDistance>() {
			@Override
			public int compare(AirportDistance o1, AirportDistance o2) {
				//uso stesso il compare che la classe Double ha già implementato
				return Double.compare(o1.getDistance(), o2.getDistance());
			}
		});

		return list;

	}

}
