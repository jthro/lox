#ifndef clox_line_h
#define clox_line_h

#include "common.h"
#include "memory.h"

/**
 * Frequency table of how many times each line occurs
 */
typedef struct {
    size_t capacity;
    int* lines;
} Lines;

void initLines(Lines* lines);

void freeLines(Lines* lines);

void incrementLines(Lines* lines, int line);

int getLine(Lines* lines, int instructionIndex);

void showLines(Lines* lines);

#endif // clox_line_h
