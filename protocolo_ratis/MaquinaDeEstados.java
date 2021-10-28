/*

Sistemas Distribuídos: Protocolo Ratis (Classe Máquina de Estados)
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

    private final Map<String, String> key2values = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Message> query(Message request){
    
        final String[] opKey = request.getContent().toString(Charset.defaultCharset()).split(":");
        final String result = opKey[0]+ ":"+ key2values.get(opKey[1]);

        LOG.info("{}:{}={}", opKey[0], opKey[1], result);
        
        return CompletableFuture.completedFuture(Message.valueOf(result));
    }

    @Override
    public CompletableFuture<Message> applyTransaction(TransactionContext trx){
        
        final RaftProtos.LogEntryProto entry = trx.getLogEntry();
        final String[] opKeyValue = entry.getStateMachineLogEntry().getLogData().toString(Charset.defaultCharset()).split(":");
        final RaftProtos.RaftPeerRole role = trx.getServerRole();
        String result;
        
        if(opKeyValue[0].equals("add")){
            
            key2values.put(opKeyValue[1], opKeyValue[2]);
            result = "add:" + opKeyValue[1] + ":" + opKeyValue[2];
            LOG.info("{}:{} {} {}={}", role, getId(), opKeyValue[0], opKeyValue[1], opKeyValue[2]);
        }
        
        else if(opKeyValue[0].equals("delete")){
            
            key2values.remove(opKeyValue[1]);
            result = "delete:" + opKeyValue[1];
            LOG.info("{}:{} {} {}", role, getId(), opKeyValue[0], opKeyValue[1]);
        }
            
        else{
            
            key2values.replace(opKeyValue[1], opKeyValue[2]);
            result = "update:" + opKeyValue[1] + ":" + opKeyValue[2];
            LOG.info("{}:{} {} {}={}", role, getId(), opKeyValue[0], opKeyValue[1], opKeyValue[2]);
        }

        final CompletableFuture<Message> f = CompletableFuture.completedFuture(Message.valueOf(result));

        if(LOG.isTraceEnabled())
        
            LOG.trace("{}:key/values={}", getId(), key2values);
 
        return f;
    }
}
