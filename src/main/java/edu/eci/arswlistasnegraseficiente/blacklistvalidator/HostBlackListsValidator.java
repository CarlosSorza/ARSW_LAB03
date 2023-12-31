/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arswlistasnegraseficiente.blacklistvalidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arswlistasnegraseficiente.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        int ocurrencesCount=0;
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int checkedListsCount=0;
        
        ArrayList<BLThread> blThreads = new ArrayList<BLThread>();
        
        int range = skds.getRegisteredServersCount() / N;
        
        for (int i = 0; i < N; i ++) {
        	if (i < N - 1) {
        		BLThread thr = new BLThread(ipaddress, i * range, ((i * range) + range) - 1, BLACK_LIST_ALARM_COUNT);
        		thr.start();
        		blThreads.add(thr);
        	}else {
        		BLThread thr = new BLThread(ipaddress, i * range, skds.getRegisteredServersCount() ,BLACK_LIST_ALARM_COUNT);
        		thr.start();
        		blThreads.add(thr);
        	}
        }
        
        for (BLThread e : blThreads) {
			
			try {
				e.join();
				blackListOcurrences.addAll(e.getBlacklistOcurrences());
				ocurrencesCount += e.getOcurrences();
				checkedListsCount += e.getCheckListCount();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
        	
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }               
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}