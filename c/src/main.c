#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "line.h"

int main(int argc, const char* argv[]) {
    Chunk chunk;
    initChunk(&chunk);

    for (int i = 0; i < 256; i++) {
        int constant = addConstant(&chunk, 1.2);
        writeChunk(&chunk, OP_CONSTANT, 123);
        writeChunk(&chunk, constant, 123);
    }

    writeChunk(&chunk, OP_RETURN, 125);

    writeConstant(&chunk, 1234, 125);

    disassembleChunk(&chunk, "test chunk");

    freeChunk(&chunk);


    return 0;
}
