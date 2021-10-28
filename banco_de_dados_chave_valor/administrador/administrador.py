'''

Sistemas Distribuídos: Banco de Dados Chave-Valor (Administrador)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import logging
import grpc
import helloworld_pb2
import helloworld_pb2_grpc
from __future__ import print_function
from menu_administrador import menuAdministrador

#Validação do id inserido
def validarID():

  while True:

    id = input("Insira o id: ")

    if not id.isdigit():

      print("O id deve ser um número inteiro")

    else:

      break

  return int(id)

#Menu do administrador
def menu():

  print("\n-------------MENU-------------")
  print(f"{menuAdministrador.INSERIR_CLIENTE.value} - Inserir cliente")
  print(f"{menuAdministrador.MODIFICAR_CLIENTE.value} - Modificar um cliente")
  print(f"{menuAdministrador.PROCURAR_CLIENTE.value} - Procurar cliente")
  print(f"{menuAdministrador.REMOVER_CLIENTE.value} - Deletar cliente")
  print(f"{menuAdministrador.SAIR.value} - Sair")
  
  op = int(input("Insira a operação desejada:"))
  
  return op

#Executando o administrador, que processa a opção inserida e se comunica com o portal do administrador por GRPC
def run():

  canal = grpc.insecure_channel("localhost:50051")
  stub = helloworld_pb2_grpc.GreeterStub(canal)
  
  while True:

    op = menu()

    if op == menuAdministrador.INSERIR_CLIENTE.value or op == menuAdministrador.MODIFICAR_CLIENTE.value:
     
      id = validarID()
      nome = input("Digite o nome de usuário: ")

      if op == menuAdministrador.INSERIR_CLIENTE.value:
    
        resposta = stub.inserirCliente(helloworld_pb2.RequisicaoInsercao(id = id, nome = nome))
        print("\nCliente recebido: " + resposta.mensagem)
      
      else:
    
        resposta = stub.modificarCliente(helloworld_pb2.RequisicaoModificacao(id = id, nome = nome))
        print("\nCliente recebido: " + resposta.mensagem)
    
    elif op == menuAdministrador.PROCURAR_CLIENTE.value or op == menuAdministrador.REMOVER_CLIENTE.value:
      
      id = validarID()

      if op == menuAdministrador.PROCURAR_CLIENTE.value:
      
        resposta = stub.procurarCliente(helloworld_pb2.RequisicaoProcura(id = id))
        print("\nCliente recebido: " + resposta.mensagem)
      
      else:
      
        resposta = stub.removerCliente(helloworld_pb2.RequisicaoRemocao(id = id))
        print("\nCliente recebido: " + resposta.mensagem)
    
    elif op == menuAdministrador.SAIR.value:
    
      print("Desconectando administrador")
      break

    else:
    
        print("Operação inválida")
    
if __name__ == "__main__":
    
    logging.basicConfig()
    run()
