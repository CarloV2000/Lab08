package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Graph<Airport, DefaultWeightedEdge> grafo; //per Fermata deve esistere hashcode and equals
	List<Airport>aeroporti;
	private Map<Integer, Airport>aeroportiIdMAp;
	
	/**
	 * crea grafo e legge da db le info da inserirci
	 * @param distanzaMinima indica la distanza minima sotto la quale non devo creare gli archi (è inserito dall'utente)
	 */
	public void creaGrafo(int distanzaMinima) {
		//crea grafo
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.aeroportiIdMAp = new HashMap<Integer, Airport>();
		
		//aggiungi vertici
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		this.aeroporti = dao.loadAllAirports();
		Graphs.addAllVertices(this.grafo, this.aeroporti);
		
		//creo idMap
		for(Airport x : this.aeroporti) {
			this.aeroportiIdMAp.put(x.getId(), x);
		}
		
		//aggiungi archi pesati nel modo piu efficiente possibile(create classe coppiaA e idMap)
		
		List<CoppiaA>coppie = dao.getAllCoppie(aeroportiIdMAp);
		for(CoppiaA c : coppie) {
			if (c.getDistanza() > distanzaMinima) {
				this.grafo.addEdge(c.getPartenza(), c.getArrivo());
				this.grafo.setEdgeWeight(c.getPartenza(), c.getArrivo(), c.getDistanza());
				
			}
		}
	}
	
	public String stringa () {
		String s = "";
		int numeroArchi = this.grafo.edgeSet().size();
		int numeroVertici = this.grafo.vertexSet().size();
		s += "Numero archi = " +numeroArchi+ "Numero vertici = " +numeroVertici+ "\n";
		
		for(DefaultWeightedEdge x : grafo.edgeSet()) {
			s += "PARTENZA ( " + grafo.getEdgeSource(x).getAirportName() +") ";
			s += "ARRIVO ( " + grafo.getEdgeTarget(x).getAirportName() +") ";
			s += grafo.getEdgeWeight(x) +"\n";
		}
		return s;
		
	}

}
