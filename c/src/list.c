#include "list.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

List ListNew(const char *string) {
    List l = malloc(sizeof(struct list));
    l->next = NULL;
    l->prev = NULL;
    l->string = strdup(string);
    return l;
}

void ListFree(List l) {
    while (l != NULL) {
        List tmp = l->next;
        free(l->string);
        free(l);
        l = tmp;
    }
}

List ListInsert(List l, const char *string, int index) {
    List new = ListNew(string);

    if (index == 0) {
        new->next = l;
        l->prev = new;
        return new;
    }

    List prev = l;
    while (index > 1) {
        if (prev->next == NULL) {
            prev->next = new;
            new->prev = prev;
            return l;
        }
        prev = prev->next;
        index--;
    }
    new->prev = prev;
    new->next = prev->next;
    prev->next = new;
    new->next->prev = new;
    return l;
}

char *ListFind(List l, int index) {
    while (index > 0) {
        if (l == NULL) {
            return NULL;
        }
        l = l->next;
        index--;
    }

    if (l == NULL) {
        return NULL;
    }

    return l->string;
}

List ListDelete(List l, int index) {
    if (index == 0) {
        List new_head = l->next;
        new_head->prev = NULL;
        free(l->string);
        free(l);
        return new_head;
    }

    List prev = l;
    while (index > 1) {
        if (prev->next == NULL) {
            return l;
        }
        prev = prev->next;
        index--;
    }

    List to_delete = prev->next;
    prev->next = to_delete->next;
    to_delete->next->prev = prev;

    free(to_delete->string);
    free(to_delete);

    return l;
}

void ListPrintForward(List l) {
    printf("H -> ");
    while (l != NULL) {
        printf("%s -> ", l->string);
        l = l->next;
    }
    printf("X\n");
}

void ListPrintBackward(List l) {
    printf("X -> ");
    if (l != NULL) {
        while (l->next != NULL) {
            l = l->next;
        }

        while (l != NULL) {
            printf("%s -> ", l->string);
            l = l->prev;
        }
    }
    printf("H\n");
}
