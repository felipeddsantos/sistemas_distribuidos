'''

Sistemas Distribuídos: Ping Pong UDP (Cliente)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

import socket      

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
host = socket.gethostname()            
port = 12345                   
        
s.connect((host, port))

while True:

    data = input("Digite mensagem: ")
    s.send(data.encode())
    
    print("Mensagem enviada.\nEsperando Resposta")
    
    data, address = s.recvfrom(4096)

    print("Resposta recebida: " + data.decode())
