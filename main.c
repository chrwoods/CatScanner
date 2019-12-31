#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>

int main(int argc, char *argv[]) {
  FILE *fp;

  /*fp = fopen("/", "r");
  if (fp == NULL) {
    printf("Error!\n");
    exit(1);
  }

  char* line = malloc(8000);
  while (!feof(fp)) {
    if (fgets(line, 8000, fp) == NULL) break;
    printf("%s\n", line);
    }*/

  DIR *dp;
  dp = opendir("./");
  if (dp == NULL) {
    printf("Didn't find a directory.\n");
    exit(1);
  }

  struct dirent *de;
  while(1) {
    de = readdir(dp);
    if (de == NULL) {
      break;
    }

    printf("%s\n", de->d_name);
    printf("%d\n", de->d_type);
  }
  return 0;
}
