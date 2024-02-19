#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void sommac(int a, int d, float b, char *size_out, float *result_out);
char * stampa (char *messaggio);

	int  c = 1;

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
void sommac(int a, int d, float b, char *size_out, float *result_out){
*result_out = a+b+c+d;
if (*result_out>100) {
	char * valore = (char *)malloc(256 * sizeof(char));
strcpy(valore, "grande");
strcpy(size_out, valore);

} else if (*result_out>50) {
	char * valore = (char *)malloc(256 * sizeof(char));
strcpy(valore, "media");
strcpy(size_out, valore);

} else{
	char * valore = (char *)malloc(256 * sizeof(char));
strcpy(valore, "piccola");
strcpy(size_out, valore);

}
}
int main(){
	int  a = 1;
	float  b = 2.2;
	int  x = 3;
	char * ans1  = (char *)malloc(256 * sizeof(char));
	char * taglia  = (char *)malloc(256 * sizeof(char));
	char * ans = (char *)malloc(256 * sizeof(char));
strcpy(ans, "no");
	float  risultato = 0.0;
	char * valore = (char *)malloc(256 * sizeof(char));
strcpy(valore, "nok");
	sommac(a, x, b, taglia, &risultato);
strcpy(valore, stampa(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat("la somma di ", intToString(a)), " e "), floatToString(b)), " incrementata di "), intToString(c)), " e' "), taglia)));
strcpy(valore, stampa(myStrcat("ed e' pari a ", floatToString(risultato))));
printf("vuoi continuare? (si/no) - inserisci due volte la risposta\n");
scanf("%s", ans);
scanf("%s", ans1);
while (strcmp(ans, "si") == 0) {
printf("inserisci un intero:");
scanf("%d", &a);
printf("inserisci un reale:");
scanf("%f", &b);
	sommac(a, x, b, taglia, &risultato);
strcpy(valore, stampa(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat(myStrcat("la somma di ", intToString(a)), " e "), floatToString(b)), " incrementata di "), intToString(c)), " e' "), taglia)));
strcpy(valore, stampa(myStrcat(" ed e' pari a ", floatToString(risultato))));
printf("vuoi continuare? (si/no):\t");
scanf("%s", ans);

}printf("\n");
printf("ciao");

}
char * stampa (char *messaggio){
	int  i = 0;
while (i<4) {
printf("\n");
i = i+1;

}printf("%s", messaggio);
	return "ok";
}
