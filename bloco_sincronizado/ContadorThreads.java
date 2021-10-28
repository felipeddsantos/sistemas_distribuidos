/*

Sistemas Distribuídos: Bloco Sincronizado (Classe Contador de Threads)
Aluno: Felipe Daniel Dias dos Santos
Matrícula: 11711ECP004
Curso: Engenharia de Computação

*/

public class ContadorThreads{

    public static BlocoSincronizado c = new BlocoSincronizado();

    public static class Thread1 implements Runnable{

        public void run(){
    
            for(int i = 0; i < 1000; i++){
    
                c.inc1();
                System.out.println("Valor de c1 na thread 1: " + c.valuec1());
                c.inc2();
                System.out.println("Valor de c2 na thread 1: " + c.valuec2());
            }
        }
    }

    public static class Thread2 implements Runnable{

        public void run(){
    
            for(int i = 0; i < 1000; i++){
    
                c.inc2();
                System.out.println("Valor de c2 na thread 2: " + c.valuec2());
                c.inc1();
                System.out.println("Valor de c1 na thread 2: " + c.valuec1());
            }   
        }
    }

    public static void main(String args[]){
    
        Thread[] t = new Thread[2];
        
        t[0] = new Thread(new Thread1()); 
        t[0].start();
        
        t[1] = new Thread(new Thread2()); 
        t[1].start();

        for(int i = 0; i < 2; i++){
        
            try{
            
                t[i].join();
            } 
        
            catch(InterruptedException ie){
            
                System.out.println("A espera foi interrompida");
            }
        }           
    }
}
