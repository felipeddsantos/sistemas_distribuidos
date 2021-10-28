'''

Sistemas Distribuídos: Banco de Dados Chave-Valor (Menu do Cliente)
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

'''

from enum import Enum

class menuCliente(Enum):

    INSERIR_TAREFA = 1
    MODIFICAR_TAREFA = 2
    LISTAR_TAREFAS = 3
    REMOVER_TODAS_TAREFAS = 4
    REMOVER_UMA_TAREFA = 5
    SAIR = 6
