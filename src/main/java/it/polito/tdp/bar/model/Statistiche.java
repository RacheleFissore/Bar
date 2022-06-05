package it.polito.tdp.bar.model;

public class Statistiche {
	private int clientiTot; // Arrivati in un certo intervallo di tempo
	private int clientiSoddisfatti; // Clienti che sono potuti rimanere a consumare al bar
	private int clientiInsoddisfatti; // Clienti che sono dovuti andare via perchè il bar era pieno
	
	public Statistiche() { // Alla creazione dell'oggetto questi parametri saranno a 0 e poi verranno incrementati con opportuni metodi
		super();
		this.clientiTot = 0;
		this.clientiSoddisfatti = 0;
		this.clientiInsoddisfatti = 0;
	}	
	
	public void incrementaClienti(int n) {
		this.clientiTot += n;
	}
	
	public void incrementaSoddisfatti(int n) {
		this.clientiSoddisfatti += n;
	}
	
	public void incrementaInsoddisfatti(int n) {
		this.clientiInsoddisfatti += n;
	}

	public int getClientiTot() {
		return clientiTot;
	}

	public int getClientiSoddisfatti() {
		return clientiSoddisfatti;
	}

	public int getClientiInsoddisfatti() {
		return clientiInsoddisfatti;
	}
	
	
	
	
	
}
