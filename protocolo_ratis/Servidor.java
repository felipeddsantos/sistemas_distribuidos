/*

Sistemas Distribuídos: Protocolo Ratis (Classe Servidor)
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
        
        String raftGroupID = "raft___grupo___1";
        Map<String, InetSocketAddress> id2addr = new HashMap<>();
        
        id2addr.put("s1", new InetSocketAddress("127.0.0.1", 3000));
        id2addr.put("s2", new InetSocketAddress("127.0.0.1", 3500));
        id2addr.put("s3", new InetSocketAddress("127.0.0.1", 4000));

        List<RaftPeer> addresses = id2addr.entrySet()
                                          .stream()
                                          .map(e -> RaftPeer.newBuilder().setId(e.getKey()).setAddress(e.getValue()).build())
                                          .collect(Collectors.toList());

        RaftPeerId myID = RaftPeerId.valueOf(args[0]);

        if(addresses.stream().noneMatch(p -> p.getId().equals(myID))){
        
            System.out.println("Identificador " + args[0] + " é inválido");
            System.exit(1);
        }

        RaftProperties properties = new RaftProperties();
        properties.setInt(GrpcConfigKeys.OutputStream.RETRY_TIMES_KEY, Integer.MAX_VALUE);
        GrpcConfigKeys.Server.setPort(properties, id2addr.get(args[0]).getPort());
        RaftServerConfigKeys.setStorageDir(properties, Collections.singletonList(new File("/tmp/" + myID)));
        final RaftGroup raftGroup = RaftGroup.valueOf(RaftGroupID.valueOf(ByteString.copyFromUtf8(raftGroupId)), addresses);
        
        RaftServer raftServer = RaftServer.newBuilder()
                                          .setServerId(myId)
                                          .setStateMachine(new MaquinaDeEstados()).setProperties(properties)
                                          .setGroup(raftGroup)
                                          .build();
                                          
        raftServer.start();

        while(raftServer.getLifeCycleState() != LifeCycle.State.CLOSED)
            
            TimeUnit.SECONDS.sleep(1);
    }
}
