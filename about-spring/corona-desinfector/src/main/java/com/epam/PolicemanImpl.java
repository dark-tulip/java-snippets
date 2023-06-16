package com.epam;

import javax.annotation.PostConstruct;

@Singleton
public class PolicemanImpl implements Policeman {
  @InjectByType
  Recommendator recommendator;

  @PostConstruct
  public void init() {
    System.out.println(recommendator.getClass());
  }
  @Override
  public void makePeopleLeaveRoom() {

    System.out.println(recommendator.getClass());
    System.out.println("Пиф паф ба-бах");
  }
}
