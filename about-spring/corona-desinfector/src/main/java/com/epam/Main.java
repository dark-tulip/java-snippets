package com.epam;

import java.util.HashMap;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    ApplicationContext context = Application.run(
      "com.epam",
      new HashMap<>(Map.of(Policeman.class, PolicemanImpl.class))
    );
    CoronaDesinfector desinfector = context.getObject(CoronaDesinfector.class);
    desinfector.start(new Room());

  }
}
