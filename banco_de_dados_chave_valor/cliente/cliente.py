'''

Sistemas Distribuídos: Banco de Dados Chave-Valor (Cliente)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import socket               
from configuracao import *
from menu_cliente import menuCliente

socket_portal = socket.socket()                  

#Conexão com o portal cliente
def comunicacaoPortal(host, porta):
	
	try:
	
		socket_portal.connect((host, porta))                       
	
	except:
	
		return comunicacaoPortal(host, porta + 1)
	
	return host, porta

#Validação do id inserido
def validarID():

  while True:

    id = input("Insira o id: ")

    if not id.isdigit():
      
      print("O id deve ser um número inteiro")
    
    else:
    
      break
  
  return int(id)

#Menu do cliente
def menu():

	print("\n-------------MENU-------------")
	print(f"{menuCliente.INSERIR_TAREFA.value} - Inserir tarefa")
	print(f"{menuCliente.MODIFICAR_TAREFA.value} - Modificar tarefa")
	print(f"{menuCliente.LISTAR_TAREFA.value} - Listar tarefas")
	print(f"{menuCliente.REMOVER_TODAS_TAREFAS.value} - Remover todas tarefas")
	print(f"{menuCliente.REMOVER_UMA_TAREFA.value} - Remover uma tarefa")
	print(f"{menuCliente.SAIR.value} - Sair")
	
	op = int(input("Insira a operação desejada:"))
	
	return op

#Executando o cliente, que processa a opção inserida e se comunica com o portal do cliente por socket
def run():
	
	while True:

		mensagem = menu()
		socket_portal.send(mensagem.encode())
		
		if mensagem == menuCliente.INSERIR_TAREFA.value:
		
			print("\nDigite o id, título e descrição da tarefa separados por vírgula: ")
			tarefa = input()
			socket_portal.send(tarefa.encode())
		
		elif mensagem == menuCliente.MODIFICAR_TAREFA.value:
		
			print("\nDigite o ID, título e a nova descrição da tarefa separados por vírgula: ")
			tarefa = input()
			socket_portal.send(tarefa.encode())
		
		elif mensagem == menuCliente.LISTAR_TAREFAS.value:
		
			print("\nDigite o id: ")
			id = input()
			socket_portal.send(id.encode())
		
		elif mensagem == menuCliente.REMOVER_TODAS_TAREFAS.value:
		
			print("\nDigite o id: ")
			id = input()
			socket_portal.send(id.encode())
		
		elif mensagem == clienteMenuEnum.REMOVER_UMA_TAREFA.value:
		
			print("\nDigite o ID e título da tarefa que deseja excluir separados por vírgula: ")
			tarefa = input()
			socket_portal.send(tarefa.encode())
		
        elif mensagem == clienteMenuEnum.SAIR.value:
			
	        print("Desconectando cliente")
			break

        else:
    
            print("Operação inválida")
			
		resposta = socket_portal.recv(tamanhoPacote)
		print("\nMensagem recebida: ", resposta.decode())

	socket_portal.close()

comunicacaoCliente(host, porta)
run()
