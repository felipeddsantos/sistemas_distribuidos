/*

Sistemas Distribuídos: Banco de Dados Chave-Valor (Máquina de Estados do Protocolo Ratis)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.BaseStateMachine;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MaquinaDeEstados extends BaseStateMachine{

    private final Map<String, String> clientes = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Message> applyTransaction(TransactionContext trx){
        
        final RaftProtos.LogEntryProto entrada = trx.getLogEntry();
        final String[] requisicao = entrada.getStateMachineLogEntry().getLogData().toString(Charset.defaultCharset()).split(",");
        final RaftProtos.RaftPeerRole role = trx.getServerRole();
        String resultado;
        
        if(requisicao[0].equals("inserir_cliente")){
            
            clientes.put(requisicao[1], requisicao[2]);
            resultado = "inserir_cliente," + requisicao[1] + "," + requisicao[2];
            LOG.info("{}:{} {} {}={}", role, getId(), requisicao[0], requisicao[1], requisicao[2]);
        }
            
        else if(requisicao[0].equals("modificar_cliente")){
            
            clientes.replace(requisicao[1], requisicao[2]);
            resultado = "modificar_cliente," + requisicao[1] + "," + requisicao[2];
            LOG.info("{}:{} {} {}={}", role, getId(), requisicao[0], requisicao[1], requisicao[2]);
        }
        
        else{
            
            clientes.remove(requisicao[1]);
            resultado = "remover_cliente," + requisicao[1];
            LOG.info("{}:{} {} {}", role, getId(), requisicao[0], requisicao[1]);
        }

        final CompletableFuture<Message> resposta = CompletableFuture.completedFuture(Message.valueOf(resultado));

        if(LOG.isTraceEnabled())
        
            LOG.trace("{}: chave/valores={}", getId(), clientes);
 
        return resposta;
    }
}
