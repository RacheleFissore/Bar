package it.polito.tdp.bar.model;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import it.polito.tdp.bar.model.Event.EventType;

public class Simulator {
	// Modello
	private List<Tavolo> tavoli; // E' ciò che modella la nostra simulazione del bar
	
	// Parametri della simulazione 
	private int NUM_EVENTI = 2000;
	private int T_ARRIVO_MAX = 10; // I gruppi di persone arrivano a distanza di 10 minuti l'uno dall'altro
	private int NUM_PERSONE_MAX = 10;
	private int DURATA_MIN = 60; // Ogni gruppo sta al bar tra i 60 e i 120 minuti
	private int DURATA_MAX = 120;
	private double TOLLERANZA_MAX = 0.9; // Tolleranza del 90%
	private double OCCUPAZIONE_MAX = 0.5; // Il tavolo viene dato se il gruppo ne occupa almeno il 50%
	
	// Coda degli eventi
	private PriorityQueue<Event> queue;
	
	// Statistiche --> permettono di ritornare i risultati
	private Statistiche statistiche;
	
	private void creaTavolo(int quantita, int dimensione) {
		for (int i = 0; i < quantita; i ++)
			this.tavoli.add(new Tavolo(dimensione,false));
	}
	
	private void creaTavoli() {
		creaTavolo(2,10); // Creo 2 tavoli da 10 persone
		creaTavolo(4,8); // Creo 4 tavoli da 8 persone
		creaTavolo(4,6); // Creo 4 tavoli da 6 persone
		creaTavolo(5,4); // Creo 5 tavoli da 4 persone
		
		// Ordino la lista con i tavoli per dimensione per facilitare la ricerca del tavolo più piccolo
		Collections.sort(this.tavoli, new Comparator<Tavolo>() {

			@Override
			public int compare(Tavolo o1, Tavolo o2) {
				return o1.getPosti() - o2.getPosti(); // Ordino i tavoli per dimensione
			}
			
		});
	}
	
	private void creaEventi() {
		Duration arrivo = Duration.ofMinutes(0); // Suppongo di partire dall'istante 0 e da li contare poi i minuti
		for (int i = 0; i < this.NUM_EVENTI; i++) {
			// Math.random(): numero tra 0 e 0.9999
			// Lo moltiplico per NUM_PERSONE_MAX che vale 10 e così ottengo un numero di persone tra 0 e 9 e sommo +1 in modo da andare da 1 a 10
			int nPersone = (int) (Math.random() * this.NUM_PERSONE_MAX + 1); // Prendo a caso un numero di persone tra 1 e 10
			// La durata deve essere almeno di 60 minuti, più un valore casuale tra 60 e 120 minuti, ossia un valore casuale in 60 minuti
			Duration durata = Duration.ofMinutes(this.DURATA_MIN + (int)(Math.random() * (this.DURATA_MAX - this.DURATA_MIN + 1)));
			double tolleranza = Math.random()*this.TOLLERANZA_MAX; // Valore casuale tra 0 e 0.9
			
			Event e = new Event(arrivo, EventType.ARRIVO_GRUPPO_CLIENTI, nPersone, durata, tolleranza, null); // Ancora non gli è stato assegnato un tavolo
																											  // quindi metto null come ultimo valore
			this.queue.add(e);
			
			// Il primo gruppo arriva al tempo 0, dopodichè i rimanenti arriveranno dopo un numero di minuti casuali tra 1 e 10 minuti
			arrivo = arrivo.plusMinutes((int)(Math.random() * this.T_ARRIVO_MAX + 1));
			
		}
	}
	
	
	public void init() {
		this.queue = new PriorityQueue<Event>();
		this.statistiche = new Statistiche();
		this.tavoli = new LinkedList<Tavolo>();
		creaTavoli();
		creaEventi();
	}
	
	
	public void run() {
		while(!queue.isEmpty()) {
			Event e = queue.poll();
			processaEvento(e); // Eseguo l'evento
		}
	}
	
	
	private void processaEvento(Event e) {
		switch (e.getType()){
			case ARRIVO_GRUPPO_CLIENTI:
				// Conto i clienti totali
				this.statistiche.incrementaClienti(e.getnPersone());
				
				// Cerco un tavolo da assegnare al gruppo di clienti
				Tavolo tavolo = null;
				
				// La nostra lista di tavoli va dal più piccolo al più grande in modo da assegnare ai clienti il tavolo più piccolo possibile, scorro la 
				// lista, vedo se il tavolo è libero, se è libero ed il numero di posti del tavolo è maggiore del numero di persone e il tavolo viene 
				// occupato almeno per il 50% dal gruppo di persone allora glielo assegno
				for (Tavolo t : this.tavoli) {
					// Scorro i tavoli che sono ordinati dal più piccolo al più grande
					if (!t.isOccupato() && t.getPosti() >= e.getnPersone() 
							&&
							t.getPosti() * this.OCCUPAZIONE_MAX <= e.getnPersone()) {
						tavolo = t; // Ho trovato il tavolo per i clienti
						break;
					}
				}
				
				if(tavolo != null) {
					System.out.format("Trovato un tavolo da %d per %d persone\n", tavolo.getPosti(), e.getnPersone());
					statistiche.incrementaSoddisfatti(e.getnPersone()); // I clienti sono soddisfatti perchè ho trovato un tavolo
					tavolo.setOccupato(true);
					e.setTavolo(tavolo); // Devo assegnare il tavolo all'evento
					// Dopo un po' i clienti si alzeranno, quindi aggiungo alla coda un nuovo evento di tipo TAVOLO_LIBERATO, che saranno inseriti
					// dentro la coda in base al tempo in cui si alzeranno. Il time di questo nuovo evento è dato dal momento in cui si alzeranno, cioè la 
					// somma del tempo a cui sono arrivati più la durata dell'evento, cioè quanto tempo sono stati seduti al tavolo
					queue.add(new Event(e.getTime().plus(e.getDurata()), EventType.TAVOLO_LIBERATO, e.getnPersone(), e.getDurata(), e.getTolleranza(), tavolo));
					
				} else {
					// C'è solo il bancone. In base alla tolleranza devo decidere se a quel gruppo di clienti andrà bene rimanere al bancone oppure no
					double bancone = Math.random(); // Es: se il numero preso a random è < dello 0,6 siamo nel 60% di probabilità che il gruppo si fermi al 
													// bancone, se cade dopo lo 0,6 siamo nel 40% di probabilità che invece il gruppo non si fermi
					if(bancone <= e.getTolleranza()) {
						// Sì, ci fermiamo. Il bancone ha capacità illimitata
						System.out.format("%d persone si fermano al bancone\n", e.getnPersone());
						statistiche.incrementaSoddisfatti(e.getnPersone());
					} else {
						// No, andiamo a casa
						System.out.format("%d persone vanno a casa\n", e.getnPersone());
						statistiche.incrementaInsoddisfatti(e.getnPersone());
					}
				}
				
				break;
			case TAVOLO_LIBERATO:
				e.getTavolo().setOccupato(false); // Reimposto il tavolo come libero
				break;
		}
	}
	
	
	public Statistiche getStatistiche() {
		return this.statistiche;
	}
	
	
	
}
