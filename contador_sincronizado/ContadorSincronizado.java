/*

Sistemas Distribuídos: Classe Contador Sincronizado
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

public class ContadorSincronizado{
    
    private int c = 0;

    public synchronized int inc(){
    
        System.out.println("Dentro: " + c);
        
        return ++c;
    }

    public synchronized int dec(){
        
        return --c;
    }

    public synchronized int value(){
        
        return c;
    }
}
