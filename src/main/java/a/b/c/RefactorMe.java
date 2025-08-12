package a.b.c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Demonstration class used by tests to exercise JavaParser lexical preservation.
 * Fields intentionally use types named NonInclusive* so tests can rename them to Inclusive*.
 * The goal is to verify that variable declaration types and generics retain their original
 * formatting and spacing when refactored via LexicalPreservingPrinter.
 */
public class RefactorMe extends NonInclusiveAbstract implements NonInclusiveInterface {

  /*
   * Fields deliberately cover a variety of declaration contexts so refactoring touches:
   * - a simple reference type
   * - a generic collection element type (List<...>)
   * - a generic map value type with explicit type arguments and diamond operator usage
   */
  NonInclusiveClz _nonInclusivefield;
  List<NonInclusiveClz> _nonInclusiveClzList;
  Map<String, NonInclusiveClz> _nonInclusiveMap;

  /**
   * Initializes fields so there are concrete usages in initializers and collections,
   * which helps verify refactoring behavior in both declarations and expressions.
   */
  public RefactorMe() {
    _nonInclusivefield = new NonInclusiveClz();
    _nonInclusiveClzList = new ArrayList<>();
    _nonInclusiveClzList.add(new NonInclusiveClz());
    _nonInclusiveMap = new HashMap<String, NonInclusiveClz>();
  }

}
