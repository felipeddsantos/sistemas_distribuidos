'''

Sistemas Distribuídos: Banco de Dados Chave-Valor (Portal do Administrador)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import logging
import socket
import struct
import time
import threading
import grpc
import helloworld_pb2
import helloworld_pb2_grpc
from concurrent import futures

clientes = dict([])

#Atualização do banco de dados
def atualizarClientes(requisicao):
   
   global clientes

   mensagem = []
   mensagem = str(requisicao).split(",")
   
   mensagem[0] = mensagem[0].split("'")[1]
   mensagem[1] = int(mensagem[1].split("'")[0])
   mensagem[2] = mensagem[2].split("'")[0]
   
   if mensagem[0] == "inserir_cliente":
    
    if mensagem[1] not in clientes.keys():
    
      clientes[mensagem[1]] = mensagem[2]
   
   elif mensagem[0] == "modificar_cliente":
   
    clientes[mensagem[1]] = mensagem[2]
   
   elif mensagem[0] == "remover_cliente":
   
    del clientes[mensagem[1]]
      
   print(clientes)

#Enviando atualização do banco de dados para o processo responsável pelo cliente Ratis
def remetenteRatis(requisicao):
  
    socket_ratis = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socket_ratis.connect(("localhost", 5100))
    socket_ratis.send(requisicao.encode())
    socket_ratis.close()

#Recebendo atualização do banco de dados do processo responsável pelo cliente Ratis, requisitado pelos outros portais
def destinatarioRatis():
  
  while True:
    
    socket_ratis = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    socket_ratis.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    socket_ratis.bind(("", 5101))

    req = struct.pack("=4sl", socket.inet_aton("224.1.1.1"), socket.INADDR_ANY)
    socket_ratis.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, req)
    requisicao = socket_ratis.recvfrom(1024)
    atualizarClientes(requisicao)

#Comunicação com o administrador por GRPC
class ComunicacaoAdministrador(helloworld_pb2_grpc.GreeterServicer):

  def inserirCliente(self, requisicao, context):
  
    if requisicao.id in clientes.keys():
  
      return helloworld_pb2.Resposta(mensagem = "Já existe um cliente com esse id")
  
    print("Inserindo cliente " + requisicao.nome + " de id " + str(requisicao.id))
    
    remetenteRatis("inserir_cliente," + str(requisicao.id) + "," + str(requisicao.nome) + ",/n")
    
    return helloworld_pb2.Resposta(mensagem = "Cliente inserido com sucesso")
  
  def modificarCliente(self, requisicao, context):
  
    if requisicao.id not in clientes.keys():
  
      return helloworld_pb2.Resposta(mensagem = "Cliente inexistente")
    
    remetenteRatis("modificar_cliente," + str(requisicao.id) + "," + str(requisicao.nome) + ",/n")
    
    return helloworld_pb2.Resposta(mensagem = "Cliente modificado com sucesso")

  def procurarCliente(self, requisicao, context):
  
    if requisicao.id not in clientes.keys():
  
      return helloworld_pb2.Resposta(mensagem = "Cliente inexistente")
    
    return helloworld_pb2.Resposta(mensagem = clientes[requisicao.id])

  def removerCliente(self, requisicao, context):
  
    if requisicao.id not in clientes.keys():
  
      return helloworld_pb2.Resposta(mensagem = "Cliente inexistente")
    
    remetenteRatis("remover_cliente," + str(requisicao.id) + ",/n")

    return helloworld_pb2.Resposta(mensagem = "Cliente removido com sucesso")

#Executando o portal do administrador que se comunica com o administrador, por GRPC, e com o cliente Ratis, por socket
def run():
  
    ratis_thread = threading.Thread(target = destinatarioRatis, args = ())
    ratis_thread.start()
   
    servidor = grpc.server(futures.ThreadPoolExecutor(max_workers = 10))
    helloworld_pb2_grpc.add_GreeterServicer_to_server(ComunicacaoAdministrador(), servidor)
    servidor.add_insecure_port("[::]:50051")
    servidor.start()
    servidor.wait_for_termination()

if __name__ == "__main__":
    
    logging.basicConfig()
    run()
