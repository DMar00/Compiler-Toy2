#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void scoping(int n, int m);
int  glob ();

	int  message = 100;

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
void scoping(int n, int m){
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 1");
if (n<=1) {
	float  message = 2.1;
if (m<=1) {
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 3.1");
printf("%s\n", message);

} else if (m>1&&m<5) {
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 3.2");
printf("%s\n", message);

} else{
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 3.3");
printf("%s\n", message);

}printf("%f\n", message);

} else{
	float  message = 2.2;
if (m<=1) {
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 3.4");
printf("%s\n", message);

} else if (m>1&&m<5) {
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 3.5");
printf("%s\n", message);

} else{
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 3.6");
printf("%s\n", message);

}printf("%f\n", message);

}printf("%s\n", message);

}
int main(){
	char * message = (char *)malloc(256 * sizeof(char));
strcpy(message, "level 0");
	int  n ;
	int  m ;
	int  k = 6;
while (k>=1) {
printf("Inserisci n ");
scanf("%d", &n);
printf("Inserisci m ");
scanf("%d", &m);
printf("I valori inseriti sono %de %d\n", n, m);
	scoping(n, m);
k = k-1;

}printf("%s\n", message);
printf("%d\n", glob());

}
int  glob (){
	return message;
}
