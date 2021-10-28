/*

Sistemas Distribuídos: Banco de Dados Chave-Valor (Cliente do Protocolo Ratis)
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
import java.net.*;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Cliente{

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        String grupoRaftID = "raft___grupo___1";
        Map<String, InetSocketAddress> id_end = new HashMap<>();
        
        id_end.put("s1", new InetSocketAddress("127.0.0.1", 3000));
        id_end.put("s2", new InetSocketAddress("127.0.0.1", 3500));
        id_end.put("s3", new InetSocketAddress("127.0.0.1", 4000));

        List<RaftPeer> enderecos = id_end.entrySet()
                                         .stream()
                                         .map(e -> RaftPeer.newBuilder().setId(e.getKey()).setAddress(e.getValue()).build())
                                         .collect(Collectors.toList());

        final RaftGroup grupoRaft = RaftGroup.valueOf(RaftGroupId.valueOf(ByteString.copyFromUtf8(grupoRaftID)), enderecos);
        RaftProperties propriedades = new RaftProperties();
        RaftClient cliente = RaftClient.newBuilder()
                                      .setProperties(propriedades)
                                      .setRaftGroup(grupoRaft)
                                      .setClientRpc(new GrpcFactory(new Parameters())
                                      .newRaftClientRpc(ClientId.randomId(), propriedades))
                                      .build();

        RaftClientReply operacao;
        String resposta = "";
        byte[] buffer;
        ServerSocket socket_portal = new ServerSocket(5100);

        while(true){

            String[] requisicao;
            System.out.println("Esperando cliente na porta 5100");

            Socket socket_conexao = socket_portal.accept();
            System.out.println("Portal" + socket_conexao.getInetAddress() + ":" + socket_conexao.getPort() + " conectado");

            BufferedReader dados = new BufferedReader(new InputStreamReader(socket_conexao.getInputStream()));
            requisicao = dados.readLine().split(",");
            socket_conexao.close();

            switch(requisicao[0]){
                
                case "inserir_cliente":
                    
                    operacao = cliente.io().send(Message.valueOf("inserir_cliente," + requisicao[1] + "," + requisicao[2]));
                    resposta = operacao.getMessage().getContent().toString(Charset.defaultCharset());
                    System.out.println("Resposta: " + resposta);
                    break;
                    
                case "modificar_cliente":
                    
                    operacao = cliente.io().send(Message.valueOf("modificar_cliente," + requisicao[1] + "," + requisicao[2]));
                    resposta = operacao.getMessage().getContent().toString(Charset.defaultCharset());
                    System.out.println("Resposta: " + resposta);
                    break;
                    
                case "remover_cliente":
                    
                    operacao = cliente.io().send(Message.valueOf("remover_cliente," + requisicao[1]));
                    resposta = operacao.getMessage().getContent().toString(Charset.defaultCharset());
                    System.out.println("Resposta: " + resposta);
                    break;
 
                default:
                
                    System.out.println("Comando inválido");
            }

            //Enviando as modificações para todos os processos
            DatagramSocket socket_ratis = new DatagramSocket();
            InetAddress endereco = InetAddress.getByName("224.1.1.1");
            buffer = resposta.getBytes();
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, endereco, 5101);
            
            socket_ratis.send(pacote);

            cliente.close();
        }
    }
}
