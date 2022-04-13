package a.b.c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RefactorMe extends InclusiveAbstract implements InclusiveInterface {

  NonInclusiveClz _nonInclusivefield;
  List<NonInclusiveClz> _nonInclusiveClzList;
  Map<String, NonInclusiveClz> _nonInclusiveMap;

  public RefactorMe() {
    _nonInclusivefield = new InclusiveClz();
    _nonInclusiveClzList = new ArrayList<>();
    _nonInclusiveClzList.add(new InclusiveClz());
    _nonInclusiveMap = new HashMap<String, InclusiveClz>();
  }

}
