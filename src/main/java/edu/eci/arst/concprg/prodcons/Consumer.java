/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private final Queue<Integer> queue;
    
    
    public Consumer(Queue<Integer> queue){
        this.queue=queue;        
    }
    
    private void  consumir () throws InterruptedException {

        synchronized (queue){
            while (queue.isEmpty()){
                queue.wait();
            }
            if (queue.size() > 0) {
                int elem=queue.poll();
                Thread.sleep(1000);
                System.out.println("Consumer consumes "+elem);
            }
            queue.notifyAll();
        }

    }

    @Override
    public void run() {
        while (true) {

            try{
                consumir();
            }
            catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
            
        }
    }
}
