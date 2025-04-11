#ifndef clox_debug_h
#define clox_debug_h

#include "chunk.h"

/**
 * Disassemble every instruction in a chunk
 * @param chunk chunk to disassemble
 * @param name name of the chunk
 */
void disassembleChunk(Chunk* chunk, const char* name);

/**
 * Disassemble an instruction
 * @param chunk pointer to the chunk the instruction belongs to
 * @param offset offset of the instruction in the chunk
 */
int disassembleInstruction(Chunk* chunk, int offset);

#endif // clox_debug_h
