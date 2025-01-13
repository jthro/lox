#ifndef LIST
#define LIST

#include <stdbool.h>

struct list {
    char *string;
    struct list *prev;
    struct list *next;
};

typedef struct list * List;

List ListNew(const char *string);

void ListFree(List l);

List ListInsert(List l, const char *string, int index);

char *ListFind(List l, int index);

List ListDelete(List l, int index);

void ListPrintForward(List l);

void ListPrintBackward(List l);

#endif // LIST
