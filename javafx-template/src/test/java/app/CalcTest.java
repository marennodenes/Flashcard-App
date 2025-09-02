package app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test suite for Calc.
 */
@SuppressWarnings({"java:S5778"})
class CalcTest {

  private static void checkCalc(Calc calc, double... operands) {
    Assertions.assertEquals(operands.length, calc.getOperandCount(), "Wrong operand count");
    for (int i = 0; i < operands.length; i++) {
      Assertions.assertEquals(operands[i], calc.peekOperand(i), 
                                "Wrong value at #" + i + " of operand stack");
    }
  }

  @Test
  void testCalc() {
    checkCalc(new Calc());
    checkCalc(new Calc(1.0), 1.0);
    checkCalc(new Calc(3.14, 1.0), 1.0, 3.14);
  }

  @Test
  void testPushOperand() {
    Calc calc = new Calc();
    calc.pushOperand(1.0);
    checkCalc(calc, 1.0);
    calc.pushOperand(3.14);
    checkCalc(calc, 3.14, 1.0);
  }

  @Test
  void testPeekOperand() {
    Calc calc = new Calc(1.0, 3.14);
    Assertions.assertEquals(3.14, calc.peekOperand());
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Calc().peekOperand());
  }

  @Test
  void testPeekOperandN() {
    Calc calc = new Calc(1.0, 3.14);
    Assertions.assertEquals(3.14, calc.peekOperand(0));
    Assertions.assertEquals(1.0, calc.peekOperand(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> calc.peekOperand(2));
  }

  @Test
  void testPopOperand() {
    Calc calc = new Calc(1.0, 3.14);
    Assertions.assertEquals(3.14, calc.popOperand());
    checkCalc(calc, 1.0);
    Assertions.assertEquals(1.0, calc.popOperand());
    checkCalc(calc);
  }

  @Test
  void testPopOperand_emptyStack() {
    Assertions.assertThrows(IllegalStateException.class, () -> new Calc().popOperand());
  }

  @Test
  void testPerformOperation1() {
    Calc calc = new Calc(1.0);
    Assertions.assertEquals(-1.0, calc.performOperation(n -> -n));
    checkCalc(calc, -1.0);
  }

  @Test
  void testPerformOperation1_emptyOperandStack() {
    Assertions.assertThrows(IllegalStateException.class, 
                              () -> new Calc().performOperation(n -> -n));
  }

  @Test
  void testPerformOperation2() {
    Calc calc = new Calc(1.0, 3.0);
    Assertions.assertEquals(-2.0, calc.performOperation((n1, n2) -> n1 - n2));
    checkCalc(calc, -2.0);
  }

  @Test
  void testPerformOperation2_lessThanTwoOperands() {
    Assertions.assertThrows(IllegalStateException.class, 
                                () -> new Calc(1.0).performOperation((n1, n2) -> n1 - n2));
    Assertions.assertThrows(IllegalStateException.class, 
                                () -> new Calc().performOperation((n1, n2) -> n1 - n2));
  }

  @Test
  void testSwap() {
    Calc calc = new Calc(1.0, 3.14);
    checkCalc(calc, 3.14, 1.0);
    calc.swap();
    checkCalc(calc, 1.0, 3.14);
    calc.swap();
    checkCalc(calc, 3.14, 1.0);
  }

  @Test
  void testSwap_lessThanTwoOperands() {
    Assertions.assertThrows(IllegalStateException.class, () -> new Calc(1.0).swap());
    Assertions.assertThrows(IllegalStateException.class, () -> new Calc().swap());
  }

  @Test
  void testDup() {
    Calc calc = new Calc(1.0, 3.14);
    Assertions.assertEquals(3.14, calc.popOperand());
    checkCalc(calc, 1.0);
    Assertions.assertEquals(1.0, calc.popOperand());
    checkCalc(calc);
  }

  @Test
  void testDup_emptyOperandStack() {
    Assertions.assertThrows(IllegalStateException.class, () -> new Calc().dup());
  }
}
