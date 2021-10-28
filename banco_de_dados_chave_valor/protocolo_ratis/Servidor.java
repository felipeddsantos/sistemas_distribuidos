/*

Sistemas Distribuídos: Banco de Dados Chave-Valor (Servidor do Protocolo Ratis)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.util.LifeCycle;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Servidor{

    public static void main(String[] args) throws IOException, InterruptedException{
    
        String grupoRaftID = "raft___grupo___1";
        Map<String, InetSocketAddress> id_end = new HashMap<>();
        
        id_end.put("s1", new InetSocketAddress("127.0.0.1", 3000));
        id_end.put("s2", new InetSocketAddress("127.0.0.1", 3500));
        id_end.put("s3", new InetSocketAddress("127.0.0.1", 4000));

        List<RaftPeer> enderecos = id_end.entrySet()
                                         .stream()
                                         .map(e -> RaftPeer.newBuilder().setId(e.getKey()).setAddress(e.getValue()).build())
                                         .collect(Collectors.toList());

        RaftPeerId id = RaftPeerId.valueOf(args[0]);

        if(enderecos.stream().noneMatch(p -> p.getId().equals(id))){
        
            System.out.println("Identificador " + args[0] + " inválido");
            System.exit(1);
        }

        RaftProperties propriedades = new RaftProperties();
        propriedades.setInt(GrpcConfigKeys.OutputStream.RETRY_TIMES_KEY, Integer.MAX_VALUE);
        GrpcConfigKeys.Server.setPort(propriedades, id_end.get(args[0]).getPort());
        RaftServerConfigKeys.setStorageDir(propriedades, Collections.singletonList(new File("/tmp/" + id)));
        
        final RaftGroup grupoRaft = RaftGroup.valueOf(RaftGroupId.valueOf(ByteString.copyFromUtf8(grupoRaftID)), enderecos);
        RaftServer servidorRaft = RaftServer.newBuilder()
                                            .setServerId(id)
                                            .setStateMachine(new MaquinaDeEstados()).setProperties(propriedades)
                                            .setGroup(grupoRaft)
                                            .build();
        
        servidorRaft.start();

        while(servidorRaft.getLifeCycleState() != LifeCycle.State.CLOSED)
            
            TimeUnit.SECONDS.sleep(1);
    }
}
