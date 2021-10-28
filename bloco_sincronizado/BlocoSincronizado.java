/*

Sistemas Distribuídos: Classe Bloco Sincronizado
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

public class BlocoSincronizado{
    
    private long c1 = 0;
    private long c2 = 0;
    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public void inc1(){
        
        synchronized(lock1){
            
            c1++;
        }
    }

    public void inc2(){
        
        synchronized(lock2){
            
            c2++;
        }
    }

    public long valuec1(){
        
        synchronized(lock1){
            
            return c1;
        }
    }
    
    public long valuec2(){
        
        synchronized(lock2){
            
            return c2;
        }
    }
}
