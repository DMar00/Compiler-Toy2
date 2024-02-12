#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void sommac(int a, int d, float b, char *size_out, float *result_out);
char * stampa (char *messaggio);

char *buffer;

	int  c = 1;

char* intToString(int num) {
	buffer = (char*)malloc(30 * sizeof(char));
	snprintf(buffer, sizeof(buffer), "%d", num);
	return buffer;
}

char* myStrcat(char *s1, const char *s2){
	char *s = strdup("");
	strcat(s, s1);
	strcat(s, s2);
	return s;
}

char* floatToString(float num) {
	buffer = (char*)malloc(30 * sizeof(char));
	snprintf(buffer, sizeof(buffer), "%f", num);
	return buffer;
}
void sommac(int a, int d, float b, char *size_out, float *result_out){
*result_out = a+b+c+d;
if (*result_out>100) {
	char * valore = strdup("grande");
free(size_out);
size_out = strdup(valore);

} else if (*result_out>50) {
	char * valore = strdup("media");
free(size_out);
size_out = strdup(valore);

} else{
	char * valore = strdup("piccola");
free(size_out);
size_out = strdup(valore);

}
}
int main(){
	int  b = 2.2;
	int  a = 1;
	int  x = 3;
	char * taglia  = strdup("");
	char * ans1  = strdup("");
	char * ans = strdup("no");
	int  risultato = 0;
	char * valore = strdup("nok");
float  t0 = risultato;
	sommac(a, x, b, taglia, &t0);
risultato = t0;
free(valore);
valore = strdup(stampa(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat("la somma di ", intToString(a)), " e "), floatToString(b)), " incrementata di "), intToString(c)), " è "), taglia)));
free(valore);
valore = strdup(stampa(myStrcat("ed è pari a ", intToString(risultato))));
printf("\n");
printf("ciao");
free(buffer);

}
char * stampa (char *messaggio){
	int  i = 0;
while (i<4) {
printf("\n");
i = i+1;

}printf("%s", messaggio);
	return "ok";
}
