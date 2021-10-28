'''

Sistemas Distribuídos: Ping Pong UDP (Servidor)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import socket                              

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)                         
host = socket.gethostname()              
port = 12345 
                               
s.bind((host, port))                                                    

while True:

    print("Esperando mensagem...")       
    
    data, address = s.recvfrom(4096)
    
    print("Mensagem recebida: " + data.decode())  
    
    data = input("Digite resposta: ")      
    s.sendto(data.encode(), address)
    
    print("Resposta enviada.")                        
