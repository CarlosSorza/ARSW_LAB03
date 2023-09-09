package edu.eci.arswlistasnegraseficiente.blacklistvalidator;



import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arswlistasnegraseficiente.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class BLThread extends Thread{
	
	private HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
	private int ocurrences = 0;
	private String host;
	private int serverRA, serverRB;
	private LinkedList<Integer> blackListOcurrences=new LinkedList<>();
	private int BLALARMCOUNT;
	private int checkedListsCount;
	
	public BLThread(String host, int a, int b, int BLAC) {
		this.host = host;
		this.serverRA = a;
		this.serverRB = b;
		this.BLALARMCOUNT = BLAC;
		this.checkedListsCount = 0;
	}

	public int getOcurrences() {
		return ocurrences;
	}
	
	public void search() {
		for (int i = serverRA; i<serverRB && this.ocurrences<BLALARMCOUNT; i++){
            checkedListsCount++;
            
            if (skds.isInBlackListServer(i, host)){
            	synchronized (blackListOcurrences){
            		blackListOcurrences.add(i);
            	}
                
                this.ocurrences++;
            }
        }
	}
	
	public void run() {
        search();
        
        
	}
	
	public LinkedList<Integer> getBlacklistOcurrences(){
		return blackListOcurrences;
	}
	
	public int getCheckListCount() {
		return checkedListsCount;
	}
}