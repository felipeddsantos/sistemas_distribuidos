/*

Sistemas Distribuídos: Classe Contador
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

public class Contador{

    private int c = 0;

    public int inc(){

        return ++c;
    }

    public int dec(){
 
        return --c;
    }

    public int value(){

        return c;
    }
}
