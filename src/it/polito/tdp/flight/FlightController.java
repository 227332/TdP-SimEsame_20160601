package it.polito.tdp.flight;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.flight.model.Airline;
import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.AirportDistance;
import it.polito.tdp.flight.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FlightController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Airline> boxAirline;

    @FXML
    private ComboBox<Airport> boxAirport;

    @FXML
    private TextArea txtResult;

    @FXML
    void doRaggiungibili(ActionEvent event) {
    	
 
    	Airline airline = boxAirline.getValue() ;
    	Airport start = boxAirport.getValue() ;
    	if(airline==null || start==null) {
    		txtResult.appendText("Selezionare compagnia e aeroporto\n") ;
    		return ;
    	}
    	
    	List<AirportDistance> list = model.getDestinations(airline, start) ;
    	
    	txtResult.clear();
    	txtResult.appendText("Distanze da "+start.getName()+"\n");
    	for(AirportDistance ad: list)
    		txtResult.appendText(String.format("%s (%.2f km) - %d steps\n", 
    				ad.getAirport().getName(), ad.getDistance(), ad.getTratte()));
    }

    @FXML
    void doServiti(ActionEvent event) {

    	Airline airline = boxAirline.getValue() ;
    	if(airline==null) {
    		txtResult.appendText("Devi selezionare una compagnia aerea\n");
    		return ;
    	}
    	
    	// Popola la seconda tendina con gli aeroporti raggiungibili
    	List<Airport> reachedAirports = model.getReachedAirports(airline) ;
    	//Prima pulisco il menu a tendina...
    	boxAirport.getItems().clear();
    	//...e poi lo popolo con i nuovi aeroporti
    	boxAirport.getItems().addAll(reachedAirports);
    	
    	// Costruisci il grafo
    	model.buildGraph(airline) ;

    	// Stampa aeroporti raggiunti
    	txtResult.clear();
    	txtResult.appendText("Aeroporti raggiunti da "+airline.getName()+"\n");
    	for(Airport a: reachedAirports) {
    		txtResult.appendText(a.getName()+"\n") ;
    	}
    }

    @FXML
    void initialize() {
        assert boxAirline != null : "fx:id=\"boxAirline\" was not injected: check your FXML file 'Flight.fxml'.";
        assert boxAirport != null : "fx:id=\"boxAirport\" was not injected: check your FXML file 'Flight.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Flight.fxml'.";

    }

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		boxAirline.getItems().addAll(model.getAllAirlines()) ;
	}
}
