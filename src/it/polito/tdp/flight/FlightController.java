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
    
    private Model model;
    
    public void setModel(Model m){
    	model=m;
    	
    	boxAirline.getItems().addAll(model.getAllAirlines());
    }
    

    @FXML
    void doRaggiungibili(ActionEvent event) {
    	if(boxAirport.getValue()==null){
    		txtResult.setText("Errore: seleziona un aeroporto \n");
    		return;
    	}
    	
    	List<AirportDistance> reachable = model.getReachableAirports(boxAirport.getValue());
    	// Stampa aeroporti raggiunti
    	txtResult.clear();
    	txtResult.appendText("Aeroporti raggiungibili da "+boxAirport.getValue().getName()+":\n");
    	for(AirportDistance a: reachable) {
    		txtResult.appendText(a.toString()+"\n") ;
    	}


    }

    @FXML
    void doServiti(ActionEvent event) {
    	if(boxAirline.getValue()==null){
    		txtResult.setText("Errore: seleziona una compagnia aerea \n");
    		return;
    	}
    	model.creaGrafo(boxAirline.getValue());
    	List<Airport> reachedAirports = model.getReachedAirports(boxAirline.getValue()) ;
    	//Prima pulisco il menu a tendina...
    	boxAirport.getItems().clear();
    	//...e poi lo popolo con i nuovi aeroporti
    	boxAirport.getItems().addAll(reachedAirports);
    	
    	// Stampa aeroporti raggiunti
    	txtResult.clear();
    	txtResult.appendText("Aeroporti raggiunti da "+boxAirline.getValue().getName()+":\n");
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
}
