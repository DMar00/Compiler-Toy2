#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void sommac(int a, int d, float b, char *size_out, float *result_out);
char * stampa (char *messaggio);

	int  c = 1;

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
valore = strdup(stampa(c = strdup(c = strdup(c = strdup("la somma di "+a);
c = strcat(c, " e ");
+b);
c = strcat(c, " incrementata di ");
+c);
c = strcat(c, " è ");aglia));
free(valore);
valore = strdup(stampa("ed è pari a "+risultato));
printf("vuoi continuare? (si/no) - inserisci due volte la risposta");
scanf("%s", ans);
scanf("%s", ans1);
while (ans="si") {
printf("inserisci un intero:");
scanf("%d", &a);
printf("inserisci un reale:");
scanf("%f", &b);
float  t0 = risultato;
	sommac(a, x, b, taglia, &t0);
risultato = t0;
free(valore);
valore = strdup(stampa(c = strdup(c = strdup(c = strdup("la somma di "+a);
c = strcat(c, " e ");
+b);
c = strcat(c, " incrementata di ");
+c);
c = strcat(c, " è ");aglia));
free(valore);
valore = strdup(stampa(" ed è pari a "+risultato));
printf("vuoi continuare? (si/no):\t");
scanf("%s", ans);

}printf("\n");
printf("ciao");

}
char * stampa (char *messaggio){
	int  i = 0;
while (i<4) {
printf("\n");

}printf("%s", messaggio);
	return "ok";
}
