#ifndef memory_h
#define memory_h

#include "common.h"

/**
 * Calculate the new capacity for a VLA
 * @param capacity old capacity
 */
#define GROW_CAPACITY(capacity) \
    ((capacity) < 8 ? 8 : (capacity) * 1.5)

/**
 * Grow a VLA to a new capacity
 * @param type type of the VLA
 * @param pointer pointer to the VLA
 * @param oldCount old capacity
 * @param newCount new capacity
 */
#define GROW_ARRAY(type, pointer, oldCount, newCount) \
    (type*)reallocate(pointer, sizeof(type) * (oldCount), \
        sizeof(type) * newCount)

/**
 * Free a VLA
 * @param type type of the VLA
 * @param pointer pointer to the VLA
 * @param oldCount old capacity
 */
#define FREE_ARRAY(type, pointer, oldCount) \
    reallocate(pointer, sizeof(type) * (oldCount), 0)

/**
 * Reallocate a memory chunk
 * @param pointer pointer to the memory
 * @param oldSize old size of the memory chunk
 * @param newSize new size of the memory chunk
 */
void* reallocate(void* pointer, size_t oldSize, size_t newSize);

#endif // memory_h
