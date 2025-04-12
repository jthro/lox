#include <stdio.h>
#include <stdlib.h>
#include "chunk.h"
#include "memory.h"
#include "value.h"
#include "line.h"

void initChunk(Chunk* chunk) {
    chunk->count = 0;
    chunk->capacity = 0;
    chunk->code = NULL;
    initLines(&chunk->lines);
    initValueArray(&chunk->constants);
}

void freeChunk(Chunk *chunk) {
    FREE_ARRAY(uint8_t, chunk->code, chunk->capacity);
    freeLines(&chunk->lines);
    freeValueArray(&chunk->constants);
    initChunk(chunk);
}

void writeChunk(Chunk* chunk, uint8_t byte, int line) {
    printf("constant operand: %d\n", byte);
    if (chunk->capacity < chunk->count + 1) {
        int oldCapacity = chunk->capacity;
        chunk->capacity = GROW_CAPACITY(oldCapacity);
        chunk->code = GROW_ARRAY(uint8_t, chunk->code, oldCapacity, chunk->capacity);
    }

    chunk->code[chunk->count] = byte;
    incrementLines(&chunk->lines, line);
    chunk->count++;
}

int addConstant(Chunk *chunk, Value value) {
    writeValueArray(&chunk->constants, value);
    return chunk->constants.count - 1;
}

void writeConstant(Chunk* chunk, Value value, int line) {

    writeChunk(chunk, OP_CONSTANT_LONG, line);
    int offset = addConstant(chunk, value);

    for (int i = 2; i >= 0; i--) {
        uint8_t byte = (offset & (0xff << i*8)) >> (i*8);
        printf("byte: %d\n", byte);
        writeChunk(chunk, byte, line);
    }
}
