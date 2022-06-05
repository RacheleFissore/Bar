package it.polito.tdp.bar.model;

import java.time.Duration;

public class Event implements Comparable<Event>{
	// Tipologie di eventi possibili
	public enum EventType {
		ARRIVO_GRUPPO_CLIENTI,
		TAVOLO_LIBERATO
	}
	
	// Duration: è un numero di secondi, è come se fosse un integer
	private Duration time; // Istante in cui si verifica l'evento creato
	private EventType type; // Tipo di evento del singolo evento
	private int nPersone; // Numero persone che vogliono sedersi al tavolo
	private Duration durata; // Quanto il gruppo di persone sta al tavolo
	private double tolleranza; // Tolleranza dei clienti nel restare al bancone del bar se non c'è posto al tavolo (é una probabilità)
	private Tavolo tavolo; // Tavolo da assegnare al gruppo di clienti
	
	public Event(Duration time, EventType type, int nPersone, Duration durata, double tolleranza, Tavolo tavolo) {
		super();
		this.time = time;
		this.type = type;
		this.nPersone = nPersone;
		this.durata = durata;
		this.tolleranza = tolleranza;
		this.tavolo = tavolo;
	}

	public Duration getTime() {
		return time;
	}

	public void setTime(Duration time) {
		this.time = time;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public int getnPersone() {
		return nPersone;
	}

	public void setnPersone(int nPersone) {
		this.nPersone = nPersone;
	}

	public Duration getDurata() {
		return durata;
	}

	public void setDurata(Duration durata) {
		this.durata = durata;
	}

	public double getTolleranza() {
		return tolleranza;
	}

	public void setTolleranza(double tolleranza) {
		this.tolleranza = tolleranza;
	}

	public Tavolo getTavolo() {
		return tavolo;
	}

	public void setTavolo(Tavolo tavolo) {
		this.tavolo = tavolo;
	}

	// Gli eventi devono essere ordinabili in base alla priorità che vogliamo noi perchè vanno a finire in una coda prioritaria.
	// La priorità viene data dal tempo, cioè gli eventi più recenti hanno priorità maggiore
	@Override 
	public int compareTo(Event o) {
		return this.time.compareTo(o.getTime());
	}
	
	
	
	
}
