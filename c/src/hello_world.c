#include "list.h"

int main(void) {
    List l = ListNew("sus");

    l = ListInsert(l, "amogus", 2);
    l = ListInsert(l, "a", 0);

    ListPrintForward(l);
    ListPrintBackward(l);

    l = ListDelete(l, 0);

    ListPrintForward(l);
    ListPrintBackward(l);

    ListFree(l);

    return 0;
}
