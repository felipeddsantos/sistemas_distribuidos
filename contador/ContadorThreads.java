/*

Sistemas Distribuídos: Contador (Classe Contador de Threads)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

public class ContadorThreads implements Runnable{

    public static Contador c = new Contador();

    public void run(){
    
        for(int i = 0; i < 20; i++){
    
            System.out.println("Saudações da " + Thread.currentThread().getName());
            c.inc();
            System.out.println("Valor atual do contador: " + c.value());
        }
    }

    public static void main(String args[]){
    
        Thread[] t = new Thread[10];
    
        for(int i = 0; i < 10; i++){
        
            t[i] = new Thread(new ContadorThreads()); 
            t[i].start();
        }

        for(int i = 0; i < 10; i++){
        
            try{
            
                t[i].join();
            } 
        
            catch(InterruptedException ie){
            
                System.out.println("A espera foi interrompida");
            }
        }           
    }
}
