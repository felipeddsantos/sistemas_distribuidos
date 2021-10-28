/*

Sistemas Distribuídos: Anel de Threads
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <ctype.h>

int count = 0, cond = 1;
char str[] = "Ola... Estou Criando 30 THREADS Que TRANSFORMEM Cada Caractere Dessa STRING de Minusculo Para MAIUSCULO";
pthread_mutex_t mutex;

int lowerToUpper(){

    for(int ind = 0; str[ind] != '\0'; ind++){
                
        if(islower(str[ind])){
                
            str[ind] = toupper(str[ind]);
                    
            return 0;
        }
    }
    
    return 1;
}

void *threadFunction(void *id){
    
    long threadId = (long) id;
    
    while(cond){
        
        pthread_mutex_lock(&mutex);
        
        if(threadId == count){
                
            printf("Eu sou a thread %ld e imprimo a string: %s\n", threadId + 1, str);
            
            if(lowerToUpper()){
            
                cond = 0;
                pthread_mutex_unlock(&mutex);
                
                return NULL;
            }
            
            sleep(1);
            count = (threadId + 1) % 30;  
        }

        pthread_mutex_unlock(&mutex);
    }
    
    return NULL;
}

int main(){

    long thread, threadCount = 30;

    pthread_t *threadRing = malloc(threadCount * sizeof(pthread_t));

    pthread_mutex_init(&mutex, NULL);
    
    for(thread = 0; thread < threadCount; thread++)
    
        pthread_create(&threadRing[thread], NULL, threadFunction, (void*) thread);
   
    for(thread = 0; thread < threadCount; thread++)
    
        pthread_join(threadRing[thread], NULL);

    printf("String final: %s\n", str);

    pthread_mutex_destroy(&mutex);
    free(threadRing);
    
    return 0;
}
