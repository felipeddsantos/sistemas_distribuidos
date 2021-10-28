'''

Sistemas Distribuídos: Banco de Dados Chave-Valor (Portal do Cliente)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import socket  
import threading
import struct
import time
from configuracao import *
from menu_cliente import menuCliente 

tarefas = dict([])
clientes = dict([])
socket_portal = socket.socket()

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
   print(tarefas)

#Conexão com o cliente
def conexaoCliente(host, porta):

   try:
   
      socket_portal.bind((host, porta))                      
   
   except:
   
      return comunicacaoCliente(host, porta + 1)
   
   return host, porta
   
#Recebendo atualização do banco de dados do processo responsável pelo cliente Ratis, requisitado pelos outros portais
def comunicacaoRatis():
  
  while True:
    
    ratis_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    ratis_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    ratis_socket.bind(("224.1.1.1", 5101))

    req = struct.pack("=4sl", socket.inet_aton("224.1.1.1"), socket.INADDR_ANY)
    ratis_socket.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, req)
    requisicao = r_socket.recvfrom(tamanhoPacote)
    atualizarClientes(requisicao)

#Comunicação com o cliente por socket
def comunicacaoCliente(socket_cliente, endereco_cliente):
   
   global tarefas
   
   print("Conexão com o cliente no endereço: ", endereco_cliente)

   try:
      
      while True:
      
         op = socket_cliente.recv(tamanhoPacote)
         op = op.decode()
         print("[" + str(endereco_cliente) + "] Mensagem recebida: " + op)
      
         if int(op) == menuCliente.SAIR.value:
      
               print("Desconectando cliente")
               break

         elif int(op) == menuCliente.INSERIR_TAREFA.value:

               requisicao = socket_cliente.recv(tamanhoPacote)
               requisicao = requisicao.decode()
               requisicao = requisicao.split(",")
               requisicao[0] = int(requisicao[0])

               if len(requisicao) == 3:
                  
                  if requisicao[0] not in clientes.keys():
                     
                     socket_cliente.send("Cliente inexistente".encode())
                  
                  else:
                  
                     if requisicao[0] not in tarefas.keys():
                  
                        tarefas[requisicao[0]] = []

                     tarefas[requisicao[0]].append((requisicao[1], requisicao[2]))
                     
                     print(tarefas)
                     socket_cliente.send("Tarefa inserida com sucesso".encode())
 
               else:
 
                  socket_cliente.send("Entrada inválida".encode())

         elif int(op) == menuCliente.MODIFICAR_TAREFA.value:
               
               requisicao = socket_cliente.recv(tamanhoPacote)
               requisicao = requisicao.decode()
               requisicao = requisicao.split(",")
               requisicao[0] = int(requisicao[0])

               if len(requisicao) == 3:
                  
                  if requisicao[0] not in clientes.keys():
                  
                     socket_cliente.send("Cliente inexistente".encode())
                  
                  else:
                  
                     id = requisicao[0]

                  if id not in tarefas.keys():
      
                     socket_cliente.send("Tarefa inexistente".encode())
      
                  else:
      
                     indice = -1
                     
                     for i in range(0, len(tarefas[id])):
                           
                           if(tarefas[id][i][0] == requisicao[1]):
                              
                              indice = i
                              break

                     if indice == -1:
                           
                           socket_cliente.send("Tarefa inexistente".encode())
                     
                     else:
                     
                           tarefas[id][indice] = (requisicao[1], requisicao[2])
                           socket_cliente.send("Tarefa modificada com sucesso".encode())
               
               else:
               
                  socket_cliente.send("Entrada inválida".encode())

         elif int(op) == menuCliente.LISTAR_TAREFAS.value:
               
               requisicao = socket_cliente.recv(tamanhoPacote)
               requisicao = requisicao.decode()
               requisicao = requisicao.split(",")
               requisicao[0] = int(requisicao[0])

               existe = False

               if len(requisicao) == 1:
                  
                  if requisicao[0] not in clientes.keys():
                  
                     socket_cliente.send("Cliente inexistente".encode())
                  
                  else:
                  
                     if requisicao[0] not in clientes.keys():
                  
                        socket_cliente.send("Cliente inexistente".encode())
                  
                     else:
                  
                        if requisicao[0] not in tarefas.keys():
                  
                           socket_cliente.send("Esse cliente não possui tarefas".encode())
                  
                        else:
                  
                           resposta = str(tarefas[requisicao[0]])
                           socket_cliente.send(resposta.encode())

               else:
                  
                  socket_cliente.send("Entrada inválida".encode())

         elif int(op) == menuCliente.REMOVER_TODAS_TAREFAS.value:
               
               requisicao = socket_cliente.recv(tamanhoPacote)
               requisicao = requisicao.decode()
               requisicao = requisicao.split(",")
               requisicao[0] = int(requisicao[0])

               if len(requisicao) == 1:
                  
                  if requisicao[0] not in clientes.keys():
                     
                     socket_cliente.send("Cliente inexistente".encode())
                  
                  else:
                  
                     if requisicao[0] not in tarefas.keys():
                  
                        socket_cliente.send("Esse cliente não possui tarefas".encode())
                  
                     else:
                  
                        tarefas[requisicao[0]] = []
                        socket_cliente.send("Tarefas removidas com sucesso".encode())
               else:
               
                  socket_cliente.send("Entrada inválida".encode())

         elif int(op) == menuCliente.REMOVER_UMA_TAREFA.value:
               
               requisicao = socket_cliente.recv(tamanhoPacote)
               requisicao = requisicao.decode()
               requisicao = requisicao.split(",")
               requisicao[0] = int(requisicao[0])

               if len(requisicao) == 2:
                  
                  if requisicao[0] not in clientes.keys():
                     
                     socket_cliente.send("Cliente inexistente".encode())
                  
                  else:
                  
                     id = requisicao[0]

                  if id not in tarefas.keys():
                     
                     socket_cliente.send("Tarefa inexistente".encode())
                  
                  else:
                  
                     indice = -1
                     
                     for i in range(0, len(tarefas[id])):
                           
                           if(tarefas[id][i][0] == requisicao[1]):
                           
                              indice = i
                              break

                     if indice == -1:
              
                           socket_cliente.send("Tarefa inexistente".encode())
              
                     else:
              
                           del tarefas[id][indice]                   
                           socket_cliente.send("Tarefa removida com sucesso".encode())
               else:
                  
                  socket_cliente.send("Entrada inválida".encode())
         
         else:
         
               socket_cliente.send("Operação inválida".encode())
      
      socket_cliente.close()
   
   except:
   
      print("Erro inesperado. Desconectando cliente do endereço " + str(endereco_cliente))

#Executando o portal do cliente que se comunica com o cliente e com o cliente Ratis, por socket
def run():

   global porta_cliente
   
   ratis_thread = threading.Thread(target = comunicacaoRatis, args = ())
   ratis_thread.start()
    
   socket_cliente.listen(5)
   
   while True:
      
      socket_cliente, endereco_cliente = socket_cliente.accept()
      cliente_thread = threading.Thread(target = comunicacaoCliente, args = (socket_cliente, endereco_cliente))
      cliente_thread.start()
      print("Conexões ativas: " + str((threading.activeCount() - 1)))
   
   socket_cliente.close()

print("Servidor iniciando")
host_cliente, porta_cliente = conexaoCliente(host, porta)
run()
