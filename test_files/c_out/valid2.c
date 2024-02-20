#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void somma(float input1, float input2, float *result_out);
void sottrazione(float input1, float input2, float *result_out);
float  moltiplicazione (float input1, float input2);
float  divisione (float input1, float input2);
void tutte_le_operazioni (float input1, float input2, float *p0, float *p1, float *p2, float *p3);
char * stampa (char *stringa);


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
	char * operazione  = (char *)malloc(256 * sizeof(char));
	float  input1 ;
	float  answer ;
	float  input2 ;
	int  flag = 1;
	float  result ;
	float  res1 ;
	float  res3 ;
	float  res4 ;
	float  res2 ;
while (flag==1) {
printf("Inserisci l'operazione da effettuare (somma, sottrazione, divisione, moltiplicazione, tutte_le_operazioni): ");
scanf("%s", operazione);
printf("Inserisci il primo input: ");
scanf("%f", &input1);
printf("Inserisci il secondo input: ");
scanf("%f", &input2);
printf("hai scelto l'operazione%scon gli argomenti%f e %f\n", operazione, input1, input2);
if (strcmp(operazione, "somma") == 0) {
	somma(input1, input2, &result);

} else if (strcmp(operazione, "sottrazione") == 0) {
	sottrazione(input1, input2, &result);

} else if (strcmp(operazione, "divisione") == 0) {
result = divisione(input1, input2);

} else if (strcmp(operazione, "moltiplicazione") == 0) {
result = moltiplicazione(input1, input2);

} else if (strcmp(operazione, "tutte_le_operazioni") == 0) {
	float  p0;
	float  p1;
	float  p2;
	float  p3;
	tutte_le_operazioni(input1, input2, &p0, &p1, &p2, &p3);
	res1 =  p0;
	res2 =  p1;
	res3 =  p2;
	res4 =  p3;

} else{
printf("Operazione non consentita\n");

}if (strcmp(operazione, "tutte_le_operazioni") != 0) {
printf("Il risultato e': %f\n", result);

} else{
printf("%s\n", stampa(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat("i risultati delle 4 operazioni sono \n", floatToString(res1)), "\n"), floatToString(res2)), "\n"), floatToString(res3)), "\n"), floatToString(res4)), "\n")));

}printf("Vuoi continuare? (1 yes/0 no): ");
scanf("%f", &answer);
if (answer==1) {
flag = 1;

} else{
flag = 0;

}
}
}
void somma(float input1, float input2, float *result_out){
*result_out = input1+input2;

}
void sottrazione(float input1, float input2, float *result_out){
*result_out = input1-input2;

}
float  moltiplicazione (float input1, float input2){
	float  result ;
result = input1*input2;
	return result;
}
float  divisione (float input1, float input2){
	float  result ;
if (input2==0) {
printf("Errore");
result = 0.0;

}result = input1/input2;
	return result;
}
void tutte_le_operazioni (float input1, float input2, float *p0, float *p1, float *p2, float *p3){
	float  sottrazione_res = 0.0;
	float  somma_res = 0.0;
	somma(input1, input2, &somma_res);
	sottrazione(input1, input2, &sottrazione_res);
	*p0 = somma_res;
	*p1 = sottrazione_res;
	*p2 = divisione(input1, input2);
	*p3 = moltiplicazione(input1, input2);

}
char * stampa (char *stringa){
	char * s = (char *)malloc(256 * sizeof(char));
strcpy(s, "Ciao! ");
	return myStrcat(s, stringa);
}
