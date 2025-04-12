#include <stdio.h>
#include <stdlib.h>

#include "line.h"
#include "memory.h"

void initLines(Lines* lines) {
    lines->capacity = 0;
    lines->lines = NULL;
}

void freeLines(Lines* lines) {
    FREE_ARRAY(int, lines->lines, lines->capacity);
    initLines(lines);
}

void incrementLines(Lines* lines, int line) {
    if (lines->capacity < line) {
        int oldCapacity = lines->capacity;
        lines->capacity = line;
        lines->lines = GROW_ARRAY(int, lines->lines, oldCapacity, lines->capacity);
    }

    lines->lines[line-1]++;
}

int getLine(Lines *lines, int instructionIndex) {
    for (int lineIndex = 0; lineIndex < lines->capacity; lineIndex++) {
        if (instructionIndex - lines->lines[lineIndex] < 0) {
            return lineIndex+1;
        }

        instructionIndex -= lines->lines[lineIndex];
    }

    exit(1);
}

void showLines(Lines* lines) {
    for (int i = 0; i < lines->capacity; i++) {
        if (lines->lines[i] != 0) {
            printf("%d: %d\n", i+1, lines->lines[i]);
        }
    }
}
