/*

Sistemas Distribuídos: Protocolo Ratis (Classe Cliente)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcFactory;
import org.apache.ratis.protocol.*;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import java.io.*;
import java.io.IOException;
import java.net.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Cliente{
    
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException{

        String raftGroupID = "raft___grupo___1";
        Map<String, InetSocketAddress> id2addr = new HashMap<>();
 
        id2addr.put("s1", new InetSocketAddress("127.0.0.1", 3000));
        id2addr.put("s2", new InetSocketAddress("127.0.0.1", 3500));
        id2addr.put("s3", new InetSocketAddress("127.0.0.1", 4000));

        List<RaftPeer> addresses = id2addr.entrySet()
                                          .stream()
                                          .map(e -> RaftPeer.newBuilder().setId(e.getKey()).setAddress(e.getValue()).build())
                                          .collect(Collectors.toList());

        final RaftGroup raftGroup = RaftGroup.valueOf(RaftGroupID.valueOf(ByteString.copyFromUtf8(raftGroupId)), addresses);
        RaftProperties raftProperties = new RaftProperties();

        RaftClient client = RaftClient.newBuilder()
                                      .setProperties(raftProperties)
                                      .setRaftGroup(raftGroup)
                                      .setClientRpc(new GrpcFactory(new Parameters())
                                      .newRaftClientRpc(ClientId.randomId(), raftProperties))
                                      .build();

        RaftClientReply getValue;
        CompletableFuture<RaftClientReply> compGetValue;
        String response = "";
        
        switch(args[0]){
                
            case "add":
            
                getValue = client.io().send(Message.valueOf("add:" + args[1] + ":" + args[2]));
                response = getValue.getMessage().getContent().toString(Charset.defaultCharset());
                System.out.println("Resposta: " + response);
                break;

            case "add_async":
                
                compGetValue = client.async().send(Message.valueOf("add:" + args[1] + ":" + args[2]));
                getValue = compGetValue.get();
                response = getValue.getMessage().getContent().toString(Charset.defaultCharset());
                System.out.println("Resposta: " + response);
                break;
                
            case "get":
            
                getValue = client.io().sendReadOnly(Message.valueOf("get:" + args[1]));
                response = getValue.getMessage().getContent().toString(Charset.defaultCharset());
                System.out.println("Resposta: " + response);
                break;
                
            case "get_stale":
               
                getValue = client.io().sendStaleRead(Message.valueOf("get:" + args[1]), 0, RaftPeerId.valueOf(args[2]));
                response = getValue.getMessage().getContent().toString(Charset.defaultCharset());
                System.out.println("Resposta: " + response);
                break;
                
            case "delete":
            
                getValue = client.io().send(Message.valueOf("delete:" + args[1]));
                response = getValue.getMessage().getContent().toString(Charset.defaultCharset());
                System.out.println("Resposta: " + response);
                break;
                    
            case "update":
            
                getValue = client.io().send(Message.valueOf("update:" + args[1] + ":" + args[2]));
                response = getValue.getMessage().getContent().toString(Charset.defaultCharset());
                System.out.println("Resposta: " + response);
                break;
 
            default:
            
                System.out.println("Comando inválido");
        }
        
        client.close();
    }
}
