'''

Sistemas Distribuídos: Ping Pong TCP (Servidor)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import socket                              

s = socket.socket()                         
host = socket.gethostname()                 
port = 12345 
                               
s.bind((host, port))                        
s.listen(2)                              

while True:

    print("Esperando conexão...")
    
    c, address = s.accept() 
    
    print("Conectado.")                 

    while True:             
        
        print("Esperando mensagem...")       
        
        data = c.recv(1024)
        data = data.decode()
        
        if data == "SAIR":
        
            print("Conexão encerrada.")
 
            c.close()
            
            break
    
        print("Mensagem recebida: " + data)     

        data = input("Digite resposta: ")   
        c.send(data.encode())  
    
        print("Resposta enviada.")                        
