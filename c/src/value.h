#ifndef clox_value_h
#define clox_value_h

#include "common.h"

typedef double Value;

typedef struct {
    int capacity;
    int count;
    Value* values;
} ValueArray;

/**
 * Initialize a value array to capacity 0
 * @param array to initialize
 */
void initValueArray(ValueArray* array);

/**
 * Append a value to a value array
 * @param array value array to append to
 * @param value to append
 */
void writeValueArray(ValueArray* array, Value value);

void freeValueArray(ValueArray* array);

void printValue(Value value);

#endif // clox_value_h
