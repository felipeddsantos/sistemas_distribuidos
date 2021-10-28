'''

Sistemas Distribuídos: Ping Pong TCP (Cliente)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import socket      

print("Conectando-se ao servidor...")

s = socket.socket()
host = socket.gethostname()            
port = 12345                   
        
s.connect((host, port))

print("Conectado.")

while True:

    data = input("Digite mensagem: ")
    s.send(data.encode())

    if data == "SAIR":
    
        print("Desconectando...")
        
        s.close()
        
        break

    print("Mensagem enviada.\nEsperando Resposta.")
    
    data = s.recv(1024)

    print("Resposta recebida: " + data.decode())
