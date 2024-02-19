#include <stdio.h>
#include <string.h>
#include <stdlib.h>

float  somma (float n1, float n2);
float  moltiplicazione (float n1, float n2);
float  divisione (float n1, float n2);
float  sottrazione (float n1, float n2);


char* intToString(int num) {
	char *buffer = (char*)malloc(30 * sizeof(char));
	snprintf(buffer, 30, "%d", num);
	return buffer;
}

char* myStrcat(char *s1, const char *s2){
	char *s = (char*)calloc(strlen(s1)+strlen(s2)+1,sizeof(char));
	strcat(s, s1);
	strcat(s, s2);
	return s;
}

char* floatToString(float num) {
	char *buffer = (char*)malloc(30 * sizeof(char));
	snprintf(buffer, 30, "%f", num);
	return buffer;
}
int main(){
	int  scelta = 2;
	float  n2 ;
	float  n1 ;
	int  condizione1 = 1;
	int  condizione2 = 1;
while (condizione1&&condizione2) {
printf("Inserisci 2 per eseguire la somma");
printf("\n");
printf("Inserisci 3 per eseguire la moltiplicazione");
printf("\n");
printf("Inserisci 4 per eseguire la divisione");
printf("\n");
printf("Inserisci 5 per eseguire la sottrazione");
printf("\n");
printf("Inserisci 1 o 0 per chiudere il programma");
printf("Inserisci un numero : \t");
scanf("%d", &scelta);
if (scelta==2) {
printf("Inserisci primo operando : \t");
scanf("%f", &n1);
printf("Inserisci secondo operando : \t");
scanf("%f", &n2);
printf("\n");
printf("Il risultato � : \t%f", somma(n1, n2));

} else if (scelta==3) {
printf("Inserisci primo operando : \t");
scanf("%f", &n1);
printf("Inserisci secondo operando : \t");
scanf("%f", &n2);
printf("\n");
printf("Il risultato � : \t%f", moltiplicazione(n1, n2));

} else if (scelta==4) {
printf("Inserisci primo operando : \t");
scanf("%f", &n1);
printf("Inserisci secondo operando : \t");
scanf("%f", &n2);
printf("\n");
printf("Il risultato � : \t%f", divisione(n1, n2));

} else if (scelta==5) {
printf("Inserisci primo operando : \t");
scanf("%f", &n1);
printf("Inserisci secondo operando : \t");
scanf("%f", &n2);
printf("\n");
printf("Il risultato � : \t%f", sottrazione(n1, n2));

} else{
condizione1 = 0;

}printf("\n");
printf("\n");

}
}
float  somma (float n1, float n2){
	float  risultato ;
risultato = n1+n2;
	return risultato;
}
float  moltiplicazione (float n1, float n2){
	float  risultato ;
risultato = n1*n2;
	return risultato;
}
float  divisione (float n1, float n2){
	float  risultato ;
risultato = n1/n2;
	return risultato;
}
float  sottrazione (float n1, float n2){
	float  risultato ;
risultato = n1-n2;
	return risultato;
}
