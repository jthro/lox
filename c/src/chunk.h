#ifndef chunk_h
#define chunk_h

#include "common.h"
#include "value.h"
#include "line.h"

typedef enum {
    OP_CONSTANT,
    OP_CONSTANT_LONG,
    OP_RETURN,
} OpCode;

/**
 * @param count n of instructions in the chunk
 * @param code pointer to the instructions
 * @param lines line number for each instruction
 * @param constants constant pool
 */
typedef struct {
    int count;
    int capacity;
    uint8_t* code;
    Lines lines;
    ValueArray constants;
} Chunk;

/**
 * Initialize an empty Chunk
 * @param chunk Chunk to initialize
 */
void initChunk(Chunk* chunk);

void freeChunk(Chunk* chunk);

/**
 * Append a byte to the end of a Chunk
 * @param chunk Chunk to write to
 * @param byte byte to write
 * @param line corresponding source code line number
 */
void writeChunk(Chunk* chunk, uint8_t byte, int line);

/**
 * Add a constant to the constant pool of a chunk
 * @param chunk to write to
 * @param value to add to the constant pool
 * @return the index of the constant in the pool
 */
int addConstant(Chunk* chunk, Value value);

/**
 * Append an OP_CONSTANT_LONG instruction to a chunk as well as
 * adding the constant to the constant pool
 */
void writeConstant(Chunk* chunk, Value value, int line);

#endif // chunk_h
