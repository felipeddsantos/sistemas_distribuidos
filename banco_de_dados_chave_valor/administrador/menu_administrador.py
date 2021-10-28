'''

Sistemas Distribuídos: Banco de Dados Chave-Valor (Menu do Administrador)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

from enum import Enum

class menuAdministrador(Enum):

    INSERIR_CLIENTE = 1
    MODIFICAR_CLIENTE = 2
    PROCURAR_CLIENTE = 3
    REMOVER_CLIENTE = 4
    SAIR = 5
